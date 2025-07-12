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
public class SearchBasedRecommendationResponseDTO {
	private List<SearchBasedRecommendationDTO> recommendations;

	private List<String> queryKeywords;

	private List<String> expandedConcepts;

	private String searchIntent;

	private Integer totalRecommendations;

	private Long processingTimeMs;

	private Double similarityThresholdUsed;
} 