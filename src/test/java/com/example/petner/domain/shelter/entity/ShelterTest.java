package com.example.petner.domain.shelter.entity;

import com.example.petner.domain.location.entity.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ShelterTest {

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
        ReflectionTestUtils.setField(shelter, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(shelter, "updatedAt", LocalDateTime.now());
    }

    @Test
    @DisplayName("Shelter 엔티티 Builder 패턴으로 정상 생성")
    void createShelter_Success() {
        // Then
        assertThat(shelter.getShelterId()).isEqualTo(1L);
        assertThat(shelter.getName()).isEqualTo("서울시 강남구 동물보호센터");
        assertThat(shelter.getDetailAddress()).isEqualTo("서울시 강남구 테헤란로 123");
        assertThat(shelter.getShelterContact()).isEqualTo("02-1234-5678");
        assertThat(shelter.getLocation()).isEqualTo(location);
        assertThat(shelter.getCreatedAt()).isNotNull();
        assertThat(shelter.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Shelter 엔티티 필수 필드 확인")
    void shelter_RequiredFields() {
        // Then - 필수 필드들이 모두 존재하는지 확인
        assertThat(shelter.getName()).isNotNull();
        assertThat(shelter.getDetailAddress()).isNotNull();
        assertThat(shelter.getShelterContact()).isNotNull();
        assertThat(shelter.getLocation()).isNotNull();
    }

    @Test
    @DisplayName("Shelter와 Location 연관관계 확인")
    void shelter_LocationRelationship() {
        // Then
        assertThat(shelter.getLocation()).isEqualTo(location);
        assertThat(shelter.getLocation().getState()).isEqualTo("서울시");
        assertThat(shelter.getLocation().getDistrict()).isEqualTo("강남구");
    }

    @Test
    @DisplayName("Shelter 엔티티 Getter 메서드 테스트")
    void shelter_GetterMethods() {
        // Then
        assertThat(shelter.getShelterId()).isNotNull();
        assertThat(shelter.getName()).isNotNull();
        assertThat(shelter.getDetailAddress()).isNotNull();
        assertThat(shelter.getShelterContact()).isNotNull();
        assertThat(shelter.getLocation()).isNotNull();
        assertThat(shelter.getCreatedAt()).isNotNull();
        assertThat(shelter.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("다양한 보호소 이름 테스트")
    void shelter_VariousNames() {
        // Given
        String[] shelterNames = {
                "부산시 동물보호센터",
                "대구시 해외동물보호센터",
                "인천시 서구 동물보호소",
                "광주시 남구 반려동물센터"
        };

        for (String shelterName : shelterNames) {
            // When
            Shelter testShelter = Shelter.builder()
                    .name(shelterName)
                    .detailAddress("주소")
                    .shelterContact("010-1234-5678")
                    .location(location)
                    .build();

            // Then
            assertThat(testShelter.getName()).isEqualTo(shelterName);
            assertThat(testShelter.getLocation()).isEqualTo(location);
        }
    }

    @Test
    @DisplayName("다양한 연락처 형식 테스트")
    void shelter_VariousContactFormats() {
        // Given
        String[] contacts = {
                "02-1234-5678",
                "051-123-4567",
                "032-987-6543",
                "053-555-1234"
        };

        for (String contact : contacts) {
            // When
            Shelter testShelter = Shelter.builder()
                    .name("테스트 보호소")
                    .detailAddress("테스트 주소")
                    .shelterContact(contact)
                    .location(location)
                    .build();

            // Then
            assertThat(testShelter.getShelterContact()).isEqualTo(contact);
        }
    }

    @Test
    @DisplayName("다양한 상세주소 테스트")
    void shelter_VariousDetailAddresses() {
        // Given
        String[] addresses = {
                "서울시 강남구 테헤란로 123",
                "부산시 해운대구 센텀중앙로 456",
                "대구시 중구 동성로 789",
                "인천시 연수구 송도동 101-102"
        };

        for (String address : addresses) {
            // When
            Shelter testShelter = Shelter.builder()
                    .name("테스트 보호소")
                    .detailAddress(address)
                    .shelterContact("02-1234-5678")
                    .location(location)
                    .build();

            // Then
            assertThat(testShelter.getDetailAddress()).isEqualTo(address);
        }
    }

    @Test
    @DisplayName("다양한 Location과의 연관관계 테스트")
    void shelter_VariousLocations() {
        // Given
        Location seoul = Location.builder().state("서울시").district("강남구").build();
        Location busan = Location.builder().state("부산시").district("해운대구").build();
        Location daegu = Location.builder().state("대구시").district("중구").build();

        Location[] locations = {seoul, busan, daegu};
        String[] expectedStates = {"서울시", "부산시", "대구시"};
        String[] expectedDistricts = {"강남구", "해운대구", "중구"};

        for (int i = 0; i < locations.length; i++) {
            // When
            Shelter testShelter = Shelter.builder()
                    .name("테스트 보호소 " + (i + 1))
                    .detailAddress("테스트 주소 " + (i + 1))
                    .shelterContact("010-1234-567" + (i + 1))
                    .location(locations[i])
                    .build();

            // Then
            assertThat(testShelter.getLocation()).isEqualTo(locations[i]);
            assertThat(testShelter.getLocation().getState()).isEqualTo(expectedStates[i]);
            assertThat(testShelter.getLocation().getDistrict()).isEqualTo(expectedDistricts[i]);
        }
    }

    @Test
    @DisplayName("시간 필드 자동 설정 확인")
    void shelter_TimestampFields() {
        // Given
        LocalDateTime testTime = LocalDateTime.of(2024, 1, 1, 12, 0, 0);
        Shelter newShelter = Shelter.builder()
                .name("시간테스트 보호소")
                .detailAddress("시간테스트 주소")
                .shelterContact("02-9999-8888")
                .location(location)
                .build();
        ReflectionTestUtils.setField(newShelter, "createdAt", testTime);
        ReflectionTestUtils.setField(newShelter, "updatedAt", testTime.plusHours(1));

        // When & Then
        assertThat(newShelter.getCreatedAt()).isEqualTo(testTime);
        assertThat(newShelter.getUpdatedAt()).isEqualTo(testTime.plusHours(1));
    }

    @Test
    @DisplayName("Shelter 엔티티 최소 필드로 생성")
    void shelter_MinimalFields() {
        // Given & When
        Shelter minimalShelter = Shelter.builder()
                .name("최소보호소")
                .detailAddress("최소주소")
                .shelterContact("02-0000-0000")
                .location(location)
                .build();

        // Then
        assertThat(minimalShelter.getName()).isEqualTo("최소보호소");
        assertThat(minimalShelter.getDetailAddress()).isEqualTo("최소주소");
        assertThat(minimalShelter.getShelterContact()).isEqualTo("02-0000-0000");
        assertThat(minimalShelter.getLocation()).isEqualTo(location);
    }
}