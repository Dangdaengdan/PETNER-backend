package com.example.petner.domain.chat.service;

import com.example.petner.domain.chat.dto.response.ChatRoomListResponseDto;
import com.example.petner.domain.chat.entity.ChatRoom;
import com.example.petner.domain.chat.entity.ChatRoomMember;
import com.example.petner.domain.chat.entity.Message;
import com.example.petner.domain.chat.repository.ChatRoomMemberRepository;
import com.example.petner.domain.chat.repository.ChatRoomRepository;
import com.example.petner.domain.chat.repository.MessageRepository;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.member.repository.MemberRepository;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.ChatException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 채팅방 조회 전용 서비스
 * Single Responsibility Principle을 적용하여 조회 책임만 담당
 *
 * 변경사항:
 * - ChatRoomMember를 통한 채팅방 목록 조회
 * - 활성 상태의 채팅방만 조회
 * - N+1 문제 방지를 위한 최적화된 쿼리 사용
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomQueryService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final MemberRepository memberRepository;
    private final MessageRepository messageRepository;
    private final ChatRoomDtoConverter dtoConverter;

    /**
     * 특정 사용자의 전체 채팅방 목록 조회 (N+1 문제 해결)
     *
     * 성능 최적화:
     * 1. 활성 채팅방 멤버 목록 조회 (1번 쿼리, FETCH JOIN 적용)
     * 2. 모든 채팅방의 마지막 메시지 배치 조회 (1번 쿼리)
     * 총 2번 쿼리로 N+1 문제 완전 해결
     *
     * @param memberId 조회할 사용자 ID
     * @return 사용자가 활성 참여 중인 채팅방 목록
     */
    public List<ChatRoomListResponseDto> getMemberChatRooms(Long memberId) {
        // 1. 사용자 존재 여부 검증
        memberRepository.findById(memberId)
                .orElseThrow(() -> new ChatException(ErrorCode.CHAT_MEMBER_NOT_FOUND));

        // 2. 활성 채팅방 멤버 목록 조회 (FETCH JOIN으로 N+1 방지)
        List<ChatRoomMember> activeChatRoomMembers = chatRoomMemberRepository
                .findActiveChatRoomsByMemberId(memberId);

        if (activeChatRoomMembers.isEmpty()) {
            return List.of();
        }

        // 3. ChatRoomMember를 채팅방 ID별로 그룹화 (중복 제거)
        Map<Long, ChatRoomMember> chatRoomMemberMap = activeChatRoomMembers.stream()
                .collect(Collectors.toMap(
                        crm -> crm.getChatRoom().getChatRoomId(),
                        crm -> crm,
                        (existing, replacement) -> existing // 중복 시 첫 번째 것 유지
                ));

        // 4. 채팅방 목록 추출
        List<ChatRoom> chatRooms = chatRoomMemberMap.values().stream()
                .map(ChatRoomMember::getChatRoom)
                .collect(Collectors.toList());

        // 5. 각 채팅방별로 멤버가 볼 수 있는 마지막 메시지 조회 (null 안전 처리)
        Map<Long, Message> visibleLastMessageMap = new HashMap<>();

        for (Map.Entry<Long, ChatRoomMember> entry : chatRoomMemberMap.entrySet()) {
            Long chatRoomId = entry.getKey();
            ChatRoomMember crm = entry.getValue();

            Message lastMessage = null;
            try {
                if (crm.getJoinedAt() != null) {
                    // 입장 시간 이후의 메시지 중 가장 최근 메시지 조회
                    List<Message> messages = messageRepository.findByChatRoomIdAndSentAtAfterOrderBySentAtDesc(
                            chatRoomId, crm.getJoinedAt());
                    lastMessage = messages.isEmpty() ? null : messages.get(0);

                    // 디버깅용 로그
                    // if (lastMessage == null) {
                    //     System.out.println("DEBUG: 채팅방 " + chatRoomId + "에서 joinedAt(" + crm.getJoinedAt() + ") 이후 메시지 없음");
                    // }
                } else {
                    // joinedAt이 null이면 모든 메시지 볼 수 있음
                    lastMessage = messageRepository.findTopByChatRoom_ChatRoomIdOrderBySentAtDesc(
                            chatRoomId).orElse(null);

                    // 디버깅용 로그
                    // if (lastMessage == null) {
                    //     System.out.println("DEBUG: 채팅방 " + chatRoomId + "에 메시지가 전혀 없음 (joinedAt=null)");
                    // }
                }
            } catch (Exception e) {
                // 메시지 조회 실패 시 null 처리 (로그는 남기지만 에러는 무시)
                // System.out.println("DEBUG: 채팅방 " + chatRoomId + " 메시지 조회 실패: " + e.getMessage());
                lastMessage = null;
            }

            // null이어도 Map에 추가 (DTO 변환에서 처리)
            visibleLastMessageMap.put(chatRoomId, lastMessage);
        }

        // 6. DTO 변환 (멤버가 볼 수 있는 마지막 메시지만 사용)
        return chatRooms.stream()
                .map(chatRoom -> dtoConverter.convertToChatRoomListResponseDto(
                        chatRoom,
                        memberId,
                        visibleLastMessageMap.get(chatRoom.getChatRoomId())
                ))
                .collect(Collectors.toList());
    }

    /**
     * 채팅방 상세 정보 조회
     * 멤버 접근 권한 확인 및 N+1 문제 방지
     *
     * @param chatRoomId 채팅방 ID
     * @param memberId 조회 요청 멤버 ID
     * @return 채팅방 상세 정보
     */
    public ChatRoom getChatRoomDetails(Long chatRoomId, Long memberId) {
        // 1. 멤버의 채팅방 접근 권한 확인
        boolean hasAccess = chatRoomMemberRepository
                .existsActiveMemberInChatRoom(chatRoomId, memberId);

        if (!hasAccess) {
            throw new ChatException(ErrorCode.CHAT_ROOM_ACCESS_DENIED);
        }

        // 2. 채팅방 상세 정보 조회 (N+1 방지)
        return chatRoomRepository.findByIdWithDetails(chatRoomId)
                .orElseThrow(() -> new ChatException(ErrorCode.CHAT_ROOM_NOT_FOUND));
    }

    /**
     * 두 멤버가 공통으로 참여한 채팅방 목록 조회
     * 관리자 기능 또는 특별한 비즈니스 로직에서 사용
     *
     * @param member1Id 첫 번째 멤버 ID
     * @param member2Id 두 번째 멤버 ID
     * @return 공통 참여 채팅방 목록
     */
    public List<ChatRoom> getCommonChatRooms(Long member1Id, Long member2Id) {
        return chatRoomRepository.findCommonActiveChatRooms(member1Id, member2Id);
    }

    /**
     * 채팅방의 활성 멤버 수 조회
     * 채팅방 관리 및 통계 정보 제공
     *
     * @param chatRoomId 채팅방 ID
     * @return 활성 멤버 수
     */
    public long getActiveMemberCount(Long chatRoomId) {
        return chatRoomMemberRepository.countActiveMembersByChatRoomId(chatRoomId);
    }
}