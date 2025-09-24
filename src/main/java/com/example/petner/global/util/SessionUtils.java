package com.example.petner.global.util;

import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.member.service.MemberService;
import com.example.petner.global.dto.SessionUser;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.MemberException;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

public final class SessionUtils {
    
    private static final String SESSION_MEMBER_KEY = "member";
    private static final String SESSION_USER_KEY = "sessionUser";
    
    private SessionUtils() {
        // 유틸리티 클래스 - 인스턴스 생성 방지
    }
    
    /**
     * 현재 로그인한 사용자 ID 조회
     * @param session HTTP 세션
     * @return 사용자 ID (미인증 시 null)
     */
    public static Long getCurrentMemberId(HttpSession session) {
        if (session == null) {
            return null;
        }
        return (Long) session.getAttribute(SESSION_MEMBER_KEY);
    }
    
    /**
     * 현재 로그인한 사용자 정보 조회 (캐싱된 정보)
     * @param session HTTP 세션
     * @return SessionUser (미인증 시 empty)
     */
    public static Optional<SessionUser> getCurrentUser(HttpSession session) {
        if (session == null) {
            return Optional.empty();
        }
        SessionUser sessionUser = (SessionUser) session.getAttribute(SESSION_USER_KEY);
        return Optional.ofNullable(sessionUser);
    }
    
    /**
     * 인증 상태 확인
     * @param session HTTP 세션
     * @return 인증 여부
     */
    public static boolean isAuthenticated(HttpSession session) {
        return getCurrentMemberId(session) != null;
    }
    
    /**
     * 인증 필수 확인 (인증되지 않은 경우 예외 발생)
     * @param session HTTP 세션
     * @throws MemberException 미인증 시 UNAUTHORIZED_ACCESS
     */
    public static void requireAuthentication(HttpSession session) {
        if (!isAuthenticated(session)) {
            throw new MemberException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
    }
    
    /**
     * 현재 로그인한 사용자 ID 조회 (인증 필수)
     * @param session HTTP 세션
     * @return 사용자 ID
     * @throws MemberException 미인증 시 UNAUTHORIZED_ACCESS
     */
    public static Long requireCurrentMemberId(HttpSession session) {
        requireAuthentication(session);
        return getCurrentMemberId(session);
    }
    
    /**
     * 현재 로그인한 사용자 정보 조회 (인증 필수)
     * @param session HTTP 세션
     * @return SessionUser
     * @throws MemberException 미인증 시 UNAUTHORIZED_ACCESS
     */
    public static SessionUser requireCurrentUser(HttpSession session) {
        requireAuthentication(session);
        return getCurrentUser(session)
                .orElseThrow(() -> new MemberException(ErrorCode.UNAUTHORIZED_ACCESS));
    }
    
    /**
     * 로그인 처리 - 세션에 사용자 정보 저장
     * @param session HTTP 세션
     * @param member 로그인한 사용자
     */
    public static void setSessionUser(HttpSession session, Member member) {
        if (session == null || member == null) {
            return;
        }
        
        session.setAttribute(SESSION_MEMBER_KEY, member.getMemberId());
        session.setAttribute(SESSION_USER_KEY, SessionUser.from(member));
    }
    
    /**
     * 세션 사용자 정보 갱신 (프로필 수정 시 사용)
     * @param session HTTP 세션
     * @param memberService 멤버 서비스
     */
    public static void refreshSessionUser(HttpSession session, MemberService memberService) {
        Long memberId = getCurrentMemberId(session);
        if (memberId != null) {
            Member member = memberService.findById(memberId);
            session.setAttribute(SESSION_USER_KEY, SessionUser.from(member));
        }
    }
    
    /**
     * 로그아웃 처리 - 세션 무효화
     * @param session HTTP 세션
     */
    public static void clearSession(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }
    
    // === 하위 호환성을 위한 메서드 (기존 방식) ===
    
    /**
     * 세션에 사용자 ID만 저장 (기존 방식 호환)
     * @param session HTTP 세션
     * @param memberId 사용자 ID
     */
    public static void setMemberId(HttpSession session, Long memberId) {
        if (session != null && memberId != null) {
            session.setAttribute(SESSION_MEMBER_KEY, memberId);
        }
    }
    
    /**
     * 세션에서 사용자 ID 조회 (기존 방식 호환)
     * @param session HTTP 세션
     * @return 사용자 ID
     */
    public static Optional<Long> getMemberId(HttpSession session) {
        return Optional.ofNullable(getCurrentMemberId(session));
    }
}