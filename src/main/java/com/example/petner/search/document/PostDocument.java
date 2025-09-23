package com.example.petner.search.document;

import com.example.petner.domain.post.entity.Post;
import com.example.petner.search.dto.PostSearchDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDocument {

    private String id; // OpenSearch document ID (postId를 문자열로)
    private Long postId;
    private String title;
    private String content;
    private Integer viewCount;
    private String thumbImageUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long authorId;
    private String authorName;

    public static PostDocument from(Post post) {
        return PostDocument.builder()
                .id(String.valueOf(post.getPostId()))
                .postId(post.getPostId())
                .title(post.getTitle())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .thumbImageUrl(post.getThumbImageUrl())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .authorId(post.getAuthor().getMemberId())
                .authorName(post.getAuthor().getNickname())
                .build();
    }

    public static PostDocument from(PostSearchDto dto) {
        return PostDocument.builder()
                .id(String.valueOf(dto.getPostId()))
                .postId(dto.getPostId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .viewCount(dto.getViewCount())
                .thumbImageUrl(dto.getThumbImageUrl())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .authorId(dto.getAuthorId())
                .authorName(dto.getAuthorName())
                .build();
    }
}