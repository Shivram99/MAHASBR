package com.mahasbr;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

//@EnableJpaAuditing
@SpringBootApplication
//@Profile("local")
//@PropertySource("classpath:application.properties")
//@ActiveProfiles("uat")
//@EnableRetry
//@EnableAsync
//@EnableScheduling
public class SbrBackEndProjectApplication {

	public static void main(String[] args) {

		LocalDateTime currentDateTimeIndia = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
		SpringApplication.run(SbrBackEndProjectApplication.class, args);

	}

}
