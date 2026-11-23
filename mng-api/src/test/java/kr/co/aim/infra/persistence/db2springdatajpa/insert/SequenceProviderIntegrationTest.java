package kr.co.aim.infra.persistence.db2springdatajpa.insert;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "factory.type=insert",
                "spring.profiles.active=web", // scheduler, pex, tex 프로필을 피해 RabbitMQ/스케줄러 실행 차단
                "spring.task.scheduling.enabled=false"
        }
)
class SequenceProviderIntegrationTest {

    @Autowired
    private Db2SequenceProvider db2SequenceProvider;

    @Autowired
    private IdocJpaRepository idocJpaRepository;

    @Autowired
    private H2TransJpaRepository h2TransJpaRepository;

    @Value("${spring.datasource.db2.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.db2.schema-name}")
    private String schemaName;

    @Test
    @DisplayName("mng-api 설정(application.yml)을 그대로 읽어 시퀀스가 순차적으로 발급되는지 검증한다")
    void testSequenceGeneration() {
        System.out.println("=== 테스트 대상 DB Driver: " + driverClassName + " ===");
        System.out.println("=== 테스트 대상 Schema: " + schemaName + " ===");

        // 1. IdocJpaRepository 검증
        Long idocSeq1 = idocJpaRepository.findMaxLineId();
        Long idocSeq2 = idocJpaRepository.findMaxLineId();
        System.out.println("IDOC_AIM -> 1회차: " + idocSeq1 + " / 2회차: " + idocSeq2);

        assertThat(idocSeq1).isNotNull().isPositive();
        assertThat(idocSeq2).isEqualTo(idocSeq1 + 1L);

        // 2. H2TransJpaRepository 검증
        Long h2TransSeq1 = h2TransJpaRepository.findMaxLineId();
        Long h2TransSeq2 = h2TransJpaRepository.findMaxLineId();
        System.out.println("H2TRANS_L -> 1회차: " + h2TransSeq1 + " / 2회차: " + h2TransSeq2);

        assertThat(h2TransSeq1).isNotNull().isPositive();
        assertThat(h2TransSeq2).isEqualTo(h2TransSeq1 + 1L);
    }
}