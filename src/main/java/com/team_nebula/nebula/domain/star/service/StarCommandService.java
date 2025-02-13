package com.team_nebula.nebula.domain.star.service;

import com.team_nebula.nebula.domain.star.dto.request.CreateStarFileDTO;
import com.team_nebula.nebula.domain.star.dto.request.UpdateStarOneRequestDTO;
import com.team_nebula.nebula.domain.star.dto.response.CreateStarResponseDTO;
import com.team_nebula.nebula.domain.star.dto.response.GetStarOneResponseDTO;
import com.team_nebula.nebula.domain.user.entity.User;

import java.util.UUID;

public interface StarCommandService {

    public CreateStarResponseDTO createStar(User user, CreateStarFileDTO requestDTO);

    public GetStarOneResponseDTO updateStar(UUID starId, UpdateStarOneRequestDTO requestDTO);
}
