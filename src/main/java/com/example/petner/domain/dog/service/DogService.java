package com.example.petner.domain.dog.service;

import com.example.petner.domain.breed.entity.Breed;
import com.example.petner.domain.breed.repository.BreedRepository;
import com.example.petner.domain.dog.dto.request.DogCreateRequestDto;
import com.example.petner.domain.dog.dto.request.DogUpdateRequestDto;
import com.example.petner.domain.dog.dto.response.DogListResponseDto;
import com.example.petner.domain.dog.dto.response.DogResponseDto;
import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.domain.dog.repository.DogRepository;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.member.repository.MemberRepository;
import com.example.petner.domain.shelter.entity.Shelter;
import com.example.petner.domain.shelter.repository.ShelterRepository;
import com.example.petner.global.dto.SessionUser;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.DogException;
import com.example.petner.global.exception.customException.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 유기견 관리 서비스
 * 유기견 CRUD 기능을 제공하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DogService {

    private final DogRepository dogRepository;
    private final BreedRepository breedRepository;
    private final MemberRepository memberRepository;
    private final ShelterRepository shelterRepository;

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
        Member member = memberRepository.findById(user.getMemberId())
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        // 2. 견종 검증
        Breed breed = breedRepository.findById(requestDto.getBreedId())
                .orElseThrow(() -> new DogException(ErrorCode.DOG_BREED_NOT_FOUND));

        // 3. 보호소 검증 (선택적)
        Shelter shelter = null;
        if (requestDto.getShelterId() != null) {
            shelter = shelterRepository.findById(requestDto.getShelterId())
                    .orElseThrow(() -> new DogException(ErrorCode.DOG_SHELTER_NOT_FOUND));
        }

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

        // 6. 응답 DTO 반환
        return DogResponseDto.from(savedDog);
    }

    /**
     * 유기견 목록 조회
     *
     * @return 전체 유기견 목록 (최신 등록순 정렬)
     */
    public List<DogListResponseDto> getAllDogs() {
        List<Dog> dogs = dogRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
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
        Dog dog = dogRepository.findById(dogId)
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
        if (!dog.getMember().getMemberId().equals(user.getMemberId())) {
            throw new DogException(ErrorCode.DOG_ACCESS_DENIED);
        }

        // 3. 견종 검증 (변경되는 경우)
        Breed breed = dog.getBreed();
        if (requestDto.getBreedId() != null && !requestDto.getBreedId().equals(breed.getBreedId())) {
            breed = breedRepository.findById(requestDto.getBreedId())
                    .orElseThrow(() -> new DogException(ErrorCode.DOG_BREED_NOT_FOUND));
        }

        // 4. 보호소 검증 (변경되는 경우)
        Shelter shelter = dog.getShelter();
        if (requestDto.getShelterId() != null) {
            if (shelter == null || !requestDto.getShelterId().equals(shelter.getShelterId())) {
                shelter = shelterRepository.findById(requestDto.getShelterId())
                        .orElseThrow(() -> new DogException(ErrorCode.DOG_SHELTER_NOT_FOUND));
            }
        }

        // 5. 유기견 정보 업데이트
        updateDogFields(dog, requestDto, breed, shelter);

        // 6. 응답 반환 (더티 체킹으로 자동 업데이트)
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
        if (!dog.getMember().getMemberId().equals(user.getMemberId())) {
            throw new DogException(ErrorCode.DOG_ACCESS_DENIED);
        }

        // 3. 삭제 수행
        dogRepository.delete(dog);
    }

    /**
     * 유기견 필드 업데이트 헬퍼 메서드
     * null이 아닌 필드만 업데이트
     */
    private void updateDogFields(Dog dog, DogUpdateRequestDto requestDto, Breed breed, Shelter shelter) {
        dog.updateDogInfo(
                StringUtils.hasText(requestDto.getName()) ? requestDto.getName() : dog.getName(),
                breed,
                StringUtils.hasText(requestDto.getBirthDate()) ? requestDto.getBirthDate() : dog.getBirthDate(),
                requestDto.getGender() != null ? requestDto.getGender() : dog.getGender(),
                requestDto.getDogSize() != null ? requestDto.getDogSize() : dog.getDogSize(),
                requestDto.getWeight() != null ? requestDto.getWeight() : dog.getWeight(),
                requestDto.getHealthStatus() != null ? requestDto.getHealthStatus() : dog.getHealthStatus(),
                requestDto.getDescription() != null ? requestDto.getDescription() : dog.getDescription(),
                requestDto.getAdoptionStatus() != null ? requestDto.getAdoptionStatus() : dog.getAdoptionStatus(),
                StringUtils.hasText(requestDto.getImageUrl()) ? requestDto.getImageUrl() : dog.getImageUrl(),
                shelter
        );
    }
}