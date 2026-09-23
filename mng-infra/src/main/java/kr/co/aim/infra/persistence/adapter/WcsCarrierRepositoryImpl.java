package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.condition.WcsCarrierSearchCondition;
import kr.co.aim.domain.model.WcsCarrier;
import kr.co.aim.domain.repository.WcsCarrierRepository;
import kr.co.aim.infra.persistence.entity.QWcsCarrierEntity;
import kr.co.aim.infra.persistence.entity.WcsCarrierEntity;
import kr.co.aim.infra.persistence.entity.WcsCarrierId;
import kr.co.aim.infra.persistence.mapper.WcsCarrierMapper;
import kr.co.aim.infra.persistence.springdatajpa.WcsCarrierJpaRepository;
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
public class WcsCarrierRepositoryImpl implements WcsCarrierRepository {

    private final WcsCarrierJpaRepository wcsCarrierJpaRepository;
    private final WcsCarrierMapper wcsCarrierMapper;
    private final JPAQueryFactory queryFactory;

    private static final QWcsCarrierEntity qCarrier = QWcsCarrierEntity.wcsCarrierEntity;

    @Override
    public List<WcsCarrier> findAll() {
        List<WcsCarrierEntity> entities = wcsCarrierJpaRepository.findAll();
        List<WcsCarrier> result = new ArrayList<>();
        for (WcsCarrierEntity entity : entities) {
            if (entity != null) {
                result.add(wcsCarrierMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public Optional<WcsCarrier> findById(String carrierName, String factoryName) {
        if (!StringUtils.hasText(carrierName) || !StringUtils.hasText(factoryName)) {
            return Optional.empty();
        }
        Optional<WcsCarrierEntity> entityOpt = wcsCarrierJpaRepository.findById(new WcsCarrierId(carrierName, factoryName));
        if (entityOpt.isPresent()) {
            return Optional.ofNullable(wcsCarrierMapper.toDomain(entityOpt.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<WcsCarrier> findByFactoryName(String factoryName) {
        if (!StringUtils.hasText(factoryName)) {
            return new ArrayList<>();
        }
        List<WcsCarrierEntity> entities = wcsCarrierJpaRepository.findByFactoryName(factoryName);
        List<WcsCarrier> result = new ArrayList<>();
        for (WcsCarrierEntity entity : entities) {
            if (entity != null) {
                result.add(wcsCarrierMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public boolean existsById(String carrierName, String factoryName) {
        if (!StringUtils.hasText(carrierName) || !StringUtils.hasText(factoryName)) {
            return false;
        }
        return wcsCarrierJpaRepository.existsById(new WcsCarrierId(carrierName, factoryName));
    }

    @Override
    public WcsCarrier save(WcsCarrier carrier) {
        WcsCarrierEntity entity = wcsCarrierMapper.toEntity(carrier);
        WcsCarrierEntity savedEntity = wcsCarrierJpaRepository.save(entity);
        return wcsCarrierMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(String carrierName, String factoryName) {
        if (StringUtils.hasText(carrierName) && StringUtils.hasText(factoryName)) {
            wcsCarrierJpaRepository.deleteById(new WcsCarrierId(carrierName, factoryName));
        }
    }

    @Override
    public Page<WcsCarrier> findCarriers(WcsCarrierSearchCondition condition, Pageable pageable) {
        String factoryName = (condition != null) ? condition.getFactoryName() : null;
        String carrierName = (condition != null) ? condition.getCarrierName() : null;
        String carrierStatus = (condition != null) ? condition.getCarrierStatus() : null;
        String carrierType = (condition != null) ? condition.getCarrierType() : null;
        String carrierDetailType = (condition != null) ? condition.getCarrierDetailType() : null;
        String carrierGroup = (condition != null) ? condition.getCarrierGroup() : null;
        String currentPositionName = (condition != null) ? condition.getCurrentPositionName() : null;
        String currentEquipmentName = (condition != null) ? condition.getCurrentEquipmentName() : null;
        String zoneName = (condition != null) ? condition.getZoneName() : null;
        String lotName = (condition != null) ? condition.getLotName() : null;
        String orderId = (condition != null) ? condition.getOrderId() : null;
        String itemName = (condition != null) ? condition.getItemName() : null;
        String productionType = (condition != null) ? condition.getProductionType() : null;

        JPAQuery<WcsCarrierEntity> query = queryFactory
                .selectFrom(qCarrier)
                .where(
                        factoryNameContains(factoryName),
                        carrierNameContains(carrierName),
                        carrierStatusEq(carrierStatus),
                        carrierTypeContains(carrierType),
                        carrierDetailTypeContains(carrierDetailType),
                        carrierGroupContains(carrierGroup),
                        currentPositionNameContains(currentPositionName),
                        currentEquipmentNameContains(currentEquipmentName),
                        zoneNameContains(zoneName),
                        lotNameContains(lotName),
                        orderIdContains(orderId),
                        itemNameContains(itemName),
                        productionTypeContains(productionType)
                );

        query.orderBy(getOrderSpecifiers(pageable != null ? pageable.getSort() : Sort.unsorted()));

        if (pageable != null && pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<WcsCarrierEntity> content = query.fetch();
        List<WcsCarrier> resultList = new ArrayList<>();
        for (WcsCarrierEntity entity : content) {
            if (entity != null) {
                resultList.add(wcsCarrierMapper.toDomain(entity));
            }
        }

        long total;
        if (pageable != null && pageable.isPaged()) {
            Long count = queryFactory
                    .select(qCarrier.count())
                    .from(qCarrier)
                    .where(
                            factoryNameContains(factoryName),
                            carrierNameContains(carrierName),
                            carrierStatusEq(carrierStatus),
                            carrierTypeContains(carrierType),
                            carrierDetailTypeContains(carrierDetailType),
                            carrierGroupContains(carrierGroup),
                            currentPositionNameContains(currentPositionName),
                            currentEquipmentNameContains(currentEquipmentName),
                            zoneNameContains(zoneName),
                            lotNameContains(lotName),
                            orderIdContains(orderId),
                            itemNameContains(itemName),
                            productionTypeContains(productionType)
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
                PathBuilder pathBuilder = new PathBuilder<>(qCarrier.getType(), qCarrier.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.ASC, qCarrier.factoryName));
            orders.add(new OrderSpecifier(Order.ASC, qCarrier.carrierName));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression factoryNameContains(String factoryName) {
        return StringUtils.hasText(factoryName) ? qCarrier.factoryName.contains(factoryName) : null;
    }

    private BooleanExpression carrierNameContains(String carrierName) {
        return StringUtils.hasText(carrierName) ? qCarrier.carrierName.contains(carrierName) : null;
    }

    private BooleanExpression carrierStatusEq(String carrierStatus) {
        return StringUtils.hasText(carrierStatus) ? qCarrier.carrierStatus.equalsIgnoreCase(carrierStatus) : null;
    }

    private BooleanExpression carrierTypeContains(String carrierType) {
        return StringUtils.hasText(carrierType) ? qCarrier.carrierType.contains(carrierType) : null;
    }

    private BooleanExpression carrierDetailTypeContains(String carrierDetailType) {
        return StringUtils.hasText(carrierDetailType) ? qCarrier.carrierDetailType.contains(carrierDetailType) : null;
    }

    private BooleanExpression carrierGroupContains(String carrierGroup) {
        return StringUtils.hasText(carrierGroup) ? qCarrier.carrierGroup.contains(carrierGroup) : null;
    }

    private BooleanExpression currentPositionNameContains(String currentPositionName) {
        return StringUtils.hasText(currentPositionName) ? qCarrier.currentPositionName.contains(currentPositionName) : null;
    }

    private BooleanExpression currentEquipmentNameContains(String currentEquipmentName) {
        return StringUtils.hasText(currentEquipmentName) ? qCarrier.currentEquipmentName.contains(currentEquipmentName) : null;
    }

    private BooleanExpression zoneNameContains(String zoneName) {
        return StringUtils.hasText(zoneName) ? qCarrier.zoneName.contains(zoneName) : null;
    }

    private BooleanExpression lotNameContains(String lotName) {
        return StringUtils.hasText(lotName) ? qCarrier.lotName.contains(lotName) : null;
    }

    private BooleanExpression orderIdContains(String orderId) {
        return StringUtils.hasText(orderId) ? qCarrier.orderId.contains(orderId) : null;
    }

    private BooleanExpression itemNameContains(String itemName) {
        return StringUtils.hasText(itemName) ? qCarrier.itemName.contains(itemName) : null;
    }

    private BooleanExpression productionTypeContains(String productionType) {
        return StringUtils.hasText(productionType) ? qCarrier.productionType.contains(productionType) : null;
    }
}
