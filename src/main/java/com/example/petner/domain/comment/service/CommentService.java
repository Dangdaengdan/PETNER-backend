package com.example.petner.domain.comment.service;

import com.example.petner.domain.comment.dto.request.CommentCreateRequestDto;
import com.example.petner.domain.comment.dto.request.CommentUpdateRequestDto;
import com.example.petner.domain.comment.dto.response.CommentResponseDto;
import com.example.petner.domain.comment.dto.response.CommentDeleteResponseDto;
import com.example.petner.domain.comment.entity.Comment;
import com.example.petner.domain.comment.repository.CommentRepository;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentValidator commentValidator;
    private final CommentQueryHelper commentQueryHelper;

    @Transactional
    public CommentResponseDto createComment(Long postId, Long currentUserId, CommentCreateRequestDto request) {
        commentValidator.validateCommentRequest(request);

        Post post = commentValidator.validateAndGetPost(postId);
        Member author = commentValidator.validateAndGetMember(currentUserId);
        Comment parentComment = commentValidator.validateAndGetParentComment(request.getParentCommentId(), postId);

        Comment comment = Comment.builder()
                .content(request.getContent())
                .post(post)
                .member(author)
                .parentComment(parentComment)
                .build();

        Comment savedComment = commentRepository.save(comment);
        return new CommentResponseDto(savedComment);
    }

    public List<CommentResponseDto> getCommentsByPost(Long postId) {
        Post post = commentValidator.validateAndGetPost(postId);
        List<Comment> allComments = commentRepository.findByPostOrderByCreatedAtAsc(post);
        return commentQueryHelper.buildCommentHierarchy(
                commentQueryHelper.filterParentComments(allComments),
                commentQueryHelper.filterReplies(allComments)
        );
    }

    /**
     * 특정 게시물의 댓글을 페이징하여 조회 (계층 구조 유지, N+1 문제 해결)
     */
    public Page<CommentResponseDto> getCommentsByPostWithPaging(Long postId, int page, int size) {
        commentValidator.validatePagingParams(page, size);
        Post post = commentValidator.validateAndGetPost(postId);

        Pageable pageable = createCommentPageable(page, size);
        Page<Comment> commentsPage = commentRepository.findByPostWithPaging(post, pageable);

        return buildPaginatedCommentHierarchy(commentsPage);
    }

    @Transactional
    public CommentResponseDto updateComment(Long postId, Long commentId, Long currentUserId, CommentUpdateRequestDto request) {
        Comment comment = commentValidator.validateCommentPermission(postId, commentId, currentUserId);
        comment.update(request.getContent());
        return new CommentResponseDto(comment);
    }

    @Transactional
    public CommentDeleteResponseDto deleteComment(Long postId, Long commentId, Long currentUserId) {
        Comment comment = commentValidator.validateCommentPermission(postId, commentId, currentUserId);
        executeDelete(comment);
        return CommentDeleteResponseDto.success(commentId, currentUserId);
    }

    private Pageable createCommentPageable(int page, int size) {
        return PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
    }

    private Page<CommentResponseDto> buildPaginatedCommentHierarchy(Page<Comment> commentsPage) {
        List<Long> parentCommentIds = commentQueryHelper.extractParentCommentIds(commentsPage.getContent());

        List<Comment> replies = parentCommentIds.isEmpty() ?
                List.of() :
                commentRepository.findRepliesByParentCommentIds(parentCommentIds);

        List<CommentResponseDto> hierarchyComments = commentQueryHelper.buildCommentHierarchy(
                commentsPage.getContent(), replies);

        return commentsPage.map(comment ->
                hierarchyComments.stream()
                        .filter(dto -> dto.getCommentId().equals(comment.getCommentId()))
                        .findFirst()
                        .orElse(new CommentResponseDto(comment)));
    }

    private void executeDelete(Comment comment) {
        boolean hasReplies = commentRepository.existsActiveReplies(comment);

        if (hasReplies) {
            comment.delete();
        } else {
            commentRepository.delete(comment);
        }
    }
}