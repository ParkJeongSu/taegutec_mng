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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static kr.co.aim.infra.persistence.entity.QProductionOrderEntity.productionOrderEntity;
import static kr.co.aim.infra.persistence.entity.QProductionOrderHistoryEntity.productionOrderHistoryEntity;

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
    public Optional<ProductionOrder> findByGalKey(String galKey) {
        return productionOrderJpaRepository.findByGalKey(galKey).map(productionOrderMapper::toDomain);
    }

    @Override
    public Optional<ProductionOrder> findByH2OrderDpLineId(Long h2orderDPLineId) {
        return productionOrderJpaRepository.findByH2OrderDpLineId(h2orderDPLineId).map(productionOrderMapper::toDomain);
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
    public List<ProductionOrder> findByProductionOrderStateAndProductionOrderTypeInOrderByCreateTimeAsc(String productionOrderState, List<String> productionOrderType) {
        return productionOrderJpaRepository.findByProductionOrderStateAndProductionOrderTypeInOrderByCreateTimeAsc(productionOrderState,productionOrderType).stream().map(productionOrderMapper::toDomain).collect(Collectors.toList());
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
        // 1. 공통 쿼리 빌더 생성 (SELECT, FROM, JOIN, WHERE)
        JPAQuery<ProductionOrderEntity> query = queryFactory
                .selectFrom(productionOrderEntity)
                .where(
                        orderIdContains(condition.getOrderId()),
                        orderLineNumberContains(condition.getOrderLineNumber()),
                        lotNameContains(condition.getLotName()),
                        descriptionContains(condition.getDescription()),
                        itemNameContains(condition.getItemName()),
                        recipeNameContains(condition.getRecipeName()),
                        carrierNameContains(condition.getCarrierName()),
                        idocIdEq(condition.getIdocId()),
                        h2OrderDpLineIdEq(condition.getH2OrderDpLineId()),
                        galKeyContains(condition.getGalKey()),
                        mngKeyEq(condition.getMngKey()),
                        productionOrderTypeContains(condition.getProductionOrderType()),
                        productionOrderStateContains(condition.getProductionOrderState()),
                        reportStateContains(condition.getReportState()),
                        holdStateContains(condition.getHoldState()),
                        reasonCodeContains(condition.getReasonCode()),
                        equipmentNameEqual(condition.getEquipmentName()),
                        planQuantityEq(condition.getPlanQuantity()),
                        releasedQuantityEq(condition.getReleasedQuantity()),
                        startedQuantityEq(condition.getStartedQuantity()),
                        endedQuantityEq(condition.getEndedQuantity()),
                        scrappedQuantityEq(condition.getScrappedQuantity()),
                        createTimeEq(condition.getCreateTime()),
                        releaseTimeEq(condition.getReleaseTime()),
                        completeTimeEq(condition.getCompleteTime()),
                        validationTimeEq(condition.getValidationTime()),
                        createUserContains(condition.getCreateUser()),
                        releaseUserContains(condition.getReleaseUser()),
                        completeUserContains(condition.getCompleteUser()),
                        dueDateEq(condition.getDueDate())
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
                            orderIdContains(condition.getOrderId()),
                            orderLineNumberContains(condition.getOrderLineNumber()),
                            lotNameContains(condition.getLotName()),
                            descriptionContains(condition.getDescription()),
                            itemNameContains(condition.getItemName()),
                            recipeNameContains(condition.getRecipeName()),
                            carrierNameContains(condition.getCarrierName()),
                            idocIdEq(condition.getIdocId()),
                            h2OrderDpLineIdEq(condition.getH2OrderDpLineId()),
                            galKeyContains(condition.getGalKey()),
                            mngKeyEq(condition.getMngKey()),
                            productionOrderTypeContains(condition.getProductionOrderType()),
                            productionOrderStateContains(condition.getProductionOrderState()),
                            reportStateContains(condition.getReportState()),
                            holdStateContains(condition.getHoldState()),
                            reasonCodeContains(condition.getReasonCode()),
                            equipmentNameEqual(condition.getEquipmentName()),
                            planQuantityEq(condition.getPlanQuantity()),
                            releasedQuantityEq(condition.getReleasedQuantity()),
                            startedQuantityEq(condition.getStartedQuantity()),
                            endedQuantityEq(condition.getEndedQuantity()),
                            scrappedQuantityEq(condition.getScrappedQuantity()),
                            createTimeEq(condition.getCreateTime()),
                            releaseTimeEq(condition.getReleaseTime()),
                            completeTimeEq(condition.getCompleteTime()),
                            validationTimeEq(condition.getValidationTime()),
                            createUserContains(condition.getCreateUser()),
                            releaseUserContains(condition.getReleaseUser()),
                            completeUserContains(condition.getCompleteUser()),
                            dueDateEq(condition.getDueDate())
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
        // 1. 공통 쿼리 빌더 생성 (SELECT, FROM, JOIN, WHERE)
        JPAQuery<ProductionOrderHistoryEntity> query = queryFactory
                .selectFrom(productionOrderHistoryEntity)
                .where(
                        historyOrderIdContains(condition.getOrderId()),
                        historyOrderLineNumberContains(condition.getOrderLineNumber()),
                        historyLotNameContains(condition.getLotName()),
                        historyDescriptionContains(condition.getDescription()),
                        historyItemNameContains(condition.getItemName()),
                        historyRecipeNameContains(condition.getRecipeName()),
                        historyCarrierNameContains(condition.getCarrierName()),
                        historyGalIdContains(condition.getGalKey()),
                        historyProductionOrderTypeContains(condition.getProductionOrderType()),
                        historyProductionOrderStateContains(condition.getProductionOrderState()),
                        historyHoldStateContains(condition.getHoldState()),
                        historyReasonCodeContains(condition.getReasonCode()),
                        historyEquipmentNameContains(condition.getEquipmentName()),
                        historyPlanQuantityEq(condition.getPlanQuantity()),
                        historyReleasedQuantityEq(condition.getReleasedQuantity()),
                        historyStartedQuantityEq(condition.getStartedQuantity()),
                        historyEndedQuantityEq(condition.getEndedQuantity()),
                        historyScrappedQuantityEq(condition.getScrappedQuantity()),
                        historyCreateTimeEq(condition.getCreateTime()),
                        historyReleaseTimeEq(condition.getReleaseTime()),
                        historyCompleteTimeEq(condition.getCompleteTime()),
                        historyValidationTimeEq(condition.getValidationTime()),
                        historyCreateUserContains(condition.getCreateUser()),
                        historyReleaseUserContains(condition.getReleaseUser()),
                        historyCompleteUserContains(condition.getCompleteUser()),
                        historyDueDateEq(condition.getDueDate()),
                        historyEventTimeBetween(condition.getFromEventTime(), condition.getToEventTime())
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
                            historyOrderIdContains(condition.getOrderId()),
                            historyOrderLineNumberContains(condition.getOrderLineNumber()),
                            historyLotNameContains(condition.getLotName()),
                            historyDescriptionContains(condition.getDescription()),
                            historyItemNameContains(condition.getItemName()),
                            historyRecipeNameContains(condition.getRecipeName()),
                            historyCarrierNameContains(condition.getCarrierName()),
                            historyGalIdContains(condition.getGalKey()),
                            historyProductionOrderTypeContains(condition.getProductionOrderType()),
                            historyProductionOrderStateContains(condition.getProductionOrderState()),
                            historyHoldStateContains(condition.getHoldState()),
                            historyReasonCodeContains(condition.getReasonCode()),
                            historyEquipmentNameContains(condition.getEquipmentName()),
                            historyPlanQuantityEq(condition.getPlanQuantity()),
                            historyReleasedQuantityEq(condition.getReleasedQuantity()),
                            historyStartedQuantityEq(condition.getStartedQuantity()),
                            historyEndedQuantityEq(condition.getEndedQuantity()),
                            historyScrappedQuantityEq(condition.getScrappedQuantity()),
                            historyCreateTimeEq(condition.getCreateTime()),
                            historyReleaseTimeEq(condition.getReleaseTime()),
                            historyCompleteTimeEq(condition.getCompleteTime()),
                            historyValidationTimeEq(condition.getValidationTime()),
                            historyCreateUserContains(condition.getCreateUser()),
                            historyReleaseUserContains(condition.getReleaseUser()),
                            historyCompleteUserContains(condition.getCompleteUser()),
                            historyDueDateEq(condition.getDueDate()),
                            historyEventTimeBetween(condition.getFromEventTime(), condition.getToEventTime())
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

    // == ProductionOrder 동적 쿼리를 위한 BooleanExpression 메소드들 ==

    private BooleanExpression equipmentNameEqual(String equipmentName) {
        return StringUtils.hasText(equipmentName) ? productionOrderEntity.equipmentName.eq(equipmentName) : null;
    }

    private BooleanExpression orderIdContains(String orderId) {
        return StringUtils.hasText(orderId) ? productionOrderEntity.orderId.contains(orderId) : null;
    }

    private BooleanExpression orderLineNumberContains(String orderLineNumber) {
        return StringUtils.hasText(orderLineNumber) ? productionOrderEntity.orderLineNumber.contains(orderLineNumber) : null;
    }

    private BooleanExpression lotNameContains(String lotName) {
        return StringUtils.hasText(lotName) ? productionOrderEntity.lotName.contains(lotName) : null;
    }

    private BooleanExpression descriptionContains(String description) {
        return StringUtils.hasText(description) ? productionOrderEntity.description.contains(description) : null;
    }

    private BooleanExpression itemNameContains(String itemName) {
        return StringUtils.hasText(itemName) ? productionOrderEntity.itemName.contains(itemName) : null;
    }

    private BooleanExpression recipeNameContains(String recipeName) {
        return StringUtils.hasText(recipeName) ? productionOrderEntity.recipeName.contains(recipeName) : null;
    }

    private BooleanExpression carrierNameContains(String carrierName) {
        return StringUtils.hasText(carrierName) ? productionOrderEntity.carrierName.contains(carrierName) : null;
    }

    private BooleanExpression idocIdEq(Long idocId) {
        return idocId != null ? productionOrderEntity.idocId.eq(idocId) : null;
    }

    private BooleanExpression h2OrderDpLineIdEq(Long h2OrderDpLineId) {
        return h2OrderDpLineId != null ? productionOrderEntity.h2OrderDpLineId.eq(h2OrderDpLineId) : null;
    }

    private BooleanExpression galKeyContains(String galKey) {
        return StringUtils.hasText(galKey) ? productionOrderEntity.galKey.contains(galKey) : null;
    }

    private BooleanExpression mngKeyEq(Long mngKey) {
        return mngKey != null ? productionOrderEntity.mngKey.eq(mngKey) : null;
    }

    private BooleanExpression productionOrderTypeContains(String productionOrderType) {
        return StringUtils.hasText(productionOrderType) ? productionOrderEntity.productionOrderType.contains(productionOrderType) : null;
    }

    private BooleanExpression productionOrderStateContains(String productionOrderState) {
        return StringUtils.hasText(productionOrderState) ? productionOrderEntity.productionOrderState.contains(productionOrderState) : null;
    }

    private BooleanExpression reportStateContains(String reportState) {
        return StringUtils.hasText(reportState) ? productionOrderEntity.reportState.contains(reportState) : null;
    }

    private BooleanExpression holdStateContains(String holdState) {
        return StringUtils.hasText(holdState) ? productionOrderEntity.holdState.contains(holdState) : null;
    }

    private BooleanExpression reasonCodeContains(String reasonCode) {
        return StringUtils.hasText(reasonCode) ? productionOrderEntity.reasonCode.contains(reasonCode) : null;
    }

    private BooleanExpression planQuantityEq(BigDecimal planQuantity) {
        return planQuantity != null ? productionOrderEntity.planQuantity.eq(planQuantity) : null;
    }

    private BooleanExpression releasedQuantityEq(BigDecimal releasedQuantity) {
        return releasedQuantity != null ? productionOrderEntity.releasedQuantity.eq(releasedQuantity) : null;
    }

    private BooleanExpression startedQuantityEq(BigDecimal startedQuantity) {
        return startedQuantity != null ? productionOrderEntity.startedQuantity.eq(startedQuantity) : null;
    }

    private BooleanExpression endedQuantityEq(BigDecimal endedQuantity) {
        return endedQuantity != null ? productionOrderEntity.endedQuantity.eq(endedQuantity) : null;
    }

    private BooleanExpression scrappedQuantityEq(BigDecimal scrappedQuantity) {
        return scrappedQuantity != null ? productionOrderEntity.scrappedQuantity.eq(scrappedQuantity) : null;
    }

    private BooleanExpression createTimeEq(LocalDateTime createTime) {
        return createTime != null ? productionOrderEntity.createTime.eq(createTime) : null;
    }

    private BooleanExpression releaseTimeEq(LocalDateTime releaseTime) {
        return releaseTime != null ? productionOrderEntity.releaseTime.eq(releaseTime) : null;
    }

    private BooleanExpression completeTimeEq(LocalDateTime completeTime) {
        return completeTime != null ? productionOrderEntity.completeTime.eq(completeTime) : null;
    }

    private BooleanExpression validationTimeEq(LocalDateTime validationTime) {
        return validationTime != null ? productionOrderEntity.validationTime.eq(validationTime) : null;
    }

    private BooleanExpression createUserContains(String createUser) {
        return StringUtils.hasText(createUser) ? productionOrderEntity.createUser.contains(createUser) : null;
    }

    private BooleanExpression releaseUserContains(String releaseUser) {
        return StringUtils.hasText(releaseUser) ? productionOrderEntity.releaseUser.contains(releaseUser) : null;
    }

    private BooleanExpression completeUserContains(String completeUser) {
        return StringUtils.hasText(completeUser) ? productionOrderEntity.completeUser.contains(completeUser) : null;
    }

    private BooleanExpression dueDateEq(LocalDateTime dueDate) {
        return dueDate != null ? productionOrderEntity.dueDate.eq(dueDate) : null;
    }

    // == ProductionOrderHistory 동적 쿼리를 위한 BooleanExpression 메소드들 ==

    private BooleanExpression historyOrderIdContains(String orderId) {
        return StringUtils.hasText(orderId) ? productionOrderHistoryEntity.orderId.contains(orderId) : null;
    }

    private BooleanExpression historyOrderLineNumberContains(String orderLineNumber) {
        return StringUtils.hasText(orderLineNumber) ? productionOrderHistoryEntity.orderLineNumber.contains(orderLineNumber) : null;
    }

    private BooleanExpression historyLotNameContains(String lotName) {
        return StringUtils.hasText(lotName) ? productionOrderHistoryEntity.lotName.contains(lotName) : null;
    }

    private BooleanExpression historyDescriptionContains(String description) {
        return StringUtils.hasText(description) ? productionOrderHistoryEntity.description.contains(description) : null;
    }

    private BooleanExpression historyItemNameContains(String itemName) {
        return StringUtils.hasText(itemName) ? productionOrderHistoryEntity.itemName.contains(itemName) : null;
    }

    private BooleanExpression historyRecipeNameContains(String recipeName) {
        return StringUtils.hasText(recipeName) ? productionOrderHistoryEntity.recipeName.contains(recipeName) : null;
    }

    private BooleanExpression historyCarrierNameContains(String carrierName) {
        return StringUtils.hasText(carrierName) ? productionOrderHistoryEntity.carrierName.contains(carrierName) : null;
    }

    private BooleanExpression historyGalIdContains(String galKey) {
        return StringUtils.hasText(galKey) ? productionOrderHistoryEntity.galKey.contains(galKey) : null;
    }

    private BooleanExpression historyProductionOrderTypeContains(String productionOrderType) {
        return StringUtils.hasText(productionOrderType) ? productionOrderHistoryEntity.productionOrderType.contains(productionOrderType) : null;
    }

    private BooleanExpression historyProductionOrderStateContains(String productionOrderState) {
        return StringUtils.hasText(productionOrderState) ? productionOrderHistoryEntity.productionOrderState.contains(productionOrderState) : null;
    }

    private BooleanExpression historyHoldStateContains(String holdState) {
        return StringUtils.hasText(holdState) ? productionOrderHistoryEntity.holdState.contains(holdState) : null;
    }

    private BooleanExpression historyReasonCodeContains(String reasonCode) {
        return StringUtils.hasText(reasonCode) ? productionOrderHistoryEntity.reasonCode.contains(reasonCode) : null;
    }

    private BooleanExpression historyEquipmentNameContains(String equipmentName) {
        return StringUtils.hasText(equipmentName) ? productionOrderHistoryEntity.equipmentName.contains(equipmentName) : null;
    }

    private BooleanExpression historyPlanQuantityEq(BigDecimal  planQuantity) {
        return planQuantity != null ? productionOrderHistoryEntity.planQuantity.eq(planQuantity) : null;
    }

    private BooleanExpression historyReleasedQuantityEq(BigDecimal  releasedQuantity) {
        return releasedQuantity != null ? productionOrderHistoryEntity.releasedQuantity.eq(releasedQuantity) : null;
    }

    private BooleanExpression historyStartedQuantityEq(BigDecimal  startedQuantity) {
        return startedQuantity != null ? productionOrderHistoryEntity.startedQuantity.eq(startedQuantity) : null;
    }

    private BooleanExpression historyEndedQuantityEq(BigDecimal  endedQuantity) {
        return endedQuantity != null ? productionOrderHistoryEntity.endedQuantity.eq(endedQuantity) : null;
    }

    private BooleanExpression historyScrappedQuantityEq(BigDecimal  scrappedQuantity) {
        return scrappedQuantity != null ? productionOrderHistoryEntity.scrappedQuantity.eq(scrappedQuantity) : null;
    }

    private BooleanExpression historyCreateTimeEq(LocalDateTime createTime) {
        return createTime != null ? productionOrderHistoryEntity.createTime.eq(createTime) : null;
    }

    private BooleanExpression historyReleaseTimeEq(LocalDateTime releaseTime) {
        return releaseTime != null ? productionOrderHistoryEntity.releaseTime.eq(releaseTime) : null;
    }

    private BooleanExpression historyCompleteTimeEq(LocalDateTime completeTime) {
        return completeTime != null ? productionOrderHistoryEntity.completeTime.eq(completeTime) : null;
    }

    private BooleanExpression historyValidationTimeEq(LocalDateTime validationTime) {
        return validationTime != null ? productionOrderHistoryEntity.validationTime.eq(validationTime) : null;
    }

    private BooleanExpression historyCreateUserContains(String createUser) {
        return StringUtils.hasText(createUser) ? productionOrderHistoryEntity.createUser.contains(createUser) : null;
    }

    private BooleanExpression historyReleaseUserContains(String releaseUser) {
        return StringUtils.hasText(releaseUser) ? productionOrderHistoryEntity.releaseUser.contains(releaseUser) : null;
    }

    private BooleanExpression historyCompleteUserContains(String completeUser) {
        return StringUtils.hasText(completeUser) ? productionOrderHistoryEntity.completeUser.contains(completeUser) : null;
    }

    private BooleanExpression historyDueDateEq(LocalDateTime dueDate) {
        return dueDate != null ? productionOrderHistoryEntity.dueDate.eq(dueDate) : null;
    }

    private BooleanExpression historyEventTimeBetween(LocalDateTime fromEventTime, LocalDateTime toEventTime) {
        if (fromEventTime != null && toEventTime != null) {
            return productionOrderHistoryEntity.eventTime.between(fromEventTime, toEventTime);
        }
        if (fromEventTime != null) {
            return productionOrderHistoryEntity.eventTime.goe(fromEventTime);
        }
        if (toEventTime != null) {
            return productionOrderHistoryEntity.eventTime.loe(toEventTime);
        }
        return null;
    }
}
