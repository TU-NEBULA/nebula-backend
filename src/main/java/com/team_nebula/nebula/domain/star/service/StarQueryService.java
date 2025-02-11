package com.team_nebula.nebula.domain.star.service;

import com.team_nebula.nebula.domain.star.dto.request.CreateStarFileDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetStarOneResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetStarListResponseDTO;
import com.team_nebula.nebula.domain.user.entity.UserNode;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface StarQueryService {

    public CreateStarFileDTO starDataParsing(MultipartFile thumbnailImage, MultipartFile htmlFile, String starJsonData);

    public GetStarListResponseDTO getStarList(Long userId);

    public List<GetStarOneResponseDTO> findAllStar(Long userId);

    public GetStarOneResponseDTO getStarOne(UUID starId);

    public GetStarListResponseDTO getStarListInCategory(Long userId, UUID categoryId);

    public List<GetStarOneResponseDTO> findStarInCategory(Long userId, UUID categoryId);

    public GetStarListResponseDTO getStarListInKeyword(Long userId, String keywordId);

    public List<GetStarOneResponseDTO> findStarInKeyword(Long userId, String keywordId);

    public GetStarListResponseDTO searchStars(Long userId, String title);

}
