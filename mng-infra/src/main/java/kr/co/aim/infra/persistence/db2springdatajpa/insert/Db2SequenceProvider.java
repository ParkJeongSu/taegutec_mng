package kr.co.aim.infra.persistence.db2springdatajpa.insert;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"web", "simulator","scheduler"})
@ConditionalOnProperty(name = "factory.type", havingValue = "insert")
public class Db2SequenceProvider {

    // ※ 주의: 다중 DataSource 설정 클래스(@Configuration)에 지정된 db2용 EntityManagerFactory 빈 이름을 넣어야 합니다.
    // (예: "db2EntityManagerFactory" 또는 "db2EntityManager")
    @PersistenceContext(unitName = "db2EntityManagerFactory")
    private EntityManager em;

    @Value("${spring.datasource.db2.schema-name}")
    private String schemaName;

    @Value("${spring.datasource.db2.driver-class-name}")
    private String driverClassName;

    public Long getNextLineId(String sequenceName) {
        String sql = buildSequenceSql(sequenceName);
        Object result = em.createNativeQuery(sql).getSingleResult();
        return ((Number) result).longValue();
    }

    private String buildSequenceSql(String sequenceName) {
        String fullSeqName = schemaName + "." + sequenceName;

        if (driverClassName.contains("sqlserver")) {
            return "SELECT NEXT VALUE FOR " + fullSeqName;
        }
        return "SELECT NEXT VALUE FOR " + fullSeqName + " FROM SYSIBM.SYSDUMMY1";
    }
}