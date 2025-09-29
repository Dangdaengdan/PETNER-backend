package com.example.petner.domain.auth.controller;

import com.example.petner.domain.auth.dto.response.LoginResponse;
import com.example.petner.domain.auth.dto.response.LogoutResponse;
import com.example.petner.domain.auth.dto.response.MemberResponse;
import com.example.petner.domain.auth.dto.response.SessionResponse;
import com.example.petner.domain.auth.service.AuthService;
import com.example.petner.global.dto.SessionUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.view.RedirectView;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        // AuthController에 필요한 값들을 수동으로 설정
        authController = new AuthController(
            authService,
            "test-client-id",
            "http://localhost:8080/api/v1/auth/kakao/callback"
        );
    }

    @Test
    @DisplayName("카카오 로그인 페이지로 리다이렉트")
    void kakaoLogin() {
        // When
        RedirectView result = authController.kakaoLogin();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUrl()).contains("https://kauth.kakao.com/oauth/authorize");
        assertThat(result.getUrl()).contains("client_id=test-client-id");
        assertThat(result.getUrl()).contains("response_type=code");
    }

    @Test
    @DisplayName("카카오 콜백 - 성공")
    void kakaoCallback_Success() {
        // Given
        String authorizationCode = "test-auth-code";
        MockHttpSession session = new MockHttpSession();

        MemberResponse memberResponse = MemberResponse.builder()
                .memberId(1L)
                .email("test@example.com")
                .nickname("테스트유저")
                .profileCompleted(true)
                .build();

        LoginResponse loginResponse = LoginResponse.builder()
                .message("로그인 성공")
                .member(memberResponse)
                .build();

        when(authService.kakaoLogin(anyString(), any())).thenReturn(loginResponse);

        // When
        RedirectView result = authController.kakaoCallback(authorizationCode, null, session);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUrl()).isEqualTo("http://localhost:3000/auth/kakao/callback?success=true");
    }

    @Test
    @DisplayName("카카오 콜백 - 에러 파라미터가 있는 경우")
    void kakaoCallback_WithError() {
        // Given
        MockHttpSession session = new MockHttpSession();

        // When
        RedirectView result = authController.kakaoCallback(null, "access_denied", session);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUrl()).isEqualTo("http://localhost:3000/auth/kakao/callback?error=access_denied");
    }

    @Test
    @DisplayName("카카오 콜백 - 인증 코드가 없는 경우")
    void kakaoCallback_NoCode() {
        // Given
        MockHttpSession session = new MockHttpSession();

        // When
        RedirectView result = authController.kakaoCallback(null, null, session);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUrl()).isEqualTo("http://localhost:3000/auth/kakao/callback?error=invalid_code");
    }

    @Test
    @DisplayName("카카오 콜백 - 빈 인증 코드")
    void kakaoCallback_EmptyCode() {
        // Given
        MockHttpSession session = new MockHttpSession();

        // When
        RedirectView result = authController.kakaoCallback("", null, session);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUrl()).isEqualTo("http://localhost:3000/auth/kakao/callback?error=invalid_code");
    }

    @Test
    @DisplayName("카카오 콜백 - 서비스 에러")
    void kakaoCallback_ServiceError() {
        // Given
        String authorizationCode = "test-auth-code";
        MockHttpSession session = new MockHttpSession();

        when(authService.kakaoLogin(anyString(), any())).thenThrow(new RuntimeException("서비스 에러"));

        // When
        RedirectView result = authController.kakaoCallback(authorizationCode, null, session);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUrl()).isEqualTo("http://localhost:3000/auth/kakao/callback?error=login_failed");
    }

    @Test
    @DisplayName("로그아웃 성공")
    void logout_Success() {
        // Given
        MockHttpSession session = new MockHttpSession();
        LogoutResponse logoutResponse = LogoutResponse.success();

        when(authService.logout(any())).thenReturn(logoutResponse);

        // When
        ResponseEntity<LogoutResponse> result = authController.logout(session);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(result.getBody()).isEqualTo(logoutResponse);
        assertThat(result.getBody().getMessage()).isEqualTo("로그아웃 성공");
    }

    @Test
    @DisplayName("현재 사용자 정보 조회 - 인증된 사용자")
    void getCurrentMember_Authenticated() {
        // Given
        SessionUser sessionUser = SessionUser.builder()
                .memberId(1L)
                .email("test@example.com")
                .nickname("테스트유저")
                .profileCompleted(true)
                .build();

        // When
        ResponseEntity<MemberResponse> result = authController.getCurrentMember(sessionUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getMemberId()).isEqualTo(1L);
        assertThat(result.getBody().getEmail()).isEqualTo("test@example.com");
        assertThat(result.getBody().getNickname()).isEqualTo("테스트유저");
        assertThat(result.getBody().isProfileCompleted()).isTrue();
    }

    @Test
    @DisplayName("세션 상태 확인 - 인증된 사용자")
    void checkAuthentication_Authenticated() {
        // Given
        SessionUser sessionUser = SessionUser.builder()
                .memberId(1L)
                .email("test@example.com")
                .nickname("테스트유저")
                .build();

        // When
        ResponseEntity<SessionResponse> result = authController.checkAuthentication(sessionUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().isAuthenticated()).isTrue();
    }

    @Test
    @DisplayName("세션 상태 확인 - 비인증 사용자")
    void checkAuthentication_Unauthenticated() {
        // When
        ResponseEntity<SessionResponse> result = authController.checkAuthentication(null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().isAuthenticated()).isFalse();
    }
}