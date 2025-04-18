package com.team_nebula.nebula.global.oauth.api;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.team_nebula.nebula.global.annotation.AuthUser;
import com.team_nebula.nebula.global.apipayload.ApiResponse;
import com.team_nebula.nebula.global.oauth.service.CustomOAuth2UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "[인증]")
@RequestMapping("/api/v1/oauth")
public class OAuthController {

	private final CustomOAuth2UserService customOAuth2UserService;

	@GetMapping("/{provider}")
	public void redirectOAuth2(
		@PathVariable String provider,
		@RequestParam(required = false, defaultValue = "web") String redirectType,
		HttpServletRequest request,
		HttpServletResponse response) throws IOException {

		request.getSession().setAttribute("redirectType", redirectType);

		response.sendRedirect("/oauth2/authorization/" + provider);
	}

	@PostMapping("/reissue")
	public ApiResponse<?> reissue(HttpServletResponse response,
		@AuthUser Long userId) {
		customOAuth2UserService.reissue(userId, response);
		return ApiResponse.onSuccess("토큰 재발급 완료");
	}

	@PostMapping("/logout")
	public ApiResponse<?> logout(HttpServletResponse response) {
		customOAuth2UserService.logout(response);
		return ApiResponse.onSuccess("로그아웃 완료");
	}

	/*
	 * 임시 토큰 인증용 API
	 */
	@PostMapping("/generate")
	public ApiResponse<?> generate() {
		return ApiResponse.onSuccess(customOAuth2UserService.generate());
	}
}