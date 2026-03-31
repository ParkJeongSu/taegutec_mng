package kr.co.aim.infra.persistence.adapter;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.InterfaceEventLog;
import kr.co.aim.domain.repository.InterfaceEventLogRepository;
import kr.co.aim.infra.persistence.entity.InterfaceEventLogEntity;
import kr.co.aim.infra.persistence.mapper.InterfaceEventLogMapper;
import kr.co.aim.infra.persistence.springdatajpa.InterfaceEventLogJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * UserRepository의 JPA 기반 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 JpaRepository에 위임합니다.
 */

@Repository
@RequiredArgsConstructor
public class InterfaceEventLogRepositoryImpl implements InterfaceEventLogRepository {

    private final InterfaceEventLogJpaRepository interfaceEventLogJpaRepository;
    private final InterfaceEventLogMapper interfaceEventLogMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입


    @Override
    public InterfaceEventLog save(InterfaceEventLog interfaceEventLog) {
        // 1. Domain -> Entity 변환
        InterfaceEventLogEntity entity = interfaceEventLogMapper.toEntity(interfaceEventLog);
        // 2. JPA 리포지토리를 통해 DB에 저장
        InterfaceEventLogEntity savedEntity = interfaceEventLogJpaRepository.save(entity);
        // 3. 저장된 Entity -> Domain 변환 후 반환
        return interfaceEventLogMapper.toDomain(savedEntity);
    }

    @Override
    public List<InterfaceEventLog> findByIfStatusOrderByCreateTimeAsc(String ifStatus) {
        return interfaceEventLogJpaRepository.findByIfStatusOrderByCreateTimeAsc(ifStatus).stream().map(interfaceEventLogMapper::toDomain).collect(Collectors.toList());
    }
}
