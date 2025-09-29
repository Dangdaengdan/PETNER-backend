package com.example.petner.domain.comment.service;

import com.example.petner.domain.comment.dto.request.CommentCreateRequestDto;
import com.example.petner.domain.comment.dto.request.CommentUpdateRequestDto;
import com.example.petner.domain.comment.dto.response.CommentDeleteResponseDto;
import com.example.petner.domain.comment.dto.response.CommentResponseDto;
import com.example.petner.domain.comment.entity.Comment;
import com.example.petner.domain.comment.repository.CommentRepository;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.post.entity.Post;
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
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentValidator commentValidator;

    @Mock
    private CommentQueryHelper commentQueryHelper;

    @InjectMocks
    private CommentService commentService;

    private Post mockPost;
    private Member mockMember;
    private Comment mockComment;
    private Comment mockParentComment;
    private CommentCreateRequestDto createRequestDto;
    private CommentUpdateRequestDto updateRequestDto;

    @BeforeEach
    void setUp() {
        mockPost = createMockPost();
        mockMember = createMockMember();
        mockParentComment = createMockParentComment();
        mockComment = createMockComment();
        createRequestDto = createMockCreateRequestDto();
        updateRequestDto = createMockUpdateRequestDto();
    }

    @Test
    @DisplayName("댓글 생성 성공")
    void createComment_Success() {
        // Given
        Long postId = 1L;
        Long currentUserId = 1L;

        doNothing().when(commentValidator).validateCommentRequest(createRequestDto);
        when(commentValidator.validateAndGetPost(postId)).thenReturn(mockPost);
        when(commentValidator.validateAndGetMember(currentUserId)).thenReturn(mockMember);
        when(commentValidator.validateAndGetParentComment(null, postId)).thenReturn(null);
        when(commentRepository.save(any(Comment.class))).thenReturn(mockComment);

        // When
        CommentResponseDto result = commentService.createComment(postId, currentUserId, createRequestDto);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getCommentId());
        assertEquals("테스트 댓글 내용", result.getContent());

        verify(commentValidator).validateCommentRequest(createRequestDto);
        verify(commentValidator).validateAndGetPost(postId);
        verify(commentValidator).validateAndGetMember(currentUserId);
        verify(commentValidator).validateAndGetParentComment(null, postId);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("대댓글 생성 성공")
    void createReply_Success() {
        // Given
        Long postId = 1L;
        Long currentUserId = 1L;
        CommentCreateRequestDto replyRequest = createMockReplyRequestDto();
        Comment mockReply = createMockReply();

        doNothing().when(commentValidator).validateCommentRequest(replyRequest);
        when(commentValidator.validateAndGetPost(postId)).thenReturn(mockPost);
        when(commentValidator.validateAndGetMember(currentUserId)).thenReturn(mockMember);
        when(commentValidator.validateAndGetParentComment(1L, postId)).thenReturn(mockParentComment);
        when(commentRepository.save(any(Comment.class))).thenReturn(mockReply);

        // When
        CommentResponseDto result = commentService.createComment(postId, currentUserId, replyRequest);

        // Then
        assertNotNull(result);
        assertEquals(2L, result.getCommentId());
        assertEquals("대댓글 내용", result.getContent());
        assertEquals(1L, result.getParentCommentId());

        verify(commentValidator).validateAndGetParentComment(1L, postId);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 생성 실패 - 게시물 없음")
    void createComment_PostNotFound() {
        // Given
        Long postId = 999L;
        Long currentUserId = 1L;

        doNothing().when(commentValidator).validateCommentRequest(createRequestDto);
        when(commentValidator.validateAndGetPost(postId))
                .thenThrow(new PostException(ErrorCode.POST_NOT_FOUND));

        // When & Then
        PostException exception = assertThrows(PostException.class,
                () -> commentService.createComment(postId, currentUserId, createRequestDto));

        assertEquals(ErrorCode.POST_NOT_FOUND, exception.getErrorCode());
        verify(commentValidator).validateCommentRequest(createRequestDto);
        verify(commentValidator).validateAndGetPost(postId);
        verifyNoInteractions(commentRepository);
    }

    @Test
    @DisplayName("게시물 댓글 조회 성공")
    void getCommentsByPost_Success() {
        // Given
        Long postId = 1L;
        List<Comment> allComments = Arrays.asList(mockComment, mockParentComment);
        List<Comment> parentComments = List.of(mockParentComment);
        List<Comment> replies = List.of(mockComment);
        List<CommentResponseDto> hierarchyResult = createMockHierarchyResult();

        when(commentValidator.validateAndGetPost(postId)).thenReturn(mockPost);
        when(commentRepository.findByPostOrderByCreatedAtAsc(mockPost)).thenReturn(allComments);
        when(commentQueryHelper.filterParentComments(allComments)).thenReturn(parentComments);
        when(commentQueryHelper.filterReplies(allComments)).thenReturn(replies);
        when(commentQueryHelper.buildCommentHierarchy(parentComments, replies)).thenReturn(hierarchyResult);

        // When
        List<CommentResponseDto> result = commentService.getCommentsByPost(postId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getCommentId());

        verify(commentValidator).validateAndGetPost(postId);
        verify(commentRepository).findByPostOrderByCreatedAtAsc(mockPost);
        verify(commentQueryHelper).filterParentComments(allComments);
        verify(commentQueryHelper).filterReplies(allComments);
        verify(commentQueryHelper).buildCommentHierarchy(parentComments, replies);
    }

    @Test
    @DisplayName("게시물 댓글 조회 - 빈 목록")
    void getCommentsByPost_EmptyList() {
        // Given
        Long postId = 1L;
        List<Comment> emptyComments = List.of();

        when(commentValidator.validateAndGetPost(postId)).thenReturn(mockPost);
        when(commentRepository.findByPostOrderByCreatedAtAsc(mockPost)).thenReturn(emptyComments);
        when(commentQueryHelper.filterParentComments(emptyComments)).thenReturn(List.of());
        when(commentQueryHelper.filterReplies(emptyComments)).thenReturn(List.of());
        when(commentQueryHelper.buildCommentHierarchy(List.of(), List.of())).thenReturn(List.of());

        // When
        List<CommentResponseDto> result = commentService.getCommentsByPost(postId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(commentValidator).validateAndGetPost(postId);
        verify(commentRepository).findByPostOrderByCreatedAtAsc(mockPost);
    }

    @Test
    @DisplayName("게시물 댓글 페이징 조회 성공")
    void getCommentsByPostWithPaging_Success() {
        // Given
        Long postId = 1L;
        int page = 0;
        int size = 20;
        List<Comment> comments = Arrays.asList(mockComment, mockParentComment);
        Page<Comment> commentsPage = new PageImpl<>(comments);
        List<Long> parentCommentIds = List.of(1L);
        List<Comment> replies = List.of(mockComment);
        List<CommentResponseDto> hierarchyResult = createMockHierarchyResult();

        doNothing().when(commentValidator).validatePagingParams(page, size);
        when(commentValidator.validateAndGetPost(postId)).thenReturn(mockPost);
        when(commentRepository.findByPostWithPaging(eq(mockPost), any(Pageable.class))).thenReturn(commentsPage);
        when(commentQueryHelper.extractParentCommentIds(comments)).thenReturn(parentCommentIds);
        when(commentRepository.findRepliesByParentCommentIds(parentCommentIds)).thenReturn(replies);
        when(commentQueryHelper.buildCommentHierarchy(comments, replies)).thenReturn(hierarchyResult);

        // When
        Page<CommentResponseDto> result = commentService.getCommentsByPostWithPaging(postId, page, size);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());

        verify(commentValidator).validatePagingParams(page, size);
        verify(commentValidator).validateAndGetPost(postId);
        verify(commentRepository).findByPostWithPaging(eq(mockPost), any(Pageable.class));
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void updateComment_Success() {
        // Given
        Long postId = 1L;
        Long commentId = 1L;
        Long currentUserId = 1L;

        when(commentValidator.validateCommentPermission(postId, commentId, currentUserId))
                .thenReturn(mockComment);
        doNothing().when(mockComment).update("수정된 댓글 내용");

        // When
        CommentResponseDto result = commentService.updateComment(postId, commentId, currentUserId, updateRequestDto);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getCommentId());

        verify(commentValidator).validateCommentPermission(postId, commentId, currentUserId);
        verify(mockComment).update("수정된 댓글 내용");
    }

    @Test
    @DisplayName("댓글 수정 실패 - 권한 없음")
    void updateComment_AccessDenied() {
        // Given
        Long postId = 1L;
        Long commentId = 1L;
        Long currentUserId = 2L; // 다른 사용자

        when(commentValidator.validateCommentPermission(postId, commentId, currentUserId))
                .thenThrow(new CommentException(ErrorCode.COMMENT_ACCESS_DENIED));

        // When & Then
        CommentException exception = assertThrows(CommentException.class,
                () -> commentService.updateComment(postId, commentId, currentUserId, updateRequestDto));

        assertEquals(ErrorCode.COMMENT_ACCESS_DENIED, exception.getErrorCode());
        verify(commentValidator).validateCommentPermission(postId, commentId, currentUserId);
    }

    @Test
    @DisplayName("댓글 삭제 성공 - 대댓글 없음")
    void deleteComment_Success_NoReplies() {
        // Given
        Long postId = 1L;
        Long commentId = 1L;
        Long currentUserId = 1L;

        when(commentValidator.validateCommentPermission(postId, commentId, currentUserId))
                .thenReturn(mockComment);
        when(commentRepository.existsActiveReplies(mockComment)).thenReturn(false);
        doNothing().when(commentRepository).delete(mockComment);

        // When
        CommentDeleteResponseDto result = commentService.deleteComment(postId, commentId, currentUserId);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(commentId, result.getCommentId());
        assertEquals(currentUserId, result.getMemberId());

        verify(commentValidator).validateCommentPermission(postId, commentId, currentUserId);
        verify(commentRepository).existsActiveReplies(mockComment);
        verify(commentRepository).delete(mockComment);
    }

    @Test
    @DisplayName("댓글 삭제 성공 - 대댓글 있음 (소프트 삭제)")
    void deleteComment_Success_HasReplies() {
        // Given
        Long postId = 1L;
        Long commentId = 1L;
        Long currentUserId = 1L;

        when(commentValidator.validateCommentPermission(postId, commentId, currentUserId))
                .thenReturn(mockComment);
        when(commentRepository.existsActiveReplies(mockComment)).thenReturn(true);
        doNothing().when(mockComment).delete();

        // When
        CommentDeleteResponseDto result = commentService.deleteComment(postId, commentId, currentUserId);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());

        verify(commentValidator).validateCommentPermission(postId, commentId, currentUserId);
        verify(commentRepository).existsActiveReplies(mockComment);
        verify(mockComment).delete(); // 소프트 삭제
        verify(commentRepository, never()).delete(mockComment); // 물리적 삭제 안됨
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 댓글 없음")
    void deleteComment_CommentNotFound() {
        // Given
        Long postId = 1L;
        Long commentId = 999L;
        Long currentUserId = 1L;

        when(commentValidator.validateCommentPermission(postId, commentId, currentUserId))
                .thenThrow(new CommentException(ErrorCode.COMMENT_NOT_FOUND));

        // When & Then
        CommentException exception = assertThrows(CommentException.class,
                () -> commentService.deleteComment(postId, commentId, currentUserId));

        assertEquals(ErrorCode.COMMENT_NOT_FOUND, exception.getErrorCode());
        verify(commentValidator).validateCommentPermission(postId, commentId, currentUserId);
        verifyNoInteractions(commentRepository);
    }

    @Test
    @DisplayName("페이징 조회 - 대댓글 없는 경우")
    void getCommentsByPostWithPaging_NoReplies() {
        // Given
        Long postId = 1L;
        int page = 0;
        int size = 20;
        List<Comment> comments = List.of(mockParentComment);
        Page<Comment> commentsPage = new PageImpl<>(comments);
        List<Long> emptyParentIds = List.of();

        doNothing().when(commentValidator).validatePagingParams(page, size);
        when(commentValidator.validateAndGetPost(postId)).thenReturn(mockPost);
        when(commentRepository.findByPostWithPaging(eq(mockPost), any(Pageable.class))).thenReturn(commentsPage);
        when(commentQueryHelper.extractParentCommentIds(comments)).thenReturn(emptyParentIds);
        when(commentQueryHelper.buildCommentHierarchy(comments, List.of())).thenReturn(List.of());

        // When
        Page<CommentResponseDto> result = commentService.getCommentsByPostWithPaging(postId, page, size);

        // Then
        assertNotNull(result);
        verify(commentValidator).validatePagingParams(page, size);
        verify(commentRepository, never()).findRepliesByParentCommentIds(any());
    }

    @Test
    @DisplayName("댓글 생성 - 저장소 예외 처리")
    void createComment_RepositoryException() {
        // Given
        Long postId = 1L;
        Long currentUserId = 1L;

        doNothing().when(commentValidator).validateCommentRequest(createRequestDto);
        when(commentValidator.validateAndGetPost(postId)).thenReturn(mockPost);
        when(commentValidator.validateAndGetMember(currentUserId)).thenReturn(mockMember);
        when(commentValidator.validateAndGetParentComment(null, postId)).thenReturn(null);
        when(commentRepository.save(any(Comment.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class,
                () -> commentService.createComment(postId, currentUserId, createRequestDto));

        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("페이징 파라미터 검증")
    void getCommentsByPostWithPaging_ValidatePagingParams() {
        // Given
        Long postId = 1L;
        int invalidPage = -1;
        int invalidSize = 0;

        doThrow(new CommentException(ErrorCode.INVALID_PAGING_PARAMS))
                .when(commentValidator).validatePagingParams(invalidPage, invalidSize);

        // When & Then
        CommentException exception = assertThrows(CommentException.class,
                () -> commentService.getCommentsByPostWithPaging(postId, invalidPage, invalidSize));

        assertEquals(ErrorCode.INVALID_PAGING_PARAMS, exception.getErrorCode());
        verify(commentValidator).validatePagingParams(invalidPage, invalidSize);
    }

    // Helper methods
    private Post createMockPost() {
        Post post = mock(Post.class);
        when(post.getPostId()).thenReturn(1L);
        when(post.getTitle()).thenReturn("테스트 게시물");
        return post;
    }

    private Member createMockMember() {
        Member member = mock(Member.class);
        when(member.getMemberId()).thenReturn(1L);
        when(member.getNickname()).thenReturn("testUser");
        when(member.getEmail()).thenReturn("test@example.com");
        return member;
    }

    private Comment createMockParentComment() {
        Comment comment = mock(Comment.class);
        when(comment.getCommentId()).thenReturn(1L);
        when(comment.getContent()).thenReturn("부모 댓글");
        when(comment.getMember()).thenReturn(mockMember);
        when(comment.getPost()).thenReturn(mockPost);
        when(comment.getParentComment()).thenReturn(null);
        when(comment.getCreatedAt()).thenReturn(LocalDateTime.now().minusHours(2));
        when(comment.getUpdatedAt()).thenReturn(LocalDateTime.now().minusHours(2));
        when(comment.getDeletedAt()).thenReturn(null);
        return comment;
    }

    private Comment createMockComment() {
        Comment comment = mock(Comment.class);
        when(comment.getCommentId()).thenReturn(1L);
        when(comment.getContent()).thenReturn("테스트 댓글 내용");
        when(comment.getMember()).thenReturn(mockMember);
        when(comment.getPost()).thenReturn(mockPost);
        when(comment.getParentComment()).thenReturn(null);
        when(comment.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(comment.getUpdatedAt()).thenReturn(LocalDateTime.now());
        when(comment.getDeletedAt()).thenReturn(null);
        return comment;
    }

    private Comment createMockReply() {
        Comment reply = mock(Comment.class);
        when(reply.getCommentId()).thenReturn(2L);
        when(reply.getContent()).thenReturn("대댓글 내용");
        when(reply.getMember()).thenReturn(mockMember);
        when(reply.getPost()).thenReturn(mockPost);
        when(reply.getParentComment()).thenReturn(mockParentComment);
        when(reply.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(reply.getUpdatedAt()).thenReturn(LocalDateTime.now());
        when(reply.getDeletedAt()).thenReturn(null);
        return reply;
    }

    private CommentCreateRequestDto createMockCreateRequestDto() {
        CommentCreateRequestDto mock = mock(CommentCreateRequestDto.class);
        when(mock.getContent()).thenReturn("테스트 댓글 내용");
        when(mock.getParentCommentId()).thenReturn(null);
        return mock;
    }

    private CommentCreateRequestDto createMockReplyRequestDto() {
        CommentCreateRequestDto mock = mock(CommentCreateRequestDto.class);
        when(mock.getContent()).thenReturn("대댓글 내용");
        when(mock.getParentCommentId()).thenReturn(1L);
        return mock;
    }

    private CommentUpdateRequestDto createMockUpdateRequestDto() {
        CommentUpdateRequestDto mock = mock(CommentUpdateRequestDto.class);
        when(mock.getContent()).thenReturn("수정된 댓글 내용");
        return mock;
    }

    private List<CommentResponseDto> createMockHierarchyResult() {
        CommentResponseDto dto = mock(CommentResponseDto.class);
        when(dto.getCommentId()).thenReturn(1L);
        when(dto.getContent()).thenReturn("부모 댓글");
        when(dto.getParentCommentId()).thenReturn(null);
        when(dto.getReplies()).thenReturn(List.of());
        return List.of(dto);
    }
}