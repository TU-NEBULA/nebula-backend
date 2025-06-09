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
public class CharResponseDTO {
	private String sessionId;
	private List<MessageResponseDTO> messages;

}
