package com.example.petner.domain.dog.service;

import com.example.petner.domain.breed.entity.Breed;
import com.example.petner.domain.dog.common.AdoptionStatus;
import com.example.petner.domain.dog.common.DogSize;
import com.example.petner.domain.dog.dto.request.DogUpdateRequestDto;
import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.shelter.entity.Shelter;
import com.example.petner.domain.location.entity.Location;
import com.example.petner.domain.upload.service.UploadService;
import com.example.petner.global.config.common.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DogUpdaterTest {

    @Mock
    private UploadService uploadService;

    @InjectMocks
    private DogUpdater dogUpdater;

    private Dog dog;
    private Member member;
    private Breed breed;
    private Shelter shelter;
    private DogUpdateRequestDto updateRequestDto;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .kakaoId("12345")
                .email("user@example.com")
                .nickname("사용자")
                .build();
        ReflectionTestUtils.setField(member, "memberId", 1L);

        breed = Breed.builder()
                .name("골든 리트리버")
                .build();
        ReflectionTestUtils.setField(breed, "breedId", 1L);

        Location location = Location.builder()
                .state("서울시")
                .district("강남구")
                .build();

        shelter = Shelter.builder()
                .name("서울시 강남구 동물보호센터")
                .location(location)
                .shelterContact("02-1234-5678")
                .build();
        ReflectionTestUtils.setField(shelter, "shelterId", 1L);

        dog = Dog.builder()
                .name("바둑이")
                .breed(breed)
                .birthDate("202201")
                .gender(Gender.MALE)
                .dogSize(DogSize.중형)
                .weight(new BigDecimal("15.5"))
                .healthStatus("예방접종 완료")
                .description("온순한 성격")
                .adoptionStatus(AdoptionStatus.입양_가능)
                .imageUrl("https://example.com/old-image.jpg")
                .member(member)
                .shelter(shelter)
                .build();
        ReflectionTestUtils.setField(dog, "dogId", 1L);

        updateRequestDto = new DogUpdateRequestDto();
    }

    @Test
    @DisplayName("Dog 정보 전체 업데이트 성공")
    void updateDogInfo_FullUpdate_Success() {
        // Given
        Breed newBreed = Breed.builder()
                .name("말티즈")
                .build();
        ReflectionTestUtils.setField(newBreed, "breedId", 2L);

        Location newLocation = Location.builder()
                .state("부산시")
                .district("해운대구")
                .build();

        Shelter newShelter = Shelter.builder()
                .name("부산시 동물보호센터")
                .location(newLocation)
                .shelterContact("051-1234-5678")
                .build();
        ReflectionTestUtils.setField(newShelter, "shelterId", 2L);

        ReflectionTestUtils.setField(updateRequestDto, "name", "새이름");
        ReflectionTestUtils.setField(updateRequestDto, "birthDate", "202302");
        ReflectionTestUtils.setField(updateRequestDto, "gender", Gender.FEMALE);
        ReflectionTestUtils.setField(updateRequestDto, "dogSize", DogSize.소형);
        ReflectionTestUtils.setField(updateRequestDto, "weight", new BigDecimal("8.0"));
        ReflectionTestUtils.setField(updateRequestDto, "healthStatus", "새 건강상태");
        ReflectionTestUtils.setField(updateRequestDto, "description", "새 설명");
        ReflectionTestUtils.setField(updateRequestDto, "adoptionStatus", AdoptionStatus.입양_절차_중);
        ReflectionTestUtils.setField(updateRequestDto, "imageUrl", "https://example.com/new-image.jpg");

        // When
        dogUpdater.updateDogInfo(dog, updateRequestDto, newBreed, newShelter);

        // Then
        assertThat(dog.getName()).isEqualTo("새이름");
        assertThat(dog.getBreed()).isEqualTo(newBreed);
        assertThat(dog.getBirthDate()).isEqualTo("202302");
        assertThat(dog.getGender()).isEqualTo(Gender.FEMALE);
        assertThat(dog.getDogSize()).isEqualTo(DogSize.소형);
        assertThat(dog.getWeight()).isEqualTo(new BigDecimal("8.0"));
        assertThat(dog.getHealthStatus()).isEqualTo("새 건강상태");
        assertThat(dog.getDescription()).isEqualTo("새 설명");
        assertThat(dog.getAdoptionStatus()).isEqualTo(AdoptionStatus.입양_절차_중);
        assertThat(dog.getImageUrl()).isEqualTo("https://example.com/new-image.jpg");
        assertThat(dog.getShelter()).isEqualTo(newShelter);

        verify(uploadService).deleteImageFromStorage("https://example.com/old-image.jpg");
    }

    @Test
    @DisplayName("Dog 정보 부분 업데이트 성공 - null 필드는 기존값 유지")
    void updateDogInfo_PartialUpdate_Success() {
        // Given
        ReflectionTestUtils.setField(updateRequestDto, "name", "새이름만변경");
        ReflectionTestUtils.setField(updateRequestDto, "weight", new BigDecimal("20.0"));
        // 나머지 필드는 null로 유지

        // When
        dogUpdater.updateDogInfo(dog, updateRequestDto, breed, shelter);

        // Then
        assertThat(dog.getName()).isEqualTo("새이름만변경");
        assertThat(dog.getWeight()).isEqualTo(new BigDecimal("20.0"));
        // 기존 값들이 유지되는지 확인
        assertThat(dog.getBreed()).isEqualTo(breed);
        assertThat(dog.getBirthDate()).isEqualTo("202201");
        assertThat(dog.getGender()).isEqualTo(Gender.MALE);
        assertThat(dog.getDogSize()).isEqualTo(DogSize.중형);
        assertThat(dog.getHealthStatus()).isEqualTo("예방접종 완료");
        assertThat(dog.getDescription()).isEqualTo("온순한 성격");
        assertThat(dog.getAdoptionStatus()).isEqualTo(AdoptionStatus.입양_가능);
        assertThat(dog.getImageUrl()).isEqualTo("https://example.com/old-image.jpg");

        verify(uploadService, never()).deleteImageFromStorage(any());
    }

    @Test
    @DisplayName("Dog 이미지 URL 변경 시 기존 이미지 삭제")
    void updateDogInfo_ImageUrlChanged_DeleteOldImage() {
        // Given
        ReflectionTestUtils.setField(updateRequestDto, "imageUrl", "https://example.com/new-image.jpg");

        // When
        dogUpdater.updateDogInfo(dog, updateRequestDto, breed, shelter);

        // Then
        assertThat(dog.getImageUrl()).isEqualTo("https://example.com/new-image.jpg");
        verify(uploadService).deleteImageFromStorage("https://example.com/old-image.jpg");
    }

    @Test
    @DisplayName("Dog 이미지 URL 변경하지 않을 때 기존 이미지 유지")
    void updateDogInfo_ImageUrlNotChanged_KeepOldImage() {
        // Given
        ReflectionTestUtils.setField(updateRequestDto, "name", "이름만변경");
        // imageUrl은 null로 유지

        // When
        dogUpdater.updateDogInfo(dog, updateRequestDto, breed, shelter);

        // Then
        assertThat(dog.getImageUrl()).isEqualTo("https://example.com/old-image.jpg");
        verify(uploadService, never()).deleteImageFromStorage(any());
    }

    @Test
    @DisplayName("Dog 이미지 삭제 실패 시에도 업데이트 계속 진행")
    void updateDogInfo_ImageDeleteFailed_ContinueUpdate() {
        // Given
        ReflectionTestUtils.setField(updateRequestDto, "name", "새이름");
        ReflectionTestUtils.setField(updateRequestDto, "imageUrl", "https://example.com/new-image.jpg");

        doThrow(new RuntimeException("이미지 삭제 실패")).when(uploadService)
                .deleteImageFromStorage("https://example.com/old-image.jpg");

        // When & Then (예외가 발생하지 않아야 함)
        dogUpdater.updateDogInfo(dog, updateRequestDto, breed, shelter);

        // Then
        assertThat(dog.getName()).isEqualTo("새이름");
        assertThat(dog.getImageUrl()).isEqualTo("https://example.com/new-image.jpg");
        verify(uploadService).deleteImageFromStorage("https://example.com/old-image.jpg");
    }

    @Test
    @DisplayName("Dog 이미지 URL이 동일할 때 기존 이미지 삭제하지 않음")
    void updateDogInfo_SameImageUrl_NoDelete() {
        // Given
        ReflectionTestUtils.setField(updateRequestDto, "name", "새이름");
        ReflectionTestUtils.setField(updateRequestDto, "imageUrl", "https://example.com/old-image.jpg"); // 동일한 URL

        // When
        dogUpdater.updateDogInfo(dog, updateRequestDto, breed, shelter);

        // Then
        assertThat(dog.getName()).isEqualTo("새이름");
        assertThat(dog.getImageUrl()).isEqualTo("https://example.com/old-image.jpg");
        verify(uploadService, never()).deleteImageFromStorage(any());
    }

    @Test
    @DisplayName("Dog 빈 문자열 이미지 URL 처리")
    void updateDogInfo_EmptyImageUrl_KeepOldImage() {
        // Given
        ReflectionTestUtils.setField(updateRequestDto, "name", "새이름");
        ReflectionTestUtils.setField(updateRequestDto, "imageUrl", ""); // 빈 문자열

        // When
        dogUpdater.updateDogInfo(dog, updateRequestDto, breed, shelter);

        // Then
        assertThat(dog.getName()).isEqualTo("새이름");
        assertThat(dog.getImageUrl()).isEqualTo("https://example.com/old-image.jpg"); // 기존 URL 유지
        verify(uploadService, never()).deleteImageFromStorage(any());
    }

    @Test
    @DisplayName("Dog 공백 이미지 URL 처리")
    void updateDogInfo_BlankImageUrl_KeepOldImage() {
        // Given
        ReflectionTestUtils.setField(updateRequestDto, "name", "새이름");
        ReflectionTestUtils.setField(updateRequestDto, "imageUrl", "   "); // 공백

        // When
        dogUpdater.updateDogInfo(dog, updateRequestDto, breed, shelter);

        // Then
        assertThat(dog.getName()).isEqualTo("새이름");
        assertThat(dog.getImageUrl()).isEqualTo("https://example.com/old-image.jpg"); // 기존 URL 유지
        verify(uploadService, never()).deleteImageFromStorage(any());
    }

    @Test
    @DisplayName("Dog 모든 필드 null일 때 기존값 유지")
    void updateDogInfo_AllFieldsNull_KeepOriginalValues() {
        // Given
        // updateRequestDto의 모든 필드가 null인 상태

        // When
        dogUpdater.updateDogInfo(dog, updateRequestDto, breed, shelter);

        // Then - 모든 기존 값이 유지되어야 함
        assertThat(dog.getName()).isEqualTo("바둑이");
        assertThat(dog.getBreed()).isEqualTo(breed);
        assertThat(dog.getBirthDate()).isEqualTo("202201");
        assertThat(dog.getGender()).isEqualTo(Gender.MALE);
        assertThat(dog.getDogSize()).isEqualTo(DogSize.중형);
        assertThat(dog.getWeight()).isEqualTo(new BigDecimal("15.5"));
        assertThat(dog.getHealthStatus()).isEqualTo("예방접종 완료");
        assertThat(dog.getDescription()).isEqualTo("온순한 성격");
        assertThat(dog.getAdoptionStatus()).isEqualTo(AdoptionStatus.입양_가능);
        assertThat(dog.getImageUrl()).isEqualTo("https://example.com/old-image.jpg");
        assertThat(dog.getShelter()).isEqualTo(shelter);

        verify(uploadService, never()).deleteImageFromStorage(any());
    }

    @Test
    @DisplayName("Dog Breed와 Shelter 변경 테스트")
    void updateDogInfo_BreedAndShelterChange() {
        // Given
        Breed newBreed = Breed.builder()
                .name("푸들")
                .build();
        ReflectionTestUtils.setField(newBreed, "breedId", 3L);

        Location newLocation = Location.builder()
                .state("대구시")
                .district("중구")
                .build();

        Shelter newShelter = Shelter.builder()
                .name("대구시 동물보호센터")
                .location(newLocation)
                .shelterContact("053-1234-5678")
                .build();
        ReflectionTestUtils.setField(newShelter, "shelterId", 3L);

        // When
        dogUpdater.updateDogInfo(dog, updateRequestDto, newBreed, newShelter);

        // Then
        assertThat(dog.getBreed()).isEqualTo(newBreed);
        assertThat(dog.getShelter()).isEqualTo(newShelter);
    }
}