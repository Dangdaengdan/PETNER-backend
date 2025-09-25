package com.example.petner.domain.post.dto.response;

import com.example.petner.domain.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostSummaryResponseDto {

    private final Long postId;
    private final String title;
    private final String thumbImageUrl;
    private final String authorNickname;
    private final int viewCount;
    private final LocalDateTime createdAt;

    public PostSummaryResponseDto(Post post) {
        this.postId = post.getPostId();
        this.title = post.getTitle();
        this.thumbImageUrl = post.getThumbImageUrl();
        this.authorNickname = post.getAuthor().getNickname();
        this.viewCount = post.getViewCount();
        this.createdAt = post.getCreatedAt();
    }
}