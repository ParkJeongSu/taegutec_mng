package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.condition.WcsTransferCommandHistorySearchCondition;
import kr.co.aim.domain.repository.WcsTransferCommandHistoryRepository;
import kr.co.aim.infra.persistence.entity.QWcsTransferCommandHistoryEntity;
import kr.co.aim.infra.persistence.entity.WcsTransferCommandHistoryEntity;
import kr.co.aim.infra.persistence.springdatajpa.WcsTransferCommandHistoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class WcsTransferCommandHistoryRepositoryImpl implements WcsTransferCommandHistoryRepository {

    private final WcsTransferCommandHistoryJpaRepository wcsTransferCommandHistoryJpaRepository;
    private final JPAQueryFactory queryFactory;

    private static final QWcsTransferCommandHistoryEntity qHistory = QWcsTransferCommandHistoryEntity.wcsTransferCommandHistoryEntity;

    @Override
    public Page<WcsTransferCommandHistoryEntity> findHistory(WcsTransferCommandHistorySearchCondition condition, Pageable pageable) {
        String transferCommandName = (condition != null) ? condition.getTransferCommandName() : null;
        String carrierName = (condition != null) ? condition.getCarrierName() : null;
        String commandStatus = (condition != null) ? condition.getCommandStatus() : null;
        String currentEquipmentName = (condition != null) ? condition.getCurrentEquipmentName() : null;
        String orderType = (condition != null) ? condition.getOrderType() : null;
        String eventUser = (condition != null) ? condition.getEventUser() : null;
        LocalDateTime startDate = (condition != null) ? condition.getStartDate() : null;
        LocalDateTime endDate = (condition != null) ? condition.getEndDate() : null;

        JPAQuery<WcsTransferCommandHistoryEntity> query = queryFactory
                .selectFrom(qHistory)
                .where(
                        transferCommandNameContains(transferCommandName),
                        carrierNameContains(carrierName),
                        commandStatusEq(commandStatus),
                        currentEquipmentNameContains(currentEquipmentName),
                        orderTypeEq(orderType),
                        eventUserContains(eventUser),
                        eventTimeBetween(startDate, endDate)
                );

        query.orderBy(getOrderSpecifiers(pageable != null ? pageable.getSort() : Sort.unsorted()));

        if (pageable != null && pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        List<WcsTransferCommandHistoryEntity> content = query.fetch();
        if (content == null) {
            content = new ArrayList<>();
        }

        long total;
        if (pageable != null && pageable.isPaged()) {
            Long count = queryFactory
                    .select(qHistory.count())
                    .from(qHistory)
                    .where(
                            transferCommandNameContains(transferCommandName),
                            carrierNameContains(carrierName),
                            commandStatusEq(commandStatus),
                            currentEquipmentNameContains(currentEquipmentName),
                            orderTypeEq(orderType),
                            eventUserContains(eventUser),
                            eventTimeBetween(startDate, endDate)
                    )
                    .fetchOne();
            total = (count != null) ? count.longValue() : 0L;
        } else {
            total = content.size();
        }

        return new PageImpl<>(content, pageable != null ? pageable : Pageable.unpaged(), total);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();

        if (sort != null && sort.isSorted()) {
            for (Sort.Order order : sort) {
                Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                PathBuilder pathBuilder = new PathBuilder<>(qHistory.getType(), qHistory.getMetadata());
                orders.add(new OrderSpecifier(direction, pathBuilder.get(order.getProperty())));
            }
        }

        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, qHistory.eventTimeKey));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression transferCommandNameContains(String transferCommandName) {
        return StringUtils.hasText(transferCommandName) ? qHistory.transferCommandName.contains(transferCommandName) : null;
    }

    private BooleanExpression carrierNameContains(String carrierName) {
        return StringUtils.hasText(carrierName) ? qHistory.carrierName.contains(carrierName) : null;
    }

    private BooleanExpression commandStatusEq(String commandStatus) {
        return StringUtils.hasText(commandStatus) ? qHistory.commandStatus.equalsIgnoreCase(commandStatus) : null;
    }

    private BooleanExpression currentEquipmentNameContains(String currentEquipmentName) {
        return StringUtils.hasText(currentEquipmentName) ? qHistory.currentEquipmentName.contains(currentEquipmentName) : null;
    }

    private BooleanExpression orderTypeEq(String orderType) {
        return StringUtils.hasText(orderType) ? qHistory.orderType.equalsIgnoreCase(orderType) : null;
    }

    private BooleanExpression eventUserContains(String eventUser) {
        return StringUtils.hasText(eventUser) ? qHistory.eventUser.contains(eventUser) : null;
    }

    private BooleanExpression eventTimeBetween(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null && endDate != null) {
            return qHistory.eventTime.between(startDate, endDate);
        }
        if (startDate != null) {
            return qHistory.eventTime.goe(startDate);
        }
        if (endDate != null) {
            return qHistory.eventTime.loe(endDate);
        }
        return null;
    }
}
