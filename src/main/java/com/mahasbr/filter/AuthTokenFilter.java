package com.mahasbr.filter;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mahasbr.service.TokenBlacklistService;
import com.mahasbr.service.UserDetailsImpl;
import com.mahasbr.service.UserDetailsServiceImpl;
import com.mahasbr.util.JwtUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthTokenFilter extends OncePerRequestFilter {
	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Autowired
	private TokenBlacklistService tokenBlacklistService;

	 private static final ThreadLocal<String> currentUsername = new ThreadLocal<>();
	    private static final ThreadLocal<Long> currentUserId = new ThreadLocal<>();

	private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

	public static final String PASSWORD_CHANGE_REQUIRED = "PASSWORD_CHANGE_REQUIRED";

	private static final Set<String> FIRST_LOGIN_ALLOWED_PATHS = Set.of(
			"/api/auth/changePassword",
			"/api/auth/logout",
			"/api/auth/me");

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		try {
			String jwt = parseJwt(request);

			if (jwt != null && jwtUtils.validateJwtToken(jwt)) {

				// Check blacklist before authenticating
				if (tokenBlacklistService.isTokenBlacklisted(jwt)) {
				    logger.warn("Rejected blacklisted token for request");
				    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				    response.setContentType("application/json");
				    response.getWriter().write("{\"message\":\"Token is blacklisted. Please login again.\"}");
				    return;
				}

				String username = jwtUtils.getUserNameFromJwtToken(jwt);

				UserDetails userDetails = userDetailsService.loadUserByUsername(username);

				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());

				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authentication);

				// A user still on their temporary password may only change it, log out or read /me.
				// The flag is read fresh from the database on every request (loadUserByUsername above),
				// so the block lifts on the very next request after the password is changed.
				if (mustChangePassword(userDetails, request)) {
					SecurityContextHolder.clearContext();
					response.setStatus(HttpServletResponse.SC_FORBIDDEN);
					response.setContentType("application/json");
					response.getWriter().write("{\"code\":\"" + PASSWORD_CHANGE_REQUIRED
							+ "\",\"message\":\"Please change your temporary password to continue.\"}");
					return;
				}
			}

		} catch (Exception e) {
			logger.error("JWT Authentication failed: {}", e.getMessage());
			writeUnauthorizedResponse(response, "Invalid or expired token");
			return;
		}

		filterChain.doFilter(request, response);
	}

	private static boolean mustChangePassword(UserDetails userDetails, HttpServletRequest request) {
		if (!(userDetails instanceof UserDetailsImpl user) || !Boolean.TRUE.equals(user.getIsFirstTimeLogin())) {
			return false;
		}
		String path = request.getServletPath();
		// /citizenSearch/** is public anyway, so a token there grants nothing extra.
		return !FIRST_LOGIN_ALLOWED_PATHS.contains(path) && !path.startsWith("/citizenSearch/");
	}

	private String parseJwt(HttpServletRequest request) {
		String headerAuth = request.getHeader("Authorization");

		if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
			return headerAuth.substring(7);
		}

		return null;
	}

	private void writeUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		response.getWriter().write("{\"message\": \"" + message + "\"}");
		response.getWriter().flush();
	}

	 // Thread-safe getters
    public static String getCurrentUsername() {
        return currentUsername.get();
    }

    public static Long getCurrentUserId() {
        return currentUserId.get();
    }

}