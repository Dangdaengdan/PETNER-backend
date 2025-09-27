package com.example.petner.domain.dog.service;

import com.example.petner.domain.breed.entity.Breed;
import com.example.petner.domain.dog.dto.request.DogUpdateRequestDto;
import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.domain.shelter.entity.Shelter;
import com.example.petner.domain.upload.service.UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 유기견 정보 업데이트를 담당하는 컴포넌트
 * Single Responsibility Principle(SRP)을 준수하여 업데이트 로직만 담당
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DogUpdater {

    private final UploadService uploadService;

    /**
     * 유기견 정보 업데이트
     * null이 아닌 필드만 선택적으로 업데이트
     */
    public void updateDogInfo(Dog dog, DogUpdateRequestDto requestDto, Breed breed, Shelter shelter) {
        String oldImageUrl = dog.getImageUrl();
        String newImageUrl = StringUtils.hasText(requestDto.getImageUrl()) ? requestDto.getImageUrl() : dog.getImageUrl();

        // 이미지 URL이 변경된 경우 기존 이미지 삭제
        if (StringUtils.hasText(requestDto.getImageUrl()) &&
            !requestDto.getImageUrl().equals(oldImageUrl)) {

            try {
                uploadService.deleteImageFromStorage(oldImageUrl);
                log.info("기존 이미지 삭제 완료: {}", oldImageUrl);
            } catch (Exception e) {
                // 기존 이미지 삭제 실패 시 로그만 남기고 업데이트는 계속 진행
                log.warn("기존 이미지 삭제 실패 (dogId: {}, oldImageUrl: {}): {}",
                        dog.getDogId(), oldImageUrl, e.getMessage());
            }
        }

        dog.updateDogInfo(
                StringUtils.hasText(requestDto.getName()) ? requestDto.getName() : dog.getName(),
                breed,
                StringUtils.hasText(requestDto.getBirthDate()) ? requestDto.getBirthDate() : dog.getBirthDate(),
                requestDto.getGender() != null ? requestDto.getGender() : dog.getGender(),
                requestDto.getDogSize() != null ? requestDto.getDogSize() : dog.getDogSize(),
                requestDto.getWeight() != null ? requestDto.getWeight() : dog.getWeight(),
                requestDto.getHealthStatus() != null ? requestDto.getHealthStatus() : dog.getHealthStatus(),
                requestDto.getDescription() != null ? requestDto.getDescription() : dog.getDescription(),
                requestDto.getAdoptionStatus() != null ? requestDto.getAdoptionStatus() : dog.getAdoptionStatus(),
                newImageUrl,
                shelter
        );
    }
}