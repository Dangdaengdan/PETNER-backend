package com.example.petner.global.config.Swagger;

import com.example.petner.global.annotation.SessionMember;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Parameter;

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
    
    /**
     * @SessionMember 파라미터를 스웨거에서 숨기는 customizer
     * 
     * 수정 이유:
     * 1. handlerMethod 파라미터와 operation 파라미터의 인덱스 불일치 문제 해결
     * 2. 순차 제거 시 인덱스 변경으로 인한 누락 문제 방지
     * 3. 파라미터 이름 기반 매칭으로 정확성 향상
     */
    @Bean
    public OperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> {
            if (operation.getParameters() == null) {
                return operation;
            }
            
            // 메서드의 모든 파라미터 검사
            Parameter[] methodParameters = handlerMethod.getMethod().getParameters();
            
            // @SessionMember 애노테이션이 있는 파라미터 이름 수집
            for (Parameter methodParam : methodParameters) {
                if (methodParam.isAnnotationPresent(SessionMember.class)) {
                    String paramName = methodParam.getName();
                    
                    // 파라미터 이름으로 OpenAPI 파라미터 찾아서 제거
                    operation.getParameters().removeIf(openApiParam -> 
                        paramName.equals(openApiParam.getName()) || 
                        "user".equals(openApiParam.getName())  // SessionUser의 일반적인 파라미터명
                    );
                }
            }
            
            return operation;
        };
    }
}