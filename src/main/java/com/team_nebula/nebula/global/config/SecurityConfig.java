package com.team_nebula.nebula.global.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.team_nebula.nebula.domain.user.repository.mysql.UserRepository;
import com.team_nebula.nebula.global.constants.Constants;
import com.team_nebula.nebula.global.oauth.handler.CustomFailureHandler;
import com.team_nebula.nebula.global.oauth.handler.CustomSuccessHandler;
import com.team_nebula.nebula.global.oauth.service.CustomOAuth2UserService;
import com.team_nebula.nebula.global.util.JWTFilter;
import com.team_nebula.nebula.global.util.JWTUtil;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final CustomOAuth2UserService customOAuth2UserService;
	private final CustomSuccessHandler customSuccessHandler;
	private final CustomFailureHandler customFailureHandler;
	private final JWTUtil jwtUtil;
	private final UserRepository userRepository;

	public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, CustomSuccessHandler customSuccessHandler,
		CustomFailureHandler customFailureHandler,
		JWTUtil jwtUtil, UserRepository userRepository) {

		this.customOAuth2UserService = customOAuth2UserService;
		this.customSuccessHandler = customSuccessHandler;
		this.customFailureHandler = customFailureHandler;
		this.jwtUtil = jwtUtil;
		this.userRepository = userRepository;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http
			.cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

				@Override
				public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

					CorsConfiguration configuration = new CorsConfiguration();

					configuration.setAllowCredentials(true);
					configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
					configuration.setAllowedHeaders(Collections.singletonList("*"));
					configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
					configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization"));
					configuration.setMaxAge(3600L);

					return configuration;
				}
			}));

		//csrf disable
		http
			.csrf((auth) -> auth.disable());

		//From 로그인 방식 disable
		http
			.formLogin((auth) -> auth.disable());

		//HTTP Basic 인증 방식 disable
		http
			.httpBasic((auth) -> auth.disable());

		//JWTFilter 추가
		http
			.addFilterBefore(new JWTFilter(jwtUtil, userRepository), UsernamePasswordAuthenticationFilter.class);

		//oauth2
		http
			.oauth2Login((oauth2) -> oauth2
				.userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
					.userService(customOAuth2UserService))
				.successHandler(customSuccessHandler)
				.failureHandler(customFailureHandler)
			);

		//경로별 인가 작업
		http
			.authorizeHttpRequests(registry ->
				registry
					.requestMatchers(Constants.NO_NEED_FILTER_URLS.toArray(String[]::new)).permitAll()
					.anyRequest().authenticated()
			);

		//세션 설정 : STATELESS
		http
			.sessionManagement((session) -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http
			.headers(headers -> headers
				.frameOptions(frameOptions -> frameOptions.disable()));

		return http.build();
	}
}