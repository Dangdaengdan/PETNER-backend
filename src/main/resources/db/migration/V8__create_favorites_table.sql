-- V8__create_favorites_table.sql
-- 관심 목록 테이블 생성

CREATE TABLE favorites (
    favoriteId BIGSERIAL PRIMARY KEY,
    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    memberId BIGINT NOT NULL,
    dogId BIGINT NOT NULL,

    CONSTRAINT fk_favorites_member FOREIGN KEY (memberId) REFERENCES members(memberId) ON DELETE CASCADE,
    CONSTRAINT fk_favorites_dog FOREIGN KEY (dogId) REFERENCES dogs(dogId) ON DELETE CASCADE,
    CONSTRAINT unique_member_dog UNIQUE (memberId, dogId)
);