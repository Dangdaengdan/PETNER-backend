package com.example.petner.domain.upload.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UploadUrlResponseDtoTest {

    @Test
    @DisplayName("UploadUrlResponseDto 생성 및 getter 테스트")
    void createUploadUrlResponseDto_Success() {
        // Given
        String expectedUrl = "https://storage.googleapis.com/test-bucket/uuid_test.jpg";
        String expectedObjectName = "uuid_test.jpg";

        // When
        UploadUrlResponseDto dto = new UploadUrlResponseDto(expectedUrl, expectedObjectName);

        // Then
        assertNotNull(dto);
        assertEquals(expectedUrl, dto.getUrl());
        assertEquals(expectedObjectName, dto.getObjectName());
    }

    @Test
    @DisplayName("null 값으로 DTO 생성 테스트")
    void createUploadUrlResponseDto_WithNullValues() {
        // When
        UploadUrlResponseDto dto = new UploadUrlResponseDto(null, null);

        // Then
        assertNotNull(dto);
        assertNull(dto.getUrl());
        assertNull(dto.getObjectName());
    }

    @Test
    @DisplayName("빈 문자열로 DTO 생성 테스트")
    void createUploadUrlResponseDto_WithEmptyStrings() {
        // Given
        String emptyUrl = "";
        String emptyObjectName = "";

        // When
        UploadUrlResponseDto dto = new UploadUrlResponseDto(emptyUrl, emptyObjectName);

        // Then
        assertNotNull(dto);
        assertEquals(emptyUrl, dto.getUrl());
        assertEquals(emptyObjectName, dto.getObjectName());
        assertTrue(dto.getUrl().isEmpty());
        assertTrue(dto.getObjectName().isEmpty());
    }

    @Test
    @DisplayName("긴 URL과 ObjectName으로 DTO 생성 테스트")
    void createUploadUrlResponseDto_WithLongValues() {
        // Given
        String longUrl = "https://storage.googleapis.com/very-long-bucket-name-for-testing/" +
                "very-long-object-name-with-uuid-12345678-1234-1234-1234-123456789abc_" +
                "very-long-file-name-for-testing-purposes.jpg?X-Goog-Algorithm=GOOG4-RSA-SHA256";
        String longObjectName = "very-long-object-name-with-uuid-12345678-1234-1234-1234-123456789abc_" +
                "very-long-file-name-for-testing-purposes.jpg";

        // When
        UploadUrlResponseDto dto = new UploadUrlResponseDto(longUrl, longObjectName);

        // Then
        assertNotNull(dto);
        assertEquals(longUrl, dto.getUrl());
        assertEquals(longObjectName, dto.getObjectName());
        assertTrue(dto.getUrl().length() > 100);
        assertTrue(dto.getObjectName().length() > 50);
    }

    @Test
    @DisplayName("특수문자가 포함된 값으로 DTO 생성 테스트")
    void createUploadUrlResponseDto_WithSpecialCharacters() {
        // Given
        String urlWithSpecialChars = "https://storage.googleapis.com/test-bucket/test%20file%20(1).jpg";
        String objectNameWithSpecialChars = "test file (1).jpg";

        // When
        UploadUrlResponseDto dto = new UploadUrlResponseDto(urlWithSpecialChars, objectNameWithSpecialChars);

        // Then
        assertNotNull(dto);
        assertEquals(urlWithSpecialChars, dto.getUrl());
        assertEquals(objectNameWithSpecialChars, dto.getObjectName());
        assertTrue(dto.getUrl().contains("%20"));
        assertTrue(dto.getObjectName().contains(" "));
    }

    @Test
    @DisplayName("한글이 포함된 값으로 DTO 생성 테스트")
    void createUploadUrlResponseDto_WithKoreanCharacters() {
        // Given
        String urlWithKorean = "https://storage.googleapis.com/test-bucket/테스트이미지.jpg";
        String objectNameWithKorean = "테스트이미지.jpg";

        // When
        UploadUrlResponseDto dto = new UploadUrlResponseDto(urlWithKorean, objectNameWithKorean);

        // Then
        assertNotNull(dto);
        assertEquals(urlWithKorean, dto.getUrl());
        assertEquals(objectNameWithKorean, dto.getObjectName());
        assertTrue(dto.getUrl().contains("테스트"));
        assertTrue(dto.getObjectName().contains("테스트"));
    }

    @Test
    @DisplayName("객체 동등성 테스트")
    void testObjectEquality() {
        // Given
        String url = "https://storage.googleapis.com/test-bucket/test.jpg";
        String objectName = "test.jpg";

        UploadUrlResponseDto dto1 = new UploadUrlResponseDto(url, objectName);
        UploadUrlResponseDto dto2 = new UploadUrlResponseDto(url, objectName);
        UploadUrlResponseDto dto3 = new UploadUrlResponseDto("different-url", "different-name");

        // Then
        assertEquals(dto1.getUrl(), dto2.getUrl());
        assertEquals(dto1.getObjectName(), dto2.getObjectName());
        assertNotEquals(dto1.getUrl(), dto3.getUrl());
        assertNotEquals(dto1.getObjectName(), dto3.getObjectName());
    }

    @Test
    @DisplayName("toString 메서드 동작 확인")
    void testToStringMethod() {
        // Given
        String url = "https://storage.googleapis.com/test-bucket/test.jpg";
        String objectName = "test.jpg";
        UploadUrlResponseDto dto = new UploadUrlResponseDto(url, objectName);

        // When
        String toStringResult = dto.toString();

        // Then
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("UploadUrlResponseDto"));
    }
}