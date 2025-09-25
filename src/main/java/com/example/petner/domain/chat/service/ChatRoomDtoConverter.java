package com.example.petner.domain.chat.service;

import com.example.petner.domain.chat.dto.response.ChatRoomListResponseDto;
import com.example.petner.domain.chat.entity.ChatRoom;
import com.example.petner.domain.chat.entity.Message;
import com.example.petner.domain.chat.repository.MessageRepository;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.ChatException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 채팅방 엔티티를 DTO로 변환하는 컴포넌트
 * Single Responsibility Principle을 적용하여 변환 책임만 담당
 */
@Component
@RequiredArgsConstructor
public class ChatRoomDtoConverter {

    private final MessageRepository messageRepository;

    /**
     * ChatRoom 엔티티를 ChatRoomListResponseDto로 변환 (N+1 문제 발생 - 기존 호환성 유지)
     *
     * @param chatRoom 채팅방 엔티티
     * @param currentMemberId 현재 사용자 ID
     * @return 변환된 응답 DTO
     * @deprecated N+1 문제가 발생할 수 있음. convertToChatRoomListResponseDto(ChatRoom, Long, Message) 사용 권장
     */
    @Deprecated
    public ChatRoomListResponseDto convertToChatRoomListResponseDto(ChatRoom chatRoom, Long currentMemberId) {
        Member otherMember = determineOtherMember(chatRoom, currentMemberId);
        ChatRoomListResponseDto.OtherMemberInfo otherMemberInfo = null;

        if (otherMember != null) {
            otherMemberInfo = new ChatRoomListResponseDto.OtherMemberInfo(otherMember.getMemberId(), otherMember.getNickname());
        }

        ChatRoomListResponseDto.DogInfo dogInfo = createDogInfo(chatRoom);

        // 🚨 N+1 문제 발생 지점 - 개별 쿼리 실행
        var lastMessage = messageRepository.findLatestMessageByChatRoomId(chatRoom.getChatRoomId());
        String lastMessageContent = lastMessage.map(message -> message.getContent()).orElse("");
        var lastMessageSentAt = lastMessage.map(message -> message.getSentAt()).orElse(chatRoom.getCreatedAt());

        return new ChatRoomListResponseDto(
                chatRoom.getChatRoomId(),
                otherMemberInfo,
                dogInfo,
                lastMessageContent,
                lastMessageSentAt
        );
    }

    /**
     * ChatRoom 엔티티를 ChatRoomListResponseDto로 변환 (N+1 문제 해결)
     *
     * @param chatRoom 채팅방 엔티티
     * @param currentMemberId 현재 사용자 ID
     * @param lastMessage 미리 조회된 마지막 메시지 (null 가능)
     * @return 변환된 응답 DTO
     */
    public ChatRoomListResponseDto convertToChatRoomListResponseDto(
            ChatRoom chatRoom,
            Long currentMemberId,
            Message lastMessage
    ) {
        Member otherMember = determineOtherMember(chatRoom, currentMemberId);
        ChatRoomListResponseDto.OtherMemberInfo otherMemberInfo = null;

        if (otherMember != null) {
            otherMemberInfo = new ChatRoomListResponseDto.OtherMemberInfo(otherMember.getMemberId(), otherMember.getNickname());
        }

        ChatRoomListResponseDto.DogInfo dogInfo = createDogInfo(chatRoom);

        // ✅ N+1 문제 해결 - 미리 조회된 메시지 사용 (추가 쿼리 없음)
        String lastMessageContent = lastMessage != null ? lastMessage.getContent() : "아직 메시지가 없습니다";
        var lastMessageSentAt = lastMessage != null ? lastMessage.getSentAt() : chatRoom.getCreatedAt();

        // 디버깅: DTO 변환 단계에서 메시지 정보 출력
        // System.out.println("DEBUG [DTO Converter]: 채팅방 " + chatRoom.getChatRoomId() +
        //         " | lastMessage: " + (lastMessage != null ? lastMessage.getMessageId() : "null") +
        //         " | content: '" + lastMessageContent + "'");

        return new ChatRoomListResponseDto(
                chatRoom.getChatRoomId(),
                otherMemberInfo,
                dogInfo,
                lastMessageContent,
                lastMessageSentAt
        );
    }

    /**
     * 현재 사용자가 아닌 다른 멤버 찾기
     * ChatRoomMember를 통해 활성/비활성 상관없이 다른 멤버를 조회
     *
     * @param chatRoom 채팅방
     * @param currentMemberId 현재 사용자 ID
     * @return 다른 멤버 정보 (없으면 null 반환)
     */
    private Member determineOtherMember(ChatRoom chatRoom, Long currentMemberId) {
        try {
            return chatRoom.getChatRoomMembers().stream()
                    .map(chatRoomMember -> chatRoomMember.getMember())
                    .filter(member -> member != null && !member.getMemberId().equals(currentMemberId))
                    .findFirst()
                    .orElse(null); // 예외 대신 null 반환
        } catch (Exception e) {
            // 예외 발생 시 null 반환하고 로그 출력
            System.err.println("채팅방 " + chatRoom.getChatRoomId() + "에서 상대방을 찾는 중 오류 발생: " + e.getMessage());
            return null;
        }
    }

    private ChatRoomListResponseDto.DogInfo createDogInfo(ChatRoom chatRoom) {
        if (chatRoom.getDog() == null) {
            return null;
        }
        return new ChatRoomListResponseDto.DogInfo(
                chatRoom.getDog().getDogId(),
                chatRoom.getDog().getName()
        );
    }
}