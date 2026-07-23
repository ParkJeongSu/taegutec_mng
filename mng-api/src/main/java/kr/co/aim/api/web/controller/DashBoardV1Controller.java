package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.dto.DashboardResponseDto;
import kr.co.aim.api.dto.EquipmentGroupDashboard;
import kr.co.aim.api.service.DashboardService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "MNG Dashboard", description = "MNG Dashboard 관련 API")
@RestController
@RequestMapping("/api/v1/mng/dashboard")
@RequiredArgsConstructor
@Slf4j
@Profile("web")
@ResponseAnnotation
public class DashBoardV1Controller {
    private final DashboardService dashboardService;

    @Operation(summary = "Dashboard", description = "Dashboard 조회")
    @GetMapping("")
    public ResponseEntity<Page<DashboardResponseDto>> getDashboardInfo(
    ) {
        return ResponseEntity.ok(dashboardService.getDashboardInfo());
    }

    @Operation(summary = "Equipment Dashboard", description = "Equipment Dashboard 조회")
    @GetMapping("/equipment")
    public ResponseEntity<Page<EquipmentGroupDashboard>> getEquipmentGroupForDashboard(
            @org.springdoc.core.annotations.ParameterObject
            Pageable pageable
    ) {
        Page<EquipmentGroupDashboard> reuslt = dashboardService.getEquipmentDataForDashboard(pageable);
        return ResponseEntity.ok(reuslt);
    }

}
