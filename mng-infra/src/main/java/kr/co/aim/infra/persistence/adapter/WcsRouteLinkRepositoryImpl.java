package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.condition.WcsRouteLinkSearchCondition;
import kr.co.aim.domain.model.WcsRouteLink;
import kr.co.aim.domain.repository.WcsRouteLinkRepository;
import kr.co.aim.infra.persistence.entity.QWcsRouteLinkEntity;
import kr.co.aim.infra.persistence.entity.WcsRouteLinkEntity;
import kr.co.aim.infra.persistence.entity.WcsRouteLinkId;
import kr.co.aim.infra.persistence.mapper.WcsRouteLinkMapper;
import kr.co.aim.infra.persistence.springdatajpa.WcsRouteLinkJpaRepository;
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
public class WcsRouteLinkRepositoryImpl implements WcsRouteLinkRepository {

    private final WcsRouteLinkJpaRepository wcsRouteLinkJpaRepository;
    private final WcsRouteLinkMapper wcsRouteLinkMapper;
    private final JPAQueryFactory queryFactory;

    private static final QWcsRouteLinkEntity qRouteLink = QWcsRouteLinkEntity.wcsRouteLinkEntity;

    @Override
    public List<WcsRouteLink> findAll() {
        List<WcsRouteLinkEntity> entities = wcsRouteLinkJpaRepository.findAll();
        List<WcsRouteLink> result = new ArrayList<>();
        for (WcsRouteLinkEntity entity : entities) {
            if (entity != null) {
                result.add(wcsRouteLinkMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public Optional<WcsRouteLink> findById(String factoryName, Long routeLinkId) {
        if (!StringUtils.hasText(factoryName) || routeLinkId == null) {
            return Optional.empty();
        }
        WcsRouteLinkId id = WcsRouteLinkId.builder()
                .factoryName(factoryName)
                .routeLinkId(routeLinkId)
                .build();
        Optional<WcsRouteLinkEntity> entityOpt = wcsRouteLinkJpaRepository.findById(id);
        if (entityOpt.isPresent()) {
            return Optional.ofNullable(wcsRouteLinkMapper.toDomain(entityOpt.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<WcsRouteLink> findByFactoryName(String factoryName) {
        if (!StringUtils.hasText(factoryName)) {
            return new ArrayList<>();
        }
        List<WcsRouteLinkEntity> entities = wcsRouteLinkJpaRepository.findByFactoryName(factoryName);
        List<WcsRouteLink> result = new ArrayList<>();
        for (WcsRouteLinkEntity entity : entities) {
            if (entity != null) {
                result.add(wcsRouteLinkMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public boolean existsById(String factoryName, Long routeLinkId) {
        if (!StringUtils.hasText(factoryName) || routeLinkId == null) {
            return false;
        }
        WcsRouteLinkId id = WcsRouteLinkId.builder()
                .factoryName(factoryName)
                .routeLinkId(routeLinkId)
                .build();
        return wcsRouteLinkJpaRepository.existsById(id);
    }

    @Override
    public WcsRouteLink save(WcsRouteLink routeLink) {
        WcsRouteLinkEntity entity = wcsRouteLinkMapper.toEntity(routeLink);
        WcsRouteLinkEntity savedEntity = wcsRouteLinkJpaRepository.save(entity);
        return wcsRouteLinkMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(String factoryName, Long routeLinkId) {
        if (StringUtils.hasText(factoryName) && routeLinkId != null) {
            WcsRouteLinkId id = WcsRouteLinkId.builder()
                    .factoryName(factoryName)
                    .routeLinkId(routeLinkId)
                    .build();
            wcsRouteLinkJpaRepository.deleteById(id);
        }
    }

    @Override
    public Page<WcsRouteLink> findRouteLinks(WcsRouteLinkSearchCondition condition, Pageable pageable) {
        String factoryName = (condition != null) ? condition.getFactoryName() : null;
        Long routeLinkId = (condition != null) ? condition.getRouteLinkId() : null;
        String description = (condition != null) ? condition.getDescription() : null;
        String fromNodeId = (condition != null) ? condition.getFromNodeId() : null;
        String toNodeId = (condition != null) ? condition.getToNodeId() : null;
        String routeLinkType = (condition != null) ? condition.getRouteLinkType() : null;
        String processType = (condition != null) ? condition.getProcessType() : null;
        String passYn = (condition != null) ? condition.getPassYn() : null;
        String usableYn = (condition != null) ? condition.getUsableYn() : null;
        String useYn = (condition != null) ? condition.getUseYn() : null;
        Integer priority = (condition != null) ? condition.getPriority() : null;

        JPAQuery<WcsRouteLinkEntity> query = queryFactory
                .selectFrom(qRouteLink)
                .where(
                        factoryNameContains(factoryName),
                        routeLinkIdEq(routeLinkId),
                        descriptionContains(description),
                        fromNodeIdContains(fromNodeId),
                        toNodeIdContains(toNodeId),
                        routeLinkTypeEq(routeLinkType),
                        processTypeEq(processType),
                        passYnEq(passYn),
                        usableYnEq(usableYn),
                        useYnEq(useYn),
                        priorityEq(priority)
                );

        query.orderBy(getOrderSpecifiers(pageable != null ? pageable.getSort() : Sort.unsorted()));

        if (pageable != null && pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<WcsRouteLinkEntity> content = query.fetch();
        List<WcsRouteLink> resultList = new ArrayList<>();
        for (WcsRouteLinkEntity entity : content) {
            if (entity != null) {
                resultList.add(wcsRouteLinkMapper.toDomain(entity));
            }
        }

        long total;
        if (pageable != null && pageable.isPaged()) {
            Long count = queryFactory
                    .select(qRouteLink.count())
                    .from(qRouteLink)
                    .where(
                            factoryNameContains(factoryName),
                            routeLinkIdEq(routeLinkId),
                            descriptionContains(description),
                            fromNodeIdContains(fromNodeId),
                            toNodeIdContains(toNodeId),
                            routeLinkTypeEq(routeLinkType),
                            processTypeEq(processType),
                            passYnEq(passYn),
                            usableYnEq(usableYn),
                            useYnEq(useYn),
                            priorityEq(priority)
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
                PathBuilder pathBuilder = new PathBuilder<>(qRouteLink.getType(), qRouteLink.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.ASC, qRouteLink.factoryName));
            orders.add(new OrderSpecifier(Order.ASC, qRouteLink.routeLinkId));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression factoryNameContains(String factoryName) {
        return StringUtils.hasText(factoryName) ? qRouteLink.factoryName.contains(factoryName) : null;
    }

    private BooleanExpression routeLinkIdEq(Long routeLinkId) {
        return routeLinkId != null ? qRouteLink.routeLinkId.eq(routeLinkId) : null;
    }

    private BooleanExpression descriptionContains(String description) {
        return StringUtils.hasText(description) ? qRouteLink.description.contains(description) : null;
    }

    private BooleanExpression fromNodeIdContains(String fromNodeId) {
        return StringUtils.hasText(fromNodeId) ? qRouteLink.fromNodeId.contains(fromNodeId) : null;
    }

    private BooleanExpression toNodeIdContains(String toNodeId) {
        return StringUtils.hasText(toNodeId) ? qRouteLink.toNodeId.contains(toNodeId) : null;
    }

    private BooleanExpression routeLinkTypeEq(String routeLinkType) {
        return StringUtils.hasText(routeLinkType) ? qRouteLink.routeLinkType.equalsIgnoreCase(routeLinkType) : null;
    }

    private BooleanExpression processTypeEq(String processType) {
        return StringUtils.hasText(processType) ? qRouteLink.processType.equalsIgnoreCase(processType) : null;
    }

    private BooleanExpression passYnEq(String passYn) {
        return StringUtils.hasText(passYn) ? qRouteLink.passYn.equalsIgnoreCase(passYn) : null;
    }

    private BooleanExpression usableYnEq(String usableYn) {
        return StringUtils.hasText(usableYn) ? qRouteLink.usableYn.equalsIgnoreCase(usableYn) : null;
    }

    private BooleanExpression useYnEq(String useYn) {
        return StringUtils.hasText(useYn) ? qRouteLink.useYn.equalsIgnoreCase(useYn) : null;
    }

    private BooleanExpression priorityEq(Integer priority) {
        return priority != null ? qRouteLink.priority.eq(priority) : null;
    }
}
