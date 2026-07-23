package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.Utils.QueryDslUtils;
import kr.co.aim.common.condition.TransportJobHistorySearchCondition;
import kr.co.aim.domain.model.TransportJob;
import kr.co.aim.domain.model.TransportJobHistory;
import kr.co.aim.domain.repository.TransportJobRepository;
import kr.co.aim.infra.persistence.entity.TransportJobEntity;
import kr.co.aim.infra.persistence.entity.TransportJobHistoryEntity;
import kr.co.aim.infra.persistence.mapper.TransportJobMapper;
import kr.co.aim.infra.persistence.springdatajpa.TransportJobJpaRepository;
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
import java.util.Optional;
import java.util.stream.Collectors;

import static kr.co.aim.infra.persistence.entity.QTransportJobHistoryEntity.transportJobHistoryEntity;

@Repository
@RequiredArgsConstructor
public class TransportJobRepositoryImpl implements TransportJobRepository {
    // Spring Data JPA가 자동으로 구현해주는 JPA 리포지토리. UserEntity를 다룬다.
    private final TransportJobJpaRepository transportJobJpaRepository;
    private final TransportJobMapper transportJobMapper;
    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입


    @Override
    public TransportJob save(TransportJob transportJob) {
        // 1. Domain -> Entity 변환
        TransportJobEntity entity = transportJobMapper.toEntity(transportJob);
        // 2. JPA 리포지토리를 통해 DB에 저장
        TransportJobEntity savedEntity = transportJobJpaRepository.save(entity);
        // 3. 저장된 Entity -> Domain 변환 후 반환
        return transportJobMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<TransportJob> findById(Long id) {
        // 1. JPA 리포지토리를 통해 ID로 Entity 조회
        Optional<TransportJobEntity> entityOptional = transportJobJpaRepository.findById(id);
        // 2. 조회된 Optional<Entity>를 Optional<Domain>으로 변환하여 반환
        return entityOptional.map(transportJobMapper::toDomain);
    }

    @Override
    public Optional<TransportJob> findByTransportJobName(String transportJobName) {
        return transportJobJpaRepository.findByTransportJobName(transportJobName).map(transportJobMapper::toDomain);
    }

    @Override
    public List<TransportJob> findAll() {
        // 1. JPA 리포지토리를 통해 모든 UserEntity 조회
        List<TransportJobEntity> entities = transportJobJpaRepository.findAll();
        // 2. Entity 리스트를 Domain 객체 리스트로 변환하여 반환
        return entities.stream()
                .map(transportJobMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        transportJobJpaRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public List<TransportJob> findByCarrierNameAndTransportJobStateIn(String carrierName, List<String> transportJobStates) {
        return transportJobJpaRepository.findByCarrierNameAndTransportJobStateIn(carrierName,transportJobStates).stream().map(transportJobMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<TransportJob> findByDestinationEquipmentNameAndDestinationPortNameAndTransportJobStateIn(
            String destinationEquipmentName,
            String destinationPortName,
            List<String> transportJobStates) {
        return transportJobJpaRepository.findByDestinationEquipmentNameAndDestinationPortNameAndTransportJobStateIn(destinationEquipmentName,destinationPortName,transportJobStates).stream().map(transportJobMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<TransportJob> findWithLockByTransportJobName(String transportJobName) {
        return transportJobJpaRepository.findWithLockByTransportJobName(transportJobName).map(transportJobMapper::toDomain);
    }

    @Override
    public Page<TransportJobHistory> findTransportJobHistoryByCondition(TransportJobHistorySearchCondition condition, Pageable pageable) {
        // 1. 공통 쿼리 빌더 생성 (SELECT, FROM, JOIN, WHERE)
        JPAQuery<TransportJobHistoryEntity> query = queryFactory
                .selectFrom(transportJobHistoryEntity)
                .where(
                        transportJobNameContains(condition.getTransportJobName()),
                        carrierNameContains(condition.getCarrierName()),
                        transportTypeContains(condition.getTransportType()),
                        transportJobStateContains(condition.getTransportJobState()),
                        carrierTypeContains(condition.getCarrierType()),
                        travelProfileContains(condition.getTravelProfile()),
                        sourceEquipmentNameContains(condition.getSourceEquipmentName()),
                        sourcePortNameContains(condition.getSourcePortName()),
                        sourceZoneNameContains(condition.getSourceZoneName()),
                        sourcePositionTypeNameContains(condition.getSourcePositionTypeName()),
                        sourcePositionNameContains(condition.getSourcePositionName()),
                        destinationEquipmentNameContains(condition.getDestinationEquipmentName()),
                        destinationPortNameContains(condition.getDestinationPortName()),
                        destinationZoneNameContains(condition.getDestinationZoneName()),
                        destinationPositionTypeNameContains(condition.getDestinationPositionTypeName()),
                        destinationPositionNameContains(condition.getDestinationPositionName()),
                        priorityEq(condition.getPriority()),
                        errorCodeContains(condition.getErrorCode()),
                        errorTextContains(condition.getErrorText()),
                        requestSourceContains(condition.getRequestSource()),
                        createTimeEq(condition.getCreateTime()),
                        departedTimeEq(condition.getDepartedTime()),
                        arrivedTimeEq(condition.getArrivedTime()),
                        reasonCodeContains(condition.getReasonCode()),
                        orderIdContains(condition.getOrderId()),
                        eventTimeBetween(condition.getFromEventTime(), condition.getToEventTime())
                );

        // 2. 정렬 적용
        query.orderBy(getOrderSpecifiers(pageable.getSort()));

        // 3. 페이징 적용 (isPaged()로 분기)
        if (pageable.isPaged()) {
            query.offset(pageable.getOffset());
            query.limit(pageable.getPageSize());
        }

        // 4. 데이터 조회
        List<TransportJobHistoryEntity> content = query.fetch();

        List<TransportJobHistory> converted = content.stream().map(transportJobMapper::toDomain).collect(Collectors.toList());

        // 5. 카운트 조회 (isPaged()로 분기)
        long total;
        if (pageable.isPaged()) {
            // [페이징 O] 별도 카운트 쿼리 실행
            Long count = queryFactory
                    .select(transportJobHistoryEntity.count())
                    .from(transportJobHistoryEntity)
                    .where(
                            transportJobNameContains(condition.getTransportJobName()),
                            carrierNameContains(condition.getCarrierName()),
                            transportTypeContains(condition.getTransportType()),
                            transportJobStateContains(condition.getTransportJobState()),
                            carrierTypeContains(condition.getCarrierType()),
                            travelProfileContains(condition.getTravelProfile()),
                            sourceEquipmentNameContains(condition.getSourceEquipmentName()),
                            sourcePortNameContains(condition.getSourcePortName()),
                            sourceZoneNameContains(condition.getSourceZoneName()),
                            sourcePositionTypeNameContains(condition.getSourcePositionTypeName()),
                            sourcePositionNameContains(condition.getSourcePositionName()),
                            destinationEquipmentNameContains(condition.getDestinationEquipmentName()),
                            destinationPortNameContains(condition.getDestinationPortName()),
                            destinationZoneNameContains(condition.getDestinationZoneName()),
                            destinationPositionTypeNameContains(condition.getDestinationPositionTypeName()),
                            destinationPositionNameContains(condition.getDestinationPositionName()),
                            priorityEq(condition.getPriority()),
                            errorCodeContains(condition.getErrorCode()),
                            errorTextContains(condition.getErrorText()),
                            requestSourceContains(condition.getRequestSource()),
                            createTimeEq(condition.getCreateTime()),
                            departedTimeEq(condition.getDepartedTime()),
                            arrivedTimeEq(condition.getArrivedTime()),
                            reasonCodeContains(condition.getReasonCode()),
                            orderIdContains(condition.getOrderId()),
                            eventTimeBetween(condition.getFromEventTime(), condition.getToEventTime())
                    )
                    .fetchOne();

            total = (count != null) ? count.longValue() : 0L;

        } else {
            // [페이징 X] .unpaged() 일 때
            total = content.size();
        }

        // 6. PageImpl 반환
        return new PageImpl<>(converted, pageable, total);
    }

    @Override
    public List<TransportJob> findByCreateTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return transportJobJpaRepository.findByCreateTimeBetween(startDateTime,endDateTime).stream().map(transportJobMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<TransportJob> findByCreateTimeBetweenAndTransportJobState(LocalDateTime startDateTime, LocalDateTime endDateTime, String transportJobState) {
        return transportJobJpaRepository.findByCreateTimeBetweenAndTransportJobState(startDateTime,endDateTime,transportJobState).stream().map(transportJobMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<TransportJob> findByOrderId(String orderId) {
        return transportJobJpaRepository.findByOrderId(orderId).map(transportJobMapper::toDomain);
    }

    /**
     * Pageable의 Sort 객체를 Querydsl의 OrderSpecifier 배열로 변환합니다.
     */
    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();

        if (sort != null && sort.isSorted()) {
            for (Sort.Order order : sort) {
                String property = order.getProperty();

                // [핵심] property가 null이 아니고, 공백이 아닐 때만 정렬을 추가합니다.
                if (StringUtils.hasText(property) && QueryDslUtils.isValidProperty(property)) {
                    Order direction = order.isAscending() ? Order.ASC : Order.DESC;

                    PathBuilder pathBuilder = new PathBuilder<>(
                            transportJobHistoryEntity.getType(),
                            transportJobHistoryEntity.getMetadata()
                    );

                    orders.add(new OrderSpecifier(direction, pathBuilder.get(property)));
                }
            }
        }

        // 유효한 정렬 필드가 하나도 없었다면 (스웨거에서 잘못 보낸 경우 포함) 기본값 적용
        if (orders.isEmpty()) {
            orders.add(new OrderSpecifier(Order.DESC, transportJobHistoryEntity.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }

    // == 동적 쿼리를 위한 BooleanExpression 메소드들 ==

    private BooleanExpression transportJobNameContains(String transportJobName) {
        return StringUtils.hasText(transportJobName) ? transportJobHistoryEntity.transportJobName.contains(transportJobName) : null;
    }

    private BooleanExpression carrierNameContains(String carrierName) {
        return StringUtils.hasText(carrierName) ? transportJobHistoryEntity.carrierName.contains(carrierName) : null;
    }

    private BooleanExpression transportTypeContains(String transportType) {
        return StringUtils.hasText(transportType) ? transportJobHistoryEntity.transportType.contains(transportType) : null;
    }

    private BooleanExpression transportJobStateContains(String transportJobState) {
        return StringUtils.hasText(transportJobState) ? transportJobHistoryEntity.transportJobState.contains(transportJobState) : null;
    }

    private BooleanExpression carrierTypeContains(String carrierType) {
        return StringUtils.hasText(carrierType) ? transportJobHistoryEntity.carrierType.contains(carrierType) : null;
    }

    private BooleanExpression travelProfileContains(String travelProfile) {
        return StringUtils.hasText(travelProfile) ? transportJobHistoryEntity.travelProfile.contains(travelProfile) : null;
    }

    private BooleanExpression sourceEquipmentNameContains(String sourceEquipmentName) {
        return StringUtils.hasText(sourceEquipmentName) ? transportJobHistoryEntity.sourceEquipmentName.contains(sourceEquipmentName) : null;
    }

    private BooleanExpression sourcePortNameContains(String sourcePortName) {
        return StringUtils.hasText(sourcePortName) ? transportJobHistoryEntity.sourcePortName.contains(sourcePortName) : null;
    }

    private BooleanExpression sourceZoneNameContains(String sourceZoneName) {
        return StringUtils.hasText(sourceZoneName) ? transportJobHistoryEntity.sourceZoneName.contains(sourceZoneName) : null;
    }

    private BooleanExpression sourcePositionTypeNameContains(String sourcePositionTypeName) {
        return StringUtils.hasText(sourcePositionTypeName) ? transportJobHistoryEntity.sourcePositionTypeName.contains(sourcePositionTypeName) : null;
    }

    private BooleanExpression sourcePositionNameContains(String sourcePositionName) {
        return StringUtils.hasText(sourcePositionName) ? transportJobHistoryEntity.sourcePositionName.contains(sourcePositionName) : null;
    }

    private BooleanExpression destinationEquipmentNameContains(String destinationEquipmentName) {
        return StringUtils.hasText(destinationEquipmentName) ? transportJobHistoryEntity.destinationEquipmentName.contains(destinationEquipmentName) : null;
    }

    private BooleanExpression destinationPortNameContains(String destinationPortName) {
        return StringUtils.hasText(destinationPortName) ? transportJobHistoryEntity.destinationPortName.contains(destinationPortName) : null;
    }

    private BooleanExpression destinationZoneNameContains(String destinationZoneName) {
        return StringUtils.hasText(destinationZoneName) ? transportJobHistoryEntity.destinationZoneName.contains(destinationZoneName) : null;
    }

    private BooleanExpression destinationPositionTypeNameContains(String destinationPositionTypeName) {
        return StringUtils.hasText(destinationPositionTypeName) ? transportJobHistoryEntity.destinationPositionTypeName.contains(destinationPositionTypeName) : null;
    }

    private BooleanExpression destinationPositionNameContains(String destinationPositionName) {
        return StringUtils.hasText(destinationPositionName) ? transportJobHistoryEntity.destinationPositionName.contains(destinationPositionName) : null;
    }

    private BooleanExpression priorityEq(Integer priority) {
        return priority != null ? transportJobHistoryEntity.priority.eq(priority) : null;
    }

    private BooleanExpression errorCodeContains(String errorCode) {
        return StringUtils.hasText(errorCode) ? transportJobHistoryEntity.errorCode.contains(errorCode) : null;
    }

    private BooleanExpression errorTextContains(String errorText) {
        return StringUtils.hasText(errorText) ? transportJobHistoryEntity.errorText.contains(errorText) : null;
    }

    private BooleanExpression requestSourceContains(String requestSource) {
        return StringUtils.hasText(requestSource) ? transportJobHistoryEntity.requestSource.contains(requestSource) : null;
    }

    private BooleanExpression createTimeEq(LocalDateTime createTime) {
        return createTime != null ? transportJobHistoryEntity.createTime.eq(createTime) : null;
    }

    private BooleanExpression departedTimeEq(LocalDateTime departedTime) {
        return departedTime != null ? transportJobHistoryEntity.departedTime.eq(departedTime) : null;
    }

    private BooleanExpression arrivedTimeEq(LocalDateTime arrivedTime) {
        return arrivedTime != null ? transportJobHistoryEntity.arrivedTime.eq(arrivedTime) : null;
    }

    private BooleanExpression reasonCodeContains(String reasonCode) {
        return StringUtils.hasText(reasonCode) ? transportJobHistoryEntity.reasonCode.contains(reasonCode) : null;
    }

    private BooleanExpression orderIdContains(String orderId) {
        return StringUtils.hasText(orderId) ? transportJobHistoryEntity.orderId.contains(orderId) : null;
    }

    private BooleanExpression eventTimeBetween(LocalDateTime fromEventTime, LocalDateTime toEventTime) {
        if (fromEventTime != null && toEventTime != null) {
            return transportJobHistoryEntity.eventTime.between(fromEventTime, toEventTime);
        }
        if (fromEventTime != null) {
            return transportJobHistoryEntity.eventTime.goe(fromEventTime);
        }
        if (toEventTime != null) {
            return transportJobHistoryEntity.eventTime.loe(toEventTime);
        }
        return null;
    }
}
