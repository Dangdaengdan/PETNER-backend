package com.example.petner.domain.shelter.dto.response;

import com.example.petner.domain.location.entity.Location;
import com.example.petner.domain.shelter.entity.Shelter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class ShelterSearchResponseDtoTest {

    private Shelter shelter;
    private Location location;

    @BeforeEach
    void setUp() {
        location = Location.builder()
                .state("서울시")
                .district("강남구")
                .build();
        ReflectionTestUtils.setField(location, "locationId", 1L);

        shelter = Shelter.builder()
                .name("서울시 강남구 동물보호센터")
                .detailAddress("서울시 강남구 테헤란로 123")
                .shelterContact("02-1234-5678")
                .location(location)
                .build();
        ReflectionTestUtils.setField(shelter, "shelterId", 1L);
    }

    @Test
    @DisplayName("Shelter 엔티티로부터 ShelterSearchResponseDto 생성 성공")
    void from_Success() {
        // When
        ShelterSearchResponseDto result = ShelterSearchResponseDto.from(shelter);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getShelterId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("서울시 강남구 동물보호센터");
    }

    @Test
    @DisplayName("Builder 패턴으로 직접 생성")
    void builder_Success() {
        // When
        ShelterSearchResponseDto result = ShelterSearchResponseDto.builder()
                .shelterId(1L)
                .name("직접생성보호소")
                .build();

        // Then
        assertThat(result.getShelterId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("직접생성보호소");
    }

    @Test
    @DisplayName("다양한 보호소 이름으로 DTO 생성")
    void from_VariousShelterNames() {
        // Given
        String[] shelterNames = {
                "부산시 동물보호센터",
                "대구시 해외동물보호센터",
                "인천시 서구 동물보호소",
                "광주시 남구 반려동물센터"
        };

        for (int i = 0; i < shelterNames.length; i++) {
            Shelter testShelter = Shelter.builder()
                    .name(shelterNames[i])
                    .detailAddress("테스트 주소")
                    .shelterContact("010-1234-567" + (i + 1))
                    .location(location)
                    .build();
            ReflectionTestUtils.setField(testShelter, "shelterId", (long) (i + 2));

            // When
            ShelterSearchResponseDto result = ShelterSearchResponseDto.from(testShelter);

            // Then
            assertThat(result.getShelterId()).isEqualTo((long) (i + 2));
            assertThat(result.getName()).isEqualTo(shelterNames[i]);
        }
    }

    @Test
    @DisplayName("긴 이름의 보호소로 DTO 생성")
    void from_LongShelterName() {
        // Given
        String longName = "매우긴이름을가진보호소입니다".repeat(5);
        Shelter longNameShelter = Shelter.builder()
                .name(longName)
                .detailAddress("긴이름 테스트 주소")
                .shelterContact("02-1234-5678")
                .location(location)
                .build();
        ReflectionTestUtils.setField(longNameShelter, "shelterId", 999L);

        // When
        ShelterSearchResponseDto result = ShelterSearchResponseDto.from(longNameShelter);

        // Then
        assertThat(result.getShelterId()).isEqualTo(999L);
        assertThat(result.getName()).isEqualTo(longName);
        assertThat(result.getName().length()).isGreaterThan(50);
    }

    @Test
    @DisplayName("특수문자가 포함된 보호소 이름으로 DTO 생성")
    void from_SpecialCharacters() {
        // Given
        String specialName = "서울시(강남구)동물보호센터&펫샵";
        Shelter specialShelter = Shelter.builder()
                .name(specialName)
                .detailAddress("특수문자 테스트 주소")
                .shelterContact("02-1234-5678")
                .location(location)
                .build();
        ReflectionTestUtils.setField(specialShelter, "shelterId", 888L);

        // When
        ShelterSearchResponseDto result = ShelterSearchResponseDto.from(specialShelter);

        // Then
        assertThat(result.getShelterId()).isEqualTo(888L);
        assertThat(result.getName()).isEqualTo(specialName);
    }

    @Test
    @DisplayName("숫자가 포함된 보호소 이름으로 DTO 생성")
    void from_WithNumbers() {
        // Given
        String nameWithNumbers = "서울시 제1동물보호센터 2024";
        Shelter numberShelter = Shelter.builder()
                .name(nameWithNumbers)
                .detailAddress("숫자포함 테스트 주소")
                .shelterContact("02-1234-5678")
                .location(location)
                .build();
        ReflectionTestUtils.setField(numberShelter, "shelterId", 777L);

        // When
        ShelterSearchResponseDto result = ShelterSearchResponseDto.from(numberShelter);

        // Then
        assertThat(result.getShelterId()).isEqualTo(777L);
        assertThat(result.getName()).isEqualTo(nameWithNumbers);
    }

    @Test
    @DisplayName("Getter 메서드 테스트")
    void getterMethods_Success() {
        // Given
        ShelterSearchResponseDto dto = ShelterSearchResponseDto.builder()
                .shelterId(123L)
                .name("게터테스트보호소")
                .build();

        // When & Then
        assertThat(dto.getShelterId()).isEqualTo(123L);
        assertThat(dto.getName()).isEqualTo("게터테스트보호소");
    }

    @Test
    @DisplayName("다양한 ID 값으로 DTO 생성")
    void from_VariousIds() {
        // Given
        Long[] shelterIds = {1L, 100L, 999L, 12345L, Long.MAX_VALUE};

        for (Long shelterId : shelterIds) {
            Shelter testShelter = Shelter.builder()
                    .name("ID테스트보호소")
                    .detailAddress("ID테스트 주소")
                    .shelterContact("02-1234-5678")
                    .location(location)
                    .build();
            ReflectionTestUtils.setField(testShelter, "shelterId", shelterId);

            // When
            ShelterSearchResponseDto result = ShelterSearchResponseDto.from(testShelter);

            // Then
            assertThat(result.getShelterId()).isEqualTo(shelterId);
            assertThat(result.getName()).isEqualTo("ID테스트보호소");
        }
    }

    @Test
    @DisplayName("Builder와 from 메서드 결과 일치성 확인")
    void builderAndFrom_Consistency() {
        // Given
        Long testId = 456L;
        String testName = "일치성테스트보호소";

        // When
        ShelterSearchResponseDto fromBuilder = ShelterSearchResponseDto.builder()
                .shelterId(testId)
                .name(testName)
                .build();

        Shelter testShelter = Shelter.builder()
                .name(testName)
                .detailAddress("일치성 테스트 주소")
                .shelterContact("02-1234-5678")
                .location(location)
                .build();
        ReflectionTestUtils.setField(testShelter, "shelterId", testId);

        ShelterSearchResponseDto fromEntity = ShelterSearchResponseDto.from(testShelter);

        // Then
        assertThat(fromBuilder.getShelterId()).isEqualTo(fromEntity.getShelterId());
        assertThat(fromBuilder.getName()).isEqualTo(fromEntity.getName());
    }

    @Test
    @DisplayName("필수 필드 확인")
    void requiredFields_Check() {
        // When
        ShelterSearchResponseDto result = ShelterSearchResponseDto.from(shelter);

        // Then - 필수 필드들이 모두 존재하는지 확인
        assertThat(result.getShelterId()).isNotNull();
        assertThat(result.getName()).isNotNull();
    }

    @Test
    @DisplayName("빈 문자열 이름으로 DTO 생성")
    void from_EmptyName() {
        // Given
        Shelter emptyShelter = Shelter.builder()
                .name("")
                .detailAddress("빈문자열 테스트 주소")
                .shelterContact("02-1234-5678")
                .location(location)
                .build();
        ReflectionTestUtils.setField(emptyShelter, "shelterId", 111L);

        // When
        ShelterSearchResponseDto result = ShelterSearchResponseDto.from(emptyShelter);

        // Then
        assertThat(result.getShelterId()).isEqualTo(111L);
        assertThat(result.getName()).isEqualTo("");
    }

    @Test
    @DisplayName("DTO 객체 동등성 확인")
    void dto_Equality() {
        // Given
        ShelterSearchResponseDto dto1 = ShelterSearchResponseDto.builder()
                .shelterId(1L)
                .name("동등성테스트보호소")
                .build();

        ShelterSearchResponseDto dto2 = ShelterSearchResponseDto.builder()
                .shelterId(1L)
                .name("동등성테스트보호소")
                .build();

        ShelterSearchResponseDto dto3 = ShelterSearchResponseDto.builder()
                .shelterId(2L)
                .name("다른보호소")
                .build();

        // When & Then
        assertThat(dto1.getShelterId()).isEqualTo(dto2.getShelterId());
        assertThat(dto1.getName()).isEqualTo(dto2.getName());
        assertThat(dto1.getShelterId()).isNotEqualTo(dto3.getShelterId());
        assertThat(dto1.getName()).isNotEqualTo(dto3.getName());
    }
}