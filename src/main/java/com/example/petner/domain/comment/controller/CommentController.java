package com.example.petner.domain.comment.controller;

import com.example.petner.domain.comment.dto.request.CommentCreateRequestDto;
import com.example.petner.domain.comment.dto.request.CommentUpdateRequestDto;
import com.example.petner.domain.comment.dto.response.CommentResponseDto;
import com.example.petner.domain.comment.dto.response.CommentDeleteResponseDto;
import com.example.petner.domain.comment.service.CommentService;
import com.example.petner.global.annotation.SessionMember;
import com.example.petner.global.dto.SessionUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "댓글 (Comments)", description = "댓글 및 대댓글 관련 API")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    @Operation(summary = "댓글/대댓글 생성", description = "특정 게시물에 댓글 또는 대댓글을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "댓글 생성 성공")
    public ResponseEntity<CommentResponseDto> createComment(
            @Parameter(description = "게시물 ID") @PathVariable Long postId,
            @Valid @RequestBody CommentCreateRequestDto request,
            @SessionMember SessionUser user) {

        CommentResponseDto response = commentService.createComment(postId, user.getMemberId(), request);
        return ResponseEntity.created(null).body(response);
    }

    @GetMapping("/posts/{postId}/comments")
    @Operation(summary = "게시물 댓글 전체 조회", description = "특정 게시물의 모든 댓글을 계층 구조로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "댓글 목록 조회 성공")
    public ResponseEntity<List<CommentResponseDto>> getCommentsByPost(
            @Parameter(description = "게시물 ID") @PathVariable Long postId) {

        List<CommentResponseDto> response = commentService.getCommentsByPost(postId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/posts/{postId}/comments/{commentId}")
    @Operation(summary = "댓글 수정", description = "특정 게시물의 댓글을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "댓글 수정 성공")
    public ResponseEntity<CommentResponseDto> updateComment(
            @Parameter(description = "게시물 ID") @PathVariable Long postId,
            @Parameter(description = "댓글 ID") @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequestDto request,
            @SessionMember SessionUser user) {

        CommentResponseDto response = commentService.updateComment(postId, commentId, user.getMemberId(), request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    @Operation(summary = "댓글 삭제", description = "특정 게시물의 댓글을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "댓글 삭제 성공")
    public ResponseEntity<CommentDeleteResponseDto> deleteComment(
            @Parameter(description = "게시물 ID") @PathVariable Long postId,
            @Parameter(description = "댓글 ID") @PathVariable Long commentId,
            @SessionMember SessionUser user) {

        CommentDeleteResponseDto response = commentService.deleteComment(postId, commentId, user.getMemberId());
        return ResponseEntity.ok(response);
    }
}