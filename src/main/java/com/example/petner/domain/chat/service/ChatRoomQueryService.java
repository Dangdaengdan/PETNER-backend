package com.example.petner.domain.chat.service;

import com.example.petner.domain.chat.dto.response.ChatRoomListResponseDto;
import com.example.petner.domain.chat.entity.ChatRoom;
import com.example.petner.domain.chat.entity.Message;
import com.example.petner.domain.chat.repository.ChatRoomRepository;
import com.example.petner.domain.chat.repository.MessageRepository;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.member.repository.MemberRepository;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.ChatException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 채팅방 조회 전용 서비스
 * Single Responsibility Principle을 적용하여 조회 책임만 담당
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomQueryService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final MessageRepository messageRepository;
    private final ChatRoomDtoConverter dtoConverter;

    /**
     * 특정 사용자의 전체 채팅방 목록 조회 (N+1 문제 해결)
     *
     * 성능 최적화:
     * 1. 채팅방 목록 조회 (1번 쿼리, FETCH JOIN 적용)
     * 2. 모든 채팅방의 마지막 메시지 배치 조회 (1번 쿼리)
     * 총 2번 쿼리로 N+1 문제 완전 해결
     *
     * @param memberId 조회할 사용자 ID
     * @return 사용자가 참여 중인 채팅방 목록
     */
    public List<ChatRoomListResponseDto> getMemberChatRooms(Long memberId) {
        // 1. 사용자 존재 여부 검증
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ChatException(ErrorCode.CHAT_MEMBER_NOT_FOUND));

        // 2. 채팅방 목록 조회 (FETCH JOIN으로 N+1 방지)
        List<ChatRoom> chatRooms = chatRoomRepository.findMemberChatRoomsWithDetails(memberId);

        if (chatRooms.isEmpty()) {
            return List.of();
        }

        // 3. 채팅방 ID 리스트 추출
        List<Long> chatRoomIds = chatRooms.stream()
                .map(ChatRoom::getChatRoomId)
                .collect(Collectors.toList());

        // 4. 모든 채팅방의 마지막 메시지를 한 번에 조회 (배치 쿼리)
        List<Message> lastMessages = messageRepository.findLastMessagesByChatRoomIds(chatRoomIds);

        // 5. 채팅방 ID를 키로 하는 마지막 메시지 맵 생성
        Map<Long, Message> lastMessageMap = lastMessages.stream()
                .collect(Collectors.toMap(
                        message -> message.getChatRoom().getChatRoomId(),
                        message -> message
                ));

        // 6. DTO 변환 (추가 쿼리 없이 메모리에서 처리)
        return chatRooms.stream()
                .map(chatRoom -> dtoConverter.convertToChatRoomListResponseDto(
                        chatRoom,
                        memberId,
                        lastMessageMap.get(chatRoom.getChatRoomId())
                ))
                .collect(Collectors.toList());
    }
}