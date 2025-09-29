package com.example.petner.domain.comment.service;

import com.example.petner.domain.comment.dto.request.CommentCreateRequestDto;
import com.example.petner.domain.comment.entity.Comment;
import com.example.petner.domain.comment.repository.CommentRepository;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.member.repository.MemberRepository;
import com.example.petner.domain.post.entity.Post;
import com.example.petner.domain.post.repository.PostRepository;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.CommentException;
import com.example.petner.global.exception.customException.MemberException;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommentValidatorTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private CommentValidator commentValidator;

    private Post mockPost;
    private Member mockMember;
    private Comment mockComment;
    private Comment mockParentComment;
    private CommentCreateRequestDto mockRequest;

    @BeforeEach
    void setUp() {
        mockPost = createMockPost();
        mockMember = createMockMember();
        mockParentComment = createMockParentComment();
        mockComment = createMockComment();
        mockRequest = createMockRequest();
    }

    @Test
    @DisplayName("댓글 생성 요청 검증 성공")
    void validateCommentRequest_Success() {
        // Given
        CommentCreateRequestDto validRequest = mock(CommentCreateRequestDto.class);
        when(validRequest.getContent()).thenReturn("유효한 댓글 내용");

        // When & Then
        assertDoesNotThrow(() -> commentValidator.validateCommentRequest(validRequest));
    }

    @Test
    @DisplayName("댓글 생성 요청 검증 실패 - null 내용")
    void validateCommentRequest_NullContent() {
        // Given
        CommentCreateRequestDto invalidRequest = mock(CommentCreateRequestDto.class);
        when(invalidRequest.getContent()).thenReturn(null);

        // When & Then
        CommentException exception = assertThrows(CommentException.class,
                () -> commentValidator.validateCommentRequest(invalidRequest));

        assertEquals(ErrorCode.COMMENT_INVALID_REQUEST, exception.getErrorCode());
    }

    @Test
    @DisplayName("댓글 생성 요청 검증 실패 - 빈 내용")
    void validateCommentRequest_EmptyContent() {
        // Given
        CommentCreateRequestDto invalidRequest = mock(CommentCreateRequestDto.class);
        when(invalidRequest.getContent()).thenReturn("   ");

        // When & Then
        CommentException exception = assertThrows(CommentException.class,
                () -> commentValidator.validateCommentRequest(invalidRequest));

        assertEquals(ErrorCode.COMMENT_INVALID_REQUEST, exception.getErrorCode());
    }

    @Test
    @DisplayName("게시물 검증 및 조회 성공")
    void validateAndGetPost_Success() {
        // Given
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));

        // When
        Post result = commentValidator.validateAndGetPost(postId);

        // Then
        assertNotNull(result);
        assertEquals(mockPost, result);
        assertEquals(1L, result.getPostId());

        verify(postRepository).findById(postId);
    }

    @Test
    @DisplayName("게시물 검증 실패 - 게시물 없음")
    void validateAndGetPost_PostNotFound() {
        // Given
        Long postId = 999L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // When & Then
        PostException exception = assertThrows(PostException.class,
                () -> commentValidator.validateAndGetPost(postId));

        assertEquals(ErrorCode.POST_NOT_FOUND, exception.getErrorCode());
        verify(postRepository).findById(postId);
    }

    @Test
    @DisplayName("회원 검증 및 조회 성공")
    void validateAndGetMember_Success() {
        // Given
        Long memberId = 1L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));

        // When
        Member result = commentValidator.validateAndGetMember(memberId);

        // Then
        assertNotNull(result);
        assertEquals(mockMember, result);
        assertEquals(1L, result.getMemberId());

        verify(memberRepository).findById(memberId);
    }

    @Test
    @DisplayName("회원 검증 실패 - 회원 없음")
    void validateAndGetMember_MemberNotFound() {
        // Given
        Long memberId = 999L;
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // When & Then
        MemberException exception = assertThrows(MemberException.class,
                () -> commentValidator.validateAndGetMember(memberId));

        assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());
        verify(memberRepository).findById(memberId);
    }

    @Test
    @DisplayName("부모 댓글 검증 성공 - 부모 댓글 없음")
    void validateAndGetParentComment_Success_NoParent() {
        // Given
        Long parentCommentId = null;
        Long postId = 1L;

        // When
        Comment result = commentValidator.validateAndGetParentComment(parentCommentId, postId);

        // Then
        assertNull(result);
        verifyNoInteractions(commentRepository);
    }

    @Test
    @DisplayName("부모 댓글 검증 성공 - 유효한 부모 댓글")
    void validateAndGetParentComment_Success_ValidParent() {
        // Given
        Long parentCommentId = 1L;
        Long postId = 1L;

        when(commentRepository.findById(parentCommentId)).thenReturn(Optional.of(mockParentComment));
        when(mockParentComment.getPost()).thenReturn(mockPost);
        when(mockPost.getPostId()).thenReturn(postId);
        when(mockParentComment.isReply()).thenReturn(false);

        // When
        Comment result = commentValidator.validateAndGetParentComment(parentCommentId, postId);

        // Then
        assertNotNull(result);
        assertEquals(mockParentComment, result);

        verify(commentRepository).findById(parentCommentId);
    }

    @Test
    @DisplayName("부모 댓글 검증 실패 - 부모 댓글 없음")
    void validateAndGetParentComment_ParentNotFound() {
        // Given
        Long parentCommentId = 999L;
        Long postId = 1L;
        when(commentRepository.findById(parentCommentId)).thenReturn(Optional.empty());

        // When & Then
        CommentException exception = assertThrows(CommentException.class,
                () -> commentValidator.validateAndGetParentComment(parentCommentId, postId));

        assertEquals(ErrorCode.COMMENT_NOT_FOUND, exception.getErrorCode());
        verify(commentRepository).findById(parentCommentId);
    }

    @Test
    @DisplayName("부모 댓글 검증 실패 - 게시물 불일치")
    void validateAndGetParentComment_PostMismatch() {
        // Given
        Long parentCommentId = 1L;
        Long postId = 2L; // 다른 게시물

        when(commentRepository.findById(parentCommentId)).thenReturn(Optional.of(mockParentComment));
        when(mockParentComment.getPost()).thenReturn(mockPost);
        when(mockPost.getPostId()).thenReturn(1L); // 부모 댓글이 속한 게시물 ID

        // When & Then
        CommentException exception = assertThrows(CommentException.class,
                () -> commentValidator.validateAndGetParentComment(parentCommentId, postId));

        assertEquals(ErrorCode.COMMENT_POST_MISMATCH, exception.getErrorCode());
        verify(commentRepository).findById(parentCommentId);
    }

    @Test
    @DisplayName("부모 댓글 검증 실패 - 최대 깊이 초과")
    void validateAndGetParentComment_MaxDepthExceeded() {
        // Given
        Long parentCommentId = 1L;
        Long postId = 1L;

        when(commentRepository.findById(parentCommentId)).thenReturn(Optional.of(mockParentComment));
        when(mockParentComment.getPost()).thenReturn(mockPost);
        when(mockPost.getPostId()).thenReturn(postId);
        when(mockParentComment.isReply()).thenReturn(true); // 이미 대댓글

        // When & Then
        CommentException exception = assertThrows(CommentException.class,
                () -> commentValidator.validateAndGetParentComment(parentCommentId, postId));

        assertEquals(ErrorCode.COMMENT_MAX_DEPTH_EXCEEDED, exception.getErrorCode());
        verify(commentRepository).findById(parentCommentId);
    }

    @Test
    @DisplayName("댓글 권한 검증 성공")
    void validateCommentPermission_Success() {
        // Given
        Long postId = 1L;
        Long commentId = 1L;
        Long currentUserId = 1L;

        when(commentRepository.findByIdWithMember(commentId)).thenReturn(Optional.of(mockComment));
        when(mockComment.getPost()).thenReturn(mockPost);
        when(mockPost.getPostId()).thenReturn(postId);
        when(mockComment.getMember()).thenReturn(mockMember);
        when(mockMember.getMemberId()).thenReturn(currentUserId);

        // When
        Comment result = commentValidator.validateCommentPermission(postId, commentId, currentUserId);

        // Then
        assertNotNull(result);
        assertEquals(mockComment, result);

        verify(commentRepository).findByIdWithMember(commentId);
    }

    @Test
    @DisplayName("댓글 권한 검증 실패 - 댓글 없음")
    void validateCommentPermission_CommentNotFound() {
        // Given
        Long postId = 1L;
        Long commentId = 999L;
        Long currentUserId = 1L;

        when(commentRepository.findByIdWithMember(commentId)).thenReturn(Optional.empty());

        // When & Then
        CommentException exception = assertThrows(CommentException.class,
                () -> commentValidator.validateCommentPermission(postId, commentId, currentUserId));

        assertEquals(ErrorCode.COMMENT_NOT_FOUND, exception.getErrorCode());
        verify(commentRepository).findByIdWithMember(commentId);
    }

    @Test
    @DisplayName("댓글 권한 검증 실패 - 게시물 불일치")
    void validateCommentPermission_PostMismatch() {
        // Given
        Long postId = 2L; // 다른 게시물
        Long commentId = 1L;
        Long currentUserId = 1L;

        when(commentRepository.findByIdWithMember(commentId)).thenReturn(Optional.of(mockComment));
        when(mockComment.getPost()).thenReturn(mockPost);
        when(mockPost.getPostId()).thenReturn(1L); // 댓글이 속한 게시물 ID

        // When & Then
        CommentException exception = assertThrows(CommentException.class,
                () -> commentValidator.validateCommentPermission(postId, commentId, currentUserId));

        assertEquals(ErrorCode.COMMENT_POST_MISMATCH, exception.getErrorCode());
        verify(commentRepository).findByIdWithMember(commentId);
    }

    @Test
    @DisplayName("댓글 권한 검증 실패 - 권한 없음")
    void validateCommentPermission_AccessDenied() {
        // Given
        Long postId = 1L;
        Long commentId = 1L;
        Long currentUserId = 2L; // 다른 사용자

        when(commentRepository.findByIdWithMember(commentId)).thenReturn(Optional.of(mockComment));
        when(mockComment.getPost()).thenReturn(mockPost);
        when(mockPost.getPostId()).thenReturn(postId);
        when(mockComment.getMember()).thenReturn(mockMember);
        when(mockMember.getMemberId()).thenReturn(1L); // 댓글 작성자 ID

        // When & Then
        CommentException exception = assertThrows(CommentException.class,
                () -> commentValidator.validateCommentPermission(postId, commentId, currentUserId));

        assertEquals(ErrorCode.COMMENT_ACCESS_DENIED, exception.getErrorCode());
        verify(commentRepository).findByIdWithMember(commentId);
    }

    @Test
    @DisplayName("페이징 파라미터 검증 성공")
    void validatePagingParams_Success() {
        // Given
        int validPage = 0;
        int validSize = 20;

        // When & Then
        assertDoesNotThrow(() -> commentValidator.validatePagingParams(validPage, validSize));
    }

    @Test
    @DisplayName("페이징 파라미터 검증 실패 - 음수 페이지")
    void validatePagingParams_NegativePage() {
        // Given
        int invalidPage = -1;
        int validSize = 20;

        // When & Then
        CommentException exception = assertThrows(CommentException.class,
                () -> commentValidator.validatePagingParams(invalidPage, validSize));

        assertEquals(ErrorCode.COMMENT_INVALID_REQUEST, exception.getErrorCode());
    }

    @Test
    @DisplayName("페이징 파라미터 검증 실패 - 0 이하 크기")
    void validatePagingParams_ZeroOrNegativeSize() {
        // Given
        int validPage = 0;
        int invalidSize = 0;

        // When & Then
        CommentException exception = assertThrows(CommentException.class,
                () -> commentValidator.validatePagingParams(validPage, invalidSize));

        assertEquals(ErrorCode.COMMENT_INVALID_REQUEST, exception.getErrorCode());
    }

    @Test
    @DisplayName("페이징 파라미터 검증 실패 - 크기 초과")
    void validatePagingParams_SizeExceeded() {
        // Given
        int validPage = 0;
        int invalidSize = 101; // 최대 100 초과

        // When & Then
        CommentException exception = assertThrows(CommentException.class,
                () -> commentValidator.validatePagingParams(validPage, invalidSize));

        assertEquals(ErrorCode.COMMENT_INVALID_REQUEST, exception.getErrorCode());
    }

    @Test
    @DisplayName("페이징 파라미터 검증 - 경계값 테스트")
    void validatePagingParams_BoundaryValues() {
        // When & Then - 유효한 경계값들
        assertDoesNotThrow(() -> commentValidator.validatePagingParams(0, 1)); // 최소값
        assertDoesNotThrow(() -> commentValidator.validatePagingParams(0, 100)); // 최대값
        assertDoesNotThrow(() -> commentValidator.validatePagingParams(Integer.MAX_VALUE, 50)); // 큰 페이지 번호
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
        when(comment.isReply()).thenReturn(false);
        return comment;
    }

    private Comment createMockComment() {
        Comment comment = mock(Comment.class);
        when(comment.getCommentId()).thenReturn(1L);
        when(comment.getContent()).thenReturn("테스트 댓글");
        when(comment.getMember()).thenReturn(mockMember);
        when(comment.getPost()).thenReturn(mockPost);
        when(comment.getParentComment()).thenReturn(null);
        when(comment.isReply()).thenReturn(false);
        return comment;
    }

    private CommentCreateRequestDto createMockRequest() {
        CommentCreateRequestDto mock = mock(CommentCreateRequestDto.class);
        when(mock.getContent()).thenReturn("테스트 댓글 내용");
        when(mock.getParentCommentId()).thenReturn(null);
        return mock;
    }
}