package com.example.petner.domain.auth.service;

import com.example.petner.domain.auth.dto.KakaoUserInfo;
import com.example.petner.domain.auth.dto.response.LoginResponse;
import com.example.petner.domain.auth.dto.response.LogoutResponse;
import com.example.petner.domain.auth.dto.response.MemberResponse;
import com.example.petner.domain.auth.dto.response.SessionResponse;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.member.repository.MemberRepository;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.MemberException;
import com.example.petner.domain.auth.client.OAuthApiClient;
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
    private final MemberRepository memberRepository;
    
    private static final String SESSION_MEMBER_KEY = "member";

    public LoginResponse kakaoLogin(String authorizationCode, HttpSession session) {
        // 외부 API 호출을 트랜잭션 밖에서 실행
        String accessToken = oAuthApiClient.getAccessToken(authorizationCode);
        KakaoUserInfo kakaoUserInfo = oAuthApiClient.getUserInfo(accessToken);
        
        // 데이터베이스 작업만 트랜잭션으로 처리
        Member member = findOrCreateMemberWithTransaction(kakaoUserInfo);
        
        session.setAttribute(SESSION_MEMBER_KEY, member.getMemberId());
        
        String maskedKakaoId = kakaoUserInfo.getKakaoId().length() > 4 ? 
            kakaoUserInfo.getKakaoId().substring(0, 4) + "****" : "****";
        log.info("[로그인] 성공: kakaoId={}, memberId={}", maskedKakaoId, member.getMemberId());
        
        return LoginResponse.builder()
                .message("로그인 성공")
                .member(MemberResponse.forLogin(member)) // 로그인용 최소 정보만 제공
                .build();
    }


    @Transactional
    protected Member findOrCreateMemberWithTransaction(KakaoUserInfo kakaoUserInfo) {
        return findOrCreateMember(kakaoUserInfo);
    }

    private Member findOrCreateMember(KakaoUserInfo kakaoUserInfo) {
        return memberRepository.findByKakaoId(kakaoUserInfo.getKakaoId())
                .orElseGet(() -> createNewMember(kakaoUserInfo));
    }
    
    private Member createNewMember(KakaoUserInfo kakaoUserInfo) {
        try {
            // 카카오 정보로 임시 회원 생성 (kakaoId만 저장)
            Member newMember = Member.createTemporaryMember(
                kakaoUserInfo.getKakaoId()
            );
            
            Member savedMember = memberRepository.save(newMember);
            log.info("[회원가입] 임시 회원 생성 완료: memberId={}, profileComplete={}", 
                savedMember.getMemberId(), savedMember.isProfileComplete());
            
            return savedMember;
        } catch (Exception e) {
            log.error("[회원가입] 임시 회원 생성 실패: kakaoId={}", kakaoUserInfo.getKakaoId(), e);
            throw new MemberException(ErrorCode.MEMBER_ALREADY_EXISTS);
        }
    }

    public LogoutResponse logout(HttpSession session) {
        try {
            Long memberId = (Long) session.getAttribute(SESSION_MEMBER_KEY);
            if (memberId != null) {
                log.info("[로그아웃] 완료: memberId={}", memberId);
            }
            session.invalidate();
            return LogoutResponse.success();
        } catch (Exception e) {
            log.warn("[로그아웃] 세션 무효화 실패", e);
            return LogoutResponse.success(); // 클라이언트에게는 성공으로 응답
        }
    }

    public MemberResponse getCurrentMember(HttpSession session) {
        Long memberId = (Long) session.getAttribute(SESSION_MEMBER_KEY);
        if (memberId == null) {
            throw new MemberException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        return MemberResponse.from(member);
    }

    public SessionResponse getSessionStatus(HttpSession session) {
        boolean isAuthenticated = session.getAttribute(SESSION_MEMBER_KEY) != null;
        return isAuthenticated ? SessionResponse.authenticated() : SessionResponse.unauthenticated();
    }
}