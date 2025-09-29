package com.example.petner.domain.member.dto.response;

import com.example.petner.domain.location.entity.Location;
import com.example.petner.domain.member.common.HousingType;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.global.config.common.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProfileResponseDtoTest {

    private Member member;
    private Location location;

    @BeforeEach
    void setUp() {
        location = Location.builder()
                .state("서울특별시")
                .district("강남구")
                .build();
        // Reflection을 이용하여 locationId 설정 (테스트용)
        try {
            java.lang.reflect.Field field = Location.class.getDeclaredField("locationId");
            field.setAccessible(true);
            field.set(location, 1L);
        } catch (Exception e) {
            // 테스트에서는 무시
        }

        member = Member.builder()
                .kakaoId("test_kakao_id")
                .email("test@example.com")
                .nickname("테스트유저")
                .gender(Gender.MALE)
                .housingType(HousingType.아파트)
                .contact("010-1234-5678")
                .location(location)
                .build();
        // Reflection을 이용하여 memberId 설정 (테스트용)
        try {
            java.lang.reflect.Field field = Member.class.getDeclaredField("memberId");
            field.setAccessible(true);
            field.set(member, 1L);
        } catch (Exception e) {
            // 테스트에서는 무시
        }
    }

    @Test
    @DisplayName("Member 엔티티로부터 ProfileResponseDto 생성 성공")
    void from_Success() {
        // When
        ProfileResponseDto responseDto = ProfileResponseDto.from(member);

        // Then
        assertEquals(member.getMemberId(), responseDto.getMemberId());
        assertEquals(member.getEmail(), responseDto.getEmail());
        assertEquals(member.getNickname(), responseDto.getNickname());
        assertEquals(member.getGender(), responseDto.getGender());
        assertEquals(member.getHousingType(), responseDto.getHousingType());
        assertEquals(member.getContact(), responseDto.getContact());
        assertEquals(location.getLocationId(), responseDto.getLocationId());
        assertEquals(location.getState(), responseDto.getState());
        assertEquals(location.getDistrict(), responseDto.getDistrict());
        assertEquals("서울특별시 강남구", responseDto.getLocationName());
        assertTrue(responseDto.isProfileCompleted());
        assertEquals(member.getCreatedAt(), responseDto.getCreatedAt());
    }

    @Test
    @DisplayName("Location이 null인 Member로부터 ProfileResponseDto 생성")
    void from_NullLocation() {
        // Given
        Member memberWithoutLocation = Member.builder()
                .kakaoId("test_kakao_id")
                .email("test@example.com")
                .nickname("테스트유저")
                .gender(Gender.MALE)
                .housingType(HousingType.아파트)
                .contact("010-1234-5678")
                .location(null)
                .build();
        // Reflection을 이용하여 memberId 설정 (테스트용)
        try {
            java.lang.reflect.Field field = Member.class.getDeclaredField("memberId");
            field.setAccessible(true);
            field.set(memberWithoutLocation, 1L);
        } catch (Exception e) {
            // 테스트에서는 무시
        }

        // When
        ProfileResponseDto responseDto = ProfileResponseDto.from(memberWithoutLocation);

        // Then
        assertEquals(memberWithoutLocation.getMemberId(), responseDto.getMemberId());
        assertEquals(memberWithoutLocation.getEmail(), responseDto.getEmail());
        assertEquals(memberWithoutLocation.getNickname(), responseDto.getNickname());
        assertEquals(memberWithoutLocation.getGender(), responseDto.getGender());
        assertEquals(memberWithoutLocation.getHousingType(), responseDto.getHousingType());
        assertEquals(memberWithoutLocation.getContact(), responseDto.getContact());
        assertNull(responseDto.getLocationId());
        assertNull(responseDto.getState());
        assertNull(responseDto.getDistrict());
        assertNull(responseDto.getLocationName());
        assertFalse(responseDto.isProfileCompleted());
        assertEquals(memberWithoutLocation.getCreatedAt(), responseDto.getCreatedAt());
    }

    @Test
    @DisplayName("Location의 state나 district가 null인 경우")
    void from_PartialLocation() {
        // Given
        Location partialLocation = Location.builder()
                .state("서울특별시")
                .district(null)
                .build();
        // Reflection을 이용하여 locationId 설정 (테스트용)
        try {
            java.lang.reflect.Field field = Location.class.getDeclaredField("locationId");
            field.setAccessible(true);
            field.set(partialLocation, 1L);
        } catch (Exception e) {
            // 테스트에서는 무시
        }

        Member memberWithPartialLocation = Member.builder()
                .kakaoId("test_kakao_id")
                .email("test@example.com")
                .nickname("테스트유저")
                .gender(Gender.MALE)
                .housingType(HousingType.아파트)
                .contact("010-1234-5678")
                .location(partialLocation)
                .build();
        // Reflection을 이용하여 memberId 설정 (테스트용)
        try {
            java.lang.reflect.Field field = Member.class.getDeclaredField("memberId");
            field.setAccessible(true);
            field.set(memberWithPartialLocation, 1L);
        } catch (Exception e) {
            // 테스트에서는 무시
        }

        // When
        ProfileResponseDto responseDto = ProfileResponseDto.from(memberWithPartialLocation);

        // Then
        assertEquals(partialLocation.getLocationId(), responseDto.getLocationId());
        assertEquals(partialLocation.getState(), responseDto.getState());
        assertNull(responseDto.getDistrict());
        assertNull(responseDto.getLocationName()); // state와 district 중 하나라도 null이면 locationName도 null
    }

    @Test
    @DisplayName("임시 회원(프로필 미완성)으로부터 ProfileResponseDto 생성")
    void from_TemporaryMember() {
        // Given
        Member temporaryMember = Member.builder()
                .kakaoId("temp_kakao_id")
                .build();
        // Reflection을 이용하여 memberId 설정 (테스트용)
        try {
            java.lang.reflect.Field field = Member.class.getDeclaredField("memberId");
            field.setAccessible(true);
            field.set(temporaryMember, 2L);
        } catch (Exception e) {
            // 테스트에서는 무시
        }

        // When
        ProfileResponseDto responseDto = ProfileResponseDto.from(temporaryMember);

        // Then
        assertEquals(temporaryMember.getMemberId(), responseDto.getMemberId());
        assertNull(responseDto.getEmail());
        assertNull(responseDto.getNickname());
        assertNull(responseDto.getGender());
        assertNull(responseDto.getHousingType());
        assertNull(responseDto.getContact());
        assertNull(responseDto.getLocationId());
        assertNull(responseDto.getState());
        assertNull(responseDto.getDistrict());
        assertNull(responseDto.getLocationName());
        assertFalse(responseDto.isProfileCompleted());
        assertEquals(temporaryMember.getCreatedAt(), responseDto.getCreatedAt());
    }

    @Test
    @DisplayName("빌더 패턴으로 ProfileResponseDto 생성")
    void builder_Success() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        ProfileResponseDto responseDto = ProfileResponseDto.builder()
                .memberId(1L)
                .email("test@example.com")
                .nickname("테스트유저")
                .gender(Gender.MALE)
                .housingType(HousingType.아파트)
                .contact("010-1234-5678")
                .locationId(1L)
                .state("서울특별시")
                .district("강남구")
                .locationName("서울특별시 강남구")
                .profileCompleted(true)
                .createdAt(now)
                .build();

        // Then
        assertEquals(1L, responseDto.getMemberId());
        assertEquals("test@example.com", responseDto.getEmail());
        assertEquals("테스트유저", responseDto.getNickname());
        assertEquals(Gender.MALE, responseDto.getGender());
        assertEquals(HousingType.아파트, responseDto.getHousingType());
        assertEquals("010-1234-5678", responseDto.getContact());
        assertEquals(1L, responseDto.getLocationId());
        assertEquals("서울특별시", responseDto.getState());
        assertEquals("강남구", responseDto.getDistrict());
        assertEquals("서울특별시 강남구", responseDto.getLocationName());
        assertTrue(responseDto.isProfileCompleted());
        assertEquals(now, responseDto.getCreatedAt());
    }

    @Test
    @DisplayName("Getter 메소드 테스트")
    void getter_Test() {
        // Given
        ProfileResponseDto responseDto = ProfileResponseDto.from(member);

        // When & Then
        assertNotNull(responseDto.getMemberId());
        assertNotNull(responseDto.getEmail());
        assertNotNull(responseDto.getNickname());
        assertNotNull(responseDto.getGender());
        assertNotNull(responseDto.getHousingType());
        assertNotNull(responseDto.getContact());
        assertNotNull(responseDto.getLocationId());
        assertNotNull(responseDto.getState());
        assertNotNull(responseDto.getDistrict());
        assertNotNull(responseDto.getLocationName());
        assertTrue(responseDto.isProfileCompleted());
    }
}