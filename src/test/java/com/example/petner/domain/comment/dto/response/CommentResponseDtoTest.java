package com.example.petner.domain.comment.dto.response;

import com.example.petner.domain.comment.entity.Comment;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.post.entity.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentResponseDtoTest {

    private Comment mockComment;
    private Comment mockParentComment;
    private Member mockMember;
    private Post mockPost;

    @BeforeEach
    void setUp() {
        mockPost = createMockPost();
        mockMember = createMockMember();
        mockParentComment = createMockParentComment();
        mockComment = createMockComment();
    }

    @Test
    @DisplayName("CommentResponseDto 생성 테스트 - 일반 댓글")
    void createCommentResponseDto_NormalComment_Success() {
        // When
        CommentResponseDto dto = new CommentResponseDto(mockComment);

        // Then
        assertNotNull(dto);
        assertEquals(1L, dto.getCommentId());
        assertEquals("테스트 댓글 내용", dto.getContent());
        assertEquals("testUser", dto.getAuthorNickname());
        assertNotNull(dto.getCreatedAt());
        assertNotNull(dto.getUpdatedAt());
        assertNull(dto.getDeletedAt());
        assertNull(dto.getParentCommentId());
        assertFalse(dto.isReply());
        assertTrue(dto.getReplies().isEmpty());
    }

    @Test
    @DisplayName("CommentResponseDto 생성 테스트 - 대댓글")
    void createCommentResponseDto_ReplyComment_Success() {
        // Given
        Comment replyComment = createMockReplyComment();

        // When
        CommentResponseDto dto = new CommentResponseDto(replyComment);

        // Then
        assertNotNull(dto);
        assertEquals(2L, dto.getCommentId());
        assertEquals("대댓글 내용", dto.getContent());
        assertEquals("testUser", dto.getAuthorNickname());
        assertEquals(1L, dto.getParentCommentId());
        assertTrue(dto.isReply());
        assertTrue(dto.getReplies().isEmpty());
    }

    @Test
    @DisplayName("CommentResponseDto 생성 테스트 - 삭제된 댓글")
    void createCommentResponseDto_DeletedComment_Success() {
        // Given
        Comment deletedComment = createMockDeletedComment();

        // When
        CommentResponseDto dto = new CommentResponseDto(deletedComment);

        // Then
        assertNotNull(dto);
        assertEquals(1L, dto.getCommentId());
        assertEquals("삭제된 댓글입니다.", dto.getContent());
        assertEquals("testUser", dto.getAuthorNickname());
        assertNotNull(dto.getDeletedAt());
    }

    @Test
    @DisplayName("CommentResponseDto getter 메서드 테스트")
    void testGetterMethods() {
        // Given
        CommentResponseDto dto = new CommentResponseDto(mockComment);

        // When & Then
        assertEquals(1L, dto.getCommentId());
        assertEquals("테스트 댓글 내용", dto.getContent());
        assertEquals("testUser", dto.getAuthorNickname());
        assertNotNull(dto.getCreatedAt());
        assertNotNull(dto.getUpdatedAt());
        assertNull(dto.getDeletedAt());
        assertNull(dto.getParentCommentId());
        assertFalse(dto.isReply());
        assertTrue(dto.getReplies().isEmpty());
    }

    @Test
    @DisplayName("CommentResponseDto setReplies 메서드 테스트")
    void testSetReplies() {
        // Given
        CommentResponseDto parentDto = new CommentResponseDto(mockParentComment);
        CommentResponseDto replyDto1 = new CommentResponseDto(createMockReplyComment());
        CommentResponseDto replyDto2 = new CommentResponseDto(createMockReplyComment());
        List<CommentResponseDto> replies = Arrays.asList(replyDto1, replyDto2);

        // When
        parentDto.setReplies(replies);

        // Then
        assertNotNull(parentDto.getReplies());
        assertEquals(2, parentDto.getReplies().size());
        assertEquals(replyDto1, parentDto.getReplies().get(0));
        assertEquals(replyDto2, parentDto.getReplies().get(1));
    }

    @Test
    @DisplayName("CommentResponseDto 생성 테스트 - 다양한 시간 값")
    void createCommentResponseDto_WithDifferentTimestamps() {
        // Given
        LocalDateTime createdTime = LocalDateTime.now().minusHours(2);
        LocalDateTime updatedTime = LocalDateTime.now().minusHours(1);

        when(mockComment.getCreatedAt()).thenReturn(createdTime);
        when(mockComment.getUpdatedAt()).thenReturn(updatedTime);

        // When
        CommentResponseDto dto = new CommentResponseDto(mockComment);

        // Then
        assertEquals(createdTime, dto.getCreatedAt());
        assertEquals(updatedTime, dto.getUpdatedAt());
        assertTrue(dto.getCreatedAt().isBefore(dto.getUpdatedAt()));
    }

    @Test
    @DisplayName("CommentResponseDto 생성 테스트 - 긴 댓글 내용")
    void createCommentResponseDto_WithLongContent() {
        // Given
        String longContent = "매우 긴 댓글 내용입니다. ".repeat(100);
        when(mockComment.getContent()).thenReturn(longContent);

        // When
        CommentResponseDto dto = new CommentResponseDto(mockComment);

        // Then
        assertEquals(longContent, dto.getContent());
        assertTrue(dto.getContent().length() > 1000);
    }

    @Test
    @DisplayName("CommentResponseDto 생성 테스트 - 특수문자 포함 닉네임")
    void createCommentResponseDto_WithSpecialCharacterNickname() {
        // Given
        String specialNickname = "user@123!#$%";
        when(mockMember.getNickname()).thenReturn(specialNickname);

        // When
        CommentResponseDto dto = new CommentResponseDto(mockComment);

        // Then
        assertEquals(specialNickname, dto.getAuthorNickname());
    }

    @Test
    @DisplayName("CommentResponseDto 생성 테스트 - 높은 ID 값")
    void createCommentResponseDto_WithHighIds() {
        // Given
        Long highCommentId = Long.MAX_VALUE;
        Long highParentId = Long.MAX_VALUE - 1;

        when(mockComment.getCommentId()).thenReturn(highCommentId);
        when(mockComment.getParentComment()).thenReturn(mockParentComment);
        when(mockParentComment.getCommentId()).thenReturn(highParentId);

        // When
        CommentResponseDto dto = new CommentResponseDto(mockComment);

        // Then
        assertEquals(highCommentId, dto.getCommentId());
        assertEquals(highParentId, dto.getParentCommentId());
    }

    @Test
    @DisplayName("CommentResponseDto toString 메서드 동작 확인")
    void testToStringMethod() {
        // Given
        CommentResponseDto dto = new CommentResponseDto(mockComment);

        // When
        String toStringResult = dto.toString();

        // Then
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("CommentResponseDto"));
    }

    @Test
    @DisplayName("CommentResponseDto 생성 테스트 - 빈 대댓글 목록")
    void createCommentResponseDto_WithEmptyReplies() {
        // Given
        CommentResponseDto dto = new CommentResponseDto(mockComment);

        // When
        dto.setReplies(List.of());

        // Then
        assertNotNull(dto.getReplies());
        assertTrue(dto.getReplies().isEmpty());
    }

    @Test
    @DisplayName("CommentResponseDto 생성 테스트 - null 대댓글 목록")
    void createCommentResponseDto_WithNullReplies() {
        // Given
        CommentResponseDto dto = new CommentResponseDto(mockComment);

        // When
        dto.setReplies(null);

        // Then
        assertNull(dto.getReplies());
    }

    @Test
    @DisplayName("CommentResponseDto 생성 테스트 - 삭제된 댓글의 내용 확인")
    void createCommentResponseDto_DeletedCommentContent() {
        // Given
        Comment deletedComment = mock(Comment.class);
        when(deletedComment.getCommentId()).thenReturn(1L);
        when(deletedComment.getContent()).thenReturn("원본 댓글 내용");
        when(deletedComment.isDeleted()).thenReturn(true);
        when(deletedComment.getMember()).thenReturn(mockMember);
        when(deletedComment.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(deletedComment.getUpdatedAt()).thenReturn(LocalDateTime.now());
        when(deletedComment.getDeletedAt()).thenReturn(LocalDateTime.now());
        when(deletedComment.getParentComment()).thenReturn(null);
        when(deletedComment.isReply()).thenReturn(false);

        // When
        CommentResponseDto dto = new CommentResponseDto(deletedComment);

        // Then
        assertEquals("삭제된 댓글입니다.", dto.getContent());
        assertNotEquals("원본 댓글 내용", dto.getContent());
    }

    @Test
    @DisplayName("CommentResponseDto 생성 테스트 - 필드 타입 검증")
    void createCommentResponseDto_FieldTypes() {
        // Given
        CommentResponseDto dto = new CommentResponseDto(mockComment);

        // When & Then
        assertTrue(dto.getCommentId() instanceof Long);
        assertTrue(dto.getContent() instanceof String);
        assertTrue(dto.getAuthorNickname() instanceof String);
        assertTrue(dto.getCreatedAt() instanceof LocalDateTime);
        assertTrue(dto.getUpdatedAt() instanceof LocalDateTime);
        assertTrue(dto.getDeletedAt() == null || dto.getDeletedAt() instanceof LocalDateTime);
        assertTrue(dto.getParentCommentId() == null || dto.getParentCommentId() instanceof Long);
        assertTrue(dto.isReply() instanceof Boolean);
        assertTrue(dto.getReplies() instanceof List);
    }

    @Test
    @DisplayName("CommentResponseDto 생성 테스트 - 동일 객체 여러 번 생성")
    void createCommentResponseDto_MultipleInstances() {
        // When
        CommentResponseDto dto1 = new CommentResponseDto(mockComment);
        CommentResponseDto dto2 = new CommentResponseDto(mockComment);

        // Then
        assertNotSame(dto1, dto2); // 다른 인스턴스
        assertEquals(dto1.getCommentId(), dto2.getCommentId()); // 같은 값
        assertEquals(dto1.getContent(), dto2.getContent());
        assertEquals(dto1.getAuthorNickname(), dto2.getAuthorNickname());
    }

    @Test
    @DisplayName("CommentResponseDto 생성 테스트 - 계층 구조 확인")
    void createCommentResponseDto_HierarchicalStructure() {
        // Given
        CommentResponseDto parentDto = new CommentResponseDto(mockParentComment);
        CommentResponseDto replyDto = new CommentResponseDto(createMockReplyComment());

        // When
        parentDto.setReplies(List.of(replyDto));

        // Then
        assertFalse(parentDto.isReply());
        assertNull(parentDto.getParentCommentId());
        assertEquals(1, parentDto.getReplies().size());

        CommentResponseDto reply = parentDto.getReplies().get(0);
        assertTrue(reply.isReply());
        assertNotNull(reply.getParentCommentId());
        assertTrue(reply.getReplies().isEmpty());
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
        when(comment.getContent()).thenReturn("부모 댓글 내용");
        when(comment.isDeleted()).thenReturn(false);
        when(comment.getMember()).thenReturn(mockMember);
        when(comment.getPost()).thenReturn(mockPost);
        when(comment.getCreatedAt()).thenReturn(LocalDateTime.now().minusHours(2));
        when(comment.getUpdatedAt()).thenReturn(LocalDateTime.now().minusHours(2));
        when(comment.getDeletedAt()).thenReturn(null);
        when(comment.getParentComment()).thenReturn(null);
        when(comment.isReply()).thenReturn(false);
        return comment;
    }

    private Comment createMockComment() {
        Comment comment = mock(Comment.class);
        when(comment.getCommentId()).thenReturn(1L);
        when(comment.getContent()).thenReturn("테스트 댓글 내용");
        when(comment.isDeleted()).thenReturn(false);
        when(comment.getMember()).thenReturn(mockMember);
        when(comment.getPost()).thenReturn(mockPost);
        when(comment.getCreatedAt()).thenReturn(LocalDateTime.now().minusHours(1));
        when(comment.getUpdatedAt()).thenReturn(LocalDateTime.now().minusHours(1));
        when(comment.getDeletedAt()).thenReturn(null);
        when(comment.getParentComment()).thenReturn(null);
        when(comment.isReply()).thenReturn(false);
        return comment;
    }

    private Comment createMockReplyComment() {
        Comment comment = mock(Comment.class);
        when(comment.getCommentId()).thenReturn(2L);
        when(comment.getContent()).thenReturn("대댓글 내용");
        when(comment.isDeleted()).thenReturn(false);
        when(comment.getMember()).thenReturn(mockMember);
        when(comment.getPost()).thenReturn(mockPost);
        when(comment.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(comment.getUpdatedAt()).thenReturn(LocalDateTime.now());
        when(comment.getDeletedAt()).thenReturn(null);
        when(comment.getParentComment()).thenReturn(mockParentComment);
        when(comment.isReply()).thenReturn(true);
        return comment;
    }

    private Comment createMockDeletedComment() {
        Comment comment = mock(Comment.class);
        when(comment.getCommentId()).thenReturn(1L);
        when(comment.getContent()).thenReturn("삭제된 댓글의 원본 내용");
        when(comment.isDeleted()).thenReturn(true);
        when(comment.getMember()).thenReturn(mockMember);
        when(comment.getPost()).thenReturn(mockPost);
        when(comment.getCreatedAt()).thenReturn(LocalDateTime.now().minusHours(1));
        when(comment.getUpdatedAt()).thenReturn(LocalDateTime.now().minusHours(1));
        when(comment.getDeletedAt()).thenReturn(LocalDateTime.now());
        when(comment.getParentComment()).thenReturn(null);
        when(comment.isReply()).thenReturn(false);
        return comment;
    }
}