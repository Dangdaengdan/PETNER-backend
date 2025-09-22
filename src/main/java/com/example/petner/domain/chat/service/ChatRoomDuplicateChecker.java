package com.example.petner.domain.chat.service;

import com.example.petner.domain.chat.entity.ChatRoom;
import com.example.petner.domain.chat.repository.ChatRoomRepository;
import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 채팅방 중복 체크 로직을 담당하는 컴포넌트
 * Single Responsibility Principle을 적용하여 중복 체크 책임만 담당
 */
@Component
@RequiredArgsConstructor
public class ChatRoomDuplicateChecker {

    private final ChatRoomRepository chatRoomRepository;

    /**
     * 기존 채팅방 존재 여부 확인
     * 강아지가 있는 경우와 없는 경우를 구분하여 적절한 중복 체크 수행
     *
     * @param dog 강아지 (nullable)
     * @param member1 첫 번째 멤버
     * @param member2 두 번째 멤버
     * @return 기존 채팅방 (없으면 Optional.empty())
     */
    public Optional<ChatRoom> findExistingChatRoom(Dog dog, Member member1, Member member2) {
        if (dog != null) {
            // 강아지가 지정된 경우: 동일한 강아지 + 두 멤버 조합 체크
            return chatRoomRepository.findByDogAndTwoMembers(dog, member1, member2);
        } else {
            // 일반 채팅방인 경우: 강아지가 null이고 두 멤버가 같은 채팅방 체크
            return chatRoomRepository.findByTwoMembersAndNullDog(member1, member2);
        }
    }
}