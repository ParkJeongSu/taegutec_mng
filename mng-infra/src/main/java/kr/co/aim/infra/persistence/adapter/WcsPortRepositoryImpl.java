package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.condition.WcsPortSearchCondition;
import kr.co.aim.domain.model.WcsPort;
import kr.co.aim.domain.repository.WcsPortRepository;
import kr.co.aim.infra.persistence.entity.QWcsPortEntity;
import kr.co.aim.infra.persistence.entity.WcsPortEntity;
import kr.co.aim.infra.persistence.entity.WcsPortId;
import kr.co.aim.infra.persistence.mapper.WcsPortMapper;
import kr.co.aim.infra.persistence.springdatajpa.WcsPortJpaRepository;
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
public class WcsPortRepositoryImpl implements WcsPortRepository {

    private final WcsPortJpaRepository wcsPortJpaRepository;
    private final WcsPortMapper wcsPortMapper;
    private final JPAQueryFactory queryFactory;

    private static final QWcsPortEntity qPort = QWcsPortEntity.wcsPortEntity;

    @Override
    public List<WcsPort> findAll() {
        List<WcsPortEntity> entities = wcsPortJpaRepository.findAll();
        List<WcsPort> result = new ArrayList<>();
        for (WcsPortEntity entity : entities) {
            if (entity != null) {
                result.add(wcsPortMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public Optional<WcsPort> findById(String factoryName, String equipmentName, Integer localNo, Integer portNumber) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(equipmentName)
                || localNo == null || portNumber == null) {
            return Optional.empty();
        }
        WcsPortId id = WcsPortId.builder()
                .factoryName(factoryName)
                .equipmentName(equipmentName)
                .localNo(localNo)
                .portNumber(portNumber)
                .build();
        Optional<WcsPortEntity> entityOpt = wcsPortJpaRepository.findById(id);
        if (entityOpt.isPresent()) {
            return Optional.ofNullable(wcsPortMapper.toDomain(entityOpt.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<WcsPort> findByFactoryNameAndEquipmentName(String factoryName, String equipmentName) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(equipmentName)) {
            return new ArrayList<>();
        }
        List<WcsPortEntity> entities = wcsPortJpaRepository.findByFactoryNameAndEquipmentName(factoryName, equipmentName);
        List<WcsPort> result = new ArrayList<>();
        for (WcsPortEntity entity : entities) {
            if (entity != null) {
                result.add(wcsPortMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public boolean existsById(String factoryName, String equipmentName, Integer localNo, Integer portNumber) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(equipmentName)
                || localNo == null || portNumber == null) {
            return false;
        }
        WcsPortId id = WcsPortId.builder()
                .factoryName(factoryName)
                .equipmentName(equipmentName)
                .localNo(localNo)
                .portNumber(portNumber)
                .build();
        return wcsPortJpaRepository.existsById(id);
    }

    @Override
    public WcsPort save(WcsPort port) {
        WcsPortEntity entity = wcsPortMapper.toEntity(port);
        WcsPortEntity savedEntity = wcsPortJpaRepository.save(entity);
        return wcsPortMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(String factoryName, String equipmentName, Integer localNo, Integer portNumber) {
        if (StringUtils.hasText(factoryName) && StringUtils.hasText(equipmentName)
                && localNo != null && portNumber != null) {
            WcsPortId id = WcsPortId.builder()
                    .factoryName(factoryName)
                    .equipmentName(equipmentName)
                    .localNo(localNo)
                    .portNumber(portNumber)
                    .build();
            wcsPortJpaRepository.deleteById(id);
        }
    }

    @Override
    public Page<WcsPort> findPorts(WcsPortSearchCondition condition, Pageable pageable) {
        String factoryName = (condition != null) ? condition.getFactoryName() : null;
        String equipmentName = (condition != null) ? condition.getEquipmentName() : null;
        Integer localNo = (condition != null) ? condition.getLocalNo() : null;
        Integer portNumber = (condition != null) ? condition.getPortNumber() : null;
        String portName = (condition != null) ? condition.getPortName() : null;
        String portStatus = (condition != null) ? condition.getPortStatus() : null;
        String carrierName = (condition != null) ? condition.getCarrierName() : null;
        String zoneName = (condition != null) ? condition.getZoneName() : null;
        String portType = (condition != null) ? condition.getPortType() : null;
        String portContainStatus = (condition != null) ? condition.getPortContainStatus() : null;
        String portEnableMode = (condition != null) ? condition.getPortEnableMode() : null;
        String portTransferStatus = (condition != null) ? condition.getPortTransferStatus() : null;
        String portTransferMode = (condition != null) ? condition.getPortTransferMode() : null;
        String portDetailType = (condition != null) ? condition.getPortDetailType() : null;
        Boolean useWorkerFlag = (condition != null) ? condition.getUseWorkerFlag() : null;
        String portUseType = (condition != null) ? condition.getPortUseType() : null;
        String portReadingEnableMode = (condition != null) ? condition.getPortReadingEnableMode() : null;
        String linkEquipmentName = (condition != null) ? condition.getLinkEquipmentName() : null;
        String linkPortName = (condition != null) ? condition.getLinkPortName() : null;
        String linkPortType = (condition != null) ? condition.getLinkPortType() : null;
        String errorHappen = (condition != null) ? condition.getErrorHappen() : null;

        JPAQuery<WcsPortEntity> query = queryFactory
                .selectFrom(qPort)
                .where(
                        factoryNameContains(factoryName),
                        equipmentNameContains(equipmentName),
                        localNoEq(localNo),
                        portNumberEq(portNumber),
                        portNameContains(portName),
                        portStatusEq(portStatus),
                        carrierNameContains(carrierName),
                        zoneNameContains(zoneName),
                        portTypeEq(portType),
                        portContainStatusEq(portContainStatus),
                        portEnableModeEq(portEnableMode),
                        portTransferStatusEq(portTransferStatus),
                        portTransferModeEq(portTransferMode),
                        portDetailTypeContains(portDetailType),
                        useWorkerFlagEq(useWorkerFlag),
                        portUseTypeEq(portUseType),
                        portReadingEnableModeEq(portReadingEnableMode),
                        linkEquipmentNameContains(linkEquipmentName),
                        linkPortNameContains(linkPortName),
                        linkPortTypeEq(linkPortType),
                        errorHappenEq(errorHappen)
                );

        query.orderBy(getOrderSpecifiers(pageable != null ? pageable.getSort() : Sort.unsorted()));

        if (pageable != null && pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<WcsPortEntity> content = query.fetch();
        List<WcsPort> resultList = new ArrayList<>();
        for (WcsPortEntity entity : content) {
            if (entity != null) {
                resultList.add(wcsPortMapper.toDomain(entity));
            }
        }

        long total;
        if (pageable != null && pageable.isPaged()) {
            Long count = queryFactory
                    .select(qPort.count())
                    .from(qPort)
                    .where(
                            factoryNameContains(factoryName),
                            equipmentNameContains(equipmentName),
                            localNoEq(localNo),
                            portNumberEq(portNumber),
                            portNameContains(portName),
                            portStatusEq(portStatus),
                            carrierNameContains(carrierName),
                            zoneNameContains(zoneName),
                            portTypeEq(portType),
                            portContainStatusEq(portContainStatus),
                            portEnableModeEq(portEnableMode),
                            portTransferStatusEq(portTransferStatus),
                            portTransferModeEq(portTransferMode),
                            portDetailTypeContains(portDetailType),
                            useWorkerFlagEq(useWorkerFlag),
                            portUseTypeEq(portUseType),
                            portReadingEnableModeEq(portReadingEnableMode),
                            linkEquipmentNameContains(linkEquipmentName),
                            linkPortNameContains(linkPortName),
                            linkPortTypeEq(linkPortType),
                            errorHappenEq(errorHappen)
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
                PathBuilder pathBuilder = new PathBuilder<>(qPort.getType(), qPort.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.ASC, qPort.factoryName));
            orders.add(new OrderSpecifier(Order.ASC, qPort.equipmentName));
            orders.add(new OrderSpecifier(Order.ASC, qPort.localNo));
            orders.add(new OrderSpecifier(Order.ASC, qPort.portNumber));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression factoryNameContains(String factoryName) {
        return StringUtils.hasText(factoryName) ? qPort.factoryName.contains(factoryName) : null;
    }

    private BooleanExpression equipmentNameContains(String equipmentName) {
        return StringUtils.hasText(equipmentName) ? qPort.equipmentName.contains(equipmentName) : null;
    }

    private BooleanExpression localNoEq(Integer localNo) {
        return localNo != null ? qPort.localNo.eq(localNo) : null;
    }

    private BooleanExpression portNumberEq(Integer portNumber) {
        return portNumber != null ? qPort.portNumber.eq(portNumber) : null;
    }

    private BooleanExpression portNameContains(String portName) {
        return StringUtils.hasText(portName) ? qPort.portName.contains(portName) : null;
    }

    private BooleanExpression portStatusEq(String portStatus) {
        return StringUtils.hasText(portStatus) ? qPort.portStatus.equalsIgnoreCase(portStatus) : null;
    }

    private BooleanExpression carrierNameContains(String carrierName) {
        return StringUtils.hasText(carrierName) ? qPort.carrierName.contains(carrierName) : null;
    }

    private BooleanExpression zoneNameContains(String zoneName) {
        return StringUtils.hasText(zoneName) ? qPort.zoneName.contains(zoneName) : null;
    }

    private BooleanExpression portTypeEq(String portType) {
        return StringUtils.hasText(portType) ? qPort.portType.equalsIgnoreCase(portType) : null;
    }

    private BooleanExpression portContainStatusEq(String portContainStatus) {
        return StringUtils.hasText(portContainStatus) ? qPort.portContainStatus.equalsIgnoreCase(portContainStatus) : null;
    }

    private BooleanExpression portEnableModeEq(String portEnableMode) {
        return StringUtils.hasText(portEnableMode) ? qPort.portEnableMode.equalsIgnoreCase(portEnableMode) : null;
    }

    private BooleanExpression portTransferStatusEq(String portTransferStatus) {
        return StringUtils.hasText(portTransferStatus) ? qPort.portTransferStatus.equalsIgnoreCase(portTransferStatus) : null;
    }

    private BooleanExpression portTransferModeEq(String portTransferMode) {
        return StringUtils.hasText(portTransferMode) ? qPort.portTransferMode.equalsIgnoreCase(portTransferMode) : null;
    }

    private BooleanExpression portDetailTypeContains(String portDetailType) {
        return StringUtils.hasText(portDetailType) ? qPort.portDetailType.contains(portDetailType) : null;
    }

    private BooleanExpression useWorkerFlagEq(Boolean useWorkerFlag) {
        return useWorkerFlag != null ? qPort.useWorkerFlag.eq(useWorkerFlag) : null;
    }

    private BooleanExpression portUseTypeEq(String portUseType) {
        return StringUtils.hasText(portUseType) ? qPort.portUseType.equalsIgnoreCase(portUseType) : null;
    }

    private BooleanExpression portReadingEnableModeEq(String portReadingEnableMode) {
        return StringUtils.hasText(portReadingEnableMode) ? qPort.portReadingEnableMode.equalsIgnoreCase(portReadingEnableMode) : null;
    }

    private BooleanExpression linkEquipmentNameContains(String linkEquipmentName) {
        return StringUtils.hasText(linkEquipmentName) ? qPort.linkEquipmentName.contains(linkEquipmentName) : null;
    }

    private BooleanExpression linkPortNameContains(String linkPortName) {
        return StringUtils.hasText(linkPortName) ? qPort.linkPortName.contains(linkPortName) : null;
    }

    private BooleanExpression linkPortTypeEq(String linkPortType) {
        return StringUtils.hasText(linkPortType) ? qPort.linkPortType.equalsIgnoreCase(linkPortType) : null;
    }

    private BooleanExpression errorHappenEq(String errorHappen) {
        return StringUtils.hasText(errorHappen) ? qPort.errorHappen.equalsIgnoreCase(errorHappen) : null;
    }
}
