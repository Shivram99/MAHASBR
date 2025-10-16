package com.mahasbr.util;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.mahasbr.service.UserDetailsImpl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {

	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${com.mahait.gov.in.jwtSecret}")
	private String jwtSecret;

	@Value("${com.mahait.gov.in.jwtExpirationMs}") // e.g., 30000 (30 sec)
	private int jwtExpirationMs;

	
	private static final long REFRESH_TOKEN_VALIDITY_MS = 30 * 60 * 1000; // 30 min

	// Key for signing JWT (HS256)
	private Key getSigningKey() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	}

	// 🔹 Generate Access Token (with username, roles, userId)
	public String generateJwtToken(Authentication authentication) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		String jti = UUID.randomUUID().toString();

		Map<String, Object> claims = new HashMap<>();
		claims.put("roles", userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList()));
		claims.put("userId", userPrincipal.getId()); // add userId for audit/filter
		claims.put("jti", jti);

		return Jwts.builder().setClaims(claims).setSubject(userPrincipal.getUsername()).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
	}

	// 🔹 Generate Refresh Token (valid 30 min)
	public String generateRefreshToken(String username) {
		logger.info("Generating refresh token for {}", username);

		return Jwts.builder().setSubject(username).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY_MS))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
	}

	// 🔹 Extract Username
	public String getUserNameFromJwtToken(String token) {
		return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody().getSubject();
	}

	// 🔹 Extract User ID
	public Long getUserIdFromJwtToken(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();

		Object userIdClaim = claims.get("userId");
		if (userIdClaim instanceof Integer) {
			return ((Integer) userIdClaim).longValue();
		} else if (userIdClaim instanceof Long) {
			return (Long) userIdClaim;
		} else if (userIdClaim instanceof String) {
			return Long.parseLong((String) userIdClaim);
		}
		return null;
	}

	// 🔹 Get token expiration date
	public Date getExpirationDateFromJwtToken(String token) {
		return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody()
				.getExpiration();
	}

	// 🔹 Validate token
	public boolean validateJwtToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
			return true;
		} catch (SecurityException | MalformedJwtException e) {
			logger.error("Invalid JWT signature: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.warn("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}
		return false;
	}
	
	public String getJtiFromJwtToken(String token) {
	    try {
	        Claims claims = Jwts.parserBuilder()
	                .setSigningKey(getSigningKey())
	                .build()
	                .parseClaimsJws(token)
	                .getBody();

	        Object jti = claims.get("jti");
	        return jti != null ? jti.toString() : null;
	    } catch (Exception e) {
	        return null;
	    }
	}
}
