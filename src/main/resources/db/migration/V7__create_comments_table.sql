-- V7__create_comments_table.sql
-- 댓글 테이블 생성

CREATE TABLE comments (
    commentId BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    postId BIGINT NOT NULL,
    memberId BIGINT NOT NULL,
    parentCommentId BIGINT,

    CONSTRAINT fk_comments_post FOREIGN KEY (postId) REFERENCES posts(postId) ON DELETE CASCADE,
    CONSTRAINT fk_comments_member FOREIGN KEY (memberId) REFERENCES members(memberId) ON DELETE CASCADE,
    CONSTRAINT fk_comments_parent FOREIGN KEY (parentCommentId) REFERENCES comments(commentId)
);