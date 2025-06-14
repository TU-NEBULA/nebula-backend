package com.team_nebula.nebula.domain.chatbot.dto.response;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponseDTO {
	private String id;
	private String content;
	private String role;
	private String createdAt;
	private Map<String, String> metadata;
}