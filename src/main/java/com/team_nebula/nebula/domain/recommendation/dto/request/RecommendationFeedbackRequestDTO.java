package com.team_nebula.nebula.domain.recommendation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationFeedbackRequestDTO {
	private String recommendationId;
	private String bookmarkId;
	private String actionType;
	private LocalDateTime shownAt;
	private LocalDateTime actionAt;
	private String sessionId;
	private String pageContext;
	private Integer recommendationPosition;
	private Integer explicitRating;
	private Integer engagementTime;
} 