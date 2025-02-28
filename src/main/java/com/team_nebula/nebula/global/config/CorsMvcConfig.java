package com.team_nebula.nebula.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry corsRegistry) {

		corsRegistry.addMapping("/**")
			.allowedOrigins("*")
			.allowedHeaders("*")
			.allowedMethods("GET", "POST", "PATCH", "DELETE", "OPTIONS")
			.exposedHeaders("Authorization", "Set-Cookie");
	}
}