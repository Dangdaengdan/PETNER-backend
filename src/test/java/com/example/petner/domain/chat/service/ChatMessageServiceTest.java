package com.example.petner.domain.chat.service;

import com.example.petner.domain.chat.dto.request.ChatMessageRequestDto;
import com.example.petner.domain.chat.dto.response.ChatMessageResponseDto;
import com.example.petner.domain.chat.entity.ChatRoom;
import com.example.petner.domain.chat.entity.ChatRoomMember;
import com.example.petner.domain.chat.entity.Message;
import com.example.petner.domain.chat.repository.ChatRoomMemberRepository;
import com.example.petner.domain.chat.repository.ChatRoomRepository;
import com.example.petner.domain.chat.repository.MessageRepository;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.member.repository.MemberRepository;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.ChatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

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
class ChatMessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatRoomMemberRepository chatRoomMemberRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ChatRoomMemberManager memberManager;

    @InjectMocks
    private ChatMessageService chatMessageService;

    private ChatRoom chatRoom;
    private Member sender;
    private Message message;
    @Mock
    private ChatRoomMember chatRoomMember;

    @BeforeEach
    void setUp() {
        chatRoom = ChatRoom.builder().build();
        ReflectionTestUtils.setField(chatRoom, "chatRoomId", 1L);

        sender = Member.builder()
                .kakaoId("12345")
                .email("sender@example.com")
                .nickname("발신자")
                .build();
        ReflectionTestUtils.setField(sender, "memberId", 1L);

        message = Message.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content("테스트 메시지")
                .build();
        ReflectionTestUtils.setField(message, "messageId", 1L);
        ReflectionTestUtils.setField(message, "sentAt", LocalDateTime.now());

        // chatRoomMember는 이제 @Mock으로 처리됨
    }

    @Test
    @DisplayName("메시지 저장 성공")
    void saveMessage_Success() {
        // Given
        Long chatRoomId = 1L;
        Long senderId = 1L;
        ChatMessageRequestDto messageDto = new ChatMessageRequestDto("테스트 메시지");

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
        when(memberRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(chatRoom);

        // When
        ChatMessageResponseDto result = chatMessageService.saveMessage(chatRoomId, senderId, messageDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getMessageId()).isEqualTo(1L);
        assertThat(result.getSenderId()).isEqualTo(1L);
        // getSenderNickname() 메서드가 없으므로 제거
        assertThat(result.getContent()).isEqualTo("테스트 메시지");

        verify(chatRoomRepository).findById(chatRoomId);
        verify(memberRepository).findById(senderId);
        verify(memberManager).validateAndReactivateMembers(chatRoom, sender);
        verify(messageRepository).save(any(Message.class));
        verify(chatRoomRepository).save(chatRoom); // 마지막 메시지 시간 업데이트
    }

    @Test
    @DisplayName("메시지 저장 실패 - 존재하지 않는 채팅방")
    void saveMessage_Fail_ChatRoomNotFound() {
        // Given
        Long chatRoomId = 999L;
        Long senderId = 1L;
        ChatMessageRequestDto messageDto = new ChatMessageRequestDto("테스트 메시지");

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> chatMessageService.saveMessage(chatRoomId, senderId, messageDto))
                .isInstanceOf(ChatException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CHAT_ROOM_NOT_FOUND);

        verify(chatRoomRepository).findById(chatRoomId);
        verify(memberRepository, never()).findById(any());
        verify(messageRepository, never()).save(any());
    }

    @Test
    @DisplayName("메시지 저장 실패 - 존재하지 않는 발신자")
    void saveMessage_Fail_SenderNotFound() {
        // Given
        Long chatRoomId = 1L;
        Long senderId = 999L;
        ChatMessageRequestDto messageDto = new ChatMessageRequestDto("테스트 메시지");

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
        when(memberRepository.findById(senderId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> chatMessageService.saveMessage(chatRoomId, senderId, messageDto))
                .isInstanceOf(ChatException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CHAT_MEMBER_NOT_FOUND);

        verify(chatRoomRepository).findById(chatRoomId);
        verify(memberRepository).findById(senderId);
        verify(messageRepository, never()).save(any());
    }

    @Test
    @DisplayName("채팅방 메시지 조회 성공 - 페이징")
    void getChatRoomMessages_Success() {
        // Given
        Long chatRoomId = 1L;
        Long requestMemberId = 1L;
        int page = 0;
        int size = 50;

        List<Message> messages = Arrays.asList(message);

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
        when(memberRepository.findById(requestMemberId)).thenReturn(Optional.of(sender));
        when(chatRoomMemberRepository.findByChatRoomAndMember(chatRoom, sender))
                .thenReturn(Optional.of(chatRoomMember));
        when(messageRepository.findByChatRoomIdWithSender(eq(chatRoomId), any(Pageable.class)))
                .thenReturn(messages);

        // When
        List<ChatMessageResponseDto> result = chatMessageService.getChatRoomMessages(chatRoomId, requestMemberId, page, size);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).isEqualTo("테스트 메시지");

        verify(chatRoomRepository).findById(chatRoomId);
        verify(memberRepository).findById(requestMemberId);
        verify(chatRoomMemberRepository).findByChatRoomAndMember(chatRoom, sender);
        verify(messageRepository).findByChatRoomIdWithSender(eq(chatRoomId), any(Pageable.class));
    }

    @Test
    @DisplayName("채팅방 메시지 조회 실패 - 권한 없음")
    void getChatRoomMessages_Fail_Unauthorized() {
        // Given
        Long chatRoomId = 1L;
        Long requestMemberId = 1L;

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
        when(memberRepository.findById(requestMemberId)).thenReturn(Optional.of(sender));
        when(chatRoomMemberRepository.findByChatRoomAndMember(chatRoom, sender))
                .thenReturn(Optional.empty()); // 채팅방 멤버가 아님

        // When & Then
        assertThatThrownBy(() -> chatMessageService.getChatRoomMessages(chatRoomId, requestMemberId, 0, 50))
                .isInstanceOf(ChatException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CHAT_UNAUTHORIZED_ACCESS);

        verify(chatRoomMemberRepository).findByChatRoomAndMember(chatRoom, sender);
        verify(messageRepository, never()).findByChatRoomIdWithSender(any(), any());
    }

    @Test
    @DisplayName("채팅방 전체 메시지 조회 성공")
    void getAllChatRoomMessages_Success() {
        // Given
        Long chatRoomId = 1L;
        Long requestMemberId = 1L;

        List<Message> messages = Arrays.asList(message);

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
        when(memberRepository.findById(requestMemberId)).thenReturn(Optional.of(sender));
        when(chatRoomMemberRepository.findByChatRoomAndMember(chatRoom, sender))
                .thenReturn(Optional.of(chatRoomMember));
        when(messageRepository.findByChatRoomIdWithSenderOrderBySentAtAsc(chatRoomId))
                .thenReturn(messages);

        // When
        List<ChatMessageResponseDto> result = chatMessageService.getAllChatRoomMessages(chatRoomId, requestMemberId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).isEqualTo("테스트 메시지");

        verify(messageRepository).findByChatRoomIdWithSenderOrderBySentAtAsc(chatRoomId);
    }

    @Test
    @DisplayName("가시 메시지 조회 성공 - 페이징")
    void getVisibleMessagesForMember_Success_WithPaging() {
        // Given
        Long chatRoomId = 1L;
        Long memberId = 1L;
        int page = 0;
        int size = 50;

        List<Message> messages = Arrays.asList(message);

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(sender));
        when(chatRoomMemberRepository.findByChatRoomAndMember(chatRoom, sender))
                .thenReturn(Optional.of(chatRoomMember));
        when(messageRepository.findByChatRoomIdWithSender(eq(chatRoomId), any(Pageable.class)))
                .thenReturn(messages);
        when(chatRoomMember.canSeeMessage(any(LocalDateTime.class))).thenReturn(true);

        // When
        List<ChatMessageResponseDto> result = chatMessageService.getVisibleMessagesForMember(chatRoomId, memberId, page, size);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).isEqualTo("테스트 메시지");

        verify(chatRoomMember).canSeeMessage(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("가시 메시지 조회 성공 - 페이징 없음")
    void getVisibleMessagesForMember_Success_WithoutPaging() {
        // Given
        Long chatRoomId = 1L;
        Long memberId = 1L;

        List<Message> messages = Arrays.asList(message);

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(sender));
        when(chatRoomMemberRepository.findByChatRoomAndMember(chatRoom, sender))
                .thenReturn(Optional.of(chatRoomMember));
        when(messageRepository.findByChatRoomIdWithSenderOrderBySentAtAsc(chatRoomId))
                .thenReturn(messages);
        when(chatRoomMember.canSeeMessage(any(LocalDateTime.class))).thenReturn(true);

        // When
        List<ChatMessageResponseDto> result = chatMessageService.getVisibleMessagesForMember(chatRoomId, memberId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).isEqualTo("테스트 메시지");

        verify(messageRepository).findByChatRoomIdWithSenderOrderBySentAtAsc(chatRoomId);
        verify(chatRoomMember).canSeeMessage(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("가시 메시지 조회 실패 - 채팅방 멤버가 아님")
    void getVisibleMessagesForMember_Fail_NotMember() {
        // Given
        Long chatRoomId = 1L;
        Long memberId = 1L;

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(sender));
        when(chatRoomMemberRepository.findByChatRoomAndMember(chatRoom, sender))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> chatMessageService.getVisibleMessagesForMember(chatRoomId, memberId, 0, 50))
                .isInstanceOf(ChatException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CHAT_UNAUTHORIZED_ACCESS);

        verify(messageRepository, never()).findByChatRoomIdWithSender(any(), any());
    }

    @Test
    @DisplayName("가시 메시지 필터링 - 볼 수 없는 메시지 제외")
    void getVisibleMessagesForMember_FilterInvisibleMessages() {
        // Given
        Long chatRoomId = 1L;
        Long memberId = 1L;

        Message visibleMessage = Message.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content("보이는 메시지")
                .build();
        ReflectionTestUtils.setField(visibleMessage, "messageId", 2L);
        ReflectionTestUtils.setField(visibleMessage, "sentAt", LocalDateTime.now());

        Message invisibleMessage = Message.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content("안보이는 메시지")
                .build();
        ReflectionTestUtils.setField(invisibleMessage, "messageId", 3L);
        ReflectionTestUtils.setField(invisibleMessage, "sentAt", LocalDateTime.now().minusHours(1));

        List<Message> messages = Arrays.asList(visibleMessage, invisibleMessage);

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(sender));
        when(chatRoomMemberRepository.findByChatRoomAndMember(chatRoom, sender))
                .thenReturn(Optional.of(chatRoomMember));
        when(messageRepository.findByChatRoomIdWithSenderOrderBySentAtAsc(chatRoomId))
                .thenReturn(messages);

        // 첫 번째 메시지는 보이고, 두 번째 메시지는 안 보임
        when(chatRoomMember.canSeeMessage(visibleMessage.getSentAt())).thenReturn(true);
        when(chatRoomMember.canSeeMessage(invisibleMessage.getSentAt())).thenReturn(false);

        // When
        List<ChatMessageResponseDto> result = chatMessageService.getVisibleMessagesForMember(chatRoomId, memberId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).isEqualTo("보이는 메시지");

        verify(chatRoomMember, times(2)).canSeeMessage(any(LocalDateTime.class));
    }
}