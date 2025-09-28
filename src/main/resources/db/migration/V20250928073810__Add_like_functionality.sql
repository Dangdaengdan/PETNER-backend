-- V20250928073810__Add_like_functionality.sql
-- 좋아요 기능 추가: post_likes 테이블 생성 및 posts 테이블에 like_count 컬럼 추가

-- 1. 게시물 좋아요 테이블 생성
CREATE TABLE post_likes (
    like_id BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- 복합 유니크 제약조건: 한 사용자는 하나의 게시물에 한 번만 좋아요 가능
    CONSTRAINT uk_post_likes_member_post UNIQUE (member_id, post_id),

    -- 외래키 제약조건
    CONSTRAINT fk_post_likes_member FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE,
    CONSTRAINT fk_post_likes_post FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE
);

-- 2. posts 테이블에 좋아요 개수 컬럼 추가
ALTER TABLE posts
ADD COLUMN like_count INTEGER NOT NULL DEFAULT 0;

-- 3. 인덱스 생성 (성능 최적화)
CREATE INDEX idx_post_likes_member_id ON post_likes(member_id);
CREATE INDEX idx_post_likes_post_id ON post_likes(post_id);

-- 4. 기존 게시물의 좋아요 개수 초기화 (0으로 설정)
UPDATE posts SET like_count = 0 WHERE like_count IS NULL;