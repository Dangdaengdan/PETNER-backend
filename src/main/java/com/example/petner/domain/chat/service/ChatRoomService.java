package com.example.petner.domain.chat.service;

import com.example.petner.domain.chat.dto.request.ChatRoomCreateRequestDto;
import com.example.petner.domain.chat.dto.response.ChatRoomResponseDto;
import com.example.petner.domain.chat.entity.ChatRoom;
import com.example.petner.domain.chat.entity.ChatRoomMember;
import com.example.petner.domain.chat.repository.ChatRoomRepository;
import com.example.petner.domain.chat.repository.ChatRoomMemberRepository;
import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 채팅방 생성 전용 서비스
 * Single Responsibility Principle을 적용하여 생성 책임만 담당
 *
 * 변경사항:
 * - ChatRoomMember 엔티티를 통한 멤버 관리
 * - 채팅방 생성 시 ChatRoomMember 레코드도 함께 생성
 * - 트랜잭션 내에서 양방향 관계 설정
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatRoomValidator chatRoomValidator;
    private final ChatRoomDuplicateChecker duplicateChecker;

    /**
     * 채팅방 생성 또는 기존 채팅방 반환
     *
     * @param requestDto 채팅방 생성 요청 데이터
     * @return 생성되거나 기존의 채팅방 정보
     *
     * 처리 과정:
     * 1. 입력 검증: member1Id, member2Id로 멤버 존재 여부 확인
     * 2. 강아지 검증: dogId가 있다면 해당 강아지 존재 여부 및 소유자 확인
     * 3. 중복 체크: 동일한 두 멤버 간 기존 채팅방 존재 여부 확인
     * 4. 채팅방 처리: 기존 채팅방이 있으면 반환, 없으면 새로 생성 및 멤버 추가
     */
    @Transactional
    public ChatRoomResponseDto createChatRoom(ChatRoomCreateRequestDto requestDto) {
        // 1. 멤버 검증
        Member[] members = chatRoomValidator.validateAndGetMembers(
                requestDto.getMember1Id(),
                requestDto.getMember2Id()
        );
        Member member1 = members[0];
        Member member2 = members[1];

        // 2. 강아지 검증 (존재 여부 + 소유자 검증)
        Dog dog = chatRoomValidator.validateAndGetDog(requestDto.getDogId(), member1, member2);

        // 3. 중복 채팅방 체크
        Optional<ChatRoom> existingChatRoom = duplicateChecker.findExistingChatRoom(
                dog, member1.getMemberId(), member2.getMemberId()
        );

        if (existingChatRoom.isPresent()) {
            // 기존 채팅방이 있다면 두 멤버를 모두 재활성화하고 반환
            ChatRoom chatRoom = existingChatRoom.get();
            reactivateMembersIfNeeded(chatRoom, member1, member2);
            return new ChatRoomResponseDto(chatRoom);
        }

        // 4. 새로운 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .dog(dog)  // 강아지 정보 (선택적)
                .build();

        // 5. 데이터베이스에 저장
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        // 6. 채팅방 멤버 추가
        createAndAddChatRoomMember(savedChatRoom, member1);
        createAndAddChatRoomMember(savedChatRoom, member2);

        // 7. 응답 DTO로 변환하여 반환
        return new ChatRoomResponseDto(savedChatRoom);
    }

    /**
     * 채팅방에 멤버 추가
     * 양방향 관계를 안전하게 설정하여 데이터 일관성 보장
     *
     * @param chatRoom 채팅방
     * @param member 추가할 멤버
     */
    private void createAndAddChatRoomMember(ChatRoom chatRoom, Member member) {
        ChatRoomMember chatRoomMember = ChatRoomMember.builder()
                .chatRoom(chatRoom)
                .member(member)
                .isActive(true)
                .build();

        // 양방향 관계 설정
        chatRoom.addMember(chatRoomMember);

        // 데이터베이스에 저장
        chatRoomMemberRepository.save(chatRoomMember);
    }

    /**
     * 기존 채팅방의 멤버들을 필요시 재활성화
     * 두 멤버 중 비활성화된 멤버가 있다면 재활성화
     *
     * @param chatRoom 기존 채팅방
     * @param member1 첫 번째 멤버
     * @param member2 두 번째 멤버
     */
    private void reactivateMembersIfNeeded(ChatRoom chatRoom, Member member1, Member member2) {
        // 첫 번째 멤버 재활성화
        reactivateMemberIfNeeded(chatRoom, member1);
        // 두 번째 멤버 재활성화
        reactivateMemberIfNeeded(chatRoom, member2);
    }

    /**
     * 특정 멤버를 필요시 재활성화
     *
     * @param chatRoom 채팅방
     * @param member 재활성화할 멤버
     */
    private void reactivateMemberIfNeeded(ChatRoom chatRoom, Member member) {
        Optional<ChatRoomMember> existingMember = chatRoomMemberRepository
                .findByChatRoomAndMember(chatRoom, member);

        if (existingMember.isPresent()) {
            // 기존 멤버가 있다면 재활성화
            existingMember.get().reactivate();
        } else {
            // 기존 멤버가 없다면 새로 추가
            createAndAddChatRoomMember(chatRoom, member);
        }
    }

    /**
     * 채팅방 나가기
     * 실제 삭제가 아닌 비활성화 처리
     *
     * @param chatRoomId 채팅방 ID
     * @param memberId 나갈 멤버 ID
     */
    @Transactional
    public void leaveChatRoom(Long chatRoomId, Long memberId) {
        // 1. 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        // 2. 멤버 비활성화
        chatRoom.removeMember(memberId);
    }

    /**
     * 채팅방 재입장
     * 비활성화된 멤버를 다시 활성화
     *
     * @param chatRoomId 채팅방 ID
     * @param memberId 재입장할 멤버 ID
     */
    @Transactional
    public void rejoinChatRoom(Long chatRoomId, Long memberId) {
        // 1. 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        // 2. 멤버 조회
        Member member = chatRoomValidator.validateAndGetMember(memberId);

        // 3. 기존 ChatRoomMember 조회
        Optional<ChatRoomMember> existingMember = chatRoomMemberRepository
                .findByChatRoomAndMember(chatRoom, member);

        if (existingMember.isPresent()) {
            // 기존 멤버 재활성화
            existingMember.get().reactivate();
        } else {
            // 새로운 멤버 추가
            createAndAddChatRoomMember(chatRoom, member);
        }
    }
}