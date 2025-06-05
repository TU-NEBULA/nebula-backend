package com.team_nebula.nebula.domain.chatbot.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team_nebula.nebula.domain.chatbot.dto.request.ChatRequestDTO;
import com.team_nebula.nebula.domain.chatbot.dto.request.SessionRequestDTO;
import com.team_nebula.nebula.domain.chatbot.dto.response.SessionResponseDTO;
import com.team_nebula.nebula.global.apipayload.code.status.ErrorStatus;
import com.team_nebula.nebula.global.apipayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService {

	private final RestTemplate restTemplate = new RestTemplate();
	private final ObjectMapper objectMapper;

	@Value("${ai.url.chat}")
	private String aiChatUrl;

	@Override
	public SessionResponseDTO createSession(Long userId, SessionRequestDTO request) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			Map<String, Object> requestBody = new HashMap<>();
			requestBody.put("title", request.getTitle());

			HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

			String urlWithParams = aiChatUrl + "/chat/sessions?user_id=" + userId;

			ResponseEntity<String> response = restTemplate.exchange(
				urlWithParams,
				HttpMethod.POST,
				requestEntity,
				String.class
			);

			log.info("AI server response: {}", response.getBody());

			String sessionId = null;
			try {
				JsonNode jsonNode = objectMapper.readTree(response.getBody());
				JsonNode dataNode = jsonNode.get("data");
				if (dataNode != null) {
					JsonNode idNode = dataNode.get("id");
					if (idNode != null) {
						sessionId = idNode.asText();
					}
				}
			} catch (Exception jsonException) {
				log.error("Failed to parse AI response JSON", jsonException);
			}

			return SessionResponseDTO.builder()
				.sessionId(sessionId)
				.build();

		} catch (Exception e) {
			log.error("Error creating session", e);
			throw new GeneralException(ErrorStatus._AI_CHATBOT_ERROR);
		}
	}

	@Override
	public SseEmitter chatStream(Long userId, ChatRequestDTO request) {
		SseEmitter emitter = new SseEmitter();

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			// AI 서비스가 기대하는 JSON 형식으로 요청 본문 생성
			Map<String, Object> requestBody = new HashMap<>();
			requestBody.put("userId", userId);
			requestBody.put("message", request.getMessage());
			requestBody.put("session_id", request.getSessionId());

			HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

			// 비동기로 스트림 응답 처리
			restTemplate.execute(
				aiChatUrl + "/chat/stream",
				org.springframework.http.HttpMethod.POST,
				requestObj -> {
					requestObj.getHeaders().setContentType(MediaType.APPLICATION_JSON);
					objectMapper.writeValue(requestObj.getBody(), requestBody);
				},
				response -> {
					try (var reader = new java.io.BufferedReader(
						new java.io.InputStreamReader(response.getBody()))) {
						String line;
						while ((line = reader.readLine()) != null) {
							if (!line.isEmpty()) {
								emitter.send(line);
							}
						}
						emitter.complete();
					}
					return null;
				}
			);
		} catch (Exception e) {
			log.error("Error in chat stream", e);
			emitter.completeWithError(e);
		}

		return emitter;
	}

}
