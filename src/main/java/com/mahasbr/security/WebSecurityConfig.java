package com.mahasbr.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahasbr.filter.AuthEntryPointJwt;
import com.mahasbr.filter.AuthTokenFilter;
import com.mahasbr.filter.CorrelationIdFilter;
import com.mahasbr.filter.XssFilter;
import com.mahasbr.repository.PermissionRepository;
import com.mahasbr.service.RefreshTokenService;
import com.mahasbr.service.UserDetailsServiceImpl;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig { // extends WebSecurityConfigurerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);
	@Autowired
	UserDetailsServiceImpl userDetailsService;

	@Autowired
	private AuthEntryPointJwt unauthorizedHandler;

	@Autowired
	private CustomLogoutSuccessHandler logoutSuccessHandler;

	@Autowired
	private PermissionRepository permissionRepository;

	@Autowired
	private RefreshTokenService refreshTokenService;

	@Autowired
	private ObjectMapper mapper;


	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public XssFilter xssFilter() {
		return new XssFilter();
	}


	@Bean
	public LogoutSuccessHandler logoutSuccessHandler() {
		return (request, response, authentication) -> {
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");

			String message;
			if (authentication == null || !authentication.isAuthenticated()
					|| "anonymousUser".equals(authentication.getPrincipal())) {
				// No active session/user found
				message = "{\"message\": \"No active session found\"}";
				System.out.println(message);
			} else {
				// Successful logout
				message = "{\"message\": \"User logged out successfully\"}";
				System.out.println(message);
			}

			response.getWriter().write(message);
			response.getWriter().flush();
		};
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		logger.info("Configuring HTTP Security...");
		http.csrf(csrf -> csrf.disable()).cors(Customizer.withDefaults())
				.exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				// .failureHandler(authenticationFailureHandler)
				.authorizeHttpRequests(auth -> auth.requestMatchers("/api/auth/signin", "/citizenSearch/**").permitAll()
						.requestMatchers("/api/auth/signup").permitAll().requestMatchers("/api/test/**").permitAll()
						.requestMatchers("/common/api**").permitAll().requestMatchers("/api/auth/progress/**")
						.permitAll().requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
						.requestMatchers(" /common/department**").permitAll().requestMatchers("/admin/**").permitAll()
						.requestMatchers("/moderator/**").hasRole("MODERATOR").requestMatchers("/developer/**")
						.hasRole("DEVELOPER").requestMatchers("/user/**").permitAll().anyRequest().authenticated());

		http.authenticationProvider(authenticationProvider());	
		http.addFilterBefore(correlationIdFilter(),UsernamePasswordAuthenticationFilter.class);
		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);		
		http.addFilterBefore(xssFilter(), UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}

	@Bean
	public AuthenticationFailureHandler authenticationFailureHandler() {
		return new CustomAuthenticationFailureHandler(mapper);
	}
	
	@Bean
	public CorrelationIdFilter correlationIdFilter() {
	    return new CorrelationIdFilter();
	}


}