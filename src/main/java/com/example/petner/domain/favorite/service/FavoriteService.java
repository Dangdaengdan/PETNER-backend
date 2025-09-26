package com.example.petner.domain.favorite.service;

import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.domain.favorite.dto.request.FavoriteAddRequestDto;
import com.example.petner.domain.favorite.dto.response.FavoriteResponseDto;
import com.example.petner.domain.favorite.entity.Favorite;
import com.example.petner.domain.favorite.repository.FavoriteRepository;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.global.dto.SessionUser;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.FavoriteException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 즐겨찾기 관리 서비스
 *
 * Single Responsibility Principle을 적용하여 즐겨찾기 추가/삭제 책임을 담당
 * 세션 사용자 정보를 기반으로 즐겨찾기를 관리합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final FavoriteValidator favoriteValidator;
    private final FavoriteDuplicateChecker duplicateChecker;

    /**
     * 즐겨찾기 추가
     *
     * @param requestDto 즐겨찾기 추가 요청 데이터
     * @param user 세션에서 주입된 현재 로그인 사용자 정보
     * @return 추가된 즐겨찾기 정보
     *
     * 처리 과정:
     * 1. 세션 사용자 검증
     * 2. 강아지 존재 여부 검증
     * 3. 중복 즐겨찾기 확인
     * 4. 즐겨찾기 생성 및 저장
     */
    @Transactional
    public FavoriteResponseDto addFavorite(FavoriteAddRequestDto requestDto, SessionUser user) {
        // 1. 세션 사용자 검증
        Member member = favoriteValidator.validateAndGetMember(user.getMemberId());

        // 2. 강아지 존재 여부 검증
        Dog dog = favoriteValidator.validateAndGetDog(requestDto.getDogId());

        // 3. 중복 즐겨찾기 확인
        duplicateChecker.checkDuplicate(member.getMemberId(), dog.getDogId());

        // 4. 즐겨찾기 생성 및 저장
        Favorite favorite = Favorite.builder()
                .member(member)
                .dog(dog)
                .build();

        Favorite savedFavorite = favoriteRepository.save(favorite);

        // 5. 응답 DTO로 변환하여 반환
        return new FavoriteResponseDto(savedFavorite);
    }

    /**
     * 즐겨찾기 제거
     *
     * @param dogId 제거할 강아지 ID
     * @param user 세션에서 주입된 현재 로그인 사용자 정보
     *
     * 처리 과정:
     * 1. 세션 사용자 검증
     * 2. 강아지 존재 여부 검증
     * 3. 즐겨찾기 존재 여부 확인
     * 4. 즐겨찾기 삭제
     */
    @Transactional
    public void removeFavorite(Long dogId, SessionUser user) {
        // 1. 세션 사용자 검증
        Member member = favoriteValidator.validateAndGetMember(user.getMemberId());

        // 2. 강아지 존재 여부 검증
        Dog dog = favoriteValidator.validateAndGetDog(dogId);

        // 3. 즐겨찾기 존재 여부 확인
        if (!duplicateChecker.exists(member.getMemberId(), dog.getDogId())) {
            throw new FavoriteException(ErrorCode.FAVORITE_NOT_IN_MY_LIST);
        }

        // 4. 즐겨찾기 삭제
        favoriteRepository.deleteByMemberMemberIdAndDogDogId(member.getMemberId(), dog.getDogId());
    }
}