package com.team_nebula.nebula.domain.star.service;

import com.team_nebula.nebula.domain.star.dto.request.CreateStarFileDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetStarOneResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetStarListResponseDTO;
import com.team_nebula.nebula.domain.user.entity.UserNode;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StarQueryService {

    public CreateStarFileDTO starDataParsing(MultipartFile thumbnailImage, MultipartFile htmlFile, String starJsonData);

    public GetStarListResponseDTO getStarList(Long userId);

    public List<GetStarOneResponseDTO> getAllStar(UserNode userNode);
}
