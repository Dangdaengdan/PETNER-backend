-- V5__create_dogs_table.sql
-- 유기견 테이블 생성

CREATE TYPE dog_size_enum AS ENUM ('소형', '중형', '대형');
CREATE TYPE adoption_status_enum AS ENUM ('입양 가능', '입양 절차 중', '입양 완료');

CREATE TABLE dogs (
    dogId BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    breedId BIGINT NOT NULL,
    birthDate DATE NOT NULL,
    gender gender_enum NOT NULL,
    dogSize dog_size_enum NOT NULL,
    weight DECIMAL(5,2) NOT NULL,
    healthStatus TEXT,
    description TEXT,
    adoptionStatus adoption_status_enum NOT NULL,
    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    imageUrl VARCHAR(500) NOT NULL,
    memberId BIGINT NOT NULL,
    shelterId BIGINT,

    CONSTRAINT fk_dogs_member FOREIGN KEY (memberId) REFERENCES members(memberId) ON DELETE CASCADE,
    CONSTRAINT fk_dogs_shelter FOREIGN KEY (shelterId) REFERENCES shelters(shelterId),
    CONSTRAINT fk_dogs_breed FOREIGN KEY (breedId) REFERENCES breeds(breedId)
);