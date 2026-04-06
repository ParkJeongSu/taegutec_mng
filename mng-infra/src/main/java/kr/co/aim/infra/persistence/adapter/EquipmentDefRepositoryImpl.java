package kr.co.aim.infra.persistence.adapter;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.EquipmentDef;
import kr.co.aim.domain.repository.EquipmentDefRepository;
import kr.co.aim.infra.persistence.entity.EquipmentDefEntity;
import kr.co.aim.infra.persistence.mapper.EquipmentDefMapper;
import kr.co.aim.infra.persistence.springdatajpa.EquipmentDefJpaRepository;
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
public class EquipmentDefRepositoryImpl implements EquipmentDefRepository {
    // Spring Data JPA가 자동으로 구현해주는 JPA 리포지토리. UserEntity를 다룬다.

    private final EquipmentDefJpaRepository equipmentDefJpaRepository;
    private final EquipmentDefMapper equipmentDefMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입


    @Override
    public List<EquipmentDef> findAll() {
        return equipmentDefJpaRepository.findAll().stream().map(equipmentDefMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<EquipmentDef> findById(Long id) {
        return equipmentDefJpaRepository.findById(id).map(equipmentDefMapper::toDomain);
    }

    @Override
    public Optional<EquipmentDef> findByEquipmentName(String equipmentDefName) {
        return equipmentDefJpaRepository.findByEquipmentDefName(equipmentDefName).map(equipmentDefMapper::toDomain);
    }

    @Override
    public EquipmentDef save(EquipmentDef equipmentDef) {
        EquipmentDefEntity entity = equipmentDefMapper.toEntity(equipmentDef);
        EquipmentDefEntity savedEntity = equipmentDefJpaRepository.save(entity);
        return equipmentDefMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        equipmentDefJpaRepository.deleteAllByIdInBatch(ids);
    }

//    @Override
//    public Page<EquipmentDefResponseDto> findEquipmentDefWithConditions(EquipmentDefSearchConditionDto condition, Pageable pageable) {
//
//        // 1. 공통 쿼리 빌더 생성 (SELECT, FROM, JOIN, WHERE)
//        JPAQuery<EquipmentDefResponseDto> query = queryFactory
//                .select(new QEquipmentDefResponseDto(
//                            equipmentDefEntity.id,
//                            equipmentDefEntity.equipmentDefName,
//                            equipmentDefEntity.description,
//                            equipmentDefEntity.equipmentType,
//                            equipmentDefEntity.equipmentGroupId,
//                            equipmentDefEntity.detailEquipmentType,
//                            equipmentDefEntity.vendorId,
//                            equipmentDefEntity.modelId,
//                            equipmentDefEntity.processCapacity,
//                            equipmentDefEntity.checkOutState,
//                            equipmentDefEntity.checkOutTime,
//                            equipmentDefEntity.checkOutUser,
//                            equipmentDefEntity.dataState,
//                            equipmentDefEntity.eventName,
//                            equipmentDefEntity.eventTime,
//                            equipmentDefEntity.eventUser,
//                            equipmentDefEntity.eventComment,
//                            equipmentDefEntity.containerType
//                ))
//                .from(equipmentDefEntity)
//                .leftJoin(equipmentGroupEntity).on(equipmentDefEntity.equipmentGroupId.eq(equipmentGroupEntity.id))
//                .where(
//                        // (WHERE 조건이 있다면 여기에 추가)
//                        equipmentDefNameContains(condition.getEquipmentDefName())
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
//        List<EquipmentDefResponseDto> content = query.fetch();
//
//        // 5. 카운트 조회 (isPaged()로 분기)
//        long total;
//        if (pageable.isPaged()) {
//            // [페이징 O] 별도 카운트 쿼리 실행
//            Long count = queryFactory
//                    .select(equipmentDefEntity.count())
//                    .from(equipmentDefEntity)
//                    .leftJoin(equipmentGroupEntity).on(equipmentDefEntity.equipmentGroupId.eq(equipmentGroupEntity.id))
//                    .where(
//                            // (WHERE 조건이 있다면 여기에 추가)
//                            equipmentDefNameContains(condition.getEquipmentDefName())
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
//                PathBuilder pathBuilder = new PathBuilder<>(equipmentDefEntity.getType(), equipmentDefEntity.getMetadata());
//
//                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
//            }
//        }
//
//        // 기본 정렬 조건 (만약 정렬 조건이 없다면 id 내림차순)
//        if (orders.isEmpty()) {
//            orders.add(new OrderSpecifier(Order.DESC, equipmentDefEntity.id));
//        }
//
//        return orders.toArray(new OrderSpecifier[0]);
//    }
//
//    // == 동적 쿼리를 위한 BooleanExpression 메소드들 ==
//    private BooleanExpression equipmentDefNameContains(String equipmentDefName) {
//        return StringUtils.hasText(equipmentDefName) ? equipmentDefEntity.equipmentDefName.contains(equipmentDefName) : null;
//    }
}
