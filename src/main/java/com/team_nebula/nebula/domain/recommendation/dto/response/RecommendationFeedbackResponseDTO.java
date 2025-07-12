package com.team_nebula.nebula.domain.recommendation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationFeedbackResponseDTO {
	private String feedbackId;
	private Boolean processed;
	private Double impactScore;
	private Boolean modelUpdated;
	private String message;
} 