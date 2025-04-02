package com.team_nebula.nebula.domain.star.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetStarOneResponseDTO {
    private UUID starId;
    private String categoryName;
    private String title;
    private String siteUrl;
    private String thumbnailUrl;
    private String summaryAI;
    private String userMemo;
    private int views;
    private String faviconUrl;
    private List<String> keywordList;
}
