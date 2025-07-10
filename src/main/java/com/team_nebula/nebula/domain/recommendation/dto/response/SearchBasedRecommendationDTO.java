package com.team_nebula.nebula.domain.recommendation.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchBasedRecommendationDTO {
	private String bookmarkId;

	private String title;

	private String url;

	private Double score;

	private String reasonType;

	private SearchBasedReasonDetailsDTO reasonDetails;

	private String domain;

	private List<String> keywords;

	private String category;

	private String publishedAt;

	private String summary;

	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class SearchBasedReasonDetailsDTO {
		private String type;

		private String searchQuery;

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