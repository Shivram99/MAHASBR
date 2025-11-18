package com.mahasbr;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

//@EnableJpaAuditing
@SpringBootApplication
//@Profile("local")
//@PropertySource("classpath:application.properties")
//@ActiveProfiles("uat")
@EnableRetry
@EnableAsync
@EnableScheduling
public class SbrBackEndProjectApplication {

	public static void main(String[] args) {

		LocalDateTime currentDateTimeIndia = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
		ConfigurableApplicationContext context =SpringApplication.run(SbrBackEndProjectApplication.class, args);

		  // Get the environment to access properties
        Environment env = context.getEnvironment();

        // Get the server port (default is 8080 if not set)
        String port = env.getProperty("server.port", "8080");

        System.out.println("==========================================");
        System.out.println("🚀 Application started successfully!");
        System.out.println("🌐 Running on: http://localhost:" + port);
        System.out.println("==========================================");
	}

}
