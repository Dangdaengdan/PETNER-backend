package com.example.petner.domain.post.service;

import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.member.repository.MemberRepository;
import com.example.petner.domain.post.dto.request.PostCreateRequest;
import com.example.petner.domain.post.dto.response.PostResponse;
import com.example.petner.domain.post.dto.response.PostSummaryResponse;
import com.example.petner.domain.post.dto.request.PostUpdateRequest;
import com.example.petner.domain.post.repository.PostRepository;
import com.example.petner.domain.post.entity.Post;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.MemberException;
import com.example.petner.global.exception.customException.PostException;
import com.example.petner.search.event.PostEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public PostResponse createPost(Long authorId, PostCreateRequest request) {
        Member author = memberRepository.findById(authorId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .thumbImageUrl(request.getThumbImageUrl())
                .author(author)
                .build();

        Post savedPost = postRepository.save(post);

        // OpenSearch 동기화를 위한 이벤트 발행
        eventPublisher.publishEvent(PostEvent.created(savedPost.getPostId()));

        return new PostResponse(savedPost);
    }

    @Transactional
    public PostResponse getPost(Long postId) {
        Post post = postRepository.findByIdWithAuthor(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));
        post.increaseViewCount();

        // OpenSearch 동기화를 위한 이벤트 발행 (조회수 업데이트)
        eventPublisher.publishEvent(PostEvent.updated(postId));

        return new PostResponse(post);
    }

    public Page<PostSummaryResponse> getPosts(Pageable pageable) {
        return postRepository.findAllWithAuthor(pageable)
                .map(PostSummaryResponse::new);
    }

    @Transactional
    public PostResponse updatePost(Long postId, PostUpdateRequest request, Long currentUserId) {
        memberRepository.findById(currentUserId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

        // 작성자 권한 확인
        if (!post.getAuthor().getMemberId().equals(currentUserId)) {
            throw new PostException(ErrorCode.POST_ACCESS_DENIED);
        }

        post.update(request.getTitle(), request.getContent(), request.getThumbImageUrl());

        // OpenSearch 동기화를 위한 이벤트 발행
        eventPublisher.publishEvent(PostEvent.updated(postId));

        return new PostResponse(post);
    }

    @Transactional
    public void deletePost(Long postId, Long currentUserId) {
        memberRepository.findById(currentUserId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

        // 작성자 권한 확인
        if (!post.getAuthor().getMemberId().equals(currentUserId)) {
            throw new PostException(ErrorCode.POST_ACCESS_DENIED);
        }

        postRepository.delete(post);

        // OpenSearch 동기화를 위한 이벤트 발행
        eventPublisher.publishEvent(PostEvent.deleted(postId));
    }
}
