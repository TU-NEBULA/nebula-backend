package com.team_nebula.nebula.domain.chatbot.service;

import com.team_nebula.nebula.domain.chatbot.dto.response.ChatResponseDTO;

public interface ChatbotService {

	ChatResponseDTO chat(Long userId, String prompt);
}