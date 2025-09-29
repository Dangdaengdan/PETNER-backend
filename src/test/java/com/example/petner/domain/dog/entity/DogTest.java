package com.example.petner.domain.dog.entity;

import com.example.petner.domain.breed.entity.Breed;
import com.example.petner.domain.dog.common.AdoptionStatus;
import com.example.petner.domain.dog.common.DogSize;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.shelter.entity.Shelter;
import com.example.petner.domain.location.entity.Location;
import com.example.petner.global.config.common.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DogTest {

    private Dog dog;
    private Member member;
    private Breed breed;
    private Shelter shelter;

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
                .healthStatus("예방접종 완료, 건강상태 양호")
                .description("사람을 좋아하고 온순한 성격입니다")
                .adoptionStatus(AdoptionStatus.입양_가능)
                .imageUrl("https://example.com/dog-image.jpg")
                .member(member)
                .shelter(shelter)
                .build();
        ReflectionTestUtils.setField(dog, "dogId", 1L);
        ReflectionTestUtils.setField(dog, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(dog, "updatedAt", LocalDateTime.now());
    }

    @Test
    @DisplayName("Dog 엔티티 Builder 패턴으로 정상 생성")
    void createDog_Success() {
        // Then
        assertThat(dog.getDogId()).isEqualTo(1L);
        assertThat(dog.getName()).isEqualTo("바둑이");
        assertThat(dog.getBreed()).isEqualTo(breed);
        assertThat(dog.getBirthDate()).isEqualTo("202201");
        assertThat(dog.getGender()).isEqualTo(Gender.MALE);
        assertThat(dog.getDogSize()).isEqualTo(DogSize.중형);
        assertThat(dog.getWeight()).isEqualTo(new BigDecimal("15.5"));
        assertThat(dog.getHealthStatus()).isEqualTo("예방접종 완료, 건강상태 양호");
        assertThat(dog.getDescription()).isEqualTo("사람을 좋아하고 온순한 성격입니다");
        assertThat(dog.getAdoptionStatus()).isEqualTo(AdoptionStatus.입양_가능);
        assertThat(dog.getImageUrl()).isEqualTo("https://example.com/dog-image.jpg");
        assertThat(dog.getMember()).isEqualTo(member);
        assertThat(dog.getShelter()).isEqualTo(shelter);
        assertThat(dog.getDeleted()).isFalse();
    }

    @Test
    @DisplayName("Dog 엔티티 Shelter 없이 생성 가능")
    void createDog_WithoutShelter_Success() {
        // Given
        Dog dogWithoutShelter = Dog.builder()
                .name("체리")
                .breed(breed)
                .birthDate("202301")
                .gender(Gender.FEMALE)
                .dogSize(DogSize.소형)
                .weight(new BigDecimal("5.2"))
                .healthStatus("건강함")
                .description("활발한 성격")
                .adoptionStatus(AdoptionStatus.입양_가능)
                .imageUrl("https://example.com/cherry.jpg")
                .member(member)
                .shelter(null)
                .build();

        // Then
        assertThat(dogWithoutShelter.getName()).isEqualTo("체리");
        assertThat(dogWithoutShelter.getShelter()).isNull();
        assertThat(dogWithoutShelter.getMember()).isEqualTo(member);
    }

    @Test
    @DisplayName("Dog 정보 업데이트 성공")
    void updateDogInfo_Success() {
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

        // When
        dog.updateDogInfo(
                "새이름",
                newBreed,
                "202302",
                Gender.FEMALE,
                DogSize.소형,
                new BigDecimal("8.0"),
                "새 건강상태",
                "새 설명",
                AdoptionStatus.입양_절차_중,
                "https://example.com/new-image.jpg",
                newShelter
        );

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
    }

    @Test
    @DisplayName("Dog 소프트 삭제 성공")
    void softDelete_Success() {
        // Given
        assertThat(dog.isDeleted()).isFalse();

        // When
        dog.softDelete();

        // Then
        assertThat(dog.isDeleted()).isTrue();
        assertThat(dog.getDeleted()).isTrue();
    }

    @Test
    @DisplayName("Dog 삭제 상태 확인")
    void isDeleted_Check() {
        // Given - 기본 상태는 삭제되지 않음
        assertThat(dog.isDeleted()).isFalse();

        // When - 소프트 삭제 수행
        dog.softDelete();

        // Then - 삭제 상태로 변경됨
        assertThat(dog.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("Dog 엔티티 필드 Getter 테스트")
    void dogGetters_Success() {
        // Then
        assertThat(dog.getDogId()).isNotNull();
        assertThat(dog.getName()).isNotNull();
        assertThat(dog.getBreed()).isNotNull();
        assertThat(dog.getBirthDate()).isNotNull();
        assertThat(dog.getGender()).isNotNull();
        assertThat(dog.getDogSize()).isNotNull();
        assertThat(dog.getWeight()).isNotNull();
        assertThat(dog.getAdoptionStatus()).isNotNull();
        assertThat(dog.getImageUrl()).isNotNull();
        assertThat(dog.getMember()).isNotNull();
        assertThat(dog.getCreatedAt()).isNotNull();
        assertThat(dog.getUpdatedAt()).isNotNull();
        assertThat(dog.getDeleted()).isNotNull();
    }

    @Test
    @DisplayName("Dog 엔티티 Builder 패턴 필수 필드 확인")
    void dogBuilder_RequiredFields() {
        // Given & When
        Dog minimalDog = Dog.builder()
                .name("최소강아지")
                .breed(breed)
                .birthDate("202301")
                .gender(Gender.FEMALE)
                .dogSize(DogSize.소형)
                .weight(new BigDecimal("3.0"))
                .adoptionStatus(AdoptionStatus.입양_가능)
                .imageUrl("https://example.com/minimal.jpg")
                .member(member)
                .build();

        // Then
        assertThat(minimalDog.getName()).isEqualTo("최소강아지");
        assertThat(minimalDog.getBreed()).isEqualTo(breed);
        assertThat(minimalDog.getMember()).isEqualTo(member);
        assertThat(minimalDog.getShelter()).isNull(); // 선택적 필드
        assertThat(minimalDog.getHealthStatus()).isNull(); // 선택적 필드
        assertThat(minimalDog.getDescription()).isNull(); // 선택적 필드
    }

    @Test
    @DisplayName("Dog 엔티티 삭제 플래그 초기값 확인")
    void dogDeleted_DefaultValue() {
        // Given & When
        Dog newDog = Dog.builder()
                .name("신규강아지")
                .breed(breed)
                .birthDate("202301")
                .gender(Gender.MALE)
                .dogSize(DogSize.중형)
                .weight(new BigDecimal("10.0"))
                .adoptionStatus(AdoptionStatus.입양_가능)
                .imageUrl("https://example.com/new.jpg")
                .member(member)
                .build();

        // Then
        assertThat(newDog.getDeleted()).isFalse();
        assertThat(newDog.isDeleted()).isFalse();
    }

    @Test
    @DisplayName("다양한 AdoptionStatus 값 테스트")
    void adoptionStatus_Values() {
        // Test 입양_가능
        dog.updateDogInfo(dog.getName(), dog.getBreed(), dog.getBirthDate(),
                dog.getGender(), dog.getDogSize(), dog.getWeight(),
                dog.getHealthStatus(), dog.getDescription(),
                AdoptionStatus.입양_가능, dog.getImageUrl(), dog.getShelter());
        assertThat(dog.getAdoptionStatus()).isEqualTo(AdoptionStatus.입양_가능);

        // Test 입양_절차_중
        dog.updateDogInfo(dog.getName(), dog.getBreed(), dog.getBirthDate(),
                dog.getGender(), dog.getDogSize(), dog.getWeight(),
                dog.getHealthStatus(), dog.getDescription(),
                AdoptionStatus.입양_절차_중, dog.getImageUrl(), dog.getShelter());
        assertThat(dog.getAdoptionStatus()).isEqualTo(AdoptionStatus.입양_절차_중);

        // Test 입양_완료
        dog.updateDogInfo(dog.getName(), dog.getBreed(), dog.getBirthDate(),
                dog.getGender(), dog.getDogSize(), dog.getWeight(),
                dog.getHealthStatus(), dog.getDescription(),
                AdoptionStatus.입양_완료, dog.getImageUrl(), dog.getShelter());
        assertThat(dog.getAdoptionStatus()).isEqualTo(AdoptionStatus.입양_완료);
    }

    @Test
    @DisplayName("다양한 DogSize 값 테스트")
    void dogSize_Values() {
        // Test 소형
        dog.updateDogInfo(dog.getName(), dog.getBreed(), dog.getBirthDate(),
                dog.getGender(), DogSize.소형, dog.getWeight(),
                dog.getHealthStatus(), dog.getDescription(),
                dog.getAdoptionStatus(), dog.getImageUrl(), dog.getShelter());
        assertThat(dog.getDogSize()).isEqualTo(DogSize.소형);

        // Test 중형
        dog.updateDogInfo(dog.getName(), dog.getBreed(), dog.getBirthDate(),
                dog.getGender(), DogSize.중형, dog.getWeight(),
                dog.getHealthStatus(), dog.getDescription(),
                dog.getAdoptionStatus(), dog.getImageUrl(), dog.getShelter());
        assertThat(dog.getDogSize()).isEqualTo(DogSize.중형);

        // Test 대형
        dog.updateDogInfo(dog.getName(), dog.getBreed(), dog.getBirthDate(),
                dog.getGender(), DogSize.대형, dog.getWeight(),
                dog.getHealthStatus(), dog.getDescription(),
                dog.getAdoptionStatus(), dog.getImageUrl(), dog.getShelter());
        assertThat(dog.getDogSize()).isEqualTo(DogSize.대형);
    }
}