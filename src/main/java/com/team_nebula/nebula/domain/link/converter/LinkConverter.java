package com.team_nebula.nebula.domain.link.converter;

import com.team_nebula.nebula.domain.star.dto.response.GetLinkOneResponseDTO;

public class LinkConverter {

    public static GetLinkOneResponseDTO convertToLinkOneDto(GetLinkOneResponseDTO data) {
        return GetLinkOneResponseDTO.builder()
                .linkId(data.getLinkId())
                .sharedKeywordNum(data.getSharedKeywordNum())
                .sharedKeywords(data.getSharedKeywords())
                .similarity(data.getSimilarity())
                .linkedNodeIdList(data.getLinkedNodeIdList())
                .build();
    }
}
