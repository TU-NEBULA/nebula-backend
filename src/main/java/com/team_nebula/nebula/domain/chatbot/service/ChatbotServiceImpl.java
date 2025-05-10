package com.team_nebula.nebula.domain.chatbot.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team_nebula.nebula.domain.chatbot.dto.response.ChatResponseDTO;
import com.team_nebula.nebula.global.AI.service.AiMessageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService {

	private final AiMessageService aiMessageService;

	@Override
	@Transactional
	public ChatResponseDTO chat(Long userId, String prompt) {

		ChatResponseDTO response = aiMessageService.chatBot(userId, prompt);
		return response;
	}
}
