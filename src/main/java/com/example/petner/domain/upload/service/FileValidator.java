package com.example.petner.domain.upload.service;

import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.UploadException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * 파일 업로드 유효성 검증을 담당하는 컴포넌트
 * Single Responsibility Principle(SRP)을 준수하여 파일 검증 로직만 담당
 */
@Component
public class FileValidator {

    // 허용된 MIME 타입
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp"
    );

    // 허용된 파일 확장자
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".jpg",
            ".jpeg",
            ".png",
            ".webp"
    );

    // MIME 타입과 확장자 매핑
    private static final Map<String, Set<String>> MIME_TO_EXTENSIONS = Map.of(
            "image/jpeg", Set.of(".jpg", ".jpeg"),
            "image/jpg", Set.of(".jpg", ".jpeg"),
            "image/png", Set.of(".png"),
            "image/webp", Set.of(".webp")
    );

    /**
     * 파일 유효성 검증
     */
    public void validateFile(String fileName, String fileType) {
        validateMimeType(fileType);
        validateFileName(fileName);
        validateFileExtension(fileName, fileType);
    }

    /**
     * MIME 타입 검증
     */
    private void validateMimeType(String fileType) {
        if (fileType == null || fileType.isBlank()) {
            throw new UploadException(ErrorCode.UPLOAD_INVALID_FILE_TYPE);
        }

        if (!ALLOWED_MIME_TYPES.contains(fileType.toLowerCase())) {
            throw new UploadException(ErrorCode.UPLOAD_INVALID_FILE_TYPE);
        }
    }

    /**
     * 파일명 검증
     */
    private void validateFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new UploadException(ErrorCode.UPLOAD_INVALID_FILE_TYPE);
        }

        // 위험한 문자 검증 (경로 탐색 및 시스템 파일명 공격 방지)
        String[] dangerousChars = {"..", "/", "\\", ":", "*", "?", "\"", "<", ">", "|"};
        for (String dangerousChar : dangerousChars) {
            if (fileName.contains(dangerousChar)) {
                throw new UploadException(ErrorCode.UPLOAD_INVALID_FILE_TYPE);
            }
        }
    }

    /**
     * 파일 확장자와 MIME 타입 일치성 검증
     */
    private void validateFileExtension(String fileName, String fileType) {
        String extension = getFileExtension(fileName).toLowerCase();

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new UploadException(ErrorCode.UPLOAD_INVALID_FILE_TYPE);
        }

        // MIME 타입과 확장자 일치성 검증
        Set<String> allowedExtensions = MIME_TO_EXTENSIONS.get(fileType.toLowerCase());
        if (allowedExtensions == null || !allowedExtensions.contains(extension)) {
            throw new UploadException(ErrorCode.UPLOAD_INVALID_FILE_TYPE);
        }
    }

    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            throw new UploadException(ErrorCode.UPLOAD_INVALID_FILE_TYPE);
        }
        return fileName.substring(lastDotIndex);
    }
}