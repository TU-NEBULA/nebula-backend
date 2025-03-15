package com.team_nebula.nebula.global.oauth.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team_nebula.nebula.global.annotation.AuthUser;
import com.team_nebula.nebula.global.apipayload.ApiResponse;
import com.team_nebula.nebula.global.oauth.dto.TokenResponseDTO;
import com.team_nebula.nebula.global.oauth.service.CustomOAuth2UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "[인증]")
@RequestMapping("/api/v1/oauth")
public class OAuthController {

	private final CustomOAuth2UserService customOAuth2UserService;

	@PostMapping("/reissue")
	public ApiResponse<?> reissue(HttpServletRequest request,
		@AuthUser Long userId) {
		TokenResponseDTO dto = customOAuth2UserService.reissue(userId);
		return ApiResponse.onSuccess(dto);
	}

	/*
	 * 임시 토큰 인증용 API
	 */
	@PostMapping("/generate")
	public ApiResponse<?> generate() {
		return ApiResponse.onSuccess(customOAuth2UserService.generate());
	}
}