package com.example.petner.domain.post.service;

import com.example.petner.domain.post.dto.request.PostUpdateRequestDto;
import com.example.petner.domain.post.entity.Post;
import com.example.petner.domain.upload.service.UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 게시글 정보 업데이트를 담당하는 컴포넌트
 * Single Responsibility Principle(SRP)을 준수하여 업데이트 로직만 담당
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PostUpdater {

    private final UploadService uploadService;

    /**
     * 게시글 정보 업데이트
     * 이미지 URL이 변경된 경우 기존 이미지 삭제 처리
     */
    public void updatePost(Post post, PostUpdateRequestDto requestDto) {
        String oldThumbImageUrl = post.getThumbImageUrl();
        String newThumbImageUrl = requestDto.getThumbImageUrl();

        // 이미지 URL이 변경된 경우 기존 이미지 삭제
        if (newThumbImageUrl != null && !newThumbImageUrl.equals(oldThumbImageUrl)) {
            if (oldThumbImageUrl != null && !oldThumbImageUrl.isBlank()) {
                try {
                    uploadService.deleteImageFromStorage(oldThumbImageUrl);
                    log.info("기존 썸네일 이미지 삭제 완료: {}", oldThumbImageUrl);
                } catch (Exception e) {
                    // 기존 이미지 삭제 실패 시 로그만 남기고 업데이트는 계속 진행
                    log.warn("기존 썸네일 이미지 삭제 실패 (postId: {}, oldImageUrl: {}): {}",
                            post.getPostId(), oldThumbImageUrl, e.getMessage());
                }
            }
        }

        post.update(requestDto.getTitle(), requestDto.getContent(), requestDto.getThumbImageUrl());
    }
}