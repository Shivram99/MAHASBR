package com.mahasbr.util;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.mahasbr.service.UserDetailsImpl;

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

	    // refresh token validity (max 30 minutes)
	    private static final long REFRESH_TOKEN_VALIDITY = 30 * 60 * 1000; // 30 min in ms

	    private Key key() {
	        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	    }

	    // 🔹 Generate Access Token
	    public String generateJwtToken(Authentication authentication) {
	        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
	        Map<String, Object> claims = new HashMap<>();
	        claims.put("roles", userPrincipal.getAuthorities().stream()
	                .map(GrantedAuthority::getAuthority)
	                .collect(Collectors.toList()));
	        return Jwts.builder()
	        		.setClaims(claims)
	                .setSubject(userPrincipal.getUsername())
	                .setIssuedAt(new Date())
	                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
	                .signWith(key(), SignatureAlgorithm.HS256)
	                .compact();
	    }

	    // 🔹 Generate Refresh Token (valid max 30 min)
	    public String generateRefreshToken(String username) {
	    	logger.info("token refresh");
	        return Jwts.builder()
	                .setSubject(username)
	                .setIssuedAt(new Date())
	                .setExpiration(new Date((new Date()).getTime() + REFRESH_TOKEN_VALIDITY))
	                .signWith(key(), SignatureAlgorithm.HS256)
	                .compact();
	    }

	    // 🔹 Extract Username from Token
	    public String getUserNameFromJwtToken(String token) {
	        return Jwts.parserBuilder().setSigningKey(key()).build()
	                .parseClaimsJws(token)
	                .getBody().getSubject();
	    }

	    // 🔹 Validate Token
	    public boolean validateJwtToken(String authToken) {
	        try {
	            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
	            return true;
	        } catch (MalformedJwtException e) {
	            logger.error("Invalid JWT token: {}", e.getMessage());
	        } catch (ExpiredJwtException e) {
	            logger.error("JWT token is expired: {}", e.getMessage());
	        } catch (UnsupportedJwtException e) {
	            logger.error("JWT token is unsupported: {}", e.getMessage());
	        } catch (IllegalArgumentException e) {
	            logger.error("JWT claims string is empty: {}", e.getMessage());
	        }

	        return false;
	    }
}
