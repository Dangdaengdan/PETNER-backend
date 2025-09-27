package com.example.petner.domain.upload.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UploadUrlResponseDto {
    private final String url;
    private final String objectName;
}