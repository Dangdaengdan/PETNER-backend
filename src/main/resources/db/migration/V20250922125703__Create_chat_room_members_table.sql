-- V20250922124903__Create_chat_room_members_table.sql
-- 채팅방과 멤버의 관계 및 사용자별 상태를 관리하기 위한 chat_room_members 테이블을 생성합니다.

CREATE TABLE chat_room_members (
                                   id BIGSERIAL  PRIMARY KEY,
                                   chat_room_id BIGINT NOT NULL,
                                   member_id BIGINT NOT NULL,
                                   is_active BOOLEAN NOT NULL DEFAULT TRUE,
                                   exited_at TIMESTAMP NULL,

                                   CONSTRAINT fk_chatroommembers_to_chatrooms FOREIGN KEY (chat_room_id) REFERENCES chat_rooms(chat_room_id),
                                   CONSTRAINT fk_chatroommembers_to_members FOREIGN KEY (member_id) REFERENCES members(member_id),
                                   CONSTRAINT uk_chatroom_member UNIQUE (chat_room_id, member_id)
);