package com.example.petner.domain.member.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HousingTypeTest {

    @Test
    @DisplayName("모든 HousingType enum 값 확인")
    void allValues_Test() {
        // When
        HousingType[] values = HousingType.values();

        // Then
        assertEquals(4, values.length);
        assertEquals(HousingType.아파트, values[0]);
        assertEquals(HousingType.단독_주택, values[1]);
        assertEquals(HousingType.빌라, values[2]);
        assertEquals(HousingType.기타, values[3]);
    }

    @Test
    @DisplayName("아파트 HousingType 테스트")
    void 아파트_Test() {
        // When
        HousingType housingType = HousingType.아파트;

        // Then
        assertEquals("아파트", housingType.getDisplayName());
        assertEquals("아파트", housingType.name());
    }

    @Test
    @DisplayName("단독 주택 HousingType 테스트")
    void 단독주택_Test() {
        // When
        HousingType housingType = HousingType.단독_주택;

        // Then
        assertEquals("단독 주택", housingType.getDisplayName());
        assertEquals("단독_주택", housingType.name());
    }

    @Test
    @DisplayName("빌라 HousingType 테스트")
    void 빌라_Test() {
        // When
        HousingType housingType = HousingType.빌라;

        // Then
        assertEquals("빌라", housingType.getDisplayName());
        assertEquals("빌라", housingType.name());
    }

    @Test
    @DisplayName("기타 HousingType 테스트")
    void 기타_Test() {
        // When
        HousingType housingType = HousingType.기타;

        // Then
        assertEquals("기타", housingType.getDisplayName());
        assertEquals("기타", housingType.name());
    }

    @Test
    @DisplayName("valueOf로 HousingType 조회 테스트")
    void valueOf_Test() {
        // When & Then
        assertEquals(HousingType.아파트, HousingType.valueOf("아파트"));
        assertEquals(HousingType.단독_주택, HousingType.valueOf("단독_주택"));
        assertEquals(HousingType.빌라, HousingType.valueOf("빌라"));
        assertEquals(HousingType.기타, HousingType.valueOf("기타"));
    }

    @Test
    @DisplayName("존재하지 않는 값으로 valueOf 호출시 예외 발생")
    void valueOf_IllegalArgument() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            HousingType.valueOf("존재하지않는타입");
        });
    }

    @Test
    @DisplayName("HousingType enum 순서 테스트")
    void ordinal_Test() {
        // When & Then
        assertEquals(0, HousingType.아파트.ordinal());
        assertEquals(1, HousingType.단독_주택.ordinal());
        assertEquals(2, HousingType.빌라.ordinal());
        assertEquals(3, HousingType.기타.ordinal());
    }

    @Test
    @DisplayName("HousingType toString 테스트")
    void toString_Test() {
        // When & Then
        assertEquals("아파트", HousingType.아파트.toString());
        assertEquals("단독_주택", HousingType.단독_주택.toString());
        assertEquals("빌라", HousingType.빌라.toString());
        assertEquals("기타", HousingType.기타.toString());
    }

    @Test
    @DisplayName("HousingType 동등성 테스트")
    void equals_Test() {
        // When & Then
        assertEquals(HousingType.아파트, HousingType.아파트);
        assertEquals(HousingType.단독_주택, HousingType.단독_주택);
        assertEquals(HousingType.빌라, HousingType.빌라);
        assertEquals(HousingType.기타, HousingType.기타);

        assertNotEquals(HousingType.아파트, HousingType.빌라);
        assertNotEquals(HousingType.단독_주택, HousingType.기타);
    }

    @Test
    @DisplayName("HousingType hashCode 테스트")
    void hashCode_Test() {
        // When & Then
        assertEquals(HousingType.아파트.hashCode(), HousingType.아파트.hashCode());
        assertEquals(HousingType.단독_주택.hashCode(), HousingType.단독_주택.hashCode());
        assertEquals(HousingType.빌라.hashCode(), HousingType.빌라.hashCode());
        assertEquals(HousingType.기타.hashCode(), HousingType.기타.hashCode());

        assertNotEquals(HousingType.아파트.hashCode(), HousingType.빌라.hashCode());
    }

    @Test
    @DisplayName("displayName getter 메소드 테스트")
    void getDisplayName_Test() {
        // When & Then
        assertNotNull(HousingType.아파트.getDisplayName());
        assertNotNull(HousingType.단독_주택.getDisplayName());
        assertNotNull(HousingType.빌라.getDisplayName());
        assertNotNull(HousingType.기타.getDisplayName());

        assertTrue(HousingType.아파트.getDisplayName().length() > 0);
        assertTrue(HousingType.단독_주택.getDisplayName().length() > 0);
        assertTrue(HousingType.빌라.getDisplayName().length() > 0);
        assertTrue(HousingType.기타.getDisplayName().length() > 0);
    }

    @Test
    @DisplayName("모든 HousingType의 displayName이 고유한지 확인")
    void uniqueDisplayNames_Test() {
        // Given
        HousingType[] values = HousingType.values();

        // When & Then
        for (int i = 0; i < values.length; i++) {
            for (int j = i + 1; j < values.length; j++) {
                assertNotEquals(values[i].getDisplayName(), values[j].getDisplayName(),
                        "DisplayName should be unique: " + values[i] + " vs " + values[j]);
            }
        }
    }

    @Test
    @DisplayName("HousingType이 null이 아닌지 확인")
    void notNull_Test() {
        // When & Then
        for (HousingType housingType : HousingType.values()) {
            assertNotNull(housingType);
            assertNotNull(housingType.getDisplayName());
            assertNotNull(housingType.name());
        }
    }
}