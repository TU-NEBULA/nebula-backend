package com.team_nebula.nebula.domain.keyword.service;


import java.util.List;

public interface KeywordQueryService {
    List<String> getKeywords(Long userId);
}
