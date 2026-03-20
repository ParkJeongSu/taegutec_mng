package kr.co.aim.infra.config;

import org.springframework.beans.factory.annotation.Qualifier;
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

@Configuration
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "kr.co.aim.infra.persistence.db2springdatajpa.powder", // 1. 이 설정은 이 패키지의 리포지토리를 담당!
        entityManagerFactoryRef = "db2EntityManagerFactory", // 2. 사용할 EntityManagerFactory 지정
        transactionManagerRef = "db2TransactionManager"      // 3. 사용할 TransactionManager 지정
)
@Profile({"scheduler","simulator"})
public class DB2PowderDataSourceConfig {

    @Bean(name = "db2DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.db2")
    @Profile({"scheduler","simulator"})
    public DataSource db2DataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "db2EntityManagerFactory")
    @Profile({"scheduler","simulator"})
    public LocalContainerEntityManagerFactoryBean db2EntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("db2DataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("kr.co.aim.infra.persistence.db2entity.powder") // 4. 이 EntityManager는 이 패키지의 엔티티만 스캔!
                .build();
    }

    @Bean(name = "db2TransactionManager")
    @Profile({"scheduler","simulator"})
    public PlatformTransactionManager db2TransactionManager(
            @Qualifier("db2EntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory.getObject());
    }

    @Bean(name = "db2JdbcTemplate")
    @Profile({"scheduler","simulator"})
    public JdbcTemplate db2JdbcTemplate(@Qualifier("db2DataSource")DataSource db2DataSource) {
        return new JdbcTemplate(db2DataSource);
    }
}