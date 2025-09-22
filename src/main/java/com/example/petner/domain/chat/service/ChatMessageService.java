package com.example.petner.domain.chat.service;

import com.example.petner.domain.chat.dto.request.ChatMessageRequestDto;
import com.example.petner.domain.chat.dto.response.ChatMessageResponseDto;
import com.example.petner.domain.chat.entity.ChatRoom;
import com.example.petner.domain.chat.entity.Message;
import com.example.petner.domain.chat.repository.ChatRoomRepository;
import com.example.petner.domain.chat.repository.MessageRepository;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.member.repository.MemberRepository;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.ChatException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 채팅 메시지 처리 서비스
 *
 * 책임:
 * - 메시지 저장 및 유효성 검증 (Single Responsibility Principle)
 * - 채팅방별 메시지 조회 기능
 * - 비즈니스 로직 캡슐화 (Information Hiding)
 * - 트랜잭션 관리
 *
 * @author VIBE CODING Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ChatMessageService {

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    /**
     * 메시지 저장 처리 (WebSocket 컨트롤러용)
     *
     * 동작 흐름:
     * 1. 채팅방 및 발신자 유효성 검증
     * 2. 채팅방 참여자 권한 검증
     * 3. 메시지 엔티티 생성 및 저장
     * 4. 응답 DTO 변환 후 반환
     *
     * @param chatRoomId WebSocket 경로에서 추출한 채팅방 ID
     * @param messageDto 클라이언트로부터 받은 메시지 DTO
     * @return 저장된 메시지의 응답 DTO
     *
     * @throws IllegalArgumentException 잘못된 채팅방 ID 또는 발신자 ID
     * @throws ChatException 채팅 관련 비즈니스 로직 위반
     */
    @Transactional
    public ChatMessageResponseDto saveMessage(Long chatRoomId, ChatMessageRequestDto messageDto) {
        log.info("메시지 저장 요청 - 채팅방 ID: {}, 발신자 ID: {}, 내용: {}",
                chatRoomId, messageDto.getSenderId(), messageDto.getContent());

        try {
            // 1. 채팅방 검증 (Fail-Fast Principle)
            ChatRoom chatRoom = validateChatRoom(chatRoomId);

            // 2. 발신자 검증
            Member sender = validateSender(messageDto.getSenderId());

            // 3. 채팅방 참여자 권한 검증
            validateChatRoomParticipant(chatRoom, sender);

            // 4. 메시지 엔티티 생성 및 저장
            Message message = createAndSaveMessage(chatRoom, sender, messageDto.getContent());

            // 5. 채팅방 마지막 메시지 시간 갱신 (최근 업데이트된 채팅방이 상위에 오도록)
            chatRoom.updateLastMessageTime();
            chatRoomRepository.save(chatRoom); // 변경된 updated_at을 데이터베이스에 반영

            // 6. 응답 DTO 변환
            ChatMessageResponseDto responseDto = new ChatMessageResponseDto(message);

            log.info("메시지 저장 완료 - 메시지 ID: {}", message.getMessageId());
            return responseDto;

        } catch (Exception e) {
            log.error("메시지 저장 실패 - 채팅방 ID: {}, 발신자 ID: {}, 오류: {}",
                    chatRoomId, messageDto.getSenderId(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 특정 채팅방의 메시지 목록 조회
     *
     * ERD Messages 테이블 기준:
     * - messageId, senderId, content, sendAt 컬럼 매핑
     * - 시간순 정렬 (오래된 메시지부터)
     *
     * @param chatRoomId 채팅방 고유 식별자
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 메시지 응답 DTO 리스트
     *
     * @throws ChatException 존재하지 않는 채팅방
     */
    public List<ChatMessageResponseDto> getChatRoomMessages(Long chatRoomId, int page, int size) {
        log.info("채팅방 메시지 조회 - 채팅방 ID: {}, 페이지: {}, 크기: {}",
                chatRoomId, page, size);

        // 1. 채팅방 존재 여부 검증
        validateChatRoomExists(chatRoomId);

        // 2. 페이징 설정 (시간순 정렬)
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.ASC, "sentAt"));

        // 3. 메시지 조회 및 DTO 변환
        List<Message> messages = messageRepository.findByChatRoom_ChatRoomId(chatRoomId, pageable);

        List<ChatMessageResponseDto> responseDtos = messages.stream()
                .map(ChatMessageResponseDto::new)
                .toList();

        log.info("채팅방 메시지 조회 완료 - {} 개 메시지 반환", responseDtos.size());
        return responseDtos;
    }

    /**
     * 특정 채팅방의 모든 메시지 조회 (페이징 없음)
     *
     * @param chatRoomId 채팅방 고유 식별자
     * @return 메시지 응답 DTO 리스트
     */
    public List<ChatMessageResponseDto> getAllChatRoomMessages(Long chatRoomId) {
        log.info("채팅방 전체 메시지 조회 - 채팅방 ID: {}", chatRoomId);

        // 1. 채팅방 존재 여부 검증
        validateChatRoomExists(chatRoomId);

        // 2. 모든 메시지 조회 (시간순 정렬)
        List<Message> messages = messageRepository.findByChatRoom_ChatRoomIdOrderBySentAtAsc(chatRoomId);

        List<ChatMessageResponseDto> responseDtos = messages.stream()
                .map(ChatMessageResponseDto::new)
                .toList();

        log.info("채팅방 전체 메시지 조회 완료 - {} 개 메시지 반환", responseDtos.size());
        return responseDtos;
    }

    /**
     * 채팅방 존재 여부 검증 (조회용)
     *
     * @param chatRoomId 검증할 채팅방 ID
     * @throws ChatException 채팅방이 존재하지 않는 경우
     */
    private void validateChatRoomExists(Long chatRoomId) {
        if (!chatRoomRepository.existsById(chatRoomId)) {
            throw new ChatException(ErrorCode.CHAT_ROOM_NOT_FOUND);
        }
    }

    /**
     * 채팅방 존재 여부 검증 (엔티티 반환)
     *
     * @param chatRoomId 검증할 채팅방 ID
     * @return 검증된 ChatRoom 엔티티
     * @throws ChatException 채팅방이 존재하지 않는 경우
     */
    private ChatRoom validateChatRoom(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatException(ErrorCode.CHAT_ROOM_NOT_FOUND));
    }

    /**
     * 발신자 존재 여부 검증
     *
     * @param senderId 검증할 발신자 ID
     * @return 검증된 Member 엔티티
     * @throws ChatException 발신자가 존재하지 않는 경우
     */
    private Member validateSender(Long senderId) {
        return memberRepository.findById(senderId)
                .orElseThrow(() -> new ChatException(ErrorCode.CHAT_MEMBER_NOT_FOUND));
    }

    /**
     * 채팅방 참여자 권한 검증
     * ChatRoomMember를 통해 활성 멤버인지 확인
     *
     * @param chatRoom 검증할 채팅방
     * @param sender 검증할 발신자
     * @throws ChatException 해당 채팅방의 활성 참여자가 아닌 경우
     */
    private void validateChatRoomParticipant(ChatRoom chatRoom, Member sender) {
        boolean isActiveMember = chatRoom.getActiveMembers().stream()
                .anyMatch(chatRoomMember ->
                    chatRoomMember.getMember().getMemberId().equals(sender.getMemberId()));

        if (!isActiveMember) {
            throw new ChatException(ErrorCode.CHAT_UNAUTHORIZED_ACCESS);
        }
    }

    /**
     * 메시지 엔티티 생성 및 저장
     *
     * @param chatRoom 채팅방 엔티티
     * @param sender 발신자 Member 엔티티
     * @param content 메시지 내용
     * @return 저장된 Message 엔티티
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