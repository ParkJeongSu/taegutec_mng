package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.condition.WcsTransferCommandSearchCondition;
import kr.co.aim.domain.model.WcsTransferCommand;
import kr.co.aim.domain.repository.WcsTransferCommandRepository;
import kr.co.aim.infra.persistence.entity.QWcsTransferCommandEntity;
import kr.co.aim.infra.persistence.entity.WcsTransferCommandEntity;
import kr.co.aim.infra.persistence.mapper.WcsTransferCommandMapper;
import kr.co.aim.infra.persistence.springdatajpa.WcsTransferCommandJpaRepository;
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
public class WcsTransferCommandRepositoryImpl implements WcsTransferCommandRepository {

    private final WcsTransferCommandJpaRepository wcsTransferCommandJpaRepository;
    private final WcsTransferCommandMapper wcsTransferCommandMapper;
    private final JPAQueryFactory queryFactory;

    private static final QWcsTransferCommandEntity qCommand = QWcsTransferCommandEntity.wcsTransferCommandEntity;

    @Override
    public List<WcsTransferCommand> findAll() {
        List<WcsTransferCommandEntity> entities = wcsTransferCommandJpaRepository.findAll();
        List<WcsTransferCommand> result = new ArrayList<>();
        for (WcsTransferCommandEntity entity : entities) {
            if (entity != null) {
                result.add(wcsTransferCommandMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public Optional<WcsTransferCommand> findById(String transferCommandName) {
        if (!StringUtils.hasText(transferCommandName)) {
            return Optional.empty();
        }
        Optional<WcsTransferCommandEntity> entityOpt = wcsTransferCommandJpaRepository.findById(transferCommandName.trim());
        if (entityOpt.isPresent()) {
            return Optional.ofNullable(wcsTransferCommandMapper.toDomain(entityOpt.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<WcsTransferCommand> findByFactoryName(String factoryName) {
        if (!StringUtils.hasText(factoryName)) {
            return new ArrayList<>();
        }
        List<WcsTransferCommandEntity> entities = wcsTransferCommandJpaRepository.findByFactoryName(factoryName.trim());
        List<WcsTransferCommand> result = new ArrayList<>();
        for (WcsTransferCommandEntity entity : entities) {
            if (entity != null) {
                result.add(wcsTransferCommandMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public List<WcsTransferCommand> findByCarrierName(String carrierName) {
        if (!StringUtils.hasText(carrierName)) {
            return new ArrayList<>();
        }
        List<WcsTransferCommandEntity> entities = wcsTransferCommandJpaRepository.findByCarrierName(carrierName.trim());
        List<WcsTransferCommand> result = new ArrayList<>();
        for (WcsTransferCommandEntity entity : entities) {
            if (entity != null) {
                result.add(wcsTransferCommandMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public boolean existsById(String transferCommandName) {
        if (!StringUtils.hasText(transferCommandName)) {
            return false;
        }
        return wcsTransferCommandJpaRepository.existsById(transferCommandName.trim());
    }

    @Override
    public WcsTransferCommand save(WcsTransferCommand command) {
        WcsTransferCommandEntity entity = wcsTransferCommandMapper.toEntity(command);
        WcsTransferCommandEntity savedEntity = wcsTransferCommandJpaRepository.save(entity);
        return wcsTransferCommandMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(String transferCommandName) {
        if (StringUtils.hasText(transferCommandName)) {
            wcsTransferCommandJpaRepository.deleteById(transferCommandName.trim());
        }
    }

    @Override
    public Page<WcsTransferCommand> findTransferCommands(WcsTransferCommandSearchCondition condition, Pageable pageable) {
        String transferCommandName = (condition != null) ? condition.getTransferCommandName() : null;
        String carrierName = (condition != null) ? condition.getCarrierName() : null;
        String factoryName = (condition != null) ? condition.getFactoryName() : null;
        String commandStatus = (condition != null) ? condition.getCommandStatus() : null;
        String currentEquipmentName = (condition != null) ? condition.getCurrentEquipmentName() : null;
        String hotLot = (condition != null) ? condition.getHotLot() : null;
        String lotName = (condition != null) ? condition.getLotName() : null;
        String owner = (condition != null) ? condition.getOwner() : null;
        String source = (condition != null) ? condition.getSource() : null;
        String target = (condition != null) ? condition.getTarget() : null;
        String targetEquipmentName = (condition != null) ? condition.getTargetEquipmentName() : null;
        String currentSource = (condition != null) ? condition.getCurrentSource() : null;
        String orderType = (condition != null) ? condition.getOrderType() : null;
        String sourceEquipmentName = (condition != null) ? condition.getSourceEquipmentName() : null;
        String sourceTransferType = (condition != null) ? condition.getSourceTransferType() : null;
        String targetTransferType = (condition != null) ? condition.getTargetTransferType() : null;
        Integer subCommandJobNo = (condition != null) ? condition.getSubCommandJobNo() : null;
        String subCommandStatus = (condition != null) ? condition.getSubCommandStatus() : null;
        String processType = (condition != null) ? condition.getProcessType() : null;
        Boolean startReportFlag = (condition != null) ? condition.getStartReportFlag() : null;

        JPAQuery<WcsTransferCommandEntity> query = queryFactory
                .selectFrom(qCommand)
                .where(
                        transferCommandNameContains(transferCommandName),
                        carrierNameContains(carrierName),
                        factoryNameContains(factoryName),
                        commandStatusEq(commandStatus),
                        currentEquipmentNameContains(currentEquipmentName),
                        hotLotEq(hotLot),
                        lotNameContains(lotName),
                        ownerContains(owner),
                        sourceContains(source),
                        targetContains(target),
                        targetEquipmentNameContains(targetEquipmentName),
                        currentSourceContains(currentSource),
                        orderTypeEq(orderType),
                        sourceEquipmentNameContains(sourceEquipmentName),
                        sourceTransferTypeEq(sourceTransferType),
                        targetTransferTypeEq(targetTransferType),
                        subCommandJobNoEq(subCommandJobNo),
                        subCommandStatusEq(subCommandStatus),
                        processTypeEq(processType),
                        startReportFlagEq(startReportFlag)
                );

        query.orderBy(getOrderSpecifiers(pageable != null ? pageable.getSort() : Sort.unsorted()));

        if (pageable != null && pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<WcsTransferCommandEntity> content = query.fetch();
        List<WcsTransferCommand> resultList = new ArrayList<>();
        for (WcsTransferCommandEntity entity : content) {
            if (entity != null) {
                resultList.add(wcsTransferCommandMapper.toDomain(entity));
            }
        }

        long total;
        if (pageable != null && pageable.isPaged()) {
            Long count = queryFactory
                    .select(qCommand.count())
                    .from(qCommand)
                    .where(
                            transferCommandNameContains(transferCommandName),
                            carrierNameContains(carrierName),
                            factoryNameContains(factoryName),
                            commandStatusEq(commandStatus),
                            currentEquipmentNameContains(currentEquipmentName),
                            hotLotEq(hotLot),
                            lotNameContains(lotName),
                            ownerContains(owner),
                            sourceContains(source),
                            targetContains(target),
                            targetEquipmentNameContains(targetEquipmentName),
                            currentSourceContains(currentSource),
                            orderTypeEq(orderType),
                            sourceEquipmentNameContains(sourceEquipmentName),
                            sourceTransferTypeEq(sourceTransferType),
                            targetTransferTypeEq(targetTransferType),
                            subCommandJobNoEq(subCommandJobNo),
                            subCommandStatusEq(subCommandStatus),
                            processTypeEq(processType),
                            startReportFlagEq(startReportFlag)
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
                PathBuilder pathBuilder = new PathBuilder<>(qCommand.getType(), qCommand.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.ASC, qCommand.transferCommandName));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression transferCommandNameContains(String transferCommandName) {
        return StringUtils.hasText(transferCommandName) ? qCommand.transferCommandName.contains(transferCommandName) : null;
    }

    private BooleanExpression carrierNameContains(String carrierName) {
        return StringUtils.hasText(carrierName) ? qCommand.carrierName.contains(carrierName) : null;
    }

    private BooleanExpression factoryNameContains(String factoryName) {
        return StringUtils.hasText(factoryName) ? qCommand.factoryName.contains(factoryName) : null;
    }

    private BooleanExpression commandStatusEq(String commandStatus) {
        return StringUtils.hasText(commandStatus) ? qCommand.commandStatus.equalsIgnoreCase(commandStatus) : null;
    }

    private BooleanExpression currentEquipmentNameContains(String currentEquipmentName) {
        return StringUtils.hasText(currentEquipmentName) ? qCommand.currentEquipmentName.contains(currentEquipmentName) : null;
    }

    private BooleanExpression hotLotEq(String hotLot) {
        return StringUtils.hasText(hotLot) ? qCommand.hotLot.equalsIgnoreCase(hotLot) : null;
    }

    private BooleanExpression lotNameContains(String lotName) {
        return StringUtils.hasText(lotName) ? qCommand.lotName.contains(lotName) : null;
    }

    private BooleanExpression ownerContains(String owner) {
        return StringUtils.hasText(owner) ? qCommand.owner.contains(owner) : null;
    }

    private BooleanExpression sourceContains(String source) {
        return StringUtils.hasText(source) ? qCommand.source.contains(source) : null;
    }

    private BooleanExpression targetContains(String target) {
        return StringUtils.hasText(target) ? qCommand.target.contains(target) : null;
    }

    private BooleanExpression targetEquipmentNameContains(String targetEquipmentName) {
        return StringUtils.hasText(targetEquipmentName) ? qCommand.targetEquipmentName.contains(targetEquipmentName) : null;
    }

    private BooleanExpression currentSourceContains(String currentSource) {
        return StringUtils.hasText(currentSource) ? qCommand.currentSource.contains(currentSource) : null;
    }

    private BooleanExpression orderTypeEq(String orderType) {
        return StringUtils.hasText(orderType) ? qCommand.orderType.equalsIgnoreCase(orderType) : null;
    }

    private BooleanExpression sourceEquipmentNameContains(String sourceEquipmentName) {
        return StringUtils.hasText(sourceEquipmentName) ? qCommand.sourceEquipmentName.contains(sourceEquipmentName) : null;
    }

    private BooleanExpression sourceTransferTypeEq(String sourceTransferType) {
        return StringUtils.hasText(sourceTransferType) ? qCommand.sourceTransferType.equalsIgnoreCase(sourceTransferType) : null;
    }

    private BooleanExpression targetTransferTypeEq(String targetTransferType) {
        return StringUtils.hasText(targetTransferType) ? qCommand.targetTransferType.equalsIgnoreCase(targetTransferType) : null;
    }

    private BooleanExpression subCommandJobNoEq(Integer subCommandJobNo) {
        return subCommandJobNo != null ? qCommand.subCommandJobNo.eq(subCommandJobNo) : null;
    }

    private BooleanExpression subCommandStatusEq(String subCommandStatus) {
        return StringUtils.hasText(subCommandStatus) ? qCommand.subCommandStatus.equalsIgnoreCase(subCommandStatus) : null;
    }

    private BooleanExpression processTypeEq(String processType) {
        return StringUtils.hasText(processType) ? qCommand.processType.equalsIgnoreCase(processType) : null;
    }

    private BooleanExpression startReportFlagEq(Boolean startReportFlag) {
        return startReportFlag != null ? qCommand.startReportFlag.eq(startReportFlag) : null;
    }
}
