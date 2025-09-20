package com.example.petner.domain.chat.service;

import com.example.petner.domain.chat.dto.request.ChatRoomCreateRequestDto;
import com.example.petner.domain.chat.dto.response.ChatRoomResponseDto;
import com.example.petner.domain.chat.entity.ChatRoom;
import com.example.petner.domain.chat.repository.ChatRoomRepository;
import com.example.petner.domain.dog.entity.Dog;
import com.example.petner.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
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
     * 4. 채팅방 처리: 기존 채팅방이 있으면 반환, 없으면 새로 생성
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
        Optional<ChatRoom> existingChatRoom = duplicateChecker.findExistingChatRoom(dog, member1, member2);

        if (existingChatRoom.isPresent()) {
            // 기존 채팅방이 있다면 그것을 반환 (중복 방지)
            return new ChatRoomResponseDto(existingChatRoom.get());
        }

        // 4. 새로운 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .dog(dog)                // 강아지 정보 (선택적)
                .member1(member1)        // 첫 번째 멤버
                .member2(member2)        // 두 번째 멤버
                .build();

        // 5. 데이터베이스에 저장
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        // 6. 응답 DTO로 변환하여 반환
        return new ChatRoomResponseDto(savedChatRoom);
    }
}