package com.team_nebula.nebula.domain.recommendation.dto.request;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchBasedRecommendationRequestDTO {
	private String query;

	@Builder.Default
	private Integer limit = 10;

	@Builder.Default
	private Boolean includeSimilarQueries = true;

	@Builder.Default
	private Boolean boostUserPreferences = true;

	@Builder.Default
	private String sessionId = "default_session";

	@Builder.Default
	private List<String> previousQueries = new ArrayList<>();
} 