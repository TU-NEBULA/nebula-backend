package com.team_nebula.nebula.domain.link.service;

import com.team_nebula.nebula.domain.link.repository.LinkRepository;
import com.team_nebula.nebula.domain.star.entity.Star;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LinkCommandServiceImpl implements LinkCommandService{

    private final LinkRepository linkRepository;

    @Override
    public void createLinksForStar(Long userId, Star star) {
        linkRepository.createLinksBetweenStars(userId, star.getId());
    }

}
