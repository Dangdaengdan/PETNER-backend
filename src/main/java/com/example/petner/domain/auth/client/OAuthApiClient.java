package com.example.petner.domain.auth.client;

import com.example.petner.domain.auth.dto.KakaoUserInfo;

/**
 * OAuth API 클라이언트 인터페이스
 * ISP(인터페이스 분리 원칙) 준수: OAuth 관련 기능만 정의
 * DIP(의존성 역전 원칙) 준수: 고수준 모듈이 추상화에 의존
 */
public interface OAuthApiClient {
    
    /**
     * Authorization Code를 사용하여 Access Token을 획득
     * @param authorizationCode OAuth Authorization Code
     * @return Access Token
     */
    String getAccessToken(String authorizationCode);
    
    /**
     * Access Token을 사용하여 사용자 정보를 조회
     * @param accessToken OAuth Access Token
     * @return 사용자 정보
     */
    KakaoUserInfo getUserInfo(String accessToken);
}