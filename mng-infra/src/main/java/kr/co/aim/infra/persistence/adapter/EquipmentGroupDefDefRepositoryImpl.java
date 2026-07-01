package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.dto.EquipmentGroupDefSearchConditionDto;
import kr.co.aim.domain.model.EquipmentGroupDef;
import kr.co.aim.domain.repository.EquipmentGroupDefRepository;
import kr.co.aim.infra.persistence.entity.EquipmentGroupDefEntity;
import kr.co.aim.infra.persistence.mapper.EquipmentGroupDefMapper;
import kr.co.aim.infra.persistence.springdatajpa.EquipmentGroupDefJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static kr.co.aim.infra.persistence.entity.QEquipmentGroupDefEntity.equipmentGroupDefEntity;

/**
 * UserRepository의 JPA 기반 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 JpaRepository에 위임합니다.
 */

@Repository
@RequiredArgsConstructor
public class EquipmentGroupDefDefRepositoryImpl implements EquipmentGroupDefRepository {
    // Spring Data JPA가 자동으로 구현해주는 JPA 리포지토리. UserEntity를 다룬다.
    private final EquipmentGroupDefJpaRepository equipmentGroupDefJpaRepository;
    private final EquipmentGroupDefMapper equipmentGroupDefMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        equipmentGroupDefJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public List<EquipmentGroupDef> findAll() {
        return equipmentGroupDefJpaRepository.findAll().stream().map(equipmentGroupDefMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<EquipmentGroupDef> findById(Long id) {
        return equipmentGroupDefJpaRepository.findById(id).map(equipmentGroupDefMapper::toDomain);
    }

    @Override
    public Optional<EquipmentGroupDef> findByEquipmentGroupName(String equipmentGroupName) {
        return equipmentGroupDefJpaRepository.findByEquipmentGroupName(equipmentGroupName).map(equipmentGroupDefMapper::toDomain);
    }

    @Override
    public EquipmentGroupDef save(EquipmentGroupDef equipmentGroupDef) {
        EquipmentGroupDefEntity entity = equipmentGroupDefMapper.toEntity(equipmentGroupDef);
        EquipmentGroupDefEntity savedEntity = equipmentGroupDefJpaRepository.save(entity);
        return equipmentGroupDefMapper.toDomain(savedEntity);
    }

    @Override
    public Page<EquipmentGroupDef> findEquipmentGroupDefWithConditions(EquipmentGroupDefSearchConditionDto condition, Pageable pageable) {
        JPAQuery<EquipmentGroupDefEntity> query = queryFactory
                .selectFrom(equipmentGroupDefEntity)
                .where(equipmentGroupNameContains(condition.getEquipmentGroupName()));

        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<EquipmentGroupDefEntity> content = query.fetch();
        List<EquipmentGroupDef> converted = content.stream().map(equipmentGroupDefMapper::toDomain).collect(Collectors.toList());

        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(equipmentGroupDefEntity.count())
                    .from(equipmentGroupDefEntity)
                    .where(equipmentGroupNameContains(condition.getEquipmentGroupName()))
                    .fetchOne();
            total = (count != null) ? count : 0L;
        } else {
            total = content.size();
        }

        return new PageImpl<>(converted, pageable, total);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();
        if (sort.isSorted()) {
            for (Sort.Order order : sort) {
                Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                PathBuilder pathBuilder = new PathBuilder<>(equipmentGroupDefEntity.getType(), equipmentGroupDefEntity.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, equipmentGroupDefEntity.id));
        }
        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression equipmentGroupNameContains(String name) {
        return StringUtils.hasText(name) ? equipmentGroupDefEntity.equipmentGroupName.contains(name) : null;
    }

}
