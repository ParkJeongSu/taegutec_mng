package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.condition.WcsSubTransferCommandSearchCondition;
import kr.co.aim.domain.model.WcsSubTransferCommand;
import kr.co.aim.domain.repository.WcsSubTransferCommandRepository;
import kr.co.aim.infra.persistence.entity.QWcsSubTransferCommandEntity;
import kr.co.aim.infra.persistence.entity.WcsSubTransferCommandEntity;
import kr.co.aim.infra.persistence.entity.WcsSubTransferCommandHistoryEntity;
import kr.co.aim.infra.persistence.entity.WcsSubTransferCommandId;
import kr.co.aim.infra.persistence.mapper.WcsSubTransferCommandMapper;
import kr.co.aim.infra.persistence.springdatajpa.WcsSubTransferCommandHistoryJpaRepository;
import kr.co.aim.infra.persistence.springdatajpa.WcsSubTransferCommandJpaRepository;
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
public class WcsSubTransferCommandRepositoryImpl implements WcsSubTransferCommandRepository {

    private final WcsSubTransferCommandJpaRepository wcsSubTransferCommandJpaRepository;
    private final WcsSubTransferCommandHistoryJpaRepository wcsSubTransferCommandHistoryJpaRepository;
    private final WcsSubTransferCommandMapper wcsSubTransferCommandMapper;
    private final JPAQueryFactory queryFactory;

    private static final QWcsSubTransferCommandEntity qSubTransferCommand = QWcsSubTransferCommandEntity.wcsSubTransferCommandEntity;

    @Override
    public List<WcsSubTransferCommand> findAll() {
        List<WcsSubTransferCommandEntity> entities = wcsSubTransferCommandJpaRepository.findAll();
        List<WcsSubTransferCommand> result = new ArrayList<>();
        for (WcsSubTransferCommandEntity entity : entities) {
            if (entity != null) {
                result.add(wcsSubTransferCommandMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public Optional<WcsSubTransferCommand> findById(String transferCommandName, Integer jobNo) {
        if (!StringUtils.hasText(transferCommandName) || jobNo == null) {
            return Optional.empty();
        }
        WcsSubTransferCommandId id = WcsSubTransferCommandId.builder()
                .transferCommandName(transferCommandName)
                .jobNo(jobNo)
                .build();
        Optional<WcsSubTransferCommandEntity> entityOpt = wcsSubTransferCommandJpaRepository.findById(id);
        if (entityOpt.isPresent()) {
            return Optional.ofNullable(wcsSubTransferCommandMapper.toDomain(entityOpt.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<WcsSubTransferCommand> findByTransferCommandName(String transferCommandName) {
        if (!StringUtils.hasText(transferCommandName)) {
            return new ArrayList<>();
        }
        List<WcsSubTransferCommandEntity> entities = wcsSubTransferCommandJpaRepository.findByTransferCommandNameOrderByJobNoAsc(transferCommandName);
        List<WcsSubTransferCommand> result = new ArrayList<>();
        for (WcsSubTransferCommandEntity entity : entities) {
            if (entity != null) {
                result.add(wcsSubTransferCommandMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public List<WcsSubTransferCommand> findByCarrierName(String carrierName) {
        if (!StringUtils.hasText(carrierName)) {
            return new ArrayList<>();
        }
        List<WcsSubTransferCommandEntity> entities = wcsSubTransferCommandJpaRepository.findByCarrierName(carrierName);
        List<WcsSubTransferCommand> result = new ArrayList<>();
        for (WcsSubTransferCommandEntity entity : entities) {
            if (entity != null) {
                result.add(wcsSubTransferCommandMapper.toDomain(entity));
            }
        }
        return result;
    }

    @Override
    public boolean existsById(String transferCommandName, Integer jobNo) {
        if (!StringUtils.hasText(transferCommandName) || jobNo == null) {
            return false;
        }
        WcsSubTransferCommandId id = WcsSubTransferCommandId.builder()
                .transferCommandName(transferCommandName)
                .jobNo(jobNo)
                .build();
        return wcsSubTransferCommandJpaRepository.existsById(id);
    }

    @Override
    public WcsSubTransferCommand save(WcsSubTransferCommand command) {
        WcsSubTransferCommandEntity entity = wcsSubTransferCommandMapper.toEntity(command);
        WcsSubTransferCommandEntity savedEntity = wcsSubTransferCommandJpaRepository.save(entity);

        WcsSubTransferCommandHistoryEntity historyEntity = wcsSubTransferCommandMapper.toHistoryEntity(command);
        wcsSubTransferCommandHistoryJpaRepository.save(historyEntity);

        return wcsSubTransferCommandMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(String transferCommandName, Integer jobNo) {
        if (StringUtils.hasText(transferCommandName) && jobNo != null) {
            WcsSubTransferCommandId id = WcsSubTransferCommandId.builder()
                    .transferCommandName(transferCommandName)
                    .jobNo(jobNo)
                    .build();
            wcsSubTransferCommandJpaRepository.deleteById(id);
        }
    }

    @Override
    public Page<WcsSubTransferCommand> findSubTransferCommands(WcsSubTransferCommandSearchCondition condition, Pageable pageable) {
        String transferCommandName = (condition != null) ? condition.getTransferCommandName() : null;
        Integer jobNo = (condition != null) ? condition.getJobNo() : null;
        String carrierName = (condition != null) ? condition.getCarrierName() : null;
        String transferEquipmentName = (condition != null) ? condition.getTransferEquipmentName() : null;
        String subCommandStatus = (condition != null) ? condition.getSubCommandStatus() : null;
        String transferType = (condition != null) ? condition.getTransferType() : null;
        String factoryName = (condition != null) ? condition.getFactoryName() : null;
        String transferUnitName = (condition != null) ? condition.getTransferUnitName() : null;
        String jobCompleteState = (condition != null) ? condition.getJobCompleteState() : null;
        String sourcePositionName = (condition != null) ? condition.getSourcePositionName() : null;
        String targetPositionName = (condition != null) ? condition.getTargetPositionName() : null;
        String loadType = (condition != null) ? condition.getLoadType() : null;

        JPAQuery<WcsSubTransferCommandEntity> query = queryFactory
                .selectFrom(qSubTransferCommand)
                .where(
                        transferCommandNameContains(transferCommandName),
                        jobNoEq(jobNo),
                        carrierNameContains(carrierName),
                        transferEquipmentNameContains(transferEquipmentName),
                        subCommandStatusEq(subCommandStatus),
                        transferTypeEq(transferType),
                        factoryNameContains(factoryName),
                        transferUnitNameContains(transferUnitName),
                        jobCompleteStateEq(jobCompleteState),
                        sourcePositionNameContains(sourcePositionName),
                        targetPositionNameContains(targetPositionName),
                        loadTypeEq(loadType)
                );

        query.orderBy(getOrderSpecifiers(pageable != null ? pageable.getSort() : Sort.unsorted()));

        if (pageable != null && pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<WcsSubTransferCommandEntity> content = query.fetch();
        List<WcsSubTransferCommand> resultList = new ArrayList<>();
        for (WcsSubTransferCommandEntity entity : content) {
            if (entity != null) {
                resultList.add(wcsSubTransferCommandMapper.toDomain(entity));
            }
        }

        long total;
        if (pageable != null && pageable.isPaged()) {
            Long count = queryFactory
                    .select(qSubTransferCommand.count())
                    .from(qSubTransferCommand)
                    .where(
                            transferCommandNameContains(transferCommandName),
                            jobNoEq(jobNo),
                            carrierNameContains(carrierName),
                            transferEquipmentNameContains(transferEquipmentName),
                            subCommandStatusEq(subCommandStatus),
                            transferTypeEq(transferType),
                            factoryNameContains(factoryName),
                            transferUnitNameContains(transferUnitName),
                            jobCompleteStateEq(jobCompleteState),
                            sourcePositionNameContains(sourcePositionName),
                            targetPositionNameContains(targetPositionName),
                            loadTypeEq(loadType)
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
                PathBuilder pathBuilder = new PathBuilder<>(qSubTransferCommand.getType(), qSubTransferCommand.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.ASC, qSubTransferCommand.transferCommandName));
            orders.add(new OrderSpecifier(Order.ASC, qSubTransferCommand.jobNo));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression transferCommandNameContains(String transferCommandName) {
        return StringUtils.hasText(transferCommandName) ? qSubTransferCommand.transferCommandName.contains(transferCommandName) : null;
    }

    private BooleanExpression jobNoEq(Integer jobNo) {
        return jobNo != null ? qSubTransferCommand.jobNo.eq(jobNo) : null;
    }

    private BooleanExpression carrierNameContains(String carrierName) {
        return StringUtils.hasText(carrierName) ? qSubTransferCommand.carrierName.contains(carrierName) : null;
    }

    private BooleanExpression transferEquipmentNameContains(String transferEquipmentName) {
        return StringUtils.hasText(transferEquipmentName) ? qSubTransferCommand.transferEquipmentName.contains(transferEquipmentName) : null;
    }

    private BooleanExpression subCommandStatusEq(String subCommandStatus) {
        return StringUtils.hasText(subCommandStatus) ? qSubTransferCommand.subCommandStatus.equalsIgnoreCase(subCommandStatus) : null;
    }

    private BooleanExpression transferTypeEq(String transferType) {
        return StringUtils.hasText(transferType) ? qSubTransferCommand.transferType.equalsIgnoreCase(transferType) : null;
    }

    private BooleanExpression factoryNameContains(String factoryName) {
        return StringUtils.hasText(factoryName) ? qSubTransferCommand.factoryName.contains(factoryName) : null;
    }

    private BooleanExpression transferUnitNameContains(String transferUnitName) {
        return StringUtils.hasText(transferUnitName) ? qSubTransferCommand.transferUnitName.contains(transferUnitName) : null;
    }

    private BooleanExpression jobCompleteStateEq(String jobCompleteState) {
        return StringUtils.hasText(jobCompleteState) ? qSubTransferCommand.jobCompleteState.equalsIgnoreCase(jobCompleteState) : null;
    }

    private BooleanExpression sourcePositionNameContains(String sourcePositionName) {
        return StringUtils.hasText(sourcePositionName) ? qSubTransferCommand.sourcePositionName.contains(sourcePositionName) : null;
    }

    private BooleanExpression targetPositionNameContains(String targetPositionName) {
        return StringUtils.hasText(targetPositionName) ? qSubTransferCommand.targetPositionName.contains(targetPositionName) : null;
    }

    private BooleanExpression loadTypeEq(String loadType) {
        return StringUtils.hasText(loadType) ? qSubTransferCommand.loadType.equalsIgnoreCase(loadType) : null;
    }
}
