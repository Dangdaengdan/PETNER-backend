package com.example.petner.domain.chat.controller;

import com.example.petner.domain.chat.dto.request.ChatRoomCreateRequestDto;
import com.example.petner.domain.chat.dto.response.ChatRoomResponseDto;
import com.example.petner.domain.chat.dto.response.ChatRoomMemberCountResponseDto;
import com.example.petner.domain.chat.service.ChatMessageService;
import com.example.petner.domain.chat.service.ChatRoomQueryService;
import com.example.petner.domain.chat.service.ChatRoomService;
import com.example.petner.global.dto.SessionUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatRoomControllerSimpleTest {

    @Mock
    private ChatRoomService chatRoomService;

    @Mock
    private ChatRoomQueryService chatRoomQueryService;

    @Mock
    private ChatMessageService chatMessageService;

    @InjectMocks
    private ChatRoomController chatRoomController;

    @Test
    @DisplayName("채팅방 생성 성공")
    void createChatRoom_Success() {
        // Given
        ChatRoomCreateRequestDto requestDto = new ChatRoomCreateRequestDto(1L, 2L);
        SessionUser user = SessionUser.builder()
                .memberId(1L)
                .email("test@example.com")
                .nickname("테스트유저")
                .build();

        // ChatRoomResponseDto는 실제 엔티티로부터 생성하는 방식이므로 Mock 사용
        ChatRoomResponseDto mockResponse = org.mockito.Mockito.mock(ChatRoomResponseDto.class);
        when(mockResponse.getChatRoomId()).thenReturn(1L);

        when(chatRoomService.createChatRoom(any(ChatRoomCreateRequestDto.class), any(SessionUser.class)))
                .thenReturn(mockResponse);

        // When
        ResponseEntity<ChatRoomResponseDto> result = chatRoomController.createChatRoom(requestDto, user);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(mockResponse);
        assertThat(result.getBody().getChatRoomId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("활성 멤버 수 조회 성공")
    void getActiveMemberCount_Success() {
        // Given
        Long chatRoomId = 1L;
        long expectedCount = 2L;

        when(chatRoomQueryService.getActiveMemberCount(chatRoomId)).thenReturn(expectedCount);

        // When
        ResponseEntity<ChatRoomMemberCountResponseDto> result =
                chatRoomController.getActiveMemberCount(chatRoomId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getChatRoomId()).isEqualTo(chatRoomId);
        assertThat(result.getBody().getActiveMemberCount()).isEqualTo(expectedCount);
    }
}