package com.team_nebula.nebula.domain.star.converter;

import com.team_nebula.nebula.domain.star.dto.response.GetLinkOneResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetStarListResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetStarOneResponseDTO;
import com.team_nebula.nebula.domain.star.entity.Star;

import java.util.ArrayList;
import java.util.Collections;
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

    public static GetLinkOneResponseDTO convertToLinkOneDto(Map<String, Object> data) {
        return GetLinkOneResponseDTO.builder()
                .linkId((Long) data.get("linkId"))
                .sharedKeywordNum((Integer) data.get("sharedKeywordNum"))
                .similarity((Double) data.get("similarity"))
                .linkedNodeIdList((List<Long>) data.get("linkedNodeIdList"))
                .build();
    }

    public static GetStarListResponseDTO convertToStarListDto(List<Map<String, Object>> queryResult) {
        if (queryResult == null || queryResult.isEmpty()) {
            return GetStarListResponseDTO.builder()
                    .type("StarWithLinks")
                    .totalStarCnt(0)
                    .totalLinkCnt(0)
                    .starListDto(Collections.emptyList())
                    .linkListDto(Collections.emptyList())
                    .build();
        }

        List<GetStarOneResponseDTO> starList = new ArrayList<>();
        List<GetLinkOneResponseDTO> linkList = new ArrayList<>();

        queryResult.forEach(row -> {
            List<Map<String, Object>> starDataList = (List<Map<String, Object>>) row.get("stars");
            if (starDataList != null) {
                starDataList.forEach(starData -> starList.add(convertToStarOneDto(starData)));
            }

            List<Map<String, Object>> linkDataList = (List<Map<String, Object>>) row.get("links");
            if (linkDataList != null) {
                linkDataList.forEach(linkData -> linkList.add(convertToLinkOneDto(linkData)));
            }
        });

        return GetStarListResponseDTO.builder()
                .type("StarWithLinks")
                .totalStarCnt(starList.size())
                .totalLinkCnt(linkList.size())
                .starListDto(starList)
                .linkListDto(linkList)
                .build();
    }
}
