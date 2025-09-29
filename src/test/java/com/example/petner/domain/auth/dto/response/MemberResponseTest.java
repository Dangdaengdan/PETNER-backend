package com.example.petner.domain.auth.dto.response;

import com.example.petner.domain.location.entity.Location;
import com.example.petner.domain.member.common.HousingType;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.global.config.common.Gender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class MemberResponseTest {

    @Test
    @DisplayName("Member -> MemberResponse 변환 - Location 정보 포함")
    void from_WithLocation() {
        // Given
        Location location = Location.builder()
                .state("서울시")
                .district("강남구")
                .build();
        ReflectionTestUtils.setField(location, "locationId", 1L);

        Member member = Member.builder()
                .kakaoId("12345")
                .email("test@example.com")
                .nickname("테스트유저")
                .gender(Gender.MALE)
                .housingType(HousingType.아파트)
                .contact("010-1234-5678")
                .location(location)
                .build();
        ReflectionTestUtils.setField(member, "memberId", 1L);

        // When
        MemberResponse response = MemberResponse.from(member);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getMemberId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getNickname()).isEqualTo("테스트유저");
        assertThat(response.getGender()).isEqualTo(Gender.MALE);
        assertThat(response.getHousingType()).isEqualTo(HousingType.아파트);
        assertThat(response.getContact()).isEqualTo("010-1234-5678");
        assertThat(response.getLocationId()).isEqualTo(1L);
        assertThat(response.getState()).isEqualTo("서울시");
        assertThat(response.getDistrict()).isEqualTo("강남구");
        assertThat(response.getLocationName()).isEqualTo("서울시 강남구");
        assertThat(response.isProfileCompleted()).isTrue(); // 모든 필드가 있으므로 true
    }

    @Test
    @DisplayName("Member -> MemberResponse 변환 - Location 정보 없음")
    void from_WithoutLocation() {
        // Given
        Member member = Member.builder()
                .kakaoId("12345")
                .email("test@example.com")
                .nickname("테스트유저")
                .build();
        ReflectionTestUtils.setField(member, "memberId", 1L);

        // When
        MemberResponse response = MemberResponse.from(member);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getMemberId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getNickname()).isEqualTo("테스트유저");
        assertThat(response.getLocationId()).isNull();
        assertThat(response.getState()).isNull();
        assertThat(response.getDistrict()).isNull();
        assertThat(response.getLocationName()).isNull();
        assertThat(response.isProfileCompleted()).isFalse();
    }

    @Test
    @DisplayName("Member -> MemberResponse 변환 - Location의 일부 정보만 있음")
    void from_WithPartialLocation() {
        // Given
        Location location = Location.builder()
                .state("서울시")
                // district는 null
                .build();
        ReflectionTestUtils.setField(location, "locationId", 1L);

        Member member = Member.builder()
                .kakaoId("12345")
                .email("test@example.com")
                .nickname("테스트유저")
                .location(location)
                .build();
        ReflectionTestUtils.setField(member, "memberId", 1L);

        // When
        MemberResponse response = MemberResponse.from(member);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getLocationId()).isEqualTo(1L);
        assertThat(response.getState()).isEqualTo("서울시");
        assertThat(response.getDistrict()).isNull();
        assertThat(response.getLocationName()).isNull(); // state와 district 모두 있어야 조합됨
    }

    @Test
    @DisplayName("로그인용 MemberResponse 생성")
    void forLogin() {
        // Given
        Member member = Member.builder()
                .kakaoId("12345")
                .email("test@example.com")
                .nickname("테스트유저")
                .build();
        ReflectionTestUtils.setField(member, "memberId", 1L);

        // When
        MemberResponse response = MemberResponse.forLogin(member);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getMemberId()).isEqualTo(1L);
        assertThat(response.isProfileCompleted()).isFalse(); // 기본값
        // forLogin()은 최소 정보만 포함
        assertThat(response.getEmail()).isNull();
        assertThat(response.getNickname()).isNull();
        assertThat(response.getLocationName()).isNull();
    }
}