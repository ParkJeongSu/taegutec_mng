package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.MaterialGroup;
import kr.co.aim.domain.repository.MaterialGroupRepository;
import kr.co.aim.domain.repository.condition.MaterialGroupSearchCondition;
import kr.co.aim.infra.persistence.entity.MaterialGroupEntity;
import kr.co.aim.infra.persistence.mapper.MaterialGroupMapper;
import kr.co.aim.infra.persistence.springdatajpa.MaterialGroupJpaRepository;
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

import static kr.co.aim.infra.persistence.entity.QMaterialGroupEntity.materialGroupEntity;

/**
 * MaterialGroupRepository의 JPA 기반 어댑터 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 MaterialGroupJpaRepository 및 QueryDSL에 위임합니다.
 */
@Repository
@RequiredArgsConstructor
public class MaterialGroupRepositoryImpl implements MaterialGroupRepository {

    private final MaterialGroupJpaRepository materialGroupJpaRepository;
    private final MaterialGroupMapper materialGroupMapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<MaterialGroup> findAll() {
        List<MaterialGroupEntity> entities = materialGroupJpaRepository.findAll();
        List<MaterialGroup> result = new ArrayList<>();
        for (MaterialGroupEntity entity : entities) {
            result.add(materialGroupMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public Optional<MaterialGroup> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        Optional<MaterialGroupEntity> entityOptional = materialGroupJpaRepository.findById(id);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(materialGroupMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<MaterialGroup> findByMaterialGroupName(String materialGroupDefName) {
        if (!StringUtils.hasText(materialGroupDefName)) {
            return Optional.empty();
        }
        Optional<MaterialGroupEntity> entityOptional = materialGroupJpaRepository.findByMaterialGroupName(materialGroupDefName);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(materialGroupMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public MaterialGroup save(MaterialGroup materialGroup) {
        MaterialGroupEntity entity = materialGroupMapper.toEntity(materialGroup);
        MaterialGroupEntity savedEntity = materialGroupJpaRepository.save(entity);
        return materialGroupMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        materialGroupJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        materialGroupJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public Page<MaterialGroup> findMaterialGroupsWithConditions(MaterialGroupSearchCondition condition, Pageable pageable) {
        JPAQuery<MaterialGroupEntity> query = queryFactory
                .select(materialGroupEntity)
                .from(materialGroupEntity)
                .where(
                        materialGroupNameContains(condition.getMaterialGroupDefName()),
                        descriptionContains(condition.getDescription()),
                        eventNameContains(condition.getEventName()),
                        eventUserContains(condition.getEventUser())
                );

        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<MaterialGroupEntity> content = query.fetch();
        List<MaterialGroup> converted = new ArrayList<>();
        for (MaterialGroupEntity entity : content) {
            if (entity != null) {
                MaterialGroup domain = materialGroupMapper.toDomain(entity);
                converted.add(domain);
            }
        }

        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(materialGroupEntity.count())
                    .from(materialGroupEntity)
                    .where(
                            materialGroupNameContains(condition.getMaterialGroupDefName()),
                            descriptionContains(condition.getDescription()),
                            eventNameContains(condition.getEventName()),
                            eventUserContains(condition.getEventUser())
                    )
                    .fetchOne();

            total = (count != null) ? count.longValue() : 0L;
        } else {
            total = converted.size();
        }

        return new PageImpl<>(converted, pageable, total);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();

        if (sort != null && sort.isSorted()) {
            for (Sort.Order order : sort) {
                Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                PathBuilder pathBuilder = new PathBuilder<>(materialGroupEntity.getType(), materialGroupEntity.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, materialGroupEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression materialGroupNameContains(String materialGroupDefName) {
        return StringUtils.hasText(materialGroupDefName) ? materialGroupEntity.materialGroupName.contains(materialGroupDefName) : null;
    }

    private BooleanExpression descriptionContains(String description) {
        return StringUtils.hasText(description) ? materialGroupEntity.description.contains(description) : null;
    }

    private BooleanExpression eventNameContains(String eventName) {
        return StringUtils.hasText(eventName) ? materialGroupEntity.eventName.contains(eventName) : null;
    }

    private BooleanExpression eventUserContains(String eventUser) {
        return StringUtils.hasText(eventUser) ? materialGroupEntity.eventUser.contains(eventUser) : null;
    }
}
