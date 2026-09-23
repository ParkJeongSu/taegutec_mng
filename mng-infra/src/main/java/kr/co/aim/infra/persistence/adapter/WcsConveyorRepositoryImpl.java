package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.condition.WcsConveyorSearchCondition;
import kr.co.aim.domain.model.WcsConveyor;
import kr.co.aim.domain.repository.WcsConveyorRepository;
import kr.co.aim.infra.persistence.entity.QWcsConveyorEntity;
import kr.co.aim.infra.persistence.entity.WcsConveyorEntity;
import kr.co.aim.infra.persistence.entity.WcsConveyorId;
import kr.co.aim.infra.persistence.mapper.WcsConveyorMapper;
import kr.co.aim.infra.persistence.springdatajpa.WcsConveyorJpaRepository;
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
public class WcsConveyorRepositoryImpl implements WcsConveyorRepository {

    private final WcsConveyorJpaRepository wcsConveyorJpaRepository;
    private final WcsConveyorMapper wcsConveyorMapper;
    private final JPAQueryFactory queryFactory;

    private static final QWcsConveyorEntity qConveyor = QWcsConveyorEntity.wcsConveyorEntity;

    @Override
    public List<WcsConveyor> findAll() {
        List<WcsConveyorEntity> entities = wcsConveyorJpaRepository.findAll();
        List<WcsConveyor> result = new ArrayList<>();
        for (WcsConveyorEntity entity : entities) {
            if (entity != null) {
                result.add(wcsConveyorMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public Optional<WcsConveyor> findById(String factoryName, String conveyorGroup, String conveyorName, Integer conveyorNumber, Integer localNo) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(conveyorGroup) || !StringUtils.hasText(conveyorName)
                || conveyorNumber == null || localNo == null) {
            return Optional.empty();
        }
        WcsConveyorId id = WcsConveyorId.builder()
                .factoryName(factoryName)
                .conveyorGroup(conveyorGroup)
                .conveyorName(conveyorName)
                .conveyorNumber(conveyorNumber)
                .localNo(localNo)
                .build();
        Optional<WcsConveyorEntity> entityOpt = wcsConveyorJpaRepository.findById(id);
        if (entityOpt.isPresent()) {
            return Optional.ofNullable(wcsConveyorMapper.toDomain(entityOpt.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<WcsConveyor> findByFactoryNameAndConveyorGroup(String factoryName, String conveyorGroup) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(conveyorGroup)) {
            return new ArrayList<>();
        }
        List<WcsConveyorEntity> entities = wcsConveyorJpaRepository.findByFactoryNameAndConveyorGroup(factoryName, conveyorGroup);
        List<WcsConveyor> result = new ArrayList<>();
        for (WcsConveyorEntity entity : entities) {
            if (entity != null) {
                result.add(wcsConveyorMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public boolean existsById(String factoryName, String conveyorGroup, String conveyorName, Integer conveyorNumber, Integer localNo) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(conveyorGroup) || !StringUtils.hasText(conveyorName)
                || conveyorNumber == null || localNo == null) {
            return false;
        }
        WcsConveyorId id = WcsConveyorId.builder()
                .factoryName(factoryName)
                .conveyorGroup(conveyorGroup)
                .conveyorName(conveyorName)
                .conveyorNumber(conveyorNumber)
                .localNo(localNo)
                .build();
        return wcsConveyorJpaRepository.existsById(id);
    }

    @Override
    public WcsConveyor save(WcsConveyor conveyor) {
        WcsConveyorEntity entity = wcsConveyorMapper.toEntity(conveyor);
        WcsConveyorEntity savedEntity = wcsConveyorJpaRepository.save(entity);
        return wcsConveyorMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(String factoryName, String conveyorGroup, String conveyorName, Integer conveyorNumber, Integer localNo) {
        if (StringUtils.hasText(factoryName) && StringUtils.hasText(conveyorGroup) && StringUtils.hasText(conveyorName)
                && conveyorNumber != null && localNo != null) {
            WcsConveyorId id = WcsConveyorId.builder()
                    .factoryName(factoryName)
                    .conveyorGroup(conveyorGroup)
                    .conveyorName(conveyorName)
                    .conveyorNumber(conveyorNumber)
                    .localNo(localNo)
                    .build();
            wcsConveyorJpaRepository.deleteById(id);
        }
    }

    @Override
    public Page<WcsConveyor> findConveyors(WcsConveyorSearchCondition condition, Pageable pageable) {
        String factoryName = (condition != null) ? condition.getFactoryName() : null;
        String conveyorGroup = (condition != null) ? condition.getConveyorGroup() : null;
        String conveyorName = (condition != null) ? condition.getConveyorName() : null;
        Integer conveyorNumber = (condition != null) ? condition.getConveyorNumber() : null;
        Integer localNo = (condition != null) ? condition.getLocalNo() : null;
        String conveyorType = (condition != null) ? condition.getConveyorType() : null;
        String status = (condition != null) ? condition.getStatus() : null;
        String autoRunStatus = (condition != null) ? condition.getAutoRunStatus() : null;
        String carrierName = (condition != null) ? condition.getCarrierName() : null;
        String carrierExist = (condition != null) ? condition.getCarrierExist() : null;
        String areaName = (condition != null) ? condition.getAreaName() : null;
        String direction = (condition != null) ? condition.getDirection() : null;
        String operationMode = (condition != null) ? condition.getOperationMode() : null;
        String machineTypeName = (condition != null) ? condition.getMachineTypeName() : null;
        String serverName = (condition != null) ? condition.getServerName() : null;
        String mode = (condition != null) ? condition.getMode() : null;
        String onlineControlStatus = (condition != null) ? condition.getOnlineControlStatus() : null;
        String conveyorConnectionStatus = (condition != null) ? condition.getConveyorConnectionStatus() : null;

        JPAQuery<WcsConveyorEntity> query = queryFactory
                .selectFrom(qConveyor)
                .where(
                        factoryNameContains(factoryName),
                        conveyorGroupContains(conveyorGroup),
                        conveyorNameContains(conveyorName),
                        conveyorNumberEq(conveyorNumber),
                        localNoEq(localNo),
                        conveyorTypeContains(conveyorType),
                        statusEq(status),
                        autoRunStatusEq(autoRunStatus),
                        carrierNameContains(carrierName),
                        carrierExistEq(carrierExist),
                        areaNameContains(areaName),
                        directionEq(direction),
                        operationModeEq(operationMode),
                        machineTypeNameContains(machineTypeName),
                        serverNameContains(serverName),
                        modeEq(mode),
                        onlineControlStatusEq(onlineControlStatus),
                        conveyorConnectionStatusEq(conveyorConnectionStatus)
                );

        query.orderBy(getOrderSpecifiers(pageable != null ? pageable.getSort() : Sort.unsorted()));

        if (pageable != null && pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<WcsConveyorEntity> content = query.fetch();
        List<WcsConveyor> resultList = new ArrayList<>();
        for (WcsConveyorEntity entity : content) {
            if (entity != null) {
                resultList.add(wcsConveyorMapper.toDomain(entity));
            }
        }

        long total;
        if (pageable != null && pageable.isPaged()) {
            Long count = queryFactory
                    .select(qConveyor.count())
                    .from(qConveyor)
                    .where(
                            factoryNameContains(factoryName),
                            conveyorGroupContains(conveyorGroup),
                            conveyorNameContains(conveyorName),
                            conveyorNumberEq(conveyorNumber),
                            localNoEq(localNo),
                            conveyorTypeContains(conveyorType),
                            statusEq(status),
                            autoRunStatusEq(autoRunStatus),
                            carrierNameContains(carrierName),
                            carrierExistEq(carrierExist),
                            areaNameContains(areaName),
                            directionEq(direction),
                            operationModeEq(operationMode),
                            machineTypeNameContains(machineTypeName),
                            serverNameContains(serverName),
                            modeEq(mode),
                            onlineControlStatusEq(onlineControlStatus),
                            conveyorConnectionStatusEq(conveyorConnectionStatus)
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
                PathBuilder pathBuilder = new PathBuilder<>(qConveyor.getType(), qConveyor.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.ASC, qConveyor.factoryName));
            orders.add(new OrderSpecifier(Order.ASC, qConveyor.conveyorGroup));
            orders.add(new OrderSpecifier(Order.ASC, qConveyor.conveyorName));
            orders.add(new OrderSpecifier(Order.ASC, qConveyor.conveyorNumber));
            orders.add(new OrderSpecifier(Order.ASC, qConveyor.localNo));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression factoryNameContains(String factoryName) {
        return StringUtils.hasText(factoryName) ? qConveyor.factoryName.contains(factoryName) : null;
    }

    private BooleanExpression conveyorGroupContains(String conveyorGroup) {
        return StringUtils.hasText(conveyorGroup) ? qConveyor.conveyorGroup.contains(conveyorGroup) : null;
    }

    private BooleanExpression conveyorNameContains(String conveyorName) {
        return StringUtils.hasText(conveyorName) ? qConveyor.conveyorName.contains(conveyorName) : null;
    }

    private BooleanExpression conveyorNumberEq(Integer conveyorNumber) {
        return conveyorNumber != null ? qConveyor.conveyorNumber.eq(conveyorNumber) : null;
    }

    private BooleanExpression localNoEq(Integer localNo) {
        return localNo != null ? qConveyor.localNo.eq(localNo) : null;
    }

    private BooleanExpression conveyorTypeContains(String conveyorType) {
        return StringUtils.hasText(conveyorType) ? qConveyor.conveyorType.contains(conveyorType) : null;
    }

    private BooleanExpression statusEq(String status) {
        return StringUtils.hasText(status) ? qConveyor.status.equalsIgnoreCase(status) : null;
    }

    private BooleanExpression autoRunStatusEq(String autoRunStatus) {
        return StringUtils.hasText(autoRunStatus) ? qConveyor.autoRunStatus.equalsIgnoreCase(autoRunStatus) : null;
    }

    private BooleanExpression carrierNameContains(String carrierName) {
        return StringUtils.hasText(carrierName) ? qConveyor.carrierName.contains(carrierName) : null;
    }

    private BooleanExpression carrierExistEq(String carrierExist) {
        return StringUtils.hasText(carrierExist) ? qConveyor.carrierExist.equalsIgnoreCase(carrierExist) : null;
    }

    private BooleanExpression areaNameContains(String areaName) {
        return StringUtils.hasText(areaName) ? qConveyor.areaName.contains(areaName) : null;
    }

    private BooleanExpression directionEq(String direction) {
        return StringUtils.hasText(direction) ? qConveyor.direction.equalsIgnoreCase(direction) : null;
    }

    private BooleanExpression operationModeEq(String operationMode) {
        return StringUtils.hasText(operationMode) ? qConveyor.operationMode.equalsIgnoreCase(operationMode) : null;
    }

    private BooleanExpression machineTypeNameContains(String machineTypeName) {
        return StringUtils.hasText(machineTypeName) ? qConveyor.machineTypeName.contains(machineTypeName) : null;
    }

    private BooleanExpression serverNameContains(String serverName) {
        return StringUtils.hasText(serverName) ? qConveyor.serverName.contains(serverName) : null;
    }

    private BooleanExpression modeEq(String mode) {
        return StringUtils.hasText(mode) ? qConveyor.mode.equalsIgnoreCase(mode) : null;
    }

    private BooleanExpression onlineControlStatusEq(String onlineControlStatus) {
        return StringUtils.hasText(onlineControlStatus) ? qConveyor.onlineControlStatus.equalsIgnoreCase(onlineControlStatus) : null;
    }

    private BooleanExpression conveyorConnectionStatusEq(String conveyorConnectionStatus) {
        return StringUtils.hasText(conveyorConnectionStatus) ? qConveyor.conveyorConnectionStatus.equalsIgnoreCase(conveyorConnectionStatus) : null;
    }
}
