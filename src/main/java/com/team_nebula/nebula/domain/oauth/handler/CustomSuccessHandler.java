package com.team_nebula.nebula.domain.oauth.handler;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.team_nebula.nebula.domain.oauth.dto.CustomOAuth2User;
import com.team_nebula.nebula.global.util.JWTUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final JWTUtil jwtUtil;

	public CustomSuccessHandler(JWTUtil jwtUtil) {

		this.jwtUtil = jwtUtil;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws
		IOException,
		ServletException {

		//OAuth2User
		CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

		String username = customUserDetails.getUsername();

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		GrantedAuthority auth = iterator.next();
		String role = auth.getAuthority();

		String token = jwtUtil.createJwt(username, role, 60 * 60 * 24L);
		String refreshToken = jwtUtil.createJwt(username, role, 60 * 60 * 24L * 7);

		response.addCookie(createCookie("Authorization", token, 60 * 60 * 24));
		response.addCookie(createCookie("refreshToken", refreshToken, 60 * 60 * 24 * 7));
		response.sendRedirect("http://localhost:3000/");
	}

	private Cookie createCookie(String key, String value, int maxAge) {

		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(maxAge);
		//cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setHttpOnly(true);

		return cookie;
	}
}
