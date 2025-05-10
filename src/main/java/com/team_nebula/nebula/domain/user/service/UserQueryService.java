package com.team_nebula.nebula.domain.user.service;

import com.team_nebula.nebula.domain.user.dto.response.UserResponseDTO;
import com.team_nebula.nebula.domain.user.entity.User;

public interface UserQueryService {
	UserResponseDTO getUser(Long userId);

	User getUserEntity(Long userId);
}
