package com.team_nebula.nebula.domain.user.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDTO {
	private Long id;
	private String role;
	private String name;
	private String username;
	private String refreshToken;
}