package com.example.petner.domain.dog.service;

import com.example.petner.domain.breed.entity.Breed;
import com.example.petner.domain.dog.dto.request.DogUpdateRequestDto;
import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.domain.shelter.entity.Shelter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 유기견 정보 업데이트를 담당하는 컴포넌트
 * Single Responsibility Principle(SRP)을 준수하여 업데이트 로직만 담당
 */
@Component
public class DogUpdater {

    /**
     * 유기견 정보 업데이트
     * null이 아닌 필드만 선택적으로 업데이트
     */
    public void updateDogInfo(Dog dog, DogUpdateRequestDto requestDto, Breed breed, Shelter shelter) {
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
                StringUtils.hasText(requestDto.getImageUrl()) ? requestDto.getImageUrl() : dog.getImageUrl(),
                shelter
        );
    }
}