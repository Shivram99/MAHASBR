package com.mahasbr.config;

import java.time.Duration;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	private static final long EXPORT_ASYNC_TIMEOUT_MILLIS = Duration.ofMinutes(10).toMillis();

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
		        .allowedOrigins("http://10.0.0.78:4200","http://localhost:4200","http://13.204.181.90:80","http://13.204.181.90") // or your Angular app URL
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
				.allowedHeaders("*")
//				.exposedHeaders("Authorization");
				.allowedHeaders("Authorization", "Content-Type").allowCredentials(true);

	}

	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
		configurer.setTaskExecutor(mvcTaskExecutor());
		configurer.setDefaultTimeout(EXPORT_ASYNC_TIMEOUT_MILLIS);
	}

	@Bean
	public AsyncTaskExecutor mvcTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setThreadNamePrefix("mvc-async-");
		executor.setCorePoolSize(4);
		executor.setMaxPoolSize(8);
		executor.setQueueCapacity(50);
		executor.setAllowCoreThreadTimeOut(true);
		executor.setKeepAliveSeconds((int) Duration.ofMinutes(1).toSeconds());
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		executor.initialize();
		return new DelegatingSecurityContextAsyncTaskExecutor(executor);
	}

}
