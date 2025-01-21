package com.team_nebula.nebula.domain.oauth.service;

import java.util.Optional;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.team_nebula.nebula.domain.user.entity.User;
import com.team_nebula.nebula.domain.user.repository.mysql.UserRepository;
import com.team_nebula.nebula.domain.oauth.dto.CustomOAuth2User;
import com.team_nebula.nebula.domain.oauth.dto.GoogleResponseDTO;
import com.team_nebula.nebula.domain.oauth.dto.KakaoResponseDTO;
import com.team_nebula.nebula.domain.oauth.dto.OAuth2Response;
import com.team_nebula.nebula.domain.user.dto.request.UserDTO;
import com.team_nebula.nebula.global.apipayload.code.status.ErrorStatus;
import com.team_nebula.nebula.global.apipayload.exception.GeneralException;
import com.team_nebula.nebula.global.util.JWTUtil;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final UserRepository userRepository;
	private final JWTUtil jwtUtil;

	public CustomOAuth2UserService(UserRepository userRepository, JWTUtil jwtUtil) {

		this.userRepository = userRepository;
		this.jwtUtil = jwtUtil;
	}

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

		OAuth2User oAuth2User = super.loadUser(userRequest);
		System.out.println(oAuth2User);

		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		OAuth2Response oAuth2Response = null;

		if (registrationId.equals("google")) {

			oAuth2Response = new GoogleResponseDTO(oAuth2User.getAttributes());
		}else if (registrationId.equals("kakao")) {

			oAuth2Response = new KakaoResponseDTO(oAuth2User.getAttributes());
		} else {

			return null;
		}

		String username = oAuth2Response.getProvider()+" "+oAuth2Response.getProviderId();

		Optional<User> existData = userRepository.findByUsername(username);

		if (existData.isEmpty()) {

			User userEntity = User.builder()
				.username(username)
				.name(oAuth2Response.getName())
				.email(oAuth2Response.getEmail())
				.role("ROLE_USER")
				.build();

			userRepository.save(userEntity);

			UserDTO userDTO = UserDTO.builder()
				.username(username)
				.name(oAuth2Response.getName())
				.role("ROLE_USER")
				.build();

			return new CustomOAuth2User(userDTO);
		}
		else {

			User user = existData.get();
			user.updateEmail(oAuth2Response.getEmail());
			user.updateName(oAuth2Response.getName());

			userRepository.save(user);

			UserDTO userDTO = UserDTO.builder()
				.username(user.getUsername())
				.name(oAuth2Response.getName())
				.role(user.getRole())
				.build();

			return new CustomOAuth2User(userDTO);
		}
	}

	public void reissue(HttpServletRequest request, HttpServletResponse response) {

		String refreshToken = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("refreshToken")) {
					refreshToken = cookie.getValue();
					break;
				}
			}
		}

		if (refreshToken == null) {
			throw new GeneralException(ErrorStatus._REFRESH_TOKEN_INVALID);
		}

		try {
			jwtUtil.isExpired(refreshToken);
		} catch (ExpiredJwtException e) {
			throw new GeneralException(ErrorStatus._REFRESH_TOKEN_EXPIRED);
		}

		String username = jwtUtil.getUsername(refreshToken);
		String role = jwtUtil.getRole(refreshToken);

		String accessToken = jwtUtil.createJwt(username, role, 60 * 60L);

		Cookie accessTokenCookie = createCookie("Authorization", accessToken);
		response.addCookie(accessTokenCookie);
	}

	private Cookie createCookie(String name, String value) {
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(60*60*60);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		// cookie.setSecure(true);
		return cookie;
	}
}