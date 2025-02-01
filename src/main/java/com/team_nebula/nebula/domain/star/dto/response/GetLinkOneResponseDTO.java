package com.team_nebula.nebula.domain.star.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetLinkOneResponseDTO {
    private Long starId;
    private String categoryName;
    private String title;
    private String siteUrl;
    private String thumbnailUrl;
    private String summaryAI;
    private String userMemo;
    private int views;
    private List<String> keywordList;
}
