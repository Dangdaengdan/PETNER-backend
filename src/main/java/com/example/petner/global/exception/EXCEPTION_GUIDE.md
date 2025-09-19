# 예외 처리 및 표준 에러 응답 가이드

## 개요
이 문서는 PETNER 프로젝트의 예외 처리 시스템을 정리하여, 일관된 에러 응답 형식을 유지하고 예외에서 HTTP 응답으로의 매핑을 단순화하는 것을 목표로 합니다.

## 구성 요소

### 1. ErrorCode
- **유형**: Enum
- **목적**: 에러 식별자, 메시지, HTTP 상태 코드를 저장
- **위치**: `com.example.petner.global.exception.ErrorCode`

#### 현재 정의된 ErrorCode 목록

**인증/세션 관련**
```java
SESSION_EXPIRED("401-AU01", "세션이 만료되었습니다", HttpStatus.UNAUTHORIZED)
UNAUTHORIZED_ACCESS("401-AU02", "인증이 필요합니다", HttpStatus.UNAUTHORIZED)
KAKAO_AUTH_FAILED("401-AU03", "카카오 인증에 실패했습니다", HttpStatus.UNAUTHORIZED)
KAKAO_API_ERROR("500-AU04", "카카오 API 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR)
```

**서버 에러**
```java
GLOBAL_ERROR("500-GL01", "서버 오류", HttpStatus.INTERNAL_SERVER_ERROR)
DATABASE_ERROR("500-DB01", "데이터 베이스 오류", HttpStatus.INTERNAL_SERVER_ERROR)
```

**도메인별 에러**
```java
// 멤버 관련
MEMBER_NOT_FOUND("404-MB01", "사용자 정보를 찾을 수 없습니다", HttpStatus.NOT_FOUND)
MEMBER_ALREADY_EXISTS("409-MB02", "이미 존재하는 사용자입니다", HttpStatus.CONFLICT)
MEMBER_EMAIL_DUPLICATE("409-MB03", "이미 사용 중인 이메일입니다", HttpStatus.CONFLICT)

// 강아지 관련
DOG_NOT_FOUND("404-DG01", "강아지 정보를 찾을 수 없습니다", HttpStatus.NOT_FOUND)
DOG_ALREADY_ADOPTED("409-DG02", "이미 입양된 강아지입니다", HttpStatus.CONFLICT)
DOG_NOT_AVAILABLE("400-DG03", "입양 불가능한 강아지입니다", HttpStatus.BAD_REQUEST)

// 게시글 관련
POST_NOT_FOUND("404-PT01", "게시글을 찾을 수 없습니다", HttpStatus.NOT_FOUND)
POST_ACCESS_DENIED("403-PT02", "게시글에 대한 접근 권한이 없습니다", HttpStatus.FORBIDDEN)

// 댓글 관련
COMMENT_NOT_FOUND("404-CM01", "댓글을 찾을 수 없습니다", HttpStatus.NOT_FOUND)
COMMENT_ACCESS_DENIED("403-CM02", "댓글에 대한 접근 권한이 없습니다", HttpStatus.FORBIDDEN)

// 보호소 관련
SHELTER_NOT_FOUND("404-SH01", "보호소 정보를 찾을 수 없습니다", HttpStatus.NOT_FOUND)
SHELTER_ACCESS_DENIED("403-SH02", "보호소에 대한 접근 권한이 없습니다", HttpStatus.FORBIDDEN)

// 즐겨찾기 관련
FAVORITE_NOT_FOUND("404-FV01", "즐겨찾기를 찾을 수 없습니다", HttpStatus.NOT_FOUND)
FAVORITE_ALREADY_EXISTS("409-FV02", "이미 즐겨찾기에 추가된 항목입니다", HttpStatus.CONFLICT)

// 위치 관련
LOCATION_NOT_FOUND("404-LC01", "위치 정보를 찾을 수 없습니다", HttpStatus.NOT_FOUND)
LOCATION_INVALID("400-LC02", "유효하지 않은 위치 정보입니다", HttpStatus.BAD_REQUEST)

// 채팅 관련
CHAT_ROOM_NOT_FOUND("404-CH01", "채팅방을 찾을 수 없습니다", HttpStatus.NOT_FOUND)
CHAT_MEMBER_NOT_FOUND("404-CH02", "사용자 정보를 찾을 수 없습니다", HttpStatus.NOT_FOUND)
CHAT_INVALID_MESSAGE_TYPE("400-CH04", "유효하지 않은 메시지 타입입니다", HttpStatus.BAD_REQUEST)
CHAT_INVALID_PAYLOAD("400-CH05", "메시지 페이로드가 유효하지 않습니다", HttpStatus.BAD_REQUEST)
```

### 2. CustomException 클래스들
- **목적**: 도메인별 에러를 `ErrorCode`와 함께 캡슐화
- **위치**: `com.example.petner.global.exception.customException`

#### 현재 구현된 예외 클래스
```java
MemberException      // 멤버 관련 예외
DogException         // 강아지 관련 예외  
PostException        // 게시글 관련 예외
ChatException        // 채팅 관련 예외
ShelterException     // 보호소 관련 예외
```

### 3. GlobalExceptionHandler
- **유형**: `@RestControllerAdvice` 기반 전역 예외 처리기
- **목적**: 예외 처리를 중앙화하고 표준화된 HTTP 응답으로 매핑
- **위치**: `com.example.petner.global.exception.GlobalExceptionHandler`

### 4. ErrorPayload
- **목적**: 에러 응답 포맷 정의
- **구조**:
    - `code`: 에러 코드 식별자 (예: `404-MB01`, `500-GL01`)
    - `message`: 사람이 읽을 수 있는 에러 메시지

## 사용법

### 1. 서비스 코드에서 예외 발생
```java
@Service
public class MemberService {
    
    public MemberDto findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }
    
    public void createMember(CreateMemberRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new MemberException(ErrorCode.MEMBER_EMAIL_DUPLICATE);
        }
        // 멤버 생성 로직...
    }
}
```

### 2. 컨트롤러 코드 (예외 처리 불필요)
```java
@RestController
@RequestMapping("/api/members")
public class MemberController {
    
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberDto> getMember(@PathVariable Long memberId) {
        // 예외가 발생하면 GlobalExceptionHandler가 자동으로 처리
        MemberDto member = memberService.findMember(memberId);
        return ResponseEntity.ok(member);
    }
}
```

## 클라이언트 응답 예시

### 성공 응답
```json
{
  "memberId": 1,
  "email": "user@example.com",
  "nickname": "testuser"
}
```

### 에러 응답 예시

#### 404: 멤버를 찾을 수 없음
**HTTP 상태**: 404 NOT_FOUND
**응답 본문**:
```json
{
  "code": "404-MB01",
  "message": "사용자 정보를 찾을 수 없습니다"
}
```

#### 409: 이메일 중복
**HTTP 상태**: 409 CONFLICT  
**응답 본문**:
```json
{
  "code": "409-MB03",
  "message": "이미 사용 중인 이메일입니다"
}
```

#### 400: 유효성 검증 실패
**HTTP 상태**: 400 BAD_REQUEST
**응답 본문**:
```json
{
  "code": "400-VALIDATION",
  "message": "입력값 검증에 실패했습니다"
}
```

#### 500: 서버 내부 오류
**HTTP 상태**: 500 INTERNAL_SERVER_ERROR
**응답 본문**:
```json
{
  "code": "500-GL01",
  "message": "서버 오류"
}
```

## 에러 코드 네이밍 규칙

### 코드 형식
```
{HTTP_STATUS}-{DOMAIN}{SEQUENCE}
```

### 도메인 코드
- `AU`: Authentication (인증)
- `GL`: Global (전역)  
- `DB`: Database (데이터베이스)
- `MB`: Member (멤버)
- `DG`: Dog (강아지)
- `PT`: Post (게시글)
- `CM`: Comment (댓글)
- `SH`: Shelter (보호소)
- `FV`: Favorite (즐겨찾기)
- `LC`: Location (위치)
- `CH`: Chat (채팅)

### 예시
- `404-MB01`: 멤버 도메인의 첫 번째 404 에러
- `409-DG02`: 강아지 도메인의 두 번째 409 에러
- `500-GL01`: 전역 첫 번째 500 에러

## 새로운 도메인 예외 추가하기

### 1. ErrorCode에 에러 코드 추가
```java
// 새로운 도메인: 알림(Notification)
NOTIFICATION_NOT_FOUND("404-NT01", "알림을 찾을 수 없습니다", HttpStatus.NOT_FOUND),
NOTIFICATION_ALREADY_READ("409-NT02", "이미 읽은 알림입니다", HttpStatus.CONFLICT)
```

### 2. 커스텀 예외 클래스 생성
```java
@Getter
@RequiredArgsConstructor
public class NotificationException extends RuntimeException {
    private final ErrorCode errorCode;
}
```

### 3. GlobalExceptionHandler에 예외 처리 추가
```java
@ExceptionHandler({
        MemberException.class, 
        DogException.class, 
        PostException.class, 
        ChatException.class, 
        ShelterException.class,
        NotificationException.class  // 새로 추가
})
```

## 주의사항

### 1. 보안 고려사항
- 에러 메시지에 민감한 정보(DB 스키마, 내부 경로 등) 포함 금지
- 프로덕션 환경에서는 상세한 스택 트레이스 노출 방지

### 2. 로깅
- 비즈니스 예외: `log.warn()` 레벨로 기록
- 시스템 예외: `log.error()` 레벨로 기록
- 모든 예외에 요청 URI 포함하여 디버깅 지원

### 3. HTTP 상태 코드 가이드라인
- `400`: 클라이언트 요청 오류 (잘못된 파라미터, 유효성 검증 실패)
- `401`: 인증 필요 (로그인하지 않음)
- `403`: 권한 없음 (로그인했지만 접근 권한 없음)
- `404`: 리소스 없음
- `409`: 충돌 (중복된 데이터, 비즈니스 규칙 위반)
- `500`: 서버 내부 오류

### 4. 확장 가능성
- 새로운 도메인 추가시 위의 패턴을 따라 확장
- ErrorCode는 기능 구현과 함께 점진적으로 추가
- 과도한 세분화보다는 실용적인 수준에서 관리

## 개발자 참고

### Spring Boot 기본 예외들도 자동 처리됨
- `MethodArgumentNotValidException`: 유효성 검증 실패
- `HttpMessageNotReadableException`: JSON 파싱 오류  
- `MethodArgumentTypeMismatchException`: 타입 불일치
- `MissingServletRequestParameterException`: 필수 파라미터 누락
- `HttpRequestMethodNotSupportedException`: 지원하지 않는 HTTP 메서드
- `NoResourceFoundException`: 리소스 없음
- `AuthenticationException`: 인증 실패
- `AccessDeniedException`: 접근 거부
- `DataIntegrityViolationException`: 데이터 무결성 위반

모든 예외는 GlobalExceptionHandler에서 적절한 ErrorPayload 형태로 변환되어 클라이언트에게 전달됩니다.