package com.team_nebula.nebula.global.AI.service;

import com.team_nebula.nebula.global.AI.dto.GetThumbnailAndKeywordsResponseDTO;
import com.team_nebula.nebula.global.apipayload.code.status.ErrorStatus;
import com.team_nebula.nebula.global.apipayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiService {

    @Value("${ai.url.extract-data}")
    private String aiExtractDataUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public GetThumbnailAndKeywordsResponseDTO analyzeHtmlFile(UUID starId, Long userId, String htmlFileKey) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("id", String.valueOf(starId));
            requestBody.put("user_id", String.valueOf(userId));
            requestBody.put("s3_key", htmlFileKey);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            // AI 서버 요청 및 응답 받기
            ResponseEntity<GetThumbnailAndKeywordsResponseDTO> responseEntity = restTemplate.exchange(
                    aiExtractDataUrl,
                    HttpMethod.POST,
                    requestEntity,
                    GetThumbnailAndKeywordsResponseDTO.class
            );

            return responseEntity.getBody();
        } catch (Exception e) {
            log.error("AI 서버 요청 실패: {}", e.getMessage());
            throw new GeneralException(ErrorStatus._AI_EXTRACT_DATA_ERROR);
        }
    }
}
