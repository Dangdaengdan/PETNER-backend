package com.example.petner.search.dto;

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
}