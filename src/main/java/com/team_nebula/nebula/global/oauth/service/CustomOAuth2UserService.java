package com.team_nebula.nebula.global.oauth.service;

import java.util.Optional;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.team_nebula.nebula.global.oauth.dto.GoogleResponseDTO;
import com.team_nebula.nebula.global.oauth.dto.KakaoResponseDTO;
import com.team_nebula.nebula.global.oauth.dto.OAuth2Response;
import com.team_nebula.nebula.global.oauth.dto.TokenResponseDTO;
import com.team_nebula.nebula.domain.user.entity.User;
import com.team_nebula.nebula.domain.user.repository.mysql.UserRepository;
import com.team_nebula.nebula.global.oauth.dto.CustomOAuth2User;
import com.team_nebula.nebula.domain.user.dto.request.UserDTO;
import com.team_nebula.nebula.global.apipayload.code.status.ErrorStatus;
import com.team_nebula.nebula.global.apipayload.exception.GeneralException;
import com.team_nebula.nebula.global.util.JWTUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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

	public TokenResponseDTO reissue(Long userId, String refreshToken) {

		log.info(userId + " " + refreshToken);
		User user = userRepository.findByIdAndRefreshToken(userId, refreshToken)
			.orElseThrow(() -> new GeneralException(ErrorStatus._USER_NOT_FOUND));

		String username = jwtUtil.getUsername(user.getUsername());
		String role = jwtUtil.getRole(user.getRole());

		String authorization = jwtUtil.createJwt(username, role, 60 * 60 * 24L);

		return TokenResponseDTO.builder()
			.authorization(authorization)
			.refreshToken(refreshToken)
			.build();
	}
}