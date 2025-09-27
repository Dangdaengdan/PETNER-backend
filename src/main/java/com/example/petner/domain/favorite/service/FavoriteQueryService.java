package com.example.petner.domain.favorite.service;

import com.example.petner.domain.favorite.dto.response.FavoriteListResponseDto;
import com.example.petner.domain.favorite.entity.Favorite;
import com.example.petner.domain.favorite.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 즐겨찾기 조회 전용 서비스
 *
 * Single Responsibility Principle을 적용하여 조회 책임만 담당
 * N+1 문제를 방지한 효율적인 즐겨찾기 목록 조회를 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteQueryService {

    private final FavoriteRepository favoriteRepository;

    /**
     * 특정 멤버의 즐겨찾기 목록 조회
     *
     * @param memberId 멤버 ID
     * @return 즐겨찾기 목록 (강아지 상세 정보 포함)
     */
    public List<FavoriteListResponseDto> getMemberFavorites(Long memberId) {
        List<Favorite> favorites = favoriteRepository.findByMemberIdWithDogDetails(memberId);

        return favorites.stream()
                .map(FavoriteListResponseDto::new)
                .toList();
    }

    /**
     * 특정 멤버의 즐겨찾기 목록 조회 (페이징)
     *
     * @param memberId 멤버 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 즐겨찾기 목록 (강아지 상세 정보 포함)
     *
     * 비즈니스 로직:
     * 1. N+1 문제 해결을 위한 페치 조인 사용
     * 2. 페이징 처리로 성능 최적화
     * 3. 최신 즐겨찾기순으로 정렬하여 반환
     */
    public List<FavoriteListResponseDto> getMemberFavorites(Long memberId, int page, int size) {
        // 페이징 설정 (최신 즐겨찾기순 정렬)
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        // 페이징된 즐겨찾기 조회 (N+1 문제 해결)
        List<Favorite> favorites = favoriteRepository.findByMemberIdWithDogDetailsPaging(memberId, pageable);

        return favorites.stream()
                .map(FavoriteListResponseDto::new)
                .toList();
    }

    /**
     * 즐겨찾기 존재 여부 확인
     *
     * @param memberId 멤버 ID
     * @param dogId 강아지 ID
     * @return 즐겨찾기 존재 여부
     */
    public boolean isFavorite(Long memberId, Long dogId) {
        return favoriteRepository.existsByMemberMemberIdAndDogDogId(memberId, dogId);
    }
}