package com.team_nebula.nebula.domain.keyword.service;

import com.team_nebula.nebula.domain.user.entity.User;

import java.util.List;

public interface KeywordQueryService {
    List<String> getKeywords(User user);
}
