package ru.tpu.hostel.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("https://5025-85-143-79-87.ngrok-free.app") // Принимает запросы от всех источников
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        .allowCredentials(true); // Позволяет отправку токенов, если это нужно клиенту
            }
        };
    }
}

