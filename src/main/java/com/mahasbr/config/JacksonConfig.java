package com.mahasbr.config;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

@Configuration
public class JacksonConfig {

	 public static final String DATE_PATTERN = "yyyy-MM-dd";
	    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

	    @Bean
	    public ObjectMapper objectMapper() {
	        ObjectMapper mapper = new ObjectMapper();

	        JavaTimeModule javaTimeModule = new JavaTimeModule();

	        // LocalDate
	        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
	        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter));
	        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormatter));

	        // LocalDateTime
	        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
	        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
	        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));

	        mapper.registerModule(javaTimeModule);
	        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

	        return mapper;
	    }
}
