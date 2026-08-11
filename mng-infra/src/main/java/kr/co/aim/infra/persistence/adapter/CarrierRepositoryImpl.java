package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.condition.CarrierLotSearchCondition;
import kr.co.aim.common.condition.CarrierSearchCondition;
import kr.co.aim.common.dto.CarrierLotSearchResultDto;
import kr.co.aim.domain.model.Carrier;
import kr.co.aim.domain.repository.CarrierRepository;
import kr.co.aim.infra.persistence.entity.CarrierEntity;
import kr.co.aim.infra.persistence.mapper.CarrierMapper;
import kr.co.aim.infra.persistence.springdatajpa.CarrierHistoryJpaRepository;
import kr.co.aim.infra.persistence.springdatajpa.CarrierJpaRepository;
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

import static kr.co.aim.infra.persistence.entity.QCarrierEntity.carrierEntity;
import static kr.co.aim.infra.persistence.entity.QLotEntity.lotEntity;
import static kr.co.aim.infra.persistence.entity.QLotCarrierMappingEntity.lotCarrierMappingEntity;


@Repository
@RequiredArgsConstructor
public class CarrierRepositoryImpl implements CarrierRepository {
    // Spring Data JPA가 자동으로 구현해주는 JPA 리포지토리. UserEntity를 다룬다.
    private final CarrierJpaRepository carrierJpaRepository;
    private final CarrierHistoryJpaRepository carrierHistoryJpaRepository;
    private final CarrierMapper carrierMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입

    @Override
    public Carrier save(Carrier carrier) {
        // 1. Domain -> Entity 변환
        CarrierEntity entity = carrierMapper.toEntity(carrier);
        // 2. JPA 리포지토리를 통해 DB에 저장
        CarrierEntity savedEntity = carrierJpaRepository.save(entity);
        // 3. 저장된 Entity -> Domain 변환 후 반환
        return carrierMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Carrier> findById(Long id) {
        // 1. JPA 리포지토리를 통해 ID로 Entity 조회
        Optional<CarrierEntity> entityOptional = carrierJpaRepository.findById(id);
        // 2. 조회된 Optional<Entity>를 Optional<Domain>으로 변환하여 반환
        return entityOptional.map(carrierMapper::toDomain);
    }

    @Override
    public Optional<Carrier> findByCarrierName(String carrierName) {
        return carrierJpaRepository.findByCarrierName(carrierName).map(carrierMapper::toDomain);
    }

    @Override
    public List<Carrier> findAll() {
        // 1. JPA 리포지토리를 통해 모든 UserEntity 조회
        List<CarrierEntity> entities = carrierJpaRepository.findAll();
        // 2. Entity 리스트를 Domain 객체 리스트로 변환하여 반환
        return entities.stream()
                .map(carrierMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        carrierJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public List<Carrier> findCarriersForEmptyContainer(String cleanState, String transportState, String transportJobId, String useState, Integer quantity, List<String> containerTypes) {
        return carrierJpaRepository.findCarriersForEmptyContainer(
                cleanState,
                transportState,
                transportJobId,
                useState,
                quantity,
                containerTypes
        ).stream().map(carrierMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Carrier> findByQuantityAndCarrierType(BigDecimal quantity, String carrierType) {
        return carrierJpaRepository.findByQuantityAndCarrierType(quantity,carrierType)
                .stream().map(carrierMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Page<Carrier> findCarrierByCondition(CarrierSearchCondition condition, Pageable pageable) {
        // 1. 공통 쿼리 빌더 생성 (SELECT, FROM, JOIN, WHERE)
        JPAQuery<CarrierEntity> query = queryFactory
                .selectFrom(carrierEntity)
                .where(
                        carrierNameContains(condition.getCarrierName()),
                        carrierDefNameContains(condition.getCarrierDefName()),
                        carrierStateContains(condition.getCarrierState()),
                        equipmentNameContains(condition.getEquipmentName()),
                        portNameContains(condition.getPortName()),
                        zoneNameContains(condition.getZoneName()),
                        positionTypeNameContains(condition.getPositionTypeName()),
                        positionNameContains(condition.getPositionName()),
                        capacityEq(condition.getCapacity()),
                        cleanStateContains(condition.getCleanState()),
                        transportStateContains(condition.getTransportState()),
                        transportJobIdContains(condition.getTransportJobId()),
                        holdStateContains(condition.getHoldState()),
                        reasonCodeContains(condition.getReasonCode()),
                        useStateContains(condition.getUseState()),
                        useCountEq(condition.getUseCount()),
                        useCountPerCleanEq(condition.getUseCountPerClean()),
                        cleanCountEq(condition.getCleanCount()),
                        quantityEq(condition.getQuantity()),
                        galQuantityEq(condition.getGalQuantity()),
                        lastCleanTimeEq(condition.getLastCleanTime()),
                        createTimeEq(condition.getCreateTime()),
                        inboundTimeEq(condition.getInboundTime()),
                        outboundTimeEq(condition.getOutboundTime()),
                        containerTypeContains(condition.getContainerType())
                );

        // 2. 정렬 적용
        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        // 3. 페이징 적용 (isPaged()로 분기)
        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        // 4. 데이터 조회
        List<CarrierEntity> content = query.fetch();

        List<Carrier> converted = content.stream().map(carrierMapper::toDomain).collect(Collectors.toList());

        // 5. 카운트 조회 (isPaged()로 분기)
        long total;
        if (pageable.isPaged()) {
            // [페이징 O] 별도 카운트 쿼리 실행
            Long count = queryFactory
                    .select(carrierEntity.count())
                    .from(carrierEntity)
                    .where(
                            carrierNameContains(condition.getCarrierName()),
                            carrierDefNameContains(condition.getCarrierDefName()),
                            carrierStateContains(condition.getCarrierState()),
                            equipmentNameContains(condition.getEquipmentName()),
                            portNameContains(condition.getPortName()),
                            zoneNameContains(condition.getZoneName()),
                            positionTypeNameContains(condition.getPositionTypeName()),
                            positionNameContains(condition.getPositionName()),
                            capacityEq(condition.getCapacity()),
                            cleanStateContains(condition.getCleanState()),
                            transportStateContains(condition.getTransportState()),
                            transportJobIdContains(condition.getTransportJobId()),
                            holdStateContains(condition.getHoldState()),
                            reasonCodeContains(condition.getReasonCode()),
                            useStateContains(condition.getUseState()),
                            useCountEq(condition.getUseCount()),
                            useCountPerCleanEq(condition.getUseCountPerClean()),
                            cleanCountEq(condition.getCleanCount()),
                            quantityEq(condition.getQuantity()),
                            galQuantityEq(condition.getGalQuantity()),
                            lastCleanTimeEq(condition.getLastCleanTime()),
                            createTimeEq(condition.getCreateTime()),
                            inboundTimeEq(condition.getInboundTime()),
                            outboundTimeEq(condition.getOutboundTime()),
                            containerTypeContains(condition.getContainerType())
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
    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();

        if (sort.isSorted()) {
            for (Sort.Order order : sort) {
                // 정렬 방향을 결정합니다 (ASC or DESC)
                Order direction = order.isAscending() ? Order.ASC : Order.DESC;

                // 정렬할 속성(컬럼)을 PathBuilder를 통해 지정합니다.
                // "userName"과 같은 문자열을 Q-Type 경로로 변환해줍니다.
                PathBuilder pathBuilder = new PathBuilder<>(carrierEntity.getType(), carrierEntity.getMetadata());

                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        // 기본 정렬 조건 (만약 정렬 조건이 없다면 id 내림차순)
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, carrierEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    @Override
    public Page<CarrierLotSearchResultDto> findCarrierLotByCondition(CarrierLotSearchCondition condition, Pageable pageable) {
        JPAQuery<CarrierLotSearchResultDto> query = queryFactory
                .select(Projections.fields(CarrierLotSearchResultDto.class,
                        // CARRIER
                        carrierEntity.id.as("carrierId"),
                        carrierEntity.carrierName,
                        carrierEntity.carrierDefName,
                        carrierEntity.carrierState,
                        carrierEntity.equipmentName,
                        carrierEntity.portName,
                        carrierEntity.zoneName,
                        carrierEntity.positionTypeName,
                        carrierEntity.positionName,
                        carrierEntity.capacity,
                        carrierEntity.cleanState,
                        carrierEntity.transportState,
                        carrierEntity.transportJobId,
                        carrierEntity.holdState.as("carrierHoldState"),
                        carrierEntity.reasonCode.as("carrierReasonCode"),
                        carrierEntity.useState,
                        carrierEntity.useCount,
                        carrierEntity.useCountPerClean,
                        carrierEntity.cleanCount,
                        carrierEntity.quantity.as("carrierQuantity"),
                        carrierEntity.galQuantity,
                        carrierEntity.lastCleanTime,
                        carrierEntity.createTime.as("carrierCreateTime"),
                        carrierEntity.inboundTime,
                        carrierEntity.outboundTime,
                        carrierEntity.containerType,

                        // LOT_CARRIER_MAPPING
                        lotCarrierMappingEntity.id.as("mappingId"),
                        lotCarrierMappingEntity.orderId,
                        lotCarrierMappingEntity.orderLineNumber,
                        lotCarrierMappingEntity.productionOrderId,
                        lotCarrierMappingEntity.productionStatus,
                        lotCarrierMappingEntity.processStatus,
                        lotCarrierMappingEntity.quantity.as("mappingQuantity"),
                        lotCarrierMappingEntity.galQuantity.as("mappingGalQuantity"),
                        lotCarrierMappingEntity.mngKey,
                        lotCarrierMappingEntity.jobStartTime,
                        lotCarrierMappingEntity.jobEndTime,
                        lotCarrierMappingEntity.mantiRequestState,
                        lotCarrierMappingEntity.mantiRequestTime,
                        lotCarrierMappingEntity.mantiReplyTime,
                        lotCarrierMappingEntity.rrnRequestState,
                        lotCarrierMappingEntity.rrnRequestTime,
                        lotCarrierMappingEntity.rrnReplyTime,
                        lotCarrierMappingEntity.holdState.as("mappingHoldState"),
                        lotCarrierMappingEntity.reasonCode.as("mappingReasonCode"),

                        // LOT
                        lotEntity.id.as("lotId"),
                        lotEntity.lotName,
                        lotEntity.originalLotName,
                        lotEntity.lotStatus,
                        lotEntity.itemId,
                        lotEntity.totalQuantity,
                        lotEntity.holdState.as("lotHoldState"),
                        lotEntity.reasonCode.as("lotReasonCode")
                ))
                .from(carrierEntity)
                .leftJoin(lotCarrierMappingEntity).on(carrierEntity.carrierName.eq(lotCarrierMappingEntity.carrierName))
                .innerJoin(lotEntity).on(lotCarrierMappingEntity.lotName.eq(lotEntity.lotName))
                .where(
                        // CARRIER Conditions
                        carrierNameContains(condition.getCarrierName()),
                        carrierDefNameContains(condition.getCarrierDefName()),
                        carrierStateContains(condition.getCarrierState()),
                        equipmentNameContains(condition.getEquipmentName()),
                        portNameContains(condition.getPortName()),
                        zoneNameContains(condition.getZoneName()),
                        positionTypeNameContains(condition.getPositionTypeName()),
                        positionNameContains(condition.getPositionName()),
                        capacityEq(condition.getCapacity()),
                        cleanStateContains(condition.getCleanState()),
                        transportStateContains(condition.getTransportState()),
                        transportJobIdContains(condition.getTransportJobId()),
                        carrierHoldStateContains(condition.getCarrierHoldState()),
                        carrierReasonCodeContains(condition.getCarrierReasonCode()),
                        useStateContains(condition.getUseState()),
                        useCountEq(condition.getUseCount()),
                        useCountPerCleanEq(condition.getUseCountPerClean()),
                        cleanCountEq(condition.getCleanCount()),
                        quantityEq(condition.getCarrierQuantity()),
                        galQuantityEq(condition.getGalQuantity()),
                        lastCleanTimeEq(condition.getLastCleanTime()),
                        createTimeEq(condition.getCarrierCreateTime()),
                        inboundTimeEq(condition.getInboundTime()),
                        outboundTimeEq(condition.getOutboundTime()),
                        containerTypeContains(condition.getContainerType()),

                        // MAPPING Conditions
                        orderIdContains(condition.getOrderId()),
                        orderLineNumberContains(condition.getOrderLineNumber()),
                        productionOrderIdEq(condition.getProductionOrderId()),
                        productionStatusContains(condition.getProductionStatus()),
                        processStatusContains(condition.getProcessStatus()),
                        mngKeyEq(condition.getMngKey()),
                        mantiRequestStateContains(condition.getMantiRequestState()),
                        rrnRequestStateContains(condition.getRrnRequestState()),
                        mappingHoldStateContains(condition.getMappingHoldState()),
                        mappingReasonCodeContains(condition.getMappingReasonCode()),

                        // LOT Conditions
                        lotNameContains(condition.getLotName()),
                        originalLotNameContains(condition.getOriginalLotName()),
                        lotStatusContains(condition.getLotStatus()),
                        itemIdContains(condition.getItemId()),
                        totalQuantityEq(condition.getTotalQuantity()),
                        lotHoldStateContains(condition.getLotHoldState()),
                        lotReasonCodeContains(condition.getLotReasonCode())
                );

        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<CarrierLotSearchResultDto> content = query.fetch();

        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(carrierEntity.count())
                    .from(carrierEntity)
                    .leftJoin(lotCarrierMappingEntity).on(carrierEntity.carrierName.eq(lotCarrierMappingEntity.carrierName))
                    .innerJoin(lotEntity).on(lotCarrierMappingEntity.lotName.eq(lotEntity.lotName))
                    .where(
                            // CARRIER Conditions
                            carrierNameContains(condition.getCarrierName()),
                            carrierDefNameContains(condition.getCarrierDefName()),
                            carrierStateContains(condition.getCarrierState()),
                            equipmentNameContains(condition.getEquipmentName()),
                            portNameContains(condition.getPortName()),
                            zoneNameContains(condition.getZoneName()),
                            positionTypeNameContains(condition.getPositionTypeName()),
                            positionNameContains(condition.getPositionName()),
                            capacityEq(condition.getCapacity()),
                            cleanStateContains(condition.getCleanState()),
                            transportStateContains(condition.getTransportState()),
                            transportJobIdContains(condition.getTransportJobId()),
                            carrierHoldStateContains(condition.getCarrierHoldState()),
                            carrierReasonCodeContains(condition.getCarrierReasonCode()),
                            useStateContains(condition.getUseState()),
                            useCountEq(condition.getUseCount()),
                            useCountPerCleanEq(condition.getUseCountPerClean()),
                            cleanCountEq(condition.getCleanCount()),
                            quantityEq(condition.getCarrierQuantity()),
                            galQuantityEq(condition.getGalQuantity()),
                            lastCleanTimeEq(condition.getLastCleanTime()),
                            createTimeEq(condition.getCarrierCreateTime()),
                            inboundTimeEq(condition.getInboundTime()),
                            outboundTimeEq(condition.getOutboundTime()),
                            containerTypeContains(condition.getContainerType()),

                            // MAPPING Conditions
                            orderIdContains(condition.getOrderId()),
                            orderLineNumberContains(condition.getOrderLineNumber()),
                            productionOrderIdEq(condition.getProductionOrderId()),
                            productionStatusContains(condition.getProductionStatus()),
                            processStatusContains(condition.getProcessStatus()),
                            mngKeyEq(condition.getMngKey()),
                            mantiRequestStateContains(condition.getMantiRequestState()),
                            rrnRequestStateContains(condition.getRrnRequestState()),
                            mappingHoldStateContains(condition.getMappingHoldState()),
                            mappingReasonCodeContains(condition.getMappingReasonCode()),

                            // LOT Conditions
                            lotNameContains(condition.getLotName()),
                            originalLotNameContains(condition.getOriginalLotName()),
                            lotStatusContains(condition.getLotStatus()),
                            itemIdContains(condition.getItemId()),
                            totalQuantityEq(condition.getTotalQuantity()),
                            lotHoldStateContains(condition.getLotHoldState()),
                            lotReasonCodeContains(condition.getLotReasonCode())
                    )
                    .fetchOne();

            total = (count != null) ? count : 0L;
        } else {
            total = content.size();
        }

        return new PageImpl<>(content, pageable, total);
    }



    // == 동적 쿼리를 위한 BooleanExpression 메소드들 ==

    private BooleanExpression carrierNameContains(String carrierName) {
        return StringUtils.hasText(carrierName) ? carrierEntity.carrierName.contains(carrierName) : null;
    }

    private BooleanExpression carrierDefNameContains(String carrierDefName) {
        return StringUtils.hasText(carrierDefName) ? carrierEntity.carrierDefName.contains(carrierDefName) : null;
    }

    private BooleanExpression carrierStateContains(String carrierState) {
        return StringUtils.hasText(carrierState) ? carrierEntity.carrierState.contains(carrierState) : null;
    }

    private BooleanExpression equipmentNameContains(String equipmentName) {
        return StringUtils.hasText(equipmentName) ? carrierEntity.equipmentName.contains(equipmentName) : null;
    }

    private BooleanExpression portNameContains(String portName) {
        return StringUtils.hasText(portName) ? carrierEntity.portName.contains(portName) : null;
    }

    private BooleanExpression zoneNameContains(String zoneName) {
        return StringUtils.hasText(zoneName) ? carrierEntity.zoneName.contains(zoneName) : null;
    }

    private BooleanExpression positionTypeNameContains(String positionTypeName) {
        return StringUtils.hasText(positionTypeName) ? carrierEntity.positionTypeName.contains(positionTypeName) : null;
    }

    private BooleanExpression positionNameContains(String positionName) {
        return StringUtils.hasText(positionName) ? carrierEntity.positionName.contains(positionName) : null;
    }

    private BooleanExpression capacityEq(Integer capacity) {
        return capacity != null ? carrierEntity.capacity.eq(capacity) : null;
    }

    private BooleanExpression cleanStateContains(String cleanState) {
        return StringUtils.hasText(cleanState) ? carrierEntity.cleanState.contains(cleanState) : null;
    }

    private BooleanExpression transportStateContains(String transportState) {
        return StringUtils.hasText(transportState) ? carrierEntity.transportState.contains(transportState) : null;
    }

    private BooleanExpression transportJobIdContains(String transportJobId) {
        return StringUtils.hasText(transportJobId) ? carrierEntity.transportJobId.contains(transportJobId) : null;
    }

    private BooleanExpression holdStateContains(String holdState) {
        return StringUtils.hasText(holdState) ? carrierEntity.holdState.contains(holdState) : null;
    }

    private BooleanExpression reasonCodeContains(String reasonCode) {
        return StringUtils.hasText(reasonCode) ? carrierEntity.reasonCode.contains(reasonCode) : null;
    }

    private BooleanExpression useStateContains(String useState) {
        return StringUtils.hasText(useState) ? carrierEntity.useState.contains(useState) : null;
    }

    private BooleanExpression useCountEq(Integer useCount) {
        return useCount != null ? carrierEntity.useCount.eq(useCount) : null;
    }

    private BooleanExpression useCountPerCleanEq(Integer useCountPerClean) {
        return useCountPerClean != null ? carrierEntity.useCountPerClean.eq(useCountPerClean) : null;
    }

    private BooleanExpression cleanCountEq(Integer cleanCount) {
        return cleanCount != null ? carrierEntity.cleanCount.eq(cleanCount) : null;
    }

    private BooleanExpression quantityEq(BigDecimal quantity) {
        return quantity != null ? carrierEntity.quantity.eq(quantity) : null;
    }

    private BooleanExpression galQuantityEq(BigDecimal galQuantity) {
        return galQuantity != null ? carrierEntity.galQuantity.eq(galQuantity) : null;
    }

    private BooleanExpression lastCleanTimeEq(LocalDateTime lastCleanTime) {
        return lastCleanTime != null ? carrierEntity.lastCleanTime.eq(lastCleanTime) : null;
    }

    private BooleanExpression createTimeEq(LocalDateTime createTime) {
        return createTime != null ? carrierEntity.createTime.eq(createTime) : null;
    }

    private BooleanExpression inboundTimeEq(LocalDateTime inboundTime) {
        return inboundTime != null ? carrierEntity.inboundTime.eq(inboundTime) : null;
    }

    private BooleanExpression outboundTimeEq(LocalDateTime outboundTime) {
        return outboundTime != null ? carrierEntity.outboundTime.eq(outboundTime) : null;
    }

    private BooleanExpression containerTypeContains(String containerType) {
        return StringUtils.hasText(containerType) ? carrierEntity.containerType.contains(containerType) : null;
    }

    // == CARRIER BooleanExpressions ==

    private BooleanExpression carrierHoldStateContains(String carrierHoldState) {
        return StringUtils.hasText(carrierHoldState) ? carrierEntity.holdState.contains(carrierHoldState) : null;
    }

    private BooleanExpression carrierReasonCodeContains(String carrierReasonCode) {
        return StringUtils.hasText(carrierReasonCode) ? carrierEntity.reasonCode.contains(carrierReasonCode) : null;
    }

    // == LOT_CARRIER_MAPPING BooleanExpressions ==
    private BooleanExpression orderIdContains(String orderId) {
        return StringUtils.hasText(orderId) ? lotCarrierMappingEntity.orderId.contains(orderId) : null;
    }

    private BooleanExpression orderLineNumberContains(String orderLineNumber) {
        return StringUtils.hasText(orderLineNumber) ? lotCarrierMappingEntity.orderLineNumber.contains(orderLineNumber) : null;
    }

    private BooleanExpression productionOrderIdEq(Long productionOrderId) {
        return productionOrderId != null ? lotCarrierMappingEntity.productionOrderId.eq(productionOrderId) : null;
    }

    private BooleanExpression productionStatusContains(String productionStatus) {
        return StringUtils.hasText(productionStatus) ? lotCarrierMappingEntity.productionStatus.contains(productionStatus) : null;
    }

    private BooleanExpression processStatusContains(String processStatus) {
        return StringUtils.hasText(processStatus) ? lotCarrierMappingEntity.processStatus.contains(processStatus) : null;
    }

    private BooleanExpression mngKeyEq(Long mngKey) {
        return mngKey != null ? lotCarrierMappingEntity.mngKey.eq(mngKey) : null;
    }

    private BooleanExpression mantiRequestStateContains(String mantiRequestState) {
        return StringUtils.hasText(mantiRequestState) ? lotCarrierMappingEntity.mantiRequestState.contains(mantiRequestState) : null;
    }

    private BooleanExpression rrnRequestStateContains(String rrnRequestState) {
        return StringUtils.hasText(rrnRequestState) ? lotCarrierMappingEntity.rrnRequestState.contains(rrnRequestState) : null;
    }

    private BooleanExpression mappingHoldStateContains(String mappingHoldState) {
        return StringUtils.hasText(mappingHoldState) ? lotCarrierMappingEntity.holdState.contains(mappingHoldState) : null;
    }

    private BooleanExpression mappingReasonCodeContains(String mappingReasonCode) {
        return StringUtils.hasText(mappingReasonCode) ? lotCarrierMappingEntity.reasonCode.contains(mappingReasonCode) : null;
    }

    // == LOT BooleanExpressions ==
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

    private BooleanExpression lotHoldStateContains(String lotHoldState) {
        return StringUtils.hasText(lotHoldState) ? lotEntity.holdState.contains(lotHoldState) : null;
    }

    private BooleanExpression lotReasonCodeContains(String lotReasonCode) {
        return StringUtils.hasText(lotReasonCode) ? lotEntity.reasonCode.contains(lotReasonCode) : null;
    }



}
