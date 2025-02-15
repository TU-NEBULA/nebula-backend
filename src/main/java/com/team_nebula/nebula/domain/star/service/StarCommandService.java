package com.team_nebula.nebula.domain.star.service;

import com.team_nebula.nebula.domain.star.dto.request.CreateStarFileDTO;
import com.team_nebula.nebula.domain.star.dto.request.CreateStarRequestDTO;
import com.team_nebula.nebula.domain.star.dto.request.UpdateStarOneRequestDTO;
import com.team_nebula.nebula.domain.star.dto.response.CreateStarResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.DeleteStarResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetStarOneResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.PutStarResponseDTO;
import com.team_nebula.nebula.domain.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface StarCommandService {

    public CreateStarResponseDTO createFirstStar(User user, MultipartFile htmlFile, String title, String siteUrl);

//    public CreateStarResponseDTO createStar(User user, CreateStarFileDTO requestDTO);

    public PutStarResponseDTO putStar(UUID starId, CreateStarRequestDTO requestDTO);

    public GetStarOneResponseDTO updateStar(UUID starId, UpdateStarOneRequestDTO requestDTO);

    public DeleteStarResponseDTO deleteStar(UUID starId);
}
