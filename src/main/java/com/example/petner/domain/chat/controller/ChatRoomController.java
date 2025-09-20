package com.example.petner.domain.chat.controller;

import com.example.petner.domain.chat.dto.request.ChatRoomCreateRequestDto;
import com.example.petner.domain.chat.dto.response.ChatRoomResponseDto;
import com.example.petner.domain.chat.service.ChatRoomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "chats", description = "채팅 관련 API")
@RestController
@RequestMapping("/api/v1/chats/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    /**
     * 채팅방 생성 API
     *
     * @param requestDto 채팅방 생성 요청 데이터 (member1Id, member2Id, dogId(선택))
     * @return 생성된 채팅방 정보 (201 Created)
     *
     * 비즈니스 로직:
     * 1. 요청된 멤버들이 존재하는지 검증
     * 2. dogId가 있다면 해당 강아지가 존재하는지 검증
     * 3. 동일한 두 멤버 간에 이미 채팅방이 있는지 확인
     * 4. 기존 채팅방이 있으면 그것을 반환, 없으면 새로 생성
     */
    @PostMapping("")
    public ResponseEntity<ChatRoomResponseDto> createChatRoom(@RequestBody ChatRoomCreateRequestDto requestDto) {
        // 채팅방 생성 또는 기존 채팅방 반환
        ChatRoomResponseDto responseDto = chatRoomService.createChatRoom(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}