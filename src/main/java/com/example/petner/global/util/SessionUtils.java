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
     * @throws MemberException 세션 데이터가 손상된 경우
     */
    public static Long getCurrentMemberId(HttpSession session) {
        if (session == null) {
            return null;
        }
        
        try {
            Object memberIdObj = session.getAttribute(SESSION_MEMBER_KEY);
            if (memberIdObj == null) {
                return null;
            }
            return (Long) memberIdObj;
        } catch (ClassCastException e) {
            // 세션 데이터가 Long이 아닌 경우 (데이터 손상)
            throw new MemberException(ErrorCode.SESSION_DATA_CORRUPTED);
        }
    }
    
    /**
     * 현재 로그인한 사용자 정보 조회 (캐싱된 정보)
     * @param session HTTP 세션
     * @return SessionUser (미인증 시 empty)
     * @throws MemberException 세션 데이터가 손상된 경우
     */
    public static Optional<SessionUser> getCurrentUser(HttpSession session) {
        if (session == null) {
            return Optional.empty();
        }
        
        try {
            Object sessionUserObj = session.getAttribute(SESSION_USER_KEY);
            if (sessionUserObj == null) {
                return Optional.empty();
            }
            SessionUser sessionUser = (SessionUser) sessionUserObj;
            return Optional.of(sessionUser);
        } catch (ClassCastException e) {
            // 세션 데이터가 SessionUser가 아닌 경우 (데이터 손상)
            throw new MemberException(ErrorCode.SESSION_DATA_CORRUPTED);
        } catch (Exception e) {
            // 직렬화/역직렬화 오류 등
            throw new MemberException(ErrorCode.SESSION_INVALID_DATA);
        }
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
     * @throws MemberException 세션 또는 멤버가 null인 경우
     */
    public static void setSessionUser(HttpSession session, Member member) {
        if (session == null) {
            throw new MemberException(ErrorCode.GLOBAL_ERROR);
        }
        if (member == null) {
            throw new MemberException(ErrorCode.MEMBER_NOT_FOUND);
        }
        
        session.setAttribute(SESSION_MEMBER_KEY, member.getMemberId());
        session.setAttribute(SESSION_USER_KEY, SessionUser.from(member));
    }
    
    /**
     * 세션 사용자 정보 갱신 (프로필 수정 시 사용)
     * @param session HTTP 세션
     * @param memberService 멤버 서비스
     * @throws MemberException 세션이 null이거나 인증되지 않은 경우, 멤버를 찾을 수 없는 경우
     */
    public static void refreshSessionUser(HttpSession session, MemberService memberService) {
        if (session == null) {
            throw new MemberException(ErrorCode.GLOBAL_ERROR);
        }
        if (memberService == null) {
            throw new MemberException(ErrorCode.GLOBAL_ERROR);
        }
        
        Long memberId = getCurrentMemberId(session);
        if (memberId == null) {
            throw new MemberException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
        
        try {
            Member member = memberService.findById(memberId);
            if (member == null) {
                throw new MemberException(ErrorCode.MEMBER_NOT_FOUND);
            }
            session.setAttribute(SESSION_USER_KEY, SessionUser.from(member));
        } catch (MemberException e) {
            // MemberException은 그대로 전파
            throw e;
        } catch (Exception e) {
            // 기타 예외는 서버 오류로 처리
            throw new MemberException(ErrorCode.GLOBAL_ERROR);
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