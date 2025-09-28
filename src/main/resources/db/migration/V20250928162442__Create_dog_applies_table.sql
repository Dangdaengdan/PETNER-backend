-- V20250928162442__Create_dog_applies_table.sql
-- 유기견 분양 신청 테이블 생성

-- dog_applies 테이블 생성
CREATE TABLE dog_applies (
    dog_apply_id BIGSERIAL PRIMARY KEY,
    dog_id BIGINT NOT NULL,
    applicant_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP,

    -- 외래키 제약조건
    CONSTRAINT fk_dog_applies_dog FOREIGN KEY (dog_id) REFERENCES dogs(dog_id),
    CONSTRAINT fk_dog_applies_applicant FOREIGN KEY (applicant_id) REFERENCES members(member_id),

    -- 유니크 제약조건 (한 유기견에 대해 한 사용자는 한 번만 신청 가능)
    CONSTRAINT uk_dog_applies_dog_applicant UNIQUE (dog_id, applicant_id),

    -- 체크 제약조건
    CONSTRAINT chk_dog_applies_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'))
);

-- 테이블 코멘트 (PostgreSQL 방식)
COMMENT ON TABLE dog_applies IS '유기견 분양 신청 테이블';
COMMENT ON COLUMN dog_applies.dog_apply_id IS '분양 신청 ID';
COMMENT ON COLUMN dog_applies.dog_id IS '유기견 ID';
COMMENT ON COLUMN dog_applies.applicant_id IS '신청자 ID';
COMMENT ON COLUMN dog_applies.status IS '신청 상태 (PENDING, APPROVED, REJECTED)';
COMMENT ON COLUMN dog_applies.created_at IS '신청 생성 시간';
COMMENT ON COLUMN dog_applies.updated_at IS '마지막 수정 시간';
COMMENT ON COLUMN dog_applies.processed_at IS '처리 시간 (승인/거절 시점)';


-- 성능 최적화를 위한 인덱스 생성
CREATE INDEX idx_dog_applies_dog_id ON dog_applies(dog_id);
CREATE INDEX idx_dog_applies_applicant_id ON dog_applies(applicant_id);
CREATE INDEX idx_dog_applies_status ON dog_applies(status);
CREATE INDEX idx_dog_applies_created_at ON dog_applies(created_at DESC);

-- 복합 인덱스 생성 (자주 사용되는 쿼리 조합)
CREATE INDEX idx_dog_applies_applicant_status ON dog_applies(applicant_id, status);
CREATE INDEX idx_dog_applies_dog_status ON dog_applies(dog_id, status);
CREATE INDEX idx_dog_applies_status_created_at ON dog_applies(status, created_at DESC);