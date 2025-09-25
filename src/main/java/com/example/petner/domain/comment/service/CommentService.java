package com.example.petner.domain.comment.service;

import com.example.petner.domain.comment.dto.request.CommentCreateRequest;
import com.example.petner.domain.comment.dto.request.CommentUpdateRequest;
import com.example.petner.domain.comment.dto.response.CommentResponse;
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
    public CommentResponse createComment(Long postId, Long currentUserId, CommentCreateRequest request) {
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
        }

        Comment comment = Comment.builder()
                .content(request.getContent())
                .post(post)
                .member(author)
                .parentComment(parentComment)
                .build();

        Comment savedComment = commentRepository.save(comment);
        return new CommentResponse(savedComment);
    }

    public List<CommentResponse> getCommentsByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

        List<Comment> allComments = commentRepository.findByPostOrderByCreatedAtAsc(post);

        Map<Long, List<CommentResponse>> repliesMap = allComments.stream()
                .filter(Comment::isReply)
                .collect(Collectors.groupingBy(
                        comment -> comment.getParentComment().getCommentId(),
                        Collectors.mapping(CommentResponse::new, Collectors.toList())
                ));

        return allComments.stream()
                .filter(comment -> !comment.isReply())
                .map(comment -> {
                    CommentResponse response = new CommentResponse(comment);
                    response.setReplies(repliesMap.getOrDefault(comment.getCommentId(), List.of()));
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentResponse updateComment(Long commentId, Long currentUserId, CommentUpdateRequest request) {
        Comment comment = commentRepository.findByIdWithMember(commentId)
                .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND));

        // 댓글 작성자와 수정 요청자가 동일한지 확인
        if (!comment.getMember().getMemberId().equals(currentUserId)) {
            throw new CommentException(ErrorCode.COMMENT_ACCESS_DENIED);
        }

        comment.update(request.getContent());

        return new CommentResponse(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, Long currentUserId) {
        Comment comment = commentRepository.findByIdWithMember(commentId)
                .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND));

        // 댓글 작성자와 삭제 요청자가 동일한지 확인
        if (!comment.getMember().getMemberId().equals(currentUserId)) {
            throw new CommentException(ErrorCode.COMMENT_ACCESS_DENIED);
        }

        commentRepository.delete(comment);
    }
}