package com.team_nebula.nebula.domain.oauth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class UserDTO {

	private String role;
	private String name;
	private String username;
}