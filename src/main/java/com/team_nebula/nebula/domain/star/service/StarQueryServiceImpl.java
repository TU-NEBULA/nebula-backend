package com.team_nebula.nebula.domain.star.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.team_nebula.nebula.domain.link.service.LinkQueryService;
import com.team_nebula.nebula.domain.star.converter.StarConverter;
import com.team_nebula.nebula.domain.star.dto.request.CreateStarFileDTO;
import com.team_nebula.nebula.domain.star.dto.request.CreateStarRequestDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetLinkOneResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetStarListResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetStarOneResponseDTO;
import com.team_nebula.nebula.domain.star.repository.StarRepository;
import com.team_nebula.nebula.domain.user.entity.UserNode;
import com.team_nebula.nebula.domain.user.repository.neo4j.UserNodeRepository;
import com.team_nebula.nebula.global.apipayload.code.status.ErrorStatus;
import com.team_nebula.nebula.global.apipayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StarQueryServiceImpl implements StarQueryService {

    private final StarRepository starRepository;
    private final UserNodeRepository userNodeRepository;
    private final LinkQueryService linkQueryService;


    // 스타 JSON 데이터 파싱
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

        return CreateStarFileDTO.builder()
                .thumbnailImage(thumbnailImage)
                .htmlFile(htmlFile)
                .starRequestDTO(request)
                .build();

    }

    // 스타 + 링크 전체 조회
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

    // 스타 노드 전체 조회
    public List<GetStarOneResponseDTO> getAllStar(UserNode userNode) {
        List<Map<String, Object>> starDataList = starRepository.findStarsByUserId(userNode.getUserId());

        return starDataList.stream()
                .map(StarConverter::convertToStarOneDto)
                .toList();
    }

    // 단일 스타 조회
    public GetStarOneResponseDTO getStarOne(Long starId) {
        Map<String, Object> data = starRepository.findStarDetailById(starId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._STAR_NOT_FOUND));

        return StarConverter.convertToStarOneDto(data);
    }

}
