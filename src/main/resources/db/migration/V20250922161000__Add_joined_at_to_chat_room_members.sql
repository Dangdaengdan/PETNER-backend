-- V20250922161000__Add_joined_at_to_chat_room_members.sql
-- 채팅방 멤버 테이블에 joined_at 컬럼 추가
-- 멤버의 입장/재입장 시간을 기록하여 메시지 가시성 제어에 사용

ALTER TABLE chat_room_members
ADD COLUMN joined_at TIMESTAMP NULL;

-- 기존 레코드들에 대해서는 현재 시간으로 설정 (기존 채팅방은 모든 메시지를 볼 수 있도록)
UPDATE chat_room_members
SET joined_at = CURRENT_TIMESTAMP
WHERE joined_at IS NULL;

-- 새로 생성되는 레코드는 엔티티에서 자동으로 설정됨

-- 인덱스 추가 (메시지 가시성 필터링 성능 향상)
CREATE INDEX idx_chat_room_members_joined_at ON chat_room_members(joined_at);