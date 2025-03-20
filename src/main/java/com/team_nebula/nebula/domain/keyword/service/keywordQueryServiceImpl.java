package com.team_nebula.nebula.domain.keyword.service;

import com.team_nebula.nebula.domain.keyword.repository.KeywordRepository;
import com.team_nebula.nebula.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class keywordQueryServiceImpl implements  KeywordQueryService {

    private final KeywordRepository keywordRepository;

    @Override
    public List<String> getKeywords(Long userId){
        return keywordRepository.getAllKeywordNames(userId);
    }
}
