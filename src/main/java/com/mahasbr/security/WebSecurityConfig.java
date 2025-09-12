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
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mahasbr.filter.AuthEntryPointJwt;
import com.mahasbr.filter.AuthTokenFilter;
import com.mahasbr.filter.XssFilter;
import com.mahasbr.repository.PermissionRepository;
import com.mahasbr.service.RefreshTokenService;
import com.mahasbr.service.UserDetailsServiceImpl;

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

	// @Autowired
	// private CustomAuthenticationFailureHandler authenticationFailureHandler;

//	@Bean
//	public ObjectMapper objectMapper() {
//		ObjectMapper objectMapper = new ObjectMapper();
//		objectMapper.registerModule(new JavaTimeModule());
//		return objectMapper;
//		// return new ObjectMapper();
//	}
	
//	@Bean
//    public ObjectMapper objectMapper() {
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.registerModule(new JavaTimeModule());
//        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        return mapper;
//    }

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
		return new HttpStatusReturningLogoutSuccessHandler();
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
						.requestMatchers("/common/api**").permitAll()
						.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
						.requestMatchers(" /common/department**").permitAll().requestMatchers("/admin/**").permitAll()
						.requestMatchers("/moderator/**").hasRole("MODERATOR").requestMatchers("/developer/**")
						.hasRole("DEVELOPER").requestMatchers("/user/**").permitAll().anyRequest().authenticated())
				.logout(logout -> logout.logoutUrl("/api/auth/logout").permitAll()
						.logoutSuccessHandler(logoutSuccessHandler()));

		http.authenticationProvider(authenticationProvider());
		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
		http.addFilterBefore(xssFilter(), UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	public AuthenticationFailureHandler authenticationFailureHandler() {
		return new CustomAuthenticationFailureHandler(mapper);
	}

	@Bean
	public AuthenticationSuccessHandler myAuthenticationSuccessHandler() {
		return new CustomSimpleUrlAuthenticationSuccessHandler();
	}

//	@Bean
//	public LogoutSuccessHandler logoutSuccessHandler() {
//		return (request, response, authentication) -> {
//			if (authentication != null) {
//				// revoke refresh token for the logged-in user
//				refreshTokenService.revoke(authentication.getName());
//			}
//
//			// clear refresh token cookie
//			ResponseCookie cookie = ResponseCookie.from("refresh", "").httpOnly(true).secure(true).sameSite("Strict")
//					.path("/").maxAge(0).build();
//
//			response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
//			response.setStatus(HttpServletResponse.SC_OK);
//			response.getWriter().write("{\"message\": \"Logout successful\"}");
//		};
//	}

}