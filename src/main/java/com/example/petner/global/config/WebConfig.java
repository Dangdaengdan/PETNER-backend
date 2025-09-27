package com.example.petner.global.config;

import com.example.petner.global.resolver.SessionMemberArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Web MVC 설정
 * 
 * 주요 기능:
 * - ArgumentResolver 등록: @SessionMember 애노테이션 지원
 * - 향후 추가될 수 있는 다른 웹 설정들
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final SessionMemberArgumentResolver sessionMemberArgumentResolver;

    /**
     * CORS 설정
     * 프론트엔드(localhost:5173)에서의 요청을 허용
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * ArgumentResolver 등록
     * @SessionMember 애노테이션이 붙은 파라미터에 SessionUser를 자동 주입
     *
     * @param resolvers ArgumentResolver 목록
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(sessionMemberArgumentResolver);
    }
}