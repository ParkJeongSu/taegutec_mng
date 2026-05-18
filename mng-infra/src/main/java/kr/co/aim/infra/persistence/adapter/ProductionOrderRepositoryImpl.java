package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparablePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.Utils.QueryDslUtils;
import kr.co.aim.common.condition.ProductionOrderHistorySearchCondition;
import kr.co.aim.common.condition.ProductionOrderSearchCondition;
import kr.co.aim.common.condition.ProductionOrderSummarySearchCondition;
import kr.co.aim.domain.model.ProductionOrder;
import kr.co.aim.domain.model.ProductionOrderHistory;
import kr.co.aim.domain.model.ProductionOrderSummary;
import kr.co.aim.domain.repository.ProductionOrderRepository;
import kr.co.aim.infra.persistence.entity.ProductionOrderEntity;
import kr.co.aim.infra.persistence.entity.ProductionOrderHistoryEntity;
import kr.co.aim.infra.persistence.mapper.ProductionOrderHistoryMapper;
import kr.co.aim.infra.persistence.mapper.ProductionOrderMapper;
import kr.co.aim.infra.persistence.springdatajpa.ProductionOrderJpaRepository;
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

import static kr.co.aim.infra.persistence.entity.QProductionOrderEntity.productionOrderEntity;
import static kr.co.aim.infra.persistence.entity.QProductionOrderHistoryEntity.productionOrderHistoryEntity;

/**
 * UserRepository의 JPA 기반 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 JpaRepository에 위임합니다.
 */

@Repository
@RequiredArgsConstructor
public class ProductionOrderRepositoryImpl implements ProductionOrderRepository {
    // Spring Data JPA가 자동으로 구현해주는 JPA 리포지토리. UserEntity를 다룬다.

    private final ProductionOrderJpaRepository productionOrderJpaRepository;
    private final ProductionOrderMapper productionOrderMapper;
    private final ProductionOrderHistoryMapper productionOrderHistoryMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입

    @Override
    public ProductionOrder save(ProductionOrder productionOrder) {
        ProductionOrderEntity entity = productionOrderMapper.toEntity(productionOrder);
        ProductionOrderEntity savedEntity = productionOrderJpaRepository.save(entity);
        return productionOrderMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<ProductionOrder> findById(Long id) {
        return productionOrderJpaRepository.findById(id).map(productionOrderMapper::toDomain);
    }

    @Override
    public List<ProductionOrder> findAll() {
        return productionOrderJpaRepository.findAll().stream().map(productionOrderMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        productionOrderJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public Optional<ProductionOrder> findByOrderIdAndOrderLineNumber(String orderId, String orderLineNumber) {
        return productionOrderJpaRepository.findByOrderIdAndOrderLineNumber(orderId,orderLineNumber).map(productionOrderMapper::toDomain);
    }

    @Override
    public Optional<ProductionOrder> findByGalId(String galId) {
        return productionOrderJpaRepository.findByGalId(galId).map(productionOrderMapper::toDomain);
    }

    @Override
    public List<ProductionOrder> findByEquipmentNameAndProductionOrderState(String equipmentName, String productionOrderState) {
        return productionOrderJpaRepository.findByEquipmentNameAndProductionOrderState(equipmentName,productionOrderState).stream().map(productionOrderMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<ProductionOrder> findByEquipmentNameAndProductionOrderTypeAndProductionOrderState(String equipmentName, String productionOrderType, String productionOrderState) {
        return productionOrderJpaRepository.findByEquipmentNameAndProductionOrderTypeAndProductionOrderState(equipmentName,productionOrderType,productionOrderState).stream().map(productionOrderMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<ProductionOrder> findByEquipmentNameAndProductionOrderStateInOrderByCreateTimeAsc(String equipmentName, List<String> productionOrderState) {
        return productionOrderJpaRepository.findByEquipmentNameAndProductionOrderStateInOrderByCreateTimeAsc(equipmentName,productionOrderState).stream().map(productionOrderMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<ProductionOrder> findByProductionOrderStateInOrderByCreateTimeAsc(List<String> productionOrderState) {
        return productionOrderJpaRepository.findByProductionOrderStateInOrderByCreateTimeAsc(productionOrderState).stream().map(productionOrderMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<ProductionOrder> findByCreateTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return productionOrderJpaRepository.findByCreateTimeBetween(startDateTime,endDateTime).stream().map(productionOrderMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<ProductionOrder> findByCreateTimeBetweenAndProductionOrderState(LocalDateTime startDateTime, LocalDateTime endDateTime, String productionOrderState) {
        return productionOrderJpaRepository.findByCreateTimeBetweenAndProductionOrderState(startDateTime,endDateTime,productionOrderState).stream().map(productionOrderMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Page<ProductionOrderSummary> findProductionOrderSummaryByCondition(ProductionOrderSummarySearchCondition condition, Pageable pageable) {
        //1. 공통 쿼리 빌더 생성 (SELECT, FROM, JOIN, WHERE)
        JPAQuery<ProductionOrderSummary> query = queryFactory
                .select(
                        Projections.constructor(ProductionOrderSummary.class,
                        productionOrderEntity.id.max(),
                        productionOrderEntity.orderId,
                        productionOrderEntity.lotName.max(),
                        productionOrderEntity.description.max(),
                        productionOrderEntity.itemName.max(),
                        productionOrderEntity.productionOrderType.max(),
                        productionOrderEntity.planQuantity.sum(),
                        productionOrderEntity.releasedQuantity.sum(),
                        productionOrderEntity.startedQuantity.sum(),
                        productionOrderEntity.endedQuantity.sum(),
                        productionOrderEntity.scrappedQuantity.sum(),
                        productionOrderEntity.createTime.max(),
                        productionOrderEntity.releaseTime.max(),
                        productionOrderEntity.completeTime.max(),
                        productionOrderEntity.createUser.max(),
                        productionOrderEntity.releaseUser.max(),
                        productionOrderEntity.completeUser.max(),
                        productionOrderEntity.dueDate.max()
                ))
                .from(productionOrderEntity)
                .groupBy(productionOrderEntity.orderId)
                .where(
                        // (WHERE 조건이 있다면 여기에 추가)
                        orderIdContains(condition.getOrderId())
                );

        // 2. 정렬 적용
        query.orderBy(getOrderSpecifiersGroupByOrderId(pageable.getSort()));

        // 3. 페이징 적용 (isPaged()로 분기)
        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        // 4. 데이터 조회
        List<ProductionOrderSummary> content = query.fetch();

        // 5. 카운트 조회 (isPaged()로 분기)
        long total;
        if (pageable.isPaged()) {
            // [페이징 O] 별도 카운트 쿼리 실행
            Long count = queryFactory
                    .select(productionOrderEntity.orderId.countDistinct())
                    .from(productionOrderEntity)
                    .where(orderIdContains(condition.getOrderId()))
                    .fetchOne();

            total = (count != null) ? count.longValue() : 0L;

        } else {
            // [페이징 X] .unpaged() 일 때
            total = content.size();
        }

        // 6. PageImpl 반환
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<ProductionOrder> findProductionOrderByCondition(ProductionOrderSearchCondition condition, Pageable pageable) {
        //1. 공통 쿼리 빌더 생성 (SELECT, FROM, JOIN, WHERE)
        JPAQuery<ProductionOrderEntity> query = queryFactory
                .selectFrom(productionOrderEntity)
                .where(
                        // (WHERE 조건이 있다면 여기에 추가)
                        orderIdContains(condition.getOrderId()),
                        equipmentNameEqual(condition.getEquipmentName())
                );

        // 2. 정렬 적용
        query.orderBy(getOrderSpecifiersByOrderId(pageable.getSort()));

        // 3. 페이징 적용 (isPaged()로 분기)
        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        // 4. 데이터 조회
        List<ProductionOrderEntity> content = query.fetch();

        List<ProductionOrder> converted = content.stream().map(productionOrderMapper::toDomain).collect(Collectors.toList());

        // 5. 카운트 조회 (isPaged()로 분기)
        long total;
        if (pageable.isPaged()) {
            // [페이징 O] 별도 카운트 쿼리 실행
            Long count = queryFactory
                    .select(productionOrderEntity.count())
                    .from(productionOrderEntity)
                    .where(
                            // (WHERE 조건이 있다면 여기에 추가)
                            orderIdContains(condition.getOrderId()),
                            equipmentNameEqual(condition.getEquipmentName())
                    )
                    .fetchOne();

            total = (count != null) ? count.longValue() : 0L;

        } else {
            // [페이징 X] .unpaged() 일 때
            total = content.size();
        }

        // 6. PageImpl 반환
        return new PageImpl<>(converted, pageable, total);
    }

    @Override
    public Page<ProductionOrderHistory> findProductionOrderHistoryByCondition(ProductionOrderHistorySearchCondition condition, Pageable pageable) {
        //1. 공통 쿼리 빌더 생성 (SELECT, FROM, JOIN, WHERE)
        JPAQuery<ProductionOrderHistoryEntity> query = queryFactory
                .selectFrom(productionOrderHistoryEntity)
                .where(
                        // (WHERE 조건이 있다면 여기에 추가)
                        historyOrderIdContains(condition.getOrderId())
                );

        // 2. 정렬 적용
        query.orderBy(getOrderHistorySpecifiersByOrderId(pageable.getSort()));

        // 3. 페이징 적용 (isPaged()로 분기)
        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        // 4. 데이터 조회
        List<ProductionOrderHistoryEntity> content = query.fetch();

        List<ProductionOrderHistory> converted = content.stream().map(productionOrderHistoryMapper::toDomain).collect(Collectors.toList());

        // 5. 카운트 조회 (isPaged()로 분기)
        long total;
        if (pageable.isPaged()) {
            // [페이징 O] 별도 카운트 쿼리 실행
            Long count = queryFactory
                    .select(productionOrderHistoryEntity.count())
                    .from(productionOrderHistoryEntity)
                    .where(
                            // (WHERE 조건이 있다면 여기에 추가)
                            historyOrderIdContains(condition.getOrderId())
                    )
                    .fetchOne();

            total = (count != null) ? count.longValue() : 0L;

        } else {
            // [페이징 X] .unpaged() 일 때
            total = content.size();
        }

        // 6. PageImpl 반환
        return new PageImpl<>(converted, pageable, total);
    }

    /**
     * Pageable의 Sort 객체를 Querydsl의 OrderSpecifier 배열로 변환합니다.
     */
    private OrderSpecifier<?>[] getOrderSpecifiersGroupByOrderId(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();

        if (sort != null && sort.isSorted()) {
            for (Sort.Order order : sort) {
                String property = order.getProperty();

                if (StringUtils.hasText(property) && QueryDslUtils.isValidProperty(property)) {
                    Order direction = order.isAscending() ? Order.ASC : Order.DESC;

                    // PathBuilder를 통해 표현식 생성
                    PathBuilder<ProductionOrderEntity> pathBuilder = new PathBuilder<>(
                            productionOrderEntity.getType(),
                            productionOrderEntity.getMetadata()
                    );

                    // 정렬 대상 필드가 orderId(GROUP BY 기준)가 아닌 경우 집계 함수 적용
                    if (property.equals("orderId")) {
                        orders.add(new OrderSpecifier(direction, pathBuilder.get(property)));
                    } else {
                        // 수량 관련 필드는 sum으로 정렬하고 싶다면 별도 처리가 가능합니다.
                        // 여기서는 질문하신 대로 대다수의 필드에 max()를 적용하는 방식을 사용합니다.
                        if (isQuantityField(property)) {
                            NumberPath<Long> numberPath = pathBuilder.getNumber(property, Long.class);
                            orders.add(new OrderSpecifier(direction, numberPath.sum()));
                        } else {
                            // 날짜(CREATE_TIME 등) 및 기타 필드는 max() 적용
                            ComparablePath<Comparable> comparablePath = pathBuilder.getComparable(property, Comparable.class);
                            orders.add(new OrderSpecifier(direction, comparablePath.max()));
                        }
                    }
                }
            }
        }

        // 기본 정렬값 설정 (여기서도 집계가 필요할 수 있음)
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, productionOrderEntity.orderId));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    /**
     * 수량 관련 필드인지 확인하는 헬퍼 메서드
     */
    private boolean isQuantityField(String property) {
        return property.contains("Quantity");
    }

    private OrderSpecifier<?>[] getOrderSpecifiersByOrderId(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();

        if (sort != null && sort.isSorted()) {
            for (Sort.Order order : sort) {
                String property = order.getProperty();

                // [핵심] property가 null이 아니고, 공백이 아닐 때만 정렬을 추가합니다.
                if (StringUtils.hasText(property) && QueryDslUtils.isValidProperty(property)) {
                    Order direction = order.isAscending() ? Order.ASC : Order.DESC;

                    PathBuilder pathBuilder = new PathBuilder<>(
                            productionOrderEntity.getType(),
                            productionOrderEntity.getMetadata()
                    );

                    orders.add(new OrderSpecifier(direction, pathBuilder.get(property)));
                }
            }
        }

        // 유효한 정렬 필드가 하나도 없었다면 (스웨거에서 잘못 보낸 경우 포함) 기본값 적용
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, productionOrderEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private OrderSpecifier<?>[] getOrderHistorySpecifiersByOrderId(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();

        if (sort != null && sort.isSorted()) {
            for (Sort.Order order : sort) {
                String property = order.getProperty();

                // [핵심] property가 null이 아니고, 공백이 아닐 때만 정렬을 추가합니다.
                if (StringUtils.hasText(property) && QueryDslUtils.isValidProperty(property)) {
                    Order direction = order.isAscending() ? Order.ASC : Order.DESC;

                    PathBuilder pathBuilder = new PathBuilder<>(
                            productionOrderHistoryEntity.getType(),
                            productionOrderHistoryEntity.getMetadata()
                    );

                    orders.add(new OrderSpecifier(direction, pathBuilder.get(property)));
                }
            }
        }

        // 유효한 정렬 필드가 하나도 없었다면 (스웨거에서 잘못 보낸 경우 포함) 기본값 적용
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, productionOrderHistoryEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }


    // == 동적 쿼리를 위한 BooleanExpression 메소드들 ==
    private BooleanExpression equipmentNameEqual(String equipmentName) {
        return StringUtils.hasText(equipmentName) ? productionOrderEntity.equipmentName.eq(equipmentName) : null;
    }

    private BooleanExpression orderIdContains(String orderId) {
        return StringUtils.hasText(orderId) ? productionOrderEntity.orderId.contains(orderId) : null;
    }

    // == 동적 쿼리를 위한 BooleanExpression 메소드들 ==
    private BooleanExpression historyOrderIdContains(String orderId) {
        return StringUtils.hasText(orderId) ? productionOrderHistoryEntity.orderId.contains(orderId) : null;
    }
}
