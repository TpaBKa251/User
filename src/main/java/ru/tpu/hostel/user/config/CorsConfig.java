package ru.tpu.hostel.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

//@Configuration
//public class CorsConfig {
//
//    @Bean
//    public CorsFilter corsFilter() {
//        CorsConfiguration corsConfig = new CorsConfiguration();
//        corsConfig.setAllowCredentials(true); // Разрешает отправку куки
//        corsConfig.addAllowedHeader("*"); // Разрешает все заголовки
//        corsConfig.addAllowedMethod("*"); // Разрешает все HTTP-методы
//        corsConfig.setAllowedOriginPatterns(List.of("*")); // Динамическое определение источников
//        corsConfig.addExposedHeader("Authorization"); // Если нужно отправлять кастомные заголовки клиенту
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", corsConfig);
//
//        return new CorsFilter(source);
//    }
//}

