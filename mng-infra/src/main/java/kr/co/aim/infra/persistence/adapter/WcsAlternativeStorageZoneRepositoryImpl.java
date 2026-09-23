package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.condition.WcsAlternativeStorageZoneSearchCondition;
import kr.co.aim.domain.model.WcsAlternativeStorageZone;
import kr.co.aim.domain.repository.WcsAlternativeStorageZoneRepository;
import kr.co.aim.infra.persistence.entity.QWcsAlternativeStorageZoneEntity;
import kr.co.aim.infra.persistence.entity.WcsAlternativeStorageZoneEntity;
import kr.co.aim.infra.persistence.entity.WcsAlternativeStorageZoneId;
import kr.co.aim.infra.persistence.mapper.WcsAlternativeStorageZoneMapper;
import kr.co.aim.infra.persistence.springdatajpa.WcsAlternativeStorageZoneJpaRepository;
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

@Repository
@RequiredArgsConstructor
public class WcsAlternativeStorageZoneRepositoryImpl implements WcsAlternativeStorageZoneRepository {

    private final WcsAlternativeStorageZoneJpaRepository wcsAlternativeStorageZoneJpaRepository;
    private final WcsAlternativeStorageZoneMapper wcsAlternativeStorageZoneMapper;
    private final JPAQueryFactory queryFactory;

    private static final QWcsAlternativeStorageZoneEntity qAltZone = QWcsAlternativeStorageZoneEntity.wcsAlternativeStorageZoneEntity;

    @Override
    public List<WcsAlternativeStorageZone> findAll() {
        List<WcsAlternativeStorageZoneEntity> entities = wcsAlternativeStorageZoneJpaRepository.findAll();
        List<WcsAlternativeStorageZone> result = new ArrayList<>();
        for (WcsAlternativeStorageZoneEntity entity : entities) {
            if (entity != null) {
                result.add(wcsAlternativeStorageZoneMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public Optional<WcsAlternativeStorageZone> findById(String factoryName, String sourceZoneName, String alternativeZoneName, Integer priority) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(sourceZoneName)
                || !StringUtils.hasText(alternativeZoneName) || priority == null) {
            return Optional.empty();
        }
        WcsAlternativeStorageZoneId id = WcsAlternativeStorageZoneId.builder()
                .factoryName(factoryName)
                .sourceZoneName(sourceZoneName)
                .alternativeZoneName(alternativeZoneName)
                .priority(priority)
                .build();
        Optional<WcsAlternativeStorageZoneEntity> entityOpt = wcsAlternativeStorageZoneJpaRepository.findById(id);
        if (entityOpt.isPresent()) {
            return Optional.ofNullable(wcsAlternativeStorageZoneMapper.toDomain(entityOpt.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<WcsAlternativeStorageZone> findByFactoryName(String factoryName) {
        if (!StringUtils.hasText(factoryName)) {
            return new ArrayList<>();
        }
        List<WcsAlternativeStorageZoneEntity> entities = wcsAlternativeStorageZoneJpaRepository.findByFactoryName(factoryName);
        List<WcsAlternativeStorageZone> result = new ArrayList<>();
        for (WcsAlternativeStorageZoneEntity entity : entities) {
            if (entity != null) {
                result.add(wcsAlternativeStorageZoneMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public List<WcsAlternativeStorageZone> findByFactoryNameAndSourceZoneName(String factoryName, String sourceZoneName) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(sourceZoneName)) {
            return new ArrayList<>();
        }
        List<WcsAlternativeStorageZoneEntity> entities = wcsAlternativeStorageZoneJpaRepository.findByFactoryNameAndSourceZoneNameOrderByPriorityAsc(factoryName, sourceZoneName);
        List<WcsAlternativeStorageZone> result = new ArrayList<>();
        for (WcsAlternativeStorageZoneEntity entity : entities) {
            if (entity != null) {
                result.add(wcsAlternativeStorageZoneMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public boolean existsById(String factoryName, String sourceZoneName, String alternativeZoneName, Integer priority) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(sourceZoneName)
                || !StringUtils.hasText(alternativeZoneName) || priority == null) {
            return false;
        }
        WcsAlternativeStorageZoneId id = WcsAlternativeStorageZoneId.builder()
                .factoryName(factoryName)
                .sourceZoneName(sourceZoneName)
                .alternativeZoneName(alternativeZoneName)
                .priority(priority)
                .build();
        return wcsAlternativeStorageZoneJpaRepository.existsById(id);
    }

    @Override
    public WcsAlternativeStorageZone save(WcsAlternativeStorageZone alternativeStorageZone) {
        WcsAlternativeStorageZoneEntity entity = wcsAlternativeStorageZoneMapper.toEntity(alternativeStorageZone);
        WcsAlternativeStorageZoneEntity savedEntity = wcsAlternativeStorageZoneJpaRepository.save(entity);
        return wcsAlternativeStorageZoneMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(String factoryName, String sourceZoneName, String alternativeZoneName, Integer priority) {
        if (StringUtils.hasText(factoryName) && StringUtils.hasText(sourceZoneName)
                && StringUtils.hasText(alternativeZoneName) && priority != null) {
            WcsAlternativeStorageZoneId id = WcsAlternativeStorageZoneId.builder()
                    .factoryName(factoryName)
                    .sourceZoneName(sourceZoneName)
                    .alternativeZoneName(alternativeZoneName)
                    .priority(priority)
                    .build();
            wcsAlternativeStorageZoneJpaRepository.deleteById(id);
        }
    }

    @Override
    public Page<WcsAlternativeStorageZone> findAlternativeStorageZones(WcsAlternativeStorageZoneSearchCondition condition, Pageable pageable) {
        String factoryName = (condition != null) ? condition.getFactoryName() : null;
        String sourceZoneName = (condition != null) ? condition.getSourceZoneName() : null;
        String alternativeZoneName = (condition != null) ? condition.getAlternativeZoneName() : null;
        Integer priority = (condition != null) ? condition.getPriority() : null;
        String description = (condition != null) ? condition.getDescription() : null;
        String useYn = (condition != null) ? condition.getUseYn() : null;

        JPAQuery<WcsAlternativeStorageZoneEntity> query = queryFactory
                .selectFrom(qAltZone)
                .where(
                        factoryNameContains(factoryName),
                        sourceZoneNameContains(sourceZoneName),
                        alternativeZoneNameContains(alternativeZoneName),
                        priorityEq(priority),
                        descriptionContains(description),
                        useYnEq(useYn)
                );

        query.orderBy(getOrderSpecifiers(pageable != null ? pageable.getSort() : Sort.unsorted()));

        if (pageable != null && pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<WcsAlternativeStorageZoneEntity> content = query.fetch();
        List<WcsAlternativeStorageZone> resultList = new ArrayList<>();
        for (WcsAlternativeStorageZoneEntity entity : content) {
            if (entity != null) {
                resultList.add(wcsAlternativeStorageZoneMapper.toDomain(entity));
            }
        }

        long total;
        if (pageable != null && pageable.isPaged()) {
            Long count = queryFactory
                    .select(qAltZone.count())
                    .from(qAltZone)
                    .where(
                            factoryNameContains(factoryName),
                            sourceZoneNameContains(sourceZoneName),
                            alternativeZoneNameContains(alternativeZoneName),
                            priorityEq(priority),
                            descriptionContains(description),
                            useYnEq(useYn)
                    )
                    .fetchOne();
            total = (count != null) ? count.longValue() : 0L;
        } else {
            total = resultList.size();
        }

        return new PageImpl<>(resultList, pageable != null ? pageable : Pageable.unpaged(), total);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();

        if (sort != null && sort.isSorted()) {
            for (Sort.Order order : sort) {
                Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                PathBuilder pathBuilder = new PathBuilder<>(qAltZone.getType(), qAltZone.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.ASC, qAltZone.factoryName));
            orders.add(new OrderSpecifier(Order.ASC, qAltZone.sourceZoneName));
            orders.add(new OrderSpecifier(Order.ASC, qAltZone.priority));
            orders.add(new OrderSpecifier(Order.ASC, qAltZone.alternativeZoneName));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression factoryNameContains(String factoryName) {
        return StringUtils.hasText(factoryName) ? qAltZone.factoryName.contains(factoryName) : null;
    }

    private BooleanExpression sourceZoneNameContains(String sourceZoneName) {
        return StringUtils.hasText(sourceZoneName) ? qAltZone.sourceZoneName.contains(sourceZoneName) : null;
    }

    private BooleanExpression alternativeZoneNameContains(String alternativeZoneName) {
        return StringUtils.hasText(alternativeZoneName) ? qAltZone.alternativeZoneName.contains(alternativeZoneName) : null;
    }

    private BooleanExpression priorityEq(Integer priority) {
        return priority != null ? qAltZone.priority.eq(priority) : null;
    }

    private BooleanExpression descriptionContains(String description) {
        return StringUtils.hasText(description) ? qAltZone.description.contains(description) : null;
    }

    private BooleanExpression useYnEq(String useYn) {
        return StringUtils.hasText(useYn) ? qAltZone.useYn.equalsIgnoreCase(useYn) : null;
    }
}
