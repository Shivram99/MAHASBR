package com.mahasbr.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
    	  registry.addMapping("/**")
          // .allowedOrigins("http://localhost:4200") // Assuming Angular app runs on localhost:4200
            .allowedOrigins("*") // Assuming Angular app runs on localhost:4200
           .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Add allowed methods as needed
           .allowedHeaders("*")
           .allowCredentials(false)
           .exposedHeaders("Access-Control-Allow-Origin");
    }
}
