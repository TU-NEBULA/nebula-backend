package com.team_nebula.nebula.global.constants;

import java.util.List;

public final class Constants {

	private Constants() {}

	public static List<String> NO_NEED_FILTER_URLS = List.of(
		"/swagger-ui.html/**",
		"/v3/api-docs/**",
		"/swagger-ui/**",
		"/h2-console/**",
		"/api/v1/oauth/generate",
		"/api/v1/oauth/kakao",
		"/api/v1/oauth/google",
		"/api/v1/terms",
		"/api/v1/keywords/top10-used"
	);
}