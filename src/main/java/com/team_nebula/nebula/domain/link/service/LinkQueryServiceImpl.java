package com.team_nebula.nebula.domain.link.service;

import com.team_nebula.nebula.domain.link.converter.LinkConverter;
import com.team_nebula.nebula.domain.link.entity.Link;
import com.team_nebula.nebula.domain.link.repository.LinkRepository;
import com.team_nebula.nebula.domain.star.dto.response.GetLinkOneResponseDTO;
import com.team_nebula.nebula.domain.user.entity.UserNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LinkQueryServiceImpl implements LinkQueryService {

    private final LinkRepository linkRepository;

    // 링크 노드 전체 조회
    @Override
    public List<GetLinkOneResponseDTO> getAllLink(Long userId){
        List<GetLinkOneResponseDTO> linkDataList = linkRepository.findLinksByUserId(userId);

        return linkDataList.stream()
                .map(LinkConverter::convertToLinkOneDto)
                .toList();
    }

    // 카테고리별 링크 노드 조회
    @Override
    public List<GetLinkOneResponseDTO> getLinkInCategory(Long userId, UUID categoryId){
        List<GetLinkOneResponseDTO> linkDataList = linkRepository.findLinkInCategory(userId, categoryId);

        return linkDataList.stream()
                .map(LinkConverter::convertToLinkOneDto)
                .toList();
    }

    // 키워드별 링크 노드 조회
    @Override
    public List<GetLinkOneResponseDTO> getLinkInKeyword(Long userId, String keywordId){
        List<GetLinkOneResponseDTO> linkDataList = linkRepository.findLinkInKeyword(userId, keywordId);

        return linkDataList.stream()
                .map(LinkConverter::convertToLinkOneDto)
                .toList();
    }


}
