-- V10__create_messages_table.sql
-- 메시지 테이블 생성

CREATE TABLE messages (
    message_id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    sent_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    chat_room_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,

    CONSTRAINT fk_messages_chatroom FOREIGN KEY (chat_room_id) REFERENCES chat_rooms(chat_room_id) ON DELETE CASCADE,
    CONSTRAINT fk_messages_sender FOREIGN KEY (sender_id) REFERENCES members(member_id) ON DELETE CASCADE
);