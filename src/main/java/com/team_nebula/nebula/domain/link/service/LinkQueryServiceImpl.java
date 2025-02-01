package com.team_nebula.nebula.domain.link.service;

import com.team_nebula.nebula.domain.link.entity.Link;
import com.team_nebula.nebula.domain.link.repository.LinkRepository;
import com.team_nebula.nebula.domain.star.dto.response.GetLinkOneResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetStarOneResponseDTO;
import com.team_nebula.nebula.domain.user.entity.UserNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LinkQueryServiceImpl implements LinkQueryService {

    private final LinkRepository linkRepository;

    @Override
    public List<GetLinkOneResponseDTO> getAllLink(UserNode userNode){
        List<Map<String, Object>> linkDataList = linkRepository.findLinksByUserId(userNode.getUserId());

        return linkDataList.stream()
                .map(data -> GetLinkOneResponseDTO.builder()
                        .linkId(((Link) data.get("l")).getId())
                        .sharedKeywordNum((int) data.get("sharedKeywordNum"))
                        .similarity((double) data.get("similarity"))
                        .linkedNodeIdList((List<Long>) data.get("linkedNodeIdList"))
                        .build())
                .toList();

    }
}
