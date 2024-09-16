package com.example.metrics.config;


import com.example.metrics.filter.JwtTokenFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class JwtConfig implements WebMvcConfigurer {

    @Value("${url-metrics.jwt.secret}")
    private String jwtSecret;

    @Bean
    public OncePerRequestFilter jwtTokenFilter() {
        return new JwtTokenFilter(jwtSecret);
    }
}
