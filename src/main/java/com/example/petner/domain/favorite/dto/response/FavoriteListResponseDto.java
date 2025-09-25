package com.example.petner.domain.favorite.dto.response;

import com.example.petner.domain.favorite.entity.Favorite;
import com.example.petner.domain.dog.entity.Dog;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 즐겨찾기 목록 응답 DTO
 *
 * 서버에서 클라이언트로 즐겨찾기 목록을 전달할 때 사용하는 데이터 전송 객체입니다.
 * 강아지의 기본 정보와 즐겨찾기 정보를 함께 포함합니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteListResponseDto {

    /**
     * 즐겨찾기 고유 ID
     */
    private Long favoriteId;

    /**
     * 즐겨찾기 추가 일시
     */
    private LocalDateTime createdAt;

    /**
     * 강아지 정보
     */
    private DogInfo dogInfo;

    /**
     * Favorite 엔티티로부터 DTO를 생성하는 생성자
     *
     * @param favorite 변환할 Favorite 엔티티
     */
    public FavoriteListResponseDto(Favorite favorite) {
        this.favoriteId = favorite.getFavoriteId();
        this.createdAt = favorite.getCreatedAt();
        this.dogInfo = new DogInfo(favorite.getDog());
    }

    /**
     * 강아지 기본 정보를 담는 내부 클래스
     */
    @Getter
    public static class DogInfo {
        private final Long dogId;
        private final String name;
        private final String breedName;
        private final String gender;
        private final String dogSize;
        private final BigDecimal weight;
        private final String adoptionStatus;
        private final String imageUrl;
        private final String shelterName;

        public DogInfo(Dog dog) {
            this.dogId = dog.getDogId();
            this.name = dog.getName();
            this.breedName = dog.getBreed().getName();
            this.gender = dog.getGender().name();
            this.dogSize = dog.getDogSize().name();
            this.weight = dog.getWeight();
            this.adoptionStatus = dog.getAdoptionStatus().name();
            this.imageUrl = dog.getImageUrl();
            this.shelterName = dog.getShelter() != null ? dog.getShelter().getName() : null;
        }
    }
}