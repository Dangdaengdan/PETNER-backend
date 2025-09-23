# 🔐 PETNER 인증 시스템 (Kakao OAuth Session-based)

## 📋 개요
카카오 OAuth 2.0을 이용한 세션 기반 인증 시스템입니다. JWT 토큰 방식이 아닌 Redis Session을 활용한 전통적인 세션 인증을 구현했습니다.

## 🏗️ 아키텍처

### 전체 플로우
```
1. 카카오 로그인 요청 (/api/v1/auth/kakao/login)
2. 카카오 OAuth 인증 페이지로 리다이렉트
3. 사용자 로그인 후 콜백 (/api/v1/auth/kakao/callback)
4. 카카오 API에서 사용자 정보 조회
5. 임시 회원 생성 (kakaoId만 저장)
6. 세션에 memberId 저장
7. 프로필 완성 여부에 따라 추가 정보 입력 안내
```

### 핵심 설계 원칙
- **SOLID 원칙 준수**: OAuthApiClient 인터페이스를 통한 DIP 적용
- **트랜잭션 경계 최적화**: 외부 API 호출과 DB 작업 분리
- **보안 강화**: 민감 정보 마스킹 및 최소 정보 노출
- **근본적 해결**: 임시방편이 아닌 구조적 개선

## 📁 구조

```
domain/auth/
├── controller/
│   └── AuthController.java       # REST API 엔드포인트
├── service/
│   └── AuthService.java          # 비즈니스 로직
├── client/
│   ├── OAuthApiClient.java       # OAuth 클라이언트 인터페이스
│   └── KakaoApiClient.java       # 카카오 API 구현체
├── dto/
│   ├── KakaoUserInfo.java        # 카카오 사용자 정보 DTO
│   └── response/
│       ├── LoginResponse.java    # 로그인 응답 DTO
│       ├── LogoutResponse.java   # 로그아웃 응답 DTO
│       ├── MemberResponse.java   # 회원 정보 응답 DTO
│       └── SessionResponse.java  # 세션 상태 응답 DTO
└── README.md                     # 이 문서

global/config/
└── RestTemplateConfig.java       # HTTP 클라이언트 설정
```

## 🔧 구현된 기능

### 0. RestTemplateConfig (Global 공통 설정)
**역할:**
- HTTP 클라이언트 및 JSON 처리 Bean 제공
- 여러 도메인에서 공통으로 사용할 수 있는 인프라 설정

**제공하는 Bean:**
```java
@Bean
public RestTemplate restTemplate() {
    return new RestTemplate();  // 외부 API 호출용 HTTP 클라이언트
}

@Bean  
public ObjectMapper objectMapper() {
    return new ObjectMapper();  // JSON ↔ Java 객체 변환 처리
}
```

**사용 위치:**
- `KakaoApiClient` 생성자에서 의존성 주입
- 카카오 OAuth 토큰 요청/응답 처리
- 카카오 사용자 정보 API 응답 파싱
- 향후 다른 외부 API 호출 시에도 재사용 가능

**Global 위치 이유:**
- 진정한 공통 인프라 설정으로 여러 도메인에서 활용
- RestTemplate과 ObjectMapper는 범용적 도구

### 1. AuthController
**엔드포인트:**
- `GET /api/v1/auth/kakao/login` - 카카오 로그인 페이지로 리다이렉트
- `GET /api/v1/auth/kakao/callback` - 카카오 콜백 처리 및 로그인
- `POST /api/v1/auth/kakao/logout` - 로그아웃 (세션 무효화)
- `GET /api/v1/auth/kakao/member` - 현재 로그인된 회원 정보 조회
- `GET /api/v1/auth/kakao/session` - 세션 상태 확인

**주요 특징:**
- 생성자 주입을 통한 의존성 관리
- 인증 코드 검증 및 에러 처리
- 민감 정보 마스킹 (authorizationCode 로깅 시)

### 2. AuthService
**핵심 메서드:**
- `kakaoLogin()` - 카카오 로그인 처리 (외부 API 호출)
- `findOrCreateMemberWithTransaction()` - DB 작업만 트랜잭션 처리
- `logout()` - 안전한 세션 무효화
- `getCurrentMember()` - 세션 기반 회원 정보 조회
- `getSessionStatus()` - 인증 상태 확인

**최적화된 트랜잭션 설계:**
```java
// 외부 API 호출은 트랜잭션 밖에서
public LoginResponse kakaoLogin(String authorizationCode, HttpSession session) {
    String accessToken = oAuthApiClient.getAccessToken(authorizationCode);
    KakaoUserInfo kakaoUserInfo = oAuthApiClient.getUserInfo(accessToken);
    
    // DB 작업만 트랜잭션으로 처리
    Member member = findOrCreateMemberWithTransaction(kakaoUserInfo);
    // ...
}

@Transactional
protected Member findOrCreateMemberWithTransaction(KakaoUserInfo kakaoUserInfo) {
    return findOrCreateMember(kakaoUserInfo);
}
```

### 3. KakaoApiClient
**SOLID 원칙 적용:**
- `OAuthApiClient` 인터페이스 구현으로 DIP 준수
- 생성자 주입을 통한 의존성 관리
- 포괄적 예외 처리 및 로깅

**안전한 API 호출:**
```java
// JSON 파싱 안전성 확보
private KakaoUserInfo parseKakaoUserInfo(JsonNode jsonNode) {
    if (jsonNode == null || jsonNode.get("id") == null) {
        throw new MemberException(ErrorCode.KAKAO_API_ERROR);
    }
    // ...
}
```

### 4. Member Entity 최적화
**임시 회원 생성 패턴:**
```java
public static Member createTemporaryMember(String kakaoId) {
    return Member.builder()
            .kakaoId(kakaoId)  // 카카오 ID만 저장
            .profileCompleted(false)
            .build();
}
```

**프로필 완성 검증:**
```java
public boolean isProfileComplete() {
    return profileCompleted != null && profileCompleted 
            && nickname != null     // 사용자 입력
            && email != null        // 사용자 입력  
            && gender != null 
            && housingType != null 
            && contact != null 
            && location != null;
}
```

### 5. 보안 강화
**민감 정보 보호:**
- 카카오 ID 마스킹: `1234****`
- 인증 코드 마스킹: `abcd****`
- 로그인 응답에서 최소 정보만 제공

**MemberResponse 설계:**
```java
// 로그인 시 최소 정보만 제공
public static MemberResponse forLogin(Member member) {
    return MemberResponse.builder()
            .memberId(member.getMemberId())
            .profileCompleted(member.isProfileComplete())
            .build();
}
```

## 🚧 추가 작업 필요 사항

### 1. 프로필 완성 API 구현
```java
// 구현 필요
PUT /api/v1/members/profile
{
    "nickname": "사용자닉네임",
    "email": "user@example.com",
    "gender": "MALE",
    "housingType": "APARTMENT", 
    "contact": "010-1234-5678",
    "locationId": 1
}
```

### 2. 닉네임 중복 검사 API
```java
// 구현 필요
GET /api/v1/members/nickname/check?nickname=원하는닉네임
```

### 3. 이메일 중복 검사 API
```java
// 구현 필요  
GET /api/v1/members/email/check?email=user@example.com
```

### 4. 세션 관리 강화
- 세션 타임아웃 설정
- 동시 로그인 제한 (옵션)
- 세션 하이재킹 방지

### 5. 예외 처리 개선
- 카카오 API 오류별 세분화된 처리
- 사용자 친화적 에러 메시지
- 재시도 로직 (네트워크 오류 시)

### 6. 테스트 코드 작성
- 단위 테스트 (Service, Controller)
- 통합 테스트 (API 엔드포인트)
- Mock을 활용한 외부 API 테스트

### 7. API 문서화
- Swagger/OpenAPI 설정
- 요청/응답 예시 문서화
- 에러 코드 정의서

## 🔒 보안 고려사항

### 현재 적용된 보안
- ✅ 민감 정보 마스킹
- ✅ 세션 기반 인증
- ✅ 최소 권한 원칙 (최소 정보만 노출)

### 추가 검토 필요
- CSRF 보호 설정
- 세션 고정 공격 방지
- Rate Limiting (API 호출 제한)
- 로그인 시도 제한

## 📝 설정 파일
**.env 파일 필수 환경변수:**
```env
KAKAO_CLIENT_ID=your_kakao_client_id
KAKAO_CLIENT_SECRET=your_kakao_client_secret  
KAKAO_REDIRECT_URI=http://localhost:8080/api/v1/auth/kakao/callback
```

## 🎯 다음 우선순위
1. **프로필 완성 API 구현** (가장 우선)
2. **닉네임/이메일 중복 검사** 
3. **테스트 코드 작성**
4. **API 문서화**

---
*마지막 업데이트: 2025-09-20*
*작성자: Claude Code Assistant*