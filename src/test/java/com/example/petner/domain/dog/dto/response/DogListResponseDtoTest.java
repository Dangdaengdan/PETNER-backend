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

class DogListResponseDtoTest {

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
    @DisplayName("Dog 엔티티로부터 DogListResponseDto 생성 성공")
    void from_Success() {
        // When
        DogListResponseDto result = DogListResponseDto.from(dog);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDogId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("바둑이");
        assertThat(result.getBreedName()).isEqualTo("골든 리트리버");
        assertThat(result.getGender()).isEqualTo(Gender.MALE);
        assertThat(result.getDogSize()).isEqualTo(DogSize.중형);
        assertThat(result.getWeight()).isEqualTo(new BigDecimal("15.5"));
        assertThat(result.getAdoptionStatus()).isEqualTo(AdoptionStatus.입양_가능);
        assertThat(result.getImageUrl()).isEqualTo("https://example.com/dog-image.jpg");
        assertThat(result.getMemberNickname()).isEqualTo("사용자");
        assertThat(result.getShelterName()).isEqualTo("서울시 강남구 동물보호센터");
        assertThat(result.getBirthDate()).isEqualTo("202201");
        assertThat(result.getCreatedAt()).isEqualTo(testTime);
    }

    @Test
    @DisplayName("Shelter가 null일 때 ShelterName도 null")
    void from_WithoutShelter_Success() {
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

        // When
        DogListResponseDto result = DogListResponseDto.from(dogWithoutShelter);

        // Then
        assertThat(result.getShelterName()).isNull();
        assertThat(result.getDogId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("체리");
        assertThat(result.getMemberNickname()).isEqualTo("사용자");
    }

    @Test
    @DisplayName("Builder 패턴으로 직접 생성")
    void builder_Success() {
        // When
        DogListResponseDto result = DogListResponseDto.builder()
                .dogId(1L)
                .name("직접생성")
                .breedName("말티즈")
                .gender(Gender.FEMALE)
                .dogSize(DogSize.소형)
                .weight(new BigDecimal("8.0"))
                .adoptionStatus(AdoptionStatus.입양_절차_중)
                .imageUrl("https://example.com/direct.jpg")
                .memberNickname("직접사용자")
                .shelterName("직접보호소")
                .birthDate("202302")
                .createdAt(testTime)
                .build();

        // Then
        assertThat(result.getDogId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("직접생성");
        assertThat(result.getBreedName()).isEqualTo("말티즈");
        assertThat(result.getGender()).isEqualTo(Gender.FEMALE);
        assertThat(result.getDogSize()).isEqualTo(DogSize.소형);
        assertThat(result.getWeight()).isEqualTo(new BigDecimal("8.0"));
        assertThat(result.getAdoptionStatus()).isEqualTo(AdoptionStatus.입양_절차_중);
        assertThat(result.getMemberNickname()).isEqualTo("직접사용자");
        assertThat(result.getShelterName()).isEqualTo("직접보호소");
        assertThat(result.getBirthDate()).isEqualTo("202302");
    }

    @Test
    @DisplayName("다양한 AdoptionStatus 값 테스트")
    void adoptionStatus_Values() {
        // Test 입양_가능
        dog.updateDogInfo(dog.getName(), dog.getBreed(), dog.getBirthDate(),
                dog.getGender(), dog.getDogSize(), dog.getWeight(),
                dog.getHealthStatus(), dog.getDescription(),
                AdoptionStatus.입양_가능, dog.getImageUrl(), dog.getShelter());
        DogListResponseDto result1 = DogListResponseDto.from(dog);
        assertThat(result1.getAdoptionStatus()).isEqualTo(AdoptionStatus.입양_가능);

        // Test 입양_절차_중
        dog.updateDogInfo(dog.getName(), dog.getBreed(), dog.getBirthDate(),
                dog.getGender(), dog.getDogSize(), dog.getWeight(),
                dog.getHealthStatus(), dog.getDescription(),
                AdoptionStatus.입양_절차_중, dog.getImageUrl(), dog.getShelter());
        DogListResponseDto result2 = DogListResponseDto.from(dog);
        assertThat(result2.getAdoptionStatus()).isEqualTo(AdoptionStatus.입양_절차_중);

        // Test 입양_완료
        dog.updateDogInfo(dog.getName(), dog.getBreed(), dog.getBirthDate(),
                dog.getGender(), dog.getDogSize(), dog.getWeight(),
                dog.getHealthStatus(), dog.getDescription(),
                AdoptionStatus.입양_완료, dog.getImageUrl(), dog.getShelter());
        DogListResponseDto result3 = DogListResponseDto.from(dog);
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
        DogListResponseDto result1 = DogListResponseDto.from(dog);
        assertThat(result1.getDogSize()).isEqualTo(DogSize.소형);

        // Test 중형
        dog.updateDogInfo(dog.getName(), dog.getBreed(), dog.getBirthDate(),
                dog.getGender(), DogSize.중형, dog.getWeight(),
                dog.getHealthStatus(), dog.getDescription(),
                dog.getAdoptionStatus(), dog.getImageUrl(), dog.getShelter());
        DogListResponseDto result2 = DogListResponseDto.from(dog);
        assertThat(result2.getDogSize()).isEqualTo(DogSize.중형);

        // Test 대형
        dog.updateDogInfo(dog.getName(), dog.getBreed(), dog.getBirthDate(),
                dog.getGender(), DogSize.대형, dog.getWeight(),
                dog.getHealthStatus(), dog.getDescription(),
                dog.getAdoptionStatus(), dog.getImageUrl(), dog.getShelter());
        DogListResponseDto result3 = DogListResponseDto.from(dog);
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
        DogListResponseDto result1 = DogListResponseDto.from(dog);
        assertThat(result1.getGender()).isEqualTo(Gender.MALE);

        // Test FEMALE
        dog.updateDogInfo(dog.getName(), dog.getBreed(), dog.getBirthDate(),
                Gender.FEMALE, dog.getDogSize(), dog.getWeight(),
                dog.getHealthStatus(), dog.getDescription(),
                dog.getAdoptionStatus(), dog.getImageUrl(), dog.getShelter());
        DogListResponseDto result2 = DogListResponseDto.from(dog);
        assertThat(result2.getGender()).isEqualTo(Gender.FEMALE);
    }

    @Test
    @DisplayName("다양한 견종명 테스트")
    void breedName_Values() {
        // Given
        Breed maltese = Breed.builder().name("말티즈").build();
        Breed poodle = Breed.builder().name("푸들").build();
        Breed husky = Breed.builder().name("시베리안 허스키").build();

        String[] breedNames = {"말티즈", "푸들", "시베리안 허스키"};
        Breed[] breeds = {maltese, poodle, husky};

        for (int i = 0; i < breeds.length; i++) {
            // When
            dog.updateDogInfo(dog.getName(), breeds[i], dog.getBirthDate(),
                    dog.getGender(), dog.getDogSize(), dog.getWeight(),
                    dog.getHealthStatus(), dog.getDescription(),
                    dog.getAdoptionStatus(), dog.getImageUrl(), dog.getShelter());
            DogListResponseDto result = DogListResponseDto.from(dog);

            // Then
            assertThat(result.getBreedName()).isEqualTo(breedNames[i]);
        }
    }

    @Test
    @DisplayName("시간 필드 정확히 매핑됨")
    void timeField_MappedCorrectly() {
        // Given
        LocalDateTime specificTime = LocalDateTime.of(2024, 3, 15, 14, 30, 45);
        ReflectionTestUtils.setField(dog, "createdAt", specificTime);

        // When
        DogListResponseDto result = DogListResponseDto.from(dog);

        // Then
        assertThat(result.getCreatedAt()).isEqualTo(specificTime);
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
        DogListResponseDto result = DogListResponseDto.from(dog);

        // Then
        assertThat(result.getWeight()).isEqualTo(specificWeight);
        assertThat(result.getWeight().compareTo(new BigDecimal("23.75"))).isEqualTo(0);
    }

    @Test
    @DisplayName("다양한 멤버 닉네임 테스트")
    void memberNickname_Values() {
        // Given
        String[] nicknames = {"애견인", "동물사랑", "펫케어전문가", "강아지매니아"};

        for (String nickname : nicknames) {
            Member testMember = Member.builder()
                    .kakaoId("test123")
                    .email("test@example.com")
                    .nickname(nickname)
                    .build();

            // When
            dog = Dog.builder()
                    .name("테스트강아지")
                    .breed(breed)
                    .birthDate("202301")
                    .gender(Gender.MALE)
                    .dogSize(DogSize.소형)
                    .weight(new BigDecimal("10.0"))
                    .adoptionStatus(AdoptionStatus.입양_가능)
                    .imageUrl("https://example.com/test.jpg")
                    .member(testMember)
                    .shelter(shelter)
                    .build();
            ReflectionTestUtils.setField(dog, "dogId", 1L);
            ReflectionTestUtils.setField(dog, "createdAt", testTime);

            DogListResponseDto result = DogListResponseDto.from(dog);

            // Then
            assertThat(result.getMemberNickname()).isEqualTo(nickname);
        }
    }

    @Test
    @DisplayName("필수 필드들 누락 없이 매핑됨")
    void requiredFields_AllMapped() {
        // When
        DogListResponseDto result = DogListResponseDto.from(dog);

        // Then - 필수 필드들이 모두 존재하는지 확인
        assertThat(result.getDogId()).isNotNull();
        assertThat(result.getName()).isNotNull();
        assertThat(result.getBreedName()).isNotNull();
        assertThat(result.getGender()).isNotNull();
        assertThat(result.getDogSize()).isNotNull();
        assertThat(result.getWeight()).isNotNull();
        assertThat(result.getAdoptionStatus()).isNotNull();
        assertThat(result.getImageUrl()).isNotNull();
        assertThat(result.getMemberNickname()).isNotNull();
        assertThat(result.getBirthDate()).isNotNull();
        assertThat(result.getCreatedAt()).isNotNull();
        // shelterName은 선택적 필드이므로 null 가능
    }
}