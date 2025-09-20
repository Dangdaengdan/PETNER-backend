package com.example.petner.domain.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatRoomListResponseDto {

    private Long chatRoomId;
    private OtherMemberInfo otherMemberInfo;
    private DogInfo dogInfo;
    private String lastMessageContent;
    private LocalDateTime lastMessageSentAt;

    @Getter
    @AllArgsConstructor
    public static class OtherMemberInfo {
        private Long memberId;
        private String nickname;
    }

    @Getter
    @AllArgsConstructor
    public static class DogInfo {
        private Long dogId;
        private String name;
    }
}