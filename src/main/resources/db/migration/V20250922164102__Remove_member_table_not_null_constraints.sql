-- V20250922164102__Remove_member_table_not_null_constraints.sql
-- 카카오 로그인 지원을 위한 Member 테이블 NOT NULL 제약 조건 제거

-- nickname을 NULL 허용으로 변경
ALTER TABLE members ALTER COLUMN nickname DROP NOT NULL;

-- gender를 NULL 허용으로 변경
ALTER TABLE members ALTER COLUMN gender DROP NOT NULL;

-- housing_type을 NULL 허용으로 변경
ALTER TABLE members ALTER COLUMN housing_type DROP NOT NULL;

-- contact를 NULL 허용으로 변경
ALTER TABLE members ALTER COLUMN contact DROP NOT NULL;

-- location_id를 NULL 허용으로 변경
ALTER TABLE members ALTER COLUMN location_id DROP NOT NULL;