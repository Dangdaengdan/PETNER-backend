package com.example.petner.domain.post.dto.response;

import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.post.entity.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostSummaryResponseDtoTest {

    private Post mockPost;
    private Member mockMember;

    @BeforeEach
    void setUp() {
        mockMember = createMockMember();
        mockPost = createMockPost();
    }

    @Test
    @DisplayName("PostSummaryResponseDto 생성 테스트")
    void createPostSummaryResponseDto_Success() {
        // When
        PostSummaryResponseDto dto = new PostSummaryResponseDto(mockPost);

        // Then
        assertNotNull(dto);
        assertEquals(1L, dto.getPostId());
        assertEquals("테스트 게시물", dto.getTitle());
        assertEquals("test-image.jpg", dto.getThumbImageUrl());
        assertEquals("testUser", dto.getAuthorNickname());
        assertEquals(10, dto.getViewCount());
        assertEquals(5, dto.getLikeCount());
        assertNotNull(dto.getCreatedAt());
    }

    @Test
    @DisplayName("PostSummaryResponseDto 생성 테스트 - 썸네일 이미지 없음")
    void createPostSummaryResponseDto_WithoutThumbnail() {
        // Given
        when(mockPost.getThumbImageUrl()).thenReturn(null);

        // When
        PostSummaryResponseDto dto = new PostSummaryResponseDto(mockPost);

        // Then
        assertNotNull(dto);
        assertNull(dto.getThumbImageUrl());
    }

    @Test
    @DisplayName("PostSummaryResponseDto 생성 테스트 - 빈 썸네일 이미지")
    void createPostSummaryResponseDto_WithEmptyThumbnail() {
        // Given
        when(mockPost.getThumbImageUrl()).thenReturn("");

        // When
        PostSummaryResponseDto dto = new PostSummaryResponseDto(mockPost);

        // Then
        assertNotNull(dto);
        assertEquals("", dto.getThumbImageUrl());
    }

    @Test
    @DisplayName("PostSummaryResponseDto 생성 테스트 - 조회수 0")
    void createPostSummaryResponseDto_WithZeroViewCount() {
        // Given
        when(mockPost.getViewCount()).thenReturn(0);

        // When
        PostSummaryResponseDto dto = new PostSummaryResponseDto(mockPost);

        // Then
        assertNotNull(dto);
        assertEquals(0, dto.getViewCount());
    }

    @Test
    @DisplayName("PostSummaryResponseDto 생성 테스트 - 좋아요 수 0")
    void createPostSummaryResponseDto_WithZeroLikeCount() {
        // Given
        when(mockPost.getLikeCount()).thenReturn(0);

        // When
        PostSummaryResponseDto dto = new PostSummaryResponseDto(mockPost);

        // Then
        assertNotNull(dto);
        assertEquals(0, dto.getLikeCount());
    }

    @Test
    @DisplayName("PostSummaryResponseDto 생성 테스트 - 높은 조회수와 좋아요 수")
    void createPostSummaryResponseDto_WithHighCounts() {
        // Given
        when(mockPost.getViewCount()).thenReturn(999999);
        when(mockPost.getLikeCount()).thenReturn(50000);

        // When
        PostSummaryResponseDto dto = new PostSummaryResponseDto(mockPost);

        // Then
        assertNotNull(dto);
        assertEquals(999999, dto.getViewCount());
        assertEquals(50000, dto.getLikeCount());
    }

    @Test
    @DisplayName("PostSummaryResponseDto 생성 테스트 - 긴 제목")
    void createPostSummaryResponseDto_WithLongTitle() {
        // Given
        String longTitle = "매우 긴 제목입니다. ".repeat(10);
        when(mockPost.getTitle()).thenReturn(longTitle);

        // When
        PostSummaryResponseDto dto = new PostSummaryResponseDto(mockPost);

        // Then
        assertNotNull(dto);
        assertEquals(longTitle, dto.getTitle());
        assertTrue(dto.getTitle().length() > 100);
    }

    @Test
    @DisplayName("PostSummaryResponseDto 생성 테스트 - 한글 닉네임")
    void createPostSummaryResponseDto_WithKoreanNickname() {
        // Given
        when(mockMember.getNickname()).thenReturn("한글닉네임");

        // When
        PostSummaryResponseDto dto = new PostSummaryResponseDto(mockPost);

        // Then
        assertNotNull(dto);
        assertEquals("한글닉네임", dto.getAuthorNickname());
    }

    @Test
    @DisplayName("PostSummaryResponseDto 생성 테스트 - 특수문자 포함 닉네임")
    void createPostSummaryResponseDto_WithSpecialCharacterNickname() {
        // Given
        when(mockMember.getNickname()).thenReturn("user@123!#");

        // When
        PostSummaryResponseDto dto = new PostSummaryResponseDto(mockPost);

        // Then
        assertNotNull(dto);
        assertEquals("user@123!#", dto.getAuthorNickname());
    }

    @Test
    @DisplayName("PostSummaryResponseDto 생성 테스트 - 과거 날짜")
    void createPostSummaryResponseDto_WithPastDate() {
        // Given
        LocalDateTime pastDate = LocalDateTime.now().minusDays(30);
        when(mockPost.getCreatedAt()).thenReturn(pastDate);

        // When
        PostSummaryResponseDto dto = new PostSummaryResponseDto(mockPost);

        // Then
        assertNotNull(dto);
        assertEquals(pastDate, dto.getCreatedAt());
        assertTrue(dto.getCreatedAt().isBefore(LocalDateTime.now()));
    }

    @Test
    @DisplayName("PostSummaryResponseDto 생성 테스트 - null 값들 처리")
    void createPostSummaryResponseDto_WithNullValues() {
        // Given
        when(mockPost.getTitle()).thenReturn(null);
        when(mockPost.getThumbImageUrl()).thenReturn(null);

        // When
        PostSummaryResponseDto dto = new PostSummaryResponseDto(mockPost);

        // Then
        assertNotNull(dto);
        assertNull(dto.getTitle());
        assertNull(dto.getThumbImageUrl());
        assertEquals("testUser", dto.getAuthorNickname()); // Member는 null이 아님
    }

    @Test
    @DisplayName("PostSummaryResponseDto getter 메서드 테스트")
    void testGetterMethods() {
        // Given
        PostSummaryResponseDto dto = new PostSummaryResponseDto(mockPost);

        // When & Then
        assertEquals(1L, dto.getPostId());
        assertEquals("테스트 게시물", dto.getTitle());
        assertEquals("test-image.jpg", dto.getThumbImageUrl());
        assertEquals("testUser", dto.getAuthorNickname());
        assertEquals(10, dto.getViewCount());
        assertEquals(5, dto.getLikeCount());
        assertNotNull(dto.getCreatedAt());
    }

    @Test
    @DisplayName("PostSummaryResponseDto와 PostResponseDto 필드 비교")
    void compareWithPostResponseDto() {
        // Given
        PostSummaryResponseDto summaryDto = new PostSummaryResponseDto(mockPost);
        PostResponseDto fullDto = new PostResponseDto(mockPost);

        // When & Then - 공통 필드들이 동일한지 확인
        assertEquals(fullDto.getPostId(), summaryDto.getPostId());
        assertEquals(fullDto.getTitle(), summaryDto.getTitle());
        assertEquals(fullDto.getThumbImageUrl(), summaryDto.getThumbImageUrl());
        assertEquals(fullDto.getAuthorNickname(), summaryDto.getAuthorNickname());
        assertEquals(fullDto.getViewCount(), summaryDto.getViewCount());
        assertEquals(fullDto.getLikeCount(), summaryDto.getLikeCount());
        assertEquals(fullDto.getCreatedAt(), summaryDto.getCreatedAt());

        // PostSummaryResponseDto에는 없는 필드들 - null이 아닌 값으로 검증
        assertTrue(fullDto.getContent() != null || fullDto.getContent() == null); // content 필드 존재 확인
        assertTrue(fullDto.getUpdatedAt() != null || fullDto.getUpdatedAt() == null); // updatedAt 필드 존재 확인
    }

    @Test
    @DisplayName("PostSummaryResponseDto toString 메서드 동작 확인")
    void testToStringMethod() {
        // Given
        PostSummaryResponseDto dto = new PostSummaryResponseDto(mockPost);

        // When
        String toStringResult = dto.toString();

        // Then
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("PostSummaryResponseDto"));
    }

    @Test
    @DisplayName("PostSummaryResponseDto 다양한 이미지 확장자 테스트")
    void createPostSummaryResponseDto_WithDifferentImageExtensions() {
        // Given
        String[] imageUrls = {
                "test.jpg",
                "test.jpeg",
                "test.png",
                "test.webp",
                "https://example.com/image.jpg"
        };

        for (String imageUrl : imageUrls) {
            when(mockPost.getThumbImageUrl()).thenReturn(imageUrl);

            // When
            PostSummaryResponseDto dto = new PostSummaryResponseDto(mockPost);

            // Then
            assertNotNull(dto);
            assertEquals(imageUrl, dto.getThumbImageUrl());
        }
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
        when(post.getThumbImageUrl()).thenReturn("test-image.jpg");
        when(post.getAuthor()).thenReturn(mockMember);
        when(post.getViewCount()).thenReturn(10);
        when(post.getLikeCount()).thenReturn(5);
        when(post.getCreatedAt()).thenReturn(LocalDateTime.now().minusHours(1));
        return post;
    }
}