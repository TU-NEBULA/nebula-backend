package com.team_nebula.nebula.domain.recommendation.service;

import com.team_nebula.nebula.domain.recommendation.dto.response.GeneralRecommendationResponseDTO;

public interface RecommendationService {
	GeneralRecommendationResponseDTO generalRecommendation(Long userId, int limit, String category,
		boolean excludeViewed, boolean diversify, String timeRange);
}