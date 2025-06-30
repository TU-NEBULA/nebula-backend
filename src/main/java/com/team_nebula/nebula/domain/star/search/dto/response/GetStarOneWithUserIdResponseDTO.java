package com.team_nebula.nebula.domain.star.search.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class GetStarOneWithUserIdResponseDTO {
    private UUID starId;
    private Long userId;
    private String categoryName;
    private String title;
    private String siteUrl;
    private String thumbnailUrl;
    private String summaryAI;
    private String userMemo;
    private Integer views;
    private String faviconUrl;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime lastAccessedAt;
    private List<String> keywordList;
}
