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
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mahasbr.filter.AuthEntryPointJwt;
import com.mahasbr.filter.AuthTokenFilter;
import com.mahasbr.filter.XssFilter;
import com.mahasbr.repository.PermissionRepository;
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

	// @Autowired
	// private CustomAuthenticationFailureHandler authenticationFailureHandler;

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		return objectMapper;
		// return new ObjectMapper();
	}

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

	/*
	 * @Bean public SimpleUrlAuthenticationSuccessHandler loginSuccessHandler() {
	 * return new SimpleUrlAuthenticationSuccessHandler(); }
	 */

	  @Bean
	  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		  logger.info("Configuring HTTP Security...");
	    http.csrf(csrf -> csrf.disable())
	    .cors(Customizer.withDefaults())
	        .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
	        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	       // .failureHandler(authenticationFailureHandler)
	        .authorizeHttpRequests(auth -> 
	          auth.requestMatchers("/api/auth/signin","/citizenSearch/**").permitAll()
	              .requestMatchers("/api/auth/signup").permitAll()
	              .requestMatchers("/api/test/**").permitAll()
	              .requestMatchers("/common/api**").permitAll()
	              .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
	              .requestMatchers(" /common/department**").permitAll()
	              .requestMatchers("/admin/**").permitAll()
	              .requestMatchers("/moderator/**").hasRole("MODERATOR")
	              .requestMatchers("/developer/**").hasRole("DEVELOPER")
	            .requestMatchers("/user/**").permitAll()
	              .anyRequest().authenticated()
	        ). logout().logoutUrl("/logout").permitAll().logoutSuccessHandler(logoutSuccessHandler);
	 
	    http.authenticationProvider(authenticationProvider());
	    http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
	   http.addFilterBefore(xssFilter(), UsernamePasswordAuthenticationFilter.class);
	    return http.build();
	  }

//	@Bean
//	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//	    logger.info("Configuring HTTP Security...");
//	    List<Permission> permissions = permissionRepository.findAll();
//	    http.csrf(csrf -> csrf.disable())
//	        .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
//	        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//	        .authorizeHttpRequests(auth -> {
//                // Public endpoints
//                auth.requestMatchers("/api/auth/signin", "/api/auth/signup").permitAll();
//                auth.requestMatchers("/common/api/**", "/common/department/**").permitAll();
//                auth.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll();
//                auth.requestMatchers("/citizenSearch/**").permitAll();
//                auth.requestMatchers("/api/test/**").permitAll();
//                auth.requestMatchers("/api/menus/**").permitAll();
//                auth.requestMatchers("/api/submenus/**").permitAll();
//                auth.requestMatchers("/developer/**").permitAll();
//
//                // Load role-based access dynamically
////                for (Permission p : permissions) {
////                    auth.requestMatchers(p.getUrlPattern()).hasRole(p.getRole().getName().toString());
////                }
//
//                // Default rule for all others
//                auth.anyRequest().authenticated();
//            })
//	        // New way to configure logout
//	        .logout(logout -> logout
//	            .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
//	            .permitAll()
//	            .logoutSuccessHandler(logoutSuccessHandler)
//	        );
//
//	    // Register JWT + XSS filters
//	    http.authenticationProvider(authenticationProvider());
//	    http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
//	    http.addFilterBefore(xssFilter(), UsernamePasswordAuthenticationFilter.class);
//
//	    return http.build();
//	}

	@Bean
	public AuthenticationFailureHandler authenticationFailureHandler() {
		return new CustomAuthenticationFailureHandler(objectMapper());
	}

	@Bean
	public AuthenticationSuccessHandler myAuthenticationSuccessHandler() {
		return new CustomSimpleUrlAuthenticationSuccessHandler();
	}

}