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
        if (summaryAI == null || summaryAI.isBlank()) {
            return "사용자가 AI요약을 하지 않았습니다.";
        } else {
            return summaryAI;
        }
    }

    public String getUserMemo() {
        if (userMemo == null || userMemo.isBlank()) {
            return "사용자가 메모를 입력하지 않았습니다.";
        } else {
            return userMemo;
        }
    }
}
