package kr.co.aim.infra.persistence.adapter;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.Carrier;
import kr.co.aim.domain.repository.CarrierRepository;
import kr.co.aim.infra.persistence.entity.CarrierEntity;
import kr.co.aim.infra.persistence.mapper.CarrierMapper;
import kr.co.aim.infra.persistence.springdatajpa.CarrierJpaRepository;
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
public class CarrierRepositoryImpl implements CarrierRepository {
    // Spring Data JPA가 자동으로 구현해주는 JPA 리포지토리. UserEntity를 다룬다.
    private final CarrierJpaRepository carrierJpaRepository;
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
    public List<Carrier> findCarriersForFullContainer(String cleanState, String transportState, String transportJobId, String useState, String orderId, String orderLineNumber) {
        return carrierJpaRepository.findCarriersForFullContainer(
                cleanState,
                transportState,
                transportJobId,
                useState,
                orderId,
                orderLineNumber
        ).stream().map(carrierMapper::toDomain).collect(Collectors.toList());
    }

//    @Override
//    public Page<CarriersResponseDto> findCarriersWithConditions(CarriersSearchConditionDto condition, Pageable pageable) {
//
//        // 1. 공통 쿼리 빌더 생성 (SELECT, FROM, JOIN, WHERE)
//        JPAQuery<CarriersResponseDto> query = queryFactory
//                .select(new QCarriersResponseDto(
//                        carriersEntity.id,
//                        carriersEntity.carrierName,
//                        carriersEntity.carrierDefId,
//                        carrierDefEntity.carrierDefName,
//                        carriersEntity.carrierState,
//                        carriersEntity.equipmentName,
//                        carriersEntity.portName,
//                        carriersEntity.zoneName,
//                        carriersEntity.shelfName,
//                        carriersEntity.capacity,
//                        carriersEntity.cleanState,
//                        carriersEntity.transportState,
//                        carriersEntity.reservedObjectId,
//                        carriersEntity.holdState,
//                        carriersEntity.reasonCode,
//                        carriersEntity.useState,
//                        carriersEntity.useCount,
//                        carriersEntity.useCountPerClean,
//                        carriersEntity.cleanCount,
//                        carriersEntity.lotQuantity,
//                        carriersEntity.capaState,
//                        carriersEntity.lastCleanTime,
//                        carriersEntity.createTime,
//                        carriersEntity.eventName,
//                        carriersEntity.eventTime,
//                        carriersEntity.eventUser,
//                        carriersEntity.eventComment,
//                        carriersEntity.containerType
//                ))
//                .from(carriersEntity)
//                .leftJoin(carrierDefEntity).on(carriersEntity.carrierDefId.eq(carrierDefEntity.id))
//                .where(
//                        // (WHERE 조건이 있다면 여기에 추가)
//                        carrierDefNameContains(condition.getCarrierDefName()),
//                        carrierNameContains(condition.getCarrierName())
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
//        List<CarriersResponseDto> content = query.fetch();
//
//        // 5. 카운트 조회 (isPaged()로 분기)
//        long total;
//        if (pageable.isPaged()) {
//            // [페이징 O] 별도 카운트 쿼리 실행
//            Long count = queryFactory
//                    .select(carriersEntity.count())
//                    .from(carriersEntity)
//                    .leftJoin(carrierDefEntity).on(carriersEntity.carrierDefId.eq(carrierDefEntity.id))
//                    .where(
//                            // (WHERE 조건이 있다면 여기에 추가)
//                            carrierDefNameContains(condition.getCarrierDefName()),
//                            carrierNameContains(condition.getCarrierName())
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
//                PathBuilder pathBuilder = new PathBuilder<>(carriersEntity.getType(), carriersEntity.getMetadata());
//
//                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
//            }
//        }
//
//        // 기본 정렬 조건 (만약 정렬 조건이 없다면 id 내림차순)
//        if (orders.isEmpty()) {
//            orders.add(new OrderSpecifier(Order.DESC, carriersEntity.id));
//        }
//
//        return orders.toArray(new OrderSpecifier[0]);
//    }
//
//    // == 동적 쿼리를 위한 BooleanExpression 메소드들 ==
//    private BooleanExpression carrierDefNameContains(String carrierDefName) {
//        return StringUtils.hasText(carrierDefName) ? carrierDefEntity.carrierDefName.contains(carrierDefName) : null;
//    }
//
//    private BooleanExpression carrierNameContains(String carrierName) {
//        return StringUtils.hasText(carrierName) ? carriersEntity.carrierName.contains(carrierName) : null;
//    }
}
