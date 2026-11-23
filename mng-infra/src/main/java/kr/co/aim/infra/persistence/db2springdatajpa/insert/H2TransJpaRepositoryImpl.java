package kr.co.aim.infra.persistence.db2springdatajpa.insert;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@RequiredArgsConstructor
@ConditionalOnProperty(name = "factory.type", havingValue = "insert")
public class H2TransJpaRepositoryImpl implements H2TransJpaRepositoryCustom {

    private final Db2SequenceProvider db2SequenceProvider;

    @Override
    public Long findNextLineId() {
        return db2SequenceProvider.getNextLineId("H2TRANS_L");
    }

    @Override
    public Long findMaxLineId() {
        return findNextLineId();
    }
}