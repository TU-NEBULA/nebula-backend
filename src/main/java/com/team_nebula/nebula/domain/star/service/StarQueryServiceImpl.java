package com.team_nebula.nebula.domain.star.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.team_nebula.nebula.domain.category.repository.CategoryRepository;
import com.team_nebula.nebula.domain.keyword.repository.KeywordRepository;
import com.team_nebula.nebula.domain.link.service.LinkQueryService;
import com.team_nebula.nebula.domain.star.dto.request.CreateStarFileDTO;
import com.team_nebula.nebula.domain.star.dto.request.CreateStarRequestDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetLinkOneResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetStarListResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetStarOneResponseDTO;
import com.team_nebula.nebula.domain.star.entity.Star;
import com.team_nebula.nebula.domain.star.repository.StarRepository;
import com.team_nebula.nebula.domain.user.entity.UserNode;
import com.team_nebula.nebula.domain.user.repository.neo4j.UserNodeRepository;
import com.team_nebula.nebula.global.apipayload.code.status.ErrorStatus;
import com.team_nebula.nebula.global.apipayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StarQueryServiceImpl implements StarQueryService {

    private final StarRepository starRepository;
    private final UserNodeRepository userNodeRepository;
    private final LinkQueryService linkQueryService;

    @Override
    public CreateStarFileDTO starDataParsing(MultipartFile thumbnailImage, MultipartFile htmlFile, String starJsonData){

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // starJsonData 파싱
        CreateStarRequestDTO request;
        try {
            request = objectMapper.readValue(starJsonData, CreateStarRequestDTO.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid JSON format for starJsonData.");
        }

        CreateStarFileDTO requestDTO = new CreateStarFileDTO();
        requestDTO.setThumbnailImage(thumbnailImage);
        requestDTO.setHtmlFile(htmlFile);
        requestDTO.setStarRequestDTO(request);

        return requestDTO;
    }

    @Override
    public GetStarListResponseDTO getStarList(Long userId){

        // 유저 인증
        UserNode userNode = userNodeRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));

        // 스타 전체 조회
        List<GetStarOneResponseDTO> stars = getAllStar(userNode);

        // 링크 전체 조회
        List<GetLinkOneResponseDTO> links = linkQueryService.getAllLink(userNode);

        return GetStarListResponseDTO.builder()
                .totalStarCnt(stars.size())
                .totalLinkCnt(links.size())
                .starListDto(stars)
                .linkListDto(links)
                .build();
    }

    @Override
    public List<GetStarOneResponseDTO> getAllStar(UserNode userNode){
        List<Map<String, Object>> starDataList = starRepository.findStarsByUserId(userNode.getUserId());

        return starDataList.stream()
                .map(data -> GetStarOneResponseDTO.builder()
                        .starId(((Star) data.get("s")).getId())
                        .categoryName((String) data.get("categoryName"))
                        .title(((Star) data.get("s")).getTitle())
                        .siteUrl(((Star) data.get("s")).getSiteUrl())
                        .thumbnailUrl(((Star) data.get("s")).getThumbnailUrl())
                        .summaryAI(((Star) data.get("s")).getSummaryAI())
                        .userMemo(((Star) data.get("s")).getUserMemo())
                        .views(((Star) data.get("s")).getViews())
                        .keywordList((List<String>) data.get("keywordList"))
                        .build())
                .toList();
    }

}
