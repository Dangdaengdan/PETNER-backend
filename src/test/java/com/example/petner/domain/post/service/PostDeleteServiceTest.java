package com.example.petner.domain.post.service;

import com.example.petner.domain.comment.repository.CommentRepository;
import com.example.petner.domain.like.repository.PostLikeRepository;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.post.dto.response.PostDeleteResponseDto;
import com.example.petner.domain.post.entity.Post;
import com.example.petner.domain.post.repository.PostRepository;
import com.example.petner.domain.upload.service.UploadService;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.PostException;
import com.example.petner.search.event.PostEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PostDeleteServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private UploadService uploadService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PostDeleteService postDeleteService;

    private Post mockPost;
    private Member mockMember;

    @BeforeEach
    void setUp() {
        mockMember = createMockMember();
        mockPost = createMockPost();
    }

    @Test
    @DisplayName("게시물 삭제 성공")
    void deletePost_Success() {
        // Given
        Long postId = 1L;
        Long authorId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));
        when(mockPost.getAuthor()).thenReturn(mockMember);
        when(mockMember.getMemberId()).thenReturn(authorId);

        // When
        PostDeleteResponseDto result = postDeleteService.deletePost(postId, authorId);

        // Then
        assertNotNull(result);
        assertEquals(postId, result.getPostId());
        assertEquals(authorId, result.getMemberId());
        assertTrue(result.isSuccess());

        verify(postRepository).findById(postId);
        verify(postLikeRepository).deleteByPost(mockPost);
        verify(commentRepository).deleteRepliesByPost(mockPost);
        verify(commentRepository).flush();
        verify(commentRepository).deleteParentCommentsByPost(mockPost);
        verify(postRepository).delete(mockPost);
        verify(eventPublisher).publishEvent(any(PostEvent.class));
    }

    @Test
    @DisplayName("게시물 삭제 성공 - 썸네일 이미지 있음")
    void deletePost_WithThumbnailImage_Success() {
        // Given
        Long postId = 1L;
        Long authorId = 1L;
        String thumbnailUrl = "test-image.jpg";

        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));
        when(mockPost.getAuthor()).thenReturn(mockMember);
        when(mockMember.getMemberId()).thenReturn(authorId);
        when(mockPost.getThumbImageUrl()).thenReturn(thumbnailUrl);

        // When
        PostDeleteResponseDto result = postDeleteService.deletePost(postId, authorId);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());

        verify(uploadService).deleteImageFromStorage(thumbnailUrl);
        verify(postRepository).delete(mockPost);
        verify(eventPublisher).publishEvent(any(PostEvent.class));
    }

    @Test
    @DisplayName("게시물 삭제 실패 - 게시물 없음")
    void deletePost_PostNotFound() {
        // Given
        Long postId = 999L;
        Long authorId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When & Then
        PostException exception = assertThrows(PostException.class,
                () -> postDeleteService.deletePost(postId, authorId));

        assertEquals(ErrorCode.POST_NOT_FOUND, exception.getErrorCode());
        verify(postRepository).findById(postId);
        verifyNoInteractions(postLikeRepository, commentRepository, uploadService, eventPublisher);
    }

    @Test
    @DisplayName("게시물 삭제 실패 - 권한 없음")
    void deletePost_AccessDenied() {
        // Given
        Long postId = 1L;
        Long authorId = 1L;
        Long otherUserId = 2L;

        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));
        when(mockPost.getAuthor()).thenReturn(mockMember);
        when(mockMember.getMemberId()).thenReturn(authorId);

        // When & Then
        PostException exception = assertThrows(PostException.class,
                () -> postDeleteService.deletePost(postId, otherUserId));

        assertEquals(ErrorCode.POST_ACCESS_DENIED, exception.getErrorCode());
        verify(postRepository).findById(postId);
        verifyNoInteractions(postLikeRepository, commentRepository, uploadService, eventPublisher);
    }

    @Test
    @DisplayName("게시물 삭제 시 이미지 삭제 실패해도 삭제 진행")
    void deletePost_ImageDeleteFailure_ContinuesExecution() {
        // Given
        Long postId = 1L;
        Long authorId = 1L;
        String thumbnailUrl = "test-image.jpg";

        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));
        when(mockPost.getAuthor()).thenReturn(mockMember);
        when(mockMember.getMemberId()).thenReturn(authorId);
        when(mockPost.getThumbImageUrl()).thenReturn(thumbnailUrl);
        doThrow(new RuntimeException("Storage error")).when(uploadService).deleteImageFromStorage(thumbnailUrl);

        // When
        PostDeleteResponseDto result = postDeleteService.deletePost(postId, authorId);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());

        verify(uploadService).deleteImageFromStorage(thumbnailUrl);
        verify(postRepository).delete(mockPost);
        verify(eventPublisher).publishEvent(any(PostEvent.class));
    }

    @Test
    @DisplayName("게시물 삭제 - 썸네일 이미지가 null인 경우")
    void deletePost_NullThumbnailImage_Success() {
        // Given
        Long postId = 1L;
        Long authorId = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));
        when(mockPost.getAuthor()).thenReturn(mockMember);
        when(mockMember.getMemberId()).thenReturn(authorId);
        when(mockPost.getThumbImageUrl()).thenReturn(null);

        // When
        PostDeleteResponseDto result = postDeleteService.deletePost(postId, authorId);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());

        verifyNoInteractions(uploadService); // 이미지 삭제 호출되지 않음
        verify(postRepository).delete(mockPost);
        verify(eventPublisher).publishEvent(any(PostEvent.class));
    }

    @Test
    @DisplayName("게시물 삭제 - 썸네일 이미지가 빈 문자열인 경우")
    void deletePost_BlankThumbnailImage_Success() {
        // Given
        Long postId = 1L;
        Long authorId = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));
        when(mockPost.getAuthor()).thenReturn(mockMember);
        when(mockMember.getMemberId()).thenReturn(authorId);
        when(mockPost.getThumbImageUrl()).thenReturn("");

        // When
        PostDeleteResponseDto result = postDeleteService.deletePost(postId, authorId);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());

        verifyNoInteractions(uploadService); // 이미지 삭제 호출되지 않음
        verify(postRepository).delete(mockPost);
        verify(eventPublisher).publishEvent(any(PostEvent.class));
    }

    @Test
    @DisplayName("관련 데이터 삭제 순서 확인")
    void deletePost_VerifyDeletionOrder() {
        // Given
        Long postId = 1L;
        Long authorId = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));
        when(mockPost.getAuthor()).thenReturn(mockMember);
        when(mockMember.getMemberId()).thenReturn(authorId);

        // When
        postDeleteService.deletePost(postId, authorId);

        // Then - 삭제 순서 확인
        var inOrder = inOrder(postLikeRepository, commentRepository, postRepository, eventPublisher);

        inOrder.verify(postLikeRepository).deleteByPost(mockPost);
        inOrder.verify(commentRepository).deleteRepliesByPost(mockPost); // 대댓글 먼저
        inOrder.verify(commentRepository).flush();
        inOrder.verify(commentRepository).deleteParentCommentsByPost(mockPost); // 부모댓글 나중에
        inOrder.verify(postRepository).delete(mockPost);
        inOrder.verify(eventPublisher).publishEvent(any(PostEvent.class));
    }

    @Test
    @DisplayName("권한 검증 메서드 테스트")
    void validateAuthor_Success() {
        // Given
        Long postId = 1L;
        Long correctAuthorId = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));
        when(mockPost.getAuthor()).thenReturn(mockMember);
        when(mockMember.getMemberId()).thenReturn(correctAuthorId);

        // When & Then - 권한이 있는 경우 예외가 발생하지 않아야 함
        assertDoesNotThrow(() -> postDeleteService.deletePost(postId, correctAuthorId));
    }

    @Test
    @DisplayName("이벤트 발행 확인")
    void deletePost_PublishesCorrectEvent() {
        // Given
        Long postId = 1L;
        Long authorId = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));
        when(mockPost.getAuthor()).thenReturn(mockMember);
        when(mockMember.getMemberId()).thenReturn(authorId);

        // When
        postDeleteService.deletePost(postId, authorId);

        // Then
        verify(eventPublisher).publishEvent(any(PostEvent.class));
    }

    // Helper methods
    private Member createMockMember() {
        Member member = mock(Member.class);
        when(member.getMemberId()).thenReturn(1L);
        when(member.getNickname()).thenReturn("testUser");
        return member;
    }

    private Post createMockPost() {
        Post post = mock(Post.class);
        when(post.getPostId()).thenReturn(1L);
        when(post.getTitle()).thenReturn("테스트 게시물");
        when(post.getThumbImageUrl()).thenReturn("test-image.jpg");
        when(post.getAuthor()).thenReturn(mockMember);
        return post;
    }
}