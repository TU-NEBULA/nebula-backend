package com.team_nebula.nebula.domain.oauth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team_nebula.nebula.domain.oauth.service.CustomOAuth2UserService;
import com.team_nebula.nebula.global.apipayload.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/oauth")
public class OAuthController {

	private final CustomOAuth2UserService customOAuth2UserService;

	@PostMapping("/reissue")
	public ApiResponse<?> reissue(HttpServletRequest request, HttpServletResponse response) {
		customOAuth2UserService.reissue(request, response);
		return ApiResponse.onSuccess("Reissue Success");
	}
}