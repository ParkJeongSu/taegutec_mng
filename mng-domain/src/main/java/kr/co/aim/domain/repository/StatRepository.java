package kr.co.aim.domain.repository;

import kr.co.aim.domain.model.EquipmentAvailabilityHourly;

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
}
