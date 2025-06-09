package com.team_nebula.nebula.global.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

	public static Cookie createCookie(String name, String value, long expiration) {
		Cookie cookie = new Cookie(name, value);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setMaxAge((int)getTokenMaxAgeInSeconds(expiration));
		return cookie;
	}

	public static long getTokenMaxAgeInSeconds(long expiration) {
		long now = System.currentTimeMillis();
		return (expiration - now) / 1000;
	}

	public static void deleteCookie(String name, HttpServletResponse response) {
		Cookie cookie = new Cookie(name, null);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}

	public static Map<String, String> extractTokensFromCookie(HttpServletRequest request) {
		Map<String, String> tokens = new HashMap<>();

		Cookie[] cookies = request.getCookies();
		if (cookies == null)
			return Collections.emptyMap();

		for (Cookie cookie : cookies) {
			if ("accessToken".equals(cookie.getName())) {
				tokens.put("accessToken", cookie.getValue());
			} else if ("refreshToken".equals(cookie.getName())) {
				tokens.put("refreshToken", cookie.getValue());
			}
		}
		return tokens;
	}
}