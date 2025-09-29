package com.example.petner.domain.favorite.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FavoriteActionResponseDtoTest {

    @Test
    @DisplayName("FavoriteActionResponseDto 생성 테스트")
    void createFavoriteActionResponseDto_Success() {
        // Given
        Long memberId = 1L;
        Long dogId = 1L;
        String message = "즐겨찾기 제거 성공";

        // When
        FavoriteActionResponseDto dto = new FavoriteActionResponseDto(memberId, dogId, message);

        // Then
        assertNotNull(dto);
        assertEquals(memberId, dto.getMemberId());
        assertEquals(dogId, dto.getDogId());
        assertEquals(message, dto.getMessage());
    }

    @Test
    @DisplayName("FavoriteActionResponseDto getter 메서드 테스트")
    void testGetterMethods() {
        // Given
        Long memberId = 123L;
        Long dogId = 456L;
        String message = "테스트 메시지";
        FavoriteActionResponseDto dto = new FavoriteActionResponseDto(memberId, dogId, message);

        // When & Then
        assertEquals(123L, dto.getMemberId());
        assertEquals(456L, dto.getDogId());
        assertEquals("테스트 메시지", dto.getMessage());
    }

    @Test
    @DisplayName("FavoriteActionResponseDto 생성 테스트 - 즐겨찾기 추가")
    void createFavoriteActionResponseDto_Add() {
        // Given
        Long memberId = 10L;
        Long dogId = 20L;
        String message = "즐겨찾기 추가 성공";

        // When
        FavoriteActionResponseDto dto = new FavoriteActionResponseDto(memberId, dogId, message);

        // Then
        assertNotNull(dto);
        assertEquals(10L, dto.getMemberId());
        assertEquals(20L, dto.getDogId());
        assertEquals("즐겨찾기 추가 성공", dto.getMessage());
    }

    @Test
    @DisplayName("FavoriteActionResponseDto 생성 테스트 - 즐겨찾기 제거")
    void createFavoriteActionResponseDto_Remove() {
        // Given
        Long memberId = 30L;
        Long dogId = 40L;
        String message = "즐겨찾기 제거 성공";

        // When
        FavoriteActionResponseDto dto = new FavoriteActionResponseDto(memberId, dogId, message);

        // Then
        assertNotNull(dto);
        assertEquals(30L, dto.getMemberId());
        assertEquals(40L, dto.getDogId());
        assertEquals("즐겨찾기 제거 성공", dto.getMessage());
    }

    @Test
    @DisplayName("FavoriteActionResponseDto 생성 테스트 - 높은 ID 값")
    void createFavoriteActionResponseDto_WithHighIds() {
        // Given
        Long highMemberId = Long.MAX_VALUE;
        Long highDogId = Long.MAX_VALUE - 1;
        String message = "높은 ID 테스트";

        // When
        FavoriteActionResponseDto dto = new FavoriteActionResponseDto(highMemberId, highDogId, message);

        // Then
        assertNotNull(dto);
        assertEquals(Long.MAX_VALUE, dto.getMemberId());
        assertEquals(Long.MAX_VALUE - 1, dto.getDogId());
        assertEquals("높은 ID 테스트", dto.getMessage());
    }

    @Test
    @DisplayName("FavoriteActionResponseDto 생성 테스트 - 0 ID 값")
    void createFavoriteActionResponseDto_WithZeroIds() {
        // Given
        Long memberId = 0L;
        Long dogId = 0L;
        String message = "0 ID 테스트";

        // When
        FavoriteActionResponseDto dto = new FavoriteActionResponseDto(memberId, dogId, message);

        // Then
        assertNotNull(dto);
        assertEquals(0L, dto.getMemberId());
        assertEquals(0L, dto.getDogId());
        assertEquals("0 ID 테스트", dto.getMessage());
    }

    @Test
    @DisplayName("FavoriteActionResponseDto 생성 테스트 - 긴 메시지")
    void createFavoriteActionResponseDto_WithLongMessage() {
        // Given
        Long memberId = 1L;
        Long dogId = 2L;
        String longMessage = "매우 긴 메시지입니다. ".repeat(20);

        // When
        FavoriteActionResponseDto dto = new FavoriteActionResponseDto(memberId, dogId, longMessage);

        // Then
        assertNotNull(dto);
        assertEquals(1L, dto.getMemberId());
        assertEquals(2L, dto.getDogId());
        assertEquals(longMessage, dto.getMessage());
        assertTrue(dto.getMessage().length() > 100);
    }

    @Test
    @DisplayName("FavoriteActionResponseDto 생성 테스트 - 빈 메시지")
    void createFavoriteActionResponseDto_WithEmptyMessage() {
        // Given
        Long memberId = 5L;
        Long dogId = 6L;
        String emptyMessage = "";

        // When
        FavoriteActionResponseDto dto = new FavoriteActionResponseDto(memberId, dogId, emptyMessage);

        // Then
        assertNotNull(dto);
        assertEquals(5L, dto.getMemberId());
        assertEquals(6L, dto.getDogId());
        assertEquals("", dto.getMessage());
        assertTrue(dto.getMessage().isEmpty());
    }

    @Test
    @DisplayName("FavoriteActionResponseDto 생성 테스트 - null 메시지")
    void createFavoriteActionResponseDto_WithNullMessage() {
        // Given
        Long memberId = 7L;
        Long dogId = 8L;
        String nullMessage = null;

        // When
        FavoriteActionResponseDto dto = new FavoriteActionResponseDto(memberId, dogId, nullMessage);

        // Then
        assertNotNull(dto);
        assertEquals(7L, dto.getMemberId());
        assertEquals(8L, dto.getDogId());
        assertNull(dto.getMessage());
    }

    @Test
    @DisplayName("FavoriteActionResponseDto 생성 테스트 - 특수문자 포함 메시지")
    void createFavoriteActionResponseDto_WithSpecialCharacters() {
        // Given
        Long memberId = 9L;
        Long dogId = 10L;
        String messageWithSpecialChars = "즐겨찾기 처리 완료! @#$%^&*()_+-=[]{}|;':\",./<>?";

        // When
        FavoriteActionResponseDto dto = new FavoriteActionResponseDto(memberId, dogId, messageWithSpecialChars);

        // Then
        assertNotNull(dto);
        assertEquals(9L, dto.getMemberId());
        assertEquals(10L, dto.getDogId());
        assertEquals(messageWithSpecialChars, dto.getMessage());
    }

    @Test
    @DisplayName("FavoriteActionResponseDto toString 메서드 동작 확인")
    void testToStringMethod() {
        // Given
        FavoriteActionResponseDto dto = new FavoriteActionResponseDto(1L, 2L, "테스트");

        // When
        String toStringResult = dto.toString();

        // Then
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("FavoriteActionResponseDto"));
    }

    @Test
    @DisplayName("FavoriteActionResponseDto 필드 null 체크")
    void createFavoriteActionResponseDto_FieldsCheck() {
        // Given
        FavoriteActionResponseDto dto = new FavoriteActionResponseDto(100L, 200L, "성공");

        // When & Then
        assertAll(
                () -> assertNotNull(dto.getMemberId()),
                () -> assertNotNull(dto.getDogId()),
                () -> assertNotNull(dto.getMessage())
        );
    }

    @Test
    @DisplayName("FavoriteActionResponseDto 필드 타입 검증")
    void createFavoriteActionResponseDto_FieldTypes() {
        // Given
        FavoriteActionResponseDto dto = new FavoriteActionResponseDto(1L, 2L, "메시지");

        // When & Then
        assertTrue(dto.getMemberId() instanceof Long);
        assertTrue(dto.getDogId() instanceof Long);
        assertTrue(dto.getMessage() instanceof String);
    }

    @Test
    @DisplayName("FavoriteActionResponseDto 생성 테스트 - 동일 파라미터로 여러 번 생성")
    void createFavoriteActionResponseDto_MultipleInstances() {
        // Given
        Long memberId = 1L;
        Long dogId = 2L;
        String message = "테스트 메시지";

        // When
        FavoriteActionResponseDto dto1 = new FavoriteActionResponseDto(memberId, dogId, message);
        FavoriteActionResponseDto dto2 = new FavoriteActionResponseDto(memberId, dogId, message);

        // Then
        assertNotSame(dto1, dto2); // 다른 인스턴스
        assertEquals(dto1.getMemberId(), dto2.getMemberId()); // 같은 값
        assertEquals(dto1.getDogId(), dto2.getDogId());
        assertEquals(dto1.getMessage(), dto2.getMessage());
    }

    @Test
    @DisplayName("FavoriteActionResponseDto 생성 테스트 - 다양한 성공 메시지")
    void createFavoriteActionResponseDto_VariousSuccessMessages() {
        // Given
        String[] messages = {
                "즐겨찾기 추가 성공",
                "즐겨찾기 제거 성공",
                "Favorite added successfully",
                "Favorite removed successfully",
                "Success"
        };

        for (int i = 0; i < messages.length; i++) {
            // When
            FavoriteActionResponseDto dto = new FavoriteActionResponseDto((long) i, (long) i + 10, messages[i]);

            // Then
            assertNotNull(dto);
            assertEquals((long) i, dto.getMemberId());
            assertEquals((long) i + 10, dto.getDogId());
            assertEquals(messages[i], dto.getMessage());
        }
    }
}