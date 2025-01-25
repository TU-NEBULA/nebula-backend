package com.team_nebula.nebula.global.oauth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team_nebula.nebula.domain.user.entity.User;
import com.team_nebula.nebula.global.annotation.AuthUser;
import com.team_nebula.nebula.global.apipayload.ApiResponse;
import com.team_nebula.nebula.global.apipayload.code.status.ErrorStatus;
import com.team_nebula.nebula.global.apipayload.exception.GeneralException;
import com.team_nebula.nebula.global.oauth.dto.TokenResponseDTO;
import com.team_nebula.nebula.global.oauth.service.CustomOAuth2UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/oauth")
public class OAuthController {

	private final CustomOAuth2UserService customOAuth2UserService;

	@PostMapping("/reissue")
	public ApiResponse<?> reissue(
		HttpServletRequest request,
		@AuthUser User user) {

		String refreshToken = request.getHeader("Authorization").substring(7);

		if (refreshToken.trim().isEmpty()) {
			throw new GeneralException(ErrorStatus._UNAUTHORIZED_USER);
		}

		TokenResponseDTO dto = customOAuth2UserService.reissue(user.getId(), refreshToken);
		return ApiResponse.onSuccess(dto);
	}
}