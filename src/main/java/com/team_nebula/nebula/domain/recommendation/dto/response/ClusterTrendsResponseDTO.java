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
public class ClusterTrendsResponseDTO {
	private List<ClusterTrendDTO> clusterTrends;

	private Object globalTrends;

	private String generatedAt;

	private String analysisPeriod;
} 