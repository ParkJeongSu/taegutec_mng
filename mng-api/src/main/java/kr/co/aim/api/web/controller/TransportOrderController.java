package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.service.TransportOrderService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.common.condition.TransportOrderSearchCondition;
import kr.co.aim.common.dto.insert.TransportOrderStatisticsResponse;
import kr.co.aim.common.dto.insert.WorkStationTransportCountResponse;
import kr.co.aim.domain.model.TransportOrder;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;

@Tag(name = "MNG Transport Order", description = "기준정보 Transport Order 관리 API")
@RestController
@RequestMapping("/api/v1/mng/transport-order")
@RequiredArgsConstructor
@Profile("web")
@ResponseAnnotation
public class TransportOrderController {

    private final TransportOrderService transportOrderService;

    @Operation(summary = "Transport Order 목록 조회")
    @GetMapping("")
    public ResponseEntity<Page<TransportOrder>> getTransportOrders(
            TransportOrderSearchCondition condition,
            @ParameterObject Pageable pageable
    ) {
        Page<TransportOrder> result = transportOrderService.findTransportOrderWithConditions(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Transport Order 상세 조회")
    @GetMapping("/{id}")
    public ResponseEntity<Page<TransportOrder>> getTransportOrder(@PathVariable("id") Long id) {
        TransportOrder result = transportOrderService.findById(id);
        return ResponseEntity.ok(new PageImpl<>(Collections.singletonList(result), PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "WorkStation별 반송 오더 통계 현황 조회")
    @GetMapping("/statistics/daily-workstation")
    public ResponseEntity<Page<TransportOrderStatisticsResponse>> getWorkStationStatistics(
            @Parameter(description = "워크스테이션 ID", example = "341", required = true)
            @RequestParam(name = "work-station-id") String workStationId,

            @Parameter(description = "조회 일자 (미입력 시 당일)", example = "2026-08-18")
            @RequestParam(name = "target-date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate
    ) {
        LocalDate date = (targetDate != null) ? targetDate : LocalDate.now();
        TransportOrderStatisticsResponse result = transportOrderService.getWorkStationStatistics(workStationId, date);
        Page<TransportOrderStatisticsResponse> pageResult = new PageImpl<>(
                Collections.singletonList(result),
                PageRequest.of(0, 1),
                1
        );
        return ResponseEntity.ok(pageResult);
    }

    @Operation(summary = "WorkStation별 최신 Transport Order 목록 조회", description = "WorkStation 및 Transport Type(I/O) 기준으로 최신 n개의 오더를 조회합니다.")
    @GetMapping("/recent")
    public ResponseEntity<Page<TransportOrder>> getRecentTransportOrders(
            @Parameter(description = "워크스테이션 ID", example = "341", required = true)
            @RequestParam(name = "work-station-id") String workStationId,

            @Parameter(description = "반송 구분 (I: Inbound, O: Outbound)", example = "I", required = true)
            @RequestParam(name = "transport-type") String transportType,

            @Parameter(description = "조회할 데이터 건수 (n개)", example = "5")
            @RequestParam(name = "limit", defaultValue = "5") int limit
    ) {
        Page<TransportOrder> result = transportOrderService.findRecentTransportOrders(workStationId, transportType, limit);
        return ResponseEntity.ok(result);
    }


    @Operation(summary = "전체 WorkStation별 Inbound/Outbound 오더 수량 집계 (페이징)", description = "지정된 일자(미입력 시 당일) 기준으로 전체 WorkStation의 Inbound, Outbound 수량을 페이징 조회합니다.")
    @GetMapping("/statistics/workstations/counts")
    public ResponseEntity<Page<WorkStationTransportCountResponse>> getWorkStationTransportCounts(
            @Parameter(description = "조회 일자 (미입력 시 당일)", example = "2026-08-18")
            @RequestParam(name = "target-date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate,
            @ParameterObject Pageable pageable
    ) {
        LocalDate date = (targetDate != null) ? targetDate : LocalDate.now();
        Page<WorkStationTransportCountResponse> result = transportOrderService.getWorkStationTransportCounts(date, pageable);
        return ResponseEntity.ok(result);
    }

}