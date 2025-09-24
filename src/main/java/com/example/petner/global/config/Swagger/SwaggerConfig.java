package com.example.petner.global.config.Swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger springdoc-ui 구성 파일
 */
@Configuration
public class SwaggerConfig {

    /* Swagger 설정 Bean */
    // 접근 경로 : http://localhost:8080/swagger-ui/index.html
    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("PETNER Backend API")
                .version("v0.0.1")
                .description("PETNER 프로젝트 API 명세서");

        return new OpenAPI()
                .components(components())
                .addSecurityItem(securityRequirement())
                .info(info);
    }
    /**
     * Swagger 보안 설정
     * 세션 쿠키 인증을 위한 설정
     */
    private SecurityRequirement securityRequirement() {
        return new SecurityRequirement().addList("SESSION");
    }

    private Components components() {
        return new Components()
                .addSecuritySchemes("SESSION", new SecurityScheme()
                        .name("SESSION")
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.COOKIE)
                        .description("세션 쿠키 인증. 브라우저에서 로그인 후 자동으로 설정됩니다."));
    }
}