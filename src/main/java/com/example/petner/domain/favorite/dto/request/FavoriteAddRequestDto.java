package com.example.petner.domain.favorite.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 즐겨찾기 추가 요청 DTO
 *
 * 클라이언트에서 서버로 즐겨찾기 추가 요청 시 사용하는 데이터 전송 객체입니다.
 * 현재 로그인한 사용자의 정보는 세션에서 자동으로 가져오므로 강아지 ID만 필요합니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteAddRequestDto {

    /**
     * 즐겨찾기에 추가할 강아지 ID
     */
    private Long dogId;

    public FavoriteAddRequestDto(Long dogId) {
        this.dogId = dogId;
    }
}