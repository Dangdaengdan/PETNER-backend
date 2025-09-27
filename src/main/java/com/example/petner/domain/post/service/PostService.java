package com.example.petner.domain.post.service;

import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.member.repository.MemberRepository;
import com.example.petner.domain.post.dto.request.PostCreateRequestDto;
import com.example.petner.domain.post.dto.response.PostResponseDto;
import com.example.petner.domain.post.dto.response.PostSummaryResponseDto;
import com.example.petner.domain.post.dto.response.PostDeleteResponseDto;
import com.example.petner.domain.post.dto.request.PostUpdateRequestDto;
import com.example.petner.domain.post.repository.PostRepository;
import com.example.petner.domain.post.entity.Post;
import com.example.petner.domain.upload.service.UploadService;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.MemberException;
import com.example.petner.global.exception.customException.PostException;
import com.example.petner.search.event.PostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final UploadService uploadService;
    private final PostUpdater postUpdater;

    @Transactional
    public PostResponseDto createPost(Long authorId, PostCreateRequestDto request) {
        String thumbImageUrl = request.getThumbImageUrl();

        try {
            Member author = memberRepository.findById(authorId)
                    .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

            Post post = Post.builder()
                    .title(request.getTitle())
                    .content(request.getContent())
                    .thumbImageUrl(thumbImageUrl)
                    .author(author)
                    .build();

            Post savedPost = postRepository.save(post);

            // OpenSearch 동기화를 위한 이벤트 발행
            eventPublisher.publishEvent(PostEvent.created(savedPost.getPostId()));

            return new PostResponseDto(savedPost);

        } catch (Exception e) {
            // Post 생성 실패 시 업로드된 이미지 삭제
            if (thumbImageUrl != null && !thumbImageUrl.isBlank()) {
                try {
                    uploadService.deleteImageFromStorage(thumbImageUrl);
                    log.info("Post 생성 실패로 인한 이미지 삭제 완료: {}", thumbImageUrl);
                } catch (Exception deleteException) {
                    log.warn("Post 생성 실패 후 이미지 삭제 실패: {}", thumbImageUrl, deleteException);
                }
            }
            throw e; // 원래 예외를 다시 던짐
        }
    }

    @Transactional
    public PostResponseDto getPost(Long postId) {
        Post post = postRepository.findByIdWithAuthor(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));
        post.increaseViewCount();

        // OpenSearch 동기화를 위한 이벤트 발행 (조회수 업데이트)
        eventPublisher.publishEvent(PostEvent.updated(postId));

        return new PostResponseDto(post);
    }

    public Page<PostSummaryResponseDto> getPosts(Pageable pageable) {
        return postRepository.findAllWithAuthor(pageable)
                .map(PostSummaryResponseDto::new);
    }

    @Transactional
    public PostResponseDto updatePost(Long postId, PostUpdateRequestDto request, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

        // 작성자 권한 확인
        if (!post.getAuthor().getMemberId().equals(currentUserId)) {
            throw new PostException(ErrorCode.POST_ACCESS_DENIED);
        }

        // 게시글 정보 업데이트 (이미지 삭제 로직 포함)
        postUpdater.updatePost(post, request);

        // OpenSearch 동기화를 위한 이벤트 발행
        eventPublisher.publishEvent(PostEvent.updated(postId));

        return new PostResponseDto(post);
    }

    @Transactional
    public PostDeleteResponseDto deletePost(Long postId, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

        // 작성자 권한 확인
        if (!post.getAuthor().getMemberId().equals(currentUserId)) {
            throw new PostException(ErrorCode.POST_ACCESS_DENIED);
        }

        // GCP Storage에서 썸네일 이미지 삭제
        if (post.getThumbImageUrl() != null && !post.getThumbImageUrl().isBlank()) {
            try {
                uploadService.deleteImageFromStorage(post.getThumbImageUrl());
            } catch (Exception e) {
                // 이미지 삭제 실패 시 로그는 남기지만 DB 삭제는 계속 진행
                log.warn("게시글 삭제 중 썸네일 이미지 삭제 실패 (postId: {}, imageUrl: {}): {}",
                        postId, post.getThumbImageUrl(), e.getMessage());
            }
        }

        postRepository.delete(post);

        // OpenSearch 동기화를 위한 이벤트 발행
        eventPublisher.publishEvent(PostEvent.deleted(postId));

        return PostDeleteResponseDto.success(postId, currentUserId);
    }
}
