package com.team_nebula.nebula.domain.star.converter;

import com.team_nebula.nebula.domain.keyword.entity.Keyword;
import com.team_nebula.nebula.domain.star.dto.response.*;
import com.team_nebula.nebula.domain.star.entity.Star;
import com.team_nebula.nebula.domain.star.search.document.StarSearchDocument;
import com.team_nebula.nebula.domain.star.search.dto.response.GetStarOneWithUserIdResponseDTO;
import com.team_nebula.nebula.domain.star.search.dto.response.SearchStarResponseDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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
                .faviconUrl(data.getFaviconUrl())
                .lastAccessedAt(data.getLastAccessedAt())
                .keywordList(data.getKeywordList())
                .build();
    }

    public static GetSearchedStarListResponseDTO convertToStarListDto(List<GetSearchedStarOneResponseDTO> queryResult) {
        if (queryResult == null || queryResult.isEmpty()) {
            return emptySearchedStarListResponse();
        }

        Set<GetStarOneResponseDTO> starSet = extractStars(queryResult);
        Set<GetStarOneResponseDTO> linkedStarSet = extractLinkedStars(queryResult);
        Set<GetLinkOneResponseDTO> linkSet = extractLinks(queryResult);

        return buildSearchedStarListResponse(starSet, linkedStarSet, linkSet);
    }

    private static GetSearchedStarListResponseDTO emptySearchedStarListResponse() {
        return GetSearchedStarListResponseDTO.builder()
                .type("검색된 스타 - 링크 - 검색된 스타와 직접 연결된 스타")
                .totalStarCnt(0)
                .totalLinkCnt(0)
                .searchedStarListDto(Collections.emptyList())
                .linkListDto(Collections.emptyList())
                .linkedStarListDto(Collections.emptyList())
                .build();
    }

    private static Set<GetStarOneResponseDTO> extractStars(List<GetSearchedStarOneResponseDTO> queryResult) {
        Set<GetStarOneResponseDTO> starSet = new HashSet<>();
        queryResult.forEach(result -> Optional.ofNullable(result.getSearchedStar()).ifPresent(starSet::add));
        return starSet;
    }

    private static Set<GetStarOneResponseDTO> extractLinkedStars(List<GetSearchedStarOneResponseDTO> queryResult) {
        Set<GetStarOneResponseDTO> linkedStarSet = new HashSet<>();
        queryResult.forEach(result -> Optional.ofNullable(result.getLinkedStar())
                .filter(linkedStar -> linkedStar.getTitle() != null)
                .ifPresent(linkedStarSet::add));
        return linkedStarSet;
    }

    private static Set<GetLinkOneResponseDTO> extractLinks(List<GetSearchedStarOneResponseDTO> queryResult) {
        Set<GetLinkOneResponseDTO> linkSet = new HashSet<>();
        queryResult.forEach(result -> Optional.ofNullable(result.getLinkData())
                .filter(linkData -> linkData.getLinkId() != null)
                .ifPresent(linkSet::add));
        return linkSet;
    }

    private static GetSearchedStarListResponseDTO buildSearchedStarListResponse(
            Set<GetStarOneResponseDTO> starSet,
            Set<GetStarOneResponseDTO> linkedStarSet,
            Set<GetLinkOneResponseDTO> linkSet) {
        return GetSearchedStarListResponseDTO.builder()
                .type("검색된 스타 - 링크 - 검색된 스타와 직접 연결된 스타")
                .totalStarCnt(starSet.size())
                .totalLinkCnt(linkSet.size())
                .searchedStarListDto(new ArrayList<>(starSet))
                .linkListDto(new ArrayList<>(linkSet))
                .linkedStarListDto(new ArrayList<>(linkedStarSet))
                .build();
    }

    public static List<GetCategoryAndKeywordListDTO> convertToNestedDto(List<GetCategoryKeywordStarRawDTO> rawList) {
        Map<String, Map<String, List<Get2DStarOneResponseDTO>>> resultMap = new HashMap<>();

        for (GetCategoryKeywordStarRawDTO raw : rawList) {
            String category = raw.getCategoryName();
            String keyword = raw.getKeywordName();

            resultMap.computeIfAbsent(category, k -> new HashMap<>());
            Map<String, List<Get2DStarOneResponseDTO>> keywordMap = resultMap.get(category);

            if (keyword != null && raw.getStarId() != null) {
                keywordMap.computeIfAbsent(keyword, kw -> new ArrayList<>())
                        .add(convertToStarDto(raw));
            } else if (keyword != null) {
                keywordMap.computeIfAbsent(keyword, kw -> new ArrayList<>());
            }
        }

        return resultMap.entrySet().stream()
                .map(categoryEntry -> GetCategoryAndKeywordListDTO.builder()
                        .category(categoryEntry.getKey())
                        .keywordList(categoryEntry.getValue().entrySet().stream()
                                .map(keywordEntry -> GetKeywordAndStarListDTO.builder()
                                        .keyword(keywordEntry.getKey())
                                        .starList(keywordEntry.getValue())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    private static Get2DStarOneResponseDTO convertToStarDto(GetCategoryKeywordStarRawDTO raw) {
        return Get2DStarOneResponseDTO.builder()
                .starId(raw.getStarId())
                .title(raw.getTitle())
                .siteUrl(raw.getSiteUrl())
                .thumbnailUrl(raw.getThumbnailUrl())
                .summaryAI(raw.getSummaryAI())
                .faviconUrl(raw.getFaviconUrl())
                .userMemo(raw.getUserMemo())
                .views(raw.getViews())
                .lastAccessedAt(raw.getLastAccessedAt())
                .build();
    }

    public static SearchStarResponseDTO convertToSearchStarDTO(StarSearchDocument document, Double score) {
        UUID starId;
        try {
            starId = UUID.fromString(document.getId());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid UUID format for document ID: {}", document.getId());
            throw new IllegalArgumentException("Invalid star ID format", e);
        }

        return SearchStarResponseDTO.builder()
                .starId(starId)
                .title(document.getTitle())
                .siteUrl(document.getSiteUrl())
                .categoryName(document.getCategoryName())
                .thumbnailUrl(document.getThumbnailUrl())
                .summaryAI(document.getSummaryAI())
                .userMemo(document.getUserMemo())
                .views(document.getViews())
                .faviconUrl(document.getFaviconUrl())
                .lastAccessedAt(document.getLastAccessedAt())
                .keywords(document.getKeywords())
                .score(score)
                .build();
    }


    public static StarSearchDocument convertToSearchDocument(GetStarOneWithUserIdResponseDTO starDTO, String allContent) {
        String lastAccessedAtStr = null;
        if (starDTO.getLastAccessedAt() != null) {
            lastAccessedAtStr = starDTO.getLastAccessedAt().toString();
        }

        List<String> keywords = new ArrayList<>();
        if (starDTO.getKeywordList() != null) {
            keywords = starDTO.getKeywordList();
        }

        return StarSearchDocument.builder()
                .id(starDTO.getStarId().toString())
                .userId(starDTO.getUserId())
                .title(starDTO.getTitle())
                .categoryName(starDTO.getCategoryName())
                .siteUrl(starDTO.getSiteUrl())
                .summaryAI(starDTO.getSummaryAI())
                .userMemo(starDTO.getUserMemo())
                .keywords(keywords)
                .views(starDTO.getViews())
                .lastAccessedAt(lastAccessedAtStr)
                .thumbnailUrl(starDTO.getThumbnailUrl())
                .faviconUrl(starDTO.getFaviconUrl())
                .allContent(allContent)
                .build();
    }

    public static GetStarOneWithUserIdResponseDTO convertStarEvent(Star star, Long userId, String faviconUrl, String category) {
        List<String> keywordNames = new ArrayList<>();
        if (star.getKeywords() != null) {
            for (Keyword keyword : star.getKeywords()) {
                if (keyword.getName() != null) {
                    keywordNames.add(keyword.getName());
                }
            }
        }

        return GetStarOneWithUserIdResponseDTO.builder()
                .starId(star.getId())
                .userId(userId)
                .categoryName(category)
                .title(star.getTitle())
                .siteUrl(star.getSiteUrl())
                .thumbnailUrl(star.getThumbnailUrl())
                .summaryAI(star.getSummaryAI())
                .userMemo(star.getUserMemo())
                .views(star.getViews())
                .faviconUrl(faviconUrl)
                .lastAccessedAt(star.getLastAccessedAt())
                .keywordList(keywordNames)
                .build();
    }

}
