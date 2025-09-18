-- V9__create_chatrooms_table.sql
-- 채팅방 테이블 생성

CREATE TABLE chat_rooms (
    chat_room_id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    dog_id BIGINT,
    member_id1 BIGINT NOT NULL,
    member_id2 BIGINT NOT NULL,

    CONSTRAINT fk_chatrooms_dog FOREIGN KEY (dog_id) REFERENCES dogs(dog_id) ON DELETE CASCADE,
    CONSTRAINT fk_chatrooms_member1 FOREIGN KEY (member_id1) REFERENCES members(member_id) ON DELETE CASCADE,
    CONSTRAINT fk_chatrooms_member2 FOREIGN KEY (member_id2) REFERENCES members(member_id) ON DELETE CASCADE
);