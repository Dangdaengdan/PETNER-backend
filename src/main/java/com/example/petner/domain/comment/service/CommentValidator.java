package com.example.petner.domain.comment.service;

import com.example.petner.domain.comment.dto.request.CommentCreateRequestDto;
import com.example.petner.domain.comment.entity.Comment;
import com.example.petner.domain.comment.repository.CommentRepository;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.member.repository.MemberRepository;
import com.example.petner.domain.post.entity.Post;
import com.example.petner.domain.post.repository.PostRepository;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.CommentException;
import com.example.petner.global.exception.customException.MemberException;
import com.example.petner.global.exception.customException.PostException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 댓글 검증 로직을 담당하는 컴포넌트
 * Single Responsibility Principle(SRP)을 준수하여 검증 로직만 담당
 */
@Component
@RequiredArgsConstructor
public class CommentValidator {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    /**
     * 댓글 생성 요청 검증
     */
    public void validateCommentRequest(CommentCreateRequestDto request) {
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new CommentException(ErrorCode.COMMENT_INVALID_REQUEST);
        }
    }

    /**
     * 게시물 존재 여부 검증 및 조회
     */
    public Post validateAndGetPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));
    }

    /**
     * 회원 존재 여부 검증 및 조회
     */
    public Member validateAndGetMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    /**
     * 부모 댓글 검증 및 조회
     */
    public Comment validateAndGetParentComment(Long parentCommentId, Long postId) {
        if (parentCommentId == null) {
            return null;
        }

        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND));

        if (!parentComment.getPost().getPostId().equals(postId)) {
            throw new CommentException(ErrorCode.COMMENT_POST_MISMATCH);
        }

        if (parentComment.isReply()) {
            throw new CommentException(ErrorCode.COMMENT_MAX_DEPTH_EXCEEDED);
        }

        return parentComment;
    }

    /**
     * 댓글 수정/삭제 권한 검증
     */
    public Comment validateCommentPermission(Long postId, Long commentId, Long currentUserId) {
        Comment comment = commentRepository.findByIdWithMember(commentId)
                .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getPost().getPostId().equals(postId)) {
            throw new CommentException(ErrorCode.COMMENT_POST_MISMATCH);
        }

        if (!comment.getMember().getMemberId().equals(currentUserId)) {
            throw new CommentException(ErrorCode.COMMENT_ACCESS_DENIED);
        }

        return comment;
    }

    /**
     * 페이징 파라미터 검증
     */
    public void validatePagingParams(int page, int size) {
        if (page < 0) {
            throw new CommentException(ErrorCode.COMMENT_INVALID_REQUEST);
        }
        if (size <= 0) {
            throw new CommentException(ErrorCode.COMMENT_INVALID_REQUEST);
        }
        if (size > 100) {
            throw new CommentException(ErrorCode.COMMENT_INVALID_REQUEST);
        }
    }
}