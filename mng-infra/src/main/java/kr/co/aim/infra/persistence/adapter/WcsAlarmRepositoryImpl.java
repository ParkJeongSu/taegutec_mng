package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.condition.WcsAlarmSearchCondition;
import kr.co.aim.domain.model.WcsAlarm;
import kr.co.aim.domain.repository.WcsAlarmRepository;
import kr.co.aim.infra.persistence.entity.QWcsAlarmEntity;
import kr.co.aim.infra.persistence.entity.WcsAlarmEntity;
import kr.co.aim.infra.persistence.entity.WcsAlarmId;
import kr.co.aim.infra.persistence.mapper.WcsAlarmMapper;
import kr.co.aim.infra.persistence.springdatajpa.WcsAlarmJpaRepository;
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
public class WcsAlarmRepositoryImpl implements WcsAlarmRepository {

    private final WcsAlarmJpaRepository wcsAlarmJpaRepository;
    private final WcsAlarmMapper wcsAlarmMapper;
    private final JPAQueryFactory queryFactory;

    private static final QWcsAlarmEntity qAlarm = QWcsAlarmEntity.wcsAlarmEntity;

    @Override
    public List<WcsAlarm> findAll() {
        List<WcsAlarmEntity> entities = wcsAlarmJpaRepository.findAll();
        List<WcsAlarm> result = new ArrayList<>();
        for (WcsAlarmEntity entity : entities) {
            if (entity != null) {
                result.add(wcsAlarmMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public Optional<WcsAlarm> findById(String factoryName, String equipmentName, String alarmId, Integer layerNumber, String layerType) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(equipmentName) || !StringUtils.hasText(alarmId)
                || layerNumber == null || !StringUtils.hasText(layerType)) {
            return Optional.empty();
        }
        WcsAlarmId id = WcsAlarmId.builder()
                .factoryName(factoryName)
                .equipmentName(equipmentName)
                .alarmId(alarmId)
                .layerNumber(layerNumber)
                .layerType(layerType)
                .build();
        Optional<WcsAlarmEntity> entityOpt = wcsAlarmJpaRepository.findById(id);
        if (entityOpt.isPresent()) {
            return Optional.ofNullable(wcsAlarmMapper.toDomain(entityOpt.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<WcsAlarm> findByFactoryNameAndEquipmentName(String factoryName, String equipmentName) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(equipmentName)) {
            return new ArrayList<>();
        }
        List<WcsAlarmEntity> entities = wcsAlarmJpaRepository.findByFactoryNameAndEquipmentName(factoryName, equipmentName);
        List<WcsAlarm> result = new ArrayList<>();
        for (WcsAlarmEntity entity : entities) {
            if (entity != null) {
                result.add(wcsAlarmMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public boolean existsById(String factoryName, String equipmentName, String alarmId, Integer layerNumber, String layerType) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(equipmentName) || !StringUtils.hasText(alarmId)
                || layerNumber == null || !StringUtils.hasText(layerType)) {
            return false;
        }
        WcsAlarmId id = WcsAlarmId.builder()
                .factoryName(factoryName)
                .equipmentName(equipmentName)
                .alarmId(alarmId)
                .layerNumber(layerNumber)
                .layerType(layerType)
                .build();
        return wcsAlarmJpaRepository.existsById(id);
    }

    @Override
    public WcsAlarm save(WcsAlarm alarm) {
        WcsAlarmEntity entity = wcsAlarmMapper.toEntity(alarm);
        WcsAlarmEntity savedEntity = wcsAlarmJpaRepository.save(entity);
        return wcsAlarmMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(String factoryName, String equipmentName, String alarmId, Integer layerNumber, String layerType) {
        if (StringUtils.hasText(factoryName) && StringUtils.hasText(equipmentName) && StringUtils.hasText(alarmId)
                && layerNumber != null && StringUtils.hasText(layerType)) {
            WcsAlarmId id = WcsAlarmId.builder()
                    .factoryName(factoryName)
                    .equipmentName(equipmentName)
                    .alarmId(alarmId)
                    .layerNumber(layerNumber)
                    .layerType(layerType)
                    .build();
            wcsAlarmJpaRepository.deleteById(id);
        }
    }

    @Override
    public Page<WcsAlarm> findAlarms(WcsAlarmSearchCondition condition, Pageable pageable) {
        String factoryName = (condition != null) ? condition.getFactoryName() : null;
        String equipmentName = (condition != null) ? condition.getEquipmentName() : null;
        String alarmId = (condition != null) ? condition.getAlarmId() : null;
        Integer layerNumber = (condition != null) ? condition.getLayerNumber() : null;
        String layerType = (condition != null) ? condition.getLayerType() : null;
        String alarmLevel = (condition != null) ? condition.getAlarmLevel() : null;
        String alarmRecoveryOptions = (condition != null) ? condition.getAlarmRecoveryOptions() : null;
        String alarmText = (condition != null) ? condition.getAlarmText() : null;
        String layerName = (condition != null) ? condition.getLayerName() : null;

        JPAQuery<WcsAlarmEntity> query = queryFactory
                .selectFrom(qAlarm)
                .where(
                        factoryNameContains(factoryName),
                        equipmentNameContains(equipmentName),
                        alarmIdContains(alarmId),
                        layerNumberEq(layerNumber),
                        layerTypeEq(layerType),
                        alarmLevelEq(alarmLevel),
                        alarmRecoveryOptionsContains(alarmRecoveryOptions),
                        alarmTextContains(alarmText),
                        layerNameContains(layerName)
                );

        query.orderBy(getOrderSpecifiers(pageable != null ? pageable.getSort() : Sort.unsorted()));

        if (pageable != null && pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<WcsAlarmEntity> content = query.fetch();
        List<WcsAlarm> resultList = new ArrayList<>();
        for (WcsAlarmEntity entity : content) {
            if (entity != null) {
                resultList.add(wcsAlarmMapper.toDomain(entity));
            }
        }

        long total;
        if (pageable != null && pageable.isPaged()) {
            Long count = queryFactory
                    .select(qAlarm.count())
                    .from(qAlarm)
                    .where(
                            factoryNameContains(factoryName),
                            equipmentNameContains(equipmentName),
                            alarmIdContains(alarmId),
                            layerNumberEq(layerNumber),
                            layerTypeEq(layerType),
                            alarmLevelEq(alarmLevel),
                            alarmRecoveryOptionsContains(alarmRecoveryOptions),
                            alarmTextContains(alarmText),
                            layerNameContains(layerName)
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
                PathBuilder pathBuilder = new PathBuilder<>(qAlarm.getType(), qAlarm.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.ASC, qAlarm.factoryName));
            orders.add(new OrderSpecifier(Order.ASC, qAlarm.equipmentName));
            orders.add(new OrderSpecifier(Order.ASC, qAlarm.alarmId));
            orders.add(new OrderSpecifier(Order.ASC, qAlarm.layerNumber));
            orders.add(new OrderSpecifier(Order.ASC, qAlarm.layerType));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression factoryNameContains(String factoryName) {
        return StringUtils.hasText(factoryName) ? qAlarm.factoryName.contains(factoryName) : null;
    }

    private BooleanExpression equipmentNameContains(String equipmentName) {
        return StringUtils.hasText(equipmentName) ? qAlarm.equipmentName.contains(equipmentName) : null;
    }

    private BooleanExpression alarmIdContains(String alarmId) {
        return StringUtils.hasText(alarmId) ? qAlarm.alarmId.contains(alarmId) : null;
    }

    private BooleanExpression layerNumberEq(Integer layerNumber) {
        return layerNumber != null ? qAlarm.layerNumber.eq(layerNumber) : null;
    }

    private BooleanExpression layerTypeEq(String layerType) {
        return StringUtils.hasText(layerType) ? qAlarm.layerType.equalsIgnoreCase(layerType) : null;
    }

    private BooleanExpression alarmLevelEq(String alarmLevel) {
        return StringUtils.hasText(alarmLevel) ? qAlarm.alarmLevel.equalsIgnoreCase(alarmLevel) : null;
    }

    private BooleanExpression alarmRecoveryOptionsContains(String alarmRecoveryOptions) {
        return StringUtils.hasText(alarmRecoveryOptions) ? qAlarm.alarmRecoveryOptions.contains(alarmRecoveryOptions) : null;
    }

    private BooleanExpression alarmTextContains(String alarmText) {
        return StringUtils.hasText(alarmText) ? qAlarm.alarmText.contains(alarmText) : null;
    }

    private BooleanExpression layerNameContains(String layerName) {
        return StringUtils.hasText(layerName) ? qAlarm.layerName.contains(layerName) : null;
    }
}
