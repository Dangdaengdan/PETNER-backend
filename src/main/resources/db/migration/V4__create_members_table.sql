-- V4__create_members_table.sql
-- 멤버 테이블 생성

CREATE TYPE gender_enum AS ENUM ('MALE', 'FEMALE');
CREATE TYPE housing_type_enum AS ENUM ('아파트', '단독 주택', '빌라', '기타');

CREATE TABLE members (
    memberId BIGSERIAL PRIMARY KEY,
    kakaoId VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) UNIQUE,
    nickname VARCHAR(50) NOT NULL UNIQUE,
    gender gender_enum NOT NULL,
    housingType housing_type_enum NOT NULL,
    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    contact VARCHAR(200) NOT NULL,
    locationId BIGINT NOT NULL,

    CONSTRAINT fk_members_location FOREIGN KEY (locationId) REFERENCES locations(locationId)
);