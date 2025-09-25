package com.example.petner.domain.favorite.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 즐겨찾기 여부 확인 응답 DTO
 *
 * 특정 강아지가 즐겨찾기에 추가되어 있는지 여부를 반환하는 DTO입니다.
 * boolean 값 대신 구조화된 응답을 제공합니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteCheckResponseDto {

    /**
     * 확인한 멤버 ID
     */
    private Long memberId;

    /**
     * 확인한 강아지 ID
     */
    private Long dogId;

    /**
     * 즐겨찾기 여부
     */
    private boolean isFavorite;

    public FavoriteCheckResponseDto(Long memberId, Long dogId, boolean isFavorite) {
        this.memberId = memberId;
        this.dogId = dogId;
        this.isFavorite = isFavorite;
    }
}