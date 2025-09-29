package com.example.petner.domain.upload.service;

import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.UploadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class FileValidatorTest {

    private FileValidator fileValidator;

    @BeforeEach
    void setUp() {
        fileValidator = new FileValidator();
    }

    @Test
    @DisplayName("유효한 JPEG 파일 검증 성공")
    void validateFile_ValidJpegFile_Success() {
        // Given
        String fileName = "test.jpg";
        String fileType = "image/jpeg";

        // When & Then
        assertDoesNotThrow(() -> fileValidator.validateFile(fileName, fileType));
    }

    @Test
    @DisplayName("유효한 PNG 파일 검증 성공")
    void validateFile_ValidPngFile_Success() {
        // Given
        String fileName = "test.png";
        String fileType = "image/png";

        // When & Then
        assertDoesNotThrow(() -> fileValidator.validateFile(fileName, fileType));
    }

    @Test
    @DisplayName("유효한 WebP 파일 검증 성공")
    void validateFile_ValidWebpFile_Success() {
        // Given
        String fileName = "test.webp";
        String fileType = "image/webp";

        // When & Then
        assertDoesNotThrow(() -> fileValidator.validateFile(fileName, fileType));
    }

    @Test
    @DisplayName("JPEG 확장자와 image/jpg MIME 타입 조합 검증 성공")
    void validateFile_JpegExtensionWithJpgMimeType_Success() {
        // Given
        String fileName = "test.jpeg";
        String fileType = "image/jpg";

        // When & Then
        assertDoesNotThrow(() -> fileValidator.validateFile(fileName, fileType));
    }

    @Test
    @DisplayName("fileType이 null인 경우 예외 발생")
    void validateFile_NullFileType_ThrowsException() {
        // Given
        String fileName = "test.jpg";
        String fileType = null;

        // When & Then
        UploadException exception = assertThrows(UploadException.class,
                () -> fileValidator.validateFile(fileName, fileType));
        assertEquals(ErrorCode.UPLOAD_INVALID_FILE_TYPE, exception.getErrorCode());
    }

    @Test
    @DisplayName("fileType이 빈 문자열인 경우 예외 발생")
    void validateFile_BlankFileType_ThrowsException() {
        // Given
        String fileName = "test.jpg";
        String fileType = "";

        // When & Then
        UploadException exception = assertThrows(UploadException.class,
                () -> fileValidator.validateFile(fileName, fileType));
        assertEquals(ErrorCode.UPLOAD_INVALID_FILE_TYPE, exception.getErrorCode());
    }

    @Test
    @DisplayName("fileName이 null인 경우 예외 발생")
    void validateFile_NullFileName_ThrowsException() {
        // Given
        String fileName = null;
        String fileType = "image/jpeg";

        // When & Then
        UploadException exception = assertThrows(UploadException.class,
                () -> fileValidator.validateFile(fileName, fileType));
        assertEquals(ErrorCode.UPLOAD_INVALID_FILE_TYPE, exception.getErrorCode());
    }

    @Test
    @DisplayName("fileName이 빈 문자열인 경우 예외 발생")
    void validateFile_BlankFileName_ThrowsException() {
        // Given
        String fileName = "";
        String fileType = "image/jpeg";

        // When & Then
        UploadException exception = assertThrows(UploadException.class,
                () -> fileValidator.validateFile(fileName, fileType));
        assertEquals(ErrorCode.UPLOAD_INVALID_FILE_TYPE, exception.getErrorCode());
    }

    @Test
    @DisplayName("허용되지 않은 MIME 타입인 경우 예외 발생")
    void validateFile_InvalidMimeType_ThrowsException() {
        // Given
        String fileName = "test.txt";
        String fileType = "text/plain";

        // When & Then
        UploadException exception = assertThrows(UploadException.class,
                () -> fileValidator.validateFile(fileName, fileType));
        assertEquals(ErrorCode.UPLOAD_INVALID_FILE_TYPE, exception.getErrorCode());
    }

    @Test
    @DisplayName("허용되지 않은 파일 확장자인 경우 예외 발생")
    void validateFile_InvalidFileExtension_ThrowsException() {
        // Given
        String fileName = "test.txt";
        String fileType = "image/jpeg";

        // When & Then
        UploadException exception = assertThrows(UploadException.class,
                () -> fileValidator.validateFile(fileName, fileType));
        assertEquals(ErrorCode.UPLOAD_INVALID_FILE_TYPE, exception.getErrorCode());
    }

    @Test
    @DisplayName("MIME 타입과 확장자가 일치하지 않는 경우 예외 발생")
    void validateFile_MismatchedMimeTypeAndExtension_ThrowsException() {
        // Given
        String fileName = "test.png";
        String fileType = "image/jpeg";

        // When & Then
        UploadException exception = assertThrows(UploadException.class,
                () -> fileValidator.validateFile(fileName, fileType));
        assertEquals(ErrorCode.UPLOAD_INVALID_FILE_TYPE, exception.getErrorCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {"..", "/", "\\", ":", "*", "?", "\"", "<", ">", "|"})
    @DisplayName("위험한 문자가 포함된 파일명인 경우 예외 발생")
    void validateFile_DangerousCharactersInFileName_ThrowsException(String dangerousChar) {
        // Given
        String fileName = "test" + dangerousChar + "file.jpg";
        String fileType = "image/jpeg";

        // When & Then
        UploadException exception = assertThrows(UploadException.class,
                () -> fileValidator.validateFile(fileName, fileType));
        assertEquals(ErrorCode.UPLOAD_INVALID_FILE_TYPE, exception.getErrorCode());
    }

    @Test
    @DisplayName("경로 탐색 공격 시도 시 예외 발생")
    void validateFile_PathTraversalAttack_ThrowsException() {
        // Given
        String fileName = "../../../etc/passwd.jpg";
        String fileType = "image/jpeg";

        // When & Then
        UploadException exception = assertThrows(UploadException.class,
                () -> fileValidator.validateFile(fileName, fileType));
        assertEquals(ErrorCode.UPLOAD_INVALID_FILE_TYPE, exception.getErrorCode());
    }

    @Test
    @DisplayName("파일 확장자가 없는 경우 예외 발생")
    void validateFile_NoFileExtension_ThrowsException() {
        // Given
        String fileName = "testfile";
        String fileType = "image/jpeg";

        // When & Then
        UploadException exception = assertThrows(UploadException.class,
                () -> fileValidator.validateFile(fileName, fileType));
        assertEquals(ErrorCode.UPLOAD_INVALID_FILE_TYPE, exception.getErrorCode());
    }

    @Test
    @DisplayName("확장자가 점으로만 끝나는 경우 예외 발생")
    void validateFile_ExtensionEndsWithDotOnly_ThrowsException() {
        // Given
        String fileName = "testfile.";
        String fileType = "image/jpeg";

        // When & Then
        UploadException exception = assertThrows(UploadException.class,
                () -> fileValidator.validateFile(fileName, fileType));
        assertEquals(ErrorCode.UPLOAD_INVALID_FILE_TYPE, exception.getErrorCode());
    }

    @Test
    @DisplayName("대소문자 구분 없이 MIME 타입 검증")
    void validateFile_CaseInsensitiveMimeType_Success() {
        // Given
        String fileName = "test.jpg";
        String fileType = "IMAGE/JPEG";

        // When & Then
        assertDoesNotThrow(() -> fileValidator.validateFile(fileName, fileType));
    }

    @Test
    @DisplayName("대소문자 구분 없이 파일 확장자 검증")
    void validateFile_CaseInsensitiveFileExtension_Success() {
        // Given
        String fileName = "test.JPG";
        String fileType = "image/jpeg";

        // When & Then
        assertDoesNotThrow(() -> fileValidator.validateFile(fileName, fileType));
    }

    @Test
    @DisplayName("복수 확장자가 있는 파일명 검증")
    void validateFile_MultipleExtensions_Success() {
        // Given
        String fileName = "test.backup.jpg";
        String fileType = "image/jpeg";

        // When & Then
        assertDoesNotThrow(() -> fileValidator.validateFile(fileName, fileType));
    }

    @Test
    @DisplayName("한글이 포함된 파일명 검증 성공")
    void validateFile_KoreanFileName_Success() {
        // Given
        String fileName = "테스트이미지.jpg";
        String fileType = "image/jpeg";

        // When & Then
        assertDoesNotThrow(() -> fileValidator.validateFile(fileName, fileType));
    }

    @Test
    @DisplayName("공백이 포함된 파일명 검증 성공")
    void validateFile_FileNameWithSpaces_Success() {
        // Given
        String fileName = "test image.jpg";
        String fileType = "image/jpeg";

        // When & Then
        assertDoesNotThrow(() -> fileValidator.validateFile(fileName, fileType));
    }

    @Test
    @DisplayName("숫자가 포함된 파일명 검증 성공")
    void validateFile_FileNameWithNumbers_Success() {
        // Given
        String fileName = "test123.jpg";
        String fileType = "image/jpeg";

        // When & Then
        assertDoesNotThrow(() -> fileValidator.validateFile(fileName, fileType));
    }
}