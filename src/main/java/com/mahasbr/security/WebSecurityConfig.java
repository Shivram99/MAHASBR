package com.mahasbr.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.mahasbr.filter.AuthEntryPointJwt;
import com.mahasbr.filter.AuthTokenFilter;
import com.mahasbr.service.UserDetailsServiceImpl;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig { // extends WebSecurityConfigurerAdapter {
	
  @Autowired
  UserDetailsServiceImpl userDetailsService;

  @Autowired
  private AuthEntryPointJwt unauthorizedHandler;

  @Autowired
  private CustomLogoutSuccessHandler logoutSuccessHandler;   
  
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
  public LogoutSuccessHandler logoutSuccessHandler() {
      return new HttpStatusReturningLogoutSuccessHandler();
  }

	/*
	 * @Bean public SimpleUrlAuthenticationSuccessHandler loginSuccessHandler() {
	 * return new SimpleUrlAuthenticationSuccessHandler(); }
	 */
  
  
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> 
          auth.requestMatchers("/api/auth/**").permitAll()
              .requestMatchers("/api/auth/signup").permitAll()
              .requestMatchers("/api/auth/signin").permitAll()
              .requestMatchers("/api/test/**").permitAll()
              .requestMatchers("/common/api**").permitAll()
              .requestMatchers(" /common/department**").permitAll()
              .requestMatchers("/admin/**").hasRole("ADMIN")
              .requestMatchers("/moderator/**").hasRole("MODERATOR")
              .requestMatchers("/developer/**").hasRole("DEVELOPER")
            .requestMatchers("/user/**").permitAll()
              .anyRequest().authenticated()
        ). logout().logoutUrl("/logout").permitAll();
        
	/*
	 * logout(logout -> logout .logoutUrl("/logout")
	 * .logoutSuccessHandler(logoutSuccessHandler) //
	 * .logoutFailureHandler(logoutFailureHandler()) );
	 */
    
    http.authenticationProvider(authenticationProvider());
    http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
  
  

}