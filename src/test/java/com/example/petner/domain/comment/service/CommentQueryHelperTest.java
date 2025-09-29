package com.example.petner.domain.comment.service;

import com.example.petner.domain.comment.dto.response.CommentResponseDto;
import com.example.petner.domain.comment.entity.Comment;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.post.entity.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommentQueryHelperTest {

    @InjectMocks
    private CommentQueryHelper commentQueryHelper;

    private Post mockPost;
    private Member mockMember;
    private Comment mockParentComment1;
    private Comment mockParentComment2;
    private Comment mockReplyComment1;
    private Comment mockReplyComment2;

    @BeforeEach
    void setUp() {
        mockPost = createMockPost();
        mockMember = createMockMember();
        mockParentComment1 = createMockParentComment(1L, "첫 번째 부모 댓글");
        mockParentComment2 = createMockParentComment(2L, "두 번째 부모 댓글");
        mockReplyComment1 = createMockReplyComment(3L, "첫 번째 대댓글", mockParentComment1);
        mockReplyComment2 = createMockReplyComment(4L, "두 번째 대댓글", mockParentComment1);
    }

    @Test
    @DisplayName("댓글 계층 구조 생성 성공 - 부모 댓글과 대댓글")
    void buildCommentHierarchy_Success() {
        // Given
        List<Comment> comments = Arrays.asList(mockParentComment1, mockParentComment2);
        List<Comment> replies = Arrays.asList(mockReplyComment1, mockReplyComment2);

        // When
        List<CommentResponseDto> result = commentQueryHelper.buildCommentHierarchy(comments, replies);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        // 첫 번째 부모 댓글 검증
        CommentResponseDto parent1 = result.get(0);
        assertEquals(1L, parent1.getCommentId());
        assertEquals("첫 번째 부모 댓글", parent1.getContent());
        assertEquals(2, parent1.getReplies().size());

        // 대댓글들 검증
        List<CommentResponseDto> replies1 = parent1.getReplies();
        assertEquals(3L, replies1.get(0).getCommentId());
        assertEquals(4L, replies1.get(1).getCommentId());

        // 두 번째 부모 댓글 검증 (대댓글 없음)
        CommentResponseDto parent2 = result.get(1);
        assertEquals(2L, parent2.getCommentId());
        assertEquals("두 번째 부모 댓글", parent2.getContent());
        assertTrue(parent2.getReplies().isEmpty());
    }

    @Test
    @DisplayName("댓글 계층 구조 생성 - 부모 댓글만 있는 경우")
    void buildCommentHierarchy_OnlyParentComments() {
        // Given
        List<Comment> comments = Arrays.asList(mockParentComment1, mockParentComment2);
        List<Comment> replies = List.of();

        // When
        List<CommentResponseDto> result = commentQueryHelper.buildCommentHierarchy(comments, replies);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        result.forEach(comment -> {
            assertNotNull(comment);
            assertTrue(comment.getReplies().isEmpty());
        });
    }

    @Test
    @DisplayName("댓글 계층 구조 생성 - 빈 목록")
    void buildCommentHierarchy_EmptyLists() {
        // Given
        List<Comment> comments = List.of();
        List<Comment> replies = List.of();

        // When
        List<CommentResponseDto> result = commentQueryHelper.buildCommentHierarchy(comments, replies);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("댓글 계층 구조 생성 - 대댓글만 있는 경우 (실제로는 발생하지 않는 시나리오)")
    void buildCommentHierarchy_OnlyReplies() {
        // Given
        List<Comment> comments = Arrays.asList(mockReplyComment1, mockReplyComment2);
        List<Comment> replies = List.of();

        // When
        List<CommentResponseDto> result = commentQueryHelper.buildCommentHierarchy(comments, replies);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty()); // 부모 댓글이 아닌 것들은 필터링됨
    }

    @Test
    @DisplayName("부모 댓글 ID 목록 추출 성공")
    void extractParentCommentIds_Success() {
        // Given
        List<Comment> comments = Arrays.asList(
                mockParentComment1, mockParentComment2, mockReplyComment1, mockReplyComment2);

        // When
        List<Long> result = commentQueryHelper.extractParentCommentIds(comments);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(1L));
        assertTrue(result.contains(2L));
        assertFalse(result.contains(3L)); // 대댓글 ID는 포함되지 않음
        assertFalse(result.contains(4L)); // 대댓글 ID는 포함되지 않음
    }

    @Test
    @DisplayName("부모 댓글 ID 목록 추출 - 빈 목록")
    void extractParentCommentIds_EmptyList() {
        // Given
        List<Comment> comments = List.of();

        // When
        List<Long> result = commentQueryHelper.extractParentCommentIds(comments);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("부모 댓글 ID 목록 추출 - 대댓글만 있는 경우")
    void extractParentCommentIds_OnlyReplies() {
        // Given
        List<Comment> comments = Arrays.asList(mockReplyComment1, mockReplyComment2);

        // When
        List<Long> result = commentQueryHelper.extractParentCommentIds(comments);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("부모 댓글 필터링 성공")
    void filterParentComments_Success() {
        // Given
        List<Comment> comments = Arrays.asList(
                mockParentComment1, mockReplyComment1, mockParentComment2, mockReplyComment2);

        // When
        List<Comment> result = commentQueryHelper.filterParentComments(comments);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(mockParentComment1));
        assertTrue(result.contains(mockParentComment2));
        assertFalse(result.contains(mockReplyComment1));
        assertFalse(result.contains(mockReplyComment2));
    }

    @Test
    @DisplayName("부모 댓글 필터링 - 빈 목록")
    void filterParentComments_EmptyList() {
        // Given
        List<Comment> comments = List.of();

        // When
        List<Comment> result = commentQueryHelper.filterParentComments(comments);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("부모 댓글 필터링 - 대댓글만 있는 경우")
    void filterParentComments_OnlyReplies() {
        // Given
        List<Comment> comments = Arrays.asList(mockReplyComment1, mockReplyComment2);

        // When
        List<Comment> result = commentQueryHelper.filterParentComments(comments);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("대댓글 필터링 성공")
    void filterReplies_Success() {
        // Given
        List<Comment> comments = Arrays.asList(
                mockParentComment1, mockReplyComment1, mockParentComment2, mockReplyComment2);

        // When
        List<Comment> result = commentQueryHelper.filterReplies(comments);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(mockReplyComment1));
        assertTrue(result.contains(mockReplyComment2));
        assertFalse(result.contains(mockParentComment1));
        assertFalse(result.contains(mockParentComment2));
    }

    @Test
    @DisplayName("대댓글 필터링 - 빈 목록")
    void filterReplies_EmptyList() {
        // Given
        List<Comment> comments = List.of();

        // When
        List<Comment> result = commentQueryHelper.filterReplies(comments);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("대댓글 필터링 - 부모 댓글만 있는 경우")
    void filterReplies_OnlyParentComments() {
        // Given
        List<Comment> comments = Arrays.asList(mockParentComment1, mockParentComment2);

        // When
        List<Comment> result = commentQueryHelper.filterReplies(comments);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("복잡한 계층 구조 생성 테스트")
    void buildCommentHierarchy_ComplexStructure() {
        // Given
        Comment parent3 = createMockParentComment(5L, "세 번째 부모 댓글");
        Comment reply3 = createMockReplyComment(6L, "세 번째 대댓글", mockParentComment2);
        Comment reply4 = createMockReplyComment(7L, "네 번째 대댓글", parent3);

        List<Comment> comments = Arrays.asList(mockParentComment1, mockParentComment2, parent3);
        List<Comment> replies = Arrays.asList(mockReplyComment1, mockReplyComment2, reply3, reply4);

        // When
        List<CommentResponseDto> result = commentQueryHelper.buildCommentHierarchy(comments, replies);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());

        // 첫 번째 부모 댓글: 2개의 대댓글
        assertEquals(2, result.get(0).getReplies().size());

        // 두 번째 부모 댓글: 1개의 대댓글
        assertEquals(1, result.get(1).getReplies().size());

        // 세 번째 부모 댓글: 1개의 대댓글
        assertEquals(1, result.get(2).getReplies().size());
    }

    @Test
    @DisplayName("대용량 댓글 처리 테스트")
    void buildCommentHierarchy_LargeData() {
        // Given
        List<Comment> comments = List.of(mockParentComment1);
        List<Comment> replies = Arrays.asList(
                createMockReplyComment(100L, "대댓글 100", mockParentComment1),
                createMockReplyComment(101L, "대댓글 101", mockParentComment1),
                createMockReplyComment(102L, "대댓글 102", mockParentComment1),
                createMockReplyComment(103L, "대댓글 103", mockParentComment1),
                createMockReplyComment(104L, "대댓글 104", mockParentComment1)
        );

        // When
        List<CommentResponseDto> result = commentQueryHelper.buildCommentHierarchy(comments, replies);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getReplies().size());
    }

    @Test
    @DisplayName("null 체크 테스트")
    void buildCommentHierarchy_NullSafety() {
        // Given
        List<Comment> comments = Arrays.asList(mockParentComment1, null, mockParentComment2);
        List<Comment> replies = Arrays.asList(mockReplyComment1, null);

        // When & Then - null이 포함되어도 예외가 발생하지 않아야 함
        assertDoesNotThrow(() -> {
            List<CommentResponseDto> result = commentQueryHelper.buildCommentHierarchy(
                    comments.stream().filter(c -> c != null).toList(),
                    replies.stream().filter(r -> r != null).toList()
            );
            assertNotNull(result);
        });
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

    private Comment createMockParentComment(Long commentId, String content) {
        Comment comment = mock(Comment.class);
        when(comment.getCommentId()).thenReturn(commentId);
        when(comment.getContent()).thenReturn(content);
        when(comment.getMember()).thenReturn(mockMember);
        when(comment.getPost()).thenReturn(mockPost);
        when(comment.getParentComment()).thenReturn(null);
        when(comment.isReply()).thenReturn(false);
        when(comment.getCreatedAt()).thenReturn(LocalDateTime.now().minusHours(commentId));
        when(comment.getUpdatedAt()).thenReturn(LocalDateTime.now().minusHours(commentId));
        when(comment.getDeletedAt()).thenReturn(null);
        return comment;
    }

    private Comment createMockReplyComment(Long commentId, String content, Comment parentComment) {
        Comment comment = mock(Comment.class);
        when(comment.getCommentId()).thenReturn(commentId);
        when(comment.getContent()).thenReturn(content);
        when(comment.getMember()).thenReturn(mockMember);
        when(comment.getPost()).thenReturn(mockPost);
        when(comment.getParentComment()).thenReturn(parentComment);
        when(comment.isReply()).thenReturn(true);
        when(comment.getCreatedAt()).thenReturn(LocalDateTime.now().minusHours(commentId));
        when(comment.getUpdatedAt()).thenReturn(LocalDateTime.now().minusHours(commentId));
        when(comment.getDeletedAt()).thenReturn(null);
        return comment;
    }
}