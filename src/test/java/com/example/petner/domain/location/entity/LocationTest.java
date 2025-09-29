package com.example.petner.domain.location.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class LocationTest {

    private Location location;

    @BeforeEach
    void setUp() {
        location = Location.builder()
                .state("서울시")
                .district("강남구")
                .build();
        ReflectionTestUtils.setField(location, "locationId", 1L);
    }

    @Test
    @DisplayName("Location 엔티티 Builder 패턴으로 정상 생성")
    void createLocation_Success() {
        // Then
        assertThat(location.getLocationId()).isEqualTo(1L);
        assertThat(location.getState()).isEqualTo("서울시");
        assertThat(location.getDistrict()).isEqualTo("강남구");
    }

    @Test
    @DisplayName("Location 엔티티 필수 필드 확인")
    void location_RequiredFields() {
        // Then - 필수 필드들이 모두 존재하는지 확인
        assertThat(location.getState()).isNotNull();
        assertThat(location.getDistrict()).isNotNull();
    }

    @Test
    @DisplayName("Location 엔티티 Getter 메서드 테스트")
    void location_GetterMethods() {
        // Then
        assertThat(location.getLocationId()).isNotNull();
        assertThat(location.getState()).isNotNull();
        assertThat(location.getDistrict()).isNotNull();
    }

    @Test
    @DisplayName("다양한 시/도 이름 테스트")
    void location_VariousStates() {
        // Given
        String[] states = {
                "서울시", "부산시", "대구시", "인천시", "광주시",
                "대전시", "울산시", "세종시", "경기도", "강원도",
                "충청북도", "충청남도", "전라북도", "전라남도",
                "경상북도", "경상남도", "제주도"
        };

        for (String state : states) {
            // When
            Location testLocation = Location.builder()
                    .state(state)
                    .district("테스트구")
                    .build();

            // Then
            assertThat(testLocation.getState()).isEqualTo(state);
            assertThat(testLocation.getDistrict()).isEqualTo("테스트구");
        }
    }

    @Test
    @DisplayName("다양한 시/군/구 이름 테스트")
    void location_VariousDistricts() {
        // Given
        String[] districts = {
                "강남구", "강북구", "강서구", "강동구",
                "서초구", "송파구", "영등포구", "마포구",
                "용산구", "성동구", "광진구", "동대문구",
                "중랑구", "성북구", "도봉구", "노원구"
        };

        for (String district : districts) {
            // When
            Location testLocation = Location.builder()
                    .state("서울시")
                    .district(district)
                    .build();

            // Then
            assertThat(testLocation.getState()).isEqualTo("서울시");
            assertThat(testLocation.getDistrict()).isEqualTo(district);
        }
    }

    @Test
    @DisplayName("실제 시/도와 시/군/구 조합 테스트")
    void location_RealCombinations() {
        // Given
        String[][] combinations = {
                {"서울시", "강남구"},
                {"부산시", "해운대구"},
                {"대구시", "중구"},
                {"인천시", "연수구"},
                {"광주시", "남구"},
                {"대전시", "서구"},
                {"울산시", "남구"},
                {"경기도", "수원시"},
                {"강원도", "춘천시"},
                {"제주도", "제주시"}
        };

        for (String[] combination : combinations) {
            // When
            Location testLocation = Location.builder()
                    .state(combination[0])
                    .district(combination[1])
                    .build();

            // Then
            assertThat(testLocation.getState()).isEqualTo(combination[0]);
            assertThat(testLocation.getDistrict()).isEqualTo(combination[1]);
        }
    }

    @Test
    @DisplayName("Location 엔티티 최소 필드로 생성")
    void location_MinimalFields() {
        // Given & When
        Location minimalLocation = Location.builder()
                .state("최소시")
                .district("최소구")
                .build();

        // Then
        assertThat(minimalLocation.getState()).isEqualTo("최소시");
        assertThat(minimalLocation.getDistrict()).isEqualTo("최소구");
    }

    @Test
    @DisplayName("특수문자가 포함된 지역명 테스트")
    void location_SpecialCharacters() {
        // Given
        String specialState = "특별시(도)";
        String specialDistrict = "특별구-1";

        // When
        Location specialLocation = Location.builder()
                .state(specialState)
                .district(specialDistrict)
                .build();

        // Then
        assertThat(specialLocation.getState()).isEqualTo(specialState);
        assertThat(specialLocation.getDistrict()).isEqualTo(specialDistrict);
    }

    @Test
    @DisplayName("긴 지역명 테스트")
    void location_LongNames() {
        // Given
        String longState = "매우긴시도이름입니다".repeat(3);
        String longDistrict = "매우긴구군이름입니다".repeat(3);

        // When
        Location longLocation = Location.builder()
                .state(longState)
                .district(longDistrict)
                .build();

        // Then
        assertThat(longLocation.getState()).isEqualTo(longState);
        assertThat(longLocation.getDistrict()).isEqualTo(longDistrict);
        assertThat(longLocation.getState().length()).isGreaterThan(20);
        assertThat(longLocation.getDistrict().length()).isGreaterThan(20);
    }

    @Test
    @DisplayName("빈 문자열 지역명 테스트")
    void location_EmptyStrings() {
        // Given & When
        Location emptyLocation = Location.builder()
                .state("")
                .district("")
                .build();

        // Then
        assertThat(emptyLocation.getState()).isEqualTo("");
        assertThat(emptyLocation.getDistrict()).isEqualTo("");
    }

    @Test
    @DisplayName("공백 문자열 지역명 테스트")
    void location_BlankStrings() {
        // Given & When
        Location blankLocation = Location.builder()
                .state("   ")
                .district("   ")
                .build();

        // Then
        assertThat(blankLocation.getState()).isEqualTo("   ");
        assertThat(blankLocation.getDistrict()).isEqualTo("   ");
    }

    @Test
    @DisplayName("다양한 ID 값 테스트")
    void location_VariousIds() {
        // Given
        Long[] locationIds = {1L, 100L, 999L, 12345L, Long.MAX_VALUE};

        for (Long locationId : locationIds) {
            Location testLocation = Location.builder()
                    .state("ID테스트시")
                    .district("ID테스트구")
                    .build();
            ReflectionTestUtils.setField(testLocation, "locationId", locationId);

            // When & Then
            assertThat(testLocation.getLocationId()).isEqualTo(locationId);
            assertThat(testLocation.getState()).isEqualTo("ID테스트시");
            assertThat(testLocation.getDistrict()).isEqualTo("ID테스트구");
        }
    }
}