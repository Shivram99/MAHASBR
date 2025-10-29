package com.mahasbr.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
		        .allowedOrigins("http://10.0.0.78:4200","http://localhost:4200","http://13.127.98.242:8082") // or your Angular app URL
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
				.allowedHeaders("*")
//				.exposedHeaders("Authorization");
				.allowedHeaders("Authorization", "Content-Type").allowCredentials(true);

	}
}
