package com.team_nebula.nebula.domain.keyword.service;

import com.team_nebula.nebula.domain.keyword.repository.KeywordRepository;
import com.team_nebula.nebula.domain.link.repository.LinkRepository;
import com.team_nebula.nebula.domain.star.entity.Star;
import com.team_nebula.nebula.domain.star.repository.StarRepository;
import com.team_nebula.nebula.global.apipayload.code.status.ErrorStatus;
import com.team_nebula.nebula.global.apipayload.exception.GeneralException;
import jakarta.persistence.EntityNotFoundException;
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
    private final LinkRepository linkrepository;
    private final StarRepository starrepository;

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
        System.out.println("------변경 전 스타 - 키워드1 --------:"+ star.getKeywords());
        // 키워드와 스타 연결 업데이트
        Star updateStar = keywordRepository.removeLinkAndReconnect(star.getId(), newKeywordNames);

        System.out.println("------변경된 스타 - 키워드1 --------:"+ updateStar.getKeywords());

        linkrepository.updateLinksBetweenStars(updateStar.getId());
    }

    @Override
    public String deleteKeywords() {
        List<String> orphanKeywords = keywordRepository.removeOrphanKeywords();
        if (orphanKeywords.isEmpty()) {
            throw new GeneralException(ErrorStatus._ORPHAN_KEYWORD_NOT_EXIST);
        }
        return "Keywords deleted: " + orphanKeywords;
    }


//    @Scheduled(cron = "0 0 3 * * ?")
//    public void cleanUpOrphanKeywords() {
//        log.info("고립된 키워드 정리 시작");
//        keywordRepository.removeOrphanKeywords();
//        log.info("고립된 키워드 정리 완료");
//    }
}
