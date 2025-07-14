package com.team_nebula.nebula.domain.chatbot.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponseDTO {
	private String sessionId;

	private List<MessageResponseDTO> messages;

	private PaginationResponseDTO pagination;
} 