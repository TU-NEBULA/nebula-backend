package com.team_nebula.nebula.global.interceptor.pre;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import com.team_nebula.nebula.global.oauth.dto.CustomOAuth2User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
		Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		CustomOAuth2User customOAuth2User = (CustomOAuth2User)authentication.getPrincipal();

		request.setAttribute("USER_ID", customOAuth2User.getId());

		return HandlerInterceptor.super.preHandle(request, response, handler);
	}
}
