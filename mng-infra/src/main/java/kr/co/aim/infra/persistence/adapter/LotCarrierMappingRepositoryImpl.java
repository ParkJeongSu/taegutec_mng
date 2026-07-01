package kr.co.aim.infra.persistence.adapter;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.LotCarrierMapping;
import kr.co.aim.domain.model.LotCarrierMappingHistory;
import kr.co.aim.domain.repository.LotCarrierMappingRepository;
import kr.co.aim.infra.persistence.entity.LotCarrierMappingEntity;
import kr.co.aim.infra.persistence.entity.LotCarrierMappingHistoryEntity;
import kr.co.aim.infra.persistence.mapper.LotCarrierMappingMapper;
import kr.co.aim.infra.persistence.springdatajpa.LotCarrierMappingJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static kr.co.aim.infra.persistence.entity.QLotCarrierMappingEntity.lotCarrierMappingEntity;
import static kr.co.aim.infra.persistence.entity.QLotCarrierMappingHistoryEntity.lotCarrierMappingHistoryEntity;

@Repository
@RequiredArgsConstructor
public class LotCarrierMappingRepositoryImpl implements LotCarrierMappingRepository {

    private final LotCarrierMappingJpaRepository jpaRepository;
    private final LotCarrierMappingMapper mapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<LotCarrierMapping> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<LotCarrierMapping> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<LotCarrierMapping> findByLotName(String lotName) {
        return jpaRepository.findByLotName(lotName).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<LotCarrierMapping> findByCarrierName(String carrierName) {
        return jpaRepository.findByCarrierName(carrierName).map(mapper::toDomain);

    }

    @Override
    public List<LotCarrierMapping> findByOrderIdAndOrderLineNumber(String orderId, String orderLineNumber) {
        return jpaRepository.findByOrderIdAndOrderLineNumber(orderId, orderLineNumber).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<LotCarrierMapping> findByMngKey(Long mngKey) {
        return jpaRepository.findByMngKey(mngKey)
                .map(mapper::toDomain);
    }

    @Override
    public LotCarrierMapping save(LotCarrierMapping mapping) {
        LotCarrierMappingEntity entity = mapper.toEntity(mapping);
        LotCarrierMappingEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        jpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public List<LotCarrierMappingHistory> findHistoryByPeriod(LocalDateTime start, LocalDateTime end) {
        List<LotCarrierMappingHistoryEntity> entities = queryFactory
                .selectFrom(lotCarrierMappingHistoryEntity)
                .where(lotCarrierMappingHistoryEntity.eventTime.between(start, end))
                .orderBy(lotCarrierMappingHistoryEntity.lotName.asc(), lotCarrierMappingHistoryEntity.eventTime.asc())
                .fetch();

        List<LotCarrierMappingHistory> domains = new ArrayList<>();
        for (LotCarrierMappingHistoryEntity entity : entities) {
            domains.add(mapper.toDomain(entity));
        }
        return domains;
    }

    @Override
    public List<LotCarrierMapping> findByMantiRequestStateAndMantiRequestTimeBeforeAndMantiReplyTimeIsNull(String mantiRequestState, LocalDateTime thresholdTime) {
        List<LotCarrierMappingEntity> entities = jpaRepository
                .findByMantiRequestStateAndMantiRequestTimeBeforeAndMantiReplyTimeIsNull(mantiRequestState, thresholdTime);

        List<LotCarrierMapping> domains = new ArrayList<>();
        for (LotCarrierMappingEntity entity : entities) {
            domains.add(mapper.toDomain(entity));
        }
        return domains;
    }
}