package com.example.petner.domain.auth.client;

import com.example.petner.domain.auth.dto.KakaoUserInfo;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.MemberException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class KakaoApiClient implements OAuthApiClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;

    public KakaoApiClient(RestTemplate restTemplate,
                         ObjectMapper objectMapper,
                         @Value("${kakao.oauth.client-id}") String clientId,
                         @Value("${kakao.oauth.client-secret}") String clientSecret,
                         @Value("${kakao.oauth.redirect-uri}") String redirectUri) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
    }

    private static final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

    @Override
    public String getAccessToken(String authorizationCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("code", authorizationCode);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(KAKAO_TOKEN_URL, request, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                return jsonNode.get("access_token").asText();
            } else {
                log.warn("[카카오 API] 토큰 요청 실패: statusCode={}", response.getStatusCode());
                throw new MemberException(ErrorCode.KAKAO_AUTH_FAILED);
            }
        } catch (Exception e) {
            log.error("[카카오 API] 토큰 요청 중 시스템 오류 발생", e);
            throw new MemberException(ErrorCode.KAKAO_API_ERROR);
        }
    }

    @Override
    public KakaoUserInfo getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                KAKAO_USER_INFO_URL, 
                HttpMethod.GET, 
                request, 
                String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                return parseKakaoUserInfo(jsonNode);
            } else {
                log.warn("[카카오 API] 사용자 정보 요청 실패: statusCode={}", response.getStatusCode());
                throw new MemberException(ErrorCode.KAKAO_AUTH_FAILED);
            }
        } catch (Exception e) {
            log.error("[카카오 API] 사용자 정보 요청 중 시스템 오류 발생", e);
            throw new MemberException(ErrorCode.KAKAO_API_ERROR);
        }
    }

    private KakaoUserInfo parseKakaoUserInfo(JsonNode jsonNode) {
        try {
            if (jsonNode == null || jsonNode.get("id") == null) {
                throw new MemberException(ErrorCode.KAKAO_API_ERROR);
            }
            
            String kakaoId = jsonNode.get("id").asText();
            JsonNode kakaoAccount = jsonNode.get("kakao_account");
            
            if (kakaoAccount == null) {
                throw new MemberException(ErrorCode.KAKAO_API_ERROR);
            }
            
            JsonNode profile = kakaoAccount.get("profile");
            String email = kakaoAccount.has("email") ? kakaoAccount.get("email").asText() : null;
            String nickname = (profile != null && profile.has("nickname")) ? 
                profile.get("nickname").asText() : null;

            return KakaoUserInfo.builder()
                    .kakaoId(kakaoId)
                    .email(email)
                    .nickname(nickname)
                    .build();
        } catch (MemberException e) {
            throw e;
        } catch (Exception e) {
            log.error("[카카오 API] 응답 파싱 중 오류 발생", e);
            throw new MemberException(ErrorCode.KAKAO_API_ERROR);
        }
    }
}