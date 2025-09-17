-- V3__create_shelters_table.sql
-- 보호소 테이블 생성

CREATE TABLE shelters (
    shelterId BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    detailAddress VARCHAR(255) NOT NULL,
    shelterContact VARCHAR(50) NOT NULL,
    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    locationId BIGINT NOT NULL,

    CONSTRAINT fk_shelters_location FOREIGN KEY (locationId) REFERENCES locations(locationId)
);