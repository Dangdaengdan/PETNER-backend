package com.example.petner.domain.post.service;

import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.member.repository.MemberRepository;
import com.example.petner.domain.post.dto.request.PostCreateRequestDto;
import com.example.petner.domain.post.dto.request.PostUpdateRequestDto;
import com.example.petner.domain.post.dto.response.PostDeleteResponseDto;
import com.example.petner.domain.post.dto.response.PostResponseDto;
import com.example.petner.domain.post.dto.response.PostSummaryResponseDto;
import com.example.petner.domain.post.entity.Post;
import com.example.petner.domain.post.repository.PostRepository;
import com.example.petner.domain.upload.service.UploadService;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.MemberException;
import com.example.petner.global.exception.customException.PostException;
import com.example.petner.search.event.PostEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private UploadService uploadService;

    @Mock
    private PostUpdater postUpdater;

    @Mock
    private PostDeleteService postDeleteService;

    @InjectMocks
    private PostService postService;

    private Member mockMember;
    private Post mockPost;
    private PostCreateRequestDto createRequest;
    private PostUpdateRequestDto updateRequest;

    @BeforeEach
    void setUp() {
        mockMember = createMockMember();
        mockPost = createMockPost();
        createRequest = createMockCreateRequest();
        updateRequest = createMockUpdateRequest();
    }

    @Test
    @DisplayName("게시물 생성 성공")
    void createPost_Success() {
        // Given
        when(memberRepository.findById(1L)).thenReturn(Optional.of(mockMember));
        when(postRepository.save(any(Post.class))).thenReturn(mockPost);

        // When
        PostResponseDto result = postService.createPost(1L, createRequest);

        // Then
        assertNotNull(result);
        assertEquals(mockPost.getPostId(), result.getPostId());
        assertEquals(mockPost.getTitle(), result.getTitle());
        assertEquals(mockPost.getContent(), result.getContent());

        verify(memberRepository).findById(1L);
        verify(postRepository).save(any(Post.class));
        verify(eventPublisher).publishEvent(any(PostEvent.class));
        verifyNoInteractions(uploadService);
    }

    @Test
    @DisplayName("게시물 생성 성공 - 썸네일 이미지 있음")
    void createPost_WithThumbnail_Success() {
        // Given
        PostCreateRequestDto requestWithImage = createMockCreateRequestWithImage();
        when(memberRepository.findById(1L)).thenReturn(Optional.of(mockMember));
        when(postRepository.save(any(Post.class))).thenReturn(mockPost);

        // When
        PostResponseDto result = postService.createPost(1L, requestWithImage);

        // Then
        assertNotNull(result);
        assertEquals(mockPost.getPostId(), result.getPostId());

        verify(memberRepository).findById(1L);
        verify(postRepository).save(any(Post.class));
        verify(eventPublisher).publishEvent(any(PostEvent.class));
    }

    @Test
    @DisplayName("게시물 생성 실패 - 멤버 없음")
    void createPost_MemberNotFound() {
        // Given
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        MemberException exception = assertThrows(MemberException.class,
                () -> postService.createPost(1L, createRequest));

        assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());
        verify(memberRepository).findById(1L);
        verifyNoInteractions(postRepository);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    @DisplayName("게시물 생성 실패 시 이미지 삭제")
    void createPost_FailureWithImageCleanup() {
        // Given
        PostCreateRequestDto requestWithImage = createMockCreateRequestWithImage();
        when(memberRepository.findById(1L)).thenReturn(Optional.of(mockMember));
        when(postRepository.save(any(Post.class))).thenThrow(new RuntimeException("DB Error"));

        // When & Then
        assertThrows(RuntimeException.class,
                () -> postService.createPost(1L, requestWithImage));

        verify(uploadService).deleteImageFromStorage(requestWithImage.getThumbImageUrl());
    }

    @Test
    @DisplayName("게시물 상세 조회 성공")
    void getPost_Success() {
        // Given
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));

        // When
        PostResponseDto result = postService.getPost(postId);

        // Then
        assertNotNull(result);
        assertEquals(mockPost.getPostId(), result.getPostId());
        assertEquals(mockPost.getTitle(), result.getTitle());

        verify(postRepository).findById(postId);
        verify(mockPost).increaseViewCount();
        verify(eventPublisher).publishEvent(any(PostEvent.class));
    }

    @Test
    @DisplayName("게시물 상세 조회 실패 - 게시물 없음")
    void getPost_NotFound() {
        // Given
        Long postId = 999L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When & Then
        PostException exception = assertThrows(PostException.class,
                () -> postService.getPost(postId));

        assertEquals(ErrorCode.POST_NOT_FOUND, exception.getErrorCode());
        verify(postRepository).findById(postId);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    @DisplayName("전체 게시물 목록 조회 성공")
    void getPosts_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Post> posts = Arrays.asList(mockPost, createMockPost(2L, "두 번째 게시물"));
        Page<Post> postPage = new PageImpl<>(posts);
        when(postRepository.findAllWithAuthor(pageable)).thenReturn(postPage);

        // When
        Page<PostSummaryResponseDto> result = postService.getPosts(pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(mockPost.getTitle(), result.getContent().get(0).getTitle());

        verify(postRepository).findAllWithAuthor(pageable);
    }

    @Test
    @DisplayName("내 게시물 목록 조회 성공")
    void getMyPosts_Success() {
        // Given
        Long memberId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));

        List<Post> myPosts = Arrays.asList(mockPost);
        Page<Post> postPage = new PageImpl<>(myPosts);
        when(postRepository.findByAuthorWithPaging(mockMember, pageable)).thenReturn(postPage);

        // When
        Page<PostSummaryResponseDto> result = postService.getMyPosts(memberId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(mockPost.getTitle(), result.getContent().get(0).getTitle());

        verify(memberRepository).findById(memberId);
        verify(postRepository).findByAuthorWithPaging(mockMember, pageable);
    }

    @Test
    @DisplayName("내 게시물 목록 조회 실패 - 멤버 없음")
    void getMyPosts_MemberNotFound() {
        // Given
        Long memberId = 999L;
        Pageable pageable = PageRequest.of(0, 10);
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // When & Then
        MemberException exception = assertThrows(MemberException.class,
                () -> postService.getMyPosts(memberId, pageable));

        assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());
        verify(memberRepository).findById(memberId);
        verifyNoInteractions(postRepository);
    }

    @Test
    @DisplayName("게시물 수정 성공")
    void updatePost_Success() {
        // Given
        Long postId = 1L;
        Long authorId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));
        when(mockPost.getAuthor()).thenReturn(mockMember);
        when(mockMember.getMemberId()).thenReturn(authorId);

        // When
        PostResponseDto result = postService.updatePost(postId, updateRequest, authorId);

        // Then
        assertNotNull(result);
        assertEquals(mockPost.getPostId(), result.getPostId());

        verify(postRepository).findById(postId);
        verify(postUpdater).updatePost(mockPost, updateRequest);
        verify(eventPublisher).publishEvent(any(PostEvent.class));
    }

    @Test
    @DisplayName("게시물 수정 실패 - 게시물 없음")
    void updatePost_PostNotFound() {
        // Given
        Long postId = 999L;
        Long authorId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When & Then
        PostException exception = assertThrows(PostException.class,
                () -> postService.updatePost(postId, updateRequest, authorId));

        assertEquals(ErrorCode.POST_NOT_FOUND, exception.getErrorCode());
        verify(postRepository).findById(postId);
        verifyNoInteractions(postUpdater);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    @DisplayName("게시물 수정 실패 - 권한 없음")
    void updatePost_AccessDenied() {
        // Given
        Long postId = 1L;
        Long authorId = 1L;
        Long otherUserId = 2L;
        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));
        when(mockPost.getAuthor()).thenReturn(mockMember);
        when(mockMember.getMemberId()).thenReturn(authorId);

        // When & Then
        PostException exception = assertThrows(PostException.class,
                () -> postService.updatePost(postId, updateRequest, otherUserId));

        assertEquals(ErrorCode.POST_ACCESS_DENIED, exception.getErrorCode());
        verify(postRepository).findById(postId);
        verifyNoInteractions(postUpdater);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    @DisplayName("게시물 삭제 성공")
    void deletePost_Success() {
        // Given
        Long postId = 1L;
        Long authorId = 1L;
        PostDeleteResponseDto deleteResponse = PostDeleteResponseDto.success(postId, authorId);
        when(postDeleteService.deletePost(postId, authorId)).thenReturn(deleteResponse);

        // When
        PostDeleteResponseDto result = postService.deletePost(postId, authorId);

        // Then
        assertNotNull(result);
        assertEquals(postId, result.getPostId());
        assertEquals(authorId, result.getMemberId());
        assertTrue(result.isSuccess());

        verify(postDeleteService).deletePost(postId, authorId);
    }

    @Test
    @DisplayName("게시물 삭제 실패 - PostDeleteService에서 예외 발생")
    void deletePost_ServiceException() {
        // Given
        Long postId = 1L;
        Long authorId = 1L;
        when(postDeleteService.deletePost(postId, authorId))
                .thenThrow(new PostException(ErrorCode.POST_ACCESS_DENIED));

        // When & Then
        PostException exception = assertThrows(PostException.class,
                () -> postService.deletePost(postId, authorId));

        assertEquals(ErrorCode.POST_ACCESS_DENIED, exception.getErrorCode());
        verify(postDeleteService).deletePost(postId, authorId);
    }

    // Helper methods
    private Member createMockMember() {
        Member member = mock(Member.class);
        when(member.getMemberId()).thenReturn(1L);
        when(member.getNickname()).thenReturn("testUser");
        when(member.getEmail()).thenReturn("test@example.com");
        return member;
    }

    private Post createMockPost() {
        return createMockPost(1L, "테스트 게시물");
    }

    private Post createMockPost(Long postId, String title) {
        Post post = mock(Post.class);
        when(post.getPostId()).thenReturn(postId);
        when(post.getTitle()).thenReturn(title);
        when(post.getContent()).thenReturn("테스트 내용");
        when(post.getThumbImageUrl()).thenReturn("test-image.jpg");
        when(post.getAuthor()).thenReturn(mockMember);
        when(post.getViewCount()).thenReturn(0);
        when(post.getLikeCount()).thenReturn(0);
        when(post.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(post.getUpdatedAt()).thenReturn(LocalDateTime.now());
        return post;
    }

    private PostCreateRequestDto createMockCreateRequest() {
        PostCreateRequestDto mock = mock(PostCreateRequestDto.class);
        when(mock.getTitle()).thenReturn("테스트 게시물");
        when(mock.getContent()).thenReturn("테스트 내용");
        when(mock.getThumbImageUrl()).thenReturn(null);
        return mock;
    }

    private PostCreateRequestDto createMockCreateRequestWithImage() {
        PostCreateRequestDto mock = mock(PostCreateRequestDto.class);
        when(mock.getTitle()).thenReturn("테스트 게시물");
        when(mock.getContent()).thenReturn("테스트 내용");
        when(mock.getThumbImageUrl()).thenReturn("test-image.jpg");
        return mock;
    }

    private PostUpdateRequestDto createMockUpdateRequest() {
        PostUpdateRequestDto mock = mock(PostUpdateRequestDto.class);
        when(mock.getTitle()).thenReturn("수정된 게시물");
        when(mock.getContent()).thenReturn("수정된 내용");
        when(mock.getThumbImageUrl()).thenReturn("updated-image.jpg");
        return mock;
    }
}