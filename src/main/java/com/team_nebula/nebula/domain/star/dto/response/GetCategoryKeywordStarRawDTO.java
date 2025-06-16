package com.team_nebula.nebula.domain.star.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetCategoryKeywordStarRawDTO {
    private String categoryName;
    private String keywordName;

    private UUID starId;
    private String title;
    private String siteUrl;
    private String thumbnailUrl;
    private String faviconUrl;
    private String summaryAI;
    private String userMemo;
    private Integer views;
    private OffsetDateTime lastAccessedAt;

}
