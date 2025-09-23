package com.example.petner.domain.comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "댓글 생성 요청")
public class CommentCreateRequest {

    @Schema(description = "댓글 내용", example = "좋은 게시물이네요!")
    @NotBlank(message = "댓글 내용은 비워둘 수 없습니다.")
    private String content;

    @Schema(description = "부모 댓글 ID (대댓글일 경우)", example = "1")
    private Long parentCommentId;
}