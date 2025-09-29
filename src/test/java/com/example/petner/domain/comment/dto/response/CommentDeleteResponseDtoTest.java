package com.example.petner.domain.comment.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommentDeleteResponseDtoTest {

    @Test
    @DisplayName("CommentDeleteResponseDto 생성자 테스트")
    void createCommentDeleteResponseDto_Constructor_Success() {
        // Given
        Long commentId = 1L;
        Long memberId = 1L;
        String message = "댓글이 성공적으로 삭제되었습니다.";

        // When
        CommentDeleteResponseDto dto = new CommentDeleteResponseDto(commentId, memberId, message);

        // Then
        assertNotNull(dto);
        assertEquals(commentId, dto.getCommentId());
        assertEquals(memberId, dto.getMemberId());
        assertEquals(message, dto.getMessage());
        assertTrue(dto.isSuccess());
    }

    @Test
    @DisplayName("CommentDeleteResponseDto static 팩토리 메서드 테스트")
    void createCommentDeleteResponseDto_StaticFactory_Success() {
        // Given
        Long commentId = 1L;
        Long memberId = 1L;

        // When
        CommentDeleteResponseDto dto = CommentDeleteResponseDto.success(commentId, memberId);

        // Then
        assertNotNull(dto);
        assertEquals(commentId, dto.getCommentId());
        assertEquals(memberId, dto.getMemberId());
        assertEquals("댓글이 성공적으로 삭제되었습니다.", dto.getMessage());
        assertTrue(dto.isSuccess());
    }

    @Test
    @DisplayName("CommentDeleteResponseDto getter 메서드 테스트")
    void testGetterMethods() {
        // Given
        Long commentId = 123L;
        Long memberId = 456L;
        String message = "테스트 메시지";
        CommentDeleteResponseDto dto = new CommentDeleteResponseDto(commentId, memberId, message);

        // When & Then
        assertEquals(123L, dto.getCommentId());
        assertEquals(456L, dto.getMemberId());
        assertEquals("테스트 메시지", dto.getMessage());
        assertTrue(dto.isSuccess());
    }

    @Test
    @DisplayName("CommentDeleteResponseDto 생성 테스트 - 높은 ID 값")
    void createCommentDeleteResponseDto_WithHighIds() {
        // Given
        Long highCommentId = Long.MAX_VALUE;
        Long highMemberId = Long.MAX_VALUE - 1;
        String message = "높은 ID 테스트";

        // When
        CommentDeleteResponseDto dto = new CommentDeleteResponseDto(highCommentId, highMemberId, message);

        // Then
        assertEquals(Long.MAX_VALUE, dto.getCommentId());
        assertEquals(Long.MAX_VALUE - 1, dto.getMemberId());
        assertEquals("높은 ID 테스트", dto.getMessage());
        assertTrue(dto.isSuccess());
    }

    @Test
    @DisplayName("CommentDeleteResponseDto 생성 테스트 - 0 ID 값")
    void createCommentDeleteResponseDto_WithZeroIds() {
        // Given
        Long commentId = 0L;
        Long memberId = 0L;
        String message = "0 ID 테스트";

        // When
        CommentDeleteResponseDto dto = new CommentDeleteResponseDto(commentId, memberId, message);

        // Then
        assertEquals(0L, dto.getCommentId());
        assertEquals(0L, dto.getMemberId());
        assertEquals("0 ID 테스트", dto.getMessage());
        assertTrue(dto.isSuccess());
    }

    @Test
    @DisplayName("CommentDeleteResponseDto 생성 테스트 - null 메시지")
    void createCommentDeleteResponseDto_WithNullMessage() {
        // Given
        Long commentId = 1L;
        Long memberId = 1L;
        String nullMessage = null;

        // When
        CommentDeleteResponseDto dto = new CommentDeleteResponseDto(commentId, memberId, nullMessage);

        // Then
        assertEquals(1L, dto.getCommentId());
        assertEquals(1L, dto.getMemberId());
        assertNull(dto.getMessage());
        assertTrue(dto.isSuccess());
    }

    @Test
    @DisplayName("CommentDeleteResponseDto 생성 테스트 - 빈 메시지")
    void createCommentDeleteResponseDto_WithEmptyMessage() {
        // Given
        Long commentId = 1L;
        Long memberId = 1L;
        String emptyMessage = "";

        // When
        CommentDeleteResponseDto dto = new CommentDeleteResponseDto(commentId, memberId, emptyMessage);

        // Then
        assertEquals(1L, dto.getCommentId());
        assertEquals(1L, dto.getMemberId());
        assertEquals("", dto.getMessage());
        assertTrue(dto.isSuccess());
        assertTrue(dto.getMessage().isEmpty());
    }

    @Test
    @DisplayName("CommentDeleteResponseDto 생성 테스트 - 긴 메시지")
    void createCommentDeleteResponseDto_WithLongMessage() {
        // Given
        Long commentId = 1L;
        Long memberId = 1L;
        String longMessage = "매우 긴 메시지입니다. ".repeat(50);

        // When
        CommentDeleteResponseDto dto = new CommentDeleteResponseDto(commentId, memberId, longMessage);

        // Then
        assertEquals(1L, dto.getCommentId());
        assertEquals(1L, dto.getMemberId());
        assertEquals(longMessage, dto.getMessage());
        assertTrue(dto.isSuccess());
        assertTrue(dto.getMessage().length() > 500);
    }

    @Test
    @DisplayName("CommentDeleteResponseDto 생성 테스트 - 특수문자 포함 메시지")
    void createCommentDeleteResponseDto_WithSpecialCharacters() {
        // Given
        Long commentId = 1L;
        Long memberId = 1L;
        String messageWithSpecialChars = "댓글 삭제 완료! @#$%^&*()_+-=[]{}|;':\",./<>?";

        // When
        CommentDeleteResponseDto dto = new CommentDeleteResponseDto(commentId, memberId, messageWithSpecialChars);

        // Then
        assertEquals(1L, dto.getCommentId());
        assertEquals(1L, dto.getMemberId());
        assertEquals(messageWithSpecialChars, dto.getMessage());
        assertTrue(dto.isSuccess());
    }

    @Test
    @DisplayName("CommentDeleteResponseDto static 팩토리 메서드 - 다양한 ID 조합")
    void createCommentDeleteResponseDto_StaticFactory_VariousIds() {
        // Given
        Long[][] idPairs = {
                {1L, 1L},
                {1L, 2L},
                {100L, 200L},
                {999L, 888L}
        };

        for (Long[] pair : idPairs) {
            Long commentId = pair[0];
            Long memberId = pair[1];

            // When
            CommentDeleteResponseDto dto = CommentDeleteResponseDto.success(commentId, memberId);

            // Then
            assertEquals(commentId, dto.getCommentId());
            assertEquals(memberId, dto.getMemberId());
            assertEquals("댓글이 성공적으로 삭제되었습니다.", dto.getMessage());
            assertTrue(dto.isSuccess());
        }
    }

    @Test
    @DisplayName("CommentDeleteResponseDto toString 메서드 동작 확인")
    void testToStringMethod() {
        // Given
        CommentDeleteResponseDto dto = CommentDeleteResponseDto.success(1L, 2L);

        // When
        String toStringResult = dto.toString();

        // Then
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("CommentDeleteResponseDto"));
    }

    @Test
    @DisplayName("CommentDeleteResponseDto 필드 타입 검증")
    void createCommentDeleteResponseDto_FieldTypes() {
        // Given
        CommentDeleteResponseDto dto = CommentDeleteResponseDto.success(1L, 2L);

        // When & Then
        assertTrue(dto.getCommentId() instanceof Long);
        assertTrue(dto.getMemberId() instanceof Long);
        assertTrue(dto.getMessage() instanceof String);
        // boolean 타입 검증 - primitive 타입이므로 instanceof 대신 값 체크
        assertNotNull(Boolean.valueOf(dto.isSuccess()));
    }

    @Test
    @DisplayName("CommentDeleteResponseDto 필드 null 체크")
    void createCommentDeleteResponseDto_FieldsNotNull() {
        // Given
        CommentDeleteResponseDto dto = CommentDeleteResponseDto.success(100L, 200L);

        // When & Then
        assertAll(
                () -> assertNotNull(dto.getCommentId()),
                () -> assertNotNull(dto.getMemberId()),
                () -> assertNotNull(dto.getMessage()),
                () -> assertNotNull(dto.isSuccess())
        );
    }

    @Test
    @DisplayName("CommentDeleteResponseDto 생성 테스트 - 동일 파라미터로 여러 번 생성")
    void createCommentDeleteResponseDto_MultipleInstances() {
        // Given
        Long commentId = 1L;
        Long memberId = 2L;
        String message = "테스트 메시지";

        // When
        CommentDeleteResponseDto dto1 = new CommentDeleteResponseDto(commentId, memberId, message);
        CommentDeleteResponseDto dto2 = new CommentDeleteResponseDto(commentId, memberId, message);

        // Then
        assertNotSame(dto1, dto2); // 다른 인스턴스
        assertEquals(dto1.getCommentId(), dto2.getCommentId()); // 같은 값
        assertEquals(dto1.getMemberId(), dto2.getMemberId());
        assertEquals(dto1.getMessage(), dto2.getMessage());
        assertEquals(dto1.isSuccess(), dto2.isSuccess());
    }

    @Test
    @DisplayName("CommentDeleteResponseDto static 팩토리와 생성자 결과 비교")
    void compareStaticFactoryAndConstructor() {
        // Given
        Long commentId = 1L;
        Long memberId = 2L;

        // When
        CommentDeleteResponseDto factoryDto = CommentDeleteResponseDto.success(commentId, memberId);
        CommentDeleteResponseDto constructorDto = new CommentDeleteResponseDto(commentId, memberId, "댓글이 성공적으로 삭제되었습니다.");

        // Then
        assertEquals(factoryDto.getCommentId(), constructorDto.getCommentId());
        assertEquals(factoryDto.getMemberId(), constructorDto.getMemberId());
        assertEquals(factoryDto.getMessage(), constructorDto.getMessage());
        assertEquals(factoryDto.isSuccess(), constructorDto.isSuccess());
    }

    @Test
    @DisplayName("CommentDeleteResponseDto 성공 상태 확인")
    void createCommentDeleteResponseDto_SuccessStatus() {
        // Given & When
        CommentDeleteResponseDto dto1 = CommentDeleteResponseDto.success(1L, 2L);
        CommentDeleteResponseDto dto2 = new CommentDeleteResponseDto(3L, 4L, "커스텀 메시지");

        // Then
        assertTrue(dto1.isSuccess());
        assertTrue(dto2.isSuccess());
    }

    @Test
    @DisplayName("CommentDeleteResponseDto 음수 ID 값 테스트")
    void createCommentDeleteResponseDto_WithNegativeIds() {
        // Given
        Long negativeCommentId = -1L;
        Long negativeMemberId = -10L;
        String message = "음수 ID 테스트";

        // When
        CommentDeleteResponseDto dto = new CommentDeleteResponseDto(negativeCommentId, negativeMemberId, message);

        // Then
        assertEquals(-1L, dto.getCommentId());
        assertEquals(-10L, dto.getMemberId());
        assertEquals("음수 ID 테스트", dto.getMessage());
        assertTrue(dto.isSuccess());
    }

    @Test
    @DisplayName("CommentDeleteResponseDto 다양한 성공 메시지")
    void createCommentDeleteResponseDto_VariousSuccessMessages() {
        // Given
        String[] messages = {
                "댓글이 성공적으로 삭제되었습니다.",
                "Comment deleted successfully",
                "삭제 완료",
                "Success",
                "댓글 삭제됨"
        };

        for (int i = 0; i < messages.length; i++) {
            // When
            CommentDeleteResponseDto dto = new CommentDeleteResponseDto((long) i, (long) i + 10, messages[i]);

            // Then
            assertEquals((long) i, dto.getCommentId());
            assertEquals((long) i + 10, dto.getMemberId());
            assertEquals(messages[i], dto.getMessage());
            assertTrue(dto.isSuccess());
        }
    }
}