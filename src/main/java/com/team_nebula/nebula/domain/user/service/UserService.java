package com.team_nebula.nebula.domain.user.service;

import com.team_nebula.nebula.domain.user.dto.response.UserResponseDTO;

public interface UserService {
	UserResponseDTO getUser(Long userId);
}
