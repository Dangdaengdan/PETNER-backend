package com.example.petner.domain.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LogoutResponse {
    private String message;
    
    public static LogoutResponse success() {
        return LogoutResponse.builder()
                .message("로그아웃 성공")
                .build();
    }
}