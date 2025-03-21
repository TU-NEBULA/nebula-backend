package com.team_nebula.nebula.domain.keyword.service;


import com.team_nebula.nebula.domain.keyword.dto.response.GetMostUsedKeywordListResponseDTO;

import java.util.List;

public interface KeywordQueryService {
    List<String> getKeywords(Long userId);

    GetMostUsedKeywordListResponseDTO getMostUsedKeywordList();
}
