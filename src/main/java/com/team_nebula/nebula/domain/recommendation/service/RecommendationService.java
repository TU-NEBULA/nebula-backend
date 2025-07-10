package com.team_nebula.nebula.domain.recommendation.service;

import com.team_nebula.nebula.domain.recommendation.dto.request.RecommendationFeedbackRequestDTO;
import com.team_nebula.nebula.domain.recommendation.dto.request.SearchBasedRecommendationRequestDTO;
import com.team_nebula.nebula.domain.recommendation.dto.response.ClusterTrendsResponseDTO;
import com.team_nebula.nebula.domain.recommendation.dto.response.GeneralRecommendationResponseDTO;
import com.team_nebula.nebula.domain.recommendation.dto.response.RecommendationFeedbackResponseDTO;
import com.team_nebula.nebula.domain.recommendation.dto.response.SearchBasedRecommendationResponseDTO;

public interface RecommendationService {
	GeneralRecommendationResponseDTO generalRecommendation(Long userId, int limit, String category,
		boolean excludeViewed, boolean diversify, String timeRange);

	SearchBasedRecommendationResponseDTO searchBasedRecommendation(Long userId,
		SearchBasedRecommendationRequestDTO request);

	ClusterTrendsResponseDTO clusterTrends(Integer clusterId, String timePeriod, boolean includeGlobal);

	RecommendationFeedbackResponseDTO feedback(Long userId, RecommendationFeedbackRequestDTO request);
}