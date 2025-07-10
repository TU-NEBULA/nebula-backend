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
public class ClusterTrendDTO {
	private Integer clusterId;

	private String clusterName;

	private Integer memberCount;

	private List<TrendingKeywordDTO> trendingKeywords;

	private List<String> popularDomains;

	private List<String> activityPeakHours;

	private List<String> primaryInterests;

	private List<String> emergingTopics;
} 