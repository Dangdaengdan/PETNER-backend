-- V5__create_dogs_table.sql
-- 유기견 테이블 생성

CREATE TYPE dog_size_enum AS ENUM ('소형', '중형', '대형');
CREATE TYPE adoption_status_enum AS ENUM ('입양 가능', '입양 절차 중', '입양 완료');

CREATE TABLE dogs (
    dog_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    breed_id BIGINT NOT NULL,
    birth_date DATE NOT NULL,
    gender gender_enum NOT NULL,
    dog_size dog_size_enum NOT NULL,
    weight DECIMAL(5,2) NOT NULL,
    health_status TEXT,
    description TEXT,
    adoption_status adoption_status_enum NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    image_url VARCHAR(500) NOT NULL,
    member_id BIGINT NOT NULL,
    shelter_id BIGINT,

    CONSTRAINT fk_dogs_member FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE,
    CONSTRAINT fk_dogs_shelter FOREIGN KEY (shelter_id) REFERENCES shelters(shelter_id),
    CONSTRAINT fk_dogs_breed FOREIGN KEY (breed_id) REFERENCES breeds(breed_id)
);