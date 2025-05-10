package com.team_nebula.nebula.domain.chatbot.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team_nebula.nebula.domain.chatbot.dto.response.ChatResponseDTO;
import com.team_nebula.nebula.domain.chatbot.entity.ChatBot;
import com.team_nebula.nebula.domain.chatbot.repository.ChatbotRepository;
import com.team_nebula.nebula.global.AI.service.AiMessageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService {

	private final AiMessageService aiMessageService;
	private final ChatbotRepository chatbotRepository;

	@Override
	@Transactional
	public ChatResponseDTO chat(Long userId, String prompt) {

		ChatBot chatBot = ChatBot.builder()
			.chatId("1")
			.title("test")
			.content("test")
			.userId("1")
			.build();

		chatbotRepository.saveChat(chatBot);

		ChatResponseDTO response = ChatResponseDTO.builder()
			.message("성공")
			.build();

		ChatResponseDTO response = aiMessageService.chatBot(userId, prompt);
		return response;
	}
}
