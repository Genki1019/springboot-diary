package com.genki.rest_api.diary.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // 全エンドポイントに適用
                        .allowedOrigins("http://localhost:5173") // ReactアプリのURL
                        .allowedMethods("GET", "POST", "PUT", "DELETE") // 許可するHTTPメソッド
                        .allowedHeaders("*"); // 全てのヘッダーを許可
            }
        };
    }
}