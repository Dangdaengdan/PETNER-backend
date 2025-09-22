package com.example.petner.domain.chat.controller;

import com.example.petner.domain.chat.dto.request.ChatMessageRequestDto;
import com.example.petner.domain.chat.dto.request.ChatRoomCreateRequestDto;
import com.example.petner.domain.chat.dto.response.ChatRoomResponseDto;
import com.example.petner.domain.chat.dto.response.ChatRoomListResponseDto;
import com.example.petner.domain.chat.dto.response.ChatMessageResponseDto;
import com.example.petner.domain.chat.dto.response.ChatRoomMemberCountResponseDto;
import com.example.petner.domain.chat.dto.response.ChatRoomActionResponseDto;
import com.example.petner.domain.chat.service.ChatRoomService;
import com.example.petner.domain.chat.service.ChatRoomQueryService;
import com.example.petner.domain.chat.service.ChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "채팅 (chats)", description = "채팅 관련 API")
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
    @Operation(summary = "채팅방 생성", description = "새로운 채팅방을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "채팅방 생성 성공")
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
    @Operation(summary = "사용자의 채팅방 목록 조회", description = "사용자의 채팅방 목록을 조회합니다")
    @ApiResponse(responseCode = "201", description = "채팅방 목록 조회 성공")
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
    @Operation(summary = "특정 채팅방의 메시지 내역 조회(페이징 o)", description = "특정 채팅방의 메시지 내역을 조회합니다")
    @ApiResponse(responseCode = "201", description = "채팅방 메시지 내역 조회 성공")
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
    @Operation(summary = "특정 채팅방의 메시지 내역 전체 조회(페이징 x)", description = "특정 채팅방의 메시지 내역을 전체 조회합니다")
    @ApiResponse(responseCode = "201", description = "채팅방 메시지 내역 전체 조회 성공")
    public ResponseEntity<List<ChatMessageResponseDto>> getAllChatRoomMessages(@PathVariable Long chatRoomId) {
        List<ChatMessageResponseDto> messages = chatMessageService.getAllChatRoomMessages(chatRoomId);
        return ResponseEntity.ok(messages);
    }

    /**
     * 채팅방 나가기 API
     * 실제 삭제가 아닌 비활성화 처리
     *
     * @param chatRoomId 나갈 채팅방 ID
     * @param memberId 나갈 멤버 ID
     * @return 처리 결과 (200 OK)
     */
    @DeleteMapping("/{chatRoomId}/members/{memberId}")
    @Operation(summary = "채팅방 나가기", description = "채팅방에서 나갑니다 (비활성화 처리)")
    @ApiResponse(responseCode = "200", description = "채팅방 나가기 성공")
    public ResponseEntity<ChatRoomActionResponseDto> leaveChatRoom(@PathVariable Long chatRoomId, @PathVariable Long memberId) {
        chatRoomService.leaveChatRoom(chatRoomId, memberId);
        ChatRoomActionResponseDto responseDto = new ChatRoomActionResponseDto(chatRoomId, memberId, "채팅방 나가기 성공");
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 채팅방 재입장 API
     * 비활성화된 멤버를 다시 활성화
     *
     * @param chatRoomId 재입장할 채팅방 ID
     * @param memberId 재입장할 멤버 ID
     * @return 처리 결과 (200 OK)
     */
    @PutMapping("/{chatRoomId}/members/{memberId}/rejoin")
    @Operation(summary = "채팅방 재입장", description = "나간 채팅방에 다시 입장합니다")
    @ApiResponse(responseCode = "200", description = "채팅방 재입장 성공")
    public ResponseEntity<ChatRoomActionResponseDto> rejoinChatRoom(@PathVariable Long chatRoomId, @PathVariable Long memberId) {
        chatRoomService.rejoinChatRoom(chatRoomId, memberId);
        ChatRoomActionResponseDto responseDto = new ChatRoomActionResponseDto(chatRoomId, memberId, "채팅방 재입장 성공");
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 채팅방 활성 멤버 수 조회 API
     * 채팅방 관리 및 통계 정보 제공
     *
     * @param chatRoomId 조회할 채팅방 ID
     * @return 활성 멤버 수 (200 OK)
     */
    @GetMapping("/{chatRoomId}/members/count")
    @Operation(summary = "채팅방 활성 멤버 수 조회", description = "채팅방의 활성 멤버 수를 조회합니다")
    @ApiResponse(responseCode = "200", description = "활성 멤버 수 조회 성공")
    public ResponseEntity<ChatRoomMemberCountResponseDto> getActiveMemberCount(@PathVariable Long chatRoomId) {
        long count = chatRoomQueryService.getActiveMemberCount(chatRoomId);
        ChatRoomMemberCountResponseDto responseDto = new ChatRoomMemberCountResponseDto(chatRoomId, count);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(
            summary = "[WS] 채팅 메시지 전송 (문서화용)",
            description = """
            ###  WebSocket STOMP 프로토콜을 통해 메시지를 전송합니다.

            - **구독(Subscribe) 주소**: `/topic/chat/{chatRoomId}`
            - **발행(Publish) 주소**: `/app/chat/{chatRoomId}`
            - **요청 본문 (Request Body)**: `ChatMessageRequestDto` 형식

            이 HTTP 엔드포인트는 호출할 수 없으며, 오직 WebSocket 명세 확인용입니다.
            """
    )
    @PostMapping("/{chatRoomId}/messages/send-doc") // 문서화를 위한 가짜 경로
    public ResponseEntity<Void> sendChatMessageForDocumentation(@PathVariable Long chatRoomId, @RequestBody ChatMessageRequestDto message) {
        // 이 메소드는 Swagger 문서화를 위한 것이므로 실제 로직은 없습니다.
        // 항상 405 Method Not Allowed를 반환하여 잘못된 사용을 방지합니다.
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
}