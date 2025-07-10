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
public class GeneralRecommendationResponseDTO {
	private List<RecommendationDTO> recommendations;

	private Long userClusterId;

	private String clusterDescription;

	private Integer totalRecommendations;

	private String generatedAt;

	private String algorithmVersion;

	private Double personalizationScore;

	private Double diversityScore;
} 