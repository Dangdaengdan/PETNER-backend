package com.example.petner.domain.like.controller;

import com.example.petner.domain.like.dto.response.PostLikeResponseDto;
import com.example.petner.domain.like.service.PostLikeService;
import com.example.petner.global.annotation.SessionMember;
import com.example.petner.global.dto.SessionUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 게시물 좋아요 관련 API Controller
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "게시물 좋아요 (Post Likes)", description = "게시물 좋아요 관련 API")
public class PostLikeController {

    private final PostLikeService postLikeService;

    @PostMapping("/posts/{postId}/like")
    @Operation(summary = "게시물 좋아요 토글", description = "게시물에 좋아요를 추가하거나 취소합니다.")
    @ApiResponse(responseCode = "200", description = "좋아요 처리 성공")
    public ResponseEntity<PostLikeResponseDto> toggleLike(
            @Parameter(description = "게시물 ID") @PathVariable Long postId,
            @SessionMember SessionUser user) {

        PostLikeResponseDto response = postLikeService.toggleLike(postId, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/posts/{postId}/like")
    @Operation(summary = "게시물 좋아요 정보 조회", description = "특정 게시물의 좋아요 개수와 현재 사용자의 좋아요 여부를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "좋아요 정보 조회 성공")
    public ResponseEntity<PostLikeResponseDto> getLikeInfo(
            @Parameter(description = "게시물 ID") @PathVariable Long postId,
            @SessionMember(required = false) SessionUser user) {

        PostLikeResponseDto response = postLikeService.getLikeInfo(postId, user);
        return ResponseEntity.ok(response);
    }
}