package com.example.petner.search.dto;

import com.example.petner.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostSearchDto {
    private final Long postId;
    private final String title;
    private final String content;
    private final Integer viewCount;
    private final String thumbImageUrl;

    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final Long authorId;
    private final String authorName;

    public static PostSearchDto from(Post post) {
        return new PostSearchDto(
                post.getPostId(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount(),
                post.getThumbImageUrl(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getAuthor().getMemberId(),
                post.getAuthor().getNickname()
        );
    }
}