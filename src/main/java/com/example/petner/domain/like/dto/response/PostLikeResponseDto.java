package com.example.petner.domain.like.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "게시물 좋아요 응답")
public class PostLikeResponseDto {

    @Schema(description = "게시물 ID", example = "1")
    private Long postId;

    @Schema(description = "좋아요 개수", example = "15")
    private Integer likeCount;

    @Schema(description = "현재 사용자의 좋아요 여부", example = "true")
    private Boolean isLiked;
}