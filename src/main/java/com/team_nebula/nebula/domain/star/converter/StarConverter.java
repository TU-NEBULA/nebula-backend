package com.team_nebula.nebula.domain.star.converter;

import com.team_nebula.nebula.domain.star.dto.response.GetStarOneResponseDTO;
import com.team_nebula.nebula.domain.star.entity.Star;

import java.util.List;
import java.util.Map;

public class StarConverter {

    public static GetStarOneResponseDTO convertToStarOneDto(Map<String, Object> data) {
        Star star = (Star) data.get("s");

        return GetStarOneResponseDTO.builder()
                .starId(star.getId())
                .categoryName((String) data.get("categoryName"))
                .title(star.getTitle())
                .siteUrl(star.getSiteUrl())
                .thumbnailUrl(star.getThumbnailUrl())
                .summaryAI(star.getSummaryAI())
                .userMemo(star.getUserMemo())
                .views(star.getViews())
                .keywordList((List<String>) data.get("keywordList"))
                .build();
    }
}
