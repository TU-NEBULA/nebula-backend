package com.team_nebula.nebula.domain.chatbot.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team_nebula.nebula.domain.chatbot.dto.response.ChatResponseDTO;
import com.team_nebula.nebula.domain.chatbot.service.ChatbotService;
import com.team_nebula.nebula.global.annotation.AuthUser;
import com.team_nebula.nebula.global.apipayload.ApiResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "[챗봇]")
@RequestMapping("/api/v1/chatbots")
public class ChatbotController {

	private final ChatbotService chatbotService;

	@PostMapping
	public ApiResponse<ChatResponseDTO> chat(
		@AuthUser Long userId,
		@RequestBody String prompt) {
		ChatResponseDTO response = chatbotService.chat(userId, prompt);
		return ApiResponse.onSuccess(response);
	}
}
