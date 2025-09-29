package com.example.petner.domain.chat.dto.response;

import com.example.petner.domain.chat.entity.ChatRoom;
import com.example.petner.domain.chat.entity.Message;
import com.example.petner.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ChatMessageResponseDtoTest {

    @Test
    @DisplayName("Message 엔티티로부터 ChatMessageResponseDto 생성 성공")
    void fromMessage_Success() {
        // Given
        ChatRoom chatRoom = ChatRoom.builder().build();
        ReflectionTestUtils.setField(chatRoom, "chatRoomId", 1L);

        Member sender = Member.builder()
                .kakaoId("12345")
                .email("sender@example.com")
                .nickname("발신자")
                .build();
        ReflectionTestUtils.setField(sender, "memberId", 1L);

        LocalDateTime sentAt = LocalDateTime.of(2024, 1, 1, 12, 0, 0);
        Message message = Message.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content("테스트 메시지")
                .build();
        ReflectionTestUtils.setField(message, "messageId", 100L);
        ReflectionTestUtils.setField(message, "sentAt", sentAt);

        // When
        ChatMessageResponseDto dto = new ChatMessageResponseDto(message);

        // Then
        assertThat(dto.getMessageId()).isEqualTo(100L);
        assertThat(dto.getSenderId()).isEqualTo(1L);
        assertThat(dto.getContent()).isEqualTo("테스트 메시지");
        assertThat(dto.getSendAt()).isEqualTo(sentAt);
    }

    @Test
    @DisplayName("생성자를 통한 ChatMessageResponseDto 생성 성공")
    void constructor_Success() {
        // Given
        Long messageId = 200L;
        Long senderId = 2L;
        String content = "생성자 테스트 메시지";
        LocalDateTime sendAt = LocalDateTime.of(2024, 2, 1, 14, 30, 0);

        // When
        ChatMessageResponseDto dto = new ChatMessageResponseDto(messageId, senderId, content, sendAt);

        // Then
        assertThat(dto.getMessageId()).isEqualTo(messageId);
        assertThat(dto.getSenderId()).isEqualTo(senderId);
        assertThat(dto.getContent()).isEqualTo(content);
        assertThat(dto.getSendAt()).isEqualTo(sendAt);
    }

    @Test
    @DisplayName("긴 메시지 내용 처리")
    void fromMessage_LongContent() {
        // Given
        ChatRoom chatRoom = ChatRoom.builder().build();
        Member sender = Member.builder()
                .kakaoId("12345")
                .email("sender@example.com")
                .nickname("발신자")
                .build();
        ReflectionTestUtils.setField(sender, "memberId", 1L);

        String longContent = "이것은 매우 긴 메시지입니다. ".repeat(10); // 적당한 길이의 텍스트 생성
        Message message = Message.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(longContent)
                .build();
        ReflectionTestUtils.setField(message, "messageId", 1L);
        ReflectionTestUtils.setField(message, "sentAt", LocalDateTime.now());

        // When
        ChatMessageResponseDto dto = new ChatMessageResponseDto(message);

        // Then
        assertThat(dto.getContent()).isEqualTo(longContent);
        assertThat(dto.getContent().length()).isGreaterThan(100);
    }

    @Test
    @DisplayName("빈 메시지 내용 처리")
    void fromMessage_EmptyContent() {
        // Given
        ChatRoom chatRoom = ChatRoom.builder().build();
        Member sender = Member.builder()
                .kakaoId("12345")
                .email("sender@example.com")
                .nickname("발신자")
                .build();
        ReflectionTestUtils.setField(sender, "memberId", 1L);

        Message message = Message.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content("")
                .build();
        ReflectionTestUtils.setField(message, "messageId", 1L);
        ReflectionTestUtils.setField(message, "sentAt", LocalDateTime.now());

        // When
        ChatMessageResponseDto dto = new ChatMessageResponseDto(message);

        // Then
        assertThat(dto.getContent()).isEmpty();
    }

    @Test
    @DisplayName("특수 문자가 포함된 메시지 처리")
    void fromMessage_SpecialCharacters() {
        // Given
        ChatRoom chatRoom = ChatRoom.builder().build();
        Member sender = Member.builder()
                .kakaoId("12345")
                .email("sender@example.com")
                .nickname("발신자")
                .build();
        ReflectionTestUtils.setField(sender, "memberId", 1L);

        String specialContent = "😀🐶 특수문자 테스트 !@#$%^&*()_+{}|:<>?[]\\;',./";
        Message message = Message.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(specialContent)
                .build();
        ReflectionTestUtils.setField(message, "messageId", 1L);
        ReflectionTestUtils.setField(message, "sentAt", LocalDateTime.now());

        // When
        ChatMessageResponseDto dto = new ChatMessageResponseDto(message);

        // Then
        assertThat(dto.getContent()).isEqualTo(specialContent);
    }

    @Test
    @DisplayName("과거와 미래 시간 처리")
    void fromMessage_DifferentTimes() {
        // Given
        ChatRoom chatRoom = ChatRoom.builder().build();
        Member sender = Member.builder()
                .kakaoId("12345")
                .email("sender@example.com")
                .nickname("발신자")
                .build();
        ReflectionTestUtils.setField(sender, "memberId", 1L);

        // 과거 시간
        LocalDateTime pastTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
        Message pastMessage = Message.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content("과거 메시지")
                .build();
        ReflectionTestUtils.setField(pastMessage, "messageId", 1L);
        ReflectionTestUtils.setField(pastMessage, "sentAt", pastTime);

        // 미래 시간
        LocalDateTime futureTime = LocalDateTime.of(2030, 12, 31, 23, 59, 59);
        Message futureMessage = Message.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content("미래 메시지")
                .build();
        ReflectionTestUtils.setField(futureMessage, "messageId", 2L);
        ReflectionTestUtils.setField(futureMessage, "sentAt", futureTime);

        // When
        ChatMessageResponseDto pastDto = new ChatMessageResponseDto(pastMessage);
        ChatMessageResponseDto futureDto = new ChatMessageResponseDto(futureMessage);

        // Then
        assertThat(pastDto.getSendAt()).isEqualTo(pastTime);
        assertThat(futureDto.getSendAt()).isEqualTo(futureTime);
        assertThat(pastDto.getContent()).isEqualTo("과거 메시지");
        assertThat(futureDto.getContent()).isEqualTo("미래 메시지");
    }
}