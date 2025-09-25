package com.example.petner.domain.favorite.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 즐겨찾기 액션 응답 DTO
 *
 * 즐겨찾기 삭제 등의 액션 성공 시 사용하는 응답 DTO입니다.
 * 처리 결과와 메시지를 포함합니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteActionResponseDto {

    /**
     * 처리된 멤버 ID
     */
    private Long memberId;

    /**
     * 처리된 강아지 ID
     */
    private Long dogId;

    /**
     * 처리 결과 메시지
     */
    private String message;

    public FavoriteActionResponseDto(Long memberId, Long dogId, String message) {
        this.memberId = memberId;
        this.dogId = dogId;
        this.message = message;
    }
}