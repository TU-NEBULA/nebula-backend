package com.team_nebula.nebula.domain.chatbot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionListResponseDTO {
	private String sessionId;
	private String title;
	private String sessionType;
	private String createdAt;
	private String updatedAt;
	private boolean isActive;
}