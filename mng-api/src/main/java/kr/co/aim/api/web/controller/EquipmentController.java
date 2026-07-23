package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.dto.EquipmentGroupDashboard;
import kr.co.aim.api.service.DashboardService;
import kr.co.aim.api.service.EquipmentService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.common.condition.EquipmentSearchCondition;
import kr.co.aim.domain.model.Equipment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "MNG Equipment", description = "MNG Equipmnent 관련 API")
@RestController
@RequestMapping("/api/v1/mng/equipment")
@RequiredArgsConstructor
@Slf4j
@ResponseAnnotation
@Profile("web")
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public class EquipmentController {
    private final EquipmentService equipmentService;

    @Operation(summary = "Equipment", description = "Equipment 조회")
    @GetMapping("")
    public ResponseEntity<Page<Equipment>> getEquipmentList(
            EquipmentSearchCondition condition,
            @org.springdoc.core.annotations.ParameterObject
            Pageable pageable
    ) {
        Page<Equipment> reuslt = equipmentService.findEquipmentByCondition(condition, pageable);
        return ResponseEntity.ok(reuslt);
    }

}
