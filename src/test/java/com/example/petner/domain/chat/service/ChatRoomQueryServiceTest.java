package com.example.petner.domain.chat.service;

import com.example.petner.domain.chat.dto.response.ChatRoomListResponseDto;
import com.example.petner.domain.chat.entity.ChatRoom;
import com.example.petner.domain.chat.entity.ChatRoomMember;
import com.example.petner.domain.chat.entity.Message;
import com.example.petner.domain.chat.repository.ChatRoomMemberRepository;
import com.example.petner.domain.chat.repository.ChatRoomRepository;
import com.example.petner.domain.chat.repository.MessageRepository;
import com.example.petner.domain.dog.entity.Dog;
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
class ChatRoomQueryServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatRoomMemberRepository chatRoomMemberRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChatRoomDtoConverter dtoConverter;

    @InjectMocks
    private ChatRoomQueryService chatRoomQueryService;

    private Member currentMember;
    private Member otherMember;
    private Dog dog;
    private ChatRoom chatRoom;
    private ChatRoomMember chatRoomMember;
    private Message message;

    @BeforeEach
    void setUp() {
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
        ReflectionTestUtils.setField(chatRoom, "createdAt", LocalDateTime.now());

        chatRoomMember = ChatRoomMember.builder()
                .chatRoom(chatRoom)
                .member(currentMember)
                .isActive(true)
                .build();
        ReflectionTestUtils.setField(chatRoomMember, "joinedAt", LocalDateTime.now().minusHours(1));

        message = Message.builder()
                .chatRoom(chatRoom)
                .sender(currentMember)
                .content("테스트 메시지")
                .build();
        ReflectionTestUtils.setField(message, "messageId", 1L);
        ReflectionTestUtils.setField(message, "sentAt", LocalDateTime.now());
    }

    @Test
    @DisplayName("사용자 채팅방 목록 조회 성공")
    void getMemberChatRooms_Success() {
        // Given
        Long memberId = 1L;
        List<ChatRoomMember> activeChatRoomMembers = Arrays.asList(chatRoomMember);
        List<Message> messages = Arrays.asList(message);

        ChatRoomListResponseDto expectedDto = new ChatRoomListResponseDto(
                1L,
                new ChatRoomListResponseDto.OtherMemberInfo(2L, "다른유저"),
                new ChatRoomListResponseDto.DogInfo(1L, "바둑이"),
                "테스트 메시지",
                LocalDateTime.now()
        );

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(currentMember));
        when(chatRoomMemberRepository.findActiveChatRoomsByMemberId(memberId))
                .thenReturn(activeChatRoomMembers);
        when(messageRepository.findByChatRoomIdAndSentAtAfterOrderBySentAtDesc(eq(1L), any(LocalDateTime.class)))
                .thenReturn(messages);
        when(dtoConverter.convertToChatRoomListResponseDto(chatRoom, memberId, message))
                .thenReturn(expectedDto);

        // When
        List<ChatRoomListResponseDto> result = chatRoomQueryService.getMemberChatRooms(memberId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getChatRoomId()).isEqualTo(1L);
        assertThat(result.get(0).getLastMessageContent()).isEqualTo("테스트 메시지");

        verify(memberRepository).findById(memberId);
        verify(chatRoomMemberRepository).findActiveChatRoomsByMemberId(memberId);
        verify(messageRepository).findByChatRoomIdAndSentAtAfterOrderBySentAtDesc(eq(1L), any(LocalDateTime.class));
        verify(dtoConverter).convertToChatRoomListResponseDto(chatRoom, memberId, message);
    }

    @Test
    @DisplayName("사용자 채팅방 목록 조회 실패 - 존재하지 않는 사용자")
    void getMemberChatRooms_Fail_MemberNotFound() {
        // Given
        Long memberId = 999L;

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> chatRoomQueryService.getMemberChatRooms(memberId))
                .isInstanceOf(ChatException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CHAT_MEMBER_NOT_FOUND);

        verify(memberRepository).findById(memberId);
        verify(chatRoomMemberRepository, never()).findActiveChatRoomsByMemberId(any());
    }

    @Test
    @DisplayName("사용자 채팅방 목록 조회 - 활성 채팅방 없음")
    void getMemberChatRooms_EmptyList() {
        // Given
        Long memberId = 1L;

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(currentMember));
        when(chatRoomMemberRepository.findActiveChatRoomsByMemberId(memberId))
                .thenReturn(List.of());

        // When
        List<ChatRoomListResponseDto> result = chatRoomQueryService.getMemberChatRooms(memberId);

        // Then
        assertThat(result).isEmpty();

        verify(memberRepository).findById(memberId);
        verify(chatRoomMemberRepository).findActiveChatRoomsByMemberId(memberId);
        verify(messageRepository, never()).findByChatRoomIdAndSentAtAfterOrderBySentAtDesc(any(), any());
    }

    @Test
    @DisplayName("사용자 채팅방 목록 조회 - joinedAt null인 경우")
    void getMemberChatRooms_NullJoinedAt() {
        // Given
        Long memberId = 1L;

        // joinedAt이 null인 ChatRoomMember 생성
        ChatRoomMember memberWithNullJoinedAt = ChatRoomMember.builder()
                .chatRoom(chatRoom)
                .member(currentMember)
                .isActive(true)
                .build();
        // joinedAt을 null로 설정
        ReflectionTestUtils.setField(memberWithNullJoinedAt, "joinedAt", null);

        List<ChatRoomMember> activeChatRoomMembers = Arrays.asList(memberWithNullJoinedAt);

        ChatRoomListResponseDto expectedDto = new ChatRoomListResponseDto(
                1L,
                new ChatRoomListResponseDto.OtherMemberInfo(2L, "다른유저"),
                new ChatRoomListResponseDto.DogInfo(1L, "바둑이"),
                "테스트 메시지",
                LocalDateTime.now()
        );

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(currentMember));
        when(chatRoomMemberRepository.findActiveChatRoomsByMemberId(memberId))
                .thenReturn(activeChatRoomMembers);
        when(messageRepository.findLatestMessageByChatRoomId(1L))
                .thenReturn(Optional.of(message));
        when(dtoConverter.convertToChatRoomListResponseDto(chatRoom, memberId, message))
                .thenReturn(expectedDto);

        // When
        List<ChatRoomListResponseDto> result = chatRoomQueryService.getMemberChatRooms(memberId);

        // Then
        assertThat(result).hasSize(1);

        verify(memberRepository).findById(memberId);
        verify(chatRoomMemberRepository).findActiveChatRoomsByMemberId(memberId);
        verify(messageRepository).findLatestMessageByChatRoomId(1L);
        verify(dtoConverter).convertToChatRoomListResponseDto(chatRoom, memberId, message);
    }

    @Test
    @DisplayName("사용자 채팅방 목록 조회 - 메시지 조회 예외 처리")
    void getMemberChatRooms_MessageQueryException() {
        // Given
        Long memberId = 1L;
        List<ChatRoomMember> activeChatRoomMembers = Arrays.asList(chatRoomMember);

        ChatRoomListResponseDto expectedDto = new ChatRoomListResponseDto(
                1L,
                new ChatRoomListResponseDto.OtherMemberInfo(2L, "다른유저"),
                new ChatRoomListResponseDto.DogInfo(1L, "바둑이"),
                "테스트 메시지",
                LocalDateTime.now()
        );

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(currentMember));
        when(chatRoomMemberRepository.findActiveChatRoomsByMemberId(memberId))
                .thenReturn(activeChatRoomMembers);
        when(messageRepository.findByChatRoomIdAndSentAtAfterOrderBySentAtDesc(eq(1L), any(LocalDateTime.class)))
                .thenThrow(new RuntimeException("DB 연결 오류"));
        when(dtoConverter.convertToChatRoomListResponseDto(chatRoom, memberId, null))
                .thenReturn(expectedDto);

        // When
        List<ChatRoomListResponseDto> result = chatRoomQueryService.getMemberChatRooms(memberId);

        // Then
        assertThat(result).hasSize(1);

        verify(messageRepository).findByChatRoomIdAndSentAtAfterOrderBySentAtDesc(eq(1L), any(LocalDateTime.class));
        verify(dtoConverter).convertToChatRoomListResponseDto(chatRoom, memberId, null);
    }

    @Test
    @DisplayName("채팅방 상세 정보 조회 성공")
    void getChatRoomDetails_Success() {
        // Given
        Long chatRoomId = 1L;
        Long memberId = 1L;

        when(chatRoomMemberRepository.existsActiveMemberInChatRoom(chatRoomId, memberId))
                .thenReturn(true);
        when(chatRoomRepository.findByIdWithDetails(chatRoomId))
                .thenReturn(Optional.of(chatRoom));

        // When
        ChatRoom result = chatRoomQueryService.getChatRoomDetails(chatRoomId, memberId);

        // Then
        assertThat(result).isEqualTo(chatRoom);
        assertThat(result.getChatRoomId()).isEqualTo(1L);

        verify(chatRoomMemberRepository).existsActiveMemberInChatRoom(chatRoomId, memberId);
        verify(chatRoomRepository).findByIdWithDetails(chatRoomId);
    }

    @Test
    @DisplayName("채팅방 상세 정보 조회 실패 - 접근 권한 없음")
    void getChatRoomDetails_Fail_AccessDenied() {
        // Given
        Long chatRoomId = 1L;
        Long memberId = 1L;

        when(chatRoomMemberRepository.existsActiveMemberInChatRoom(chatRoomId, memberId))
                .thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> chatRoomQueryService.getChatRoomDetails(chatRoomId, memberId))
                .isInstanceOf(ChatException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CHAT_ROOM_ACCESS_DENIED);

        verify(chatRoomMemberRepository).existsActiveMemberInChatRoom(chatRoomId, memberId);
        verify(chatRoomRepository, never()).findByIdWithDetails(any());
    }

    @Test
    @DisplayName("채팅방 상세 정보 조회 실패 - 채팅방 없음")
    void getChatRoomDetails_Fail_ChatRoomNotFound() {
        // Given
        Long chatRoomId = 999L;
        Long memberId = 1L;

        when(chatRoomMemberRepository.existsActiveMemberInChatRoom(chatRoomId, memberId))
                .thenReturn(true);
        when(chatRoomRepository.findByIdWithDetails(chatRoomId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> chatRoomQueryService.getChatRoomDetails(chatRoomId, memberId))
                .isInstanceOf(ChatException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CHAT_ROOM_NOT_FOUND);

        verify(chatRoomMemberRepository).existsActiveMemberInChatRoom(chatRoomId, memberId);
        verify(chatRoomRepository).findByIdWithDetails(chatRoomId);
    }

    @Test
    @DisplayName("공통 채팅방 조회 성공")
    void getCommonChatRooms_Success() {
        // Given
        Long member1Id = 1L;
        Long member2Id = 2L;
        List<ChatRoom> commonChatRooms = Arrays.asList(chatRoom);

        when(chatRoomRepository.findCommonActiveChatRooms(member1Id, member2Id))
                .thenReturn(commonChatRooms);

        // When
        List<ChatRoom> result = chatRoomQueryService.getCommonChatRooms(member1Id, member2Id);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(chatRoom);

        verify(chatRoomRepository).findCommonActiveChatRooms(member1Id, member2Id);
    }

    @Test
    @DisplayName("공통 채팅방 조회 - 공통 채팅방 없음")
    void getCommonChatRooms_Empty() {
        // Given
        Long member1Id = 1L;
        Long member2Id = 2L;

        when(chatRoomRepository.findCommonActiveChatRooms(member1Id, member2Id))
                .thenReturn(List.of());

        // When
        List<ChatRoom> result = chatRoomQueryService.getCommonChatRooms(member1Id, member2Id);

        // Then
        assertThat(result).isEmpty();

        verify(chatRoomRepository).findCommonActiveChatRooms(member1Id, member2Id);
    }

    @Test
    @DisplayName("활성 멤버 수 조회 성공")
    void getActiveMemberCount_Success() {
        // Given
        Long chatRoomId = 1L;
        long expectedCount = 5L;

        when(chatRoomMemberRepository.countActiveMembersByChatRoomId(chatRoomId))
                .thenReturn(expectedCount);

        // When
        long result = chatRoomQueryService.getActiveMemberCount(chatRoomId);

        // Then
        assertThat(result).isEqualTo(expectedCount);

        verify(chatRoomMemberRepository).countActiveMembersByChatRoomId(chatRoomId);
    }

    @Test
    @DisplayName("활성 멤버 수 조회 - 멤버 없음")
    void getActiveMemberCount_NoMembers() {
        // Given
        Long chatRoomId = 1L;

        when(chatRoomMemberRepository.countActiveMembersByChatRoomId(chatRoomId))
                .thenReturn(0L);

        // When
        long result = chatRoomQueryService.getActiveMemberCount(chatRoomId);

        // Then
        assertThat(result).isEqualTo(0L);

        verify(chatRoomMemberRepository).countActiveMembersByChatRoomId(chatRoomId);
    }

    @Test
    @DisplayName("채팅방 목록 조회 - 메시지 없는 채팅방")
    void getMemberChatRooms_NoMessages() {
        // Given
        Long memberId = 1L;
        List<ChatRoomMember> activeChatRoomMembers = Arrays.asList(chatRoomMember);

        ChatRoomListResponseDto expectedDto = new ChatRoomListResponseDto(
                1L,
                new ChatRoomListResponseDto.OtherMemberInfo(2L, "다른유저"),
                new ChatRoomListResponseDto.DogInfo(1L, "바둑이"),
                "아직 메시지가 없습니다",
                chatRoom.getCreatedAt()
        );

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(currentMember));
        when(chatRoomMemberRepository.findActiveChatRoomsByMemberId(memberId))
                .thenReturn(activeChatRoomMembers);
        when(messageRepository.findByChatRoomIdAndSentAtAfterOrderBySentAtDesc(eq(1L), any(LocalDateTime.class)))
                .thenReturn(List.of()); // 빈 메시지 목록
        when(dtoConverter.convertToChatRoomListResponseDto(chatRoom, memberId, null))
                .thenReturn(expectedDto);

        // When
        List<ChatRoomListResponseDto> result = chatRoomQueryService.getMemberChatRooms(memberId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastMessageContent()).isEqualTo("아직 메시지가 없습니다");

        verify(dtoConverter).convertToChatRoomListResponseDto(chatRoom, memberId, null);
    }
}