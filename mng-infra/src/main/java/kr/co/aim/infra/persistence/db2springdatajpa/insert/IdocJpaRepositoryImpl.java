package kr.co.aim.infra.persistence.db2springdatajpa.insert;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@RequiredArgsConstructor
@ConditionalOnProperty(name = "factory.type", havingValue = "insert")
public class IdocJpaRepositoryImpl implements IdocJpaRepositoryCustom {

    private final Db2SequenceProvider db2SequenceProvider;

    @Override
    public Long findNextLineId() {
        return db2SequenceProvider.getNextLineId("IDOC_AIM");
    }

    @Override
    public Long findMaxLineId() {
        return findNextLineId();
    }
}