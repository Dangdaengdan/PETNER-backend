package com.example.petner.global.resolver;

import com.example.petner.global.annotation.SessionMember;
import com.example.petner.global.dto.SessionUser;
import com.example.petner.global.exception.customException.MemberException;
import com.example.petner.global.util.SessionUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @SessionMember 애노테이션이 붙은 파라미터에 현재 로그인 사용자 정보를 자동으로 주입하는 ArgumentResolver
 * 
 * 동작 방식:
 * 1. @SessionMember 애노테이션이 있는 파라미터 감지
 * 2. HttpSession에서 SessionUser 정보 조회
 * 3. required=true면 인증 필수, required=false면 인증 선택적
 * 4. SessionUser 객체를 컨트롤러 메서드에 자동 주입
 */
@Slf4j
@Component
public class SessionMemberArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * 이 ArgumentResolver가 처리할 파라미터인지 판단
     * @param parameter 메서드 파라미터 정보
     * @return @SessionMember 애노테이션이 있고 SessionUser 타입이면 true
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (parameter == null) {
            log.error("MethodParameter가 null입니다");
            return false;
        }
        
        try {
            boolean hasAnnotation = parameter.hasParameterAnnotation(SessionMember.class);
            boolean isSessionUserType = SessionUser.class.isAssignableFrom(parameter.getParameterType());
            
            if (hasAnnotation && !isSessionUserType) {
                log.warn("@SessionMember 애노테이션은 SessionUser 타입 파라미터에만 사용 가능합니다. " +
                        "현재 타입: {}, 메서드: {}", 
                        parameter.getParameterType() != null ? parameter.getParameterType().getSimpleName() : "null",
                        parameter.getMethod());
            }
            
            return hasAnnotation && isSessionUserType;
        } catch (Exception e) {
            log.error("supportsParameter 처리 중 오류 발생", e);
            return false;
        }
    }

    /**
     * 실제 파라미터 값을 해결(resolve)하는 메서드
     * @param parameter 메서드 파라미터 정보
     * @param mavContainer ModelAndView 컨테이너
     * @param webRequest 웹 요청 객체
     * @param binderFactory 데이터 바인더 팩토리
     * @return SessionUser 객체 또는 null
     * @throws MemberException 인증 실패 시
     */
    @Override
    public Object resolveArgument(MethodParameter parameter,
                                ModelAndViewContainer mavContainer,
                                NativeWebRequest webRequest,
                                WebDataBinderFactory binderFactory) {
        
        // 파라미터 검증
        if (parameter == null) {
            log.error("MethodParameter가 null입니다");
            throw new RuntimeException("내부 오류가 발생했습니다");
        }
        
        if (webRequest == null) {
            log.error("NativeWebRequest가 null입니다");
            throw new RuntimeException("내부 오류가 발생했습니다");
        }
        
        // HTTP 요청에서 세션 추출
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            log.error("HttpServletRequest를 가져올 수 없습니다");
            throw new RuntimeException("내부 오류가 발생했습니다");
        }
        
        HttpSession session = request.getSession(false);
        SessionMember annotation = parameter.getParameterAnnotation(SessionMember.class);
        
        if (annotation == null) {
            log.error("@SessionMember 애노테이션을 찾을 수 없습니다 - 메서드: {}", 
                     parameter.getMethod());
            throw new RuntimeException("내부 오류가 발생했습니다");
        }
        
        // 세션에서 사용자 정보 조회
        SessionUser sessionUser = null;
        try {
            if (annotation.required()) {
                // 인증 필수: 없으면 예외 발생
                sessionUser = SessionUtils.requireCurrentUser(session);
                log.debug("@SessionMember 인증 필수 모드: 사용자 정보 주입 완료 (memberId: {})", 
                         sessionUser.getMemberId());
            } else {
                // 인증 선택적: 없으면 null 반환
                sessionUser = SessionUtils.getCurrentUser(session).orElse(null);
                log.debug("@SessionMember 선택적 모드: 사용자 정보 주입 완료 (memberId: {})", 
                         sessionUser != null ? sessionUser.getMemberId() : "null");
            }
        } catch (MemberException e) {
            log.debug("@SessionMember 인증 실패: {} (required={})", e.getErrorCode().getMessage(), annotation.required());
            if (annotation.required()) {
                // required=true인 경우 예외를 다시 던짐 (GlobalExceptionHandler에서 401 응답 처리)
                throw e;
            }
            // required=false인 경우 null 반환
            return null;
        } catch (Exception e) {
            log.error("@SessionMember 처리 중 예기치 못한 오류 발생", e);
            throw new RuntimeException("내부 오류가 발생했습니다", e);
        }
        
        return sessionUser;
    }
}