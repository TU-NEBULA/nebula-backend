package com.team_nebula.nebula.domain.link.service;

import com.team_nebula.nebula.domain.link.repository.LinkRepository;
import com.team_nebula.nebula.domain.star.entity.Star;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkCommandServiceImpl implements LinkCommandService{

    private final LinkRepository linkRepository;

    @Override
    public void createLinksForStar(Star star) {
        linkRepository.createLinksBetweenStars(star.getId());
    }

}
