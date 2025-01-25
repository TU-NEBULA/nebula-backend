package com.team_nebula.nebula.global.util;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.team_nebula.nebula.domain.user.dto.request.UserDTO;
import com.team_nebula.nebula.global.oauth.dto.CustomOAuth2User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTFilter extends OncePerRequestFilter {

	private final JWTUtil jwtUtil;

	public JWTFilter(JWTUtil jwtUtil) {

		this.jwtUtil = jwtUtil;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws
		ServletException, IOException {

		String authorizationHeader = request.getHeader("Authorization");

		if (!jwtUtil.validateAuthorizationHeader(authorizationHeader)) {
			System.out.println("Authorization header is missing or invalid");
			filterChain.doFilter(request, response);
			return;
		}

		// Bearer 토큰 추출
		String token = authorizationHeader.substring(7); // "Bearer " 이후의 토큰 값

		// 토큰 검증
		if (jwtUtil.isExpired(token)) {
			System.out.println("Token expired");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("Token expired");
			return;
		}

		//토큰에서 username과 role 획득
		String username = jwtUtil.getUsername(token);
		String role = jwtUtil.getRole(token);

		UserDTO userDTO = UserDTO.builder()
			.username(username)
			.role(role)
			.refreshToken(token)
			.build();

		//UserDetails에 회원 정보 객체 담기
		CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);

		//스프링 시큐리티 인증 토큰 생성
		Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null,
			customOAuth2User.getAuthorities());

		//세션에 사용자 등록
		SecurityContextHolder.getContext().setAuthentication(authToken);

		filterChain.doFilter(request, response);
	}
}