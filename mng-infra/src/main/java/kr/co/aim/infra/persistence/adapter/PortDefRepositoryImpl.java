package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.condition.PortDefSearchCondition;
import kr.co.aim.domain.model.PortDef;
import kr.co.aim.domain.repository.PortDefRepository;
import kr.co.aim.infra.persistence.entity.PortDefEntity;
import kr.co.aim.infra.persistence.mapper.PortDefMapper;
import kr.co.aim.infra.persistence.springdatajpa.PortDefJpaRepository;
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

import static kr.co.aim.infra.persistence.entity.QPortDefEntity.portDefEntity;

/**
 * UserRepository의 JPA 기반 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 JpaRepository에 위임합니다.
 */

@Repository
@RequiredArgsConstructor
public class PortDefRepositoryImpl implements PortDefRepository {
    // Spring Data JPA가 자동으로 구현해주는 JPA 리포지토리. UserEntity를 다룬다.

    private final PortDefJpaRepository portDefJpaRepository;
    private final PortDefMapper portDefMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입

    @Override
    public List<PortDef> findAll() {
        return portDefJpaRepository.findAll().stream().map(portDefMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<PortDef> findById(Long id) {
        return portDefJpaRepository.findById(id).map(portDefMapper::toDomain);
    }

    @Override
    public Optional<PortDef> findByEquipmentNameAndPortName(String equipmentName, String portName) {
        return portDefJpaRepository.findByEquipmentNameAndPortName(equipmentName,portName).map(portDefMapper::toDomain);
    }

    @Override
    public PortDef save(PortDef portDef) {
        PortDefEntity entity = portDefMapper.toEntity(portDef);
        PortDefEntity savedEntity = portDefJpaRepository.save(entity);
        return portDefMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<PortDef> findByLocationId(String locationId) {
        return portDefJpaRepository.findByLocationId(locationId).map(portDefMapper::toDomain);
    }

    @Override
    public Page<PortDef> findPortDefWithConditions(PortDefSearchCondition condition, Pageable pageable) {
        JPAQuery<PortDefEntity> query = queryFactory
                .selectFrom(portDefEntity)
                .where(
                        equipmentNameContains(condition.getEquipmentName()),
                        portNameContains(condition.getPortName()),
                        factoryNameContains(condition.getFactoryName()),
                        portNumberEq(condition.getPortNumber()),
                        descriptionContains(condition.getDescription()),
                        transportModeContains(condition.getTransportMode()),
                        portTypeContains(condition.getPortType()),
                        detailPortTypeContains(condition.getDetailPortType()),
                        portUseTypeContains(condition.getPortUseType()),
                        portRoleTypeContains(condition.getPortRoleType()),
                        workCenterNameContains(condition.getWorkCenterName()),
                        locationIdContains(condition.getLocationId()),
                        connectedEquipmentNameContains(condition.getConnectedEquipmentName()),
                        connectedPortNameContains(condition.getConnectedPortName())
                );

        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<PortDefEntity> content = query.fetch();
        List<PortDef> converted = content.stream().map(portDefMapper::toDomain).collect(Collectors.toList());

        long total;
        if (pageable.isPaged()) {
            Long count = queryFactory
                    .select(portDefEntity.count())
                    .from(portDefEntity)
                    .where(
                            equipmentNameContains(condition.getEquipmentName()),
                            portNameContains(condition.getPortName()),
                            factoryNameContains(condition.getFactoryName()),
                            portNumberEq(condition.getPortNumber()),
                            descriptionContains(condition.getDescription()),
                            transportModeContains(condition.getTransportMode()),
                            portTypeContains(condition.getPortType()),
                            detailPortTypeContains(condition.getDetailPortType()),
                            portUseTypeContains(condition.getPortUseType()),
                            portRoleTypeContains(condition.getPortRoleType()),
                            workCenterNameContains(condition.getWorkCenterName()),
                            locationIdContains(condition.getLocationId()),
                            connectedEquipmentNameContains(condition.getConnectedEquipmentName()),
                            connectedPortNameContains(condition.getConnectedPortName())
                    )
                    .fetchOne();
            total = (count != null) ? count : 0L;
        } else {
            total = content.size();
        }

        return new PageImpl<>(converted, pageable, total);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        portDefJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public Optional<PortDef> findWithLockByEquipmentNameAndPortName(String equipmentName, String portName) {
        return portDefJpaRepository.findWithLockByEquipmentNameAndPortName(equipmentName, portName).map(portDefMapper::toDomain);
    }

    @Override
    public List<PortDef> findByWorkCenterNameAndDetailPortTypeInAndPortTypeIn(String workCenterName, List<String> detailPortTypes, List<String> portTypes) {
        return portDefJpaRepository.findByWorkCenterNameAndDetailPortTypeInAndPortTypeIn(workCenterName,detailPortTypes,portTypes).stream().map(portDefMapper::toDomain).collect(Collectors.toList());
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();
        if (sort.isSorted()) {
            for (Sort.Order order : sort) {
                Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                PathBuilder pathBuilder = new PathBuilder<>(portDefEntity.getType(), portDefEntity.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, portDefEntity.id));
        }
        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression equipmentNameContains(String name) {
        return StringUtils.hasText(name) ? portDefEntity.equipmentName.contains(name) : null;
    }

    private BooleanExpression portNameContains(String name) {
        return StringUtils.hasText(name) ? portDefEntity.portName.contains(name) : null;
    }

    private BooleanExpression factoryNameContains(String factoryName) {
        return StringUtils.hasText(factoryName) ? portDefEntity.factoryName.contains(factoryName) : null;
    }

    private BooleanExpression portNumberEq(Integer portNumber) {
        return portNumber != null ? portDefEntity.portNumber.eq(portNumber) : null;
    }

    private BooleanExpression descriptionContains(String description) {
        return StringUtils.hasText(description) ? portDefEntity.description.contains(description) : null;
    }

    private BooleanExpression transportModeContains(String transportMode) {
        return StringUtils.hasText(transportMode) ? portDefEntity.transportMode.contains(transportMode) : null;
    }

    private BooleanExpression portTypeContains(String portType) {
        return StringUtils.hasText(portType) ? portDefEntity.portType.contains(portType) : null;
    }

    private BooleanExpression detailPortTypeContains(String detailPortType) {
        return StringUtils.hasText(detailPortType) ? portDefEntity.detailPortType.contains(detailPortType) : null;
    }

    private BooleanExpression portUseTypeContains(String portUseType) {
        return StringUtils.hasText(portUseType) ? portDefEntity.portUseType.contains(portUseType) : null;
    }

    private BooleanExpression portRoleTypeContains(String portRoleType) {
        return StringUtils.hasText(portRoleType) ? portDefEntity.portRoleType.contains(portRoleType) : null;
    }

    private BooleanExpression workCenterNameContains(String workCenterName) {
        return StringUtils.hasText(workCenterName) ? portDefEntity.workCenterName.contains(workCenterName) : null;
    }

    private BooleanExpression locationIdContains(String locationId) {
        return StringUtils.hasText(locationId) ? portDefEntity.locationId.contains(locationId) : null;
    }

    private BooleanExpression connectedEquipmentNameContains(String connectedEquipmentName) {
        return StringUtils.hasText(connectedEquipmentName) ? portDefEntity.connectedEquipmentName.contains(connectedEquipmentName) : null;
    }

    private BooleanExpression connectedPortNameContains(String connectedPortName) {
        return StringUtils.hasText(connectedPortName) ? portDefEntity.connectedPortName.contains(connectedPortName) : null;
    }


}
