package kr.co.aim.infra.persistence.adapter;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.Lot;
import kr.co.aim.domain.model.LotHistory;
import kr.co.aim.domain.repository.LotRepository;
import kr.co.aim.infra.persistence.entity.LotEntity;
import kr.co.aim.infra.persistence.entity.LotHistoryEntity;
import kr.co.aim.infra.persistence.mapper.LotMapper;
import kr.co.aim.infra.persistence.springdatajpa.LotJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static kr.co.aim.infra.persistence.entity.QLotEntity.lotEntity;
import static kr.co.aim.infra.persistence.entity.QLotHistoryEntity.lotHistoryEntity;

@Repository
@RequiredArgsConstructor
public class LotRepositoryImpl implements LotRepository {

    private final LotJpaRepository lotJpaRepository;
    private final LotMapper lotMapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Lot> findAll() {
        return lotJpaRepository.findAll().stream()
                .map(lotMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Lot> findById(Long id) {
        return lotJpaRepository.findById(id)
                .map(lotMapper::toDomain);
    }

    @Override
    public Optional<Lot> findByLotName(String lotName) {
        return lotJpaRepository.findByLotName(lotName)
                .map(lotMapper::toDomain);
    }

    @Override
    public Lot save(Lot lot) {
        LotEntity entity = lotMapper.toEntity(lot);
        LotEntity savedEntity = lotJpaRepository.save(entity);
        return lotMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        lotJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public List<LotHistory> findLotHistoryByPeriod(LocalDateTime start, LocalDateTime end) {
        List<LotHistoryEntity> entities = queryFactory
                .selectFrom(lotHistoryEntity)
                .where(lotHistoryEntity.eventTime.between(start, end))
                .orderBy(lotHistoryEntity.lotName.asc(), lotHistoryEntity.eventTime.asc())
                .fetch();

        List<LotHistory> domains = new ArrayList<>();
        for (LotHistoryEntity entity : entities) {
            domains.add(lotMapper.toDomain(entity));
        }
        return domains;
    }
}