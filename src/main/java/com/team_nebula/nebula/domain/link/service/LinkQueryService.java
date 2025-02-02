package com.team_nebula.nebula.domain.link.service;

import com.team_nebula.nebula.domain.star.dto.response.GetLinkOneResponseDTO;
import com.team_nebula.nebula.domain.user.entity.UserNode;

import java.util.List;

public interface LinkQueryService {
    public List<GetLinkOneResponseDTO> getAllLink(UserNode userNode);

    public List<GetLinkOneResponseDTO> getLinkInCategory(Long userId, Long categoryId);

    public List<GetLinkOneResponseDTO> getLinkInKeyword(Long userId, Long keywordId);

}
