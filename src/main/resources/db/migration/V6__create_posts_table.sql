-- V6__create_posts_table.sql
-- 게시글 테이블 생성

CREATE TABLE posts (
    postId BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    viewCount INTEGER NOT NULL DEFAULT 0,
    thumbImageUrl VARCHAR(500),
    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    authorId BIGINT NOT NULL,

    CONSTRAINT fk_posts_author FOREIGN KEY (authorId) REFERENCES members(memberId) ON DELETE CASCADE
);