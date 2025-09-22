-- V20250922124902__Add_index_to_messages_table.sql
-- 특정 채팅방의 메시지를 시간순으로 조회하는 쿼리의 성능을 향상시키기 위해 복합 인덱스를 생성합니다.

CREATE INDEX idx_messages_on_chatroom_and_sent_at ON messages (chat_room_id, sent_at);