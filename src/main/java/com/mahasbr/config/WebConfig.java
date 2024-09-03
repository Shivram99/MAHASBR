package com.mahasbr.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

//    @Bean
//    public void addCorsMappings(CorsRegistry registry) {
//    	  registry.addMapping("/**")
//          // .allowedOrigins("http://localhost:4200") // Assuming Angular app runs on localhost:4200
//            .allowedOrigins("*") // Assuming Angular app runs on localhost:4200
//           .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Add allowed methods as needed
//           .allowedHeaders("*")
//           .allowCredentials(true)
//           .exposedHeaders("Access-Control-Allow-Origin");
//    }
    
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter());
    }
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }
}



