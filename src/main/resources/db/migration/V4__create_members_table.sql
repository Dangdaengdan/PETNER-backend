-- V4__create_members_table.sql
-- 멤버 테이블 생성

CREATE TYPE gender_enum AS ENUM ('MALE', 'FEMALE');
CREATE TYPE housing_type_enum AS ENUM ('아파트', '단독 주택', '빌라', '기타');

CREATE TABLE members (
    member_id BIGSERIAL PRIMARY KEY,
    kakao_id VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) UNIQUE,
    nickname VARCHAR(50) NOT NULL UNIQUE,
    gender gender_enum NOT NULL,
    housing_type housing_type_enum NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    contact VARCHAR(200) NOT NULL,
    location_id BIGINT NOT NULL,

    CONSTRAINT fk_members_location FOREIGN KEY (location_id) REFERENCES locations(location_id)
);