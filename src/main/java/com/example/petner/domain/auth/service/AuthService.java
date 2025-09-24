package com.example.petner.domain.auth.service;

import com.example.petner.domain.auth.dto.KakaoUserInfo;
import com.example.petner.domain.auth.dto.response.LoginResponse;
import com.example.petner.domain.auth.dto.response.LogoutResponse;
import com.example.petner.domain.auth.dto.response.MemberResponse;
import com.example.petner.domain.auth.dto.response.SessionResponse;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.member.service.MemberService;
import com.example.petner.global.dto.SessionUser;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.MemberException;
import com.example.petner.domain.auth.client.OAuthApiClient;
import com.example.petner.global.util.SessionUtils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final OAuthApiClient oAuthApiClient;
    private final MemberService memberService;

    @Transactional
    public LoginResponse kakaoLogin(String authorizationCode, HttpSession session) {
        // 외부 API 호출
        String accessToken = oAuthApiClient.getAccessToken(authorizationCode);
        KakaoUserInfo kakaoUserInfo = oAuthApiClient.getUserInfo(accessToken);
        
        // 회원 찾기 또는 생성 (MemberService에서 트랜잭션 처리)
        Member member = memberService.findOrCreateMember(kakaoUserInfo);
        
        // 세션에 사용자 정보 저장
        SessionUtils.setSessionUser(session, member);
        
        String maskedKakaoId = kakaoUserInfo.getKakaoId().length() > 4 ? 
            kakaoUserInfo.getKakaoId().substring(0, 4) + "****" : "****";
        log.info("[로그인] 성공: kakaoId={}, memberId={}", maskedKakaoId, member.getMemberId());
        
        return LoginResponse.builder()
                .message("로그인 성공")
                .member(MemberResponse.forLogin(member))
                .build();
    }


    public LogoutResponse logout(HttpSession session) {
        try {
            Long memberId = SessionUtils.getCurrentMemberId(session);
            if (memberId != null) {
                log.info("[로그아웃] 완료: memberId={}", memberId);
            }
            SessionUtils.clearSession(session);
            return LogoutResponse.success();
        } catch (Exception e) {
            log.warn("[로그아웃] 세션 무효화 실패", e);
            return LogoutResponse.success(); // 클라이언트에게는 성공으로 응답
        }
    }

    public MemberResponse getCurrentMember(HttpSession session) {
        // SessionUser 캐시에서 먼저 조회 시도
        SessionUser sessionUser = SessionUtils.getCurrentUser(session)
                .orElse(null);
        
        if (sessionUser != null) {
            // 캐시된 정보로 MemberResponse 생성 (DB 조회 없음)
            return MemberResponse.builder()
                    .memberId(sessionUser.getMemberId())
                    .email(sessionUser.getEmail())
                    .nickname(sessionUser.getNickname())
                    .gender(sessionUser.getGender())
                    .housingType(sessionUser.getHousingType())
                    .contact(sessionUser.getContact())
                    .locationId(sessionUser.getLocationId())
                    .state(sessionUser.getState())
                    .district(sessionUser.getDistrict())
                    .locationName(sessionUser.getLocationName())
                    .profileCompleted(sessionUser.isProfileCompleted())
                    .build();
        }
        
        // 캐시가 없으면 기존 방식으로 폴백 (DB 조회)
        Long memberId = SessionUtils.requireCurrentMemberId(session);
        Member member = memberService.findById(memberId);
        return MemberResponse.from(member);
    }

    public SessionResponse getSessionStatus(HttpSession session) {
        boolean isAuthenticated = SessionUtils.isAuthenticated(session);
        return isAuthenticated ? SessionResponse.authenticated() : SessionResponse.unauthenticated();
    }
}