package com.team_nebula.nebula.domain.recommendation.service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team_nebula.nebula.domain.recommendation.dto.request.RecommendationFeedbackRequestDTO;
import com.team_nebula.nebula.domain.recommendation.dto.request.SearchBasedRecommendationRequestDTO;
import com.team_nebula.nebula.domain.recommendation.dto.response.ClusterTrendDTO;
import com.team_nebula.nebula.domain.recommendation.dto.response.ClusterTrendsResponseDTO;
import com.team_nebula.nebula.domain.recommendation.dto.response.GeneralRecommendationResponseDTO;
import com.team_nebula.nebula.domain.recommendation.dto.response.RecommendationDTO;
import com.team_nebula.nebula.domain.recommendation.dto.response.RecommendationFeedbackResponseDTO;
import com.team_nebula.nebula.domain.recommendation.dto.response.SearchBasedRecommendationDTO;
import com.team_nebula.nebula.domain.recommendation.dto.response.SearchBasedRecommendationResponseDTO;
import com.team_nebula.nebula.domain.recommendation.dto.response.TrendingKeywordDTO;
import com.team_nebula.nebula.global.apipayload.code.status.ErrorStatus;
import com.team_nebula.nebula.global.apipayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

	private final WebClient webClient;
	private final ObjectMapper objectMapper;

	@Value("${ai.url.recommendation}")
	private String aiRecommendationUrl;

	@Override
	public GeneralRecommendationResponseDTO generalRecommendation(Long userId, int limit, String category,
		boolean excludeViewed, boolean diversify, String timeRange) {
		String url = UriComponentsBuilder.fromHttpUrl(aiRecommendationUrl + "recommendations/general")
			.queryParam("user_id", userId)
			.queryParam("limit", limit)
			.queryParam("exclude_viewed", excludeViewed)
			.queryParam("diversify", diversify)
			.queryParam("time_range", timeRange)
			.queryParamIfPresent("category", Optional.ofNullable(category))
			.toUriString();

		String response = callAiServer(url);
		return parseResponse(response, GeneralRecommendationResponseDTO.class);
	}

	@Override
	public SearchBasedRecommendationResponseDTO searchBasedRecommendation(Long userId,
		SearchBasedRecommendationRequestDTO request) {
		Map<String, Object> params = Map.of(
			"user_id", userId,
			"query", request.getQuery(),
			"limit", request.getLimit(),
			"include_similar_queries", request.getIncludeSimilarQueries(),
			"boost_user_preferences", request.getBoostUserPreferences(),
			"session_id", Objects.toString(request.getSessionId(), ""),
			"previous_queries", Optional.ofNullable(request.getPreviousQueries()).orElse(Collections.emptyList())
		);

		String response = callAiServer("recommendations/search-based", params);
		return parseResponse(response, SearchBasedRecommendationResponseDTO.class);
	}

	@Override
	public ClusterTrendsResponseDTO clusterTrends(Integer clusterId, String timePeriod, boolean includeGlobal) {
		String url = UriComponentsBuilder.fromHttpUrl(aiRecommendationUrl + "recommendations/cluster-trends")
			.queryParam("time_period", timePeriod)
			.queryParam("include_global", includeGlobal)
			.queryParamIfPresent("cluster_id", Optional.ofNullable(clusterId))
			.toUriString();

		String response = callAiServer(url);
		return parseResponse(response, ClusterTrendsResponseDTO.class);
	}

	@Override
	public RecommendationFeedbackResponseDTO feedback(Long userId, RecommendationFeedbackRequestDTO request) {
		Map<String, Object> params = new HashMap<>();
		params.put("user_id", userId);
		params.put("recommendation_id", request.getRecommendationId());
		params.put("bookmark_id", request.getBookmarkId());
		params.put("action_type", request.getActionType());
		params.put("shown_at", formatDateTime(request.getShownAt()));
		params.put("action_at", formatDateTime(request.getActionAt()));
		params.put("session_id", Objects.toString(request.getSessionId(), ""));
		params.put("page_context", Objects.toString(request.getPageContext(), ""));
		params.put("recommendation_position", Objects.toString(request.getRecommendationPosition(), "0"));
		params.put("explicit_rating", Optional.ofNullable(request.getExplicitRating()).filter(r -> r > 0).orElse(1));
		params.put("engagement_time", Objects.toString(request.getEngagementTime(), "0"));

		String response = callAiServer("recommendations/feedback", params);
		return parseResponse(response, RecommendationFeedbackResponseDTO.class);
	}

	private String callAiServer(String endpoint, Map<String, Object> params) {
		return webClient.post()
			.uri(aiRecommendationUrl + endpoint)
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(params)
			.retrieve()
			.bodyToMono(String.class)
			.block();
	}

	private String callAiServer(String fullUrl) {
		return webClient.get()
			.uri(fullUrl)
			.retrieve()
			.bodyToMono(String.class)
			.block();
	}

	private <T> T parseResponse(String response, Class<T> responseType) {
		try {
			if (response == null) {
				throw new GeneralException(ErrorStatus._AI_SERVER_ERROR);
			}

			JsonNode root = objectMapper.readTree(response);

			if (responseType == GeneralRecommendationResponseDTO.class) {
				return (T)parseGeneralRecommendation(root);
			} else if (responseType == SearchBasedRecommendationResponseDTO.class) {
				return (T)parseSearchBasedRecommendation(root);
			} else if (responseType == ClusterTrendsResponseDTO.class) {
				return (T)parseClusterTrends(root);
			} else if (responseType == RecommendationFeedbackResponseDTO.class) {
				return (T)parseFeedback(root);
			}

			throw new GeneralException(ErrorStatus._AI_SERVER_ERROR);
		} catch (Exception e) {
			log.error("AI 서버 응답 파싱 실패: {}", e.getMessage());
			throw new GeneralException(ErrorStatus._AI_SERVER_ERROR);
		}
	}

	private GeneralRecommendationResponseDTO parseGeneralRecommendation(JsonNode root) {
		List<RecommendationDTO> recommendations = new ArrayList<>();

		if (root.has("recommendations") && root.get("recommendations").isArray()) {
			for (JsonNode item : root.get("recommendations")) {
				recommendations.add(RecommendationDTO.builder()
					.bookmarkId(getText(item, "bookmark_id"))
					.title(getText(item, "title"))
					.url(getText(item, "url"))
					.score(getDouble(item, "score"))
					.reasonType(getText(item, "reason_type"))
					.reasonDetails(parseReasonDetails(item.get("reason_details")))
					.domain(getText(item, "domain"))
					.keywords(parseStringList(item, "keywords"))
					.category(getText(item, "category"))
					.publishedAt(getText(item, "published_at"))
					.summary(getText(item, "summary"))
					.build());
			}
		}

		return GeneralRecommendationResponseDTO.builder()
			.recommendations(recommendations)
			.userClusterId(getLong(root, "user_cluster_id"))
			.clusterDescription(getText(root, "cluster_description"))
			.totalRecommendations(getInt(root, "total_recommendations"))
			.generatedAt(getText(root, "generated_at"))
			.algorithmVersion(getText(root, "algorithm_version"))
			.personalizationScore(getDouble(root, "personalization_score"))
			.diversityScore(getDouble(root, "diversity_score"))
			.build();
	}

	private SearchBasedRecommendationResponseDTO parseSearchBasedRecommendation(JsonNode root) {
		List<SearchBasedRecommendationDTO> recommendations = new ArrayList<>();

		if (root.has("recommendations") && root.get("recommendations").isArray()) {
			for (JsonNode item : root.get("recommendations")) {
				recommendations.add(SearchBasedRecommendationDTO.builder()
					.bookmarkId(getText(item, "bookmark_id"))
					.title(getText(item, "title"))
					.url(getText(item, "url"))
					.score(getDouble(item, "score"))
					.reasonType(getText(item, "reason_type"))
					.reasonDetails(parseSearchReasonDetails(item.get("reason_details")))
					.domain(getText(item, "domain"))
					.keywords(parseStringList(item, "keywords"))
					.category(getText(item, "category"))
					.publishedAt(getText(item, "published_at"))
					.summary(getText(item, "summary"))
					.build());
			}
		}

		return SearchBasedRecommendationResponseDTO.builder()
			.recommendations(recommendations)
			.queryKeywords(parseStringList(root, "query_keywords"))
			.expandedConcepts(parseStringList(root, "expanded_concepts"))
			.searchIntent(getText(root, "search_intent"))
			.totalRecommendations(getInt(root, "total_recommendations"))
			.processingTimeMs(getLong(root, "processing_time_ms"))
			.similarityThresholdUsed(getDouble(root, "similarity_threshold_used"))
			.build();
	}

	private ClusterTrendsResponseDTO parseClusterTrends(JsonNode root) {
		List<ClusterTrendDTO> trends = new ArrayList<>();

		if (root.has("cluster_trends") && root.get("cluster_trends").isArray()) {
			for (JsonNode item : root.get("cluster_trends")) {
				trends.add(ClusterTrendDTO.builder()
					.clusterId(getInt(item, "cluster_id"))
					.clusterName(getText(item, "cluster_name"))
					.memberCount(getInt(item, "member_count"))
					.trendingKeywords(parseTrendingKeywords(item.get("trending_keywords")))
					.popularDomains(parseStringList(item, "popular_domains"))
					.activityPeakHours(parseStringList(item, "activity_peak_hours"))
					.primaryInterests(parseStringList(item, "primary_interests"))
					.emergingTopics(parseStringList(item, "emerging_topics"))
					.build());
			}
		}

		return ClusterTrendsResponseDTO.builder()
			.clusterTrends(trends)
			.globalTrends(root.get("global_trends"))
			.generatedAt(getText(root, "generated_at"))
			.analysisPeriod(getText(root, "analysis_period"))
			.build();
	}

	private RecommendationFeedbackResponseDTO parseFeedback(JsonNode root) {
		return RecommendationFeedbackResponseDTO.builder()
			.feedbackId(getText(root, "feedback_id"))
			.processed(getBoolean(root, "processed"))
			.impactScore(getDouble(root, "impact_score"))
			.modelUpdated(getBoolean(root, "model_updated"))
			.message(getText(root, "message"))
			.build();
	}

	private RecommendationDTO.ReasonDetailsDTO parseReasonDetails(JsonNode node) {
		if (node == null || node.isNull())
			return null;

		return RecommendationDTO.ReasonDetailsDTO.builder()
			.algorithm(getText(node, "algorithm"))
			.similarityScore(getDouble(node, "similarity_score"))
			.clusterScore(getDouble(node, "cluster_score"))
			.popularityScore(getDouble(node, "popularity_score"))
			.build();
	}

	private SearchBasedRecommendationDTO.SearchBasedReasonDetailsDTO parseSearchReasonDetails(JsonNode node) {
		if (node == null || node.isNull())
			return null;

		List<SearchBasedRecommendationDTO.RecommendationFactorDTO> factors = new ArrayList<>();
		if (node.has("factors") && node.get("factors").isArray()) {
			for (JsonNode factor : node.get("factors")) {
				factors.add(SearchBasedRecommendationDTO.RecommendationFactorDTO.builder()
					.factor(getText(factor, "factor"))
					.description(getText(factor, "description"))
					.weight(getDouble(factor, "weight"))
					.build());
			}
		}

		return SearchBasedRecommendationDTO.SearchBasedReasonDetailsDTO.builder()
			.type(getText(node, "type"))
			.searchQuery(getText(node, "search_query"))
			.expandedKeywords(parseStringList(node, "expanded_keywords"))
			.factors(factors)
			.confidence(getDouble(node, "confidence"))
			.build();
	}

	private List<TrendingKeywordDTO> parseTrendingKeywords(JsonNode node) {
		List<TrendingKeywordDTO> keywords = new ArrayList<>();
		if (node != null && node.isArray()) {
			for (JsonNode keyword : node) {
				keywords.add(TrendingKeywordDTO.builder()
					.keyword(getText(keyword, "keyword"))
					.score(getDouble(keyword, "score"))
					.growth(getInt(keyword, "growth"))
					.frequency(getInt(keyword, "frequency"))
					.build());
			}
		}
		return keywords;
	}

	private String formatDateTime(java.time.LocalDateTime dateTime) {
		return dateTime != null ?
			dateTime.atZone(java.time.ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT) : null;
	}

	private List<String> parseStringList(JsonNode parent, String field) {
		List<String> result = new ArrayList<>();
		if (parent != null && parent.has(field) && parent.get(field).isArray()) {
			for (JsonNode item : parent.get(field)) {
				result.add(item.asText());
			}
		}
		return result;
	}

	private String getText(JsonNode node, String field) {
		return node != null && node.has(field) && !node.get(field).isNull() ? node.get(field).asText() : null;
	}

	private Integer getInt(JsonNode node, String field) {
		return node != null && node.has(field) && !node.get(field).isNull() ? node.get(field).asInt() : null;
	}

	private Long getLong(JsonNode node, String field) {
		return node != null && node.has(field) && !node.get(field).isNull() ? node.get(field).asLong() : null;
	}

	private Double getDouble(JsonNode node, String field) {
		return node != null && node.has(field) && !node.get(field).isNull() ? node.get(field).asDouble() : null;
	}

	private Boolean getBoolean(JsonNode node, String field) {
		return node != null && node.has(field) && !node.get(field).isNull() ? node.get(field).asBoolean() : null;
	}
}
