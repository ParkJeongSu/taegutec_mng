package kr.co.aim.infra.persistence.db2springdatajpa.insert;

public interface IdocJpaRepositoryCustom {
    Long findNextLineId();
    Long findMaxLineId();
}