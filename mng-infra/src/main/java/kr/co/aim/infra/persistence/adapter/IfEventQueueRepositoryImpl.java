package kr.co.aim.infra.persistence.adapter;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.IfEventQueue;
import kr.co.aim.domain.repository.IfEventQueueRepository;
import kr.co.aim.infra.persistence.entity.IfEventQueueEntity;
import kr.co.aim.infra.persistence.mapper.IfEventQueueMapper;
import kr.co.aim.infra.persistence.springdatajpa.IfEventQueueJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * UserRepository의 JPA 기반 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 JpaRepository에 위임합니다.
 */

@Repository
@RequiredArgsConstructor
public class IfEventQueueRepositoryImpl implements IfEventQueueRepository {

    private final IfEventQueueJpaRepository ifEventQueueJpaRepository;
    private final IfEventQueueMapper ifEventQueueMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입


    @Override
    public Optional<IfEventQueue> findById(Long id) {
        return ifEventQueueJpaRepository.findById(id).map(ifEventQueueMapper::toDomain);
    }

    @Override
    public IfEventQueue save(IfEventQueue interfaceEventLog) {
        // 1. Domain -> Entity 변환
        IfEventQueueEntity entity = ifEventQueueMapper.toEntity(interfaceEventLog);
        // 2. JPA 리포지토리를 통해 DB에 저장
        IfEventQueueEntity savedEntity = ifEventQueueJpaRepository.save(entity);
        // 3. 저장된 Entity -> Domain 변환 후 반환
        return ifEventQueueMapper.toDomain(savedEntity);
    }

    @Override
    public List<IfEventQueue> findByIfStatusOrderByCreateTimeAsc(String ifStatus) {
        return ifEventQueueJpaRepository.findByIfStatusOrderByCreateTimeAsc(ifStatus).stream().map(ifEventQueueMapper::toDomain).collect(Collectors.toList());
    }
}
