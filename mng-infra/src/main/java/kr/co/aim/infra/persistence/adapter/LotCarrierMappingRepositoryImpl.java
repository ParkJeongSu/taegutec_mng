package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.condition.LotCarrierMappingSearchCondition;
import kr.co.aim.domain.model.LotCarrierMapping;
import kr.co.aim.domain.model.LotCarrierMappingHistory;
import kr.co.aim.domain.repository.LotCarrierMappingRepository;
import kr.co.aim.infra.persistence.entity.LotCarrierMappingEntity;
import kr.co.aim.infra.persistence.entity.LotCarrierMappingHistoryEntity;
import kr.co.aim.infra.persistence.mapper.LotCarrierMappingMapper;
import kr.co.aim.infra.persistence.springdatajpa.LotCarrierMappingJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

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
    public List<LotCarrierMapping> findByLotNameAndProductionStatusNot(String lotName, String productionStatus) {
        return jpaRepository.findByLotNameAndProductionStatusNot(lotName,productionStatus).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<LotCarrierMapping> findByLotNameAndCarrierName(String lotName, String carrierName) {
        return jpaRepository.findByLotNameAndCarrierName(lotName,carrierName).map(mapper::toDomain);
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
    public List<LotCarrierMapping> findByMngKey(Long mngKey) {
        return jpaRepository.findByMngKey(mngKey).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
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

    @Override
    public Page<LotCarrierMapping> findLotCarrierMappingWithConditions(LotCarrierMappingSearchCondition condition, Pageable pageable) {
        JPAQuery<LotCarrierMappingEntity> query = queryFactory
                .selectFrom(lotCarrierMappingEntity)
                .where(
                        lotNameContains(condition.getLotName()),
                        carrierNameContains(condition.getCarrierName()),
                        orderIdContains(condition.getOrderId()),
                        orderLineNumberContains(condition.getOrderLineNumber()),
                        mngKeyEq(condition.getMngKey()),
                        mantiRequestStateContains(condition.getMantiRequestState())
                );

        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<LotCarrierMappingEntity> content = query.fetch();
        List<LotCarrierMapping> converted = content.stream().map(mapper::toDomain).collect(Collectors.toList());

        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(lotCarrierMappingEntity.count())
                    .from(lotCarrierMappingEntity)
                    .where(
                            lotNameContains(condition.getLotName()),
                            carrierNameContains(condition.getCarrierName()),
                            orderIdContains(condition.getOrderId()),
                            orderLineNumberContains(condition.getOrderLineNumber()),
                            mngKeyEq(condition.getMngKey()),
                            mantiRequestStateContains(condition.getMantiRequestState())
                    )
                    .fetchOne();

            total = (count != null) ? count : 0L;
        } else {
            total = content.size();
        }

        return new PageImpl<>(converted, pageable, total);
    }

    @Override
    public List<LotCarrierMapping> findByOrderIdAndOrderLineNumberAndProductionStatusIn(String orderId, String orderLineNumber, List<String> productionStatus) {
        return jpaRepository.findByOrderIdAndOrderLineNumberAndProductionStatusIn(orderId,orderLineNumber,productionStatus).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<LotCarrierMapping> findLotCarrierMappingForUnpacking(String lotName, String carrierType) {
        return jpaRepository.findLotCarrierMappingForUnpacking(lotName,carrierType).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();
        if (sort.isSorted()) {
            for (Sort.Order order : sort) {
                Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                PathBuilder pathBuilder = new PathBuilder<>(lotCarrierMappingEntity.getType(), lotCarrierMappingEntity.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, lotCarrierMappingEntity.id));
        }
        return orders.toArray(new OrderSpecifier[0]);
    }

    // == 동적 쿼리를 위한 BooleanExpression 메소드들 ==

    private BooleanExpression lotNameContains(String lotName) {
        return StringUtils.hasText(lotName) ? lotCarrierMappingEntity.lotName.contains(lotName) : null;
    }

    private BooleanExpression carrierNameContains(String carrierName) {
        return StringUtils.hasText(carrierName) ? lotCarrierMappingEntity.carrierName.contains(carrierName) : null;
    }

    private BooleanExpression orderIdContains(String orderId) {
        return StringUtils.hasText(orderId) ? lotCarrierMappingEntity.orderId.contains(orderId) : null;
    }

    private BooleanExpression orderLineNumberContains(String orderLineNumber) {
        return StringUtils.hasText(orderLineNumber) ? lotCarrierMappingEntity.orderLineNumber.contains(orderLineNumber) : null;
    }

    private BooleanExpression mngKeyEq(Long mngKey) {
        return mngKey != null ? lotCarrierMappingEntity.mngKey.eq(mngKey) : null;
    }

    private BooleanExpression mantiRequestStateContains(String mantiRequestState) {
        return StringUtils.hasText(mantiRequestState) ? lotCarrierMappingEntity.mantiRequestState.contains(mantiRequestState) : null;
    }




}