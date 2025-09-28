-- V20250928170000__Remove_message_columns_from_dog_applies.sql
-- dog_applies 테이블에서 메시지 관련 컬럼 제거

-- application_message 컬럼 제거
ALTER TABLE dog_applies DROP COLUMN IF EXISTS application_message;

-- response_message 컬럼 제거
ALTER TABLE dog_applies DROP COLUMN IF EXISTS response_message;