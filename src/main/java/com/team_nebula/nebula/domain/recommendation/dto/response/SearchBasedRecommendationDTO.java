package com.team_nebula.nebula.domain.recommendation.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchBasedRecommendationDTO {
	@JsonProperty("bookmark_id")
	private String bookmarkId;

	private String title;

	private String url;

	private Double score;

	@JsonProperty("reason_type")
	private String reasonType;

	@JsonProperty("reason_details")
	private SearchBasedReasonDetailsDTO reasonDetails;

	private String domain;

	private List<String> keywords;

	private String category;

	@JsonProperty("published_at")
	private String publishedAt;

	private String summary;

	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class SearchBasedReasonDetailsDTO {
		private String type;

		@JsonProperty("search_query")
		private String searchQuery;

		@JsonProperty("expanded_keywords")
		private List<String> expandedKeywords;

		private List<RecommendationFactorDTO> factors;

		private Double confidence;
	}

	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RecommendationFactorDTO {
		private String factor;

		private String description;

		private Double weight;
	}
} 