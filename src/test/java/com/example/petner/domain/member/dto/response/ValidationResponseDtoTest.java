package com.example.petner.domain.member.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationResponseDtoTest {

    @Test
    @DisplayName("사용 가능한 경우 ValidationResponseDto 생성")
    void available_Success() {
        // Given
        String fieldName = "닉네임";

        // When
        ValidationResponseDto responseDto = ValidationResponseDto.available(fieldName);

        // Then
        assertTrue(responseDto.isAvailable());
        assertEquals("사용 가능한 " + fieldName + "입니다.", responseDto.getMessage());
    }

    @Test
    @DisplayName("사용 불가능한 경우 ValidationResponseDto 생성")
    void unavailable_Success() {
        // Given
        String fieldName = "이메일";

        // When
        ValidationResponseDto responseDto = ValidationResponseDto.unavailable(fieldName);

        // Then
        assertFalse(responseDto.isAvailable());
        assertEquals("이미 사용 중인 " + fieldName + "입니다.", responseDto.getMessage());
    }

    @Test
    @DisplayName("생성자를 통한 ValidationResponseDto 생성")
    void constructor_Success() {
        // Given
        boolean available = true;
        String message = "커스텀 메시지";

        // When
        ValidationResponseDto responseDto = new ValidationResponseDto(available, message);

        // Then
        assertTrue(responseDto.isAvailable());
        assertEquals(message, responseDto.getMessage());
    }

    @Test
    @DisplayName("닉네임 중복 확인 - 사용 가능")
    void nicknameValidation_Available() {
        // When
        ValidationResponseDto responseDto = ValidationResponseDto.available("닉네임");

        // Then
        assertTrue(responseDto.isAvailable());
        assertEquals("사용 가능한 닉네임입니다.", responseDto.getMessage());
    }

    @Test
    @DisplayName("닉네임 중복 확인 - 사용 불가능")
    void nicknameValidation_Unavailable() {
        // When
        ValidationResponseDto responseDto = ValidationResponseDto.unavailable("닉네임");

        // Then
        assertFalse(responseDto.isAvailable());
        assertEquals("이미 사용 중인 닉네임입니다.", responseDto.getMessage());
    }

    @Test
    @DisplayName("이메일 중복 확인 - 사용 가능")
    void emailValidation_Available() {
        // When
        ValidationResponseDto responseDto = ValidationResponseDto.available("이메일");

        // Then
        assertTrue(responseDto.isAvailable());
        assertEquals("사용 가능한 이메일입니다.", responseDto.getMessage());
    }

    @Test
    @DisplayName("이메일 중복 확인 - 사용 불가능")
    void emailValidation_Unavailable() {
        // When
        ValidationResponseDto responseDto = ValidationResponseDto.unavailable("이메일");

        // Then
        assertFalse(responseDto.isAvailable());
        assertEquals("이미 사용 중인 이메일입니다.", responseDto.getMessage());
    }

    @Test
    @DisplayName("빈 필드명으로 ValidationResponseDto 생성")
    void emptyFieldName_Test() {
        // Given
        String emptyFieldName = "";

        // When
        ValidationResponseDto availableDto = ValidationResponseDto.available(emptyFieldName);
        ValidationResponseDto unavailableDto = ValidationResponseDto.unavailable(emptyFieldName);

        // Then
        assertTrue(availableDto.isAvailable());
        assertEquals("사용 가능한 입니다.", availableDto.getMessage());

        assertFalse(unavailableDto.isAvailable());
        assertEquals("이미 사용 중인 입니다.", unavailableDto.getMessage());
    }

    @Test
    @DisplayName("null 필드명으로 ValidationResponseDto 생성")
    void nullFieldName_Test() {
        // Given
        String nullFieldName = null;

        // When
        ValidationResponseDto availableDto = ValidationResponseDto.available(nullFieldName);
        ValidationResponseDto unavailableDto = ValidationResponseDto.unavailable(nullFieldName);

        // Then
        assertTrue(availableDto.isAvailable());
        assertEquals("사용 가능한 null입니다.", availableDto.getMessage());

        assertFalse(unavailableDto.isAvailable());
        assertEquals("이미 사용 중인 null입니다.", unavailableDto.getMessage());
    }

    @Test
    @DisplayName("Getter 메소드 테스트")
    void getter_Test() {
        // Given
        boolean available = false;
        String message = "테스트 메시지";
        ValidationResponseDto responseDto = new ValidationResponseDto(available, message);

        // When & Then
        assertEquals(available, responseDto.isAvailable());
        assertEquals(message, responseDto.getMessage());
    }

    @Test
    @DisplayName("정적 팩토리 메소드의 일관성 테스트")
    void staticFactoryMethod_Consistency() {
        // Given
        String fieldName = "테스트필드";

        // When
        ValidationResponseDto available1 = ValidationResponseDto.available(fieldName);
        ValidationResponseDto available2 = ValidationResponseDto.available(fieldName);
        ValidationResponseDto unavailable1 = ValidationResponseDto.unavailable(fieldName);
        ValidationResponseDto unavailable2 = ValidationResponseDto.unavailable(fieldName);

        // Then
        assertEquals(available1.isAvailable(), available2.isAvailable());
        assertEquals(available1.getMessage(), available2.getMessage());
        assertEquals(unavailable1.isAvailable(), unavailable2.isAvailable());
        assertEquals(unavailable1.getMessage(), unavailable2.getMessage());

        assertNotEquals(available1.isAvailable(), unavailable1.isAvailable());
        assertNotEquals(available1.getMessage(), unavailable1.getMessage());
    }
}