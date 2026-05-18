package kr.co.aim.infra.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class QueryDSLConfig {


    @Bean(name = "mssqlQueryFactory")
    @Primary
    public JPAQueryFactory jpaQueryFactory(
            @Qualifier("mssqlEntityManagerFactory") jakarta.persistence.EntityManager entityManager
    )
    {
        return new JPAQueryFactory(entityManager);
    }
    // 2. DB2 전용 팩토리
    @Bean(name = "db2QueryFactory")
    public JPAQueryFactory db2QueryFactory(
            @Qualifier("db2EntityManagerFactory") jakarta.persistence.EntityManager entityManager) {
        return new JPAQueryFactory(entityManager);
    }
}
