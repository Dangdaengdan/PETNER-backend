package com.example.petner.domain.auth.controller;

import com.example.petner.domain.auth.dto.response.LoginResponse;
import com.example.petner.domain.auth.dto.response.LogoutResponse;
import com.example.petner.domain.auth.dto.response.MemberResponse;
import com.example.petner.domain.auth.dto.response.SessionResponse;
import com.example.petner.domain.auth.service.AuthService;
import com.example.petner.global.annotation.SessionMember;
import com.example.petner.global.dto.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth/kakao")
public class AuthController {

    private final AuthService authService;
    private final String kakaoClientId;
    private final String kakaoRedirectUri;

    public AuthController(AuthService authService,
                         @Value("${kakao.oauth.client-id}") String kakaoClientId,
                         @Value("${kakao.oauth.redirect-uri}") String kakaoRedirectUri) {
        this.authService = authService;
        this.kakaoClientId = kakaoClientId;
        this.kakaoRedirectUri = kakaoRedirectUri;
    }

    @GetMapping("/login")
    public RedirectView kakaoLogin() {
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?client_id=" + kakaoClientId +
                "&redirect_uri=" + URLEncoder.encode(kakaoRedirectUri, StandardCharsets.UTF_8) +
                "&response_type=code";
        
        log.info("[카카오 로그인] 요청 시작");
        return new RedirectView(kakaoAuthUrl);
    }

    @GetMapping("/callback")
    public ResponseEntity<LoginResponse> kakaoCallback(
            @RequestParam("code") String authorizationCode,
            HttpSession session) {
        
        if (authorizationCode == null || authorizationCode.trim().isEmpty()) {
            log.warn("[카카오 콜백] 인증 코드가 비어있음");
            throw new RuntimeException("인증 코드가 비어있습니다");
        }
        
        String maskedCode = authorizationCode.length() > 4 ? 
            authorizationCode.substring(0, 4) + "****" : "****";
        log.info("[카카오 콜백] 처리 시작: code={}", maskedCode);
        
        LoginResponse response = authService.kakaoLogin(authorizationCode, session);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(HttpSession session) {
        LogoutResponse response = authService.logout(session);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/member")
    public ResponseEntity<MemberResponse> getCurrentMember(@SessionMember SessionUser user) {
        // @SessionMember로 자동 주입받은 SessionUser에서 바로 MemberResponse 생성
        MemberResponse member = MemberResponse.builder()
                .memberId(user.getMemberId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .gender(user.getGender())
                .housingType(user.getHousingType())
                .contact(user.getContact())
                .locationId(user.getLocationId())
                .state(user.getState())
                .district(user.getDistrict())
                .locationName(user.getLocationName())
                .profileCompleted(user.isProfileCompleted())
                .build();
        return ResponseEntity.ok(member);
    }

    @GetMapping("/session")
    public ResponseEntity<SessionResponse> checkAuthentication(@SessionMember(required = false) SessionUser user) {
        boolean isAuthenticated = user != null;
        return ResponseEntity.ok(isAuthenticated ? SessionResponse.authenticated() : SessionResponse.unauthenticated());
    }
    
}