package com.example.petner;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.flyway.enabled=false",
    "opensearch.enabled=false",
    "spring.cloud.gcp.storage.enabled=false"
})
class DatabaseTest {

    @Test
    void testH2Database() {
        // H2 데이터베이스가 제대로 설정되었는지 확인하는 간단한 테스트
        // 실제 엔티티가 있다면 여기서 CRUD 테스트를 수행할 수 있습니다
    }
}