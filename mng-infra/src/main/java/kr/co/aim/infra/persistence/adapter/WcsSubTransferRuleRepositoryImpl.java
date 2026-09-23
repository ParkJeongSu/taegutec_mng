package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.condition.WcsSubTransferRuleSearchCondition;
import kr.co.aim.domain.model.WcsSubTransferRule;
import kr.co.aim.domain.repository.WcsSubTransferRuleRepository;
import kr.co.aim.infra.persistence.entity.QWcsSubTransferRuleEntity;
import kr.co.aim.infra.persistence.entity.WcsSubTransferRuleEntity;
import kr.co.aim.infra.persistence.entity.WcsSubTransferRuleId;
import kr.co.aim.infra.persistence.mapper.WcsSubTransferRuleMapper;
import kr.co.aim.infra.persistence.springdatajpa.WcsSubTransferRuleJpaRepository;
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
public class WcsSubTransferRuleRepositoryImpl implements WcsSubTransferRuleRepository {

    private final WcsSubTransferRuleJpaRepository wcsSubTransferRuleJpaRepository;
    private final WcsSubTransferRuleMapper wcsSubTransferRuleMapper;
    private final JPAQueryFactory queryFactory;

    private static final QWcsSubTransferRuleEntity qRule = QWcsSubTransferRuleEntity.wcsSubTransferRuleEntity;

    @Override
    public List<WcsSubTransferRule> findAll() {
        List<WcsSubTransferRuleEntity> entities = wcsSubTransferRuleJpaRepository.findAll();
        List<WcsSubTransferRule> result = new ArrayList<>();
        for (WcsSubTransferRuleEntity entity : entities) {
            if (entity != null) {
                result.add(wcsSubTransferRuleMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public Optional<WcsSubTransferRule> findById(String factoryName, String equipmentName, String moduleName, Long routeLinkId) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(equipmentName)
                || !StringUtils.hasText(moduleName) || routeLinkId == null) {
            return Optional.empty();
        }
        WcsSubTransferRuleId id = WcsSubTransferRuleId.builder()
                .factoryName(factoryName)
                .equipmentName(equipmentName)
                .moduleName(moduleName)
                .routeLinkId(routeLinkId)
                .build();
        Optional<WcsSubTransferRuleEntity> entityOpt = wcsSubTransferRuleJpaRepository.findById(id);
        if (entityOpt.isPresent()) {
            return Optional.ofNullable(wcsSubTransferRuleMapper.toDomain(entityOpt.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<WcsSubTransferRule> findByFactoryName(String factoryName) {
        if (!StringUtils.hasText(factoryName)) {
            return new ArrayList<>();
        }
        List<WcsSubTransferRuleEntity> entities = wcsSubTransferRuleJpaRepository.findByFactoryName(factoryName);
        List<WcsSubTransferRule> result = new ArrayList<>();
        for (WcsSubTransferRuleEntity entity : entities) {
            if (entity != null) {
                result.add(wcsSubTransferRuleMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public List<WcsSubTransferRule> findByFactoryNameAndEquipmentName(String factoryName, String equipmentName) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(equipmentName)) {
            return new ArrayList<>();
        }
        List<WcsSubTransferRuleEntity> entities = wcsSubTransferRuleJpaRepository.findByFactoryNameAndEquipmentName(factoryName, equipmentName);
        List<WcsSubTransferRule> result = new ArrayList<>();
        for (WcsSubTransferRuleEntity entity : entities) {
            if (entity != null) {
                result.add(wcsSubTransferRuleMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public boolean existsById(String factoryName, String equipmentName, String moduleName, Long routeLinkId) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(equipmentName)
                || !StringUtils.hasText(moduleName) || routeLinkId == null) {
            return false;
        }
        WcsSubTransferRuleId id = WcsSubTransferRuleId.builder()
                .factoryName(factoryName)
                .equipmentName(equipmentName)
                .moduleName(moduleName)
                .routeLinkId(routeLinkId)
                .build();
        return wcsSubTransferRuleJpaRepository.existsById(id);
    }

    @Override
    public WcsSubTransferRule save(WcsSubTransferRule subTransferRule) {
        WcsSubTransferRuleEntity entity = wcsSubTransferRuleMapper.toEntity(subTransferRule);
        WcsSubTransferRuleEntity savedEntity = wcsSubTransferRuleJpaRepository.save(entity);
        return wcsSubTransferRuleMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(String factoryName, String equipmentName, String moduleName, Long routeLinkId) {
        if (StringUtils.hasText(factoryName) && StringUtils.hasText(equipmentName)
                && StringUtils.hasText(moduleName) && routeLinkId != null) {
            WcsSubTransferRuleId id = WcsSubTransferRuleId.builder()
                    .factoryName(factoryName)
                    .equipmentName(equipmentName)
                    .moduleName(moduleName)
                    .routeLinkId(routeLinkId)
                    .build();
            wcsSubTransferRuleJpaRepository.deleteById(id);
        }
    }

    @Override
    public Page<WcsSubTransferRule> findSubTransferRules(WcsSubTransferRuleSearchCondition condition, Pageable pageable) {
        String factoryName = (condition != null) ? condition.getFactoryName() : null;
        String equipmentName = (condition != null) ? condition.getEquipmentName() : null;
        String moduleName = (condition != null) ? condition.getModuleName() : null;
        Long routeLinkId = (condition != null) ? condition.getRouteLinkId() : null;
        Integer carrierCount = (condition != null) ? condition.getCarrierCount() : null;
        String description = (condition != null) ? condition.getDescription() : null;
        String moduleType = (condition != null) ? condition.getModuleType() : null;
        String ngStatus = (condition != null) ? condition.getNgStatus() : null;

        JPAQuery<WcsSubTransferRuleEntity> query = queryFactory
                .selectFrom(qRule)
                .where(
                        factoryNameContains(factoryName),
                        equipmentNameContains(equipmentName),
                        moduleNameContains(moduleName),
                        routeLinkIdEq(routeLinkId),
                        carrierCountEq(carrierCount),
                        descriptionContains(description),
                        moduleTypeEq(moduleType),
                        ngStatusEq(ngStatus)
                );

        query.orderBy(getOrderSpecifiers(pageable != null ? pageable.getSort() : Sort.unsorted()));

        if (pageable != null && pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<WcsSubTransferRuleEntity> content = query.fetch();
        List<WcsSubTransferRule> resultList = new ArrayList<>();
        for (WcsSubTransferRuleEntity entity : content) {
            if (entity != null) {
                resultList.add(wcsSubTransferRuleMapper.toDomain(entity));
            }
        }

        long total;
        if (pageable != null && pageable.isPaged()) {
            Long count = queryFactory
                    .select(qRule.count())
                    .from(qRule)
                    .where(
                            factoryNameContains(factoryName),
                            equipmentNameContains(equipmentName),
                            moduleNameContains(moduleName),
                            routeLinkIdEq(routeLinkId),
                            carrierCountEq(carrierCount),
                            descriptionContains(description),
                            moduleTypeEq(moduleType),
                            ngStatusEq(ngStatus)
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
                PathBuilder pathBuilder = new PathBuilder<>(qRule.getType(), qRule.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.ASC, qRule.factoryName));
            orders.add(new OrderSpecifier(Order.ASC, qRule.equipmentName));
            orders.add(new OrderSpecifier(Order.ASC, qRule.moduleName));
            orders.add(new OrderSpecifier(Order.ASC, qRule.routeLinkId));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression factoryNameContains(String factoryName) {
        return StringUtils.hasText(factoryName) ? qRule.factoryName.contains(factoryName) : null;
    }

    private BooleanExpression equipmentNameContains(String equipmentName) {
        return StringUtils.hasText(equipmentName) ? qRule.equipmentName.contains(equipmentName) : null;
    }

    private BooleanExpression moduleNameContains(String moduleName) {
        return StringUtils.hasText(moduleName) ? qRule.moduleName.contains(moduleName) : null;
    }

    private BooleanExpression routeLinkIdEq(Long routeLinkId) {
        return routeLinkId != null ? qRule.routeLinkId.eq(routeLinkId) : null;
    }

    private BooleanExpression carrierCountEq(Integer carrierCount) {
        return carrierCount != null ? qRule.carrierCount.eq(carrierCount) : null;
    }

    private BooleanExpression descriptionContains(String description) {
        return StringUtils.hasText(description) ? qRule.description.contains(description) : null;
    }

    private BooleanExpression moduleTypeEq(String moduleType) {
        return StringUtils.hasText(moduleType) ? qRule.moduleType.equalsIgnoreCase(moduleType) : null;
    }

    private BooleanExpression ngStatusEq(String ngStatus) {
        return StringUtils.hasText(ngStatus) ? qRule.ngStatus.equalsIgnoreCase(ngStatus) : null;
    }
}
