package com.team_nebula.nebula.global.util;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.team_nebula.nebula.domain.user.entity.User;
import com.team_nebula.nebula.global.annotation.AuthUser;
import com.team_nebula.nebula.global.apipayload.code.status.ErrorStatus;
import com.team_nebula.nebula.global.apipayload.exception.GeneralException;
import com.team_nebula.nebula.global.oauth.service.CustomOAuth2UserService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthArgumentResolver implements HandlerMethodArgumentResolver {

	private final JWTUtil jwtUtil;
	private final CustomOAuth2UserService customOAuth2UserService;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterAnnotation(AuthUser.class) != null
			&& parameter.getParameterType().equals(User.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

		String authorization = webRequest.getHeader("Authorization");

		// 토큰 유효성 검증
		if (!jwtUtil.validateAuthorizationHeader(authorization)) {
			throw new GeneralException(ErrorStatus._REFRESH_TOKEN_INVALID);
		}

		// 토큰에서 사용자 추출하기
		String token = jwtUtil.extractTokenFromAuthorizationHeader(authorization);
		String username = jwtUtil.getUsername(token);

		return customOAuth2UserService.loadUserByUsername(username);
	}
}