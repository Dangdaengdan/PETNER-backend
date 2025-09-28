-- V20250928135604__Add_deleted_column_to_dogs_table.sql
-- 유기견 소프트 삭제를 위한 deleted 컬럼 추가

-- dogs 테이블에 deleted 컬럼 추가
ALTER TABLE dogs
ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE;

-- 성능 향상을 위한 인덱스 추가 (deleted 컬럼과 함께 자주 사용되는 컬럼들과 복합 인덱스)
CREATE INDEX idx_dogs_deleted_created_at ON dogs(deleted, created_at DESC);
CREATE INDEX idx_dogs_deleted_member_id ON dogs(deleted, member_id);
CREATE INDEX idx_dogs_deleted_adoption_status ON dogs(deleted, adoption_status);