-- V3__create_shelters_table.sql
-- 보호소 테이블 생성

CREATE TABLE shelters (
    shelter_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    detail_address VARCHAR(255) NOT NULL,
    shelter_contact VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    location_id BIGINT NOT NULL,

    CONSTRAINT fk_shelters_location FOREIGN KEY (location_id) REFERENCES locations(location_id)
);