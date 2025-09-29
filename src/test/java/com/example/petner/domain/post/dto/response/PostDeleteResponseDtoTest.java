package com.example.petner.domain.post.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PostDeleteResponseDtoTest {

    @Test
    @DisplayName("PostDeleteResponseDto 생성 테스트 - 일반 생성자")
    void createPostDeleteResponseDto_BasicConstructor() {
        // Given
        Long postId = 1L;
        Long memberId = 2L;
        String message = "게시글이 삭제되었습니다.";

        // When
        PostDeleteResponseDto dto = new PostDeleteResponseDto(postId, memberId, message);

        // Then
        assertNotNull(dto);
        assertEquals(postId, dto.getPostId());
        assertEquals(memberId, dto.getMemberId());
        assertEquals(message, dto.getMessage());
        assertTrue(dto.isSuccess()); // 기본값은 true
    }

    @Test
    @DisplayName("PostDeleteResponseDto 생성 테스트 - success 정적 메서드")
    void createPostDeleteResponseDto_SuccessStaticMethod() {
        // Given
        Long postId = 1L;
        Long memberId = 2L;

        // When
        PostDeleteResponseDto dto = PostDeleteResponseDto.success(postId, memberId);

        // Then
        assertNotNull(dto);
        assertEquals(postId, dto.getPostId());
        assertEquals(memberId, dto.getMemberId());
        assertEquals("게시글이 성공적으로 삭제되었습니다.", dto.getMessage());
        assertTrue(dto.isSuccess());
    }

    @Test
    @DisplayName("PostDeleteResponseDto 생성 테스트 - null 값들")
    void createPostDeleteResponseDto_WithNullValues() {
        // When
        PostDeleteResponseDto dto = new PostDeleteResponseDto(null, null, null);

        // Then
        assertNotNull(dto);
        assertNull(dto.getPostId());
        assertNull(dto.getMemberId());
        assertNull(dto.getMessage());
        assertTrue(dto.isSuccess()); // success는 항상 true로 설정됨
    }

    @Test
    @DisplayName("PostDeleteResponseDto 생성 테스트 - 0 ID 값들")
    void createPostDeleteResponseDto_WithZeroIds() {
        // Given
        Long postId = 0L;
        Long memberId = 0L;
        String message = "테스트 메시지";

        // When
        PostDeleteResponseDto dto = new PostDeleteResponseDto(postId, memberId, message);

        // Then
        assertNotNull(dto);
        assertEquals(0L, dto.getPostId());
        assertEquals(0L, dto.getMemberId());
        assertEquals(message, dto.getMessage());
        assertTrue(dto.isSuccess());
    }

    @Test
    @DisplayName("PostDeleteResponseDto 생성 테스트 - 음수 ID 값들")
    void createPostDeleteResponseDto_WithNegativeIds() {
        // Given
        Long postId = -1L;
        Long memberId = -2L;
        String message = "음수 ID 테스트";

        // When
        PostDeleteResponseDto dto = new PostDeleteResponseDto(postId, memberId, message);

        // Then
        assertNotNull(dto);
        assertEquals(-1L, dto.getPostId());
        assertEquals(-2L, dto.getMemberId());
        assertEquals(message, dto.getMessage());
        assertTrue(dto.isSuccess());
    }

    @Test
    @DisplayName("PostDeleteResponseDto 생성 테스트 - 매우 큰 ID 값들")
    void createPostDeleteResponseDto_WithLargeIds() {
        // Given
        Long postId = Long.MAX_VALUE;
        Long memberId = Long.MAX_VALUE - 1;
        String message = "큰 ID 테스트";

        // When
        PostDeleteResponseDto dto = new PostDeleteResponseDto(postId, memberId, message);

        // Then
        assertNotNull(dto);
        assertEquals(Long.MAX_VALUE, dto.getPostId());
        assertEquals(Long.MAX_VALUE - 1, dto.getMemberId());
        assertEquals(message, dto.getMessage());
        assertTrue(dto.isSuccess());
    }

    @Test
    @DisplayName("PostDeleteResponseDto 생성 테스트 - 빈 메시지")
    void createPostDeleteResponseDto_WithEmptyMessage() {
        // Given
        Long postId = 1L;
        Long memberId = 2L;
        String emptyMessage = "";

        // When
        PostDeleteResponseDto dto = new PostDeleteResponseDto(postId, memberId, emptyMessage);

        // Then
        assertNotNull(dto);
        assertEquals(postId, dto.getPostId());
        assertEquals(memberId, dto.getMemberId());
        assertEquals("", dto.getMessage());
        assertTrue(dto.isSuccess());
    }

    @Test
    @DisplayName("PostDeleteResponseDto 생성 테스트 - 긴 메시지")
    void createPostDeleteResponseDto_WithLongMessage() {
        // Given
        Long postId = 1L;
        Long memberId = 2L;
        String longMessage = "매우 긴 메시지입니다. ".repeat(100);

        // When
        PostDeleteResponseDto dto = new PostDeleteResponseDto(postId, memberId, longMessage);

        // Then
        assertNotNull(dto);
        assertEquals(postId, dto.getPostId());
        assertEquals(memberId, dto.getMemberId());
        assertEquals(longMessage, dto.getMessage());
        assertTrue(dto.getMessage().length() > 1000);
        assertTrue(dto.isSuccess());
    }

    @Test
    @DisplayName("PostDeleteResponseDto 생성 테스트 - 한글 메시지")
    void createPostDeleteResponseDto_WithKoreanMessage() {
        // Given
        Long postId = 1L;
        Long memberId = 2L;
        String koreanMessage = "게시글이 성공적으로 삭제되었습니다.";

        // When
        PostDeleteResponseDto dto = new PostDeleteResponseDto(postId, memberId, koreanMessage);

        // Then
        assertNotNull(dto);
        assertEquals(koreanMessage, dto.getMessage());
        assertTrue(dto.getMessage().contains("게시글"));
        assertTrue(dto.getMessage().contains("삭제"));
    }

    @Test
    @DisplayName("PostDeleteResponseDto 생성 테스트 - 특수문자 포함 메시지")
    void createPostDeleteResponseDto_WithSpecialCharacters() {
        // Given
        Long postId = 1L;
        Long memberId = 2L;
        String specialMessage = "삭제 완료! @#$%^&*()_+{}|:<>?[]\\;',./";

        // When
        PostDeleteResponseDto dto = new PostDeleteResponseDto(postId, memberId, specialMessage);

        // Then
        assertNotNull(dto);
        assertEquals(specialMessage, dto.getMessage());
        assertTrue(dto.getMessage().contains("@#$%"));
    }

    @Test
    @DisplayName("PostDeleteResponseDto success 정적 메서드 테스트 - 다양한 ID")
    void testSuccessStaticMethod_WithVariousIds() {
        // Given & When
        PostDeleteResponseDto dto1 = PostDeleteResponseDto.success(1L, 100L);
        PostDeleteResponseDto dto2 = PostDeleteResponseDto.success(999999L, 1L);
        PostDeleteResponseDto dto3 = PostDeleteResponseDto.success(0L, 0L);

        // Then
        assertEquals(1L, dto1.getPostId());
        assertEquals(100L, dto1.getMemberId());
        assertEquals("게시글이 성공적으로 삭제되었습니다.", dto1.getMessage());
        assertTrue(dto1.isSuccess());

        assertEquals(999999L, dto2.getPostId());
        assertEquals(1L, dto2.getMemberId());
        assertTrue(dto2.isSuccess());

        assertEquals(0L, dto3.getPostId());
        assertEquals(0L, dto3.getMemberId());
        assertTrue(dto3.isSuccess());
    }

    @Test
    @DisplayName("PostDeleteResponseDto getter 메서드 테스트")
    void testGetterMethods() {
        // Given
        Long postId = 123L;
        Long memberId = 456L;
        String message = "테스트 메시지";
        PostDeleteResponseDto dto = new PostDeleteResponseDto(postId, memberId, message);

        // When & Then
        assertEquals(postId, dto.getPostId());
        assertEquals(memberId, dto.getMemberId());
        assertEquals(message, dto.getMessage());
        assertTrue(dto.isSuccess());
    }

    @Test
    @DisplayName("PostDeleteResponseDto toString 메서드 동작 확인")
    void testToStringMethod() {
        // Given
        PostDeleteResponseDto dto = PostDeleteResponseDto.success(1L, 2L);

        // When
        String toStringResult = dto.toString();

        // Then
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("PostDeleteResponseDto"));
    }

    @Test
    @DisplayName("PostDeleteResponseDto 불변성 테스트")
    void testImmutability() {
        // Given
        Long originalPostId = 1L;
        Long originalMemberId = 2L;
        String originalMessage = "원본 메시지";

        PostDeleteResponseDto dto = new PostDeleteResponseDto(originalPostId, originalMemberId, originalMessage);

        // When - getter로 얻은 값들이 원본과 같은지 확인
        Long retrievedPostId = dto.getPostId();
        Long retrievedMemberId = dto.getMemberId();
        String retrievedMessage = dto.getMessage();

        // Then
        assertEquals(originalPostId, retrievedPostId);
        assertEquals(originalMemberId, retrievedMemberId);
        assertEquals(originalMessage, retrievedMessage);

        // 객체 생성 후 값이 변경되지 않는지 확인 (final 필드이므로 컴파일 타임에 보장됨)
        assertSame(originalPostId, dto.getPostId());
        assertSame(originalMemberId, dto.getMemberId());
        assertSame(originalMessage, dto.getMessage());
    }

    @Test
    @DisplayName("PostDeleteResponseDto 객체 동등성 테스트")
    void testObjectEquality() {
        // Given
        Long postId = 1L;
        Long memberId = 2L;
        String message = "테스트 메시지";

        PostDeleteResponseDto dto1 = new PostDeleteResponseDto(postId, memberId, message);
        PostDeleteResponseDto dto2 = new PostDeleteResponseDto(postId, memberId, message);
        PostDeleteResponseDto dto3 = PostDeleteResponseDto.success(postId, memberId);

        // Then - 같은 값으로 생성된 객체들의 필드값 비교
        assertEquals(dto1.getPostId(), dto2.getPostId());
        assertEquals(dto1.getMemberId(), dto2.getMemberId());
        assertEquals(dto1.getMessage(), dto2.getMessage());
        assertEquals(dto1.isSuccess(), dto2.isSuccess());

        // static method로 생성된 객체와 비교
        assertEquals(dto1.getPostId(), dto3.getPostId());
        assertEquals(dto1.getMemberId(), dto3.getMemberId());
        assertNotEquals(dto1.getMessage(), dto3.getMessage()); // 메시지가 다름
        assertEquals(dto1.isSuccess(), dto3.isSuccess());
    }
}