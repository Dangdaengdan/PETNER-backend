package com.example.petner.domain.chat.service;

import com.example.petner.domain.chat.dto.request.ChatMessageSendDto;
import com.example.petner.domain.chat.entity.ChatRoom;
import com.example.petner.domain.chat.entity.Message;
import com.example.petner.domain.chat.repository.ChatRoomRepository;
import com.example.petner.domain.chat.repository.MessageRepository;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.member.repository.MemberRepository;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.ChatException;
import com.example.petner.global.exception.customException.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 채팅 메시지 처리 서비스
 * 메시지 저장, 검증, 브로드캐스트 기능을 담당
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ChatMessageService {

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatMessageBroadcastService broadcastService;

    /**
     * 메시지 저장 및 실시간 브로드캐스트
     *
     * @param messageDto 클라이언트로부터 받은 메시지 DTO
     */
    @Transactional
    public void saveAndBroadcastMessage(ChatMessageSendDto messageDto) {
        // 1. 채팅방 검증
        ChatRoom chatRoom = validateChatRoom(messageDto.getChatRoomId());

        // 2. 발신자 검증
        Member sender = validateSender(messageDto.getSenderId());

        // 3. 채팅방 참여자 검증
        validateChatRoomParticipant(chatRoom, sender);

        // 4. 메시지 엔티티 생성 및 저장
        Message message = createAndSaveMessage(chatRoom, sender, messageDto.getContent());

        // 5. 실시간 브로드캐스트
        broadcastService.broadcastNewMessage(message);

        log.info("Message saved and broadcasted: {} in room {}", message.getMessageId(), chatRoom.getChatRoomId());
    }

    /**
     * 채팅방 존재 여부 검증
     */
    private ChatRoom validateChatRoom(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatException(ErrorCode.CHATROOM_NOT_FOUND));
    }

    /**
     * 발신자 존재 여부 검증
     */
    private Member validateSender(Long senderId) {
        return memberRepository.findById(senderId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }

    /**
     * 발신자가 해당 채팅방의 참여자인지 검증
     */
    private void validateChatRoomParticipant(ChatRoom chatRoom, Member sender) {
        if (!chatRoom.getMember1().getMemberId().equals(sender.getMemberId()) &&
            !chatRoom.getMember2().getMemberId().equals(sender.getMemberId())) {
            throw new ChatException(ErrorCode.CHAT_UNAUTHORIZED_ACCESS);
        }
    }

    /**
     * 메시지 엔티티 생성 및 저장
     */
    private Message createAndSaveMessage(ChatRoom chatRoom, Member sender, String content) {
        Message message = Message.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(content)
                .build();

        return messageRepository.save(message);
    }
}