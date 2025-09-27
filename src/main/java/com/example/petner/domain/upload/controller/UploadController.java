package com.example.petner.domain.upload.controller;

import com.example.petner.domain.upload.dto.response.DownloadUrlResponseDto;
import com.example.petner.domain.upload.dto.response.UploadUrlResponseDto;
import com.example.petner.domain.upload.service.FileValidator;
import com.example.petner.global.annotation.SessionMember;
import com.example.petner.global.dto.SessionUser;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.UploadException;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/upload")
@RequiredArgsConstructor
public class UploadController {

    private final Storage storage;
    private final FileValidator fileValidator;

    @Value("${GCP_BUCKET}")
    private String bucketName;

    // Presigned URL 생성 (파일 업로드)
    @GetMapping("/presigned-url")
    public ResponseEntity<UploadUrlResponseDto> generatePresignedUrl(
            @RequestParam String fileName,
            @RequestParam(required = false) String fileType,
            @SessionMember SessionUser user) {

        // 파일 유효성 검증
        fileValidator.validateFile(fileName, fileType);

        try {
            // UUID를 파일명에 적용하여 고유한 객체명 생성
            String uniqueFileName = UUID.randomUUID() + "_" + fileName;
            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, uniqueFileName).build();

            // 5분 유효 기간의 presigned URL 생성
            URL signedUrl = storage.signUrl(blobInfo, 5, TimeUnit.MINUTES,
                    Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
                    Storage.SignUrlOption.withV4Signature());

            return ResponseEntity.ok(new UploadUrlResponseDto(signedUrl.toString(), uniqueFileName));
        } catch (Exception e) {
            throw new UploadException(ErrorCode.UPLOAD_PRESIGNED_URL_GENERATION_FAILED);
        }
    }

    // Presigned URL 생성 (파일 로드/다운로드)
    @GetMapping("/presigned-url/download")
    public ResponseEntity<DownloadUrlResponseDto> generatePresignedGetUrl(
            @RequestParam String objectName,
            @SessionMember SessionUser user) {
        if (objectName == null || objectName.isBlank()) {
            throw new UploadException(ErrorCode.UPLOAD_OBJECT_NOT_FOUND);
        }

        try {
            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName).build();

            // 5분 유효 기간의 presigned URL 생성 (GET 방식)
            URL signedUrl = storage.signUrl(blobInfo, 5, TimeUnit.MINUTES,
                    Storage.SignUrlOption.httpMethod(HttpMethod.GET),
                    Storage.SignUrlOption.withV4Signature());

            return ResponseEntity.ok(new DownloadUrlResponseDto(signedUrl.toString()));
        } catch (Exception e) {
            throw new UploadException(ErrorCode.UPLOAD_DOWNLOAD_URL_GENERATION_FAILED);
        }
    }
}