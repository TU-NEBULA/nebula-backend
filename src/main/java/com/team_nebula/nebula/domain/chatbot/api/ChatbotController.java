package com.team_nebula.nebula.domain.chatbot.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.team_nebula.nebula.domain.chatbot.dto.request.ChatRequestDTO;
import com.team_nebula.nebula.domain.chatbot.dto.request.SessionRequestDTO;
import com.team_nebula.nebula.domain.chatbot.dto.response.CharResponseDTO;
import com.team_nebula.nebula.domain.chatbot.dto.response.SessionListResponseDTO;
import com.team_nebula.nebula.domain.chatbot.dto.response.SessionsResponseDTO;
import com.team_nebula.nebula.domain.chatbot.dto.response.SessionResponseDTO;
import com.team_nebula.nebula.domain.chatbot.service.ChatbotService;
import com.team_nebula.nebula.global.annotation.AuthUser;
import com.team_nebula.nebula.global.apipayload.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Tag(name = "[챗봇]")
public class ChatbotController {

	private final ChatbotService chatbotService;

	@Operation(summary = "새 채팅 세션 생성", description = "새로운 채팅 세션을 생성하는 API")
	@PostMapping("/sessions")
	public ApiResponse<SessionResponseDTO> createSession(
		@AuthUser Long userId,
		@RequestBody SessionRequestDTO request) {
		return ApiResponse.onSuccess(chatbotService.createSession(userId, request));
	}

	@Operation(summary = "사용자 채팅 세션 목록 조회", description = "사용자 채팅 세션 목록을 조회하는 API")
	@GetMapping("/sessions")
	public ApiResponse<SessionsResponseDTO> getSessions(
		@AuthUser Long userId,
		@RequestParam(name = "limit", defaultValue = "20") int limit,
		@RequestParam(name = "offset", defaultValue = "0") int offset) {
		return ApiResponse.onSuccess(chatbotService.getSessions(userId, limit, offset));
	}

	@Operation(summary = "채팅 세션 정보 수정", description = "채팅 세션 정보를 수정하는 API")
	@PutMapping("/sessions/{sessionId}")
	public ApiResponse<SessionListResponseDTO> updateSession(
		@AuthUser Long userId,
		@PathVariable String sessionId,
		@RequestBody SessionRequestDTO request) {
		return ApiResponse.onSuccess(chatbotService.updateSession(userId, sessionId, request));
	}

	@Operation(summary = "채팅 스트림", description = "채팅 세션에서 메시지를 스트리밍하는 API")
	@PostMapping("/stream")
	public SseEmitter chatStream(
		@RequestParam Long userId,
		@RequestBody ChatRequestDTO request) {
		return chatbotService.chatStream(userId, request);
	}

	@Operation(summary = "채팅 세션 메시지 조회", description = "채팅 세션의 메시지를 조회하는 API")
	@GetMapping("/sessions/{sessionId}/messages")
	public ApiResponse<List<CharResponseDTO>> getSessionMessages(
		@AuthUser Long userId,
		@PathVariable String sessionId) {
		return ApiResponse.onSuccess(chatbotService.getSessionMessages(userId, sessionId));
	}
}
