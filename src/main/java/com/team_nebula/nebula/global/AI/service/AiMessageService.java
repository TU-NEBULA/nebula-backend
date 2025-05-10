package com.team_nebula.nebula.global.AI.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team_nebula.nebula.domain.star.dto.request.AnalyzeHtmlRequestDTO;
import com.team_nebula.nebula.global.AI.dto.GetThumbnailAndKeywordsResponseDTO;
import com.team_nebula.nebula.global.apipayload.code.status.ErrorStatus;
import com.team_nebula.nebula.global.apipayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiMessageService {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${rabbitmq.queue.extract-data}")
    private String extractDataQueue;

    public GetThumbnailAndKeywordsResponseDTO analyzeHtmlFile(Long userId, String htmlFileKey) {
        try {
            // 1. 요청 메시지 생성
            Map<String, Object> message = new HashMap<>();
            message.put("s3_key", htmlFileKey);
            message.put("user_id", userId);

            // 2. JSON 문자열로 변환
            String jsonMessage = objectMapper.writeValueAsString(message);

            // 3. 메시지 전송 및 응답 대기 (5초 제한)
            Message response = rabbitTemplate.sendAndReceive(extractDataQueue, new Message(jsonMessage.getBytes()));

            if (response == null) {
                throw new GeneralException(ErrorStatus._AI_EXTRACT_DATA_ERROR);
            }

            String responseJson = new String(response.getBody());

            // 4. 응답 JSON을 DTO로 변환
            return objectMapper.readValue(responseJson, GetThumbnailAndKeywordsResponseDTO.class);

        } catch (Exception e) {
            throw new GeneralException(ErrorStatus._AI_EXTRACT_DATA_ERROR);
        }
    }
}

