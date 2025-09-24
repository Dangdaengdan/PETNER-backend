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
     */
    @Bean
    public OperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> {
            // 메서드의 모든 파라미터 검사
            Parameter[] parameters = handlerMethod.getMethod().getParameters();
            
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                
                // @SessionMember 애노테이션이 있는 파라미터는 스웨거에서 제거
                if (parameter.isAnnotationPresent(SessionMember.class)) {
                    if (operation.getParameters() != null && i < operation.getParameters().size()) {
                        operation.getParameters().remove(i);
                    }
                }
            }
            
            return operation;
        };
    }
}