package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.service.StatService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.common.condition.EquipmentAvailabilityHourlySearchCondition;
import kr.co.aim.common.condition.EquipmentProductivityDailySearchCondition;
import kr.co.aim.common.condition.TransportRouteDailySearchCondition;
import kr.co.aim.common.condition.WorkOrderProcessedDailySearchCondition;
import kr.co.aim.domain.model.EquipmentAvailabilityHourly;
import kr.co.aim.domain.model.EquipmentProductivityDaily;
import kr.co.aim.domain.model.TransportRouteDaily;
import kr.co.aim.domain.model.WorkOrderProcessedDaily;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MNG Statistics", description = "기준정보 및 실적 통계 관리 API")
@RestController
@RequestMapping("/api/v1/mng/stat")
@RequiredArgsConstructor
@Profile("web")
@ResponseAnnotation
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public class StatController {

    private final StatService statService;

    @Operation(summary = "시간별 설비 가동 통계 조회")
    @GetMapping("/availability-hourly")
    public ResponseEntity<Page<EquipmentAvailabilityHourly>> getAvailabilityHourly(
            EquipmentAvailabilityHourlySearchCondition condition,
            @ParameterObject Pageable pageable
    ) {
        Page<EquipmentAvailabilityHourly> result = statService.findAvailabilityWithConditions(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "일별 설비 생산성 통계 조회")
    @GetMapping("/productivity-daily")
    public ResponseEntity<Page<EquipmentProductivityDaily>> getProductivityDaily(
            EquipmentProductivityDailySearchCondition condition,
            @ParameterObject Pageable pageable
    ) {
        Page<EquipmentProductivityDaily> result = statService.findProductivityWithConditions(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "일별 반송 경로 통계 조회")
    @GetMapping("/transport-route-daily")
    public ResponseEntity<Page<TransportRouteDaily>> getTransportRouteDaily(
            TransportRouteDailySearchCondition condition,
            @ParameterObject Pageable pageable
    ) {
        Page<TransportRouteDaily> result = statService.findTransportRouteWithConditions(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "일별 작업 오더 마감 통계 조회")
    @GetMapping("/work-order-processed-daily")
    public ResponseEntity<Page<WorkOrderProcessedDaily>> getWorkOrderProcessedDaily(
            WorkOrderProcessedDailySearchCondition condition,
            @ParameterObject Pageable pageable
    ) {
        Page<WorkOrderProcessedDaily> result = statService.findWorkOrderProcessedWithConditions(condition, pageable);
        return ResponseEntity.ok(result);
    }

//    @Operation(summary = "특정 일자 통계 배치 수동 집계 실행")
//    @PostMapping("/aggregate/{statDate}")
//    public ResponseEntity<String> aggregateDailyStatistics(@PathVariable("statDate") String statDate) {
//        statService.aggregateDailyStatistics(statDate);
//        return ResponseEntity.ok("SUCCESS");
//    }
}