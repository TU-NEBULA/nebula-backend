package com.team_nebula.nebula.global.config;

import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.team_nebula.nebula.global.annotation.AuthUser;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

	@Value("${app.local-url}")
	private String localUrl;

	@Value("${app.ec2-url}")
	private String ec2Url;

	static {
		SpringDocUtils.getConfig().addAnnotationsToIgnore(AuthUser.class);
	}

	@Bean
	public OpenAPI openAPI() {

		SecurityScheme securityScheme = new SecurityScheme()
			.type(SecurityScheme.Type.HTTP)
			.scheme("bearer")
			.bearerFormat("JWT");

		SecurityRequirement securityRequirement = new SecurityRequirement()
			.addList("bearerAuth");

		Server localServer = new Server()
			.url(localUrl)
			.description("Local development server");

		Server ec2Server = new Server()
			.url(ec2Url)
			.description("EC2 development server");

		return new OpenAPI()
			.info(new Info().title("Nebula API").version("1.0"))
			.addServersItem(localServer)
			.addServersItem(ec2Server)
			.addSecurityItem(securityRequirement)
			.schemaRequirement("bearerAuth", securityScheme);
	}
}