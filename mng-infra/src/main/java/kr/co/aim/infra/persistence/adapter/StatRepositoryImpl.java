package kr.co.aim.infra.persistence.adapter;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.aim.common.Utils.StatTimeUtils;
import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.domain.model.EquipmentAvailabilityHourly;
import kr.co.aim.domain.repository.StatRepository;
import kr.co.aim.infra.persistence.entity.*;
import kr.co.aim.infra.persistence.mapper.*;
import kr.co.aim.infra.persistence.springdatajpa.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static kr.co.aim.infra.persistence.entity.QEquipmentProductivityDailyEntity.equipmentProductivityDailyEntity;
import static kr.co.aim.infra.persistence.entity.QProductionOrderHistoryEntity.productionOrderHistoryEntity;
import static kr.co.aim.infra.persistence.entity.QTransportJobHistoryEntity.transportJobHistoryEntity;

/**
 * UserRepository의 JPA 기반 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 JpaRepository에 위임합니다.
 */

@Repository
@RequiredArgsConstructor
public class StatRepositoryImpl implements StatRepository {

    private final EquipmentAvailabilityHourlyJpaRepository equipmentAvailabilityHourlyJpaRepository;
    private final EquipmentProductivityDailyJpaRepository equipmentProductivityDailyJpaRepository;
    private final TransportRouteDailyJpaRepository  transportRouteDailyJpaRepository;
    private final WorkOrderProcessedDailyJpaRepository workOrderProcessedDailyJpaRepository;

    private final EquipmentAvailabilityHourlyMapper  equipmentAvailabilityHourlyMapper;
    private final EquipmentProductivityDailyMapper   equipmentProductivityDailyMapper;
    private final TransportRouteDailyMapper transportRouteDailyMapper;
    private final WorkOrderProcessedDailyMapper  workOrderProcessedDailyMapper;

    private final JPAQueryFactory queryFactory; // ✨ JPAQueryFactory 주입

    /**
     * 1) 설비 가동 시간 통계 저장 (자바 가공 리스트 일괄 저장)
     */

    @Override
    public void saveAvailabilityAll(List<EquipmentAvailabilityHourly> list) {
        List<EquipmentAvailabilityHourlyEntity> entities = new ArrayList<>();
        for (EquipmentAvailabilityHourly domain : list) {
            entities.add(equipmentAvailabilityHourlyMapper.toEntity(domain));
        }
        equipmentAvailabilityHourlyJpaRepository.saveAll(entities);
    }

    /**
     * 2) STAT_EQP_PRODUCTIVITY_DAILY 집계
     * PRODUCTION_ORDER_HISTORY를 Group By 연산하여 대량 Insert/Upsert 형태로 처리합니다.
     */
    @Override
    public void calculateAndSaveProductivity(String statDate) {
        // Querydsl을 사용하여 특정 날짜에 종료(COMPLETE_TIME)된 데이터를 설비별로 집계조회
        // 타겟팅 포맷팅 기법을 사용하여 쿼리 조건 생성
        LocalDateTime startOfDay = StatTimeUtils.toStartDateTime(statDate);
        LocalDateTime endOfDay = StatTimeUtils.toEndDateTime(statDate);

        // 💡 별칭(Alias) 지정을 위한 수량 경로 변수 선언
        com.querydsl.core.types.dsl.NumberPath<java.math.BigDecimal> totalProcessedQtyAlias =
                com.querydsl.core.types.dsl.Expressions.numberPath(java.math.BigDecimal.class, "totalProcessedQty");
        com.querydsl.core.types.dsl.NumberPath<java.math.BigDecimal> scrappedQtyAlias =
                com.querydsl.core.types.dsl.Expressions.numberPath(java.math.BigDecimal.class, "scrappedQty");

        List<Tuple> results = queryFactory
                .select(
                        productionOrderHistoryEntity.equipmentName,
                        productionOrderHistoryEntity.count(), // TOTAL_PROCESSED_COUNT
                        productionOrderHistoryEntity.endedQuantity.sum().as(totalProcessedQtyAlias), // TOTAL_PROCESSED_QUANTITY
                        productionOrderHistoryEntity.scrappedQuantity.sum().as(scrappedQtyAlias)
                )
                .from(productionOrderHistoryEntity)
                .where(productionOrderHistoryEntity.completeTime.between(startOfDay, endOfDay))
                .groupBy( productionOrderHistoryEntity.equipmentName)
                .fetch();

        List<EquipmentProductivityDailyEntity> entities = new ArrayList<>();
        for (Tuple tuple : results) {
            String eqpName = tuple.get(productionOrderHistoryEntity.equipmentName);
            Long totalCount = tuple.get(productionOrderHistoryEntity.count());
            // 💡 위에서 선언한 Alias 변수를 key로 사용하여 정밀한 BigDecimal 수량 추출
            java.math.BigDecimal totalProcessedQty = tuple.get(totalProcessedQtyAlias);
            java.math.BigDecimal scrappedQty = tuple.get(scrappedQtyAlias);

            EquipmentProductivityDailyEntity entity = new EquipmentProductivityDailyEntity(
                    TsidUtils.nextId(),
                    statDate,
                    eqpName,
                    totalCount != null ? totalCount.intValue() : 0,
                    totalProcessedQty, totalProcessedQty, scrappedQty, 0 // 정밀 비즈니스 수량 가공은 필요에 맞춰 추가 바인딩
            );
            entities.add(entity);
        }
        equipmentProductivityDailyJpaRepository.saveAll(entities);
    }

    /**
     * 3) STAT_TRANSPORT_ROUTE_DAILY 집계
     * TRANSPORT_JOB_HISTORY 기준 반송 경로 통계 산출
     */
    @Override
    public void calculateAndSaveTransportRoute(String statDate) {
        LocalDateTime startOfDay = StatTimeUtils.toStartDateTime(statDate);
        LocalDateTime endOfDay = StatTimeUtils.toEndDateTime(statDate);

        List<Tuple> results = queryFactory
                .select(
                        transportJobHistoryEntity.sourceEquipmentName,
                        transportJobHistoryEntity.destinationEquipmentName,
                        transportJobHistoryEntity.count()
                        // AVG, MAX 등의 시간 차이 연산(ARRIVED_TIME - CREATE_TIME)은 DB 함수 또는 이력 분석을 기반으로 SELECT 절에 추가 가능합니다.
                )
                .from(transportJobHistoryEntity)
                .where(transportJobHistoryEntity.eventTime.between(startOfDay, endOfDay))
                .groupBy(transportJobHistoryEntity.sourceEquipmentName, transportJobHistoryEntity.destinationEquipmentName)
                .fetch();

        List<TransportRouteDailyEntity> entities = new ArrayList<>();
        for (Tuple tuple : results) {
            String srcName = tuple.get(transportJobHistoryEntity.sourceEquipmentName);
            String destName = tuple.get(transportJobHistoryEntity.destinationEquipmentName);
            Long totalCount = tuple.get(transportJobHistoryEntity.count());

            if (srcName != null && destName != null) {
                TransportRouteDailyEntity entity = new TransportRouteDailyEntity(
                        TsidUtils.nextId(),
                        statDate, srcName, destName,
                        totalCount != null ? totalCount.intValue() : 0,
                        0, 0, 0, 0, 0, 0, 0 // 시간 통계 바인딩
                );
                entities.add(entity);
            }
        }
        transportRouteDailyJpaRepository.saveAll(entities);
    }
    /**
     * 4) STAT_WORK_ORDER_PROCESSED_DAILY 집계
     * 상위 단에서 설계한 대로 대용량 HISTORY 대신 앞서 정산된 [STAT_EQP_PRODUCTIVITY_DAILY]를 롤업(Roll-up)하여 연산 효율 극대화
     */
    @Override
    public void calculateAndSaveWorkOrderProcessed(String statDate) {
        com.querydsl.core.types.dsl.NumberPath<java.math.BigDecimal> totalProcessedQtyAlias =
                com.querydsl.core.types.dsl.Expressions.numberPath(java.math.BigDecimal.class, "totalProcessedQty");
        List<Tuple> results = queryFactory
                .select(
                        equipmentProductivityDailyEntity.totalProcessedCount.sum(),
                        equipmentProductivityDailyEntity.totalProcessedQuantity.sum().as(totalProcessedQtyAlias)
                )
                .from(equipmentProductivityDailyEntity)
                .where(equipmentProductivityDailyEntity.statDate.eq(statDate))
                .fetch();

        if (!results.isEmpty()) {
            Tuple tuple = results.get(0);
            Integer sumCount = tuple.get(equipmentProductivityDailyEntity.totalProcessedCount.sum());
            java.math.BigDecimal totalProcessedQty = tuple.get(totalProcessedQtyAlias);

            if (sumCount != null) {
                WorkOrderProcessedDailyEntity entity = new WorkOrderProcessedDailyEntity(
                        TsidUtils.nextId(),
                        statDate,
                        sumCount,
                        0, // 평균 처리 시간 가공
                        totalProcessedQty != null ? totalProcessedQty : BigDecimal.ZERO
                );
                workOrderProcessedDailyJpaRepository.save(entity);
            }
        }
    }
}
