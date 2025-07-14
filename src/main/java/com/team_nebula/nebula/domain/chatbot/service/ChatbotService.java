package com.team_nebula.nebula.domain.chatbot.service;

import java.util.List;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.team_nebula.nebula.domain.chatbot.dto.request.ChatRequestDTO;
import com.team_nebula.nebula.domain.chatbot.dto.request.SessionRequestDTO;
import com.team_nebula.nebula.domain.chatbot.dto.response.CharResponseDTO;
import com.team_nebula.nebula.domain.chatbot.dto.response.SessionListResponseDTO;
import com.team_nebula.nebula.domain.chatbot.dto.response.SessionsResponseDTO;
import com.team_nebula.nebula.domain.chatbot.dto.response.SessionResponseDTO;

public interface ChatbotService {
	SseEmitter chatStream(Long userId, ChatRequestDTO request);

	SessionsResponseDTO getSessions(Long userId, int limit, int offset);

	SessionListResponseDTO updateSession(Long userId, String sessionId, SessionRequestDTO request);

	SessionResponseDTO createSession(Long userId, SessionRequestDTO request);

	List<CharResponseDTO> getSessionMessages(Long userId, String sessionId);
}