package com.example.petner.domain.chat.service;

import com.example.petner.domain.chat.entity.ChatRoom;
import com.example.petner.domain.chat.entity.ChatRoomMember;
import com.example.petner.domain.chat.repository.ChatRoomMemberRepository;
import com.example.petner.domain.member.entity.Member;
import com.example.petner.global.exception.ErrorCode;
import com.example.petner.global.exception.customException.ChatException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 채팅방 멤버 관리 전담 서비스
 * Single Responsibility Principle을 적용하여 멤버 상태 관리 책임만 담당
 *
 * 책임:
 * - 채팅방 멤버 활성화/비활성화 처리
 * - 멤버 재입장 로직 관리
 * - 멤버 권한 검증
 *
 * @author VIBE CODING Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ChatRoomMemberManager {

    private final ChatRoomMemberRepository chatRoomMemberRepository;

    /**
     * 채팅방 참여자 권한 검증 및 비활성 멤버 자동 재입장 처리
     *
     * 동작 흐름:
     * 1. 발신자가 채팅방의 멤버인지 확인 (활성/비활성 모두 포함)
     * 2. 발신자가 비활성 상태라면 자동 재입장 처리
     * 3. 상대방이 비활성 상태라면 자동 재입장 처리 (메시지를 받을 수 있도록)
     *
     * @param chatRoom 검증할 채팅방
     * @param sender 검증할 발신자
     * @throws ChatException 해당 채팅방의 멤버가 아닌 경우
     */
    @Transactional
    public void validateAndReactivateMembers(ChatRoom chatRoom, Member sender) {
        // 1. 발신자가 이 채팅방의 멤버인지 확인 (활성/비활성 상관없이)
        boolean isMember = chatRoom.getAllMembers().stream()
                .anyMatch(chatRoomMember ->
                    chatRoomMember.getMember().getMemberId().equals(sender.getMemberId()));

        if (!isMember) {
            log.warn("채팅방 접근 권한 없음 - 채팅방 ID: {}, 발신자 ID: {}",
                    chatRoom.getChatRoomId(), sender.getMemberId());
            throw new ChatException(ErrorCode.CHAT_UNAUTHORIZED_ACCESS);
        }

        // 2. 발신자가 비활성 상태라면 재입장 처리
        boolean isSenderActive = chatRoomMemberRepository.existsActiveMemberInChatRoom(
                chatRoom.getChatRoomId(), sender.getMemberId());

        if (!isSenderActive) {
            log.info("발신자 자동 재입장 처리 - 채팅방 ID: {}, 발신자 ID: {}",
                    chatRoom.getChatRoomId(), sender.getMemberId());
            reactivateMember(chatRoom, sender);
        }

        // 3. 모든 멤버 중 비활성 상태인 멤버들을 재입장 처리 (메시지를 받을 수 있도록)
        chatRoom.getAllMembers().stream()
                .filter(chatRoomMember -> !chatRoomMember.isActive())
                .forEach(inactiveMember -> {
                    log.info("비활성 멤버 자동 재입장 처리 - 채팅방 ID: {}, 멤버 ID: {}",
                            chatRoom.getChatRoomId(), inactiveMember.getMember().getMemberId());
                    try {
                        reactivateMember(chatRoom, inactiveMember.getMember());
                    } catch (Exception e) {
                        log.warn("멤버 재입장 실패 - 채팅방 ID: {}, 멤버 ID: {}, 오류: {}",
                                chatRoom.getChatRoomId(), inactiveMember.getMember().getMemberId(), e.getMessage());
                    }
                });
    }

    /**
     * 멤버를 채팅방에 재입장시키는 메서드
     *
     * @param chatRoom 채팅방 엔티티
     * @param member 재입장시킬 멤버
     */
    @Transactional
    public void reactivateMember(ChatRoom chatRoom, Member member) {
        ChatRoomMember chatRoomMember = chatRoomMemberRepository
                .findByChatRoomAndMember(chatRoom, member)
                .orElseThrow(() -> new ChatException(ErrorCode.CHAT_MEMBER_NOT_FOUND));

        // 멤버를 활성화
        chatRoomMember.reactivate();
        chatRoomMemberRepository.save(chatRoomMember);

        log.info("멤버 재입장 완료 - 채팅방 ID: {}, 멤버 ID: {}",
                chatRoom.getChatRoomId(), member.getMemberId());
    }

    /**
     * 특정 멤버가 채팅방에서 활성 상태인지 확인
     *
     * @param chatRoomId 채팅방 ID
     * @param memberId 멤버 ID
     * @return 활성 상태 여부
     */
    public boolean isActiveMemberInChatRoom(Long chatRoomId, Long memberId) {
        return chatRoomMemberRepository.existsActiveMemberInChatRoom(chatRoomId, memberId);
    }

    /**
     * 채팅방의 활성 멤버 수 조회
     *
     * @param chatRoomId 채팅방 ID
     * @return 활성 멤버 수
     */
    public long getActiveMemberCount(Long chatRoomId) {
        return chatRoomMemberRepository.countActiveMembersByChatRoomId(chatRoomId);
    }
}