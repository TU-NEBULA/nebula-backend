package com.team_nebula.nebula.domain.star.dto.request;

import lombok.Getter;
import java.util.List;

@Getter
public class CreateStarRequestDTO {

    private Long userId;
    private String title;
    private String siteUrl;
    private String summaryAI;
    private String userMemo;
    private String embedding;
    private String categoryName;
    private List<String> keywordList;

    public String getSummaryAI() {
        return (summaryAI == null || summaryAI.isBlank()) ? "No summary provided" : summaryAI;
    }

    public String getUserMemo() {
        return (userMemo == null || userMemo.isBlank()) ? "No memo provided" : userMemo;
    }
}
