package kr.co.aim.domain.repository;

import kr.co.aim.common.condition.EquipmentAvailabilityHourlySearchCondition;
import kr.co.aim.common.condition.EquipmentProductivityDailySearchCondition;
import kr.co.aim.common.condition.TransportRouteDailySearchCondition;
import kr.co.aim.common.condition.WorkOrderProcessedDailySearchCondition;
import kr.co.aim.domain.model.EquipmentAvailabilityHourly;
import kr.co.aim.domain.model.EquipmentProductivityDaily;
import kr.co.aim.domain.model.TransportRouteDaily;
import kr.co.aim.domain.model.WorkOrderProcessedDaily;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StatRepository {

    // 1. 설비 가동 이력 집계 대상 조회 및 저장
    void saveAvailabilityAll(List<EquipmentAvailabilityHourly> list);

    // 2. 생산성 실적 집계 및 저장
    void calculateAndSaveProductivity(String statDate);

    // 3. 반송 경로 실적 집계 및 저장
    void calculateAndSaveTransportRoute(String statDate);

    // 4. 작업 오더 최종 마감 집계 및 저장
    void calculateAndSaveWorkOrderProcessed(String statDate);

    // == 조건별 조회 페이징 메서드 ==
    Page<EquipmentAvailabilityHourly> findAvailabilityWithConditions(EquipmentAvailabilityHourlySearchCondition condition, Pageable pageable);

    Page<EquipmentProductivityDaily> findProductivityWithConditions(EquipmentProductivityDailySearchCondition condition, Pageable pageable);

    Page<TransportRouteDaily> findTransportRouteWithConditions(TransportRouteDailySearchCondition condition, Pageable pageable);

    Page<WorkOrderProcessedDaily> findWorkOrderProcessedWithConditions(WorkOrderProcessedDailySearchCondition condition, Pageable pageable);
}
