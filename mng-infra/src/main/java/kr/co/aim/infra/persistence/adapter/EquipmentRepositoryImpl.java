package kr.co.aim.infra.persistence.adapter;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.Equipment;
import kr.co.aim.domain.repository.EquipmentRepository;
import kr.co.aim.infra.persistence.entity.EquipmentEntity;
import kr.co.aim.infra.persistence.mapper.EquipmentMapper;
import kr.co.aim.infra.persistence.springdatajpa.EquipmentJpaRepository;
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
public class EquipmentRepositoryImpl implements EquipmentRepository {
    // Spring Data JPA가 자동으로 구현해주는 JPA 리포지토리. UserEntity를 다룬다.
    private final EquipmentJpaRepository equipmentJpaRepository;
    private final EquipmentMapper equipmentMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입

    @Override
    public List<Equipment> findAll() {
        return equipmentJpaRepository.findAll().stream().map(equipmentMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Equipment> findById(Long id) {
        return equipmentJpaRepository.findById(id).map(equipmentMapper::toDomain);
    }

    @Override
    public Optional<Equipment> findByEquipmentName(String equipmentName) {
        return equipmentJpaRepository.findByEquipmentName(equipmentName).map(equipmentMapper::toDomain);
    }

    //@PublishHistoryEvent
    @Override
    public Equipment save(Equipment equipment) {
        EquipmentEntity entity = equipmentMapper.toEntity(equipment);
        EquipmentEntity savedEntity = equipmentJpaRepository.save(entity);
        return equipmentMapper.toDomain(savedEntity);
    }
        @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        equipmentJpaRepository.deleteAllByIdInBatch(ids);
    }

//    @Override
//    public Page<EquipmentsResponseDto> findEquipmentsWithConditions(EquipmentsSearchConditionDto condition, Pageable pageable) {
//
//        QEquipmentsEntity joinEquipments = new QEquipmentsEntity("joinEquipments"); // 별칭 필수!
//
//        // 1. 공통 쿼리 빌더 생성 (SELECT, FROM, JOIN, WHERE)
//        JPAQuery<EquipmentsResponseDto> query = queryFactory
//                .select(new QEquipmentsResponseDto(
//                        equipmentsEntity.id,
//                        equipmentsEntity.equipmentName,
//                        equipmentsEntity.equipmentDefId,
//                        equipmentsEntity.parentEquipmentId,
//                        equipmentsEntity.equipmentLevel,
//                        equipmentsEntity.equipmentState,
//                        equipmentsEntity.communicationState,
//                        equipmentsEntity.loadingCount,
//                        equipmentsEntity.processCount,
//                        equipmentsEntity.recipeName,
//                        equipmentsEntity.holdState,
//                        equipmentsEntity.reasonCode,
//                        equipmentsEntity.resourceState,
//                        equipmentsEntity.operationMode,
//                        equipmentsEntity.messageServiceAddress,
//                        equipmentsEntity.eventName,
//                        equipmentsEntity.eventTime,
//                        equipmentsEntity.eventUser,
//                        equipmentsEntity.eventComment,
//                        equipmentsEntity.workOrderId
//                ))
//                .from(equipmentsEntity)
//                .leftJoin(equipmentDefEntity).on(equipmentsEntity.equipmentDefId.eq(equipmentDefEntity.id))
//                .leftJoin(joinEquipments).on(equipmentsEntity.parentEquipmentId.eq(joinEquipments.id))
//                .where(
//                        // (WHERE 조건이 있다면 여기에 추가)
//                        equipmentNameContains(condition.getEquipmentName())
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
//        List<EquipmentsResponseDto> content = query.fetch();
//
//        // 5. 카운트 조회 (isPaged()로 분기)
//        long total;
//        if (pageable.isPaged()) {
//            // [페이징 O] 별도 카운트 쿼리 실행
//            Long count = queryFactory
//                    .select(equipmentsEntity.count())
//                    .from(equipmentsEntity)
//                    .leftJoin(equipmentDefEntity).on(equipmentsEntity.equipmentDefId.eq(equipmentDefEntity.id))
//                    .leftJoin(joinEquipments).on(equipmentsEntity.parentEquipmentId.eq(joinEquipments.id))
//                    .where(
//                            // (WHERE 조건이 있다면 여기에 추가)
//                            equipmentNameContains(condition.getEquipmentName())
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
//                PathBuilder pathBuilder = new PathBuilder<>(equipmentsEntity.getType(), equipmentsEntity.getMetadata());
//
//                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
//            }
//        }
//
//        // 기본 정렬 조건 (만약 정렬 조건이 없다면 id 내림차순)
//        if (orders.isEmpty()) {
//            orders.add(new OrderSpecifier(Order.DESC, equipmentsEntity.id));
//        }
//
//        return orders.toArray(new OrderSpecifier[0]);
//    }
//
//
//    // == 동적 쿼리를 위한 BooleanExpression 메소드들 ==
//    private BooleanExpression equipmentNameContains(String equipmentName) {
//        return StringUtils.hasText(equipmentName) ? equipmentsEntity.equipmentName.contains(equipmentName) : null;
//    }
}
