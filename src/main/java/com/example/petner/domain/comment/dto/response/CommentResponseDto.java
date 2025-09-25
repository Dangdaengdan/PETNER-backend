package com.example.petner.domain.comment.dto.response;

import com.example.petner.domain.comment.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CommentResponseDto {

    private final Long commentId;
    private final String content;
    private final String authorNickname;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final Long parentCommentId;
    private final boolean isReply;
    private List<CommentResponseDto> replies;

    public CommentResponseDto(Comment comment) {
        this.commentId = comment.getCommentId();
        this.content = comment.getContent();
        this.authorNickname = comment.getMember().getNickname();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
        this.parentCommentId = comment.getParentComment() != null ? comment.getParentComment().getCommentId() : null;
        this.isReply = comment.isReply();
        this.replies = List.of();
    }

    public void setReplies(List<CommentResponseDto> replies) {
        this.replies = replies;
    }
}