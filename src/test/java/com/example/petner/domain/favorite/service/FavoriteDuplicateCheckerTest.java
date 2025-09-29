package com.example.petner.domain.favorite.service;

import com.example.petner.domain.favorite.repository.FavoriteRepository;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.FavoriteException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FavoriteDuplicateCheckerTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @InjectMocks
    private FavoriteDuplicateChecker favoriteDuplicateChecker;

    @Test
    @DisplayName("중복 체크 성공 - 중복 없음")
    void checkDuplicate_NoDuplicate_Success() {
        // Given
        Long memberId = 1L;
        Long dogId = 1L;
        when(favoriteRepository.existsByMemberMemberIdAndDogDogId(memberId, dogId)).thenReturn(false);

        // When & Then
        assertDoesNotThrow(() -> favoriteDuplicateChecker.checkDuplicate(memberId, dogId));

        verify(favoriteRepository).existsByMemberMemberIdAndDogDogId(memberId, dogId);
    }

    @Test
    @DisplayName("중복 체크 실패 - 중복 존재")
    void checkDuplicate_DuplicateExists_ThrowsException() {
        // Given
        Long memberId = 1L;
        Long dogId = 1L;
        when(favoriteRepository.existsByMemberMemberIdAndDogDogId(memberId, dogId)).thenReturn(true);

        // When & Then
        FavoriteException exception = assertThrows(FavoriteException.class,
                () -> favoriteDuplicateChecker.checkDuplicate(memberId, dogId));

        assertEquals(ErrorCode.FAVORITE_ALREADY_EXISTS, exception.getErrorCode());
        verify(favoriteRepository).existsByMemberMemberIdAndDogDogId(memberId, dogId);
    }

    @Test
    @DisplayName("존재 여부 확인 - 존재함")
    void exists_True() {
        // Given
        Long memberId = 1L;
        Long dogId = 1L;
        when(favoriteRepository.existsByMemberMemberIdAndDogDogId(memberId, dogId)).thenReturn(true);

        // When
        boolean result = favoriteDuplicateChecker.exists(memberId, dogId);

        // Then
        assertTrue(result);
        verify(favoriteRepository).existsByMemberMemberIdAndDogDogId(memberId, dogId);
    }

    @Test
    @DisplayName("존재 여부 확인 - 존재하지 않음")
    void exists_False() {
        // Given
        Long memberId = 1L;
        Long dogId = 2L;
        when(favoriteRepository.existsByMemberMemberIdAndDogDogId(memberId, dogId)).thenReturn(false);

        // When
        boolean result = favoriteDuplicateChecker.exists(memberId, dogId);

        // Then
        assertFalse(result);
        verify(favoriteRepository).existsByMemberMemberIdAndDogDogId(memberId, dogId);
    }

    @Test
    @DisplayName("중복 체크 - 다양한 ID 조합")
    void checkDuplicate_VariousIdCombinations() {
        // Given
        Object[][] testCases = {
                {1L, 1L, false}, // 중복 없음
                {1L, 2L, false}, // 중복 없음
                {2L, 1L, false}, // 중복 없음
                {100L, 200L, false} // 중복 없음
        };

        for (Object[] testCase : testCases) {
            Long memberId = (Long) testCase[0];
            Long dogId = (Long) testCase[1];
            boolean exists = (Boolean) testCase[2];

            when(favoriteRepository.existsByMemberMemberIdAndDogDogId(memberId, dogId))
                    .thenReturn(exists);

            // When & Then
            assertDoesNotThrow(() -> favoriteDuplicateChecker.checkDuplicate(memberId, dogId));
            verify(favoriteRepository).existsByMemberMemberIdAndDogDogId(memberId, dogId);
        }
    }

    @Test
    @DisplayName("존재 여부 확인 - 다양한 시나리오")
    void exists_VariousScenarios() {
        // Given
        Object[][] testCases = {
                {1L, 1L, true},
                {1L, 2L, false},
                {2L, 1L, false},
                {999L, 888L, false},
                {0L, 0L, false}
        };

        for (Object[] testCase : testCases) {
            Long memberId = (Long) testCase[0];
            Long dogId = (Long) testCase[1];
            boolean expected = (Boolean) testCase[2];

            when(favoriteRepository.existsByMemberMemberIdAndDogDogId(memberId, dogId))
                    .thenReturn(expected);

            // When
            boolean result = favoriteDuplicateChecker.exists(memberId, dogId);

            // Then
            assertEquals(expected, result);
            verify(favoriteRepository).existsByMemberMemberIdAndDogDogId(memberId, dogId);
        }
    }

    @Test
    @DisplayName("중복 체크 - 높은 ID 값")
    void checkDuplicate_WithHighIds() {
        // Given
        Long highMemberId = Long.MAX_VALUE;
        Long highDogId = Long.MAX_VALUE - 1;
        when(favoriteRepository.existsByMemberMemberIdAndDogDogId(highMemberId, highDogId))
                .thenReturn(false);

        // When & Then
        assertDoesNotThrow(() -> favoriteDuplicateChecker.checkDuplicate(highMemberId, highDogId));
        verify(favoriteRepository).existsByMemberMemberIdAndDogDogId(highMemberId, highDogId);
    }

    @Test
    @DisplayName("존재 여부 확인 - 높은 ID 값")
    void exists_WithHighIds() {
        // Given
        Long highMemberId = Long.MAX_VALUE;
        Long highDogId = Long.MAX_VALUE - 1;
        when(favoriteRepository.existsByMemberMemberIdAndDogDogId(highMemberId, highDogId))
                .thenReturn(true);

        // When
        boolean result = favoriteDuplicateChecker.exists(highMemberId, highDogId);

        // Then
        assertTrue(result);
        verify(favoriteRepository).existsByMemberMemberIdAndDogDogId(highMemberId, highDogId);
    }

    @Test
    @DisplayName("중복 체크 - 0 ID 값")
    void checkDuplicate_WithZeroIds() {
        // Given
        Long memberId = 0L;
        Long dogId = 0L;
        when(favoriteRepository.existsByMemberMemberIdAndDogDogId(memberId, dogId))
                .thenReturn(false);

        // When & Then
        assertDoesNotThrow(() -> favoriteDuplicateChecker.checkDuplicate(memberId, dogId));
        verify(favoriteRepository).existsByMemberMemberIdAndDogDogId(memberId, dogId);
    }

    @Test
    @DisplayName("존재 여부 확인 - 0 ID 값")
    void exists_WithZeroIds() {
        // Given
        Long memberId = 0L;
        Long dogId = 0L;
        when(favoriteRepository.existsByMemberMemberIdAndDogDogId(memberId, dogId))
                .thenReturn(false);

        // When
        boolean result = favoriteDuplicateChecker.exists(memberId, dogId);

        // Then
        assertFalse(result);
        verify(favoriteRepository).existsByMemberMemberIdAndDogDogId(memberId, dogId);
    }

    @Test
    @DisplayName("중복 체크 - 음수 ID 값")
    void checkDuplicate_WithNegativeIds() {
        // Given
        Long negativeMemberId = -1L;
        Long negativeDogId = -10L;
        when(favoriteRepository.existsByMemberMemberIdAndDogDogId(negativeMemberId, negativeDogId))
                .thenReturn(false);

        // When & Then
        assertDoesNotThrow(() -> favoriteDuplicateChecker.checkDuplicate(negativeMemberId, negativeDogId));
        verify(favoriteRepository).existsByMemberMemberIdAndDogDogId(negativeMemberId, negativeDogId);
    }

    @Test
    @DisplayName("존재 여부 확인 - 음수 ID 값")
    void exists_WithNegativeIds() {
        // Given
        Long negativeMemberId = -1L;
        Long negativeDogId = -10L;
        when(favoriteRepository.existsByMemberMemberIdAndDogDogId(negativeMemberId, negativeDogId))
                .thenReturn(false);

        // When
        boolean result = favoriteDuplicateChecker.exists(negativeMemberId, negativeDogId);

        // Then
        assertFalse(result);
        verify(favoriteRepository).existsByMemberMemberIdAndDogDogId(negativeMemberId, negativeDogId);
    }

    @Test
    @DisplayName("중복 체크 - 저장소 예외 처리")
    void checkDuplicate_RepositoryException() {
        // Given
        Long memberId = 1L;
        Long dogId = 1L;
        when(favoriteRepository.existsByMemberMemberIdAndDogDogId(memberId, dogId))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class,
                () -> favoriteDuplicateChecker.checkDuplicate(memberId, dogId));
        verify(favoriteRepository).existsByMemberMemberIdAndDogDogId(memberId, dogId);
    }

    @Test
    @DisplayName("존재 여부 확인 - 저장소 예외 처리")
    void exists_RepositoryException() {
        // Given
        Long memberId = 1L;
        Long dogId = 1L;
        when(favoriteRepository.existsByMemberMemberIdAndDogDogId(memberId, dogId))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class,
                () -> favoriteDuplicateChecker.exists(memberId, dogId));
        verify(favoriteRepository).existsByMemberMemberIdAndDogDogId(memberId, dogId);
    }

    @Test
    @DisplayName("중복 체크와 존재 여부 확인 - 동일한 매개변수")
    void checkDuplicateAndExists_SameParameters() {
        // Given
        Long memberId = 1L;
        Long dogId = 1L;

        // 첫 번째 호출: 중복 없음
        when(favoriteRepository.existsByMemberMemberIdAndDogDogId(memberId, dogId))
                .thenReturn(false);

        // When & Then - 중복 체크 (예외 없어야 함)
        assertDoesNotThrow(() -> favoriteDuplicateChecker.checkDuplicate(memberId, dogId));

        // When & Then - 존재 여부 확인
        boolean exists = favoriteDuplicateChecker.exists(memberId, dogId);
        assertFalse(exists);

        verify(favoriteRepository, times(2)).existsByMemberMemberIdAndDogDogId(memberId, dogId);
    }

    @Test
    @DisplayName("중복 체크 - 연속된 다른 ID 조합")
    void checkDuplicate_SequentialDifferentIds() {
        // Given
        when(favoriteRepository.existsByMemberMemberIdAndDogDogId(1L, 1L)).thenReturn(false);
        when(favoriteRepository.existsByMemberMemberIdAndDogDogId(1L, 2L)).thenReturn(true);
        when(favoriteRepository.existsByMemberMemberIdAndDogDogId(2L, 1L)).thenReturn(false);

        // When & Then
        assertDoesNotThrow(() -> favoriteDuplicateChecker.checkDuplicate(1L, 1L));

        FavoriteException exception = assertThrows(FavoriteException.class,
                () -> favoriteDuplicateChecker.checkDuplicate(1L, 2L));
        assertEquals(ErrorCode.FAVORITE_ALREADY_EXISTS, exception.getErrorCode());

        assertDoesNotThrow(() -> favoriteDuplicateChecker.checkDuplicate(2L, 1L));

        verify(favoriteRepository).existsByMemberMemberIdAndDogDogId(1L, 1L);
        verify(favoriteRepository).existsByMemberMemberIdAndDogDogId(1L, 2L);
        verify(favoriteRepository).existsByMemberMemberIdAndDogDogId(2L, 1L);
    }

    @Test
    @DisplayName("존재 여부 확인 - 여러 번 연속 호출")
    void exists_MultipleCalls() {
        // Given
        Long memberId = 1L;
        Long dogId = 1L;
        when(favoriteRepository.existsByMemberMemberIdAndDogDogId(memberId, dogId))
                .thenReturn(true);

        // When
        boolean result1 = favoriteDuplicateChecker.exists(memberId, dogId);
        boolean result2 = favoriteDuplicateChecker.exists(memberId, dogId);
        boolean result3 = favoriteDuplicateChecker.exists(memberId, dogId);

        // Then
        assertTrue(result1);
        assertTrue(result2);
        assertTrue(result3);

        verify(favoriteRepository, times(3)).existsByMemberMemberIdAndDogDogId(memberId, dogId);
    }

    @Test
    @DisplayName("중복 체크 예외 메시지 확인")
    void checkDuplicate_ExceptionMessage() {
        // Given
        Long memberId = 1L;
        Long dogId = 1L;
        when(favoriteRepository.existsByMemberMemberIdAndDogDogId(memberId, dogId)).thenReturn(true);

        // When & Then
        FavoriteException exception = assertThrows(FavoriteException.class,
                () -> favoriteDuplicateChecker.checkDuplicate(memberId, dogId));

        assertEquals(ErrorCode.FAVORITE_ALREADY_EXISTS, exception.getErrorCode());
        assertNotNull(exception.getMessage());
    }

    @Test
    @DisplayName("중복 체크와 존재 여부 확인 - 반대 결과")
    void checkDuplicateAndExists_OppositeResults() {
        // Given
        Long memberId = 1L;
        Long dogId = 1L;

        // When - exists는 true 반환
        when(favoriteRepository.existsByMemberMemberIdAndDogDogId(memberId, dogId)).thenReturn(true);
        boolean existsResult = favoriteDuplicateChecker.exists(memberId, dogId);

        // Then
        assertTrue(existsResult);

        // When & Then - checkDuplicate는 예외 발생
        FavoriteException exception = assertThrows(FavoriteException.class,
                () -> favoriteDuplicateChecker.checkDuplicate(memberId, dogId));

        assertEquals(ErrorCode.FAVORITE_ALREADY_EXISTS, exception.getErrorCode());
        verify(favoriteRepository, times(2)).existsByMemberMemberIdAndDogDogId(memberId, dogId);
    }
}