package com.team_nebula.nebula.domain.recommendation.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team_nebula.nebula.domain.recommendation.dto.response.GeneralRecommendationResponseDTO;
import com.team_nebula.nebula.domain.recommendation.dto.response.RecommendationDTO;
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
		try {
			String url = UriComponentsBuilder.fromHttpUrl(aiRecommendationUrl + "/general")
				.queryParam("user_id", userId)
				.queryParam("limit", limit)
				.queryParam("exclude_viewed", excludeViewed)
				.queryParam("diversify", diversify)
				.queryParam("time_range", timeRange)
				.queryParamIfPresent("category", Optional.ofNullable(category))
				.toUriString();

			log.info("AI 추천 서버 호출: {}", url);

			String response = webClient.get()
				.uri(url)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(String.class)
				.block();

			log.info("AI 서버 응답: {}", response);

			if (response != null) {
				return parseRecommendationResponse(response);
			}

		} catch (Exception e) {
			log.error("AI 추천 서버 호출 실패: {}", e.getMessage());
		}

		// AI 서버 호출 실패 시 기본 응답 생성
		List<RecommendationDTO> recommendations = new ArrayList<>();

		return GeneralRecommendationResponseDTO.builder()
			.recommendations(recommendations)
			.userClusterId(5L)
			.clusterDescription("프론트엔드 개발자 그룹")
			.totalRecommendations(0)
			.generatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")))
			.algorithmVersion("v1.0")
			.personalizationScore(null)
			.diversityScore(null)
			.build();
	}

	private GeneralRecommendationResponseDTO parseRecommendationResponse(String responseBody) {
		try {
			JsonNode root = objectMapper.readTree(responseBody);

			// 추천 목록 파싱
			List<RecommendationDTO> recommendations = new ArrayList<>();
			JsonNode recommendationsNode = root.path("recommendations");

			if (recommendationsNode.isArray()) {
				for (JsonNode recommendationNode : recommendationsNode) {
					RecommendationDTO.ReasonDetailsDTO reasonDetails = null;

					JsonNode reasonDetailsNode = recommendationNode.path("reason_details");
					if (!reasonDetailsNode.isMissingNode()) {
						reasonDetails = RecommendationDTO.ReasonDetailsDTO.builder()
							.algorithm(reasonDetailsNode.path("algorithm").asText(null))
							.similarityScore(reasonDetailsNode.path("similarity_score").isNull() ? null :
								reasonDetailsNode.path("similarity_score").asDouble())
							.clusterScore(reasonDetailsNode.path("cluster_score").isNull() ? null :
								reasonDetailsNode.path("cluster_score").asDouble())
							.popularityScore(reasonDetailsNode.path("popularity_score").isNull() ? null :
								reasonDetailsNode.path("popularity_score").asDouble())
							.build();
					}

					// keywords 파싱
					List<String> keywords = new ArrayList<>();
					JsonNode keywordsNode = recommendationNode.path("keywords");
					if (!keywordsNode.isMissingNode() && keywordsNode.isArray()) {
						for (JsonNode keywordNode : keywordsNode) {
							keywords.add(keywordNode.asText());
						}
					}

					recommendations.add(RecommendationDTO.builder()
						.bookmarkId(recommendationNode.path("bookmark_id").asText(null))
						.title(recommendationNode.path("title").asText(null))
						.url(recommendationNode.path("url").asText(null))
						.score(recommendationNode.path("score").isNull() ? null :
							recommendationNode.path("score").asDouble())
						.reasonType(recommendationNode.path("reason_type").asText(null))
						.reasonDetails(reasonDetails)
						.domain(recommendationNode.path("domain").asText(null))
						.keywords(keywords)
						.category(recommendationNode.path("category").asText(null))
						.publishedAt(recommendationNode.path("published_at").asText(null))
						.summary(recommendationNode.path("summary").asText(null))
						.build()
					);
				}
			}

			return GeneralRecommendationResponseDTO.builder()
				.recommendations(recommendations)
				.userClusterId(root.path("user_cluster_id").isNull() ? null : root.path("user_cluster_id").asLong())
				.clusterDescription(root.path("cluster_description").asText(null))
				.totalRecommendations(
					root.path("total_recommendations").isNull() ? null : root.path("total_recommendations").asInt())
				.generatedAt(root.path("generated_at").asText(null))
				.algorithmVersion(root.path("algorithm_version").asText(null))
				.personalizationScore(
					root.path("personalization_score").isNull() ? null : root.path("personalization_score").asDouble())
				.diversityScore(root.path("diversity_score").isNull() ? null : root.path("diversity_score").asDouble())
				.build();

		} catch (Exception e) {
			log.error("AI 추천 응답 파싱 실패: {}", e.getMessage());
			throw new GeneralException(ErrorStatus._AI_SERVER_ERROR);
		}
	}
}
