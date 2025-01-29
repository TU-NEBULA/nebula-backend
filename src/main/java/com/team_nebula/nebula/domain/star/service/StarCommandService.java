package com.team_nebula.nebula.domain.star.service;

import com.team_nebula.nebula.domain.star.dto.request.CreateStarFileDTO;
import com.team_nebula.nebula.domain.star.dto.response.CreateStarResponseDTO;

public interface StarCommandService {

    public CreateStarResponseDTO createStar(CreateStarFileDTO requestDTO);
}
