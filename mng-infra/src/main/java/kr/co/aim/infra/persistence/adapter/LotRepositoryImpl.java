package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.condition.LotSearchCondition;
import kr.co.aim.domain.model.Lot;
import kr.co.aim.domain.model.LotHistory;
import kr.co.aim.domain.repository.LotRepository;
import kr.co.aim.infra.persistence.entity.LotEntity;
import kr.co.aim.infra.persistence.entity.LotHistoryEntity;
import kr.co.aim.infra.persistence.mapper.LotMapper;
import kr.co.aim.infra.persistence.springdatajpa.LotJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
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

    @Override
    public Page<Lot> findLotWithConditions(LotSearchCondition condition, Pageable pageable) {
        JPAQuery<LotEntity> query = queryFactory
                .selectFrom(lotEntity)
                .where(
                        lotNameContains(condition.getLotName()),
                        originalLotNameContains(condition.getOriginalLotName()),
                        lotStatusContains(condition.getLotStatus()),
                        itemIdContains(condition.getItemId()),
                        totalQuantityEq(condition.getTotalQuantity()),
                        holdStateContains(condition.getHoldState()),
                        reasonCodeContains(condition.getReasonCode())
                );

        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<LotEntity> content = query.fetch();
        List<Lot> converted = content.stream().map(lotMapper::toDomain).collect(Collectors.toList());

        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(lotEntity.count())
                    .from(lotEntity)
                    .where(
                            lotNameContains(condition.getLotName()),
                            originalLotNameContains(condition.getOriginalLotName()),
                            lotStatusContains(condition.getLotStatus()),
                            itemIdContains(condition.getItemId()),
                            totalQuantityEq(condition.getTotalQuantity()),
                            holdStateContains(condition.getHoldState()),
                            reasonCodeContains(condition.getReasonCode())
                    )
                    .fetchOne();

            total = (count != null) ? count : 0L;
        } else {
            total = content.size();
        }

        return new PageImpl<>(converted, pageable, total);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();
        if (sort.isSorted()) {
            for (Sort.Order order : sort) {
                Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                PathBuilder pathBuilder = new PathBuilder<>(lotEntity.getType(), lotEntity.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, lotEntity.id));
        }
        return orders.toArray(new OrderSpecifier[0]);
    }

    // == 동적 쿼리를 위한 BooleanExpression 메소드들 ==

    private BooleanExpression lotNameContains(String lotName) {
        return StringUtils.hasText(lotName) ? lotEntity.lotName.contains(lotName) : null;
    }

    private BooleanExpression originalLotNameContains(String originalLotName) {
        return StringUtils.hasText(originalLotName) ? lotEntity.originalLotName.contains(originalLotName) : null;
    }

    private BooleanExpression lotStatusContains(String lotStatus) {
        return StringUtils.hasText(lotStatus) ? lotEntity.lotStatus.contains(lotStatus) : null;
    }

    private BooleanExpression itemIdContains(String itemId) {
        return StringUtils.hasText(itemId) ? lotEntity.itemId.contains(itemId) : null;
    }

    private BooleanExpression totalQuantityEq(BigDecimal totalQuantity) {
        return totalQuantity != null ? lotEntity.totalQuantity.eq(totalQuantity) : null;
    }

    private BooleanExpression holdStateContains(String holdState) {
        return StringUtils.hasText(holdState) ? lotEntity.holdState.contains(holdState) : null;
    }

    private BooleanExpression reasonCodeContains(String reasonCode) {
        return StringUtils.hasText(reasonCode) ? lotEntity.reasonCode.contains(reasonCode) : null;
    }
}