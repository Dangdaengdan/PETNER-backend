package com.example.petner.domain.upload.service;

import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.UploadException;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadService {

    private final Storage storage;

    @Value("${gcp.bucket}")
    private String bucketName;

    /**
     * GCP Storage에서 파일 삭제
     *
     * @param imageUrl 삭제할 이미지의 전체 URL 또는 objectName
     */
    public void deleteImageFromStorage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            log.warn("이미지 URL이 비어있어 삭제를 건너뜁니다.");
            return;
        }

        try {
            String objectName = extractObjectNameFromUrl(imageUrl);
            log.info("GCP Storage에서 파일 삭제 시도: {}", objectName);

            boolean deleted = storage.delete(BlobId.of(bucketName, objectName));

            if (deleted) {
                log.info("GCP Storage에서 파일 삭제 성공: {}", objectName);
            } else {
                log.warn("GCP Storage에서 파일을 찾을 수 없음: {}", objectName);
                // 파일이 이미 없는 경우는 예외를 던지지 않음 (중복 삭제 방지)
            }
        } catch (Exception e) {
            log.error("GCP Storage 파일 삭제 실패: {}", imageUrl, e);
            throw new UploadException(ErrorCode.UPLOAD_DELETE_FAILED);
        }
    }

    /**
     * URL에서 objectName 추출
     *
     * @param imageUrl 전체 URL 또는 objectName
     * @return objectName
     */
    private String extractObjectNameFromUrl(String imageUrl) {
        try {
            // Case 1: 전체 GCP Storage URL인 경우
            if (imageUrl.contains("storage.googleapis.com")) {
                // https://storage.googleapis.com/bucket-name/objectName?query... 형태
                String[] parts = imageUrl.split("/");
                if (parts.length >= 5) {
                    String objectNameWithQuery = parts[4]; // objectName?query...
                    // Query parameter 제거
                    return objectNameWithQuery.split("\\?")[0];
                }
            }

            // Case 2: 이미 objectName인 경우 (UUID_filename.ext 형태)
            if (imageUrl.matches("^[a-f0-9-]{36}_.*\\.[a-zA-Z0-9]+$")) {
                return imageUrl;
            }

            // Case 3: 단순 파일명인 경우 그대로 반환
            return imageUrl;

        } catch (Exception e) {
            log.error("URL에서 objectName 추출 실패: {}", imageUrl, e);
            throw new UploadException(ErrorCode.UPLOAD_INVALID_URL_FORMAT);
        }
    }
}