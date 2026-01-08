package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.dto.*;
import kr.co.aim.domain.model.EquipmentGroup;
import kr.co.aim.domain.repository.EquipmentGroupRepository;
import kr.co.aim.infra.persistence.entity.EquipmentGroupEntity;
import kr.co.aim.infra.persistence.entity.QEquipmentsEntity;
import kr.co.aim.infra.persistence.mapper.EquipmentGroupMapper;
import kr.co.aim.infra.persistence.springdatajpa.EquipmentGroupJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static kr.co.aim.infra.persistence.entity.QEquipmentGroupEntity.equipmentGroupEntity;

/**
 * UserRepository의 JPA 기반 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 JpaRepository에 위임합니다.
 */

@Repository
@RequiredArgsConstructor
public class EquipmentGroupRepositoryImpl implements EquipmentGroupRepository {
    // Spring Data JPA가 자동으로 구현해주는 JPA 리포지토리. UserEntity를 다룬다.
    private final EquipmentGroupJpaRepository equipmentGroupJpaRepository;
    private final EquipmentGroupMapper equipmentGroupMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        equipmentGroupJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public List<EquipmentGroup> findAll() {
        return equipmentGroupJpaRepository.findAll().stream().map(equipmentGroupMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<EquipmentGroup> findById(Long id) {
        return equipmentGroupJpaRepository.findById(id).map(equipmentGroupMapper::toDomain);
    }

    @Override
    public Optional<EquipmentGroup> findByEquipmentGroupName(String equipmentGroupName) {
        return equipmentGroupJpaRepository.findByEquipmentGroupName(equipmentGroupName).map(equipmentGroupMapper::toDomain);
    }

    @Override
    public EquipmentGroup save(EquipmentGroup equipmentGroup) {
        EquipmentGroupEntity entity = equipmentGroupMapper.toEntity(equipmentGroup);
        EquipmentGroupEntity savedEntity = equipmentGroupJpaRepository.save(entity);
        return equipmentGroupMapper.toDomain(savedEntity);
    }

    @Override
    public Page<EquipmentGroupResponseDto> findEquipmentGroupWithConditions(EquipmentGroupSearchCondtionDto condition, Pageable pageable) {
        // This variable is declared in the original code but not used in the query.
        QEquipmentsEntity joinEquipments = new QEquipmentsEntity("joinEquipments");

        // 1. 공통 쿼리 빌더 생성 (SELECT, FROM, WHERE)
        JPAQuery<EquipmentGroupResponseDto> query = queryFactory
                .select(new QEquipmentGroupResponseDto(
                        equipmentGroupEntity.id,
                        equipmentGroupEntity.equipmentGroupName,
                        equipmentGroupEntity.description,
                        equipmentGroupEntity.checkOutState,
                        equipmentGroupEntity.checkOutTime,
                        equipmentGroupEntity.checkOutUser,
                        equipmentGroupEntity.dataState,
                        equipmentGroupEntity.eventName,
                        equipmentGroupEntity.eventTime,
                        equipmentGroupEntity.eventUser,
                        equipmentGroupEntity.eventComment
                ))
                .from(equipmentGroupEntity)
                .where(
                        // (WHERE 조건이 있다면 여기에 추가)
                        equipmentDefNameContains(condition.getEquipmentGroupName())
                );

        // 2. 정렬 적용
        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        // 3. 페이징 적용 (isPaged()로 분기)
        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        // 4. 데이터 조회
        List<EquipmentGroupResponseDto> content = query.fetch();

        // 5. 카운트 조회 (isPaged()로 분기)
        long total;
        if (pageable.isPaged()) {
            // [페이징 O] 별도 카운트 쿼리 실행
            Long count = queryFactory
                    .select(equipmentGroupEntity.count())
                    .from(equipmentGroupEntity)
                    .where(
                            // (WHERE 조건이 있다면 여기에 추가)
                            equipmentDefNameContains(condition.getEquipmentGroupName())
                    )
                    .fetchOne();

            total = (count != null) ? count.longValue() : 0L;

        } else {
            // [페이징 X] .unpaged() 일 때
            total = content.size();
        }

        // 6. PageImpl 반환
        return new PageImpl<>(content, pageable, total);
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
                PathBuilder pathBuilder = new PathBuilder<>(equipmentGroupEntity.getType(), equipmentGroupEntity.getMetadata());

                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        // 기본 정렬 조건 (만약 정렬 조건이 없다면 id 내림차순)
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, equipmentGroupEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    // == 동적 쿼리를 위한 BooleanExpression 메소드들 ==
    private BooleanExpression equipmentDefNameContains(String equipmentGroupName) {
        return StringUtils.hasText(equipmentGroupName) ? equipmentGroupEntity.equipmentGroupName.contains(equipmentGroupName) : null;
    }
}
