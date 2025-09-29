package com.example.petner.domain.comment.controller;

import com.example.petner.domain.comment.dto.request.CommentCreateRequestDto;
import com.example.petner.domain.comment.dto.request.CommentUpdateRequestDto;
import com.example.petner.domain.comment.dto.response.CommentDeleteResponseDto;
import com.example.petner.domain.comment.dto.response.CommentResponseDto;
import com.example.petner.domain.comment.service.CommentService;
import com.example.petner.global.dto.SessionUser;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.CommentException;
import com.example.petner.global.exception.customException.PostException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private SessionUser sessionUser;
    private CommentCreateRequestDto createRequestDto;
    private CommentUpdateRequestDto updateRequestDto;
    private CommentResponseDto commentResponseDto;

    @BeforeEach
    void setUp() {
        sessionUser = SessionUser.builder()
                .memberId(1L)
                .email("test@example.com")
                .nickname("testUser")
                .build();

        createRequestDto = createMockCreateRequestDto();
        updateRequestDto = createMockUpdateRequestDto();
        commentResponseDto = createMockCommentResponseDto();
    }

    @Test
    @DisplayName("댓글 생성 성공")
    void createComment_Success() {
        // Given
        Long postId = 1L;
        when(commentService.createComment(eq(postId), eq(1L), any(CommentCreateRequestDto.class)))
                .thenReturn(commentResponseDto);

        // When
        ResponseEntity<CommentResponseDto> response = commentController.createComment(postId, createRequestDto, sessionUser);

        // Then
        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(commentResponseDto.getCommentId(), response.getBody().getCommentId());
        assertEquals(commentResponseDto.getContent(), response.getBody().getContent());

        verify(commentService).createComment(postId, 1L, createRequestDto);
    }

    @Test
    @DisplayName("댓글 생성 실패 - 게시물 없음")
    void createComment_PostNotFound() {
        // Given
        Long postId = 999L;
        when(commentService.createComment(eq(postId), eq(1L), any(CommentCreateRequestDto.class)))
                .thenThrow(new PostException(ErrorCode.POST_NOT_FOUND));

        // When & Then
        assertThrows(PostException.class,
                () -> commentController.createComment(postId, createRequestDto, sessionUser));

        verify(commentService).createComment(postId, 1L, createRequestDto);
    }

    @Test
    @DisplayName("대댓글 생성 성공")
    void createReply_Success() {
        // Given
        Long postId = 1L;
        CommentCreateRequestDto replyRequest = mock(CommentCreateRequestDto.class);
        when(replyRequest.getContent()).thenReturn("대댓글 내용");
        when(replyRequest.getParentCommentId()).thenReturn(1L);

        CommentResponseDto replyResponse = mock(CommentResponseDto.class);
        when(replyResponse.getCommentId()).thenReturn(2L);
        when(replyResponse.getContent()).thenReturn("대댓글 내용");
        when(replyResponse.getParentCommentId()).thenReturn(1L);

        when(commentService.createComment(eq(postId), eq(1L), any(CommentCreateRequestDto.class)))
                .thenReturn(replyResponse);

        // When
        ResponseEntity<CommentResponseDto> response = commentController.createComment(postId, replyRequest, sessionUser);

        // Then
        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2L, response.getBody().getCommentId());
        assertEquals("대댓글 내용", response.getBody().getContent());
        assertEquals(1L, response.getBody().getParentCommentId());

        verify(commentService).createComment(postId, 1L, replyRequest);
    }

    @Test
    @DisplayName("게시물 댓글 조회 성공 - 페이징")
    void getCommentsByPost_WithPaging_Success() {
        // Given
        Long postId = 1L;
        int page = 0;
        int size = 20;
        List<CommentResponseDto> comments = createMockCommentList();
        Page<CommentResponseDto> commentPage = new PageImpl<>(comments);

        when(commentService.getCommentsByPostWithPaging(postId, page, size)).thenReturn(commentPage);

        // When
        ResponseEntity<Page<CommentResponseDto>> response = commentController.getCommentsByPost(postId, page, size);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getContent().size());

        verify(commentService).getCommentsByPostWithPaging(postId, page, size);
    }

    @Test
    @DisplayName("게시물 댓글 전체 조회 성공")
    void getAllCommentsByPost_Success() {
        // Given
        Long postId = 1L;
        List<CommentResponseDto> comments = createMockCommentList();
        when(commentService.getCommentsByPost(postId)).thenReturn(comments);

        // When
        ResponseEntity<List<CommentResponseDto>> response = commentController.getAllCommentsByPost(postId);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        verify(commentService).getCommentsByPost(postId);
    }

    @Test
    @DisplayName("게시물 댓글 조회 - 빈 목록")
    void getCommentsByPost_EmptyList() {
        // Given
        Long postId = 1L;
        Page<CommentResponseDto> emptyPage = new PageImpl<>(List.of());
        when(commentService.getCommentsByPostWithPaging(postId, 0, 20)).thenReturn(emptyPage);

        // When
        ResponseEntity<Page<CommentResponseDto>> response = commentController.getCommentsByPost(postId, 0, 20);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getContent().isEmpty());

        verify(commentService).getCommentsByPostWithPaging(postId, 0, 20);
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void updateComment_Success() {
        // Given
        Long postId = 1L;
        Long commentId = 1L;
        CommentResponseDto updatedComment = createMockUpdatedCommentResponseDto();
        when(commentService.updateComment(postId, commentId, 1L, updateRequestDto))
                .thenReturn(updatedComment);

        // When
        ResponseEntity<CommentResponseDto> response = commentController.updateComment(postId, commentId, updateRequestDto, sessionUser);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("수정된 댓글 내용", response.getBody().getContent());

        verify(commentService).updateComment(postId, commentId, 1L, updateRequestDto);
    }

    @Test
    @DisplayName("댓글 수정 실패 - 권한 없음")
    void updateComment_AccessDenied() {
        // Given
        Long postId = 1L;
        Long commentId = 1L;
        when(commentService.updateComment(postId, commentId, 1L, updateRequestDto))
                .thenThrow(new CommentException(ErrorCode.COMMENT_ACCESS_DENIED));

        // When & Then
        assertThrows(CommentException.class,
                () -> commentController.updateComment(postId, commentId, updateRequestDto, sessionUser));

        verify(commentService).updateComment(postId, commentId, 1L, updateRequestDto);
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void deleteComment_Success() {
        // Given
        Long postId = 1L;
        Long commentId = 1L;
        CommentDeleteResponseDto deleteResponse = CommentDeleteResponseDto.success(commentId, 1L);
        when(commentService.deleteComment(postId, commentId, 1L)).thenReturn(deleteResponse);

        // When
        ResponseEntity<CommentDeleteResponseDto> response = commentController.deleteComment(postId, commentId, sessionUser);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(commentId, response.getBody().getCommentId());

        verify(commentService).deleteComment(postId, commentId, 1L);
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 댓글 없음")
    void deleteComment_CommentNotFound() {
        // Given
        Long postId = 1L;
        Long commentId = 999L;
        when(commentService.deleteComment(postId, commentId, 1L))
                .thenThrow(new CommentException(ErrorCode.COMMENT_NOT_FOUND));

        // When & Then
        assertThrows(CommentException.class,
                () -> commentController.deleteComment(postId, commentId, sessionUser));

        verify(commentService).deleteComment(postId, commentId, 1L);
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 권한 없음")
    void deleteComment_AccessDenied() {
        // Given
        Long postId = 1L;
        Long commentId = 1L;
        when(commentService.deleteComment(postId, commentId, 1L))
                .thenThrow(new CommentException(ErrorCode.COMMENT_ACCESS_DENIED));

        // When & Then
        assertThrows(CommentException.class,
                () -> commentController.deleteComment(postId, commentId, sessionUser));

        verify(commentService).deleteComment(postId, commentId, 1L);
    }

    @Test
    @DisplayName("게시물 댓글 조회 - 다양한 페이징 파라미터")
    void getCommentsByPost_VariousPagingParameters() {
        // Given
        Long postId = 1L;
        Page<CommentResponseDto> page1 = new PageImpl<>(createMockCommentList());
        Page<CommentResponseDto> page2 = new PageImpl<>(List.of(commentResponseDto));

        when(commentService.getCommentsByPostWithPaging(postId, 0, 10)).thenReturn(page1);
        when(commentService.getCommentsByPostWithPaging(postId, 1, 5)).thenReturn(page2);

        // When
        ResponseEntity<Page<CommentResponseDto>> response1 = commentController.getCommentsByPost(postId, 0, 10);
        ResponseEntity<Page<CommentResponseDto>> response2 = commentController.getCommentsByPost(postId, 1, 5);

        // Then
        assertEquals(200, response1.getStatusCodeValue());
        assertEquals(200, response2.getStatusCodeValue());
        assertEquals(2, response1.getBody().getContent().size());
        assertEquals(1, response2.getBody().getContent().size());

        verify(commentService).getCommentsByPostWithPaging(postId, 0, 10);
        verify(commentService).getCommentsByPostWithPaging(postId, 1, 5);
    }

    @Test
    @DisplayName("댓글 생성 - 서비스 예외 처리")
    void createComment_ServiceException() {
        // Given
        Long postId = 1L;
        when(commentService.createComment(eq(postId), eq(1L), any(CommentCreateRequestDto.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class,
                () -> commentController.createComment(postId, createRequestDto, sessionUser));

        verify(commentService).createComment(postId, 1L, createRequestDto);
    }

    @Test
    @DisplayName("댓글 조회 - 서비스 예외 처리")
    void getCommentsByPost_ServiceException() {
        // Given
        Long postId = 1L;
        when(commentService.getCommentsByPostWithPaging(postId, 0, 20))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class,
                () -> commentController.getCommentsByPost(postId, 0, 20));

        verify(commentService).getCommentsByPostWithPaging(postId, 0, 20);
    }

    @Test
    @DisplayName("댓글 생성 - 빈 내용")
    void createComment_EmptyContent() {
        // Given
        Long postId = 1L;
        CommentCreateRequestDto emptyRequest = mock(CommentCreateRequestDto.class);
        when(emptyRequest.getContent()).thenReturn("");

        when(commentService.createComment(eq(postId), eq(1L), any(CommentCreateRequestDto.class)))
                .thenThrow(new CommentException(ErrorCode.COMMENT_CONTENT_REQUIRED));

        // When & Then
        assertThrows(CommentException.class,
                () -> commentController.createComment(postId, emptyRequest, sessionUser));

        verify(commentService).createComment(postId, 1L, emptyRequest);
    }

    @Test
    @DisplayName("계층구조 댓글 조회 확인")
    void getCommentsByPost_HierarchicalStructure() {
        // Given
        Long postId = 1L;
        CommentResponseDto parentComment = mock(CommentResponseDto.class);
        when(parentComment.getCommentId()).thenReturn(1L);
        when(parentComment.getContent()).thenReturn("부모 댓글");
        when(parentComment.getParentCommentId()).thenReturn(null);
        when(parentComment.getReplies()).thenReturn(List.of());

        CommentResponseDto replyComment = mock(CommentResponseDto.class);
        when(replyComment.getCommentId()).thenReturn(2L);
        when(replyComment.getContent()).thenReturn("대댓글");
        when(replyComment.getParentCommentId()).thenReturn(1L);
        when(replyComment.getReplies()).thenReturn(List.of());

        List<CommentResponseDto> hierarchicalComments = Arrays.asList(parentComment);
        when(commentService.getCommentsByPost(postId)).thenReturn(hierarchicalComments);

        // When
        ResponseEntity<List<CommentResponseDto>> response = commentController.getAllCommentsByPost(postId);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        CommentResponseDto parent = response.getBody().get(0);
        assertEquals(1L, parent.getCommentId());
        assertEquals("부모 댓글", parent.getContent());
        assertNull(parent.getParentCommentId());

        verify(commentService).getCommentsByPost(postId);
    }

    // Helper methods
    private CommentCreateRequestDto createMockCreateRequestDto() {
        CommentCreateRequestDto mock = mock(CommentCreateRequestDto.class);
        when(mock.getContent()).thenReturn("테스트 댓글 내용");
        when(mock.getParentCommentId()).thenReturn(null);
        return mock;
    }

    private CommentUpdateRequestDto createMockUpdateRequestDto() {
        CommentUpdateRequestDto mock = mock(CommentUpdateRequestDto.class);
        when(mock.getContent()).thenReturn("수정된 댓글 내용");
        return mock;
    }

    private CommentResponseDto createMockCommentResponseDto() {
        CommentResponseDto mock = mock(CommentResponseDto.class);
        when(mock.getCommentId()).thenReturn(1L);
        when(mock.getContent()).thenReturn("테스트 댓글 내용");
        when(mock.getAuthorNickname()).thenReturn("testUser");
        when(mock.getParentCommentId()).thenReturn(null);
        when(mock.getReplies()).thenReturn(List.of());
        when(mock.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(mock.getUpdatedAt()).thenReturn(LocalDateTime.now());
        when(mock.isDeleted()).thenReturn(false);
        return mock;
    }

    private CommentResponseDto createMockUpdatedCommentResponseDto() {
        CommentResponseDto mock = mock(CommentResponseDto.class);
        when(mock.getCommentId()).thenReturn(1L);
        when(mock.getContent()).thenReturn("수정된 댓글 내용");
        when(mock.getAuthorNickname()).thenReturn("testUser");
        when(mock.getParentCommentId()).thenReturn(null);
        when(mock.getReplies()).thenReturn(List.of());
        when(mock.getCreatedAt()).thenReturn(LocalDateTime.now().minusHours(1));
        when(mock.getUpdatedAt()).thenReturn(LocalDateTime.now());
        when(mock.isDeleted()).thenReturn(false);
        return mock;
    }

    private List<CommentResponseDto> createMockCommentList() {
        CommentResponseDto comment1 = mock(CommentResponseDto.class);
        when(comment1.getCommentId()).thenReturn(1L);
        when(comment1.getContent()).thenReturn("첫 번째 댓글");
        when(comment1.getAuthorNickname()).thenReturn("user1");
        when(comment1.getParentCommentId()).thenReturn(null);
        when(comment1.getReplies()).thenReturn(List.of());
        when(comment1.getCreatedAt()).thenReturn(LocalDateTime.now().minusHours(2));

        CommentResponseDto comment2 = mock(CommentResponseDto.class);
        when(comment2.getCommentId()).thenReturn(2L);
        when(comment2.getContent()).thenReturn("두 번째 댓글");
        when(comment2.getAuthorNickname()).thenReturn("user2");
        when(comment2.getParentCommentId()).thenReturn(null);
        when(comment2.getReplies()).thenReturn(List.of());
        when(comment2.getCreatedAt()).thenReturn(LocalDateTime.now().minusHours(1));

        return Arrays.asList(comment1, comment2);
    }
}