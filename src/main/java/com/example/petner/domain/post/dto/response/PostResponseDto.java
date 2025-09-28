package com.example.petner.domain.post.dto.response;

import com.example.petner.domain.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostResponseDto {

    private final Long postId;
    private final String title;
    private final String content;
    private final String thumbImageUrl;
    private final String authorNickname;
    private final int viewCount;
    private final int likeCount;
    private final boolean isLiked;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public PostResponseDto(Post post) {
        this.postId = post.getPostId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.thumbImageUrl = post.getThumbImageUrl();
        this.authorNickname = post.getAuthor().getNickname();
        this.viewCount = post.getViewCount();
        this.likeCount = post.getLikeCount();
        this.isLiked = false; // 기본값, 실제 값은 Service에서 설정
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }

    public PostResponseDto(Post post, boolean isLiked) {
        this.postId = post.getPostId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.thumbImageUrl = post.getThumbImageUrl();
        this.authorNickname = post.getAuthor().getNickname();
        this.viewCount = post.getViewCount();
        this.likeCount = post.getLikeCount();
        this.isLiked = isLiked;
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }
}
