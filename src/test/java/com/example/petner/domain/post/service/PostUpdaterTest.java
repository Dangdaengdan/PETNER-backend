package com.example.petner.domain.post.service;

import com.example.petner.domain.post.dto.request.PostUpdateRequestDto;
import com.example.petner.domain.post.entity.Post;
import com.example.petner.domain.upload.service.UploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PostUpdaterTest {

    @Mock
    private UploadService uploadService;

    @InjectMocks
    private PostUpdater postUpdater;

    private Post mockPost;
    private PostUpdateRequestDto mockRequestDto;

    @BeforeEach
    void setUp() {
        mockPost = mock(Post.class);
        mockRequestDto = mock(PostUpdateRequestDto.class);
    }

    @Test
    @DisplayName("게시물 업데이트 성공 - 이미지 변경 없음")
    void updatePost_NoImageChange_Success() {
        // Given
        String existingImageUrl = "existing-image.jpg";
        when(mockPost.getThumbImageUrl()).thenReturn(existingImageUrl);
        when(mockRequestDto.getTitle()).thenReturn("수정된 제목");
        when(mockRequestDto.getContent()).thenReturn("수정된 내용");
        when(mockRequestDto.getThumbImageUrl()).thenReturn(existingImageUrl);

        // When
        postUpdater.updatePost(mockPost, mockRequestDto);

        // Then
        verify(mockPost).update("수정된 제목", "수정된 내용", existingImageUrl);
        verifyNoInteractions(uploadService); // 이미지가 변경되지 않았으므로 삭제 호출 안됨
    }

    @Test
    @DisplayName("게시물 업데이트 성공 - 새 이미지로 변경")
    void updatePost_ImageChanged_Success() {
        // Given
        String oldImageUrl = "old-image.jpg";
        String newImageUrl = "new-image.jpg";

        when(mockPost.getThumbImageUrl()).thenReturn(oldImageUrl);
        when(mockRequestDto.getTitle()).thenReturn("수정된 제목");
        when(mockRequestDto.getContent()).thenReturn("수정된 내용");
        when(mockRequestDto.getThumbImageUrl()).thenReturn(newImageUrl);

        // When
        postUpdater.updatePost(mockPost, mockRequestDto);

        // Then
        verify(uploadService).deleteImageFromStorage(oldImageUrl);
        verify(mockPost).update("수정된 제목", "수정된 내용", newImageUrl);
    }

    @Test
    @DisplayName("게시물 업데이트 성공 - 기존 이미지가 null인 경우")
    void updatePost_OldImageNull_Success() {
        // Given
        String newImageUrl = "new-image.jpg";

        when(mockPost.getThumbImageUrl()).thenReturn(null);
        when(mockRequestDto.getTitle()).thenReturn("수정된 제목");
        when(mockRequestDto.getContent()).thenReturn("수정된 내용");
        when(mockRequestDto.getThumbImageUrl()).thenReturn(newImageUrl);

        // When
        postUpdater.updatePost(mockPost, mockRequestDto);

        // Then
        verify(mockPost).update("수정된 제목", "수정된 내용", newImageUrl);
        verifyNoInteractions(uploadService); // 기존 이미지가 null이므로 삭제 호출 안됨
    }

    @Test
    @DisplayName("게시물 업데이트 성공 - 기존 이미지가 빈 문자열인 경우")
    void updatePost_OldImageBlank_Success() {
        // Given
        String newImageUrl = "new-image.jpg";

        when(mockPost.getThumbImageUrl()).thenReturn("");
        when(mockRequestDto.getTitle()).thenReturn("수정된 제목");
        when(mockRequestDto.getContent()).thenReturn("수정된 내용");
        when(mockRequestDto.getThumbImageUrl()).thenReturn(newImageUrl);

        // When
        postUpdater.updatePost(mockPost, mockRequestDto);

        // Then
        verify(mockPost).update("수정된 제목", "수정된 내용", newImageUrl);
        verifyNoInteractions(uploadService); // 기존 이미지가 빈 문자열이므로 삭제 호출 안됨
    }

    @Test
    @DisplayName("게시물 업데이트 성공 - 새 이미지가 null인 경우")
    void updatePost_NewImageNull_Success() {
        // Given
        String oldImageUrl = "old-image.jpg";

        when(mockPost.getThumbImageUrl()).thenReturn(oldImageUrl);
        when(mockRequestDto.getTitle()).thenReturn("수정된 제목");
        when(mockRequestDto.getContent()).thenReturn("수정된 내용");
        when(mockRequestDto.getThumbImageUrl()).thenReturn(null);

        // When
        postUpdater.updatePost(mockPost, mockRequestDto);

        // Then
        verify(mockPost).update("수정된 제목", "수정된 내용", null);
        verifyNoInteractions(uploadService); // 새 이미지가 null이므로 이미지 변경으로 간주하지 않음
    }

    @Test
    @DisplayName("게시물 업데이트 - 이미지 삭제 실패해도 업데이트 진행")
    void updatePost_ImageDeleteFailure_ContinuesUpdate() {
        // Given
        String oldImageUrl = "old-image.jpg";
        String newImageUrl = "new-image.jpg";

        when(mockPost.getThumbImageUrl()).thenReturn(oldImageUrl);
        when(mockRequestDto.getTitle()).thenReturn("수정된 제목");
        when(mockRequestDto.getContent()).thenReturn("수정된 내용");
        when(mockRequestDto.getThumbImageUrl()).thenReturn(newImageUrl);
        doThrow(new RuntimeException("Storage error")).when(uploadService).deleteImageFromStorage(oldImageUrl);

        // When
        postUpdater.updatePost(mockPost, mockRequestDto);

        // Then
        verify(uploadService).deleteImageFromStorage(oldImageUrl);
        verify(mockPost).update("수정된 제목", "수정된 내용", newImageUrl); // 예외 발생해도 업데이트는 진행
    }

    @Test
    @DisplayName("게시물 업데이트 - 이미지를 제거하는 경우")
    void updatePost_RemoveImage_Success() {
        // Given
        String oldImageUrl = "old-image.jpg";
        String emptyImageUrl = "";

        when(mockPost.getThumbImageUrl()).thenReturn(oldImageUrl);
        when(mockRequestDto.getTitle()).thenReturn("수정된 제목");
        when(mockRequestDto.getContent()).thenReturn("수정된 내용");
        when(mockRequestDto.getThumbImageUrl()).thenReturn(emptyImageUrl);

        // When
        postUpdater.updatePost(mockPost, mockRequestDto);

        // Then
        verify(uploadService).deleteImageFromStorage(oldImageUrl);
        verify(mockPost).update("수정된 제목", "수정된 내용", emptyImageUrl);
    }

    @Test
    @DisplayName("게시물 업데이트 - 같은 이미지 URL인 경우")
    void updatePost_SameImageUrl_NoImageDeletion() {
        // Given
        String sameImageUrl = "same-image.jpg";

        when(mockPost.getThumbImageUrl()).thenReturn(sameImageUrl);
        when(mockRequestDto.getTitle()).thenReturn("수정된 제목");
        when(mockRequestDto.getContent()).thenReturn("수정된 내용");
        when(mockRequestDto.getThumbImageUrl()).thenReturn(sameImageUrl);

        // When
        postUpdater.updatePost(mockPost, mockRequestDto);

        // Then
        verify(mockPost).update("수정된 제목", "수정된 내용", sameImageUrl);
        verifyNoInteractions(uploadService); // 같은 URL이므로 삭제하지 않음
    }

    @Test
    @DisplayName("게시물 업데이트 - 제목만 변경")
    void updatePost_TitleOnly_Success() {
        // Given
        String imageUrl = "image.jpg";

        when(mockPost.getThumbImageUrl()).thenReturn(imageUrl);
        when(mockRequestDto.getTitle()).thenReturn("새로운 제목");
        when(mockRequestDto.getContent()).thenReturn("기존 내용");
        when(mockRequestDto.getThumbImageUrl()).thenReturn(imageUrl);

        // When
        postUpdater.updatePost(mockPost, mockRequestDto);

        // Then
        verify(mockPost).update("새로운 제목", "기존 내용", imageUrl);
        verifyNoInteractions(uploadService);
    }

    @Test
    @DisplayName("게시물 업데이트 - 내용만 변경")
    void updatePost_ContentOnly_Success() {
        // Given
        String imageUrl = "image.jpg";

        when(mockPost.getThumbImageUrl()).thenReturn(imageUrl);
        when(mockRequestDto.getTitle()).thenReturn("기존 제목");
        when(mockRequestDto.getContent()).thenReturn("새로운 내용");
        when(mockRequestDto.getThumbImageUrl()).thenReturn(imageUrl);

        // When
        postUpdater.updatePost(mockPost, mockRequestDto);

        // Then
        verify(mockPost).update("기존 제목", "새로운 내용", imageUrl);
        verifyNoInteractions(uploadService);
    }

    @Test
    @DisplayName("게시물 업데이트 - 모든 필드 변경")
    void updatePost_AllFieldsChanged_Success() {
        // Given
        String oldImageUrl = "old-image.jpg";
        String newImageUrl = "new-image.jpg";

        when(mockPost.getThumbImageUrl()).thenReturn(oldImageUrl);
        when(mockPost.getPostId()).thenReturn(1L);
        when(mockRequestDto.getTitle()).thenReturn("완전히 새로운 제목");
        when(mockRequestDto.getContent()).thenReturn("완전히 새로운 내용");
        when(mockRequestDto.getThumbImageUrl()).thenReturn(newImageUrl);

        // When
        postUpdater.updatePost(mockPost, mockRequestDto);

        // Then
        verify(uploadService).deleteImageFromStorage(oldImageUrl);
        verify(mockPost).update("완전히 새로운 제목", "완전히 새로운 내용", newImageUrl);
    }

    @Test
    @DisplayName("게시물 업데이트 - null 값들 처리")
    void updatePost_WithNullValues_Success() {
        // Given
        when(mockPost.getThumbImageUrl()).thenReturn(null);
        when(mockRequestDto.getTitle()).thenReturn(null);
        when(mockRequestDto.getContent()).thenReturn(null);
        when(mockRequestDto.getThumbImageUrl()).thenReturn(null);

        // When
        postUpdater.updatePost(mockPost, mockRequestDto);

        // Then
        verify(mockPost).update(null, null, null);
        verifyNoInteractions(uploadService);
    }

    @Test
    @DisplayName("게시물 업데이트 - 빈 문자열 값들 처리")
    void updatePost_WithEmptyValues_Success() {
        // Given
        when(mockPost.getThumbImageUrl()).thenReturn("");
        when(mockRequestDto.getTitle()).thenReturn("");
        when(mockRequestDto.getContent()).thenReturn("");
        when(mockRequestDto.getThumbImageUrl()).thenReturn("");

        // When
        postUpdater.updatePost(mockPost, mockRequestDto);

        // Then
        verify(mockPost).update("", "", "");
        verifyNoInteractions(uploadService);
    }
}