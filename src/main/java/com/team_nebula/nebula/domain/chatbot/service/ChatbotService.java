package com.team_nebula.nebula.domain.chatbot.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.team_nebula.nebula.domain.chatbot.dto.request.ChatRequestDTO;
import com.team_nebula.nebula.domain.chatbot.dto.request.SessionRequestDTO;
import com.team_nebula.nebula.domain.chatbot.dto.response.ChatResponseDTO;
import com.team_nebula.nebula.domain.chatbot.dto.response.SessionListResponseDTO;
import com.team_nebula.nebula.domain.chatbot.dto.response.SessionResponseDTO;
import com.team_nebula.nebula.domain.chatbot.dto.response.SessionsResponseDTO;

public interface ChatbotService {
	SseEmitter chatStream(Long userId, ChatRequestDTO request);

	SessionsResponseDTO getSessions(Long userId, int limit, int offset);

	SessionListResponseDTO updateSession(Long userId, String sessionId, SessionRequestDTO request);

	SessionResponseDTO createSession(Long userId, SessionRequestDTO request);

	ChatResponseDTO getSessionMessages(Long userId, String sessionId, int page, int size);
}