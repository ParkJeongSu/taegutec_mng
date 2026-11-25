package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.repository.condition.LotSearchCondition;
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

    private final LotJpaRepository jpaRepository;
    private final LotMapper mapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Lot> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Lot> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Lot> findByLotName(String lotName) {
        return jpaRepository.findByLotName(lotName).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Lot> findByLotNameAndProductionStatusNot(String lotName, String productionStatus) {
        return jpaRepository.findByLotNameAndProductionStatusNot(lotName,productionStatus).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Lot> findByLotNameAndCarrierName(String lotName, String carrierName) {
        return jpaRepository.findByLotNameAndCarrierName(lotName,carrierName).map(mapper::toDomain);
    }

    @Override
    public Optional<Lot> findByCarrierName(String carrierName) {
        return jpaRepository.findByCarrierName(carrierName).map(mapper::toDomain);

    }

    @Override
    public List<Lot> findByOrderIdAndOrderLineNumber(String orderId, String orderLineNumber) {
        return jpaRepository.findByOrderIdAndOrderLineNumber(orderId, orderLineNumber).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Lot> findByMngKey(Long mngKey) {
        return jpaRepository.findByMngKey(mngKey).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Lot save(Lot mapping) {
        LotEntity entity = mapper.toEntity(mapping);
        LotEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        jpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public List<LotHistory> findHistoryByPeriod(LocalDateTime start, LocalDateTime end) {
        List<LotHistoryEntity> entities = queryFactory
                .selectFrom(lotHistoryEntity)
                .where(lotHistoryEntity.eventTime.between(start, end))
                .orderBy(lotHistoryEntity.lotName.asc(), lotHistoryEntity.eventTime.asc())
                .fetch();

        List<LotHistory> domains = new ArrayList<>();
        for (LotHistoryEntity entity : entities) {
            domains.add(mapper.toDomain(entity));
        }
        return domains;
    }

    @Override
    public List<Lot> findByMantiRequestStateAndMantiRequestTimeBeforeAndMantiReplyTimeIsNull(String mantiRequestState, LocalDateTime thresholdTime) {
        List<LotEntity> entities = jpaRepository
                .findByMantiRequestStateAndMantiRequestTimeBeforeAndMantiReplyTimeIsNull(mantiRequestState, thresholdTime);

        List<Lot> domains = new ArrayList<>();
        for (LotEntity entity : entities) {
            domains.add(mapper.toDomain(entity));
        }
        return domains;
    }

    @Override
    public Page<Lot> findLotWithConditions(LotSearchCondition condition, Pageable pageable) {
        JPAQuery<LotEntity> query = queryFactory
                .selectFrom(lotEntity)
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

        List<LotEntity> content = query.fetch();
        List<Lot> converted = content.stream().map(mapper::toDomain).collect(Collectors.toList());

        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(lotEntity.count())
                    .from(lotEntity)
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
    public List<Lot> findByOrderIdAndOrderLineNumberAndProductionStatusIn(String orderId, String orderLineNumber, List<String> productionStatus) {
        return jpaRepository.findByOrderIdAndOrderLineNumberAndProductionStatusIn(orderId,orderLineNumber,productionStatus).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Lot> findLotForUnpacking(String lotName, String carrierType) {
        return jpaRepository.findLotForUnpacking(lotName,carrierType).stream().map(mapper::toDomain).collect(Collectors.toList());
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

    private BooleanExpression carrierNameContains(String carrierName) {
        return StringUtils.hasText(carrierName) ? lotEntity.carrierName.contains(carrierName) : null;
    }

    private BooleanExpression orderIdContains(String orderId) {
        return StringUtils.hasText(orderId) ? lotEntity.orderId.contains(orderId) : null;
    }

    private BooleanExpression orderLineNumberContains(String orderLineNumber) {
        return StringUtils.hasText(orderLineNumber) ? lotEntity.orderLineNumber.contains(orderLineNumber) : null;
    }

    private BooleanExpression mngKeyEq(Long mngKey) {
        return mngKey != null ? lotEntity.mngKey.eq(mngKey) : null;
    }

    private BooleanExpression mantiRequestStateContains(String mantiRequestState) {
        return StringUtils.hasText(mantiRequestState) ? lotEntity.mantiRequestState.contains(mantiRequestState) : null;
    }




}