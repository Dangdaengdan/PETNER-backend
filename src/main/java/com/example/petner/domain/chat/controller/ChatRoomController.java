package com.example.petner.domain.chat.controller;

import com.example.petner.domain.chat.dto.request.ChatRoomCreateRequestDto;
import com.example.petner.domain.chat.dto.response.ChatRoomResponseDto;
import com.example.petner.domain.chat.dto.response.ChatRoomListResponseDto;
import com.example.petner.domain.chat.dto.response.ChatMessageResponseDto;
import com.example.petner.domain.chat.service.ChatRoomService;
import com.example.petner.domain.chat.service.ChatRoomQueryService;
import com.example.petner.domain.chat.service.ChatMessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "chats", description = "채팅 관련 API")
@RestController
@RequestMapping("/api/v1/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatRoomQueryService chatRoomQueryService;
    private final ChatMessageService chatMessageService;

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

    /**
     * 특정 채팅방의 메시지 내역 조회 API
     *
     * ERD Messages 테이블 기준 응답:
     * - messageId: Messages.messageId (PK)
     * - senderId: Messages.senderId (FK from Members)
     * - content: Messages.content
     * - sendAt: Messages.sendAt
     *
     * @param chatRoomId 조회할 채팅방 ID
     * @param page 페이지 번호 (선택, 기본값: 0)
     * @param size 페이지 크기 (선택, 기본값: 50)
     * @return 채팅방 메시지 목록 (200 OK)
     *
     * 비즈니스 로직:
     * 1. 채팅방 존재 여부 검증
     * 2. 메시지 시간순 정렬 조회 (오래된 메시지부터)
     * 3. 페이징 적용 (대화 내역이 많을 경우 성능 최적화)
     * 4. ERD 컬럼명에 맞춘 응답 DTO 반환
     */
    @GetMapping("/{chatRoomId}/messages")
    public ResponseEntity<List<ChatMessageResponseDto>> getChatRoomMessages(
            @PathVariable Long chatRoomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        List<ChatMessageResponseDto> messages = chatMessageService.getChatRoomMessages(chatRoomId, page, size);
        return ResponseEntity.ok(messages);
    }

    /**
     * 특정 채팅방의 전체 메시지 내역 조회 API (페이징 없음)
     *
     * 프론트엔드 테스트용 엔드포인트
     * 실제 프로덕션에서는 위의 페이징 API 사용 권장
     *
     * @param chatRoomId 조회할 채팅방 ID
     * @return 채팅방 전체 메시지 목록 (200 OK)
     */
    @GetMapping("/{chatRoomId}/messages/all")
    public ResponseEntity<List<ChatMessageResponseDto>> getAllChatRoomMessages(@PathVariable Long chatRoomId) {
        List<ChatMessageResponseDto> messages = chatMessageService.getAllChatRoomMessages(chatRoomId);
        return ResponseEntity.ok(messages);
    }
}