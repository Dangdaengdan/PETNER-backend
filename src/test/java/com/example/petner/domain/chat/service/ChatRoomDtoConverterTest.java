package com.example.petner.domain.chat.service;

import com.example.petner.domain.chat.dto.response.ChatRoomListResponseDto;
import com.example.petner.domain.chat.entity.ChatRoom;
import com.example.petner.domain.chat.entity.ChatRoomMember;
import com.example.petner.domain.chat.entity.Message;
import com.example.petner.domain.chat.repository.MessageRepository;
import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.domain.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatRoomDtoConverterTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private ChatRoomDtoConverter chatRoomDtoConverter;

    private Member currentMember;
    private Member otherMember;
    private Dog dog;
    private ChatRoom chatRoom;
    private ChatRoom chatRoomWithoutDog;
    private Message message;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        testTime = LocalDateTime.of(2024, 1, 1, 12, 0, 0);

        currentMember = Member.builder()
                .kakaoId("12345")
                .email("current@example.com")
                .nickname("현재유저")
                .build();
        ReflectionTestUtils.setField(currentMember, "memberId", 1L);

        otherMember = Member.builder()
                .kakaoId("67890")
                .email("other@example.com")
                .nickname("다른유저")
                .build();
        ReflectionTestUtils.setField(otherMember, "memberId", 2L);

        dog = Dog.builder()
                .name("바둑이")
                .build();
        ReflectionTestUtils.setField(dog, "dogId", 1L);

        // 강아지가 있는 채팅방
        chatRoom = ChatRoom.builder()
                .dog(dog)
                .build();
        ReflectionTestUtils.setField(chatRoom, "chatRoomId", 1L);
        ReflectionTestUtils.setField(chatRoom, "createdAt", testTime);

        // 강아지가 없는 채팅방
        chatRoomWithoutDog = ChatRoom.builder()
                .build();
        ReflectionTestUtils.setField(chatRoomWithoutDog, "chatRoomId", 2L);
        ReflectionTestUtils.setField(chatRoomWithoutDog, "createdAt", testTime);

        // ChatRoomMember 설정
        List<ChatRoomMember> chatRoomMembers = new ArrayList<>();

        ChatRoomMember member1 = ChatRoomMember.builder()
                .chatRoom(chatRoom)
                .member(currentMember)
                .isActive(true)
                .build();

        ChatRoomMember member2 = ChatRoomMember.builder()
                .chatRoom(chatRoom)
                .member(otherMember)
                .isActive(true)
                .build();

        chatRoomMembers.add(member1);
        chatRoomMembers.add(member2);

        ReflectionTestUtils.setField(chatRoom, "chatRoomMembers", chatRoomMembers);

        message = Message.builder()
                .chatRoom(chatRoom)
                .sender(currentMember)
                .content("테스트 메시지")
                .build();
        ReflectionTestUtils.setField(message, "messageId", 1L);
        ReflectionTestUtils.setField(message, "sentAt", testTime.plusMinutes(30));
    }

    @Test
    @DisplayName("ChatRoom을 ChatRoomListResponseDto로 변환 - 강아지 정보 포함")
    void convertToChatRoomListResponseDto_WithDog() {
        // Given
        Long currentMemberId = 1L;

        when(messageRepository.findLatestMessageByChatRoomId(1L))
                .thenReturn(Optional.of(message));

        // When
        ChatRoomListResponseDto result = chatRoomDtoConverter
                .convertToChatRoomListResponseDto(chatRoom, currentMemberId);

        // Then
        assertThat(result.getChatRoomId()).isEqualTo(1L);
        assertThat(result.getOtherMemberInfo()).isNotNull();
        assertThat(result.getOtherMemberInfo().getMemberId()).isEqualTo(2L);
        assertThat(result.getOtherMemberInfo().getNickname()).isEqualTo("다른유저");
        assertThat(result.getDogInfo()).isNotNull();
        assertThat(result.getDogInfo().getDogId()).isEqualTo(1L);
        assertThat(result.getDogInfo().getName()).isEqualTo("바둑이");
        assertThat(result.getLastMessageContent()).isEqualTo("테스트 메시지");
        assertThat(result.getLastMessageSentAt()).isEqualTo(testTime.plusMinutes(30));

        verify(messageRepository).findLatestMessageByChatRoomId(1L);
    }

    @Test
    @DisplayName("ChatRoom을 ChatRoomListResponseDto로 변환 - 강아지 정보 없음")
    void convertToChatRoomListResponseDto_WithoutDog() {
        // Given
        Long currentMemberId = 1L;

        // 강아지 없는 채팅방에 멤버 설정
        List<ChatRoomMember> membersWithoutDog = new ArrayList<>();

        ChatRoomMember member1 = ChatRoomMember.builder()
                .chatRoom(chatRoomWithoutDog)
                .member(currentMember)
                .isActive(true)
                .build();

        ChatRoomMember member2 = ChatRoomMember.builder()
                .chatRoom(chatRoomWithoutDog)
                .member(otherMember)
                .isActive(true)
                .build();

        membersWithoutDog.add(member1);
        membersWithoutDog.add(member2);

        ReflectionTestUtils.setField(chatRoomWithoutDog, "chatRoomMembers", membersWithoutDog);

        when(messageRepository.findLatestMessageByChatRoomId(2L))
                .thenReturn(Optional.of(message));

        // When
        ChatRoomListResponseDto result = chatRoomDtoConverter
                .convertToChatRoomListResponseDto(chatRoomWithoutDog, currentMemberId);

        // Then
        assertThat(result.getChatRoomId()).isEqualTo(2L);
        assertThat(result.getOtherMemberInfo()).isNotNull();
        assertThat(result.getOtherMemberInfo().getMemberId()).isEqualTo(2L);
        assertThat(result.getDogInfo()).isNull();
        assertThat(result.getLastMessageContent()).isEqualTo("테스트 메시지");

        verify(messageRepository).findLatestMessageByChatRoomId(2L);
    }

    @Test
    @DisplayName("ChatRoom을 ChatRoomListResponseDto로 변환 - 마지막 메시지 없음")
    void convertToChatRoomListResponseDto_NoLastMessage() {
        // Given
        Long currentMemberId = 1L;

        when(messageRepository.findLatestMessageByChatRoomId(1L))
                .thenReturn(Optional.empty());

        // When
        ChatRoomListResponseDto result = chatRoomDtoConverter
                .convertToChatRoomListResponseDto(chatRoom, currentMemberId);

        // Then
        assertThat(result.getChatRoomId()).isEqualTo(1L);
        assertThat(result.getLastMessageContent()).isEmpty();
        assertThat(result.getLastMessageSentAt()).isEqualTo(testTime); // 채팅방 생성 시간

        verify(messageRepository).findLatestMessageByChatRoomId(1L);
    }

    @Test
    @DisplayName("ChatRoom을 ChatRoomListResponseDto로 변환 - 미리 조회된 메시지 사용")
    void convertToChatRoomListResponseDto_WithPreloadedMessage() {
        // Given
        Long currentMemberId = 1L;

        // When
        ChatRoomListResponseDto result = chatRoomDtoConverter
                .convertToChatRoomListResponseDto(chatRoom, currentMemberId, message);

        // Then
        assertThat(result.getChatRoomId()).isEqualTo(1L);
        assertThat(result.getOtherMemberInfo()).isNotNull();
        assertThat(result.getOtherMemberInfo().getMemberId()).isEqualTo(2L);
        assertThat(result.getDogInfo()).isNotNull();
        assertThat(result.getDogInfo().getDogId()).isEqualTo(1L);
        assertThat(result.getLastMessageContent()).isEqualTo("테스트 메시지");
        assertThat(result.getLastMessageSentAt()).isEqualTo(testTime.plusMinutes(30));

        // 메시지 Repository 호출 안됨 (N+1 문제 해결)
        verify(messageRepository, never()).findLatestMessageByChatRoomId(any());
    }

    @Test
    @DisplayName("ChatRoom을 ChatRoomListResponseDto로 변환 - 미리 조회된 메시지가 null")
    void convertToChatRoomListResponseDto_WithNullPreloadedMessage() {
        // Given
        Long currentMemberId = 1L;

        // When
        ChatRoomListResponseDto result = chatRoomDtoConverter
                .convertToChatRoomListResponseDto(chatRoom, currentMemberId, null);

        // Then
        assertThat(result.getChatRoomId()).isEqualTo(1L);
        assertThat(result.getLastMessageContent()).isEqualTo("아직 메시지가 없습니다");
        assertThat(result.getLastMessageSentAt()).isEqualTo(testTime); // 채팅방 생성 시간

        // 메시지 Repository 호출 안됨
        verify(messageRepository, never()).findLatestMessageByChatRoomId(any());
    }

    @Test
    @DisplayName("상대방 멤버 찾기 - 정상적인 경우")
    void determineOtherMember_Success() {
        // Given
        Long currentMemberId = 1L;

        // When
        ChatRoomListResponseDto result = chatRoomDtoConverter
                .convertToChatRoomListResponseDto(chatRoom, currentMemberId, message);

        // Then
        assertThat(result.getOtherMemberInfo()).isNotNull();
        assertThat(result.getOtherMemberInfo().getMemberId()).isEqualTo(2L);
        assertThat(result.getOtherMemberInfo().getNickname()).isEqualTo("다른유저");
    }

    @Test
    @DisplayName("상대방 멤버 찾기 - 멤버가 한 명만 있는 경우")
    void determineOtherMember_OnlyCurrentMember() {
        // Given
        Long currentMemberId = 1L;

        List<ChatRoomMember> singleMember = new ArrayList<>();
        ChatRoomMember member1 = ChatRoomMember.builder()
                .chatRoom(chatRoom)
                .member(currentMember)
                .isActive(true)
                .build();
        singleMember.add(member1);

        ReflectionTestUtils.setField(chatRoom, "chatRoomMembers", singleMember);

        // When
        ChatRoomListResponseDto result = chatRoomDtoConverter
                .convertToChatRoomListResponseDto(chatRoom, currentMemberId, message);

        // Then
        assertThat(result.getOtherMemberInfo()).isNull();
    }

    @Test
    @DisplayName("상대방 멤버 찾기 - 예외 발생 시 null 반환")
    void determineOtherMember_ExceptionHandling() {
        // Given
        Long currentMemberId = 1L;

        // 잘못된 ChatRoomMembers 설정으로 예외 유발
        ReflectionTestUtils.setField(chatRoom, "chatRoomMembers", null);

        // When
        ChatRoomListResponseDto result = chatRoomDtoConverter
                .convertToChatRoomListResponseDto(chatRoom, currentMemberId, message);

        // Then - 예외가 발생해도 정상적으로 처리되어야 함
        assertThat(result.getOtherMemberInfo()).isNull();
        assertThat(result.getChatRoomId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("강아지 정보 생성 - 강아지 있음")
    void createDogInfo_WithDog() {
        // Given
        Long currentMemberId = 1L;

        // When
        ChatRoomListResponseDto result = chatRoomDtoConverter
                .convertToChatRoomListResponseDto(chatRoom, currentMemberId, message);

        // Then
        assertThat(result.getDogInfo()).isNotNull();
        assertThat(result.getDogInfo().getDogId()).isEqualTo(1L);
        assertThat(result.getDogInfo().getName()).isEqualTo("바둑이");
    }

    @Test
    @DisplayName("강아지 정보 생성 - 강아지 없음")
    void createDogInfo_WithoutDog() {
        // Given
        Long currentMemberId = 1L;

        // When
        ChatRoomListResponseDto result = chatRoomDtoConverter
                .convertToChatRoomListResponseDto(chatRoomWithoutDog, currentMemberId, message);

        // Then
        assertThat(result.getDogInfo()).isNull();
    }

    @Test
    @DisplayName("deprecated 메서드 동작 확인")
    void deprecatedMethod_StillWorks() {
        // Given
        Long currentMemberId = 1L;

        when(messageRepository.findLatestMessageByChatRoomId(1L))
                .thenReturn(Optional.of(message));

        // When
        @SuppressWarnings("deprecation")
        ChatRoomListResponseDto result = chatRoomDtoConverter
                .convertToChatRoomListResponseDto(chatRoom, currentMemberId);

        // Then
        assertThat(result.getChatRoomId()).isEqualTo(1L);
        assertThat(result.getLastMessageContent()).isEqualTo("테스트 메시지");

        // N+1 문제가 있는 메서드이므로 Repository 호출됨
        verify(messageRepository).findLatestMessageByChatRoomId(1L);
    }
}