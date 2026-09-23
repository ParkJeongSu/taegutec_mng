package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.condition.WcsRouteNodeSearchCondition;
import kr.co.aim.domain.model.WcsRouteNode;
import kr.co.aim.domain.repository.WcsRouteNodeRepository;
import kr.co.aim.infra.persistence.entity.QWcsRouteNodeEntity;
import kr.co.aim.infra.persistence.entity.WcsRouteNodeEntity;
import kr.co.aim.infra.persistence.entity.WcsRouteNodeId;
import kr.co.aim.infra.persistence.mapper.WcsRouteNodeMapper;
import kr.co.aim.infra.persistence.springdatajpa.WcsRouteNodeJpaRepository;
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
public class WcsRouteNodeRepositoryImpl implements WcsRouteNodeRepository {

    private final WcsRouteNodeJpaRepository wcsRouteNodeJpaRepository;
    private final WcsRouteNodeMapper wcsRouteNodeMapper;
    private final JPAQueryFactory queryFactory;

    private static final QWcsRouteNodeEntity qRouteNode = QWcsRouteNodeEntity.wcsRouteNodeEntity;

    @Override
    public List<WcsRouteNode> findAll() {
        List<WcsRouteNodeEntity> entities = wcsRouteNodeJpaRepository.findAll();
        List<WcsRouteNode> result = new ArrayList<>();
        for (WcsRouteNodeEntity entity : entities) {
            if (entity != null) {
                result.add(wcsRouteNodeMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public Optional<WcsRouteNode> findById(String factoryName, Long routeNodeId) {
        if (!StringUtils.hasText(factoryName) || routeNodeId == null) {
            return Optional.empty();
        }
        WcsRouteNodeId id = WcsRouteNodeId.builder()
                .factoryName(factoryName)
                .routeNodeId(routeNodeId)
                .build();
        Optional<WcsRouteNodeEntity> entityOpt = wcsRouteNodeJpaRepository.findById(id);
        if (entityOpt.isPresent()) {
            return Optional.ofNullable(wcsRouteNodeMapper.toDomain(entityOpt.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<WcsRouteNode> findByFactoryName(String factoryName) {
        if (!StringUtils.hasText(factoryName)) {
            return new ArrayList<>();
        }
        List<WcsRouteNodeEntity> entities = wcsRouteNodeJpaRepository.findByFactoryName(factoryName);
        List<WcsRouteNode> result = new ArrayList<>();
        for (WcsRouteNodeEntity entity : entities) {
            if (entity != null) {
                result.add(wcsRouteNodeMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public List<WcsRouteNode> findByFactoryNameAndNodeId(String factoryName, String nodeId) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(nodeId)) {
            return new ArrayList<>();
        }
        List<WcsRouteNodeEntity> entities = wcsRouteNodeJpaRepository.findByFactoryNameAndNodeId(factoryName, nodeId);
        List<WcsRouteNode> result = new ArrayList<>();
        for (WcsRouteNodeEntity entity : entities) {
            if (entity != null) {
                result.add(wcsRouteNodeMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public boolean existsById(String factoryName, Long routeNodeId) {
        if (!StringUtils.hasText(factoryName) || routeNodeId == null) {
            return false;
        }
        WcsRouteNodeId id = WcsRouteNodeId.builder()
                .factoryName(factoryName)
                .routeNodeId(routeNodeId)
                .build();
        return wcsRouteNodeJpaRepository.existsById(id);
    }

    @Override
    public WcsRouteNode save(WcsRouteNode routeNode) {
        WcsRouteNodeEntity entity = wcsRouteNodeMapper.toEntity(routeNode);
        WcsRouteNodeEntity savedEntity = wcsRouteNodeJpaRepository.save(entity);
        return wcsRouteNodeMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(String factoryName, Long routeNodeId) {
        if (StringUtils.hasText(factoryName) && routeNodeId != null) {
            WcsRouteNodeId id = WcsRouteNodeId.builder()
                    .factoryName(factoryName)
                    .routeNodeId(routeNodeId)
                    .build();
            wcsRouteNodeJpaRepository.deleteById(id);
        }
    }

    @Override
    public Page<WcsRouteNode> findRouteNodes(WcsRouteNodeSearchCondition condition, Pageable pageable) {
        String factoryName = (condition != null) ? condition.getFactoryName() : null;
        Long routeNodeId = (condition != null) ? condition.getRouteNodeId() : null;
        String nodeId = (condition != null) ? condition.getNodeId() : null;
        String nodeName = (condition != null) ? condition.getNodeName() : null;
        String equipmentId = (condition != null) ? condition.getEquipmentId() : null;
        String unitId = (condition != null) ? condition.getUnitId() : null;
        String bayId = (condition != null) ? condition.getBayId() : null;
        String craneId = (condition != null) ? condition.getCraneId() : null;
        String routeNodeType = (condition != null) ? condition.getRouteNodeType() : null;
        String nodeEquipmentType = (condition != null) ? condition.getNodeEquipmentType() : null;
        String controllerType = (condition != null) ? condition.getControllerType() : null;
        String rerouteType = (condition != null) ? condition.getRerouteType() : null;
        String useYn = (condition != null) ? condition.getUseYn() : null;
        Integer nodeSeq = (condition != null) ? condition.getNodeSeq() : null;

        JPAQuery<WcsRouteNodeEntity> query = queryFactory
                .selectFrom(qRouteNode)
                .where(
                        factoryNameContains(factoryName),
                        routeNodeIdEq(routeNodeId),
                        nodeIdContains(nodeId),
                        nodeNameContains(nodeName),
                        equipmentIdContains(equipmentId),
                        unitIdContains(unitId),
                        bayIdContains(bayId),
                        craneIdContains(craneId),
                        routeNodeTypeEq(routeNodeType),
                        nodeEquipmentTypeEq(nodeEquipmentType),
                        controllerTypeEq(controllerType),
                        rerouteTypeEq(rerouteType),
                        useYnEq(useYn),
                        nodeSeqEq(nodeSeq)
                );

        query.orderBy(getOrderSpecifiers(pageable != null ? pageable.getSort() : Sort.unsorted()));

        if (pageable != null && pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<WcsRouteNodeEntity> content = query.fetch();
        List<WcsRouteNode> resultList = new ArrayList<>();
        for (WcsRouteNodeEntity entity : content) {
            if (entity != null) {
                resultList.add(wcsRouteNodeMapper.toDomain(entity));
            }
        }

        long total;
        if (pageable != null && pageable.isPaged()) {
            Long count = queryFactory
                    .select(qRouteNode.count())
                    .from(qRouteNode)
                    .where(
                            factoryNameContains(factoryName),
                            routeNodeIdEq(routeNodeId),
                            nodeIdContains(nodeId),
                            nodeNameContains(nodeName),
                            equipmentIdContains(equipmentId),
                            unitIdContains(unitId),
                            bayIdContains(bayId),
                            craneIdContains(craneId),
                            routeNodeTypeEq(routeNodeType),
                            nodeEquipmentTypeEq(nodeEquipmentType),
                            controllerTypeEq(controllerType),
                            rerouteTypeEq(rerouteType),
                            useYnEq(useYn),
                            nodeSeqEq(nodeSeq)
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
                PathBuilder pathBuilder = new PathBuilder<>(qRouteNode.getType(), qRouteNode.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.ASC, qRouteNode.factoryName));
            orders.add(new OrderSpecifier(Order.ASC, qRouteNode.routeNodeId));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression factoryNameContains(String factoryName) {
        return StringUtils.hasText(factoryName) ? qRouteNode.factoryName.contains(factoryName) : null;
    }

    private BooleanExpression routeNodeIdEq(Long routeNodeId) {
        return routeNodeId != null ? qRouteNode.routeNodeId.eq(routeNodeId) : null;
    }

    private BooleanExpression nodeIdContains(String nodeId) {
        return StringUtils.hasText(nodeId) ? qRouteNode.nodeId.contains(nodeId) : null;
    }

    private BooleanExpression nodeNameContains(String nodeName) {
        return StringUtils.hasText(nodeName) ? qRouteNode.nodeName.contains(nodeName) : null;
    }

    private BooleanExpression equipmentIdContains(String equipmentId) {
        return StringUtils.hasText(equipmentId) ? qRouteNode.equipmentId.contains(equipmentId) : null;
    }

    private BooleanExpression unitIdContains(String unitId) {
        return StringUtils.hasText(unitId) ? qRouteNode.unitId.contains(unitId) : null;
    }

    private BooleanExpression bayIdContains(String bayId) {
        return StringUtils.hasText(bayId) ? qRouteNode.bayId.contains(bayId) : null;
    }

    private BooleanExpression craneIdContains(String craneId) {
        return StringUtils.hasText(craneId) ? qRouteNode.craneId.contains(craneId) : null;
    }

    private BooleanExpression routeNodeTypeEq(String routeNodeType) {
        return StringUtils.hasText(routeNodeType) ? qRouteNode.routeNodeType.equalsIgnoreCase(routeNodeType) : null;
    }

    private BooleanExpression nodeEquipmentTypeEq(String nodeEquipmentType) {
        return StringUtils.hasText(nodeEquipmentType) ? qRouteNode.nodeEquipmentType.equalsIgnoreCase(nodeEquipmentType) : null;
    }

    private BooleanExpression controllerTypeEq(String controllerType) {
        return StringUtils.hasText(controllerType) ? qRouteNode.controllerType.equalsIgnoreCase(controllerType) : null;
    }

    private BooleanExpression rerouteTypeEq(String rerouteType) {
        return StringUtils.hasText(rerouteType) ? qRouteNode.rerouteType.equalsIgnoreCase(rerouteType) : null;
    }

    private BooleanExpression useYnEq(String useYn) {
        return StringUtils.hasText(useYn) ? qRouteNode.useYn.equalsIgnoreCase(useYn) : null;
    }

    private BooleanExpression nodeSeqEq(Integer nodeSeq) {
        return nodeSeq != null ? qRouteNode.nodeSeq.eq(nodeSeq) : null;
    }
}
