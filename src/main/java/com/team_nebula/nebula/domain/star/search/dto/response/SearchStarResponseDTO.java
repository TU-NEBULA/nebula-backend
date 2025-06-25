package com.team_nebula.nebula.domain.star.search.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchStarResponseDTO {
    private UUID starId;
    private String title;
    private String siteUrl;
    private String thumbnailUrl;
    private String summaryAI;
    private String userMemo;
    private Integer views;
    private String faviconUrl;
    private OffsetDateTime lastAccessedAt;
    private List<String> keywords;
    private Double score; // 검색 점수
}
