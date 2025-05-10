package com.team_nebula.nebula.global.util;

import static com.team_nebula.nebula.global.util.CookieUtil.*;

import java.io.IOException;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.team_nebula.nebula.domain.user.dto.request.UserDTO;
import com.team_nebula.nebula.domain.user.entity.User;
import com.team_nebula.nebula.domain.user.repository.mysql.UserRepository;
import com.team_nebula.nebula.global.apipayload.code.status.ErrorStatus;
import com.team_nebula.nebula.global.apipayload.exception.GeneralException;
import com.team_nebula.nebula.global.constants.Constants;
import com.team_nebula.nebula.global.oauth.dto.CustomOAuth2User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTFilter extends OncePerRequestFilter {

	private final JWTUtil jwtUtil;
	private final UserRepository userRepository;

	private static final AntPathMatcher pathMatcher = new AntPathMatcher();

	public JWTFilter(JWTUtil jwtUtil, UserRepository userRepository) {
		this.jwtUtil = jwtUtil;
		this.userRepository = userRepository;
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String uri = request.getRequestURI();
		return Constants.NO_NEED_FILTER_URLS.stream()
			.anyMatch(pattern -> pathMatcher.match(pattern, uri));
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws
		ServletException, IOException {

		Map<String, String> tokens = extractTokensFromCookie(request);

		if (tokens == null || tokens.isEmpty() || tokens.get("refreshToken") == null) {
			ErrorResponseUtil.sendErrorResponse(response, ErrorStatus._UNAUTHORIZED);
			return;
		}

		if (tokens.get("accessToken") == null) {
			ErrorResponseUtil.sendErrorResponse(response, ErrorStatus._TOKEN_EXPIRED);
			return;
		}

		if (jwtUtil.isExpired(tokens.get("accessToken"))) {
			if (request.getRequestURI().equals("/api/v1/oauth/reissue")) {
				authenticateUser(tokens.get("refreshToken"));
				filterChain.doFilter(request, response);
				return;
			}

			ErrorResponseUtil.sendErrorResponse(response, ErrorStatus._TOKEN_EXPIRED);
			return;
		}
		authenticateUser(tokens.get("accessToken"));
		filterChain.doFilter(request, response);
	}

	private void authenticateUser(String token) {
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
	}
}