# PET:NER 🐕

유기견 입양을 돕는 플랫폼 백엔드 서비스

## 📋 목차

- [프로젝트 소개](#-프로젝트-소개)
- [주요 기능](#-주요-기능)
- [기술 스택](#-기술-스택)
- [시작하기](#-시작하기)
- [환경 설정](#-환경-설정)
- [API 문서](#-api-문서)
- [프로젝트 구조](#-프로젝트-구조)
- [팀원 소개](#-팀원-소개)

## 🐾 프로젝트 소개

PET:NER는 유기견 입양을 돕기 위한 플랫폼입니다. 보호소의 유기견 정보를 제공하고, 입양 신청 및 커뮤니티 기능을 통해 유기견과 새로운 가족을 연결합니다.

## 👥 팀원 소개

<div align="center">

| 팀장<br/>이선민 | 팀원<br/>임채현 | 팀원<br/>남서현 | 팀원<br/>배수하 |
|:---:|:---:|:---:|:---:|
| <img src="https://github.com/sunm2n.png" width="120" height="120"> | <img src="https://github.com/Chaehyunli.png" width="120" height="120"> | <img src="https://github.com/DOOYEE0709.png" width="120" height="120"> | <img src="https://github.com/shshbb9160.png" width="120" height="120"> |
| [@sunm2n](https://github.com/sunm2n) | [@Chaehyunli](https://github.com/Chaehyunli) | [@DOOYEE0709](https://github.com/DOOYEE0709) | [@shshbb9160](https://github.com/shshbb9160) |

</div>

## ✨ 주요 기능

### 인증/인가
- 카카오 소셜 로그인
- 세션 기반 인증 (Redis)

### 유기견 관리
- 보호소 정보 조회
- 유기견 정보 조회 및 검색 (OpenSearch)
- 견종 정보 관리
- 입양 신청 및 관리

### 커뮤니티
- 게시글 작성/수정/삭제
- 댓글 기능
- 좋아요/즐겨찾기 기능
- 이미지 업로드 (Google Cloud Storage)

### 실시간 채팅
- WebSocket 기반 1:1 실시간 채팅
- 채팅방 생성 및 관리
- 메시지 전송/수신

### 검색
- OpenSearch를 활용한 고성능 검색 기능
- 유기견 정보 전문 검색

## 🛠 기술 스택

### Backend
- **Java 17**
- **Spring Boot 3.5.5**
- **Spring Security** - 인증/인가
- **Spring Data JPA** - ORM
- **Spring WebSocket** - 실시간 통신

### Database
- **PostgreSQL** - 메인 데이터베이스
- **Redis** - 세션 저장소 및 캐시
- **Flyway** - 데이터베이스 마이그레이션

### Search & Storage
- **OpenSearch** - 검색 엔진
- **Google Cloud Storage** - 이미지 저장소

### Documentation
- **Swagger/OpenAPI** - API 문서 자동화

### Build Tool
- **Gradle** - 빌드 및 의존성 관리

## 🚀 시작하기

### 필수 요구사항

- Java 17 이상
- PostgreSQL 13 이상
- Redis 6 이상
- OpenSearch 2.11 이상
- Google Cloud Platform 계정 (GCS 사용)

### 설치 및 실행

1. **저장소 클론**
```bash
git clone https://github.com/your-organization/PETNER_backend.git
cd PETNER_backend
```

2. **환경 변수 설정**
```bash
cp .env.example .env
# .env 파일을 편집하여 필요한 환경 변수 설정
```

3. **데이터베이스 생성**
```bash
# PostgreSQL 데이터베이스 생성
createdb petnerdb
```

4. **애플리케이션 빌드 및 실행**
```bash
# 빌드
./gradlew build

# 실행
./gradlew bootRun
```

애플리케이션은 기본적으로 `http://localhost:8080`에서 실행됩니다.

## ⚙️ 환경 설정

### 필수 환경 변수

`.env` 파일에 다음 환경 변수를 설정해야 합니다:

#### Database
```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/petnerdb
SPRING_DATASOURCE_USERNAME=your_postgres_username
SPRING_DATASOURCE_PASSWORD=your_postgres_password
```

#### Redis
```properties
SPRING_REDIS_CACHE_HOST=localhost
SPRING_REDIS_CACHE_PORT=6380
```

#### 카카오 OAuth2 설정
카카오 개발자 콘솔에서 애플리케이션을 등록하고 Client ID와 Secret을 발급받아 설정합니다.

#### Google Cloud Storage 설정
GCP 콘솔에서 서비스 계정을 생성하고 키 파일을 발급받아 프로젝트에 설정합니다.

#### OpenSearch 설정
OpenSearch 서버의 호스트와 포트 정보를 설정합니다.

### Docker를 이용한 개발 환경 구성 (선택사항)

```bash
# PostgreSQL
docker run -d \
  --name petner-postgres \
  -e POSTGRES_DB=petnerdb \
  -e POSTGRES_USER=petner \
  -e POSTGRES_PASSWORD=petner \
  -p 5432:5432 \
  postgres:15

# Redis
docker run -d \
  --name petner-redis \
  -p 6380:6379 \
  redis:7-alpine

# OpenSearch
docker run -d \
  --name petner-opensearch \
  -e "discovery.type=single-node" \
  -e "DISABLE_SECURITY_PLUGIN=true" \
  -p 9200:9200 \
  opensearchproject/opensearch:2.11.0
```

## 📚 API 문서

애플리케이션 실행 후 Swagger UI를 통해 API 문서를 확인할 수 있습니다.

```
http://localhost:8080/swagger-ui.html
```

## 📁 프로젝트 구조

```
src/
├── main/
│   ├── java/com/example/petner/
│   │   ├── domain/
│   │   │   ├── auth/          # 인증/인가
│   │   │   ├── member/        # 회원 관리
│   │   │   ├── dog/           # 유기견 정보
│   │   │   ├── dogApply/      # 입양 신청
│   │   │   ├── shelter/       # 보호소 관리
│   │   │   ├── breed/         # 견종 정보
│   │   │   ├── post/          # 게시글
│   │   │   ├── comment/       # 댓글
│   │   │   ├── like/          # 좋아요
│   │   │   ├── favorite/      # 즐겨찾기
│   │   │   ├── chat/          # 실시간 채팅
│   │   │   ├── location/      # 위치 정보
│   │   │   └── upload/        # 파일 업로드
│   │   ├── global/            # 전역 설정 및 유틸리티
│   │   └── PetnerApplication.java
│   └── resources/
│       ├── db/migration/      # Flyway 마이그레이션
│       └── application.yml    # 애플리케이션 설정
└── test/                      # 테스트 코드
```

## 🧪 테스트

```bash
# 전체 테스트 실행
./gradlew test

# 특정 테스트 실행
./gradlew test --tests "com.example.petner.domain.post.*"
```

## 📝 라이선스

All Rights Reserved

---

**PET:NER** - 유기견에게 새로운 가족을 찾아주세요 🐕💙
