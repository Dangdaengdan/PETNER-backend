package com.example.petner.domain.chat.controller;

import com.example.petner.domain.chat.dto.request.ChatMessageRequestDto;
import com.example.petner.domain.chat.dto.response.ChatMessageResponseDto;
import com.example.petner.domain.chat.service.ChatMessageService;
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
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatMessageControllerSimpleTest {

    @Mock
    private ChatMessageService chatMessageService;

    @Mock
    private SimpMessageHeaderAccessor headerAccessor;

    @InjectMocks
    private ChatMessageController chatMessageController;

    private SessionUser sessionUser;
    private ChatMessageRequestDto messageRequest;
    private ChatMessageResponseDto messageResponse;

    @BeforeEach
    void setUp() {
        sessionUser = SessionUser.builder()
                .memberId(1L)
                .email("test@example.com")
                .nickname("테스트유저")
                .profileCompleted(true)
                .build();

        messageRequest = new ChatMessageRequestDto("테스트 메시지");
        messageResponse = new ChatMessageResponseDto(1L, 1L, "테스트 메시지", LocalDateTime.now());
    }

    @Test
    @DisplayName("WebSocket 메시지 전송 성공")
    void sendMessage_Success() {
        // Given
        Long chatRoomId = 1L;
        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put("sessionUser", sessionUser);

        when(headerAccessor.getSessionAttributes()).thenReturn(sessionAttributes);
        when(chatMessageService.saveMessage(eq(chatRoomId), eq(sessionUser.getMemberId()), any(ChatMessageRequestDto.class)))
                .thenReturn(messageResponse);

        // When
        ChatMessageResponseDto result = chatMessageController.sendMessage(chatRoomId, messageRequest, headerAccessor);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getMessageId()).isEqualTo(1L);
        assertThat(result.getSenderId()).isEqualTo(1L);
        assertThat(result.getContent()).isEqualTo("테스트 메시지");

        verify(chatMessageService).saveMessage(eq(chatRoomId), eq(sessionUser.getMemberId()), any(ChatMessageRequestDto.class));
    }

    @Test
    @DisplayName("WebSocket 메시지 전송 실패 - 세션 유저 없음")
    void sendMessage_Fail_NoSessionUser() {
        // Given
        Long chatRoomId = 1L;
        Map<String, Object> sessionAttributes = new HashMap<>();
        // sessionUser를 세션에 넣지 않음

        when(headerAccessor.getSessionAttributes()).thenReturn(sessionAttributes);

        // When & Then
        assertThatThrownBy(() -> chatMessageController.sendMessage(chatRoomId, messageRequest, headerAccessor))
                .isInstanceOf(ChatException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CHAT_UNAUTHORIZED_ACCESS);

        verify(chatMessageService, never()).saveMessage(any(), any(), any());
    }
}