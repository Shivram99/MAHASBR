package com.mahasbr.config;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class Config {

    @Bean
    public JavaMailSender javaMailSender() {
        return new JavaMailSenderImpl();
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
    
//    @Bean
//    public TomcatServletWebServerFactory tomcatCustomizer() {
//        return new TomcatServletWebServerFactory() {
//            @Override
//            protected void customizeConnector(Connector connector) {
//                super.customizeConnector(connector);
//                connector.setMaxPostSize(-1);    // unlimited
//            }
//        };
//    }
}