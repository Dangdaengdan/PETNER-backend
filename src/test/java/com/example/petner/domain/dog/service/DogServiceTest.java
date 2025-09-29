package com.example.petner.domain.dog.service;

import com.example.petner.domain.breed.entity.Breed;
import com.example.petner.domain.dog.common.AdoptionStatus;
import com.example.petner.domain.dog.common.DogSize;
import com.example.petner.domain.dog.dto.request.DogCreateRequestDto;
import com.example.petner.domain.dog.dto.request.DogUpdateRequestDto;
import com.example.petner.domain.dog.dto.response.DogListResponseDto;
import com.example.petner.domain.dog.dto.response.DogResponseDto;
import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.domain.dog.repository.DogRepository;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.shelter.entity.Shelter;
import com.example.petner.domain.location.entity.Location;
import com.example.petner.domain.upload.service.UploadService;
import com.example.petner.global.config.common.Gender;
import com.example.petner.global.dto.SessionUser;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.DogException;
import com.example.petner.search.event.DogEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DogServiceTest {

    @Mock
    private DogRepository dogRepository;

    @Mock
    private DogValidator dogValidator;

    @Mock
    private DogUpdater dogUpdater;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private UploadService uploadService;

    @InjectMocks
    private DogService dogService;

    private SessionUser sessionUser;
    private Member member;
    private Breed breed;
    private Shelter shelter;
    private Dog dog;
    private DogCreateRequestDto createRequestDto;
    private DogUpdateRequestDto updateRequestDto;

    @BeforeEach
    void setUp() {
        sessionUser = SessionUser.builder()
                .memberId(1L)
                .email("user@example.com")
                .nickname("사용자")
                .build();

        member = Member.builder()
                .kakaoId("12345")
                .email("user@example.com")
                .nickname("사용자")
                .build();
        ReflectionTestUtils.setField(member, "memberId", 1L);

        breed = Breed.builder()
                .name("골든 리트리버")
                .build();
        ReflectionTestUtils.setField(breed, "breedId", 1L);

        Location location = Location.builder()
                .state("서울시")
                .district("강남구")
                .build();

        shelter = Shelter.builder()
                .name("서울시 강남구 동물보호센터")
                .location(location)
                .shelterContact("02-1234-5678")
                .build();
        ReflectionTestUtils.setField(shelter, "shelterId", 1L);

        dog = Dog.builder()
                .name("바둑이")
                .breed(breed)
                .birthDate("202201")
                .gender(Gender.MALE)
                .dogSize(DogSize.중형)
                .weight(new BigDecimal("15.5"))
                .healthStatus("예방접종 완료")
                .description("온순한 성격")
                .adoptionStatus(AdoptionStatus.입양_가능)
                .imageUrl("https://example.com/dog-image.jpg")
                .member(member)
                .shelter(shelter)
                .build();
        ReflectionTestUtils.setField(dog, "dogId", 1L);
        ReflectionTestUtils.setField(dog, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(dog, "updatedAt", LocalDateTime.now());

        createRequestDto = new DogCreateRequestDto();
        ReflectionTestUtils.setField(createRequestDto, "name", "바둑이");
        ReflectionTestUtils.setField(createRequestDto, "breedId", 1L);
        ReflectionTestUtils.setField(createRequestDto, "birthDate", "202201");
        ReflectionTestUtils.setField(createRequestDto, "gender", Gender.MALE);
        ReflectionTestUtils.setField(createRequestDto, "dogSize", DogSize.중형);
        ReflectionTestUtils.setField(createRequestDto, "weight", new BigDecimal("15.5"));
        ReflectionTestUtils.setField(createRequestDto, "healthStatus", "예방접종 완료");
        ReflectionTestUtils.setField(createRequestDto, "description", "온순한 성격");
        ReflectionTestUtils.setField(createRequestDto, "adoptionStatus", AdoptionStatus.입양_가능);
        ReflectionTestUtils.setField(createRequestDto, "imageUrl", "https://example.com/dog-image.jpg");
        ReflectionTestUtils.setField(createRequestDto, "shelterId", 1L);

        updateRequestDto = new DogUpdateRequestDto();
    }

    @Test
    @DisplayName("유기견 등록 성공")
    void createDog_Success() {
        // Given
        when(dogValidator.validateAndGetMember(sessionUser)).thenReturn(member);
        when(dogValidator.validateAndGetBreed(1L)).thenReturn(breed);
        when(dogValidator.validateAndGetShelter(1L)).thenReturn(shelter);
        when(dogRepository.save(any(Dog.class))).thenReturn(dog);

        // When
        DogResponseDto result = dogService.createDog(createRequestDto, sessionUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDogId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("바둑이");

        verify(dogValidator).validateAndGetMember(sessionUser);
        verify(dogValidator).validateAndGetBreed(1L);
        verify(dogValidator).validateAndGetShelter(1L);
        verify(dogRepository).save(any(Dog.class));
        verify(eventPublisher).publishEvent(any(DogEvent.class));
    }

    @Test
    @DisplayName("유기견 등록 실패 시 이미지 삭제")
    void createDog_Failure_DeleteImage() {
        // Given
        when(dogValidator.validateAndGetMember(sessionUser)).thenReturn(member);
        when(dogValidator.validateAndGetBreed(1L)).thenThrow(new RuntimeException("견종 조회 실패"));

        // When & Then
        assertThatThrownBy(() -> dogService.createDog(createRequestDto, sessionUser))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("견종 조회 실패");

        verify(uploadService).deleteImageFromStorage("https://example.com/dog-image.jpg");
    }

    @Test
    @DisplayName("유기견 목록 조회 (페이징) 성공")
    void getDogs_Success() {
        // Given
        List<Dog> dogs = Arrays.asList(dog);
        Pageable expectedPageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        when(dogRepository.findAllWithAssociationsPaging(any(Pageable.class))).thenReturn(dogs);

        // When
        List<DogListResponseDto> result = dogService.getDogs(0, 10);

        // Then
        assertThat(result).hasSize(1);
        verify(dogRepository).findAllWithAssociationsPaging(any(Pageable.class));
    }

    @Test
    @DisplayName("전체 유기견 목록 조회 성공")
    void getAllDogs_Success() {
        // Given
        List<Dog> dogs = Arrays.asList(dog);
        when(dogRepository.findAllWithAssociations()).thenReturn(dogs);

        // When
        List<DogListResponseDto> result = dogService.getAllDogs();

        // Then
        assertThat(result).hasSize(1);
        verify(dogRepository).findAllWithAssociations();
    }

    @Test
    @DisplayName("내가 등록한 유기견 목록 조회 성공")
    void getMyDogs_Success() {
        // Given
        List<Dog> dogs = Arrays.asList(dog);
        when(dogRepository.findByMemberIdWithAssociationsPaging(eq(1L), any(Pageable.class))).thenReturn(dogs);

        // When
        List<DogListResponseDto> result = dogService.getMyDogs(0, 10, sessionUser);

        // Then
        assertThat(result).hasSize(1);
        verify(dogRepository).findByMemberIdWithAssociationsPaging(eq(1L), any(Pageable.class));
    }

    @Test
    @DisplayName("유기견 상세 조회 성공")
    void getDogById_Success() {
        // Given
        when(dogRepository.findByIdWithAssociations(1L)).thenReturn(Optional.of(dog));

        // When
        DogResponseDto result = dogService.getDogById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDogId()).isEqualTo(1L);
        verify(dogRepository).findByIdWithAssociations(1L);
    }

    @Test
    @DisplayName("유기견 상세 조회 실패 - 존재하지 않는 유기견")
    void getDogById_NotFound() {
        // Given
        when(dogRepository.findByIdWithAssociations(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> dogService.getDogById(1L))
                .isInstanceOf(DogException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DOG_NOT_FOUND);

        verify(dogRepository).findByIdWithAssociations(1L);
    }

    @Test
    @DisplayName("유기견 정보 수정 성공")
    void updateDog_Success() {
        // Given
        when(dogRepository.findById(1L)).thenReturn(Optional.of(dog));

        // When
        DogResponseDto result = dogService.updateDog(1L, updateRequestDto, sessionUser);

        // Then
        assertThat(result).isNotNull();
        verify(dogRepository).findById(1L);
        verify(dogValidator).validateDogAccess(dog, sessionUser);
        verify(dogUpdater).updateDogInfo(eq(dog), eq(updateRequestDto), any(Breed.class), any(Shelter.class));
        verify(eventPublisher).publishEvent(any(DogEvent.class));
    }

    @Test
    @DisplayName("유기견 정보 수정 실패 - 존재하지 않는 유기견")
    void updateDog_NotFound() {
        // Given
        when(dogRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> dogService.updateDog(1L, updateRequestDto, sessionUser))
                .isInstanceOf(DogException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DOG_NOT_FOUND);

        verify(dogRepository).findById(1L);
    }

    @Test
    @DisplayName("유기견 소프트 삭제 성공")
    void deleteDog_Success() {
        // Given
        when(dogRepository.findByIdWithAssociationsIncludeDeleted(1L)).thenReturn(Optional.of(dog));

        // When
        dogService.deleteDog(1L, sessionUser);

        // Then
        verify(dogRepository).findByIdWithAssociationsIncludeDeleted(1L);
        verify(dogValidator).validateDogAccess(dog, sessionUser);
        verify(eventPublisher).publishEvent(any(DogEvent.class));
    }

    @Test
    @DisplayName("유기견 소프트 삭제 실패 - 존재하지 않는 유기견")
    void deleteDog_NotFound() {
        // Given
        when(dogRepository.findByIdWithAssociationsIncludeDeleted(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> dogService.deleteDog(1L, sessionUser))
                .isInstanceOf(DogException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DOG_NOT_FOUND);

        verify(dogRepository).findByIdWithAssociationsIncludeDeleted(1L);
    }

    @Test
    @DisplayName("유기견 소프트 삭제 실패 - 이미 삭제된 유기견")
    void deleteDog_AlreadyDeleted() {
        // Given
        dog.softDelete(); // 이미 삭제된 상태로 설정
        when(dogRepository.findByIdWithAssociationsIncludeDeleted(1L)).thenReturn(Optional.of(dog));

        // When & Then
        assertThatThrownBy(() -> dogService.deleteDog(1L, sessionUser))
                .isInstanceOf(DogException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DOG_ALREADY_DELETED);

        verify(dogRepository).findByIdWithAssociationsIncludeDeleted(1L);
    }

    @Test
    @DisplayName("유기견 수정 시 견종 변경")
    void updateDog_BreedChanged() {
        // Given
        Breed newBreed = Breed.builder()
                .name("말티즈")
                .build();
        ReflectionTestUtils.setField(newBreed, "breedId", 2L);
        ReflectionTestUtils.setField(updateRequestDto, "breedId", 2L);

        when(dogRepository.findById(1L)).thenReturn(Optional.of(dog));
        when(dogValidator.validateAndGetBreed(2L)).thenReturn(newBreed);

        // When
        dogService.updateDog(1L, updateRequestDto, sessionUser);

        // Then
        verify(dogValidator).validateAndGetBreed(2L);
        verify(dogUpdater).updateDogInfo(eq(dog), eq(updateRequestDto), eq(newBreed), any(Shelter.class));
    }

    @Test
    @DisplayName("유기견 수정 시 보호소 변경")
    void updateDog_ShelterChanged() {
        // Given
        Location newLocation = Location.builder()
                .state("부산시")
                .district("해운대구")
                .build();

        Shelter newShelter = Shelter.builder()
                .name("부산시 동물보호센터")
                .location(newLocation)
                .shelterContact("051-1234-5678")
                .build();
        ReflectionTestUtils.setField(newShelter, "shelterId", 2L);
        ReflectionTestUtils.setField(updateRequestDto, "shelterId", 2L);

        when(dogRepository.findById(1L)).thenReturn(Optional.of(dog));
        when(dogValidator.validateAndGetShelter(2L)).thenReturn(newShelter);

        // When
        dogService.updateDog(1L, updateRequestDto, sessionUser);

        // Then
        verify(dogValidator).validateAndGetShelter(2L);
        verify(dogUpdater).updateDogInfo(eq(dog), eq(updateRequestDto), any(Breed.class), eq(newShelter));
    }

    @Test
    @DisplayName("페이징 파라미터 정확히 전달되는지 확인")
    void pagingParameters_PassedCorrectly() {
        // Given
        int page = 2;
        int size = 20;
        List<Dog> dogs = Arrays.asList(dog);

        when(dogRepository.findAllWithAssociationsPaging(any(Pageable.class))).thenReturn(dogs);

        // When
        dogService.getDogs(page, size);

        // Then
        verify(dogRepository).findAllWithAssociationsPaging(argThat(pageable ->
                pageable.getPageNumber() == page &&
                pageable.getPageSize() == size &&
                pageable.getSort().equals(Sort.by(Sort.Direction.DESC, "createdAt"))
        ));
    }

    @Test
    @DisplayName("빈 목록 조회 시 정상 처리")
    void getDogs_EmptyList() {
        // Given
        when(dogRepository.findAllWithAssociationsPaging(any(Pageable.class))).thenReturn(List.of());

        // When
        List<DogListResponseDto> result = dogService.getDogs(0, 10);

        // Then
        assertThat(result).isEmpty();
        verify(dogRepository).findAllWithAssociationsPaging(any(Pageable.class));
    }

    @Test
    @DisplayName("이벤트 발행 확인 - 생성")
    void eventPublisher_Create() {
        // Given
        when(dogValidator.validateAndGetMember(sessionUser)).thenReturn(member);
        when(dogValidator.validateAndGetBreed(1L)).thenReturn(breed);
        when(dogValidator.validateAndGetShelter(1L)).thenReturn(shelter);
        when(dogRepository.save(any(Dog.class))).thenReturn(dog);

        // When
        dogService.createDog(createRequestDto, sessionUser);

        // Then
        verify(eventPublisher).publishEvent(any(DogEvent.class));
    }

    @Test
    @DisplayName("이벤트 발행 확인 - 수정")
    void eventPublisher_Update() {
        // Given
        when(dogRepository.findById(1L)).thenReturn(Optional.of(dog));

        // When
        dogService.updateDog(1L, updateRequestDto, sessionUser);

        // Then
        verify(eventPublisher).publishEvent(any(DogEvent.class));
    }

    @Test
    @DisplayName("이벤트 발행 확인 - 삭제")
    void eventPublisher_Delete() {
        // Given
        when(dogRepository.findByIdWithAssociationsIncludeDeleted(1L)).thenReturn(Optional.of(dog));

        // When
        dogService.deleteDog(1L, sessionUser);

        // Then
        verify(eventPublisher).publishEvent(any(DogEvent.class));
    }
}