package com.team_nebula.nebula.domain.user.converter;

import com.team_nebula.nebula.domain.user.dto.response.UserResponseDTO;
import com.team_nebula.nebula.domain.user.entity.User;

public class UserConverter {

	public static UserResponseDTO toUserResponseDTO(User user) {
		return UserResponseDTO.builder()
			.id(user.getId())
			.email(user.getEmail())
			.name(user.getName())
			.build();
	}


}
