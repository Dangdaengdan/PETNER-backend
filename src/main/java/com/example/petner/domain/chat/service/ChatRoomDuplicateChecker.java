package com.example.petner.domain.chat.service;

import com.example.petner.domain.chat.entity.ChatRoom;
import com.example.petner.domain.chat.repository.ChatRoomRepository;
import com.example.petner.domain.dog.entity.Dog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 채팅방 중복 체크 로직을 담당하는 컴포넌트
 * Single Responsibility Principle을 적용하여 중복 체크 책임만 담당
 *
 * 변경사항:
 * - member1, member2 대신 member ID를 사용
 * - ChatRoomMember를 통한 멤버 관계 확인
 */
@Component
@RequiredArgsConstructor
public class ChatRoomDuplicateChecker {

    private final ChatRoomRepository chatRoomRepository;

    /**
     * 기존 채팅방 존재 여부 확인 (활성/비활성 상관없이)
     * 강아지가 있는 경우와 없는 경우를 구분하여 적절한 중복 체크 수행
     *
     * @param dog 강아지 (nullable)
     * @param member1Id 첫 번째 멤버 ID
     * @param member2Id 두 번째 멤버 ID
     * @return 기존 채팅방 (없으면 Optional.empty())
     */
    public Optional<ChatRoom> findExistingChatRoom(Dog dog, Long member1Id, Long member2Id) {
        if (dog != null) {
            // 강아지가 지정된 경우: 동일한 강아지 + 두 멤버 조합 체크 (활성/비활성 무관)
            return chatRoomRepository.findByDogAndTwoMembers(dog.getDogId(), member1Id, member2Id);
        } else {
            // 일반 채팅방인 경우: 강아지가 null이고 두 멤버가 같은 채팅방 체크 (활성/비활성 무관)
            return chatRoomRepository.findByTwoMembersAndNullDog(member1Id, member2Id);
        }
    }

    /**
     * 두 멤버가 공통으로 참여한 모든 채팅방 조회
     * 동일한 사용자들이 여러 채팅방에 참여했는지 확인할 때 사용
     *
     * @param member1Id 첫 번째 멤버 ID
     * @param member2Id 두 번째 멤버 ID
     * @return 공통 참여 채팅방 목록
     */
    public List<ChatRoom> findAllCommonChatRooms(Long member1Id, Long member2Id) {
        return chatRoomRepository.findCommonActiveChatRooms(member1Id, member2Id);
    }

    /**
     * 특정 강아지와 관련된 채팅방에서 두 멤버가 이미 채팅 중인지 확인
     *
     * @param dog 강아지
     * @param member1Id 첫 번째 멤버 ID
     * @param member2Id 두 번째 멤버 ID
     * @return 중복 여부
     */
    public boolean isDuplicateChatRoom(Dog dog, Long member1Id, Long member2Id) {
        return findExistingChatRoom(dog, member1Id, member2Id).isPresent();
    }
}