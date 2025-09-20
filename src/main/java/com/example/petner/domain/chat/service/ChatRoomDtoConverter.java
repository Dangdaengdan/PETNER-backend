package com.example.petner.domain.chat.service;

import com.example.petner.domain.chat.dto.response.ChatRoomListResponseDto;
import com.example.petner.domain.chat.entity.ChatRoom;
import com.example.petner.domain.chat.repository.MessageRepository;
import com.example.petner.domain.member.entity.Member;
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
     * ChatRoom 엔티티를 ChatRoomListResponseDto로 변환
     *
     * @param chatRoom 채팅방 엔티티
     * @param currentMemberId 현재 사용자 ID
     * @return 변환된 응답 DTO
     */
    public ChatRoomListResponseDto convertToChatRoomListResponseDto(ChatRoom chatRoom, Long currentMemberId) {
        Member otherMember = determineOtherMember(chatRoom, currentMemberId);
        ChatRoomListResponseDto.OtherMemberInfo otherMemberInfo =
                new ChatRoomListResponseDto.OtherMemberInfo(otherMember.getMemberId(), otherMember.getNickname());

        ChatRoomListResponseDto.DogInfo dogInfo = createDogInfo(chatRoom);

        var lastMessage = messageRepository.findTopByChatRoomOrderBySentAtDesc(chatRoom);
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

    private Member determineOtherMember(ChatRoom chatRoom, Long currentMemberId) {
        return chatRoom.getMember1().getMemberId().equals(currentMemberId)
                ? chatRoom.getMember2() : chatRoom.getMember1();
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