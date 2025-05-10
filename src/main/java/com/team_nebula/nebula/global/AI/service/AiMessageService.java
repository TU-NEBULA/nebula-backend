package com.team_nebula.nebula.global.AI.service;

import com.team_nebula.nebula.domain.star.dto.request.AnalyzeHtmlRequestDTO;
import com.team_nebula.nebula.global.AI.dto.GetThumbnailAndKeywordsResponseDTO;
import com.team_nebula.nebula.global.apipayload.code.status.ErrorStatus;
import com.team_nebula.nebula.global.apipayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiMessageService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    public GetThumbnailAndKeywordsResponseDTO analyzeHtmlFile(String htmlFileKey) {
        AnalyzeHtmlRequestDTO request = AnalyzeHtmlRequestDTO.builder()
                .htmlFileKey(htmlFileKey)
                .build();

        try {
            // AI 서버로 메시지 전송하고 응답 대기 (RPC 방식)
            GetThumbnailAndKeywordsResponseDTO response = (GetThumbnailAndKeywordsResponseDTO) rabbitTemplate.convertSendAndReceive(
                    exchange,
                    routingKey,
                    request
            );

            if (response == null) {
                throw new GeneralException(ErrorStatus._AI_EXTRACT_DATA_ERROR);
            }
            return response;

        } catch (Exception e) {
            throw new GeneralException(ErrorStatus._AI_EXTRACT_DATA_ERROR);
        }
    }
}

