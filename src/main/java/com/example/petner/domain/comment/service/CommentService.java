package com.example.petner.domain.comment.service;

import com.example.petner.domain.comment.dto.request.CommentCreateRequestDto;
import com.example.petner.domain.comment.dto.request.CommentUpdateRequestDto;
import com.example.petner.domain.comment.dto.response.CommentResponseDto;
import com.example.petner.domain.comment.dto.response.CommentDeleteResponseDto;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public CommentResponseDto createComment(Long postId, Long currentUserId, CommentCreateRequestDto request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

        Member author = memberRepository.findById(currentUserId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        Comment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND));

            if (!parentComment.getPost().getPostId().equals(postId)) {
                throw new CommentException(ErrorCode.COMMENT_POST_MISMATCH);
            }

            // 2-depth 제한: 대댓글의 대댓글은 불가
            if (parentComment.isReply()) {
                throw new CommentException(ErrorCode.COMMENT_MAX_DEPTH_EXCEEDED);
            }
        }

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
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

        List<Comment> allComments = commentRepository.findByPostOrderByCreatedAtAsc(post);

        Map<Long, List<CommentResponseDto>> repliesMap = allComments.stream()
                .filter(Comment::isReply)
                .collect(Collectors.groupingBy(
                        comment -> comment.getParentComment().getCommentId(),
                        Collectors.mapping(CommentResponseDto::new, Collectors.toList())
                ));

        return allComments.stream()
                .filter(comment -> !comment.isReply())
                .map(comment -> {
                    CommentResponseDto response = new CommentResponseDto(comment);
                    response.setReplies(repliesMap.getOrDefault(comment.getCommentId(), List.of()));
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentResponseDto updateComment(Long postId, Long commentId, Long currentUserId, CommentUpdateRequestDto request) {
        // 게시물 존재 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

        Comment comment = commentRepository.findByIdWithMember(commentId)
                .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND));

        // 댓글이 해당 게시물에 속하는지 확인
        if (!comment.getPost().getPostId().equals(postId)) {
            throw new CommentException(ErrorCode.COMMENT_POST_MISMATCH);
        }

        // 댓글 작성자와 수정 요청자가 동일한지 확인
        if (!comment.getMember().getMemberId().equals(currentUserId)) {
            throw new CommentException(ErrorCode.COMMENT_ACCESS_DENIED);
        }

        comment.update(request.getContent());

        return new CommentResponseDto(comment);
    }

    @Transactional
    public CommentDeleteResponseDto deleteComment(Long postId, Long commentId, Long currentUserId) {
        Comment comment = validateDeletePermission(postId, commentId, currentUserId);
        executeDelete(comment);
        return CommentDeleteResponseDto.success(commentId, currentUserId);
    }

    private Comment validateDeletePermission(Long postId, Long commentId, Long currentUserId) {
        // 게시물 존재 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

        Comment comment = commentRepository.findByIdWithMember(commentId)
                .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND));

        // 댓글이 해당 게시물에 속하는지 확인
        if (!comment.getPost().getPostId().equals(postId)) {
            throw new CommentException(ErrorCode.COMMENT_POST_MISMATCH);
        }

        if (!comment.getMember().getMemberId().equals(currentUserId)) {
            throw new CommentException(ErrorCode.COMMENT_ACCESS_DENIED);
        }

        return comment;
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