package com.team_nebula.nebula.domain.chatbot.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team_nebula.nebula.domain.chatbot.dto.request.ChatRequestDTO;
import com.team_nebula.nebula.domain.chatbot.dto.request.SessionRequestDTO;
import com.team_nebula.nebula.domain.chatbot.dto.response.CharResponseDTO;
import com.team_nebula.nebula.domain.chatbot.dto.response.MessageResponseDTO;
import com.team_nebula.nebula.domain.chatbot.dto.response.SessionListResponseDTO;
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
	private final WebClient webClient;

	@Value("${ai.url.chat}")
	private String aiChatUrl;

	@Override
	public SessionResponseDTO createSession(Long userId, SessionRequestDTO request) {
		try {
			String url = aiChatUrl + "/chat/sessions?user_id=" + userId;

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			Map<String, Object> requestBody = Map.of("title", request.getTitle());
			HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

			ResponseEntity<String> response = restTemplate.exchange(
				url,
				HttpMethod.POST,
				requestEntity,
				String.class
			);

			log.info("AI 서버 응답: {}", response.getBody());

			String sessionId = extractSessionId(response.getBody());

			return SessionResponseDTO.builder()
				.sessionId(sessionId)
				.build();

		} catch (Exception e) {
			log.error("세션 생성 중 오류 발생", e);
			throw new GeneralException(ErrorStatus._AI_CHATBOT_ERROR);
		}
	}

	private String extractSessionId(String responseBody) {
		try {
			JsonNode root = objectMapper.readTree(responseBody);
			return Optional.ofNullable(root.get("data"))
				.map(data -> data.get("id"))
				.map(JsonNode::asText)
				.orElse(null);
		} catch (Exception e) {
			log.error("AI 응답 JSON 파싱 실패", e);
			return null;
		}
	}

	@Override
	public List<SessionListResponseDTO> getSessions(Long userId, int limit, int offset) {
		try {
			String url = String.format("%s/chat/sessions?user_id=%d&limit=%d&offset=%d", aiChatUrl, userId, limit,
				offset);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			ResponseEntity<String> response = restTemplate.exchange(
				url,
				HttpMethod.GET,
				new HttpEntity<>(headers),
				String.class
			);

			log.info("AI 서버 세션 목록 응답: {}", response.getBody());

			return parseSessionsFromResponse(response.getBody());

		} catch (Exception e) {
			log.error("세션 목록 조회 중 오류 발생", e);
			throw new GeneralException(ErrorStatus._AI_CHATBOT_ERROR);
		}
	}

	private List<SessionListResponseDTO> parseSessionsFromResponse(String responseBody) {
		List<SessionListResponseDTO> sessions = new ArrayList<>();

		try {
			JsonNode root = objectMapper.readTree(responseBody);
			JsonNode sessionsNode = root.path("data").path("sessions");

			if (!sessionsNode.isArray()) {
				log.warn("세션 목록이 배열이 아닙니다.");
				return sessions;
			}

			for (JsonNode sessionNode : sessionsNode) {
				sessions.add(SessionListResponseDTO.builder()
					.sessionId(getText(sessionNode, "id"))
					.title(getText(sessionNode, "title"))
					.sessionType(getText(sessionNode, "session_type"))
					.createdAt(getText(sessionNode, "created_at"))
					.updatedAt(getText(sessionNode, "updated_at"))
					.isActive(sessionNode.path("is_active").asBoolean(true))
					.build()
				);
			}

		} catch (Exception e) {
			log.error("AI 세션 목록 JSON 파싱 실패", e);
		}

		return sessions;
	}

	private String getText(JsonNode node, String fieldName) {
		JsonNode valueNode = node.get(fieldName);
		return (valueNode != null && !valueNode.isNull()) ? valueNode.asText() : null;
	}

	@Override
	public SseEmitter chatStream(Long userId, ChatRequestDTO request) {
		SseEmitter emitter = new SseEmitter(300_000L);

		Map<String, Object> requestBody = buildChatRequestBody(userId, request);

		webClient.post()
			.uri(aiChatUrl + "/chat/stream")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(requestBody)
			.accept(MediaType.TEXT_EVENT_STREAM)
			.retrieve()
			.bodyToFlux(String.class)
			.subscribe(
				chunk -> {
					try {
						
						if (!chunk.isBlank()) {
							if (chunk.startsWith("data: ")) {
								chunk = chunk.substring(6);
							}
							emitter.send(SseEmitter.event()
								.data(chunk, MediaType.APPLICATION_JSON_UTF8));
						}
					} catch (Exception e) {
						log.error("SSE 전송 중 오류", e);
						emitter.completeWithError(e);
					}
				},
				error -> {
					log.error("AI 스트리밍 중 오류 발생", error);
					emitter.completeWithError(error);
				},
				() -> {
					log.info("스트리밍 완료");
					emitter.complete();
				}
			);

		return emitter;
	}

	private Map<String, Object> buildChatRequestBody(Long userId, ChatRequestDTO request) {
		Map<String, Object> body = new HashMap<>();
		body.put("userId", userId);
		body.put("message", request.getMessage());
		body.put("session_id", request.getSessionId());
		return body;
	}

	@Override
	public List<CharResponseDTO> getSessionMessages(Long userId, String sessionId) {
		try {
			String url = String.format("%s/chat/sessions/%s/messages?user_id=%d", aiChatUrl, sessionId, userId);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			ResponseEntity<String> response = restTemplate.exchange(
				url,
				HttpMethod.GET,
				new HttpEntity<>(headers),
				String.class
			);

			log.info("AI 서버 메시지 응답: {}", response.getBody());

			JsonNode root = objectMapper.readTree(response.getBody());
			JsonNode data = root.path("data");

			String receivedSessionId = getText(data, "session_id");
			JsonNode messagesNode = data.path("messages");

			List<MessageResponseDTO> messageList = new ArrayList<>();

			if (messagesNode.isArray()) {
				for (JsonNode messageNode : messagesNode) {
					messageList.add(MessageResponseDTO.builder()
						.id(getText(messageNode, "id"))
						.content(getText(messageNode, "content"))
						.role(getText(messageNode, "role"))
						.createdAt(getText(messageNode, "created_at"))
						.metadata(convertMetadata(messageNode.path("metadata")))
						.build()
					);
				}
			}

			CharResponseDTO responseDto = CharResponseDTO.builder()
				.sessionId(receivedSessionId)
				.messages(messageList)
				.build();

			return List.of(responseDto);

		} catch (Exception e) {
			log.error("세션 메시지 조회 중 오류 발생", e);
			throw new GeneralException(ErrorStatus._AI_CHATBOT_ERROR);
		}
	}

	private Map<String, String> convertMetadata(JsonNode metadataNode) {
		Map<String, String> metadata = new HashMap<>();
		if (metadataNode != null && metadataNode.isObject()) {
			metadataNode.fields().forEachRemaining(entry ->
				metadata.put(entry.getKey(), entry.getValue().asText())
			);
		}
		return metadata;
	}
}
