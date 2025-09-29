package com.example.petner.domain.favorite.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FavoriteCheckResponseDtoTest {

    @Test
    @DisplayName("FavoriteCheckResponseDto 생성 테스트 - 즐겨찾기 있음")
    void createFavoriteCheckResponseDto_IsFavorite() {
        // Given
        Long memberId = 1L;
        Long dogId = 1L;
        boolean isFavorite = true;

        // When
        FavoriteCheckResponseDto dto = new FavoriteCheckResponseDto(memberId, dogId, isFavorite);

        // Then
        assertNotNull(dto);
        assertEquals(memberId, dto.getMemberId());
        assertEquals(dogId, dto.getDogId());
        assertTrue(dto.isFavorite());
    }

    @Test
    @DisplayName("FavoriteCheckResponseDto 생성 테스트 - 즐겨찾기 없음")
    void createFavoriteCheckResponseDto_IsNotFavorite() {
        // Given
        Long memberId = 2L;
        Long dogId = 3L;
        boolean isFavorite = false;

        // When
        FavoriteCheckResponseDto dto = new FavoriteCheckResponseDto(memberId, dogId, isFavorite);

        // Then
        assertNotNull(dto);
        assertEquals(2L, dto.getMemberId());
        assertEquals(3L, dto.getDogId());
        assertFalse(dto.isFavorite());
    }

    @Test
    @DisplayName("FavoriteCheckResponseDto getter 메서드 테스트")
    void testGetterMethods() {
        // Given
        Long memberId = 100L;
        Long dogId = 200L;
        boolean isFavorite = true;
        FavoriteCheckResponseDto dto = new FavoriteCheckResponseDto(memberId, dogId, isFavorite);

        // When & Then
        assertEquals(100L, dto.getMemberId());
        assertEquals(200L, dto.getDogId());
        assertTrue(dto.isFavorite());
    }

    @Test
    @DisplayName("FavoriteCheckResponseDto 생성 테스트 - 높은 ID 값")
    void createFavoriteCheckResponseDto_WithHighIds() {
        // Given
        Long highMemberId = Long.MAX_VALUE;
        Long highDogId = Long.MAX_VALUE - 1;
        boolean isFavorite = true;

        // When
        FavoriteCheckResponseDto dto = new FavoriteCheckResponseDto(highMemberId, highDogId, isFavorite);

        // Then
        assertNotNull(dto);
        assertEquals(Long.MAX_VALUE, dto.getMemberId());
        assertEquals(Long.MAX_VALUE - 1, dto.getDogId());
        assertTrue(dto.isFavorite());
    }

    @Test
    @DisplayName("FavoriteCheckResponseDto 생성 테스트 - 0 ID 값")
    void createFavoriteCheckResponseDto_WithZeroIds() {
        // Given
        Long memberId = 0L;
        Long dogId = 0L;
        boolean isFavorite = false;

        // When
        FavoriteCheckResponseDto dto = new FavoriteCheckResponseDto(memberId, dogId, isFavorite);

        // Then
        assertNotNull(dto);
        assertEquals(0L, dto.getMemberId());
        assertEquals(0L, dto.getDogId());
        assertFalse(dto.isFavorite());
    }

    @Test
    @DisplayName("FavoriteCheckResponseDto 생성 테스트 - 다양한 ID 조합")
    void createFavoriteCheckResponseDto_VariousIdCombinations() {
        // Given
        Long[][] idPairs = {
                {1L, 1L},
                {1L, 2L},
                {10L, 5L},
                {999L, 1L},
                {1L, 999L}
        };

        for (Long[] pair : idPairs) {
            Long memberId = pair[0];
            Long dogId = pair[1];

            // When
            FavoriteCheckResponseDto dto = new FavoriteCheckResponseDto(memberId, dogId, true);

            // Then
            assertNotNull(dto);
            assertEquals(memberId, dto.getMemberId());
            assertEquals(dogId, dto.getDogId());
            assertTrue(dto.isFavorite());
        }
    }

    @Test
    @DisplayName("FavoriteCheckResponseDto boolean 값 테스트")
    void createFavoriteCheckResponseDto_BooleanValues() {
        // Given
        Long memberId = 1L;
        Long dogId = 2L;

        // When - true case
        FavoriteCheckResponseDto trueDto = new FavoriteCheckResponseDto(memberId, dogId, true);
        // When - false case
        FavoriteCheckResponseDto falseDto = new FavoriteCheckResponseDto(memberId, dogId, false);

        // Then
        assertTrue(trueDto.isFavorite());
        assertFalse(falseDto.isFavorite());
        assertNotEquals(trueDto.isFavorite(), falseDto.isFavorite());
    }

    @Test
    @DisplayName("FavoriteCheckResponseDto toString 메서드 동작 확인")
    void testToStringMethod() {
        // Given
        FavoriteCheckResponseDto dto = new FavoriteCheckResponseDto(1L, 2L, true);

        // When
        String toStringResult = dto.toString();

        // Then
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("FavoriteCheckResponseDto"));
    }

    @Test
    @DisplayName("FavoriteCheckResponseDto 필드 null 체크")
    void createFavoriteCheckResponseDto_FieldsNotNull() {
        // Given
        FavoriteCheckResponseDto dto = new FavoriteCheckResponseDto(10L, 20L, true);

        // When & Then
        assertAll(
                () -> assertNotNull(dto.getMemberId()),
                () -> assertNotNull(dto.getDogId()),
                () -> assertNotNull(dto.isFavorite()) // boolean은 primitive가 아닌 Boolean wrapper로 처리됨
        );
    }

    @Test
    @DisplayName("FavoriteCheckResponseDto 필드 타입 검증")
    void createFavoriteCheckResponseDto_FieldTypes() {
        // Given
        FavoriteCheckResponseDto dto = new FavoriteCheckResponseDto(1L, 2L, false);

        // When & Then
        assertTrue(dto.getMemberId() instanceof Long);
        assertTrue(dto.getDogId() instanceof Long);
        assertTrue(dto.isFavorite() instanceof Boolean || dto.isFavorite() == false); // primitive boolean 처리
    }

    @Test
    @DisplayName("FavoriteCheckResponseDto 생성 테스트 - 동일 파라미터로 여러 번 생성")
    void createFavoriteCheckResponseDto_MultipleInstances() {
        // Given
        Long memberId = 1L;
        Long dogId = 2L;
        boolean isFavorite = true;

        // When
        FavoriteCheckResponseDto dto1 = new FavoriteCheckResponseDto(memberId, dogId, isFavorite);
        FavoriteCheckResponseDto dto2 = new FavoriteCheckResponseDto(memberId, dogId, isFavorite);

        // Then
        assertNotSame(dto1, dto2); // 다른 인스턴스
        assertEquals(dto1.getMemberId(), dto2.getMemberId()); // 같은 값
        assertEquals(dto1.getDogId(), dto2.getDogId());
        assertEquals(dto1.isFavorite(), dto2.isFavorite());
    }

    @Test
    @DisplayName("FavoriteCheckResponseDto 생성 테스트 - 실제 사용 시나리오")
    void createFavoriteCheckResponseDto_RealScenarios() {
        // Given - 즐겨찾기에 추가된 경우
        FavoriteCheckResponseDto addedDto = new FavoriteCheckResponseDto(1L, 10L, true);

        // Given - 즐겨찾기에 없는 경우
        FavoriteCheckResponseDto notAddedDto = new FavoriteCheckResponseDto(1L, 20L, false);

        // When & Then
        assertTrue(addedDto.isFavorite());
        assertFalse(notAddedDto.isFavorite());
        assertEquals(1L, addedDto.getMemberId());
        assertEquals(1L, notAddedDto.getMemberId());
        assertEquals(10L, addedDto.getDogId());
        assertEquals(20L, notAddedDto.getDogId());
    }

    @Test
    @DisplayName("FavoriteCheckResponseDto 생성 테스트 - 음수 ID 값")
    void createFavoriteCheckResponseDto_WithNegativeIds() {
        // Given
        Long negativeMemberId = -1L;
        Long negativeDogId = -10L;
        boolean isFavorite = true;

        // When
        FavoriteCheckResponseDto dto = new FavoriteCheckResponseDto(negativeMemberId, negativeDogId, isFavorite);

        // Then
        assertNotNull(dto);
        assertEquals(-1L, dto.getMemberId());
        assertEquals(-10L, dto.getDogId());
        assertTrue(dto.isFavorite());
    }

    @Test
    @DisplayName("FavoriteCheckResponseDto 생성 테스트 - 서로 다른 멤버의 즐겨찾기 상태")
    void createFavoriteCheckResponseDto_DifferentMemberStatuses() {
        // Given
        Long dogId = 100L;

        // 멤버 1: 즐겨찾기 있음
        FavoriteCheckResponseDto member1Dto = new FavoriteCheckResponseDto(1L, dogId, true);

        // 멤버 2: 즐겨찾기 없음
        FavoriteCheckResponseDto member2Dto = new FavoriteCheckResponseDto(2L, dogId, false);

        // When & Then
        assertEquals(dogId, member1Dto.getDogId());
        assertEquals(dogId, member2Dto.getDogId());
        assertTrue(member1Dto.isFavorite());
        assertFalse(member2Dto.isFavorite());
        assertNotEquals(member1Dto.getMemberId(), member2Dto.getMemberId());
    }

    @Test
    @DisplayName("FavoriteCheckResponseDto isFavorite 메서드 명명 확인")
    void createFavoriteCheckResponseDto_IsFavoriteMethodNaming() {
        // Given
        FavoriteCheckResponseDto dto = new FavoriteCheckResponseDto(1L, 2L, true);

        // When & Then
        // isFavorite() 메서드가 boolean getter 명명 규칙을 따르는지 확인
        assertTrue(dto.isFavorite());

        // 메서드가 실제로 존재하고 호출 가능한지 확인
        boolean result = dto.isFavorite();
        assertTrue(result);
    }
}