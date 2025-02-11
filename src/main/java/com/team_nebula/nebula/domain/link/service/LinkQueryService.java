package com.team_nebula.nebula.domain.link.service;

import com.team_nebula.nebula.domain.star.dto.response.GetLinkOneResponseDTO;
import com.team_nebula.nebula.domain.user.entity.UserNode;

import java.util.List;
import java.util.UUID;

public interface LinkQueryService {
    public List<GetLinkOneResponseDTO> getAllLink(Long userId);

    public List<GetLinkOneResponseDTO> getLinkInCategory(Long userId, UUID categoryId);

    public List<GetLinkOneResponseDTO> getLinkInKeyword(Long userId, Long keywordId);

}
