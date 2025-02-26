package com.team_nebula.nebula.domain.keyword.service;

import com.team_nebula.nebula.domain.star.entity.Star;

import java.util.List;

public interface KeywordCommandService {
    public void linkStarToKeywords(Star star, List<String> keywordNames);

    public void updateKeywordsForStar(Star star, List<String> newKeywordNames);

    public String deleteKeywords();
    }
