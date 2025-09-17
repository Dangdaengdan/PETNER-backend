-- V10__create_messages_table.sql
-- 메시지 테이블 생성

CREATE TABLE messages (
    messageId BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    sentAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    chatRoomId BIGINT NOT NULL,
    senderId BIGINT NOT NULL,

    CONSTRAINT fk_messages_chatroom FOREIGN KEY (chatRoomId) REFERENCES chat_rooms(chatRoomId) ON DELETE CASCADE,
    CONSTRAINT fk_messages_sender FOREIGN KEY (senderId) REFERENCES members(memberId) ON DELETE CASCADE
);