package com.example.petner.global.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 세션에서 현재 로그인한 사용자 정보를 자동으로 주입하는 애노테이션
 * 
 * 사용 예시:
 * <pre>
 * {@code
 * @GetMapping("/my-profile")
 * public ResponseEntity<?> getMyProfile(@SessionMember SessionUser user) {
 *     // user 파라미터에 현재 로그인 사용자 정보가 자동으로 주입됨
 *     return ResponseEntity.ok(user.getNickname());
 * }
 * 
 * @PostMapping("/optional-api")
 * public ResponseEntity<?> optionalApi(@SessionMember(required = false) SessionUser user) {
 *     // 로그인하지 않은 경우 user는 null
 *     if (user != null) {
 *         return ResponseEntity.ok("로그인 사용자");
 *     } else {
 *         return ResponseEntity.ok("게스트 사용자");
 *     }
 * }
 * }
 * </pre>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface SessionMember {
    
    /**
     * 인증 필수 여부
     * @return true: 인증되지 않은 경우 MemberException(UNAUTHORIZED_ACCESS) 발생
     *         false: 인증되지 않은 경우 null 반환
     */
    boolean required() default true;
}