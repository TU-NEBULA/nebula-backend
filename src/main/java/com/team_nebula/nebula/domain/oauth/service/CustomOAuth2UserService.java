package com.team_nebula.nebula.domain.oauth.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.team_nebula.nebula.domain.User.entity.User;
import com.team_nebula.nebula.domain.User.repository.UserRepository;
import com.team_nebula.nebula.domain.oauth.dto.CustomOAuth2User;
import com.team_nebula.nebula.domain.oauth.dto.GoogleResponse;
import com.team_nebula.nebula.domain.oauth.dto.KakaoResponse;
import com.team_nebula.nebula.domain.oauth.dto.OAuth2Response;
import com.team_nebula.nebula.domain.oauth.dto.UserDTO;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final UserRepository userRepository;

	public CustomOAuth2UserService(UserRepository userRepository) {

		this.userRepository = userRepository;
	}

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

		OAuth2User oAuth2User = super.loadUser(userRequest);
		System.out.println(oAuth2User);

		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		OAuth2Response oAuth2Response = null;
		if (registrationId.equals("google")) {

			oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
		}else if (registrationId.equals("kakao")) {

			oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
		} else {

			return null;
		}
		String username = oAuth2Response.getProvider()+" "+oAuth2Response.getProviderId();
		User existData = userRepository.findByUsername(username)
			.orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

		if (existData == null) {

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

			existData.updateEmail(oAuth2Response.getEmail());
			existData.updateName(oAuth2Response.getName());

			userRepository.save(existData);

			UserDTO userDTO = UserDTO.builder()
				.username(existData.getUsername())
				.name(oAuth2Response.getName())
				.role(existData.getRole())
				.build();

			return new CustomOAuth2User(userDTO);
		}
	}
}