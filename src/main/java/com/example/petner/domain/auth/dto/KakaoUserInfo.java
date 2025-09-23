package com.example.petner.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoUserInfo {
    private String kakaoId;
    private String email;
    private String nickname;
}