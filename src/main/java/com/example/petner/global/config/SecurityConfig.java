package com.example.petner.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableWebSecurity
@EnableRedisHttpSession
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Swagger UI 관련 경로 허용
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/ws-stomp/**"  ).permitAll()
                // 개발 단계에서는 모든 요청 허용
                .anyRequest().permitAll()
            )
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionFixation().migrateSession()
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            );
        
        return http.build();
    }
}