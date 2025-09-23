package com.example.petner.domain.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SessionResponse {
    private boolean authenticated;
    private String status;
    
    public static SessionResponse authenticated() {
        return SessionResponse.builder()
                .authenticated(true)
                .status("인증됨")
                .build();
    }
    
    public static SessionResponse unauthenticated() {
        return SessionResponse.builder()
                .authenticated(false)
                .status("인증되지 않음")
                .build();
    }
}