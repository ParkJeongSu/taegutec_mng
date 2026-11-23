package kr.co.aim.infra.persistence.db2springdatajpa.insert;

public interface H2TransJpaRepositoryCustom {
    Long findNextLineId();
    Long findMaxLineId();
}