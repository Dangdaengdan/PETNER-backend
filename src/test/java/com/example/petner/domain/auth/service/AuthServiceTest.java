package com.example.petner.domain.auth.service;

import com.example.petner.domain.auth.client.OAuthApiClient;
import com.example.petner.domain.auth.dto.KakaoUserInfo;
import com.example.petner.domain.auth.dto.response.LoginResponse;
import com.example.petner.domain.auth.dto.response.LogoutResponse;
import com.example.petner.domain.auth.dto.response.MemberResponse;
import com.example.petner.domain.auth.dto.response.SessionResponse;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.member.service.MemberService;
import com.example.petner.global.dto.SessionUser;
import com.example.petner.global.util.SessionUtils;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private OAuthApiClient oAuthApiClient;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("카카오 로그인 성공")
    void kakaoLogin_Success() {
        // Given
        String authorizationCode = "test-auth-code";
        String accessToken = "test-access-token";
        HttpSession session = new MockHttpSession();

        KakaoUserInfo kakaoUserInfo = KakaoUserInfo.builder()
                .kakaoId("12345")
                .email("test@example.com")
                .nickname("테스트유저")
                .build();

        Member member = Member.builder()
                .kakaoId("12345")
                .email("test@example.com")
                .nickname("테스트유저")
                .build();
        ReflectionTestUtils.setField(member, "memberId", 1L);

        when(oAuthApiClient.getAccessToken(authorizationCode)).thenReturn(accessToken);
        when(oAuthApiClient.getUserInfo(accessToken)).thenReturn(kakaoUserInfo);
        when(memberService.findOrCreateMember(kakaoUserInfo)).thenReturn(member);

        try (MockedStatic<SessionUtils> sessionUtils = mockStatic(SessionUtils.class)) {
            // When
            LoginResponse response = authService.kakaoLogin(authorizationCode, session);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getMessage()).isEqualTo("로그인 성공");
            assertThat(response.getMember()).isNotNull();
            assertThat(response.getMember().getMemberId()).isEqualTo(1L);
            assertThat(response.getMember().isProfileCompleted()).isFalse(); // forLogin은 최소 정보만 제공

            verify(oAuthApiClient).getAccessToken(authorizationCode);
            verify(oAuthApiClient).getUserInfo(accessToken);
            verify(memberService).findOrCreateMember(kakaoUserInfo);
            sessionUtils.verify(() -> SessionUtils.setSessionUser(session, member));
        }
    }

    @Test
    @DisplayName("카카오 로그인 실패 - OAuth API 에러")
    void kakaoLogin_OAuthApiError() {
        // Given
        String authorizationCode = "invalid-code";
        HttpSession session = new MockHttpSession();

        when(oAuthApiClient.getAccessToken(authorizationCode))
                .thenThrow(new RuntimeException("카카오 API 에러"));

        // When & Then
        try {
            authService.kakaoLogin(authorizationCode, session);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("카카오 API 에러");
        }

        verify(oAuthApiClient).getAccessToken(authorizationCode);
        verify(oAuthApiClient, never()).getUserInfo(anyString());
        verify(memberService, never()).findOrCreateMember(any());
    }

    @Test
    @DisplayName("로그아웃 성공 - 인증된 사용자")
    void logout_AuthenticatedUser() {
        // Given
        HttpSession session = new MockHttpSession();
        Long memberId = 1L;

        try (MockedStatic<SessionUtils> sessionUtils = mockStatic(SessionUtils.class)) {
            sessionUtils.when(() -> SessionUtils.getCurrentMemberId(session))
                    .thenReturn(memberId);

            // When
            LogoutResponse response = authService.logout(session);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getMessage()).isEqualTo("로그아웃 성공");
            assertThat(response.getMessage()).isEqualTo("로그아웃 성공");

            sessionUtils.verify(() -> SessionUtils.getCurrentMemberId(session));
            sessionUtils.verify(() -> SessionUtils.clearSession(session));
        }
    }

    @Test
    @DisplayName("로그아웃 성공 - 비인증 사용자")
    void logout_UnauthenticatedUser() {
        // Given
        HttpSession session = new MockHttpSession();

        try (MockedStatic<SessionUtils> sessionUtils = mockStatic(SessionUtils.class)) {
            sessionUtils.when(() -> SessionUtils.getCurrentMemberId(session))
                    .thenReturn(null);

            // When
            LogoutResponse response = authService.logout(session);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getMessage()).isEqualTo("로그아웃 성공");
            assertThat(response.getMessage()).isEqualTo("로그아웃 성공");

            sessionUtils.verify(() -> SessionUtils.getCurrentMemberId(session));
            sessionUtils.verify(() -> SessionUtils.clearSession(session));
        }
    }

    @Test
    @DisplayName("로그아웃 - 세션 무효화 실패")
    void logout_SessionClearFailure() {
        // Given
        HttpSession session = new MockHttpSession();

        try (MockedStatic<SessionUtils> sessionUtils = mockStatic(SessionUtils.class)) {
            sessionUtils.when(() -> SessionUtils.getCurrentMemberId(session))
                    .thenThrow(new RuntimeException("세션 에러"));

            // When
            LogoutResponse response = authService.logout(session);

            // Then - 클라이언트에게는 성공으로 응답
            assertThat(response).isNotNull();
            assertThat(response.getMessage()).isEqualTo("로그아웃 성공");
            assertThat(response.getMessage()).isEqualTo("로그아웃 성공");
        }
    }

    @Test
    @DisplayName("현재 사용자 정보 조회 - 세션 캐시 사용")
    void getCurrentMember_FromSessionCache() {
        // Given
        HttpSession session = new MockHttpSession();
        SessionUser sessionUser = SessionUser.builder()
                .memberId(1L)
                .email("test@example.com")
                .nickname("테스트유저")
                .profileCompleted(true)
                .build();

        try (MockedStatic<SessionUtils> sessionUtils = mockStatic(SessionUtils.class)) {
            sessionUtils.when(() -> SessionUtils.getCurrentUser(session))
                    .thenReturn(Optional.of(sessionUser));

            // When
            MemberResponse response = authService.getCurrentMember(session);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getMemberId()).isEqualTo(1L);
            assertThat(response.getEmail()).isEqualTo("test@example.com");
            assertThat(response.getNickname()).isEqualTo("테스트유저");
            assertThat(response.isProfileCompleted()).isTrue();

            sessionUtils.verify(() -> SessionUtils.getCurrentUser(session));
            verify(memberService, never()).findById(any());
        }
    }

    @Test
    @DisplayName("현재 사용자 정보 조회 - DB 폴백")
    void getCurrentMember_FallbackToDatabase() {
        // Given
        HttpSession session = new MockHttpSession();
        Long memberId = 1L;

        Member member = Member.builder()
                .kakaoId("12345")
                .email("test@example.com")
                .nickname("테스트유저")
                .build();
        ReflectionTestUtils.setField(member, "memberId", memberId);

        try (MockedStatic<SessionUtils> sessionUtils = mockStatic(SessionUtils.class)) {
            sessionUtils.when(() -> SessionUtils.getCurrentUser(session))
                    .thenReturn(Optional.empty());
            sessionUtils.when(() -> SessionUtils.requireCurrentMemberId(session))
                    .thenReturn(memberId);

            when(memberService.findById(memberId)).thenReturn(member);

            // When
            MemberResponse response = authService.getCurrentMember(session);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getMemberId()).isEqualTo(1L);
            assertThat(response.getEmail()).isEqualTo("test@example.com");
            assertThat(response.getNickname()).isEqualTo("테스트유저");

            sessionUtils.verify(() -> SessionUtils.getCurrentUser(session));
            sessionUtils.verify(() -> SessionUtils.requireCurrentMemberId(session));
            verify(memberService).findById(memberId);
        }
    }

    @Test
    @DisplayName("세션 상태 확인 - 인증된 사용자")
    void getSessionStatus_Authenticated() {
        // Given
        HttpSession session = new MockHttpSession();

        try (MockedStatic<SessionUtils> sessionUtils = mockStatic(SessionUtils.class)) {
            sessionUtils.when(() -> SessionUtils.isAuthenticated(session))
                    .thenReturn(true);

            // When
            SessionResponse response = authService.getSessionStatus(session);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.isAuthenticated()).isTrue();

            sessionUtils.verify(() -> SessionUtils.isAuthenticated(session));
        }
    }

    @Test
    @DisplayName("세션 상태 확인 - 비인증 사용자")
    void getSessionStatus_Unauthenticated() {
        // Given
        HttpSession session = new MockHttpSession();

        try (MockedStatic<SessionUtils> sessionUtils = mockStatic(SessionUtils.class)) {
            sessionUtils.when(() -> SessionUtils.isAuthenticated(session))
                    .thenReturn(false);

            // When
            SessionResponse response = authService.getSessionStatus(session);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.isAuthenticated()).isFalse();

            sessionUtils.verify(() -> SessionUtils.isAuthenticated(session));
        }
    }
}