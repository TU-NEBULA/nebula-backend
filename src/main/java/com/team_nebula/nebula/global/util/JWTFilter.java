package com.team_nebula.nebula.global.util;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.team_nebula.nebula.domain.user.dto.request.UserDTO;
import com.team_nebula.nebula.domain.user.entity.User;
import com.team_nebula.nebula.domain.user.repository.mysql.UserRepository;
import com.team_nebula.nebula.global.apipayload.code.status.ErrorStatus;
import com.team_nebula.nebula.global.apipayload.exception.GeneralException;
import com.team_nebula.nebula.global.oauth.dto.CustomOAuth2User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTFilter extends OncePerRequestFilter {

	private final JWTUtil jwtUtil;
	private final UserRepository userRepository;

	public JWTFilter(JWTUtil jwtUtil, UserRepository userRepository) {
		this.jwtUtil = jwtUtil;
		this.userRepository = userRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws
		ServletException, IOException {

		String authorizationHeader = request.getHeader("Authorization");

		if (!jwtUtil.validateAuthorizationHeader(authorizationHeader)) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = authorizationHeader.substring(7);

		if (jwtUtil.isExpired(token)) {
			ErrorResponseUtil.sendErrorResponse(response, ErrorStatus._BAD_REQUEST);
			return;
		}

		String tokenType = jwtUtil.getTokenType(token);

		if (request.getRequestURI().equals("/api/v1/oauth/reissue")) {
			if (!"refresh".equals(tokenType)) {
				ErrorResponseUtil.sendErrorResponse(response, ErrorStatus._TOKEN_TYPE_ERROR);
				return;
			}

		} else if (!"access".equals(tokenType)) {
			ErrorResponseUtil.sendErrorResponse(response, ErrorStatus._TOKEN_TYPE_ERROR);
			return;
		}

		String username = jwtUtil.getUsername(token);

		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));

		//토큰에서 username과 role 획득
		String role = jwtUtil.getRole(token);

		UserDTO userDTO = UserDTO.builder()
			.id(user.getId())
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