package com.example.petner.domain.post.service;

import com.example.petner.domain.comment.repository.CommentRepository;
import com.example.petner.domain.like.repository.PostLikeRepository;
import com.example.petner.domain.post.dto.response.PostDeleteResponseDto;
import com.example.petner.domain.post.entity.Post;
import com.example.petner.domain.post.repository.PostRepository;
import com.example.petner.domain.upload.service.UploadService;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.PostException;
import com.example.petner.search.event.PostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PostDeleteService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final UploadService uploadService;
    private final ApplicationEventPublisher eventPublisher;

    public PostDeleteResponseDto deletePost(Long postId, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

        validateAuthor(post, currentUserId);
        deleteRelatedData(post);
        deletePostImage(post);
        deletePostEntity(post);
        publishDeleteEvent(postId);

        return PostDeleteResponseDto.success(postId, currentUserId);
    }

    private void validateAuthor(Post post, Long currentUserId) {
        if (!post.getAuthor().getMemberId().equals(currentUserId)) {
            throw new PostException(ErrorCode.POST_ACCESS_DENIED);
        }
    }

    private void deleteRelatedData(Post post) {
        postLikeRepository.deleteByPost(post);
        commentRepository.deleteRepliesByPost(post);  // 대댓글 먼저 삭제
        commentRepository.flush();  // 즉시 DB에 반영
        commentRepository.deleteParentCommentsByPost(post);  // 부모댓글 나중에 삭제
    }

    private void deletePostImage(Post post) {
        if (post.getThumbImageUrl() != null && !post.getThumbImageUrl().isBlank()) {
            try {
                uploadService.deleteImageFromStorage(post.getThumbImageUrl());
            } catch (Exception e) {
                log.warn("게시글 삭제 중 썸네일 이미지 삭제 실패 (postId: {}, imageUrl: {}): {}",
                        post.getPostId(), post.getThumbImageUrl(), e.getMessage());
            }
        }
    }

    private void deletePostEntity(Post post) {
        postRepository.delete(post);
    }

    private void publishDeleteEvent(Long postId) {
        eventPublisher.publishEvent(PostEvent.deleted(postId));
    }
}