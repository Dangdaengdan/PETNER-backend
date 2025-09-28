package com.example.petner.domain.post.controller;

import com.example.petner.domain.post.dto.request.PostCreateRequestDto;
import com.example.petner.domain.post.dto.request.PostUpdateRequestDto;
import com.example.petner.domain.post.dto.response.PostResponseDto;
import com.example.petner.domain.post.dto.response.PostSummaryResponseDto;
import com.example.petner.domain.post.dto.response.PostDeleteResponseDto;
import com.example.petner.domain.post.service.PostService;
import com.example.petner.search.document.PostDocument;
import com.example.petner.search.service.PostSearchService;
import com.example.petner.global.annotation.SessionMember;
import com.example.petner.global.dto.SessionUser;
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
import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Tag(name = "게시물 (Posts)", description = "게시물 관련 API")
public class PostController {

    private final PostService postService;
    private final PostSearchService postSearchService;

    @PostMapping
    @Operation(summary = "게시물 생성", description = "새로운 게시물을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "게시물 생성 성공")
    public ResponseEntity<PostResponseDto> createPost(@Valid @RequestBody PostCreateRequestDto request, @SessionMember SessionUser user) {
        PostResponseDto response = postService.createPost(user.getMemberId(), request);

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
    public ResponseEntity<PostResponseDto> getPost(@PathVariable Long postId) {
        PostResponseDto response = postService.getPost(postId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "게시물 목록 조회", description = "전체 게시물 목록을 페이징하여 조회합니다.")
    @ApiResponse(responseCode = "200", description = "게시물 목록 조회 성공")
    public ResponseEntity<Page<PostSummaryResponseDto>> getPosts(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "정렬 방식 (예: createdAt,desc)", example = "createdAt,desc") @RequestParam(defaultValue = "createdAt,desc") String sort) {

        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 && "asc".equalsIgnoreCase(sortParams[1])
            ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
        Page<PostSummaryResponseDto> response = postService.getPosts(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    @Operation(summary = "내 게시물 목록 조회", description = "현재 사용자가 작성한 게시물 목록을 페이징하여 조회합니다.")
    @ApiResponse(responseCode = "200", description = "내 게시물 목록 조회 성공")
    public ResponseEntity<Page<PostSummaryResponseDto>> getMyPosts(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "정렬 방식 (예: createdAt,desc)", example = "createdAt,desc") @RequestParam(defaultValue = "createdAt,desc") String sort,
            @SessionMember SessionUser user) {

        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 && "asc".equalsIgnoreCase(sortParams[1])
            ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
        Page<PostSummaryResponseDto> response = postService.getMyPosts(user.getMemberId(), pageable);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{postId}")
    @Operation(summary = "게시물 수정", description = "특정 게시물을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "게시물 수정 성공")
    public ResponseEntity<PostResponseDto> updatePost(@PathVariable Long postId, @Valid @RequestBody PostUpdateRequestDto request, @SessionMember SessionUser user) {
        PostResponseDto response = postService.updatePost(postId, request, user.getMemberId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "게시물 삭제", description = "특정 게시물을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "게시물 삭제 성공")
    public ResponseEntity<PostDeleteResponseDto> deletePost(@PathVariable Long postId, @SessionMember SessionUser user) {
        PostDeleteResponseDto response = postService.deletePost(postId, user.getMemberId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "게시물 검색", description = "키워드를 통해 게시물을 검색하고 정렬할 수 있습니다.")
    @ApiResponse(responseCode = "200", description = "게시물 검색 성공")
    public ResponseEntity<List<PostDocument>> searchPosts(
            @Parameter(description = "검색 키워드", example = "강아지") @RequestParam(required = false) String q,
            @Parameter(description = "정렬 방식 (latest, oldest, viewCount)", example = "latest") @RequestParam(defaultValue = "latest") String sort,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size) {

        List<PostDocument> response = postSearchService.searchPosts(q, sort, page, size);
        return ResponseEntity.ok(response);
    }
}
