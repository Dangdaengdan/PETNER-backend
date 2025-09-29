package com.example.petner.domain.upload.controller;

import com.example.petner.domain.upload.dto.response.DownloadUrlResponseDto;
import com.example.petner.domain.upload.dto.response.UploadUrlResponseDto;
import com.example.petner.domain.upload.service.FileValidator;
import com.example.petner.global.dto.SessionUser;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.UploadException;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.SignUrlOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UploadControllerTest {

    @Mock
    private Storage storage;

    @Mock
    private FileValidator fileValidator;

    @InjectMocks
    private UploadController uploadController;

    private SessionUser sessionUser;

    @BeforeEach
    void setUp() {
        sessionUser = SessionUser.builder()
                .memberId(1L)
                .email("test@example.com")
                .nickname("testUser")
                .build();

        ReflectionTestUtils.setField(uploadController, "bucketName", "test-bucket");
    }

    @Test
    @DisplayName("업로드용 Presigned URL 생성 성공")
    void generatePresignedUrl_Success() throws Exception {
        // Given
        String fileName = "test.jpg";
        String fileType = "image/jpeg";
        String expectedUrl = "https://storage.googleapis.com/test-bucket/unique-file-name";

        URL mockUrl = new URL(expectedUrl);
        when(storage.signUrl(any(BlobInfo.class), eq(5L), eq(TimeUnit.MINUTES),
                any(SignUrlOption.class), any(SignUrlOption.class)))
                .thenReturn(mockUrl);

        // When
        ResponseEntity<UploadUrlResponseDto> response = uploadController.generatePresignedUrl(fileName, fileType, sessionUser);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(expectedUrl, response.getBody().getUrl());
        assertNotNull(response.getBody().getObjectName());
        assertTrue(response.getBody().getObjectName().contains(fileName));

        verify(fileValidator).validateFile(fileName, fileType);
        verify(storage).signUrl(any(BlobInfo.class), eq(5L), eq(TimeUnit.MINUTES),
                any(SignUrlOption.class), any(SignUrlOption.class));
    }

    @Test
    @DisplayName("파일 유효성 검증 실패 시 예외 발생")
    void generatePresignedUrl_ValidationFailed() {
        // Given
        String fileName = "test.txt";
        String fileType = "text/plain";

        doThrow(new UploadException(ErrorCode.UPLOAD_INVALID_FILE_TYPE))
                .when(fileValidator).validateFile(fileName, fileType);

        // When & Then
        assertThrows(UploadException.class,
                () -> uploadController.generatePresignedUrl(fileName, fileType, sessionUser));

        verify(fileValidator).validateFile(fileName, fileType);
        verifyNoInteractions(storage);
    }

    @Test
    @DisplayName("Storage 서비스 오류 시 예외 발생")
    void generatePresignedUrl_StorageError() throws Exception {
        // Given
        String fileName = "test.jpg";
        String fileType = "image/jpeg";

        when(storage.signUrl(any(BlobInfo.class), eq(5L), eq(TimeUnit.MINUTES),
                any(SignUrlOption.class), any(SignUrlOption.class)))
                .thenThrow(new RuntimeException("Storage error"));

        // When & Then
        assertThrows(UploadException.class,
                () -> uploadController.generatePresignedUrl(fileName, fileType, sessionUser));

        verify(fileValidator).validateFile(fileName, fileType);
    }

    @Test
    @DisplayName("다운로드용 Presigned URL 생성 성공")
    void generatePresignedGetUrl_Success() throws Exception {
        // Given
        String objectName = "uuid_test.jpg";
        String expectedUrl = "https://storage.googleapis.com/test-bucket/" + objectName;

        URL mockUrl = new URL(expectedUrl);
        when(storage.signUrl(any(BlobInfo.class), eq(5L), eq(TimeUnit.MINUTES),
                any(SignUrlOption.class), any(SignUrlOption.class)))
                .thenReturn(mockUrl);

        // When
        ResponseEntity<DownloadUrlResponseDto> response = uploadController.generatePresignedGetUrl(objectName);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(expectedUrl, response.getBody().getUrl());

        verify(storage).signUrl(any(BlobInfo.class), eq(5L), eq(TimeUnit.MINUTES),
                any(SignUrlOption.class), any(SignUrlOption.class));
    }

    @Test
    @DisplayName("objectName이 null인 경우 예외 발생")
    void generatePresignedGetUrl_NullObjectName() {
        // When & Then
        UploadException exception = assertThrows(UploadException.class,
                () -> uploadController.generatePresignedGetUrl(null));

        assertEquals(ErrorCode.UPLOAD_OBJECT_NOT_FOUND, exception.getErrorCode());
        verifyNoInteractions(storage);
    }

    @Test
    @DisplayName("objectName이 빈 문자열인 경우 예외 발생")
    void generatePresignedGetUrl_BlankObjectName() {
        // When & Then
        UploadException exception = assertThrows(UploadException.class,
                () -> uploadController.generatePresignedGetUrl(""));

        assertEquals(ErrorCode.UPLOAD_OBJECT_NOT_FOUND, exception.getErrorCode());
        verifyNoInteractions(storage);
    }

    @Test
    @DisplayName("다운로드 URL 생성 중 Storage 오류 발생")
    void generatePresignedGetUrl_StorageError() throws Exception {
        // Given
        String objectName = "uuid_test.jpg";

        when(storage.signUrl(any(BlobInfo.class), eq(5L), eq(TimeUnit.MINUTES),
                any(SignUrlOption.class), any(SignUrlOption.class)))
                .thenThrow(new RuntimeException("Storage error"));

        // When & Then
        assertThrows(UploadException.class,
                () -> uploadController.generatePresignedGetUrl(objectName));
    }

    @Test
    @DisplayName("fileType 파라미터 없이도 정상 동작")
    void generatePresignedUrl_WithoutFileType() throws Exception {
        // Given
        String fileName = "test.jpg";
        String expectedUrl = "https://storage.googleapis.com/test-bucket/unique-file-name";

        URL mockUrl = new URL(expectedUrl);
        when(storage.signUrl(any(BlobInfo.class), eq(5L), eq(TimeUnit.MINUTES),
                any(SignUrlOption.class), any(SignUrlOption.class)))
                .thenReturn(mockUrl);

        // When
        ResponseEntity<UploadUrlResponseDto> response = uploadController.generatePresignedUrl(fileName, null, sessionUser);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(expectedUrl, response.getBody().getUrl());
        assertNotNull(response.getBody().getObjectName());

        verify(fileValidator).validateFile(fileName, null);
    }
}