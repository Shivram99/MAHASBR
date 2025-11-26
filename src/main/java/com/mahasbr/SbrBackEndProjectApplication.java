package com.mahasbr;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

//@EnableJpaAuditing
@SpringBootApplication
//@Profile("local")
//@PropertySource("classpath:application.properties")
//@ActiveProfiles("uat")
//@EnableRetry
@EnableAsync
//@EnableScheduling
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

	
	@Value("${spring.servlet.multipart.max-file-size:#{null}}")
    private String maxFileSize;

    @Value("${spring.servlet.multipart.max-request-size:#{null}}")
    private String maxRequestSize;

    @PostConstruct
    public void log() {
        System.out.println("=== UPLOAD DEBUG START ===");
        System.out.println("spring.servlet.multipart.max-file-size = " + maxFileSize);
        System.out.println("spring.servlet.multipart.max-request-size = " + maxRequestSize);

        try {
            Class<?> c = Class.forName("org.apache.catalina.connector.Connector");
            System.out.println("Connector class: " + c.getName());
        } catch (Exception e) {
            System.out.println("Connector class lookup failed: " + e.getMessage());
        }

        System.out.println("Servlet API = " + HttpServletRequest.class.getPackage().getImplementationVersion());
        System.out.println("=== UPLOAD DEBUG END ===");
    }
}
