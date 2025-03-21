package com.team_nebula.nebula.domain.star.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team_nebula.nebula.domain.link.service.LinkQueryService;
import com.team_nebula.nebula.domain.star.converter.StarConverter;
import com.team_nebula.nebula.domain.star.dto.response.GetLinkOneResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetSearchedStarListResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetSearchedStarOneResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetStarListResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetStarOneResponseDTO;
import com.team_nebula.nebula.domain.star.repository.StarRepository;
import com.team_nebula.nebula.domain.user.repository.neo4j.UserNodeRepository;
import com.team_nebula.nebula.global.apipayload.code.status.ErrorStatus;
import com.team_nebula.nebula.global.apipayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StarQueryServiceImpl implements StarQueryService {

    private final StarRepository starRepository;
    private final UserNodeRepository userNodeRepository;
    private final LinkQueryService linkQueryService;

    // 스타 + 링크 전체 조회
    @Override
    public GetStarListResponseDTO getStarList(Long userId){

        // 스타 전체 조회
        List<GetStarOneResponseDTO> stars = findAllStar(userId);

        // 링크 전체 조회
        List<GetLinkOneResponseDTO> links = linkQueryService.getAllLink(userId);

        return GetStarListResponseDTO.builder()
                .type("ALL")
                .totalStarCnt(stars.size())
                .totalLinkCnt(links.size())
                .starListDto(stars)
                .linkListDto(links)
                .build();
    }

    // 스타 노드 전체 조회
    @Override
    public List<GetStarOneResponseDTO> findAllStar(Long userId) {

        List<GetStarOneResponseDTO> starDataList = starRepository.findStarsByUserId(userId);

        return starDataList.stream()
                .map(StarConverter::convertToStarOneDto)
                .toList();
    }

    // 단일 스타 조회
    @Override
    public GetStarOneResponseDTO getStarOne(UUID starId) {
        starRepository.incrementViews(starId);
        GetStarOneResponseDTO data = starRepository.findStarDetailById(starId);
        if (data == null) {
            starRepository.reduceViews(starId);
            throw new GeneralException(ErrorStatus._STAR_NOT_FOUND);
        }

        return StarConverter.convertToStarOneDto(data);
    }

    // 카테고리별 스타 조회
    @Override
    public GetStarListResponseDTO getStarListInCategory(Long userId, UUID categoryId){

        List<GetStarOneResponseDTO> starsInCategory = findStarInCategory(userId, categoryId);
        List<GetLinkOneResponseDTO> linksInCategory = linkQueryService.getLinkInCategory(userId, categoryId);

        return GetStarListResponseDTO.builder()
                .type("Category")
                .totalStarCnt(starsInCategory.size())
                .totalLinkCnt(linksInCategory.size())
                .starListDto(starsInCategory)
                .linkListDto(linksInCategory)
                .build();
    }

    @Override
    public List<GetStarOneResponseDTO> findStarInCategory(Long userId, UUID categoryId) {
        List<GetStarOneResponseDTO> starDataList = starRepository.findStarsInCategory(userId, categoryId);

        return starDataList.stream()
                .map(StarConverter::convertToStarOneDto)
                .toList();
    }

    @Override
    public GetStarListResponseDTO getStarListInKeyword(Long userId, String keywordId){

        List<GetStarOneResponseDTO> starsInCategory = findStarInKeyword(userId, keywordId);
        List<GetLinkOneResponseDTO> linksInCategory = linkQueryService.getLinkInKeyword(userId, keywordId);

        return GetStarListResponseDTO.builder()
                .type("Keyword")
                .totalStarCnt(starsInCategory.size())
                .totalLinkCnt(linksInCategory.size())
                .starListDto(starsInCategory)
                .linkListDto(linksInCategory)
                .build();
    }

    @Override
    public List<GetStarOneResponseDTO> findStarInKeyword(Long userId, String keywordId) {
        List<GetStarOneResponseDTO> starDataList = starRepository.findStarsInKeyword(userId, keywordId);

        return starDataList.stream()
                .map(StarConverter::convertToStarOneDto)
                .toList();
    }

    @Override
    public GetSearchedStarListResponseDTO searchStars(Long userId, String title) {
        List<GetSearchedStarOneResponseDTO> queryResult = starRepository.searchStars(userId, title);
        return StarConverter.convertToStarListDto(queryResult);
    }


}
