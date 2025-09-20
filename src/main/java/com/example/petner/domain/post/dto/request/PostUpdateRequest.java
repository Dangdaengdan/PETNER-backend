package com.example.petner.domain.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "게시물 수정 요청")
public class PostUpdateRequest {

    @Schema(description = "게시물 제목", example = "수정된 게시물", maxLength = 200)
    @NotBlank(message = "게시물 제목은 비워둘 수 없습니다.")
    @Size(max = 200, message = "제목은 200자를 초과할 수 없습니다.")
    private String title;

    @Schema(description = "게시물 내용", example = "수정된 내용입니다.")
    @NotBlank(message = "게시물 내용은 비워둘 수 없습니다.")
    private String content;

    @Schema(description = "썸네일 이미지 URL", example = "https://example.com/updated-image.jpg", maxLength = 500)
    @Size(max = 500, message = "썸네일 이미지 URL은 500자를 초과할 수 없습니다.")
    private String thumbImageUrl;
}
