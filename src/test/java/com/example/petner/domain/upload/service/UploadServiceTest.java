package com.example.petner.domain.upload.service;

import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.UploadException;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UploadServiceTest {

    @Mock
    private Storage storage;

    @InjectMocks
    private UploadService uploadService;

    private final String bucketName = "test-bucket";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(uploadService, "bucketName", bucketName);
    }

    @Test
    @DisplayName("GCP Storage에서 파일 삭제 성공")
    void deleteImageFromStorage_Success() {
        // Given
        String imageUrl = "https://storage.googleapis.com/test-bucket/uuid_test.jpg";
        when(storage.delete(any(BlobId.class))).thenReturn(true);

        // When & Then
        assertDoesNotThrow(() -> uploadService.deleteImageFromStorage(imageUrl));
        verify(storage).delete(BlobId.of(bucketName, "uuid_test.jpg"));
    }

    @Test
    @DisplayName("ObjectName으로 파일 삭제 성공")
    void deleteImageFromStorage_WithObjectName_Success() {
        // Given
        String objectName = "12345678-1234-1234-1234-123456789abc_test.jpg";
        when(storage.delete(any(BlobId.class))).thenReturn(true);

        // When & Then
        assertDoesNotThrow(() -> uploadService.deleteImageFromStorage(objectName));
        verify(storage).delete(BlobId.of(bucketName, objectName));
    }

    @Test
    @DisplayName("파일이 존재하지 않는 경우 예외 발생하지 않음")
    void deleteImageFromStorage_FileNotFound_NoException() {
        // Given
        String imageUrl = "https://storage.googleapis.com/test-bucket/not-exist.jpg";
        when(storage.delete(any(BlobId.class))).thenReturn(false);

        // When & Then
        assertDoesNotThrow(() -> uploadService.deleteImageFromStorage(imageUrl));
        verify(storage).delete(BlobId.of(bucketName, "not-exist.jpg"));
    }

    @Test
    @DisplayName("imageUrl이 null인 경우 아무 작업 하지 않음")
    void deleteImageFromStorage_NullUrl_DoNothing() {
        // When & Then
        assertDoesNotThrow(() -> uploadService.deleteImageFromStorage(null));
        verifyNoInteractions(storage);
    }

    @Test
    @DisplayName("imageUrl이 빈 문자열인 경우 아무 작업 하지 않음")
    void deleteImageFromStorage_BlankUrl_DoNothing() {
        // When & Then
        assertDoesNotThrow(() -> uploadService.deleteImageFromStorage(""));
        assertDoesNotThrow(() -> uploadService.deleteImageFromStorage("   "));
        verifyNoInteractions(storage);
    }

    @Test
    @DisplayName("Storage 삭제 중 예외 발생 시 UploadException 발생")
    void deleteImageFromStorage_StorageException_ThrowsUploadException() {
        // Given
        String imageUrl = "https://storage.googleapis.com/test-bucket/test.jpg";
        when(storage.delete(any(BlobId.class))).thenThrow(new RuntimeException("Storage error"));

        // When & Then
        UploadException exception = assertThrows(UploadException.class,
                () -> uploadService.deleteImageFromStorage(imageUrl));
        assertEquals(ErrorCode.UPLOAD_DELETE_FAILED, exception.getErrorCode());
    }

    @Test
    @DisplayName("GCP Storage URL에서 objectName 추출 성공")
    void extractObjectNameFromUrl_GcpStorageUrl_Success() {
        // Given
        String imageUrl = "https://storage.googleapis.com/test-bucket/uuid_test.jpg?query=param";
        when(storage.delete(any(BlobId.class))).thenReturn(true);

        // When
        uploadService.deleteImageFromStorage(imageUrl);

        // Then
        verify(storage).delete(BlobId.of(bucketName, "uuid_test.jpg"));
    }

    @Test
    @DisplayName("Query parameter가 있는 URL에서 objectName 추출")
    void extractObjectNameFromUrl_WithQueryParams_Success() {
        // Given
        String imageUrl = "https://storage.googleapis.com/test-bucket/uuid_test.jpg?X-Goog-Algorithm=GOOG4-RSA-SHA256";
        when(storage.delete(any(BlobId.class))).thenReturn(true);

        // When
        uploadService.deleteImageFromStorage(imageUrl);

        // Then
        verify(storage).delete(BlobId.of(bucketName, "uuid_test.jpg"));
    }

    @Test
    @DisplayName("UUID 형태의 objectName 직접 전달 시 성공")
    void extractObjectNameFromUrl_UuidObjectName_Success() {
        // Given
        String objectName = "12345678-1234-1234-1234-123456789abc_test.jpg";
        when(storage.delete(any(BlobId.class))).thenReturn(true);

        // When
        uploadService.deleteImageFromStorage(objectName);

        // Then
        verify(storage).delete(BlobId.of(bucketName, objectName));
    }

    @Test
    @DisplayName("단순 파일명 전달 시 성공")
    void extractObjectNameFromUrl_SimpleFileName_Success() {
        // Given
        String fileName = "simple-test.jpg";
        when(storage.delete(any(BlobId.class))).thenReturn(true);

        // When
        uploadService.deleteImageFromStorage(fileName);

        // Then
        verify(storage).delete(BlobId.of(bucketName, fileName));
    }

    @Test
    @DisplayName("잘못된 형식의 URL에서 objectName 추출 실패")
    void extractObjectNameFromUrl_InvalidUrl_ThrowsException() {
        // Given
        String invalidUrl = "invalid-url-format";
        when(storage.delete(any(BlobId.class))).thenThrow(new RuntimeException("Parse error"));

        // When & Then
        UploadException exception = assertThrows(UploadException.class,
                () -> uploadService.deleteImageFromStorage(invalidUrl));
        assertEquals(ErrorCode.UPLOAD_DELETE_FAILED, exception.getErrorCode());
    }

    @Test
    @DisplayName("잘못된 GCP Storage URL 형식 처리")
    void extractObjectNameFromUrl_MalformedGcpUrl_HandledGracefully() {
        // Given
        String malformedUrl = "https://storage.googleapis.com/bucket/test.jpg";
        when(storage.delete(any(BlobId.class))).thenReturn(true);

        // When
        uploadService.deleteImageFromStorage(malformedUrl);

        // Then - 예외가 발생하지 않고 적절히 처리되어야 함
        verify(storage).delete(BlobId.of(bucketName, "test.jpg"));
    }
}