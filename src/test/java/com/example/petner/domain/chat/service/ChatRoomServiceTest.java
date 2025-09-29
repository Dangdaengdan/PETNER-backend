package com.example.petner.domain.chat.service;

import com.example.petner.domain.chat.dto.request.ChatRoomCreateRequestDto;
import com.example.petner.domain.chat.dto.response.ChatRoomResponseDto;
import com.example.petner.domain.chat.entity.ChatRoom;
import com.example.petner.domain.chat.entity.ChatRoomMember;
import com.example.petner.domain.chat.repository.ChatRoomMemberRepository;
import com.example.petner.domain.chat.repository.ChatRoomRepository;
import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.global.dto.SessionUser;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.ChatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatRoomMemberRepository chatRoomMemberRepository;

    @Mock
    private ChatRoomValidator chatRoomValidator;

    @Mock
    private ChatRoomDuplicateChecker duplicateChecker;

    @InjectMocks
    private ChatRoomService chatRoomService;

    private SessionUser sessionUser;
    private Member currentMember;
    private Member otherMember;
    private Dog dog;
    private ChatRoom chatRoom;

    @BeforeEach
    void setUp() {
        sessionUser = SessionUser.builder()
                .memberId(1L)
                .email("current@example.com")
                .nickname("현재유저")
                .build();

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

        chatRoom = ChatRoom.builder()
                .dog(dog)
                .build();
        ReflectionTestUtils.setField(chatRoom, "chatRoomId", 1L);
    }

    @Test
    @DisplayName("채팅방 생성 성공 - 새로운 채팅방")
    void createChatRoom_Success_NewChatRoom() {
        // Given
        ChatRoomCreateRequestDto requestDto = new ChatRoomCreateRequestDto(1L, 2L);

        when(chatRoomValidator.validateAndGetMember(1L)).thenReturn(currentMember);
        when(chatRoomValidator.validateAndGetMember(2L)).thenReturn(otherMember);
        when(chatRoomValidator.validateAndGetDog(1L, currentMember, otherMember)).thenReturn(dog);
        when(duplicateChecker.findExistingChatRoom(dog, 1L, 2L)).thenReturn(Optional.empty());
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(chatRoom);

        // When
        ChatRoomResponseDto result = chatRoomService.createChatRoom(requestDto, sessionUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getChatRoomId()).isEqualTo(1L);
        assertThat(result.getDogId()).isEqualTo(1L);

        verify(chatRoomValidator).validateAndGetMember(1L);
        verify(chatRoomValidator).validateAndGetMember(2L);
        verify(chatRoomValidator).validateAndGetDog(1L, currentMember, otherMember);
        verify(duplicateChecker).findExistingChatRoom(dog, 1L, 2L);
        verify(chatRoomRepository).save(any(ChatRoom.class));
        verify(chatRoomMemberRepository, times(2)).save(any(ChatRoomMember.class));
    }

    @Test
    @DisplayName("채팅방 생성 성공 - 기존 채팅방 존재")
    void createChatRoom_Success_ExistingChatRoom() {
        // Given
        ChatRoomCreateRequestDto requestDto = new ChatRoomCreateRequestDto(1L, 2L);

        when(chatRoomValidator.validateAndGetMember(1L)).thenReturn(currentMember);
        when(chatRoomValidator.validateAndGetMember(2L)).thenReturn(otherMember);
        when(chatRoomValidator.validateAndGetDog(1L, currentMember, otherMember)).thenReturn(dog);
        when(duplicateChecker.findExistingChatRoom(dog, 1L, 2L)).thenReturn(Optional.of(chatRoom));

        // When
        ChatRoomResponseDto result = chatRoomService.createChatRoom(requestDto, sessionUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getChatRoomId()).isEqualTo(1L);

        verify(chatRoomValidator).validateAndGetMember(1L);
        verify(chatRoomValidator).validateAndGetMember(2L);
        verify(chatRoomValidator).validateAndGetDog(1L, currentMember, otherMember);
        verify(duplicateChecker).findExistingChatRoom(dog, 1L, 2L);
        verify(chatRoomRepository, never()).save(any(ChatRoom.class)); // 새로운 채팅방 생성하지 않음
    }

    @Test
    @DisplayName("채팅방 생성 실패 - 자신과 채팅방 생성 시도")
    void createChatRoom_Fail_SameMember() {
        // Given
        ChatRoomCreateRequestDto requestDto = new ChatRoomCreateRequestDto(1L, 1L); // 같은 멤버 ID

        when(chatRoomValidator.validateAndGetMember(1L)).thenReturn(currentMember);

        // When & Then
        assertThatThrownBy(() -> chatRoomService.createChatRoom(requestDto, sessionUser))
                .isInstanceOf(ChatException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CHAT_INVALID_SAME_MEMBER);

        verify(chatRoomValidator, times(2)).validateAndGetMember(1L);
        verify(chatRoomRepository, never()).save(any());
    }

    @Test
    @DisplayName("채팅방 생성 성공 - 강아지 없는 일반 채팅방")
    void createChatRoom_Success_WithoutDog() {
        // Given
        ChatRoomCreateRequestDto requestDto = new ChatRoomCreateRequestDto(null, 2L); // dogId 없음

        when(chatRoomValidator.validateAndGetMember(1L)).thenReturn(currentMember);
        when(chatRoomValidator.validateAndGetMember(2L)).thenReturn(otherMember);
        when(chatRoomValidator.validateAndGetDog(null, currentMember, otherMember)).thenReturn(null);
        when(duplicateChecker.findExistingChatRoom(null, 1L, 2L)).thenReturn(Optional.empty());

        ChatRoom chatRoomWithoutDog = ChatRoom.builder().build();
        ReflectionTestUtils.setField(chatRoomWithoutDog, "chatRoomId", 2L);
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(chatRoomWithoutDog);

        // When
        ChatRoomResponseDto result = chatRoomService.createChatRoom(requestDto, sessionUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getChatRoomId()).isEqualTo(2L);
        assertThat(result.getDogId()).isNull();

        verify(chatRoomValidator).validateAndGetDog(null, currentMember, otherMember);
        verify(duplicateChecker).findExistingChatRoom(null, 1L, 2L);
    }

    @Test
    @DisplayName("채팅방 나가기 성공")
    void leaveChatRoom_Success() {
        // Given
        Long chatRoomId = 1L;
        Long memberId = 1L;

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));

        // When
        chatRoomService.leaveChatRoom(chatRoomId, memberId);

        // Then
        verify(chatRoomRepository).findById(chatRoomId);
        // chatRoom.removeMember는 실제 엔티티 메서드이므로 verify하지 않음
    }

    @Test
    @DisplayName("채팅방 나가기 실패 - 존재하지 않는 채팅방")
    void leaveChatRoom_Fail_ChatRoomNotFound() {
        // Given
        Long chatRoomId = 999L;
        Long memberId = 1L;

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> chatRoomService.leaveChatRoom(chatRoomId, memberId))
                .isInstanceOf(ChatException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CHAT_ROOM_NOT_FOUND);

        verify(chatRoomRepository).findById(chatRoomId);
    }

    @Test
    @DisplayName("채팅방 재입장 성공 - 기존 멤버 재활성화")
    void rejoinChatRoom_Success_ExistingMember() {
        // Given
        Long chatRoomId = 1L;
        Long memberId = 1L;

        ChatRoomMember existingMember = ChatRoomMember.builder()
                .chatRoom(chatRoom)
                .member(currentMember)
                .isActive(false)
                .build();

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
        when(chatRoomValidator.validateAndGetMember(memberId)).thenReturn(currentMember);
        when(chatRoomMemberRepository.findByChatRoomAndMember(chatRoom, currentMember))
                .thenReturn(Optional.of(existingMember));

        // When
        chatRoomService.rejoinChatRoom(chatRoomId, memberId);

        // Then
        verify(chatRoomRepository).findById(chatRoomId);
        verify(chatRoomValidator).validateAndGetMember(memberId);
        verify(chatRoomMemberRepository).findByChatRoomAndMember(chatRoom, currentMember);
        // existingMember.reactivate()는 실제 엔티티 메서드이므로 verify하지 않음
    }

    @Test
    @DisplayName("채팅방 재입장 성공 - 새로운 멤버 추가")
    void rejoinChatRoom_Success_NewMember() {
        // Given
        Long chatRoomId = 1L;
        Long memberId = 1L;

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));
        when(chatRoomValidator.validateAndGetMember(memberId)).thenReturn(currentMember);
        when(chatRoomMemberRepository.findByChatRoomAndMember(chatRoom, currentMember))
                .thenReturn(Optional.empty());

        // When
        chatRoomService.rejoinChatRoom(chatRoomId, memberId);

        // Then
        verify(chatRoomRepository).findById(chatRoomId);
        verify(chatRoomValidator).validateAndGetMember(memberId);
        verify(chatRoomMemberRepository).findByChatRoomAndMember(chatRoom, currentMember);
        verify(chatRoomMemberRepository).save(any(ChatRoomMember.class));
    }

    @Test
    @DisplayName("채팅방 재입장 실패 - 존재하지 않는 채팅방")
    void rejoinChatRoom_Fail_ChatRoomNotFound() {
        // Given
        Long chatRoomId = 999L;
        Long memberId = 1L;

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> chatRoomService.rejoinChatRoom(chatRoomId, memberId))
                .isInstanceOf(ChatException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CHAT_ROOM_NOT_FOUND);

        verify(chatRoomRepository).findById(chatRoomId);
        verify(chatRoomValidator, never()).validateAndGetMember(any());
    }
}