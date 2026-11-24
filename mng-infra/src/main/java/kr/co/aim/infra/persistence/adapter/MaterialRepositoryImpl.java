package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.domain.model.Material;
import kr.co.aim.domain.repository.MaterialRepository;
import kr.co.aim.domain.repository.condition.MaterialSearchCondition;
import kr.co.aim.infra.persistence.entity.MaterialEntity;
import kr.co.aim.infra.persistence.mapper.MaterialMapper;
import kr.co.aim.infra.persistence.springdatajpa.MaterialJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static kr.co.aim.infra.persistence.entity.QMaterialEntity.materialEntity;

/**
 * MaterialRepository의 JPA 기반 어댑터 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 MaterialJpaRepository 및 QueryDSL에 위임합니다.
 */
@Repository
@RequiredArgsConstructor
public class MaterialRepositoryImpl implements MaterialRepository {

    private final MaterialJpaRepository materialJpaRepository;
    private final MaterialMapper materialMapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Material> findAll() {
        List<MaterialEntity> entities = materialJpaRepository.findAll();
        List<Material> result = new ArrayList<>();
        for (MaterialEntity entity : entities) {
            result.add(materialMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public Optional<Material> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        Optional<MaterialEntity> entityOptional = materialJpaRepository.findById(id);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(materialMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Material> findByMaterialId(String materialId) {
        if (!StringUtils.hasText(materialId)) {
            return Optional.empty();
        }
        Optional<MaterialEntity> entityOptional = materialJpaRepository.findByMaterialId(materialId);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(materialMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public Material save(Material material) {
        MaterialEntity entity = materialMapper.toEntity(material);
        MaterialEntity savedEntity = materialJpaRepository.save(entity);
        return materialMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        materialJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        materialJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public Page<Material> findMaterialsWithConditions(MaterialSearchCondition condition, Pageable pageable) {
        JPAQuery<MaterialEntity> query = queryFactory
                .select(materialEntity)
                .from(materialEntity)
                .where(
                        materialIdContains(condition.getMaterialId()),
                        descriptionContains(condition.getDescription()),
                        materialClassIdEq(condition.getMaterialClassId()),
                        materialGroupIdEq(condition.getMaterialGroupId()),
                        carrierNameContains(condition.getCarrierName()),
                        quantityEq(condition.getQuantity()),
                        eventNameContains(condition.getEventName()),
                        eventUserContains(condition.getEventUser())
                );

        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<MaterialEntity> content = query.fetch();
        List<Material> converted = new ArrayList<>();
        for (MaterialEntity entity : content) {
            if (entity != null) {
                Material domain = materialMapper.toDomain(entity);
                converted.add(domain);
            }
        }

        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(materialEntity.count())
                    .from(materialEntity)
                    .where(
                            materialIdContains(condition.getMaterialId()),
                            descriptionContains(condition.getDescription()),
                            materialClassIdEq(condition.getMaterialClassId()),
                            materialGroupIdEq(condition.getMaterialGroupId()),
                            carrierNameContains(condition.getCarrierName()),
                            quantityEq(condition.getQuantity()),
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
                PathBuilder pathBuilder = new PathBuilder<>(materialEntity.getType(), materialEntity.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, materialEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression materialIdContains(String materialId) {
        return StringUtils.hasText(materialId) ? materialEntity.materialId.contains(materialId) : null;
    }

    private BooleanExpression descriptionContains(String description) {
        return StringUtils.hasText(description) ? materialEntity.description.contains(description) : null;
    }

    private BooleanExpression materialClassIdEq(Long materialClassId) {
        return materialClassId != null ? materialEntity.materialClassId.eq(materialClassId) : null;
    }

    private BooleanExpression materialGroupIdEq(Long materialGroupId) {
        return materialGroupId != null ? materialEntity.materialGroupId.eq(materialGroupId) : null;
    }

    private BooleanExpression carrierNameContains(String carrierName) {
        return StringUtils.hasText(carrierName) ? materialEntity.carrierName.contains(carrierName) : null;
    }

    private BooleanExpression quantityEq(BigDecimal quantity) {
        return quantity != null ? materialEntity.quantity.eq(quantity) : null;
    }

    private BooleanExpression eventNameContains(String eventName) {
        return StringUtils.hasText(eventName) ? materialEntity.eventName.contains(eventName) : null;
    }

    private BooleanExpression eventUserContains(String eventUser) {
        return StringUtils.hasText(eventUser) ? materialEntity.eventUser.contains(eventUser) : null;
    }
}
