package com.team_nebula.nebula.global.oauth.dto;

import java.util.Map;

public class KakaoResponseDTO implements OAuth2Response {

	private final Map<String, Object> attributes;

	public KakaoResponseDTO(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String getProvider() {
		return "kakao";
	}

	@Override
	public String getProviderId() {

		return attributes.get("id").toString();
	}

	@Override
	public String getEmail() {

		Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
		return kakaoAccount != null ? kakaoAccount.get("email").toString() : null;
	}

	@Override
	public String getName() {

		Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
		if (kakaoAccount != null) {
			Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
			if (profile != null) {
				return profile.get("nickname").toString();
			}
		}
		return null;
	}
}