package com.example.petner.domain.comment.controller;

import com.example.petner.domain.comment.dto.request.CommentCreateRequest;
import com.example.petner.domain.comment.dto.request.CommentUpdateRequest;
import com.example.petner.domain.comment.dto.response.CommentResponse;
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
    public ResponseEntity<CommentResponse> createComment(
            @Parameter(description = "게시물 ID") @PathVariable Long postId,
            @Valid @RequestBody CommentCreateRequest request,
            @SessionMember SessionUser user) {

        CommentResponse response = commentService.createComment(postId, user.getMemberId(), request);
        return ResponseEntity.created(null).body(response);
    }

    @GetMapping("/posts/{postId}/comments")
    @Operation(summary = "게시물 댓글 전체 조회", description = "특정 게시물의 모든 댓글을 계층 구조로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "댓글 목록 조회 성공")
    public ResponseEntity<List<CommentResponse>> getCommentsByPost(
            @Parameter(description = "게시물 ID") @PathVariable Long postId) {

        List<CommentResponse> response = commentService.getCommentsByPost(postId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/comments/{commentId}")
    @Operation(summary = "댓글 수정", description = "특정 댓글을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "댓글 수정 성공")
    public ResponseEntity<CommentResponse> updateComment(
            @Parameter(description = "댓글 ID") @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequest request,
            @SessionMember SessionUser user) {

        CommentResponse response = commentService.updateComment(commentId, user.getMemberId(), request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "댓글 삭제", description = "특정 댓글을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "댓글 삭제 성공")
    public ResponseEntity<String> deleteComment(
            @Parameter(description = "댓글 ID") @PathVariable Long commentId,
            @SessionMember SessionUser user) {

        commentService.deleteComment(commentId, user.getMemberId());
        return ResponseEntity.ok("댓글이 성공적으로 삭제되었습니다.");
    }
}