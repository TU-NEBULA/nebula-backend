package com.team_nebula.nebula.domain.star.service;

import com.team_nebula.nebula.domain.star.dto.request.CreateStarFileDTO;
import com.team_nebula.nebula.domain.star.dto.response.CreateStarResponseDTO;
import com.team_nebula.nebula.domain.user.entity.User;

public interface StarCommandService {

    public CreateStarResponseDTO createStar(User user, CreateStarFileDTO requestDTO);
}
