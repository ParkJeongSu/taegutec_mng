package kr.co.aim.infra.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(name = "factory.type", havingValue = "insert")
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "kr.co.aim.infra.persistence.db2springdatajpa.insert", // 1. 이 설정은 이 패키지의 리포지토리를 담당!
        entityManagerFactoryRef = "db2EntityManagerFactory", // 2. 사용할 EntityManagerFactory 지정
        transactionManagerRef = "db2TransactionManager"      // 3. 사용할 TransactionManager 지정
)
@Profile({"scheduler","simulator","web","tex","pex"})
public class DB2InsertDataSourceConfig {

    @Value("${spring.datasource.db2.schema-name}")
    private String db2Schema;

    @Bean(name = "db2DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.db2")
    @Profile({"scheduler","simulator","web","pex","tex"})
    public DataSource db2DataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "db2EntityManagerFactory")
    @Profile({"scheduler","simulator","web","pex","tex"})
    public LocalContainerEntityManagerFactoryBean db2EntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("db2DataSource") DataSource dataSource) {

        Map<String, Object> properties = new HashMap<>();

        // 하드코딩 대신 주입받은 변수를 사용합니다.
        properties.put("hibernate.default_schema", db2Schema);

        return builder
                .dataSource(dataSource)
                .packages("kr.co.aim.infra.persistence.db2entity.insert") // 4. 이 EntityManager는 이 패키지의 엔티티만 스캔!
                .persistenceUnit("db2InsertUnit") // ★ 고유한 이름 부여
                .properties(properties)
                .build();
    }

    @Bean(name = "db2TransactionManager")
    @Profile({"scheduler","simulator","web","pex","tex"})
    public PlatformTransactionManager db2TransactionManager(
            @Qualifier("db2EntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory.getObject());
    }

    @Bean(name = "db2JdbcTemplate")
    @Profile({"scheduler","simulator","web","pex","tex"})
    public JdbcTemplate db2JdbcTemplate(@Qualifier("db2DataSource")DataSource db2DataSource) {
        return new JdbcTemplate(db2DataSource);
    }
}