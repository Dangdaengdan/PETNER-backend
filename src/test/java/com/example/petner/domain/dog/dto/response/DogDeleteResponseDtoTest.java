package com.example.petner.domain.dog.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DogDeleteResponseDtoTest {

    @Test
    @DisplayName("DogDeleteResponseDto 정상 생성")
    void createDogDeleteResponseDto_Success() {
        // Given
        Long dogId = 1L;
        Long memberId = 2L;
        String message = "유기견 정보 삭제 성공";

        // When
        DogDeleteResponseDto result = new DogDeleteResponseDto(dogId, memberId, message);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDogId()).isEqualTo(dogId);
        assertThat(result.getMemberId()).isEqualTo(memberId);
        assertThat(result.getMessage()).isEqualTo(message);
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    @DisplayName("DogDeleteResponseDto success 필드 자동 설정")
    void createDogDeleteResponseDto_SuccessAutoSet() {
        // Given
        Long dogId = 5L;
        Long memberId = 10L;
        String message = "삭제 완료";

        // When
        DogDeleteResponseDto result = new DogDeleteResponseDto(dogId, memberId, message);

        // Then
        assertThat(result.isSuccess()).isTrue(); // 항상 true로 설정됨
    }

    @Test
    @DisplayName("DogDeleteResponseDto 다양한 메시지 테스트")
    void createDogDeleteResponseDto_VariousMessages() {
        // Given
        String[] messages = {
                "유기견 정보 삭제 성공",
                "삭제가 완료되었습니다",
                "강아지 정보가 성공적으로 삭제되었습니다",
                "Dog deleted successfully"
        };

        for (String message : messages) {
            // When
            DogDeleteResponseDto result = new DogDeleteResponseDto(1L, 1L, message);

            // Then
            assertThat(result.getMessage()).isEqualTo(message);
            assertThat(result.isSuccess()).isTrue();
        }
    }

    @Test
    @DisplayName("DogDeleteResponseDto 다양한 ID 값 테스트")
    void createDogDeleteResponseDto_VariousIds() {
        // Given
        Long[] dogIds = {1L, 100L, 999L, 12345L};
        Long[] memberIds = {1L, 50L, 200L, 67890L};

        for (int i = 0; i < dogIds.length; i++) {
            // When
            DogDeleteResponseDto result = new DogDeleteResponseDto(
                    dogIds[i], memberIds[i], "테스트 메시지");

            // Then
            assertThat(result.getDogId()).isEqualTo(dogIds[i]);
            assertThat(result.getMemberId()).isEqualTo(memberIds[i]);
            assertThat(result.isSuccess()).isTrue();
        }
    }

    @Test
    @DisplayName("DogDeleteResponseDto null 메시지 처리")
    void createDogDeleteResponseDto_NullMessage() {
        // Given
        Long dogId = 1L;
        Long memberId = 1L;
        String message = null;

        // When
        DogDeleteResponseDto result = new DogDeleteResponseDto(dogId, memberId, message);

        // Then
        assertThat(result.getDogId()).isEqualTo(dogId);
        assertThat(result.getMemberId()).isEqualTo(memberId);
        assertThat(result.getMessage()).isNull();
        assertThat(result.isSuccess()).isTrue(); // success는 여전히 true
    }

    @Test
    @DisplayName("DogDeleteResponseDto 빈 문자열 메시지 처리")
    void createDogDeleteResponseDto_EmptyMessage() {
        // Given
        Long dogId = 1L;
        Long memberId = 1L;
        String message = "";

        // When
        DogDeleteResponseDto result = new DogDeleteResponseDto(dogId, memberId, message);

        // Then
        assertThat(result.getDogId()).isEqualTo(dogId);
        assertThat(result.getMemberId()).isEqualTo(memberId);
        assertThat(result.getMessage()).isEqualTo("");
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    @DisplayName("DogDeleteResponseDto Getter 메서드 테스트")
    void dogDeleteResponseDto_GetterMethods() {
        // Given
        Long dogId = 123L;
        Long memberId = 456L;
        String message = "삭제 성공 메시지";
        DogDeleteResponseDto dto = new DogDeleteResponseDto(dogId, memberId, message);

        // When & Then
        assertThat(dto.getDogId()).isEqualTo(dogId);
        assertThat(dto.getMemberId()).isEqualTo(memberId);
        assertThat(dto.getMessage()).isEqualTo(message);
        assertThat(dto.isSuccess()).isTrue();
    }

    @Test
    @DisplayName("DogDeleteResponseDto 동일한 ID 값 테스트")
    void createDogDeleteResponseDto_SameIds() {
        // Given
        Long sameId = 42L;
        String message = "동일한 ID 테스트";

        // When
        DogDeleteResponseDto result = new DogDeleteResponseDto(sameId, sameId, message);

        // Then
        assertThat(result.getDogId()).isEqualTo(sameId);
        assertThat(result.getMemberId()).isEqualTo(sameId);
        assertThat(result.getDogId()).isEqualTo(result.getMemberId());
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    @DisplayName("DogDeleteResponseDto 큰 ID 값 테스트")
    void createDogDeleteResponseDto_LargeIds() {
        // Given
        Long largeDogId = Long.MAX_VALUE;
        Long largeMemberId = Long.MAX_VALUE - 1;
        String message = "큰 ID 값 테스트";

        // When
        DogDeleteResponseDto result = new DogDeleteResponseDto(largeDogId, largeMemberId, message);

        // Then
        assertThat(result.getDogId()).isEqualTo(largeDogId);
        assertThat(result.getMemberId()).isEqualTo(largeMemberId);
        assertThat(result.getMessage()).isEqualTo(message);
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    @DisplayName("DogDeleteResponseDto 긴 메시지 테스트")
    void createDogDeleteResponseDto_LongMessage() {
        // Given
        Long dogId = 1L;
        Long memberId = 1L;
        String longMessage = "이것은 매우 긴 삭제 성공 메시지입니다. ".repeat(10);

        // When
        DogDeleteResponseDto result = new DogDeleteResponseDto(dogId, memberId, longMessage);

        // Then
        assertThat(result.getMessage()).isEqualTo(longMessage);
        assertThat(result.getMessage().length()).isGreaterThan(100);
        assertThat(result.isSuccess()).isTrue();
    }
}