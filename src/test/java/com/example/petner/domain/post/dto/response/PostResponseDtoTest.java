package com.example.petner.domain.post.dto.response;

import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.post.entity.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostResponseDtoTest {

    private Post mockPost;
    private Member mockMember;

    @BeforeEach
    void setUp() {
        mockMember = createMockMember();
        mockPost = createMockPost();
    }

    @Test
    @DisplayName("PostResponseDto 생성 테스트 - 기본 생성자")
    void createPostResponseDto_BasicConstructor() {
        // When
        PostResponseDto dto = new PostResponseDto(mockPost);

        // Then
        assertNotNull(dto);
        assertEquals(1L, dto.getPostId());
        assertEquals("테스트 게시물", dto.getTitle());
        assertEquals("테스트 내용", dto.getContent());
        assertEquals("test-image.jpg", dto.getThumbImageUrl());
        assertEquals("testUser", dto.getAuthorNickname());
        assertEquals(10, dto.getViewCount());
        assertEquals(5, dto.getLikeCount());
        assertFalse(dto.isLiked()); // 기본값은 false
        assertNotNull(dto.getCreatedAt());
        assertNotNull(dto.getUpdatedAt());
    }

    @Test
    @DisplayName("PostResponseDto 생성 테스트 - isLiked 포함 생성자")
    void createPostResponseDto_WithIsLiked() {
        // When
        PostResponseDto dto = new PostResponseDto(mockPost, true);

        // Then
        assertNotNull(dto);
        assertEquals(1L, dto.getPostId());
        assertEquals("테스트 게시물", dto.getTitle());
        assertEquals("테스트 내용", dto.getContent());
        assertEquals("test-image.jpg", dto.getThumbImageUrl());
        assertEquals("testUser", dto.getAuthorNickname());
        assertEquals(10, dto.getViewCount());
        assertEquals(5, dto.getLikeCount());
        assertTrue(dto.isLiked()); // 명시적으로 true 설정
        assertNotNull(dto.getCreatedAt());
        assertNotNull(dto.getUpdatedAt());
    }

    @Test
    @DisplayName("PostResponseDto 생성 테스트 - isLiked false")
    void createPostResponseDto_WithIsLikedFalse() {
        // When
        PostResponseDto dto = new PostResponseDto(mockPost, false);

        // Then
        assertNotNull(dto);
        assertFalse(dto.isLiked());
    }

    @Test
    @DisplayName("PostResponseDto 생성 테스트 - 썸네일 이미지 없음")
    void createPostResponseDto_WithoutThumbnail() {
        // Given
        when(mockPost.getThumbImageUrl()).thenReturn(null);

        // When
        PostResponseDto dto = new PostResponseDto(mockPost);

        // Then
        assertNotNull(dto);
        assertNull(dto.getThumbImageUrl());
    }

    @Test
    @DisplayName("PostResponseDto 생성 테스트 - 빈 썸네일 이미지")
    void createPostResponseDto_WithEmptyThumbnail() {
        // Given
        when(mockPost.getThumbImageUrl()).thenReturn("");

        // When
        PostResponseDto dto = new PostResponseDto(mockPost);

        // Then
        assertNotNull(dto);
        assertEquals("", dto.getThumbImageUrl());
    }

    @Test
    @DisplayName("PostResponseDto 생성 테스트 - 조회수 0")
    void createPostResponseDto_WithZeroViewCount() {
        // Given
        when(mockPost.getViewCount()).thenReturn(0);

        // When
        PostResponseDto dto = new PostResponseDto(mockPost);

        // Then
        assertNotNull(dto);
        assertEquals(0, dto.getViewCount());
    }

    @Test
    @DisplayName("PostResponseDto 생성 테스트 - 좋아요 수 0")
    void createPostResponseDto_WithZeroLikeCount() {
        // Given
        when(mockPost.getLikeCount()).thenReturn(0);

        // When
        PostResponseDto dto = new PostResponseDto(mockPost);

        // Then
        assertNotNull(dto);
        assertEquals(0, dto.getLikeCount());
    }

    @Test
    @DisplayName("PostResponseDto 생성 테스트 - 높은 조회수")
    void createPostResponseDto_WithHighViewCount() {
        // Given
        when(mockPost.getViewCount()).thenReturn(999999);

        // When
        PostResponseDto dto = new PostResponseDto(mockPost);

        // Then
        assertNotNull(dto);
        assertEquals(999999, dto.getViewCount());
    }

    @Test
    @DisplayName("PostResponseDto 생성 테스트 - 긴 제목")
    void createPostResponseDto_WithLongTitle() {
        // Given
        String longTitle = "매우 긴 제목입니다. ".repeat(10);
        when(mockPost.getTitle()).thenReturn(longTitle);

        // When
        PostResponseDto dto = new PostResponseDto(mockPost);

        // Then
        assertNotNull(dto);
        assertEquals(longTitle, dto.getTitle());
        assertTrue(dto.getTitle().length() > 100);
    }

    @Test
    @DisplayName("PostResponseDto 생성 테스트 - 긴 내용")
    void createPostResponseDto_WithLongContent() {
        // Given
        String longContent = "매우 긴 내용입니다. ".repeat(100);
        when(mockPost.getContent()).thenReturn(longContent);

        // When
        PostResponseDto dto = new PostResponseDto(mockPost);

        // Then
        assertNotNull(dto);
        assertEquals(longContent, dto.getContent());
        assertTrue(dto.getContent().length() > 1000);
    }

    @Test
    @DisplayName("PostResponseDto 생성 테스트 - 한글 닉네임")
    void createPostResponseDto_WithKoreanNickname() {
        // Given
        when(mockMember.getNickname()).thenReturn("한글닉네임");

        // When
        PostResponseDto dto = new PostResponseDto(mockPost);

        // Then
        assertNotNull(dto);
        assertEquals("한글닉네임", dto.getAuthorNickname());
    }

    @Test
    @DisplayName("PostResponseDto 생성 테스트 - 특수문자 포함 닉네임")
    void createPostResponseDto_WithSpecialCharacterNickname() {
        // Given
        when(mockMember.getNickname()).thenReturn("user@123!#");

        // When
        PostResponseDto dto = new PostResponseDto(mockPost);

        // Then
        assertNotNull(dto);
        assertEquals("user@123!#", dto.getAuthorNickname());
    }

    @Test
    @DisplayName("PostResponseDto 생성 테스트 - createdAt과 updatedAt 다름")
    void createPostResponseDto_WithDifferentTimestamps() {
        // Given
        LocalDateTime created = LocalDateTime.now().minusHours(1);
        LocalDateTime updated = LocalDateTime.now();
        when(mockPost.getCreatedAt()).thenReturn(created);
        when(mockPost.getUpdatedAt()).thenReturn(updated);

        // When
        PostResponseDto dto = new PostResponseDto(mockPost);

        // Then
        assertNotNull(dto);
        assertEquals(created, dto.getCreatedAt());
        assertEquals(updated, dto.getUpdatedAt());
        assertTrue(dto.getUpdatedAt().isAfter(dto.getCreatedAt()));
    }

    @Test
    @DisplayName("PostResponseDto 생성 테스트 - null 값들 처리")
    void createPostResponseDto_WithNullValues() {
        // Given
        when(mockPost.getTitle()).thenReturn(null);
        when(mockPost.getContent()).thenReturn(null);
        when(mockPost.getThumbImageUrl()).thenReturn(null);

        // When
        PostResponseDto dto = new PostResponseDto(mockPost);

        // Then
        assertNotNull(dto);
        assertNull(dto.getTitle());
        assertNull(dto.getContent());
        assertNull(dto.getThumbImageUrl());
    }

    @Test
    @DisplayName("PostResponseDto getter 메서드 테스트")
    void testGetterMethods() {
        // Given
        PostResponseDto dto = new PostResponseDto(mockPost, true);

        // When & Then
        assertEquals(1L, dto.getPostId());
        assertEquals("테스트 게시물", dto.getTitle());
        assertEquals("테스트 내용", dto.getContent());
        assertEquals("test-image.jpg", dto.getThumbImageUrl());
        assertEquals("testUser", dto.getAuthorNickname());
        assertEquals(10, dto.getViewCount());
        assertEquals(5, dto.getLikeCount());
        assertTrue(dto.isLiked());
        assertNotNull(dto.getCreatedAt());
        assertNotNull(dto.getUpdatedAt());
    }

    @Test
    @DisplayName("PostResponseDto toString 메서드 동작 확인")
    void testToStringMethod() {
        // Given
        PostResponseDto dto = new PostResponseDto(mockPost);

        // When
        String toStringResult = dto.toString();

        // Then
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("PostResponseDto"));
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
        Post post = mock(Post.class);
        when(post.getPostId()).thenReturn(1L);
        when(post.getTitle()).thenReturn("테스트 게시물");
        when(post.getContent()).thenReturn("테스트 내용");
        when(post.getThumbImageUrl()).thenReturn("test-image.jpg");
        when(post.getAuthor()).thenReturn(mockMember);
        when(post.getViewCount()).thenReturn(10);
        when(post.getLikeCount()).thenReturn(5);
        when(post.getCreatedAt()).thenReturn(LocalDateTime.now().minusHours(1));
        when(post.getUpdatedAt()).thenReturn(LocalDateTime.now());
        return post;
    }
}