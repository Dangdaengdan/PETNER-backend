-- V8__create_favorites_table.sql
-- 관심 목록 테이블 생성

CREATE TABLE favorites (
    favorite_id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    member_id BIGINT NOT NULL,
    dog_id BIGINT NOT NULL,

    CONSTRAINT fk_favorites_member FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE,
    CONSTRAINT fk_favorites_dog FOREIGN KEY (dog_id) REFERENCES dogs(dog_id) ON DELETE CASCADE,
    CONSTRAINT unique_member_dog UNIQUE (member_id, dog_id)
);