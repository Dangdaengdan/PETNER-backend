package com.example.petner.domain.chat.service;

import com.example.petner.domain.chat.dto.response.ChatRoomListResponseDto;
import com.example.petner.domain.chat.entity.ChatRoom;
import com.example.petner.domain.chat.repository.ChatRoomRepository;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.domain.member.repository.MemberRepository;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.ChatException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    private final ChatRoomDtoConverter dtoConverter;

    /**
     * 특정 사용자의 전체 채팅방 목록 조회
     *
     * @param memberId 조회할 사용자 ID
     * @return 사용자가 참여 중인 채팅방 목록
     */
    public List<ChatRoomListResponseDto> getMemberChatRooms(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ChatException(ErrorCode.CHAT_MEMBER_NOT_FOUND));

        List<ChatRoom> chatRooms = chatRoomRepository.findMemberChatRoomsWithDetails(memberId);

        return chatRooms.stream()
                .map(chatRoom -> dtoConverter.convertToChatRoomListResponseDto(chatRoom, memberId))
                .collect(Collectors.toList());
    }
}