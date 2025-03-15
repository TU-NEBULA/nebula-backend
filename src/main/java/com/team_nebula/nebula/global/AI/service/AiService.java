package com.team_nebula.nebula.global.AI.service;

import com.team_nebula.nebula.domain.user.entity.UserNode;
import com.team_nebula.nebula.global.AI.dto.GetThumbnailAndKeywordsResponseDTO;
import com.team_nebula.nebula.global.apipayload.code.status.ErrorStatus;
import com.team_nebula.nebula.global.apipayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiService {

    @Value("${ai.url.extract-data}")
    private String aiExtractDataUrl;

    @Value("${ai.url.keyword-sync}")
    private String aiKeywordSyncUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    private final WebClient webClient;

    @Autowired
    public AiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

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

//    public Mono<GetThumbnailAndKeywordsResponseDTO> analyzeHtmlFile(UUID starId, Long userId, String htmlFileKey) {
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("id", String.valueOf(starId));
//        requestBody.put("user_id", String.valueOf(userId));
//        requestBody.put("s3_key", htmlFileKey);
//
//        return webClient.post()
//                .uri(aiExtractDataUrl)
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(requestBody)
//                .retrieve()
//                .bodyToMono(GetThumbnailAndKeywordsResponseDTO.class)
//                .doOnSuccess(response -> log.info("AI 서버 응답 성공: {}", response))
//                .doOnError(e -> log.error("AI 서버 요청 실패: {}", e.getMessage()))
//                .onErrorMap(e -> new GeneralException(ErrorStatus._AI_EXTRACT_DATA_ERROR));
//    }

    public Mono<Void> checkUpdatedNum(UserNode userNode) {
        userNode.updateUpdatedCnt();

        // 50단위로 호출, 증가할때는 피보나치 수열
        if (userNode.getUpdatedCnt() >= 50) {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("user_id", String.valueOf(userNode.getUserId()));

            return webClient.post()
                    .uri(aiKeywordSyncUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .doOnSuccess(response -> log.info("Keyword sync 성공: userId={}", userNode.getUserId()))
                    .doOnError(e -> log.error("Keyword sync 실패: {}", e.getMessage()))
                    .onErrorMap(e -> new GeneralException(ErrorStatus._AI_KEYWORD_SYNC_ERROR));
        }
        return Mono.empty();
    }
}
