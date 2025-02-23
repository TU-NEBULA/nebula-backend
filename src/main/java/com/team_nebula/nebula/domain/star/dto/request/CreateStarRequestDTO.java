package com.team_nebula.nebula.domain.star.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateStarRequestDTO {

    private String thumbnailUrl;
    private String summaryAI;
    private String userMemo;
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
