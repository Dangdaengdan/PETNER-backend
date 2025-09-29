package com.example.petner.domain.upload.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DownloadUrlResponseDtoTest {

    @Test
    @DisplayName("DownloadUrlResponseDto 생성 및 getter 테스트")
    void createDownloadUrlResponseDto_Success() {
        // Given
        String expectedUrl = "https://storage.googleapis.com/test-bucket/uuid_test.jpg";

        // When
        DownloadUrlResponseDto dto = new DownloadUrlResponseDto(expectedUrl);

        // Then
        assertNotNull(dto);
        assertEquals(expectedUrl, dto.getUrl());
    }

    @Test
    @DisplayName("null 값으로 DTO 생성 테스트")
    void createDownloadUrlResponseDto_WithNullValue() {
        // When
        DownloadUrlResponseDto dto = new DownloadUrlResponseDto(null);

        // Then
        assertNotNull(dto);
        assertNull(dto.getUrl());
    }

    @Test
    @DisplayName("빈 문자열로 DTO 생성 테스트")
    void createDownloadUrlResponseDto_WithEmptyString() {
        // Given
        String emptyUrl = "";

        // When
        DownloadUrlResponseDto dto = new DownloadUrlResponseDto(emptyUrl);

        // Then
        assertNotNull(dto);
        assertEquals(emptyUrl, dto.getUrl());
        assertTrue(dto.getUrl().isEmpty());
    }

    @Test
    @DisplayName("긴 URL로 DTO 생성 테스트")
    void createDownloadUrlResponseDto_WithLongUrl() {
        // Given
        String longUrl = "https://storage.googleapis.com/very-long-bucket-name-for-testing/" +
                "very-long-object-name-with-uuid-12345678-1234-1234-1234-123456789abc_" +
                "very-long-file-name-for-testing-purposes.jpg?" +
                "X-Goog-Algorithm=GOOG4-RSA-SHA256&" +
                "X-Goog-Credential=service-account%40project.iam.gserviceaccount.com%2F20240101%2Fauto%2Fstorage%2Fgoog4_request&" +
                "X-Goog-Date=20240101T000000Z&" +
                "X-Goog-Expires=300&" +
                "X-Goog-SignedHeaders=host&" +
                "X-Goog-Signature=very-long-signature-string";

        // When
        DownloadUrlResponseDto dto = new DownloadUrlResponseDto(longUrl);

        // Then
        assertNotNull(dto);
        assertEquals(longUrl, dto.getUrl());
        assertTrue(dto.getUrl().length() > 200);
        assertTrue(dto.getUrl().contains("X-Goog-Algorithm"));
        assertTrue(dto.getUrl().contains("X-Goog-Signature"));
    }

    @Test
    @DisplayName("Query parameter가 포함된 URL로 DTO 생성 테스트")
    void createDownloadUrlResponseDto_WithQueryParameters() {
        // Given
        String urlWithParams = "https://storage.googleapis.com/test-bucket/test.jpg?" +
                "X-Goog-Algorithm=GOOG4-RSA-SHA256&" +
                "X-Goog-Expires=300";

        // When
        DownloadUrlResponseDto dto = new DownloadUrlResponseDto(urlWithParams);

        // Then
        assertNotNull(dto);
        assertEquals(urlWithParams, dto.getUrl());
        assertTrue(dto.getUrl().contains("?"));
        assertTrue(dto.getUrl().contains("&"));
        assertTrue(dto.getUrl().contains("X-Goog-Algorithm"));
        assertTrue(dto.getUrl().contains("X-Goog-Expires"));
    }

    @Test
    @DisplayName("특수문자가 포함된 URL로 DTO 생성 테스트")
    void createDownloadUrlResponseDto_WithSpecialCharacters() {
        // Given
        String urlWithSpecialChars = "https://storage.googleapis.com/test-bucket/test%20file%20(1).jpg";

        // When
        DownloadUrlResponseDto dto = new DownloadUrlResponseDto(urlWithSpecialChars);

        // Then
        assertNotNull(dto);
        assertEquals(urlWithSpecialChars, dto.getUrl());
        assertTrue(dto.getUrl().contains("%20"));
        assertTrue(dto.getUrl().contains("("));
        assertTrue(dto.getUrl().contains(")"));
    }

    @Test
    @DisplayName("한글이 포함된 URL로 DTO 생성 테스트")
    void createDownloadUrlResponseDto_WithKoreanCharacters() {
        // Given
        String urlWithKorean = "https://storage.googleapis.com/test-bucket/테스트이미지.jpg";

        // When
        DownloadUrlResponseDto dto = new DownloadUrlResponseDto(urlWithKorean);

        // Then
        assertNotNull(dto);
        assertEquals(urlWithKorean, dto.getUrl());
        assertTrue(dto.getUrl().contains("테스트"));
        assertTrue(dto.getUrl().contains("이미지"));
    }

    @Test
    @DisplayName("HTTPS가 아닌 URL로 DTO 생성 테스트")
    void createDownloadUrlResponseDto_WithHttpUrl() {
        // Given
        String httpUrl = "http://storage.googleapis.com/test-bucket/test.jpg";

        // When
        DownloadUrlResponseDto dto = new DownloadUrlResponseDto(httpUrl);

        // Then
        assertNotNull(dto);
        assertEquals(httpUrl, dto.getUrl());
        assertTrue(dto.getUrl().startsWith("http://"));
    }

    @Test
    @DisplayName("다양한 파일 확장자가 포함된 URL로 DTO 생성 테스트")
    void createDownloadUrlResponseDto_WithDifferentFileExtensions() {
        // Given
        String[] urls = {
                "https://storage.googleapis.com/test-bucket/test.jpg",
                "https://storage.googleapis.com/test-bucket/test.jpeg",
                "https://storage.googleapis.com/test-bucket/test.png",
                "https://storage.googleapis.com/test-bucket/test.webp"
        };

        // When & Then
        for (String url : urls) {
            DownloadUrlResponseDto dto = new DownloadUrlResponseDto(url);
            assertNotNull(dto);
            assertEquals(url, dto.getUrl());
            assertTrue(dto.getUrl().contains("test."));
        }
    }

    @Test
    @DisplayName("객체 동등성 테스트")
    void testObjectEquality() {
        // Given
        String url = "https://storage.googleapis.com/test-bucket/test.jpg";

        DownloadUrlResponseDto dto1 = new DownloadUrlResponseDto(url);
        DownloadUrlResponseDto dto2 = new DownloadUrlResponseDto(url);
        DownloadUrlResponseDto dto3 = new DownloadUrlResponseDto("different-url");

        // Then
        assertEquals(dto1.getUrl(), dto2.getUrl());
        assertNotEquals(dto1.getUrl(), dto3.getUrl());
    }

    @Test
    @DisplayName("toString 메서드 동작 확인")
    void testToStringMethod() {
        // Given
        String url = "https://storage.googleapis.com/test-bucket/test.jpg";
        DownloadUrlResponseDto dto = new DownloadUrlResponseDto(url);

        // When
        String toStringResult = dto.toString();

        // Then
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("DownloadUrlResponseDto"));
    }

    @Test
    @DisplayName("공백이 포함된 URL로 DTO 생성 테스트")
    void createDownloadUrlResponseDto_WithWhitespace() {
        // Given
        String urlWithWhitespace = "  https://storage.googleapis.com/test-bucket/test.jpg  ";

        // When
        DownloadUrlResponseDto dto = new DownloadUrlResponseDto(urlWithWhitespace);

        // Then
        assertNotNull(dto);
        assertEquals(urlWithWhitespace, dto.getUrl());
        assertTrue(dto.getUrl().startsWith("  "));
        assertTrue(dto.getUrl().endsWith("  "));
    }
}