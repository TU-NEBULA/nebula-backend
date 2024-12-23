package com.team_nebula.nebula.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {
		Info info = new Info()
			.version("v1.0")
			.title("Nebula API")
			.description("북마크 관리 서비스 API");
		return new OpenAPI()
			.info(info);
	}
}