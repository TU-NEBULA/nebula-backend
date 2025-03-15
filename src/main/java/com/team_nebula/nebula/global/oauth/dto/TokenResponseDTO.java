package com.team_nebula.nebula.global.oauth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponseDTO {
	private String accessToken;
	private String refreshToken;

	public static TokenResponseDTO of(String accessToken, String refreshToken) {
		return TokenResponseDTO.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}
}
