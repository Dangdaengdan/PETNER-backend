package com.example.petner.domain.favorite.dto.response;

import com.example.petner.domain.favorite.entity.Favorite;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 즐겨찾기 응답 DTO
 *
 * 서버에서 클라이언트로 즐겨찾기 정보를 전달할 때 사용하는 데이터 전송 객체입니다.
 * 즐겨찾기 추가/제거 성공 시 응답으로 사용됩니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteResponseDto {

    /**
     * 즐겨찾기 고유 ID
     */
    private Long favoriteId;

    /**
     * 멤버 ID
     */
    private Long memberId;

    /**
     * 강아지 ID
     */
    private Long dogId;

    /**
     * 즐겨찾기 추가 일시
     */
    private LocalDateTime createdAt;

    /**
     * Favorite 엔티티로부터 DTO를 생성하는 생성자
     *
     * @param favorite 변환할 Favorite 엔티티
     */
    public FavoriteResponseDto(Favorite favorite) {
        this.favoriteId = favorite.getFavoriteId();
        this.memberId = favorite.getMember().getMemberId();
        this.dogId = favorite.getDog().getDogId();
        this.createdAt = favorite.getCreatedAt();
    }
}