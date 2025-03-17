package com.team_nebula.nebula.global.oauth.handler;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.team_nebula.nebula.domain.user.entity.User;
import com.team_nebula.nebula.domain.user.repository.mysql.UserRepository;
import com.team_nebula.nebula.global.apipayload.code.status.ErrorStatus;
import com.team_nebula.nebula.global.apipayload.exception.GeneralException;
import com.team_nebula.nebula.global.oauth.dto.CustomOAuth2User;
import com.team_nebula.nebula.global.oauth.dto.TokenResponseDTO;
import com.team_nebula.nebula.global.util.JWTUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Value("${app.redirect-url}")
	private String webRedirectUrl;

	@Value("${app.extension-redirect-url}")
	private String extensionRedirectUrl;

	private final UserRepository userRepository;
	private final JWTUtil jwtUtil;

	public CustomSuccessHandler(UserRepository userRepository, JWTUtil jwtUtil) {
		this.userRepository = userRepository;
		this.jwtUtil = jwtUtil;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws
		IOException,
		ServletException {

		//OAuth2User
		CustomOAuth2User customUserDetails = (CustomOAuth2User)authentication.getPrincipal();

		String username = customUserDetails.getUsername();

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		GrantedAuthority auth = iterator.next();
		String role = auth.getAuthority();

		TokenResponseDTO tokenResponseDTO = jwtUtil.generateTokens(username);

		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));

		user.updateRefreshToken(tokenResponseDTO.getRefreshToken());
		userRepository.save(user);

		long accessTokenExpirationTime = jwtUtil.getExpiration(tokenResponseDTO.getAccessToken()).getTime();
		long refreshTokenExpirationTime = jwtUtil.getExpiration(tokenResponseDTO.getRefreshToken()).getTime();

		response.addCookie(createCookie("accessToken", tokenResponseDTO.getAccessToken(),
			accessTokenExpirationTime));

		response.addCookie(createCookie("refreshToken", tokenResponseDTO.getRefreshToken(),
			refreshTokenExpirationTime));

		HttpSession session = request.getSession(false);
		String redirectType = (String)session.getAttribute("redirectType");

		String redirectUrl = "web".equals(redirectType) ? webRedirectUrl : extensionRedirectUrl;

		redirectUrl = String.format("%s?accessToken=%s&refreshToken=%s", redirectUrl,
			tokenResponseDTO.getAccessToken(), tokenResponseDTO.getRefreshToken());

		response.sendRedirect(redirectUrl);

	}

	private Cookie createCookie(String key, String value, long expirationTime) {
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge((int)expirationTime);
		cookie.setSecure(true);
		cookie.setDomain("nebula-ai.kr");
		cookie.setPath("/");
		cookie.setHttpOnly(true);

		return cookie;
	}
}
