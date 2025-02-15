package com.team_nebula.nebula.domain.star.converter;

import com.team_nebula.nebula.domain.star.dto.response.*;

import java.util.*;

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

//    public static GetStarListResponseDTO convertToStarListDto(List<Map<String, Object>> queryResult) {
//        if (queryResult == null || queryResult.isEmpty()) {
//            return GetStarListResponseDTO.builder()
//                    .type("StarWithLinks")
//                    .totalStarCnt(0)
//                    .totalLinkCnt(0)
//                    .starListDto(Collections.emptyList())
//                    .linkListDto(Collections.emptyList())
//                    .build();
//        }
//
//        List<GetStarOneResponseDTO> starList = new ArrayList<>();
//        List<GetLinkOneResponseDTO> linkList = new ArrayList<>();
//
//        queryResult.forEach(row -> {
//            List<GetStarOneResponseDTO> starDataList = (List<GetStarOneResponseDTO>) row.get("stars");
//            if (starDataList != null) {
//                starDataList.forEach(starData -> starList.add(convertToStarOneDto(starData)));
//            }
//
//            List<GetLinkOneResponseDTO> linkDataList = (List<GetLinkOneResponseDTO>) row.get("links");
//            if (linkDataList != null) {
//                linkDataList.forEach(linkData -> linkList.add(convertToLinkOneDto(linkData)));
//            }
//        });
//
//        return GetStarListResponseDTO.builder()
//                .type("StarWithLinks")
//                .totalStarCnt(starList.size())
//                .totalLinkCnt(linkList.size())
//                .starListDto(starList)
//                .linkListDto(linkList)
//                .build();
//    }
public static GetSearchedStarListResponseDTO convertToStarListDto(List<GetSearchedStarOneResponseDTO> queryResult) {
    if (queryResult == null || queryResult.isEmpty()) {
        return GetSearchedStarListResponseDTO.builder()
                .type("검새된 스타 - 링크 - 검색된 스타와 직접 연결된 스타")
                .totalStarCnt(0)
                .totalLinkCnt(0)
                .searchedStarListDto(Collections.emptyList())
                .linkListDto(Collections.emptyList())
                .linkedStarListDto(Collections.emptyList())
                .build();
    }

    Set<GetStarOneResponseDTO> starSet = new HashSet<>();
    Set<GetStarOneResponseDTO> linkedStarSet = new HashSet<>();
    Set<GetLinkOneResponseDTO> linkSet = new HashSet<>();

    queryResult.forEach(result -> {
        if (result.getSearchedStar() != null) {
            starSet.add(result.getSearchedStar());
        }
        if (result.getLinkedStar() != null && result.getLinkedStar().getTitle() != null) {
            linkedStarSet.add(result.getLinkedStar());
        }
        if (result.getLinkData() != null && result.getLinkData().getLinkId() != null) {
            linkSet.add(result.getLinkData());
        }
    });

    List<GetStarOneResponseDTO> starList = new ArrayList<>(starSet);
    List<GetStarOneResponseDTO> linkedStarList = new ArrayList<>(linkedStarSet);
    List<GetLinkOneResponseDTO> linkList = new ArrayList<>(linkSet);

    return GetSearchedStarListResponseDTO.builder()
            .type("검색된 스타 - 링크 - 검색된 스타와 직접 연결된 스타")
            .totalStarCnt(starList.size())
            .totalLinkCnt(linkList.size())
            .searchedStarListDto(starList)
            .linkListDto(linkList)
            .linkedStarListDto(linkedStarList)
            .build();
    }

}
