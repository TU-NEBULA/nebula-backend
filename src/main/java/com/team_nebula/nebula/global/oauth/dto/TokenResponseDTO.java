package com.team_nebula.nebula.global.oauth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponseDTO {
	private String authorization;
	private String refreshToken;
}
