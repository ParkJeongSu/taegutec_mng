package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.condition.WcsShelfSearchCondition;
import kr.co.aim.domain.model.WcsShelf;
import kr.co.aim.domain.repository.WcsShelfRepository;
import kr.co.aim.infra.persistence.entity.QWcsShelfEntity;
import kr.co.aim.infra.persistence.entity.WcsShelfEntity;
import kr.co.aim.infra.persistence.entity.WcsShelfId;
import kr.co.aim.infra.persistence.mapper.WcsShelfMapper;
import kr.co.aim.infra.persistence.springdatajpa.WcsShelfJpaRepository;
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
public class WcsShelfRepositoryImpl implements WcsShelfRepository {

    private final WcsShelfJpaRepository wcsShelfJpaRepository;
    private final WcsShelfMapper wcsShelfMapper;
    private final JPAQueryFactory queryFactory;

    private static final QWcsShelfEntity qShelf = QWcsShelfEntity.wcsShelfEntity;

    @Override
    public List<WcsShelf> findAll() {
        List<WcsShelfEntity> entities = wcsShelfJpaRepository.findAll();
        List<WcsShelf> result = new ArrayList<>();
        for (WcsShelfEntity entity : entities) {
            if (entity != null) {
                result.add(wcsShelfMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public Optional<WcsShelf> findById(String factoryName, String stockerName, String shelfName) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(stockerName) || !StringUtils.hasText(shelfName)) {
            return Optional.empty();
        }
        Optional<WcsShelfEntity> entityOpt = wcsShelfJpaRepository.findById(new WcsShelfId(factoryName, shelfName, stockerName));
        if (entityOpt.isPresent()) {
            return Optional.ofNullable(wcsShelfMapper.toDomain(entityOpt.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<WcsShelf> findByFactoryNameAndStockerName(String factoryName, String stockerName) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(stockerName)) {
            return new ArrayList<>();
        }
        List<WcsShelfEntity> entities = wcsShelfJpaRepository.findByFactoryNameAndStockerName(factoryName, stockerName);
        List<WcsShelf> result = new ArrayList<>();
        for (WcsShelfEntity entity : entities) {
            if (entity != null) {
                result.add(wcsShelfMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public boolean existsById(String factoryName, String stockerName, String shelfName) {
        if (!StringUtils.hasText(factoryName) || !StringUtils.hasText(stockerName) || !StringUtils.hasText(shelfName)) {
            return false;
        }
        return wcsShelfJpaRepository.existsById(new WcsShelfId(factoryName, shelfName, stockerName));
    }

    @Override
    public WcsShelf save(WcsShelf shelf) {
        WcsShelfEntity entity = wcsShelfMapper.toEntity(shelf);
        WcsShelfEntity savedEntity = wcsShelfJpaRepository.save(entity);
        return wcsShelfMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(String factoryName, String stockerName, String shelfName) {
        if (StringUtils.hasText(factoryName) && StringUtils.hasText(stockerName) && StringUtils.hasText(shelfName)) {
            wcsShelfJpaRepository.deleteById(new WcsShelfId(factoryName, shelfName, stockerName));
        }
    }

    @Override
    public Page<WcsShelf> findShelves(WcsShelfSearchCondition condition, Pageable pageable) {
        String factoryName = (condition != null) ? condition.getFactoryName() : null;
        String stockerName = (condition != null) ? condition.getStockerName() : null;
        String shelfName = (condition != null) ? condition.getShelfName() : null;
        String zoneName = (condition != null) ? condition.getZoneName() : null;
        String shelfStatus = (condition != null) ? condition.getShelfStatus() : null;
        String carrierName = (condition != null) ? condition.getCarrierName() : null;
        String shelfType = (condition != null) ? condition.getShelfType() : null;
        String shelfEnableMode = (condition != null) ? condition.getShelfEnableMode() : null;
        String shelfTransferStatus = (condition != null) ? condition.getShelfTransferStatus() : null;
        Integer row = (condition != null) ? condition.getRow() : null;
        Integer col = (condition != null) ? condition.getCol() : null;
        Integer stage = (condition != null) ? condition.getStage() : null;
        Integer bin = (condition != null) ? condition.getBin() : null;

        JPAQuery<WcsShelfEntity> query = queryFactory
                .selectFrom(qShelf)
                .where(
                        factoryNameContains(factoryName),
                        stockerNameContains(stockerName),
                        shelfNameContains(shelfName),
                        zoneNameContains(zoneName),
                        shelfStatusEq(shelfStatus),
                        carrierNameContains(carrierName),
                        shelfTypeContains(shelfType),
                        shelfEnableModeEq(shelfEnableMode),
                        shelfTransferStatusEq(shelfTransferStatus),
                        rowEq(row),
                        colEq(col),
                        stageEq(stage),
                        binEq(bin)
                );

        query.orderBy(getOrderSpecifiers(pageable != null ? pageable.getSort() : Sort.unsorted()));

        if (pageable != null && pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<WcsShelfEntity> content = query.fetch();
        List<WcsShelf> resultList = new ArrayList<>();
        for (WcsShelfEntity entity : content) {
            if (entity != null) {
                resultList.add(wcsShelfMapper.toDomain(entity));
            }
        }

        long total;
        if (pageable != null && pageable.isPaged()) {
            Long count = queryFactory
                    .select(qShelf.count())
                    .from(qShelf)
                    .where(
                            factoryNameContains(factoryName),
                            stockerNameContains(stockerName),
                            shelfNameContains(shelfName),
                            zoneNameContains(zoneName),
                            shelfStatusEq(shelfStatus),
                            carrierNameContains(carrierName),
                            shelfTypeContains(shelfType),
                            shelfEnableModeEq(shelfEnableMode),
                            shelfTransferStatusEq(shelfTransferStatus),
                            rowEq(row),
                            colEq(col),
                            stageEq(stage),
                            binEq(bin)
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
                PathBuilder pathBuilder = new PathBuilder<>(qShelf.getType(), qShelf.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.ASC, qShelf.factoryName));
            orders.add(new OrderSpecifier(Order.ASC, qShelf.stockerName));
            orders.add(new OrderSpecifier(Order.ASC, qShelf.shelfName));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression factoryNameContains(String factoryName) {
        return StringUtils.hasText(factoryName) ? qShelf.factoryName.contains(factoryName) : null;
    }

    private BooleanExpression stockerNameContains(String stockerName) {
        return StringUtils.hasText(stockerName) ? qShelf.stockerName.contains(stockerName) : null;
    }

    private BooleanExpression shelfNameContains(String shelfName) {
        return StringUtils.hasText(shelfName) ? qShelf.shelfName.contains(shelfName) : null;
    }

    private BooleanExpression zoneNameContains(String zoneName) {
        return StringUtils.hasText(zoneName) ? qShelf.zoneName.contains(zoneName) : null;
    }

    private BooleanExpression shelfStatusEq(String shelfStatus) {
        return StringUtils.hasText(shelfStatus) ? qShelf.shelfStatus.equalsIgnoreCase(shelfStatus) : null;
    }

    private BooleanExpression carrierNameContains(String carrierName) {
        return StringUtils.hasText(carrierName) ? qShelf.carrierName.contains(carrierName) : null;
    }

    private BooleanExpression shelfTypeContains(String shelfType) {
        return StringUtils.hasText(shelfType) ? qShelf.shelfType.contains(shelfType) : null;
    }

    private BooleanExpression shelfEnableModeEq(String shelfEnableMode) {
        return StringUtils.hasText(shelfEnableMode) ? qShelf.shelfEnableMode.equalsIgnoreCase(shelfEnableMode) : null;
    }

    private BooleanExpression shelfTransferStatusEq(String shelfTransferStatus) {
        return StringUtils.hasText(shelfTransferStatus) ? qShelf.shelfTransferStatus.equalsIgnoreCase(shelfTransferStatus) : null;
    }

    private BooleanExpression rowEq(Integer row) {
        return (row != null) ? qShelf.row.eq(row) : null;
    }

    private BooleanExpression colEq(Integer col) {
        return (col != null) ? qShelf.col.eq(col) : null;
    }

    private BooleanExpression stageEq(Integer stage) {
        return (stage != null) ? qShelf.stage.eq(stage) : null;
    }

    private BooleanExpression binEq(Integer bin) {
        return (bin != null) ? qShelf.bin.eq(bin) : null;
    }
}
