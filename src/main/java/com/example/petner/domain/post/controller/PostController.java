package com.example.petner.domain.post.controller;

import com.example.petner.domain.post.dto.request.PostCreateRequest;
import com.example.petner.domain.post.dto.request.PostUpdateRequest;
import com.example.petner.domain.post.dto.response.PostResponse;
import com.example.petner.domain.post.dto.response.PostSummaryResponse;
import com.example.petner.domain.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Tag(name = "게시물 (Posts)", description = "게시물 관련 API")
public class PostController {

    private final PostService postService;

    @PostMapping
    @Operation(summary = "게시물 생성", description = "새로운 게시물을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "게시물 생성 성공")
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody PostCreateRequest request) {
        // TODO: 추후 인증 기능이 구현되면, 현재 로그인한 사용자의 ID를 가져와서 사용해야 합니다.
        Long currentMemberId = 1L;
        PostResponse response = postService.createPost(currentMemberId, request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getPostId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{postId}")
    @Operation(summary = "게시물 상세 조회", description = "특정 게시물 하나를 상세 조회합니다.")
    @ApiResponse(responseCode = "200", description = "게시물 조회 성공")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long postId) {
        PostResponse response = postService.getPost(postId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "게시물 목록 조회", description = "전체 게시물 목록을 페이징하여 조회합니다.")
    @ApiResponse(responseCode = "200", description = "게시물 목록 조회 성공")
    public ResponseEntity<Page<PostSummaryResponse>> getPosts(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "정렬 방식 (예: createdAt,desc)", example = "createdAt,desc") @RequestParam(defaultValue = "createdAt,desc") String sort) {

        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 && "asc".equalsIgnoreCase(sortParams[1])
            ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
        Page<PostSummaryResponse> response = postService.getPosts(pageable);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{postId}")
    @Operation(summary = "게시물 수정", description = "특정 게시물을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "게시물 수정 성공")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long postId, @Valid @RequestBody PostUpdateRequest request) {
        // TODO: 추후 인증 기능이 구현되면, 게시물 작성자와 현재 로그인한 사용자가 동일한지 확인해야 합니다.
        PostResponse response = postService.updatePost(postId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "게시물 삭제", description = "특정 게시물을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "게시물 삭제 성공")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        // TODO: 추후 인증 기능이 구현되면, 게시물 작성자와 현재 로그인한 사용자가 동일한지 확인해야 합니다.
        postService.deletePost(postId);
        return ResponseEntity.ok("게시물이 성공적으로 삭제되었습니다.");
    }
}
