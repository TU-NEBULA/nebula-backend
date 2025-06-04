package com.team_nebula.nebula.global.config;

import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.team_nebula.nebula.global.annotation.AuthUser;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

	@Value("${app.url}")
	private String url;

	static {
		SpringDocUtils.getConfig().addAnnotationsToIgnore(AuthUser.class);
	}

	@Bean
	public OpenAPI openAPI() {
		Server server = new Server()
			.url(url)
			.description("server");

		return new OpenAPI()
			.info(new Info().title("Nebula API").version("1.0"))
			.addServersItem(server);
	}
}