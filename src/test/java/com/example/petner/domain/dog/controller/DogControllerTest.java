package com.example.petner.domain.dog.controller;

import com.example.petner.domain.dog.common.AdoptionStatus;
import com.example.petner.domain.dog.common.DogSize;
import com.example.petner.domain.dog.dto.request.DogCreateRequestDto;
import com.example.petner.domain.dog.dto.request.DogUpdateRequestDto;
import com.example.petner.domain.dog.dto.response.DogDeleteResponseDto;
import com.example.petner.domain.dog.dto.response.DogListResponseDto;
import com.example.petner.domain.dog.dto.response.DogResponseDto;
import com.example.petner.domain.dog.service.DogService;
import com.example.petner.global.config.common.Gender;
import com.example.petner.global.dto.SessionUser;
import com.example.petner.search.document.DogDocument;
import com.example.petner.search.service.DogSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DogControllerTest {

    @Mock
    private DogService dogService;

    @Mock
    private DogSearchService dogSearchService;

    @InjectMocks
    private DogController dogController;

    private SessionUser sessionUser;
    private DogCreateRequestDto createRequestDto;
    private DogUpdateRequestDto updateRequestDto;
    private DogResponseDto responseDto;
    private List<DogListResponseDto> listResponseDto;
    private List<DogDocument> searchDocuments;

    @BeforeEach
    void setUp() {
        sessionUser = SessionUser.builder()
                .memberId(1L)
                .email("user@example.com")
                .nickname("사용자")
                .build();

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
        ReflectionTestUtils.setField(updateRequestDto, "name", "새이름");
        ReflectionTestUtils.setField(updateRequestDto, "weight", new BigDecimal("18.0"));

        responseDto = DogResponseDto.builder()
                .dogId(1L)
                .name("바둑이")
                .breed(DogResponseDto.BreedInfo.builder()
                        .breedId(1L)
                        .name("골든 리트리버")
                        .build())
                .birthDate("202201")
                .gender(Gender.MALE)
                .dogSize(DogSize.중형)
                .weight(new BigDecimal("15.5"))
                .healthStatus("예방접종 완료")
                .description("온순한 성격")
                .adoptionStatus(AdoptionStatus.입양_가능)
                .imageUrl("https://example.com/dog-image.jpg")
                .member(DogResponseDto.MemberInfo.builder()
                        .memberId(1L)
                        .nickname("사용자")
                        .build())
                .shelter(DogResponseDto.ShelterInfo.builder()
                        .shelterId(1L)
                        .name("서울시 강남구 동물보호센터")
                        .contact("02-1234-5678")
                        .build())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        DogListResponseDto listItem = DogListResponseDto.builder()
                .dogId(1L)
                .name("바둑이")
                .breedName("골든 리트리버")
                .gender(Gender.MALE)
                .dogSize(DogSize.중형)
                .adoptionStatus(AdoptionStatus.입양_가능)
                .imageUrl("https://example.com/dog-image.jpg")
                .memberNickname("사용자")
                .createdAt(LocalDateTime.now())
                .build();

        listResponseDto = Arrays.asList(listItem);

        DogDocument document = DogDocument.builder()
                .dogId(1L)
                .name("바둑이")
                .breedName("골든 리트리버")
                .dogSize(DogSize.중형)
                .gender(Gender.MALE)
                .adoptionStatus(AdoptionStatus.입양_가능)
                .imageUrl("https://example.com/dog-image.jpg")
                .build();

        searchDocuments = Arrays.asList(document);
    }

    @Test
    @DisplayName("유기견 등록 API 성공")
    void createDog_Success() {
        // Given
        when(dogService.createDog(createRequestDto, sessionUser)).thenReturn(responseDto);

        // When
        ResponseEntity<DogResponseDto> response = dogController.createDog(createRequestDto, sessionUser);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(responseDto);
        assertThat(response.getBody().getDogId()).isEqualTo(1L);
        assertThat(response.getBody().getName()).isEqualTo("바둑이");

        verify(dogService).createDog(createRequestDto, sessionUser);
    }

    @Test
    @DisplayName("유기견 목록 조회 API 성공 (페이징)")
    void getDogs_Success() {
        // Given
        int page = 0;
        int size = 10;
        when(dogService.getDogs(page, size)).thenReturn(listResponseDto);

        // When
        ResponseEntity<List<DogListResponseDto>> response = dogController.getDogs(page, size);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(listResponseDto);
        assertThat(response.getBody()).hasSize(1);

        verify(dogService).getDogs(page, size);
    }

    @Test
    @DisplayName("전체 유기견 목록 조회 API 성공")
    void getAllDogs_Success() {
        // Given
        when(dogService.getAllDogs()).thenReturn(listResponseDto);

        // When
        ResponseEntity<List<DogListResponseDto>> response = dogController.getAllDogs();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(listResponseDto);
        assertThat(response.getBody()).hasSize(1);

        verify(dogService).getAllDogs();
    }

    @Test
    @DisplayName("내가 등록한 유기견 목록 조회 API 성공")
    void getMyDogs_Success() {
        // Given
        int page = 0;
        int size = 10;
        when(dogService.getMyDogs(page, size, sessionUser)).thenReturn(listResponseDto);

        // When
        ResponseEntity<List<DogListResponseDto>> response = dogController.getMyDogs(page, size, sessionUser);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(listResponseDto);
        assertThat(response.getBody()).hasSize(1);

        verify(dogService).getMyDogs(page, size, sessionUser);
    }

    @Test
    @DisplayName("유기견 상세 조회 API 성공")
    void getDogById_Success() {
        // Given
        Long dogId = 1L;
        when(dogService.getDogById(dogId)).thenReturn(responseDto);

        // When
        ResponseEntity<DogResponseDto> response = dogController.getDogById(dogId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(responseDto);
        assertThat(response.getBody().getDogId()).isEqualTo(1L);

        verify(dogService).getDogById(dogId);
    }

    @Test
    @DisplayName("유기견 정보 수정 API 성공")
    void updateDog_Success() {
        // Given
        Long dogId = 1L;
        when(dogService.updateDog(dogId, updateRequestDto, sessionUser)).thenReturn(responseDto);

        // When
        ResponseEntity<DogResponseDto> response = dogController.updateDog(dogId, updateRequestDto, sessionUser);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(responseDto);
        assertThat(response.getBody().getDogId()).isEqualTo(1L);

        verify(dogService).updateDog(dogId, updateRequestDto, sessionUser);
    }

    @Test
    @DisplayName("유기견 삭제 API 성공")
    void deleteDog_Success() {
        // Given
        Long dogId = 1L;

        // When
        ResponseEntity<DogDeleteResponseDto> response = dogController.deleteDog(dogId, sessionUser);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDogId()).isEqualTo(dogId);
        assertThat(response.getBody().getMemberId()).isEqualTo(sessionUser.getMemberId());
        assertThat(response.getBody().getMessage()).isEqualTo("유기견 정보 삭제 성공");

        verify(dogService).deleteDog(dogId, sessionUser);
    }

    @Test
    @DisplayName("유기견 검색 API 성공 - 모든 파라미터")
    void searchDogs_Success_AllParameters() {
        // Given
        String q = "말티즈";
        DogSize dogSize = DogSize.소형;
        String breedName = "말티즈";
        Gender gender = Gender.FEMALE;
        String location = "서울";
        AdoptionStatus adoptionStatus = AdoptionStatus.입양_가능;
        int page = 0;
        int size = 10;

        when(dogSearchService.searchDogs(q, dogSize, breedName, gender, location, adoptionStatus, page, size))
                .thenReturn(searchDocuments);

        // When
        ResponseEntity<List<DogDocument>> response = dogController.searchDogs(
                q, dogSize, breedName, gender, location, adoptionStatus, page, size);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(searchDocuments);
        assertThat(response.getBody()).hasSize(1);

        verify(dogSearchService).searchDogs(q, dogSize, breedName, gender, location, adoptionStatus, page, size);
    }

    @Test
    @DisplayName("유기견 검색 API 성공 - 키워드만")
    void searchDogs_Success_KeywordOnly() {
        // Given
        String q = "골든";
        when(dogSearchService.searchDogs(q, null, null, null, null, null, 0, 10))
                .thenReturn(searchDocuments);

        // When
        ResponseEntity<List<DogDocument>> response = dogController.searchDogs(
                q, null, null, null, null, null, 0, 10);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(searchDocuments);

        verify(dogSearchService).searchDogs(q, null, null, null, null, null, 0, 10);
    }

    @Test
    @DisplayName("유기견 검색 API 성공 - 필터만")
    void searchDogs_Success_FiltersOnly() {
        // Given
        DogSize dogSize = DogSize.중형;
        Gender gender = Gender.MALE;
        AdoptionStatus adoptionStatus = AdoptionStatus.입양_가능;

        when(dogSearchService.searchDogs(null, dogSize, null, gender, null, adoptionStatus, 0, 10))
                .thenReturn(searchDocuments);

        // When
        ResponseEntity<List<DogDocument>> response = dogController.searchDogs(
                null, dogSize, null, gender, null, adoptionStatus, 0, 10);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(searchDocuments);

        verify(dogSearchService).searchDogs(null, dogSize, null, gender, null, adoptionStatus, 0, 10);
    }

    @Test
    @DisplayName("기본 페이징 파라미터 확인")
    void defaultPagingParameters() {
        // Given
        when(dogService.getDogs(0, 10)).thenReturn(listResponseDto);

        // When
        ResponseEntity<List<DogListResponseDto>> response = dogController.getDogs(0, 10);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        verify(dogService).getDogs(0, 10);
    }

    @Test
    @DisplayName("빈 목록 응답 처리")
    void emptyListResponse() {
        // Given
        when(dogService.getDogs(0, 10)).thenReturn(List.of());

        // When
        ResponseEntity<List<DogListResponseDto>> response = dogController.getDogs(0, 10);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();

        verify(dogService).getDogs(0, 10);
    }

    @Test
    @DisplayName("다양한 페이징 파라미터 처리")
    void variousPagingParameters() {
        // Given
        int[] pages = {1, 2, 5};
        int[] sizes = {5, 15, 20};

        for (int page : pages) {
            for (int size : sizes) {
                when(dogService.getDogs(page, size)).thenReturn(listResponseDto);

                // When
                ResponseEntity<List<DogListResponseDto>> response = dogController.getDogs(page, size);

                // Then
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                verify(dogService).getDogs(page, size);
            }
        }
    }

    @Test
    @DisplayName("서비스 호출 순서 확인")
    void serviceCallOrder() {
        // Given
        Long dogId = 1L;
        when(dogService.getDogById(dogId)).thenReturn(responseDto);

        // When
        dogController.getDogById(dogId);

        // Then
        verify(dogService, times(1)).getDogById(dogId);
        verifyNoMoreInteractions(dogService);
    }

    @Test
    @DisplayName("검색 서비스 호출 확인")
    void searchServiceCall() {
        // Given
        String q = "테스트";
        when(dogSearchService.searchDogs(q, null, null, null, null, null, 0, 10))
                .thenReturn(searchDocuments);

        // When
        dogController.searchDogs(q, null, null, null, null, null, 0, 10);

        // Then
        verify(dogSearchService, times(1))
                .searchDogs(q, null, null, null, null, null, 0, 10);
        verifyNoMoreInteractions(dogSearchService);
    }

    @Test
    @DisplayName("다양한 AdoptionStatus 검색 테스트")
    void searchDogs_VariousAdoptionStatus() {
        // Given
        AdoptionStatus[] statuses = {AdoptionStatus.입양_가능, AdoptionStatus.입양_절차_중, AdoptionStatus.입양_완료};

        for (AdoptionStatus status : statuses) {
            when(dogSearchService.searchDogs(null, null, null, null, null, status, 0, 10))
                    .thenReturn(searchDocuments);

            // When
            ResponseEntity<List<DogDocument>> response = dogController.searchDogs(
                    null, null, null, null, null, status, 0, 10);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(dogSearchService).searchDogs(null, null, null, null, null, status, 0, 10);
        }
    }

    @Test
    @DisplayName("다양한 DogSize 검색 테스트")
    void searchDogs_VariousDogSize() {
        // Given
        DogSize[] sizes = {DogSize.소형, DogSize.중형, DogSize.대형};

        for (DogSize size : sizes) {
            when(dogSearchService.searchDogs(null, size, null, null, null, null, 0, 10))
                    .thenReturn(searchDocuments);

            // When
            ResponseEntity<List<DogDocument>> response = dogController.searchDogs(
                    null, size, null, null, null, null, 0, 10);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(dogSearchService).searchDogs(null, size, null, null, null, null, 0, 10);
        }
    }

    @Test
    @DisplayName("다양한 Gender 검색 테스트")
    void searchDogs_VariousGender() {
        // Given
        Gender[] genders = {Gender.MALE, Gender.FEMALE};

        for (Gender gender : genders) {
            when(dogSearchService.searchDogs(null, null, null, gender, null, null, 0, 10))
                    .thenReturn(searchDocuments);

            // When
            ResponseEntity<List<DogDocument>> response = dogController.searchDogs(
                    null, null, null, gender, null, null, 0, 10);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(dogSearchService).searchDogs(null, null, null, gender, null, null, 0, 10);
        }
    }
}