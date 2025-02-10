package com.team_nebula.nebula.domain.keyword.service;

import com.team_nebula.nebula.domain.keyword.repository.KeywordRepository;
import com.team_nebula.nebula.domain.star.entity.Star;
import com.team_nebula.nebula.global.apipayload.code.status.ErrorStatus;
import com.team_nebula.nebula.global.apipayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class KeywordCommandServiceImpl implements KeywordCommandService {

    private final KeywordRepository keywordRepository;

    @Override
    public void linkStarToKeywords(Star star, List<String> keywordNames) {
        if (keywordNames == null || keywordNames.isEmpty()) {
            throw new GeneralException(ErrorStatus._KEYWORD_NOT_INPUT);
        }

        keywordRepository.linkStarToKeywords(star.getId(), keywordNames);

        if (star.getKeywords() == null) {
            throw new GeneralException(ErrorStatus._KEYWORD_NOT_FOUND);
        }
    }
}
