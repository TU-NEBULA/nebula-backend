package com.team_nebula.nebula.global.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.team_nebula.nebula.global.oauth.dto.TokenResponseDTO;

import io.jsonwebtoken.Jwts;

@Component
public class JWTUtil {

	private SecretKey secretKey;
	private final long accessExpiration;
	private final long refreshExpiration;

	public JWTUtil(
		@Value("${spring.jwt.secret}") String secret,
		@Value("${spring.jwt.access-token-expiration}") long accessExpiration,
		@Value("${spring.jwt.refresh-token-expiration}") long refreshExpiration
	) {
		this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
			Jwts.SIG.HS256.key().build().getAlgorithm());
		this.accessExpiration = accessExpiration;
		this.refreshExpiration = refreshExpiration;
	}

	public String getUsername(String token) {

		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.get("username", String.class);
	}

	public String getRole(String token) {

		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.get("role", String.class);
	}

	public Date getExpiration(String token) {
		return Jwts.parser()
			.setSigningKey(secretKey)
			.build()
			.parseClaimsJws(token)
			.getBody()
			.getExpiration();
	}

	public Boolean isExpired(String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.getExpiration()
			.before(new Date());
	}

	public String createJwt(String username, String role, String tokenType, Long expiredMs) {

		return Jwts.builder()
			.claim("username", username)
			.claim("role", role)
			.claim("tokenType", tokenType)
			.issuedAt(new Date(System.currentTimeMillis()))
			.expiration(new Date(System.currentTimeMillis() + expiredMs * 1000))
			.signWith(secretKey)
			.compact();
	}

	public TokenResponseDTO generateTokens(String username) {
		return TokenResponseDTO.of(
			createJwt(username, "ROLE_USER", "access", accessExpiration),
			createJwt(username, "ROLE_USER", "refresh", refreshExpiration)
		);
	}
}