package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.repository.condition.LotGroupSearchCondition;
import kr.co.aim.domain.model.LotGroup;
import kr.co.aim.domain.model.LotGroupHistory;
import kr.co.aim.domain.repository.LotGroupRepository;
import kr.co.aim.infra.persistence.entity.LotGroupEntity;
import kr.co.aim.infra.persistence.entity.LotGroupHistoryEntity;
import kr.co.aim.infra.persistence.mapper.LotGroupMapper;
import kr.co.aim.infra.persistence.springdatajpa.LotGroupJpaRepository;
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

import static kr.co.aim.infra.persistence.entity.QLotGroupEntity.lotGroupEntity;
import static kr.co.aim.infra.persistence.entity.QLotGroupHistoryEntity.lotGroupHistoryEntity;

@Repository
@RequiredArgsConstructor
public class LotGroupRepositoryImpl implements LotGroupRepository {

    private final LotGroupJpaRepository lotGroupJpaRepository;
    private final LotGroupMapper lotGroupMapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<LotGroup> findAll() {
        return lotGroupJpaRepository.findAll().stream()
                .map(lotGroupMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<LotGroup> findById(Long id) {
        return lotGroupJpaRepository.findById(id)
                .map(lotGroupMapper::toDomain);
    }

    @Override
    public Optional<LotGroup> findByLotGroupName(String lotGroupName) {
        return lotGroupJpaRepository.findByLotGroupName(lotGroupName)
                .map(lotGroupMapper::toDomain);
    }

    @Override
    public LotGroup save(LotGroup lot) {
        LotGroupEntity entity = lotGroupMapper.toEntity(lot);
        LotGroupEntity savedEntity = lotGroupJpaRepository.save(entity);
        return lotGroupMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        lotGroupJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public List<LotGroupHistory> findLotGroupHistoryByPeriod(LocalDateTime start, LocalDateTime end) {
        List<LotGroupHistoryEntity> entities = queryFactory
                .selectFrom(lotGroupHistoryEntity)
                .where(lotGroupHistoryEntity.eventTime.between(start, end))
                .orderBy(lotGroupHistoryEntity.lotGroupName.asc(), lotGroupHistoryEntity.eventTime.asc())
                .fetch();

        List<LotGroupHistory> domains = new ArrayList<>();
        for (LotGroupHistoryEntity entity : entities) {
            domains.add(lotGroupMapper.toDomain(entity));
        }
        return domains;
    }

    @Override
    public Page<LotGroup> findLotGroupWithConditions(LotGroupSearchCondition condition, Pageable pageable) {
        JPAQuery<LotGroupEntity> query = queryFactory
                .selectFrom(lotGroupEntity)
                .where(
                        lotGroupNameContains(condition.getLotGroupName()),
                        originalLotGroupNameContains(condition.getOriginalLotGroupName()),
                        lotGroupStatusContains(condition.getLotGroupStatus()),
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

        List<LotGroupEntity> content = query.fetch();
        List<LotGroup> converted = content.stream().map(lotGroupMapper::toDomain).collect(Collectors.toList());

        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(lotGroupEntity.count())
                    .from(lotGroupEntity)
                    .where(
                            lotGroupNameContains(condition.getLotGroupName()),
                            originalLotGroupNameContains(condition.getOriginalLotGroupName()),
                            lotGroupStatusContains(condition.getLotGroupStatus()),
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
                PathBuilder pathBuilder = new PathBuilder<>(lotGroupEntity.getType(), lotGroupEntity.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, lotGroupEntity.id));
        }
        return orders.toArray(new OrderSpecifier[0]);
    }

    // == 동적 쿼리를 위한 BooleanExpression 메소드들 ==

    private BooleanExpression lotGroupNameContains(String lotGroupName) {
        return StringUtils.hasText(lotGroupName) ? lotGroupEntity.lotGroupName.contains(lotGroupName) : null;
    }

    private BooleanExpression originalLotGroupNameContains(String originalLotGroupName) {
        return StringUtils.hasText(originalLotGroupName) ? lotGroupEntity.originalLotGroupName.contains(originalLotGroupName) : null;
    }

    private BooleanExpression lotGroupStatusContains(String lotGroupStatus) {
        return StringUtils.hasText(lotGroupStatus) ? lotGroupEntity.lotGroupStatus.contains(lotGroupStatus) : null;
    }

    private BooleanExpression itemIdContains(String itemId) {
        return StringUtils.hasText(itemId) ? lotGroupEntity.itemId.contains(itemId) : null;
    }

    private BooleanExpression totalQuantityEq(BigDecimal totalQuantity) {
        return totalQuantity != null ? lotGroupEntity.totalQuantity.eq(totalQuantity) : null;
    }

    private BooleanExpression holdStateContains(String holdState) {
        return StringUtils.hasText(holdState) ? lotGroupEntity.holdState.contains(holdState) : null;
    }

    private BooleanExpression reasonCodeContains(String reasonCode) {
        return StringUtils.hasText(reasonCode) ? lotGroupEntity.reasonCode.contains(reasonCode) : null;
    }
}