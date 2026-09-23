package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.condition.WcsCraneSearchCondition;
import kr.co.aim.domain.model.WcsCrane;
import kr.co.aim.domain.repository.WcsCraneRepository;
import kr.co.aim.infra.persistence.entity.QWcsCraneEntity;
import kr.co.aim.infra.persistence.entity.WcsCraneEntity;
import kr.co.aim.infra.persistence.entity.WcsCraneId;
import kr.co.aim.infra.persistence.mapper.WcsCraneMapper;
import kr.co.aim.infra.persistence.springdatajpa.WcsCraneJpaRepository;
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
public class WcsCraneRepositoryImpl implements WcsCraneRepository {

    private final WcsCraneJpaRepository wcsCraneJpaRepository;
    private final WcsCraneMapper wcsCraneMapper;
    private final JPAQueryFactory queryFactory;

    private static final QWcsCraneEntity qCrane = QWcsCraneEntity.wcsCraneEntity;

    @Override
    public List<WcsCrane> findAll() {
        List<WcsCraneEntity> entities = wcsCraneJpaRepository.findAll();
        List<WcsCrane> result = new ArrayList<>();
        for (WcsCraneEntity entity : entities) {
            if (entity != null) {
                result.add(wcsCraneMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public Optional<WcsCrane> findById(String factoryName, String stockerName, String craneName, Integer craneNumber, Integer localNo) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(stockerName) || !StringUtils.hasText(craneName)
                || craneNumber == null || localNo == null) {
            return Optional.empty();
        }
        WcsCraneId id = WcsCraneId.builder()
                .factoryName(factoryName)
                .stockerName(stockerName)
                .craneName(craneName)
                .craneNumber(craneNumber)
                .localNo(localNo)
                .build();
        Optional<WcsCraneEntity> entityOpt = wcsCraneJpaRepository.findById(id);
        if (entityOpt.isPresent()) {
            return Optional.ofNullable(wcsCraneMapper.toDomain(entityOpt.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<WcsCrane> findByFactoryNameAndStockerName(String factoryName, String stockerName) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(stockerName)) {
            return new ArrayList<>();
        }
        List<WcsCraneEntity> entities = wcsCraneJpaRepository.findByFactoryNameAndStockerName(factoryName, stockerName);
        List<WcsCrane> result = new ArrayList<>();
        for (WcsCraneEntity entity : entities) {
            if (entity != null) {
                result.add(wcsCraneMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public boolean existsById(String factoryName, String stockerName, String craneName, Integer craneNumber, Integer localNo) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(stockerName) || !StringUtils.hasText(craneName)
                || craneNumber == null || localNo == null) {
            return false;
        }
        WcsCraneId id = WcsCraneId.builder()
                .factoryName(factoryName)
                .stockerName(stockerName)
                .craneName(craneName)
                .craneNumber(craneNumber)
                .localNo(localNo)
                .build();
        return wcsCraneJpaRepository.existsById(id);
    }

    @Override
    public WcsCrane save(WcsCrane crane) {
        WcsCraneEntity entity = wcsCraneMapper.toEntity(crane);
        WcsCraneEntity savedEntity = wcsCraneJpaRepository.save(entity);
        return wcsCraneMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(String factoryName, String stockerName, String craneName, Integer craneNumber, Integer localNo) {
        if (StringUtils.hasText(factoryName) && StringUtils.hasText(stockerName) && StringUtils.hasText(craneName)
                && craneNumber != null && localNo != null) {
            WcsCraneId id = WcsCraneId.builder()
                    .factoryName(factoryName)
                    .stockerName(stockerName)
                    .craneName(craneName)
                    .craneNumber(craneNumber)
                    .localNo(localNo)
                    .build();
            wcsCraneJpaRepository.deleteById(id);
        }
    }

    @Override
    public Page<WcsCrane> findCranes(WcsCraneSearchCondition condition, Pageable pageable) {
        String factoryName = (condition != null) ? condition.getFactoryName() : null;
        String stockerName = (condition != null) ? condition.getStockerName() : null;
        String craneName = (condition != null) ? condition.getCraneName() : null;
        Integer craneNumber = (condition != null) ? condition.getCraneNumber() : null;
        Integer localNo = (condition != null) ? condition.getLocalNo() : null;
        String status = (condition != null) ? condition.getStatus() : null;
        String autoRunStatus = (condition != null) ? condition.getAutoRunStatus() : null;
        String carrierName = (condition != null) ? condition.getCarrierName() : null;
        String carrierExist = (condition != null) ? condition.getCarrierExist() : null;
        String zoneName = (condition != null) ? condition.getZoneName() : null;
        String forkStatus = (condition != null) ? condition.getForkStatus() : null;
        String forkPreStatus = (condition != null) ? condition.getForkPreStatus() : null;
        String mode = (condition != null) ? condition.getMode() : null;
        String positionType = (condition != null) ? condition.getPositionType() : null;
        String rowPosition = (condition != null) ? condition.getRowPosition() : null;
        String columnPosition = (condition != null) ? condition.getColumnPosition() : null;
        String stagePosition = (condition != null) ? condition.getStagePosition() : null;
        String portNoPosition = (condition != null) ? condition.getPortNoPosition() : null;
        String errorHappen = (condition != null) ? condition.getErrorHappen() : null;
        String jobCompleteState = (condition != null) ? condition.getJobCompleteState() : null;
        Boolean opportunisticEnabled = (condition != null) ? condition.getOpportunisticEnabled() : null;

        JPAQuery<WcsCraneEntity> query = queryFactory
                .selectFrom(qCrane)
                .where(
                        factoryNameContains(factoryName),
                        stockerNameContains(stockerName),
                        craneNameContains(craneName),
                        craneNumberEq(craneNumber),
                        localNoEq(localNo),
                        statusEq(status),
                        autoRunStatusEq(autoRunStatus),
                        carrierNameContains(carrierName),
                        carrierExistEq(carrierExist),
                        zoneNameContains(zoneName),
                        forkStatusEq(forkStatus),
                        forkPreStatusEq(forkPreStatus),
                        modeEq(mode),
                        positionTypeEq(positionType),
                        rowPositionContains(rowPosition),
                        columnPositionContains(columnPosition),
                        stagePositionContains(stagePosition),
                        portNoPositionContains(portNoPosition),
                        errorHappenEq(errorHappen),
                        jobCompleteStateEq(jobCompleteState),
                        opportunisticEnabledEq(opportunisticEnabled)
                );

        query.orderBy(getOrderSpecifiers(pageable != null ? pageable.getSort() : Sort.unsorted()));

        if (pageable != null && pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<WcsCraneEntity> content = query.fetch();
        List<WcsCrane> resultList = new ArrayList<>();
        for (WcsCraneEntity entity : content) {
            if (entity != null) {
                resultList.add(wcsCraneMapper.toDomain(entity));
            }
        }

        long total;
        if (pageable != null && pageable.isPaged()) {
            Long count = queryFactory
                    .select(qCrane.count())
                    .from(qCrane)
                    .where(
                            factoryNameContains(factoryName),
                            stockerNameContains(stockerName),
                            craneNameContains(craneName),
                            craneNumberEq(craneNumber),
                            localNoEq(localNo),
                            statusEq(status),
                            autoRunStatusEq(autoRunStatus),
                            carrierNameContains(carrierName),
                            carrierExistEq(carrierExist),
                            zoneNameContains(zoneName),
                            forkStatusEq(forkStatus),
                            forkPreStatusEq(forkPreStatus),
                            modeEq(mode),
                            positionTypeEq(positionType),
                            rowPositionContains(rowPosition),
                            columnPositionContains(columnPosition),
                            stagePositionContains(stagePosition),
                            portNoPositionContains(portNoPosition),
                            errorHappenEq(errorHappen),
                            jobCompleteStateEq(jobCompleteState),
                            opportunisticEnabledEq(opportunisticEnabled)
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
                PathBuilder pathBuilder = new PathBuilder<>(qCrane.getType(), qCrane.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.ASC, qCrane.factoryName));
            orders.add(new OrderSpecifier(Order.ASC, qCrane.stockerName));
            orders.add(new OrderSpecifier(Order.ASC, qCrane.craneName));
            orders.add(new OrderSpecifier(Order.ASC, qCrane.craneNumber));
            orders.add(new OrderSpecifier(Order.ASC, qCrane.localNo));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression factoryNameContains(String factoryName) {
        return StringUtils.hasText(factoryName) ? qCrane.factoryName.contains(factoryName) : null;
    }

    private BooleanExpression stockerNameContains(String stockerName) {
        return StringUtils.hasText(stockerName) ? qCrane.stockerName.contains(stockerName) : null;
    }

    private BooleanExpression craneNameContains(String craneName) {
        return StringUtils.hasText(craneName) ? qCrane.craneName.contains(craneName) : null;
    }

    private BooleanExpression craneNumberEq(Integer craneNumber) {
        return craneNumber != null ? qCrane.craneNumber.eq(craneNumber) : null;
    }

    private BooleanExpression localNoEq(Integer localNo) {
        return localNo != null ? qCrane.localNo.eq(localNo) : null;
    }

    private BooleanExpression statusEq(String status) {
        return StringUtils.hasText(status) ? qCrane.status.equalsIgnoreCase(status) : null;
    }

    private BooleanExpression autoRunStatusEq(String autoRunStatus) {
        return StringUtils.hasText(autoRunStatus) ? qCrane.autoRunStatus.equalsIgnoreCase(autoRunStatus) : null;
    }

    private BooleanExpression carrierNameContains(String carrierName) {
        return StringUtils.hasText(carrierName) ? qCrane.carrierName.contains(carrierName) : null;
    }

    private BooleanExpression carrierExistEq(String carrierExist) {
        return StringUtils.hasText(carrierExist) ? qCrane.carrierExist.equalsIgnoreCase(carrierExist) : null;
    }

    private BooleanExpression zoneNameContains(String zoneName) {
        return StringUtils.hasText(zoneName) ? qCrane.zoneName.contains(zoneName) : null;
    }

    private BooleanExpression forkStatusEq(String forkStatus) {
        return StringUtils.hasText(forkStatus) ? qCrane.forkStatus.equalsIgnoreCase(forkStatus) : null;
    }

    private BooleanExpression forkPreStatusEq(String forkPreStatus) {
        return StringUtils.hasText(forkPreStatus) ? qCrane.forkPreStatus.equalsIgnoreCase(forkPreStatus) : null;
    }

    private BooleanExpression modeEq(String mode) {
        return StringUtils.hasText(mode) ? qCrane.mode.equalsIgnoreCase(mode) : null;
    }

    private BooleanExpression positionTypeEq(String positionType) {
        return StringUtils.hasText(positionType) ? qCrane.positionType.equalsIgnoreCase(positionType) : null;
    }

    private BooleanExpression rowPositionContains(String rowPosition) {
        return StringUtils.hasText(rowPosition) ? qCrane.rowPosition.contains(rowPosition) : null;
    }

    private BooleanExpression columnPositionContains(String columnPosition) {
        return StringUtils.hasText(columnPosition) ? qCrane.columnPosition.contains(columnPosition) : null;
    }

    private BooleanExpression stagePositionContains(String stagePosition) {
        return StringUtils.hasText(stagePosition) ? qCrane.stagePosition.contains(stagePosition) : null;
    }

    private BooleanExpression portNoPositionContains(String portNoPosition) {
        return StringUtils.hasText(portNoPosition) ? qCrane.portNoPosition.contains(portNoPosition) : null;
    }

    private BooleanExpression errorHappenEq(String errorHappen) {
        return StringUtils.hasText(errorHappen) ? qCrane.errorHappen.equalsIgnoreCase(errorHappen) : null;
    }

    private BooleanExpression jobCompleteStateEq(String jobCompleteState) {
        return StringUtils.hasText(jobCompleteState) ? qCrane.jobCompleteState.equalsIgnoreCase(jobCompleteState) : null;
    }

    private BooleanExpression opportunisticEnabledEq(Boolean opportunisticEnabled) {
        return opportunisticEnabled != null ? qCrane.opportunisticEnabled.eq(opportunisticEnabled) : null;
    }
}
