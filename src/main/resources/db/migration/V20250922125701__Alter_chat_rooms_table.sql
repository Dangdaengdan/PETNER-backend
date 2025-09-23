-- V20250922124901__Alter_chat_rooms_table.sql
-- 채팅방 참여자 정보를 관리하는 member_id1, member_id2 컬럼을 삭제합니다.

ALTER TABLE chat_rooms DROP COLUMN member_id2;
ALTER TABLE chat_rooms DROP COLUMN member_id1;