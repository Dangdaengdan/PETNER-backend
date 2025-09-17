-- V1__create_locations_table.sql
-- 지역 테이블 생성 (다른 테이블들이 참조하므로 먼저 생성)

CREATE TABLE locations (
    locationId BIGSERIAL PRIMARY KEY,
    state VARCHAR(50) NOT NULL,
    district VARCHAR(50) NOT NULL,

    CONSTRAINT unique_location UNIQUE (state, district)
);