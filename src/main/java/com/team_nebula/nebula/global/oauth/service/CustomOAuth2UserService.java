package com.team_nebula.nebula.global.oauth.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team_nebula.nebula.domain.user.dto.request.UserDTO;
import com.team_nebula.nebula.domain.user.entity.User;
import com.team_nebula.nebula.domain.user.entity.UserNode;
import com.team_nebula.nebula.domain.user.repository.mysql.UserRepository;
import com.team_nebula.nebula.domain.user.repository.neo4j.UserNodeRepository;
import com.team_nebula.nebula.global.apipayload.code.status.ErrorStatus;
import com.team_nebula.nebula.global.apipayload.exception.GeneralException;
import com.team_nebula.nebula.global.oauth.dto.CustomOAuth2User;
import com.team_nebula.nebula.global.oauth.dto.GoogleResponseDTO;
import com.team_nebula.nebula.global.oauth.dto.KakaoResponseDTO;
import com.team_nebula.nebula.global.oauth.dto.OAuth2Response;
import com.team_nebula.nebula.global.oauth.dto.TokenResponseDTO;
import com.team_nebula.nebula.global.util.CookieUtil;
import com.team_nebula.nebula.global.util.JWTUtil;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final UserRepository userRepository;
	private final UserNodeRepository userNodeRepository;
	private final JWTUtil jwtUtil;
	private final long refreshExpiration;

	public CustomOAuth2UserService(UserRepository userRepository,
		UserNodeRepository userNodeRepository, JWTUtil jwtUtil,
		@Value("${spring.jwt.refresh-token-expiration}") long refreshExpiration) {

		this.userRepository = userRepository;
		this.userNodeRepository = userNodeRepository;
		this.jwtUtil = jwtUtil;
		this.refreshExpiration = refreshExpiration;
	}

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

		OAuth2User oAuth2User = super.loadUser(userRequest);
		System.out.println(oAuth2User);

		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		OAuth2Response oAuth2Response = null;

		if (registrationId.equals("google")) {

			oAuth2Response = new GoogleResponseDTO(oAuth2User.getAttributes());
		} else if (registrationId.equals("kakao")) {

			oAuth2Response = new KakaoResponseDTO(oAuth2User.getAttributes());
		} else {

			return null;
		}

		String username = oAuth2Response.getProvider() + oAuth2Response.getProviderId();

		Optional<User> existData = userRepository.findByUsername(username);

		String refreshToken = jwtUtil.createJwt(username, "ROLE_USER", "refreshToken", refreshExpiration);

		// 초기 로그인 시
		if (existData.isEmpty()) {

			// MySQL 저장
			User userEntity = User.builder()
				.username(username)
				.name(oAuth2Response.getName())
				.email(oAuth2Response.getEmail())
				.role("ROLE_USER")
				.refreshToken(refreshToken)
				.build();

			userRepository.save(userEntity);

			// Neo4j 저장
			UserNode userNode = UserNode.builder().userId(userEntity.getId()).build();

			userNodeRepository.save(userNode);

			UserDTO userDTO = UserDTO.builder()
				.id(userEntity.getId())
				.username(username)
				.name(oAuth2Response.getName())
				.role("ROLE_USER")
				.refreshToken(refreshToken)
				.build();

			return new CustomOAuth2User(userDTO);
		} else {

			User user = existData.get();
			user.updateEmail(oAuth2Response.getEmail());
			user.updateName(oAuth2Response.getName());
			user.updateRefreshToken(refreshToken);

			userRepository.save(user);

			UserDTO userDTO = UserDTO.builder()
				.id(user.getId())
				.username(user.getUsername())
				.name(oAuth2Response.getName())
				.role(user.getRole())
				.build();

			return new CustomOAuth2User(userDTO);
		}
	}

	@Transactional
	public void reissue(Long userId, HttpServletResponse response) {

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));

		TokenResponseDTO tokenResponseDTO = jwtUtil.generateTokens(user.getUsername());
		long expiration = jwtUtil.getExpiration(tokenResponseDTO.getRefreshToken()).getTime();

		response.addCookie(CookieUtil.createCookie("accessToken", tokenResponseDTO.getAccessToken(), expiration));
	}

	public TokenResponseDTO generate() {

		User user = userRepository.findByUsername("temp").orElse(null);

		if (user == null) {
			user = User.builder()
				.username("temp")
				.name("temp")
				.email("temp")
				.role("ROLE_USER")
				.build();

			userRepository.save(user);
		}

		String accessToken = jwtUtil.createJwt(user.getUsername(), user.getRole(), "access", 60 * 60 * 24L * 30);
		String refreshToken = jwtUtil.createJwt(user.getUsername(), user.getRole(), "refresh", 60 * 60 * 24L * 30);

		return TokenResponseDTO
			.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	public void logout(HttpServletResponse response) {
		CookieUtil.deleteCookie("accessToken", response);
		CookieUtil.deleteCookie("refreshToken", response);
	}
}