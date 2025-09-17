-- V9__create_chatrooms_table.sql
-- 채팅방 테이블 생성

CREATE TABLE chat_rooms (
    chatRoomId BIGSERIAL PRIMARY KEY,
    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    dogId BIGINT,
    memberId1 BIGINT NOT NULL,
    memberId2 BIGINT NOT NULL,

    CONSTRAINT fk_chatrooms_dog FOREIGN KEY (dogId) REFERENCES dogs(dogId) ON DELETE CASCADE,
    CONSTRAINT fk_chatrooms_member1 FOREIGN KEY (memberId1) REFERENCES members(memberId) ON DELETE CASCADE,
    CONSTRAINT fk_chatrooms_member2 FOREIGN KEY (memberId2) REFERENCES members(memberId) ON DELETE CASCADE
);