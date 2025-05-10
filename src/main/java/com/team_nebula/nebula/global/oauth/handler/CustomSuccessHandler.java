package com.team_nebula.nebula.global.oauth.handler;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.team_nebula.nebula.domain.term.entity.UserTerm;
import com.team_nebula.nebula.domain.term.repository.UserTermRepository;
import com.team_nebula.nebula.domain.user.entity.User;
import com.team_nebula.nebula.domain.user.repository.mysql.UserRepository;
import com.team_nebula.nebula.global.apipayload.code.status.ErrorStatus;
import com.team_nebula.nebula.global.apipayload.exception.GeneralException;
import com.team_nebula.nebula.global.oauth.dto.CustomOAuth2User;
import com.team_nebula.nebula.global.oauth.dto.TokenResponseDTO;
import com.team_nebula.nebula.global.util.CookieUtil;
import com.team_nebula.nebula.global.util.JWTUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Value("${app.redirect-url}")
	private String webRedirectUrl;

	@Value("${app.extension-redirect-url}")
	private String extensionRedirectUrl;

	private final UserRepository userRepository;
	private final UserTermRepository userTermRepository;
	private final JWTUtil jwtUtil;

	public CustomSuccessHandler(UserRepository userRepository, UserTermRepository userTermRepository, JWTUtil jwtUtil) {
		this.userRepository = userRepository;
		this.userTermRepository = userTermRepository;
		this.jwtUtil = jwtUtil;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		CustomOAuth2User customUserDetails = (CustomOAuth2User)authentication.getPrincipal();
		String username = customUserDetails.getUsername();
		User user = findUserByUsername(username);

		TokenResponseDTO tokenResponseDTO = jwtUtil.generateTokens(username);
		updateRefreshToken(user, tokenResponseDTO.getRefreshToken());

		HttpSession session = request.getSession(false);
		String redirectType = (String)session.getAttribute("redirectType");
		String redirectUrl = "";

		if (redirectType.equals("extension")) {
			redirectUrl = generateExtensionToken(tokenResponseDTO);
		} else {
			redirectUrl = generateWebToken(response, tokenResponseDTO);
		}

		boolean isAgreed = isAgreedTerms(user);
		redirectUrl = String.format("%s?isAgreed=%s", redirectUrl, isAgreed);
		response.sendRedirect(redirectUrl);
	}

	private User findUserByUsername(String username) {
		return userRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));
	}

	private void updateRefreshToken(User user, String refreshToken) {
		user.updateRefreshToken(refreshToken);
		userRepository.save(user);
	}

	private String generateWebToken(HttpServletResponse response, TokenResponseDTO tokenResponseDTO) {
		long refreshTokenExpirationTime = jwtUtil.getExpiration(tokenResponseDTO.getRefreshToken()).getTime();

		response.addCookie(
			CookieUtil.createCookie("accessToken", tokenResponseDTO.getAccessToken(), refreshTokenExpirationTime));
		response.addCookie(
			CookieUtil.createCookie("refreshToken", tokenResponseDTO.getRefreshToken(), refreshTokenExpirationTime));

		return webRedirectUrl;
	}

	private String generateExtensionToken(TokenResponseDTO tokenResponseDTO) {
		String redirectUrl = extensionRedirectUrl;

		redirectUrl = String.format("%s?accessToken=%s&refreshToken=%s", redirectUrl,
			tokenResponseDTO.getAccessToken(), tokenResponseDTO.getRefreshToken());

		return redirectUrl;
	}

	private boolean isAgreedTerms(User user) {
		List<UserTerm> userTerms = userTermRepository.findByUser(user);
		return !userTerms.isEmpty();
	}
}