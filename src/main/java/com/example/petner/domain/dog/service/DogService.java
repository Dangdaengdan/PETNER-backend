package com.example.petner.domain.dog.service;

import com.example.petner.domain.breed.entity.Breed;
import com.example.petner.domain.dog.dto.request.DogCreateRequestDto;
import com.example.petner.domain.dog.dto.request.DogUpdateRequestDto;
import com.example.petner.domain.dog.dto.response.DogListResponseDto;
import com.example.petner.domain.dog.dto.response.DogResponseDto;
import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.domain.dog.repository.DogRepository;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.shelter.entity.Shelter;
import com.example.petner.global.dto.SessionUser;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.DogException;
import com.example.petner.search.event.DogEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 유기견 관리 서비스
 * 유기견 CRUD 기능을 제공하는 서비스 클래스
 * SOLID 원칙을 준수하여 리팩토링된 버전
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DogService {

    private final DogRepository dogRepository;
    private final DogValidator dogValidator;
    private final DogUpdater dogUpdater;

    /**
     * 유기견 등록
     *
     * @param requestDto 유기견 등록 요청 데이터
     * @param user 세션 사용자 정보
     * @return 등록된 유기견 정보
     */
    @Transactional
    public DogResponseDto createDog(DogCreateRequestDto requestDto, SessionUser user) {
        // 1. 사용자 검증
        Member member = dogValidator.validateAndGetMember(user);

        // 2. 견종 검증
        Breed breed = dogValidator.validateAndGetBreed(requestDto.getBreedId());

        // 3. 보호소 검증 (선택적)
        Shelter shelter = dogValidator.validateAndGetShelter(requestDto.getShelterId());

        // 4. 유기견 엔티티 생성
        Dog dog = Dog.builder()
                .name(requestDto.getName())
                .breed(breed)
                .birthDate(requestDto.getBirthDate())
                .gender(requestDto.getGender())
                .dogSize(requestDto.getDogSize())
                .weight(requestDto.getWeight())
                .healthStatus(requestDto.getHealthStatus())
                .description(requestDto.getDescription())
                .adoptionStatus(requestDto.getAdoptionStatus())
                .imageUrl(requestDto.getImageUrl())
                .member(member)
                .shelter(shelter)
                .build();

        // 5. 데이터베이스에 저장
        Dog savedDog = dogRepository.save(dog);

        // 6. OpenSearch 동기화를 위한 이벤트 발행
        eventPublisher.publishEvent(DogEvent.created(savedDog.getDogId()));

        // 7. 응답 DTO 반환
        return DogResponseDto.from(savedDog);
    }

    /**
     * 유기견 목록 조회 (페이징)
     *
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 유기견 목록 (최신 등록순 정렬)
     *
     * 비즈니스 로직:
     * 1. N+1 문제 해결을 위한 페치 조인 사용
     * 2. 페이징 처리로 성능 최적화
     * 3. 최신 등록순으로 정렬하여 반환
     */
    public List<DogListResponseDto> getDogs(int page, int size) {
        // 페이징 설정 (최신 등록순 정렬)
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        // 페이징된 유기견 조회 (N+1 문제 해결)
        List<Dog> dogs = dogRepository.findAllWithAssociationsPaging(pageable);

        return dogs.stream()
                .map(DogListResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 유기견 목록 조회 (전체 - 페이징 없음)
     *
     * @return 전체 유기견 목록 (최신 등록순 정렬)
     */
    public List<DogListResponseDto> getAllDogs() {
        List<Dog> dogs = dogRepository.findAllWithAssociations();
        return dogs.stream()
                .map(DogListResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 유기견 상세 조회
     *
     * @param dogId 유기견 ID
     * @return 유기견 상세 정보
     */
    public DogResponseDto getDogById(Long dogId) {
        Dog dog = dogRepository.findByIdWithAssociations(dogId)
                .orElseThrow(() -> new DogException(ErrorCode.DOG_NOT_FOUND));

        return DogResponseDto.from(dog);
    }

    /**
     * 유기견 정보 수정
     *
     * @param dogId 유기견 ID
     * @param requestDto 수정 요청 데이터
     * @param user 세션 사용자 정보
     * @return 수정된 유기견 정보
     */
    @Transactional
    public DogResponseDto updateDog(Long dogId, DogUpdateRequestDto requestDto, SessionUser user) {
        // 1. 유기견 조회
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new DogException(ErrorCode.DOG_NOT_FOUND));

        // 2. 권한 검증 (본인이 등록한 유기견인지 확인)
        dogValidator.validateDogAccess(dog, user);

        // 3. 견종 검증 (변경되는 경우)
        Breed breed = dog.getBreed();
        if (requestDto.getBreedId() != null && !requestDto.getBreedId().equals(breed.getBreedId())) {
            breed = dogValidator.validateAndGetBreed(requestDto.getBreedId());
        }

        // 4. 보호소 검증 (변경되는 경우)
        Shelter shelter = dog.getShelter();
        if (requestDto.getShelterId() != null) {
            if (shelter == null || !requestDto.getShelterId().equals(shelter.getShelterId())) {
                shelter = dogValidator.validateAndGetShelter(requestDto.getShelterId());
            }
        }

        // 5. 유기견 정보 업데이트
        dogUpdater.updateDogInfo(dog, requestDto, breed, shelter);

        // 6. OpenSearch 동기화를 위한 이벤트 발행
        eventPublisher.publishEvent(DogEvent.updated(dogId));

        // 7. 응답 반환 (더티 체킹으로 자동 업데이트)
        return DogResponseDto.from(dog);
    }

    /**
     * 유기견 삭제
     *
     * @param dogId 유기견 ID
     * @param user 세션 사용자 정보
     */
    @Transactional
    public void deleteDog(Long dogId, SessionUser user) {
        // 1. 유기견 조회
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new DogException(ErrorCode.DOG_NOT_FOUND));

        // 2. 권한 검증 (본인이 등록한 유기견인지 확인)
        dogValidator.validateDogAccess(dog, user);

        // 3. 삭제 수행
        dogRepository.delete(dog);

        // 4. OpenSearch 동기화를 위한 이벤트 발행
        eventPublisher.publishEvent(DogEvent.deleted(dogId));
    }
}