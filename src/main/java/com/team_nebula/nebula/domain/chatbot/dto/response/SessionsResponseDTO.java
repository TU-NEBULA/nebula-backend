package com.team_nebula.nebula.domain.chatbot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionsResponseDTO {
	private List<SessionListResponseDTO> sessions;
	private int total;
} 