-- V2__create_breeds_table.sql
-- 견종 테이블 생성

CREATE TABLE breeds (
    breedId BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);