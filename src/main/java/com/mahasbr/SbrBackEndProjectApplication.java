package com.mahasbr;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;

import jakarta.annotation.PostConstruct;

//@EnableJpaAuditing
@SpringBootApplication
//@Profile("local")
//@PropertySource("classpath:application.properties")
//@ActiveProfiles("uat")
//@EnableRetry
@EnableAsync
//@EnableScheduling
public class SbrBackEndProjectApplication {
	private static final Logger logger =
            LoggerFactory.getLogger(SbrBackEndProjectApplication.class);
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

	
	@Value("${spring.servlet.multipart.max-file-size:#{null}}")
    private String maxFileSize;

    @Value("${spring.servlet.multipart.max-request-size:#{null}}")
    private String maxRequestSize;

    @PostConstruct
    @Profile("local") // 🔥 ONLY in local
    public void logUploadConfig() {

        logger.debug("=== UPLOAD DEBUG START ===");
        logger.debug("spring.servlet.multipart.max-file-size = {}", maxFileSize);
        logger.debug("spring.servlet.multipart.max-request-size = {}", maxRequestSize);

        try {
            Class<?> connector = Class.forName("org.apache.catalina.connector.Connector");
            logger.debug("Tomcat Connector detected: {}", connector.getName());
        } catch (Exception e) {
            logger.warn("Connector class lookup failed", e);
        }

        logger.debug("=== UPLOAD DEBUG END ===");
    }
}
