package com.example.petner.domain.dog.dto.response;

import com.example.petner.domain.breed.entity.Breed;
import com.example.petner.domain.dog.common.AdoptionStatus;
import com.example.petner.domain.dog.common.DogSize;
import com.example.petner.domain.dog.entity.Dog;
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

class DogResponseDtoTest {

    private Dog dog;
    private Member member;
    private Breed breed;
    private Shelter shelter;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        testTime = LocalDateTime.of(2024, 1, 1, 12, 0, 0);

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
                .imageUrl("https://example.com/dog-image.jpg")
                .member(member)
                .shelter(shelter)
                .build();
        ReflectionTestUtils.setField(dog, "dogId", 1L);
        ReflectionTestUtils.setField(dog, "createdAt", testTime);
        ReflectionTestUtils.setField(dog, "updatedAt", testTime);
    }

    @Test
    @DisplayName("Dog 엔티티로부터 DogResponseDto 생성 성공")
    void from_Success() {
        // When
        DogResponseDto result = DogResponseDto.from(dog);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDogId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("바둑이");
        assertThat(result.getBirthDate()).isEqualTo("202201");
        assertThat(result.getGender()).isEqualTo(Gender.MALE);
        assertThat(result.getDogSize()).isEqualTo(DogSize.중형);
        assertThat(result.getWeight()).isEqualTo(new BigDecimal("15.5"));
        assertThat(result.getHealthStatus()).isEqualTo("예방접종 완료");
        assertThat(result.getDescription()).isEqualTo("온순한 성격");
        assertThat(result.getAdoptionStatus()).isEqualTo(AdoptionStatus.입양_가능);
        assertThat(result.getImageUrl()).isEqualTo("https://example.com/dog-image.jpg");
        assertThat(result.getCreatedAt()).isEqualTo(testTime);
        assertThat(result.getUpdatedAt()).isEqualTo(testTime);
    }

    @Test
    @DisplayName("BreedInfo 중첩 클래스 정상 생성")
    void breedInfo_Success() {
        // When
        DogResponseDto result = DogResponseDto.from(dog);

        // Then
        assertThat(result.getBreed()).isNotNull();
        assertThat(result.getBreed().getBreedId()).isEqualTo(1L);
        assertThat(result.getBreed().getName()).isEqualTo("골든 리트리버");
    }

    @Test
    @DisplayName("MemberInfo 중첩 클래스 정상 생성")
    void memberInfo_Success() {
        // When
        DogResponseDto result = DogResponseDto.from(dog);

        // Then
        assertThat(result.getMember()).isNotNull();
        assertThat(result.getMember().getMemberId()).isEqualTo(1L);
        assertThat(result.getMember().getNickname()).isEqualTo("사용자");
    }

    @Test
    @DisplayName("ShelterInfo 중첩 클래스 정상 생성")
    void shelterInfo_Success() {
        // When
        DogResponseDto result = DogResponseDto.from(dog);

        // Then
        assertThat(result.getShelter()).isNotNull();
        assertThat(result.getShelter().getShelterId()).isEqualTo(1L);
        assertThat(result.getShelter().getName()).isEqualTo("서울시 강남구 동물보호센터");
        assertThat(result.getShelter().getContact()).isEqualTo("02-1234-5678");
    }

    @Test
    @DisplayName("Shelter가 null일 때 ShelterInfo도 null")
    void shelterInfo_Null() {
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
        ReflectionTestUtils.setField(dogWithoutShelter, "dogId", 2L);
        ReflectionTestUtils.setField(dogWithoutShelter, "createdAt", testTime);
        ReflectionTestUtils.setField(dogWithoutShelter, "updatedAt", testTime);

        // When
        DogResponseDto result = DogResponseDto.from(dogWithoutShelter);

        // Then
        assertThat(result.getShelter()).isNull();
        assertThat(result.getDogId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("체리");
    }

    @Test
    @DisplayName("BreedInfo.from() 정적 메서드 테스트")
    void breedInfo_from_Success() {
        // When
        DogResponseDto.BreedInfo result = DogResponseDto.BreedInfo.from(breed);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getBreedId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("골든 리트리버");
    }

    @Test
    @DisplayName("MemberInfo.from() 정적 메서드 테스트")
    void memberInfo_from_Success() {
        // When
        DogResponseDto.MemberInfo result = DogResponseDto.MemberInfo.from(member);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getMemberId()).isEqualTo(1L);
        assertThat(result.getNickname()).isEqualTo("사용자");
    }

    @Test
    @DisplayName("ShelterInfo.from() 정적 메서드 테스트")
    void shelterInfo_from_Success() {
        // When
        DogResponseDto.ShelterInfo result = DogResponseDto.ShelterInfo.from(shelter);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getShelterId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("서울시 강남구 동물보호센터");
        assertThat(result.getContact()).isEqualTo("02-1234-5678");
    }

    @Test
    @DisplayName("Builder 패턴으로 직접 생성")
    void builder_Success() {
        // When
        DogResponseDto result = DogResponseDto.builder()
                .dogId(1L)
                .name("직접생성")
                .breed(DogResponseDto.BreedInfo.builder()
                        .breedId(1L)
                        .name("말티즈")
                        .build())
                .birthDate("202302")
                .gender(Gender.FEMALE)
                .dogSize(DogSize.소형)
                .weight(new BigDecimal("8.0"))
                .healthStatus("건강함")
                .description("활발함")
                .adoptionStatus(AdoptionStatus.입양_절차_중)
                .imageUrl("https://example.com/direct.jpg")
                .member(DogResponseDto.MemberInfo.builder()
                        .memberId(2L)
                        .nickname("직접사용자")
                        .build())
                .shelter(null)
                .createdAt(testTime)
                .updatedAt(testTime)
                .build();

        // Then
        assertThat(result.getDogId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("직접생성");
        assertThat(result.getBreed().getName()).isEqualTo("말티즈");
        assertThat(result.getGender()).isEqualTo(Gender.FEMALE);
        assertThat(result.getDogSize()).isEqualTo(DogSize.소형);
        assertThat(result.getAdoptionStatus()).isEqualTo(AdoptionStatus.입양_절차_중);
        assertThat(result.getMember().getNickname()).isEqualTo("직접사용자");
        assertThat(result.getShelter()).isNull();
    }

    @Test
    @DisplayName("다양한 AdoptionStatus 값 테스트")
    void adoptionStatus_Values() {
        // Test 입양_가능
        dog.updateDogInfo(dog.getName(), dog.getBreed(), dog.getBirthDate(),
                dog.getGender(), dog.getDogSize(), dog.getWeight(),
                dog.getHealthStatus(), dog.getDescription(),
                AdoptionStatus.입양_가능, dog.getImageUrl(), dog.getShelter());
        DogResponseDto result1 = DogResponseDto.from(dog);
        assertThat(result1.getAdoptionStatus()).isEqualTo(AdoptionStatus.입양_가능);

        // Test 입양_절차_중
        dog.updateDogInfo(dog.getName(), dog.getBreed(), dog.getBirthDate(),
                dog.getGender(), dog.getDogSize(), dog.getWeight(),
                dog.getHealthStatus(), dog.getDescription(),
                AdoptionStatus.입양_절차_중, dog.getImageUrl(), dog.getShelter());
        DogResponseDto result2 = DogResponseDto.from(dog);
        assertThat(result2.getAdoptionStatus()).isEqualTo(AdoptionStatus.입양_절차_중);

        // Test 입양_완료
        dog.updateDogInfo(dog.getName(), dog.getBreed(), dog.getBirthDate(),
                dog.getGender(), dog.getDogSize(), dog.getWeight(),
                dog.getHealthStatus(), dog.getDescription(),
                AdoptionStatus.입양_완료, dog.getImageUrl(), dog.getShelter());
        DogResponseDto result3 = DogResponseDto.from(dog);
        assertThat(result3.getAdoptionStatus()).isEqualTo(AdoptionStatus.입양_완료);
    }

    @Test
    @DisplayName("다양한 DogSize 값 테스트")
    void dogSize_Values() {
        // Test 소형
        dog.updateDogInfo(dog.getName(), dog.getBreed(), dog.getBirthDate(),
                dog.getGender(), DogSize.소형, dog.getWeight(),
                dog.getHealthStatus(), dog.getDescription(),
                dog.getAdoptionStatus(), dog.getImageUrl(), dog.getShelter());
        DogResponseDto result1 = DogResponseDto.from(dog);
        assertThat(result1.getDogSize()).isEqualTo(DogSize.소형);

        // Test 중형
        dog.updateDogInfo(dog.getName(), dog.getBreed(), dog.getBirthDate(),
                dog.getGender(), DogSize.중형, dog.getWeight(),
                dog.getHealthStatus(), dog.getDescription(),
                dog.getAdoptionStatus(), dog.getImageUrl(), dog.getShelter());
        DogResponseDto result2 = DogResponseDto.from(dog);
        assertThat(result2.getDogSize()).isEqualTo(DogSize.중형);

        // Test 대형
        dog.updateDogInfo(dog.getName(), dog.getBreed(), dog.getBirthDate(),
                dog.getGender(), DogSize.대형, dog.getWeight(),
                dog.getHealthStatus(), dog.getDescription(),
                dog.getAdoptionStatus(), dog.getImageUrl(), dog.getShelter());
        DogResponseDto result3 = DogResponseDto.from(dog);
        assertThat(result3.getDogSize()).isEqualTo(DogSize.대형);
    }

    @Test
    @DisplayName("다양한 Gender 값 테스트")
    void gender_Values() {
        // Test MALE
        dog.updateDogInfo(dog.getName(), dog.getBreed(), dog.getBirthDate(),
                Gender.MALE, dog.getDogSize(), dog.getWeight(),
                dog.getHealthStatus(), dog.getDescription(),
                dog.getAdoptionStatus(), dog.getImageUrl(), dog.getShelter());
        DogResponseDto result1 = DogResponseDto.from(dog);
        assertThat(result1.getGender()).isEqualTo(Gender.MALE);

        // Test FEMALE
        dog.updateDogInfo(dog.getName(), dog.getBreed(), dog.getBirthDate(),
                Gender.FEMALE, dog.getDogSize(), dog.getWeight(),
                dog.getHealthStatus(), dog.getDescription(),
                dog.getAdoptionStatus(), dog.getImageUrl(), dog.getShelter());
        DogResponseDto result2 = DogResponseDto.from(dog);
        assertThat(result2.getGender()).isEqualTo(Gender.FEMALE);
    }

    @Test
    @DisplayName("시간 필드들 정확히 매핑됨")
    void timeFields_MappedCorrectly() {
        // Given
        LocalDateTime specificTime = LocalDateTime.of(2024, 3, 15, 14, 30, 45);
        ReflectionTestUtils.setField(dog, "createdAt", specificTime);
        ReflectionTestUtils.setField(dog, "updatedAt", specificTime.plusHours(1));

        // When
        DogResponseDto result = DogResponseDto.from(dog);

        // Then
        assertThat(result.getCreatedAt()).isEqualTo(specificTime);
        assertThat(result.getUpdatedAt()).isEqualTo(specificTime.plusHours(1));
    }

    @Test
    @DisplayName("BigDecimal weight 필드 정확히 매핑됨")
    void weight_MappedCorrectly() {
        // Given
        BigDecimal specificWeight = new BigDecimal("23.75");
        dog.updateDogInfo(dog.getName(), dog.getBreed(), dog.getBirthDate(),
                dog.getGender(), dog.getDogSize(), specificWeight,
                dog.getHealthStatus(), dog.getDescription(),
                dog.getAdoptionStatus(), dog.getImageUrl(), dog.getShelter());

        // When
        DogResponseDto result = DogResponseDto.from(dog);

        // Then
        assertThat(result.getWeight()).isEqualTo(specificWeight);
        assertThat(result.getWeight().compareTo(new BigDecimal("23.75"))).isEqualTo(0);
    }
}