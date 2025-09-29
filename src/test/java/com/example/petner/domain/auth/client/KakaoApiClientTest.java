package com.example.petner.domain.auth.client;

import com.example.petner.domain.auth.dto.KakaoUserInfo;
import com.example.petner.global.exception.customException.MemberException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KakaoApiClientTest {

    @Mock
    private RestTemplate restTemplate;

    private ObjectMapper objectMapper;
    private KakaoApiClient kakaoApiClient;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        kakaoApiClient = new KakaoApiClient(
                restTemplate,
                objectMapper,
                "test-client-id",
                "test-client-secret",
                "http://localhost:8080/callback"
        );
    }

    @Test
    @DisplayName("액세스 토큰 요청 성공")
    void getAccessToken_Success() {
        // Given
        String authorizationCode = "test-auth-code";
        String responseBody = """
            {
                "access_token": "test-access-token",
                "token_type": "bearer",
                "refresh_token": "test-refresh-token",
                "expires_in": 21599
            }
            """;

        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(responseEntity);

        // When
        String accessToken = kakaoApiClient.getAccessToken(authorizationCode);

        // Then
        assertThat(accessToken).isEqualTo("test-access-token");
    }

    @Test
    @DisplayName("액세스 토큰 요청 실패 - HTTP 에러")
    void getAccessToken_HttpError() {
        // Given
        String authorizationCode = "invalid-code";
        ResponseEntity<String> responseEntity = new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(responseEntity);

        // When & Then
        assertThatThrownBy(() -> kakaoApiClient.getAccessToken(authorizationCode))
                .isInstanceOf(MemberException.class);
    }

    @Test
    @DisplayName("액세스 토큰 요청 실패 - JSON 파싱 에러")
    void getAccessToken_JsonParseError() {
        // Given
        String authorizationCode = "test-auth-code";
        String invalidJsonResponse = "invalid json";

        ResponseEntity<String> responseEntity = new ResponseEntity<>(invalidJsonResponse, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(responseEntity);

        // When & Then
        assertThatThrownBy(() -> kakaoApiClient.getAccessToken(authorizationCode))
                .isInstanceOf(MemberException.class);
    }

    @Test
    @DisplayName("액세스 토큰 요청 실패 - 네트워크 에러")
    void getAccessToken_NetworkError() {
        // Given
        String authorizationCode = "test-auth-code";
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenThrow(new RuntimeException("Network error"));

        // When & Then
        assertThatThrownBy(() -> kakaoApiClient.getAccessToken(authorizationCode))
                .isInstanceOf(MemberException.class);
    }

    @Test
    @DisplayName("사용자 정보 요청 성공")
    void getUserInfo_Success() {
        // Given
        String accessToken = "test-access-token";
        String responseBody = """
            {
                "id": "12345",
                "connected_at": "2023-01-01T00:00:00Z",
                "kakao_account": {
                    "profile": {
                        "nickname": "테스트유저",
                        "profile_image_url": "http://example.com/profile.jpg"
                    },
                    "email": "test@example.com",
                    "is_email_valid": true,
                    "is_email_verified": true
                }
            }
            """;

        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
                .thenReturn(responseEntity);

        // When
        KakaoUserInfo userInfo = kakaoApiClient.getUserInfo(accessToken);

        // Then
        assertThat(userInfo).isNotNull();
        assertThat(userInfo.getKakaoId()).isEqualTo("12345");
        assertThat(userInfo.getEmail()).isEqualTo("test@example.com");
        assertThat(userInfo.getNickname()).isEqualTo("테스트유저");
    }

    @Test
    @DisplayName("사용자 정보 요청 성공 - 이메일 없음")
    void getUserInfo_Success_NoEmail() {
        // Given
        String accessToken = "test-access-token";
        String responseBody = """
            {
                "id": "12345",
                "connected_at": "2023-01-01T00:00:00Z",
                "kakao_account": {
                    "profile": {
                        "nickname": "테스트유저"
                    }
                }
            }
            """;

        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
                .thenReturn(responseEntity);

        // When
        KakaoUserInfo userInfo = kakaoApiClient.getUserInfo(accessToken);

        // Then
        assertThat(userInfo).isNotNull();
        assertThat(userInfo.getKakaoId()).isEqualTo("12345");
        assertThat(userInfo.getEmail()).isNull();
        assertThat(userInfo.getNickname()).isEqualTo("테스트유저");
    }

    @Test
    @DisplayName("사용자 정보 요청 성공 - 닉네임 없음")
    void getUserInfo_Success_NoNickname() {
        // Given
        String accessToken = "test-access-token";
        String responseBody = """
            {
                "id": "12345",
                "connected_at": "2023-01-01T00:00:00Z",
                "kakao_account": {
                    "email": "test@example.com"
                }
            }
            """;

        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
                .thenReturn(responseEntity);

        // When
        KakaoUserInfo userInfo = kakaoApiClient.getUserInfo(accessToken);

        // Then
        assertThat(userInfo).isNotNull();
        assertThat(userInfo.getKakaoId()).isEqualTo("12345");
        assertThat(userInfo.getEmail()).isEqualTo("test@example.com");
        assertThat(userInfo.getNickname()).isNull();
    }

    @Test
    @DisplayName("사용자 정보 요청 실패 - HTTP 에러")
    void getUserInfo_HttpError() {
        // Given
        String accessToken = "invalid-token";
        ResponseEntity<String> responseEntity = new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
                .thenReturn(responseEntity);

        // When & Then
        assertThatThrownBy(() -> kakaoApiClient.getUserInfo(accessToken))
                .isInstanceOf(MemberException.class);
    }

    @Test
    @DisplayName("사용자 정보 요청 실패 - ID 없는 응답")
    void getUserInfo_NoIdInResponse() {
        // Given
        String accessToken = "test-access-token";
        String responseBody = """
            {
                "connected_at": "2023-01-01T00:00:00Z",
                "kakao_account": {
                    "email": "test@example.com"
                }
            }
            """;

        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
                .thenReturn(responseEntity);

        // When & Then
        assertThatThrownBy(() -> kakaoApiClient.getUserInfo(accessToken))
                .isInstanceOf(MemberException.class);
    }

    @Test
    @DisplayName("사용자 정보 요청 실패 - kakao_account 없는 응답")
    void getUserInfo_NoKakaoAccountInResponse() {
        // Given
        String accessToken = "test-access-token";
        String responseBody = """
            {
                "id": "12345",
                "connected_at": "2023-01-01T00:00:00Z"
            }
            """;

        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
                .thenReturn(responseEntity);

        // When & Then
        assertThatThrownBy(() -> kakaoApiClient.getUserInfo(accessToken))
                .isInstanceOf(MemberException.class);
    }

    @Test
    @DisplayName("사용자 정보 요청 실패 - JSON 파싱 에러")
    void getUserInfo_JsonParseError() {
        // Given
        String accessToken = "test-access-token";
        String invalidJsonResponse = "invalid json";

        ResponseEntity<String> responseEntity = new ResponseEntity<>(invalidJsonResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
                .thenReturn(responseEntity);

        // When & Then
        assertThatThrownBy(() -> kakaoApiClient.getUserInfo(accessToken))
                .isInstanceOf(MemberException.class);
    }

    @Test
    @DisplayName("사용자 정보 요청 실패 - 네트워크 에러")
    void getUserInfo_NetworkError() {
        // Given
        String accessToken = "test-access-token";
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
                .thenThrow(new RuntimeException("Network error"));

        // When & Then
        assertThatThrownBy(() -> kakaoApiClient.getUserInfo(accessToken))
                .isInstanceOf(MemberException.class);
    }
}