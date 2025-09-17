-- V6__create_posts_table.sql
-- 게시글 테이블 생성

CREATE TABLE posts (
    post_id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    view_count INTEGER NOT NULL DEFAULT 0,
    thumb_image_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    author_id BIGINT NOT NULL,

    CONSTRAINT fk_posts_author FOREIGN KEY (author_id) REFERENCES members(member_id) ON DELETE CASCADE
);