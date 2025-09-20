package com.example.petner.domain.chat.controller;

import com.example.petner.domain.chat.dto.request.ChatRoomCreateRequestDto;
import com.example.petner.domain.chat.dto.response.ChatRoomResponseDto;
import com.example.petner.domain.chat.dto.response.ChatRoomListResponseDto;
import com.example.petner.domain.chat.service.ChatRoomService;
import com.example.petner.domain.chat.service.ChatRoomQueryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "chats", description = "채팅 관련 API")
@RestController
@RequestMapping("/api/v1/chats/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatRoomQueryService chatRoomQueryService;

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

    /**
     * 특정 사용자의 전체 채팅방 목록 조회 API
     *
     * @param memberId 조회할 사용자 ID
     * @return 사용자가 참여 중인 채팅방 목록 (200 OK)
     *
     * 비즈니스 로직:
     * 1. 요청된 멤버가 존재하는지 검증
     * 2. 해당 멤버가 참여 중인 모든 채팅방 조회
     * 3. 각 채팅방의 상대방 정보와 마지막 메시지 포함하여 반환
     * 4. N+1 문제 방지를 위한 효율적인 조회 수행
     */
    @GetMapping("/members/{memberId}")
    public ResponseEntity<List<ChatRoomListResponseDto>> getMemberChatRooms(@PathVariable Long memberId) {
        List<ChatRoomListResponseDto> chatRooms = chatRoomQueryService.getMemberChatRooms(memberId);
        return ResponseEntity.ok(chatRooms);
    }
}