package com.team_nebula.nebula.domain.keyword.service;

import com.team_nebula.nebula.domain.keyword.repository.KeywordRepository;
import com.team_nebula.nebula.domain.star.entity.Star;
import com.team_nebula.nebula.global.apipayload.code.status.ErrorStatus;
import com.team_nebula.nebula.global.apipayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
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

    @Override
    public void updateKeywordsForStar(Star star, List<String> newKeywordNames) {
        keywordRepository.removeKeywordRelations(star.getId());

        if (newKeywordNames != null && !newKeywordNames.isEmpty()) {
            keywordRepository.linkStarToKeywords(star.getId(), newKeywordNames);
        }
    }

    public String deleteKeywords() {
        List<String> orphanKeywords = keywordRepository.removeOrphanKeywords();
        if (orphanKeywords.isEmpty()) {
            throw new GeneralException(ErrorStatus._ORPHAN_KEYWORD_NOT_EXIST);
        }
        String deleteMessage = "Keywords deleted: " + orphanKeywords;
        return deleteMessage;
    }


//    @Scheduled(cron = "0 0 3 * * ?")
//    public void cleanUpOrphanKeywords() {
//        log.info("고립된 키워드 정리 시작");
//        keywordRepository.removeOrphanKeywords();
//        log.info("고립된 키워드 정리 완료");
//    }
}
