package com.team_nebula.nebula.domain.star.converter;

import com.team_nebula.nebula.domain.star.dto.response.GetLinkOneResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetStarListResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetStarOneResponseDTO;

import java.util.*;

import static com.team_nebula.nebula.domain.link.converter.LinkConverter.convertToLinkOneDto;

public class StarConverter {

    public static GetStarOneResponseDTO convertToStarOneDto(GetStarOneResponseDTO data) {
        return GetStarOneResponseDTO.builder()
                .starId(data.getStarId())
                .categoryName(data.getCategoryName())
                .title(data.getTitle())
                .siteUrl(data.getSiteUrl())
                .thumbnailUrl(data.getThumbnailUrl())
                .summaryAI(data.getSummaryAI())
                .userMemo(data.getUserMemo())
                .views(data.getViews())
                .keywordList(data.getKeywordList())
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
            List<GetStarOneResponseDTO> starDataList = (List<GetStarOneResponseDTO>) row.get("stars");
            if (starDataList != null) {
                starDataList.forEach(starData -> starList.add(convertToStarOneDto(starData)));
            }

            List<GetLinkOneResponseDTO> linkDataList = (List<GetLinkOneResponseDTO>) row.get("links");
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
