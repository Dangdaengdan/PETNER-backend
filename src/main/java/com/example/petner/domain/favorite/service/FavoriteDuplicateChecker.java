package com.example.petner.domain.favorite.service;

import com.example.petner.domain.favorite.repository.FavoriteRepository;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.FavoriteException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 즐겨찾기 중복 검사 서비스
 *
 * Single Responsibility Principle을 적용하여 중복 검사 책임만 담당
 * 즐겨찾기 추가 시 중복 여부를 확인합니다.
 */
@Service
@RequiredArgsConstructor
public class FavoriteDuplicateChecker {

    private final FavoriteRepository favoriteRepository;

    /**
     * 즐겨찾기 중복 여부 확인
     *
     * @param memberId 멤버 ID
     * @param dogId 강아지 ID
     * @throws FavoriteException 이미 즐겨찾기에 추가된 경우
     */
    public void checkDuplicate(Long memberId, Long dogId) {
        if (favoriteRepository.existsByMemberMemberIdAndDogDogId(memberId, dogId)) {
            throw new FavoriteException(ErrorCode.FAVORITE_ALREADY_EXISTS);
        }
    }

    /**
     * 즐겨찾기 존재 여부 확인 (예외 던지지 않음)
     *
     * @param memberId 멤버 ID
     * @param dogId 강아지 ID
     * @return 즐겨찾기 존재 여부
     */
    public boolean exists(Long memberId, Long dogId) {
        return favoriteRepository.existsByMemberMemberIdAndDogDogId(memberId, dogId);
    }
}