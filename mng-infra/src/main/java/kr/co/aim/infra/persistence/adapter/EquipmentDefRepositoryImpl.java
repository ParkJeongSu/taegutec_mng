package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.condition.EquipmentDefSearchCondition;
import kr.co.aim.domain.model.EquipmentDef;
import kr.co.aim.domain.repository.EquipmentDefRepository;
import kr.co.aim.infra.persistence.entity.EquipmentDefEntity;
import kr.co.aim.infra.persistence.mapper.EquipmentDefMapper;
import kr.co.aim.infra.persistence.springdatajpa.EquipmentDefJpaRepository;
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
/**
 * UserRepository의 JPA 기반 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 JpaRepository에 위임합니다.
 */

import static kr.co.aim.infra.persistence.entity.QEquipmentDefEntity.equipmentDefEntity;

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
    public Optional<EquipmentDef> findByEquipmentName(String equipmentName) {
        return equipmentDefJpaRepository.findByEquipmentName(equipmentName).map(equipmentDefMapper::toDomain);
    }

    @Override
    public EquipmentDef save(EquipmentDef equipmentDef) {
        EquipmentDefEntity entity = equipmentDefMapper.toEntity(equipmentDef);
        EquipmentDefEntity savedEntity = equipmentDefJpaRepository.save(entity);
        return equipmentDefMapper.toDomain(savedEntity);
    }

    @Override
    public Page<EquipmentDef> findEquipmentDefWithConditions(EquipmentDefSearchCondition condition, Pageable pageable) {
        JPAQuery<EquipmentDefEntity> query = queryFactory
                .selectFrom(equipmentDefEntity)
                .where(equipmentNameContains(condition.getEquipmentName()));

        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<EquipmentDefEntity> content = query.fetch();
        List<EquipmentDef> converted = content.stream().map(equipmentDefMapper::toDomain).collect(Collectors.toList());

        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(equipmentDefEntity.count())
                    .from(equipmentDefEntity)
                    .where(equipmentNameContains(condition.getEquipmentName()))
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
                PathBuilder pathBuilder = new PathBuilder<>(equipmentDefEntity.getType(), equipmentDefEntity.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, equipmentDefEntity.id));
        }
        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression equipmentNameContains(String name) {
        return StringUtils.hasText(name) ? equipmentDefEntity.equipmentName.contains(name) : null;
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        equipmentDefJpaRepository.deleteAllByIdInBatch(ids);
    }
}
