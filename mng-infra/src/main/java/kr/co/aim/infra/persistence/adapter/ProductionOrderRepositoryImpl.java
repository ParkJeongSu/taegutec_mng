package kr.co.aim.infra.persistence.adapter;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.CarrierDef;
import kr.co.aim.domain.model.ProductionOrder;
import kr.co.aim.domain.repository.CarrierDefRepository;
import kr.co.aim.domain.repository.ProductionOrderRepository;
import kr.co.aim.infra.persistence.entity.CarrierDefEntity;
import kr.co.aim.infra.persistence.entity.ProductionOrderEntity;
import kr.co.aim.infra.persistence.mapper.CarrierDefMapper;
import kr.co.aim.infra.persistence.mapper.ProductionOrderMapper;
import kr.co.aim.infra.persistence.springdatajpa.CarrierDefJpaRepository;
import kr.co.aim.infra.persistence.springdatajpa.ProductionOrderJpaRepository;
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
public class ProductionOrderRepositoryImpl implements ProductionOrderRepository {
    // Spring Data JPA가 자동으로 구현해주는 JPA 리포지토리. UserEntity를 다룬다.

    private final ProductionOrderJpaRepository productionOrderJpaRepository;
    private final ProductionOrderMapper productionOrderMapper;
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


//    @Override
//    public Page<CarrierDefResponseDto> findCarrierDefWithConditions(CarrierDefSearchConditionDto condition, Pageable pageable) {
//
//        // 1. 공통 쿼리 빌더 생성 (SELECT, FROM, WHERE)
//        JPAQuery<CarrierDefResponseDto> query = queryFactory
//                .select(new QCarrierDefResponseDto(
//                        carrierDefEntity.id,
//                        carrierDefEntity.carrierDefName,
//                        carrierDefEntity.description,
//                        carrierDefEntity.carrierType,
//                        carrierDefEntity.carrierDetailType,
//                        carrierDefEntity.defaultCapacity,
//                        carrierDefEntity.useCountLimit,
//                        carrierDefEntity.useDurationLimit,
//                        carrierDefEntity.countLimitPerClean,
//                        carrierDefEntity.durationLimitPerClean,
//                        carrierDefEntity.cleanCountLimit,
//                        carrierDefEntity.checkOutState,
//                        carrierDefEntity.checkOutTime,
//                        carrierDefEntity.checkOutUser,
//                        carrierDefEntity.dataState,
//                        carrierDefEntity.eventName,
//                        carrierDefEntity.eventTime,
//                        carrierDefEntity.eventUser,
//                        carrierDefEntity.eventComment
//                ))
//                .from(carrierDefEntity)
//                .where(
//                        // (WHERE 조건이 있다면 여기에 추가)
//                        carrierDefNameContains(condition.getCarrierDefName())
//                );
//
//        // 2. 정렬 적용
//        query.orderBy(getOrderSpecifiers(pageable.getSort()));
//
//        // 3. 페이징 적용 (isPaged()로 분기)
//        if (pageable.isPaged()) {
//            query.offset(pageable.getOffset());
//            query.limit(pageable.getPageSize());
//        }
//
//        // 4. 데이터 조회
//        List<CarrierDefResponseDto> content = query.fetch();
//
//        // 5. 카운트 조회 (isPaged()로 분기)
//        long total;
//        if (pageable.isPaged()) {
//            // [페이징 O] 별도 카운트 쿼리 실행
//            Long count = queryFactory
//                    .select(carrierDefEntity.count())
//                    .from(carrierDefEntity)
//                    .where(
//                            // (WHERE 조건이 있다면 여기에 추가)
//                            carrierDefNameContains(condition.getCarrierDefName())
//                    )
//                    .fetchOne();
//
//            total = (count != null) ? count.longValue() : 0L;
//
//        } else {
//            // [페이징 X] .unpaged() 일 때
//            total = content.size();
//        }
//
//        // 6. PageImpl 반환
//        return new PageImpl<>(content, pageable, total);
//    }
//
//
//    /**
//     * Pageable의 Sort 객체를 Querydsl의 OrderSpecifier 배열로 변환합니다.
//     */
//    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
//        List<OrderSpecifier> orders = new ArrayList<>();
//
//        if (sort.isSorted()) {
//            for (Sort.Order order : sort) {
//                // 정렬 방향을 결정합니다 (ASC or DESC)
//                Order direction = order.isAscending() ? Order.ASC : Order.DESC;
//
//                // 정렬할 속성(컬럼)을 PathBuilder를 통해 지정합니다.
//                // "userName"과 같은 문자열을 Q-Type 경로로 변환해줍니다.
//                PathBuilder pathBuilder = new PathBuilder<>(carrierDefEntity.getType(), carrierDefEntity.getMetadata());
//
//                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
//            }
//        }
//
//        // 기본 정렬 조건 (만약 정렬 조건이 없다면 id 내림차순)
//        if (orders.isEmpty()) {
//            orders.add(new OrderSpecifier(Order.DESC, carrierDefEntity.id));
//        }
//
//        return orders.toArray(new OrderSpecifier[0]);
//    }
//
//    // == 동적 쿼리를 위한 BooleanExpression 메소드들 ==
//    private BooleanExpression carrierDefNameContains(String carrierDefName) {
//        return StringUtils.hasText(carrierDefName) ? carrierDefEntity.carrierDefName.contains(carrierDefName) : null;
//    }
}
