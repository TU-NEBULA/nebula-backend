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
public class RecommendationDTO {
	private String bookmarkId;

	private String title;

	private String url;

	private Double score;

	private String reasonType;

	private ReasonDetailsDTO reasonDetails;

	private String domain;

	private List<String> keywords;

	private String category;

	private String publishedAt;

	private String summary;

	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ReasonDetailsDTO {
		private String algorithm;

		@JsonProperty("similarity_score")
		private Double similarityScore;

		@JsonProperty("cluster_score")
		private Double clusterScore;

		@JsonProperty("popularity_score")
		private Double popularityScore;
	}
} 