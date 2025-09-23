package com.example.petner.domain.chat.controller;

import com.example.petner.domain.chat.dto.request.ChatMessageRequestDto;
import com.example.petner.domain.chat.dto.response.ChatMessageResponseDto;
import com.example.petner.domain.chat.service.ChatMessageService;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.ChatException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * 실시간 채팅 메시지 컨트롤러
 *
 * 책임:
 * - WebSocket을 통한 실시간 메시지 수신 및 브로드캐스팅
 * - 메시지 저장 처리 위임 (Single Responsibility Principle)
 * - 클라이언트와 서비스 레이어 간의 인터페이스 역할 (Interface Segregation Principle)
 *
 * @author VIBE CODING Team
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    /**
     * 채팅 메시지 수신 및 브로드캐스팅 처리
     *
     * 동작 흐름:
     * 1. 클라이언트로부터 /app/chat/{chatRoomId}로 메시지 수신
     * 2. 메시지 유효성 검증 및 저장 (서비스 레이어에 위임)
     * 3. /topic/chat/{chatRoomId}로 브로드캐스팅
     *
     * @param chatRoomId 채팅방 고유 식별자
     * @param message 클라이언트가 전송한 메시지 데이터
     * @return 브로드캐스팅할 메시지 응답 DTO
     *
     * @throws ChatException 잘못된 채팅방 ID 또는 메시지 내용
     * @throws RuntimeException 메시지 저장 실패
     */
    @MessageMapping("/chat/{chatRoomId}")
    @SendTo("/topic/chat/{chatRoomId}")
    public ChatMessageResponseDto sendMessage(
            @DestinationVariable Long chatRoomId,
            ChatMessageRequestDto message
    ) {
        try {
            log.info("메시지 수신 - 채팅방 ID: {}, 발신자 ID: {}, 내용: {}",
                    chatRoomId, message.getSenderId(), message.getContent());

            // Open/Closed Principle: 새로운 메시지 처리 로직 추가 시 서비스 레이어만 수정
            // Dependency Inversion Principle: 구체적인 구현이 아닌 추상화(인터페이스)에 의존
            ChatMessageResponseDto savedMessage = chatMessageService.saveMessage(chatRoomId, message);

            log.info("메시지 저장 완료 - 메시지 ID: {}", savedMessage.getMessageId());

            return savedMessage;

        } catch (ChatException e) {
            log.warn("잘못된 메시지 요청 - 채팅방 ID: {}, 오류: {}", chatRoomId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("메시지 처리 중 오류 발생 - 채팅방 ID: {}, 오류: {}", chatRoomId, e.getMessage(), e);
            throw new ChatException(ErrorCode.CHAT_INVALID_PAYLOAD);
        }
    }
}