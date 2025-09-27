package com.example.petner.domain.comment.service;

import com.example.petner.domain.comment.dto.response.CommentResponseDto;
import com.example.petner.domain.comment.entity.Comment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 댓글 조회 관련 헬퍼 클래스
 * Single Responsibility Principle(SRP)을 준수하여 댓글 변환 로직만 담당
 */
@Component
public class CommentQueryHelper {

    /**
     * 댓글 목록과 답글 목록을 계층 구조로 변환
     */
    public List<CommentResponseDto> buildCommentHierarchy(List<Comment> comments, List<Comment> replies) {
        // 답글을 부모 댓글 ID별로 그룹화
        Map<Long, List<CommentResponseDto>> repliesMap = replies.stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getParentComment().getCommentId(),
                        Collectors.mapping(CommentResponseDto::new, Collectors.toList())
                ));

        // 부모 댓글에 답글 연결
        return comments.stream()
                .filter(comment -> !comment.isReply()) // 부모 댓글만
                .map(comment -> {
                    CommentResponseDto response = new CommentResponseDto(comment);
                    response.setReplies(repliesMap.getOrDefault(comment.getCommentId(), List.of()));
                    return response;
                })
                .collect(Collectors.toList());
    }

    /**
     * 페이징을 위한 부모 댓글 ID 목록 추출
     */
    public List<Long> extractParentCommentIds(List<Comment> comments) {
        return filterParentComments(comments).stream()
                .map(Comment::getCommentId)
                .collect(Collectors.toList());
    }

    /**
     * 부모 댓글만 필터링
     */
    public List<Comment> filterParentComments(List<Comment> comments) {
        return comments.stream()
                .filter(comment -> !comment.isReply())
                .collect(Collectors.toList());
    }

    /**
     * 답글만 필터링
     */
    public List<Comment> filterReplies(List<Comment> comments) {
        return comments.stream()
                .filter(Comment::isReply)
                .collect(Collectors.toList());
    }
}