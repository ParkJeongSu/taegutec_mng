package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.aim.api.dto.WcsAlarmCreateRequestDto;
import kr.co.aim.api.dto.WcsAlarmResponse;
import kr.co.aim.api.dto.WcsAlarmUpdateRequestDto;
import kr.co.aim.api.service.WcsAlarmService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.common.condition.WcsAlarmSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "WCS Alarm", description = "WCS 설비 알람 정의 및 상태 관리 API")
@RestController
@Profile("web")
@ResponseAnnotation
@RequestMapping("/api/v1/wcs/alarm")
@RequiredArgsConstructor
public class WcsAlarmController {

    private final WcsAlarmService wcsAlarmService;

    /**
     * WCS 알람 조건별 목록 조회 (페이징 지원)
     */
    @Operation(summary = "WCS 알람 목록 조회", description = "동적 조건 및 페이징 처리를 통한 WCS 알람 목록 조회")
    @GetMapping
    public ResponseEntity<Page<WcsAlarmResponse>> findAlarms(
            WcsAlarmSearchCondition condition,
            Pageable pageable) {
        Page<WcsAlarmResponse> response = wcsAlarmService.findAlarms(condition, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 5개 복합키 기반 WCS 알람 단건 상세 조회
     */
    @Operation(summary = "WCS 알람 단건 상세 조회", description = "5개 복합키(factoryName, equipmentName, alarmId, layerNumber, layerType) 기반 단건 상세 조회")
    @GetMapping("/{factoryName}/{equipmentName}/{alarmId}/{layerNumber}/{layerType}")
    public ResponseEntity<WcsAlarmResponse> findById(
            @PathVariable String factoryName,
            @PathVariable String equipmentName,
            @PathVariable String alarmId,
            @PathVariable Integer layerNumber,
            @PathVariable String layerType) {
        WcsAlarmResponse response = wcsAlarmService.findById(factoryName, equipmentName, alarmId, layerNumber, layerType);
        return ResponseEntity.ok(response);
    }

    /**
     * 신규 WCS 알람 등록
     */
    @Operation(summary = "WCS 알람 등록", description = "신규 WCS 알람 정보 등록 및 이력 적재")
    @PostMapping
    public ResponseEntity<WcsAlarmResponse> createAlarm(
            @RequestBody @Valid WcsAlarmCreateRequestDto dto) {
        WcsAlarmResponse response = wcsAlarmService.createAlarm(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 5개 복합키 기반 WCS 알람 정보 수정
     */
    @Operation(summary = "WCS 알람 수정", description = "기존 WCS 알람 정보 수정 및 변경 이력 적재")
    @PutMapping("/{factoryName}/{equipmentName}/{alarmId}/{layerNumber}/{layerType}")
    public ResponseEntity<WcsAlarmResponse> updateAlarm(
            @PathVariable String factoryName,
            @PathVariable String equipmentName,
            @PathVariable String alarmId,
            @PathVariable Integer layerNumber,
            @PathVariable String layerType,
            @RequestBody @Valid WcsAlarmUpdateRequestDto dto) {
        WcsAlarmResponse response = wcsAlarmService.updateAlarm(factoryName, equipmentName, alarmId, layerNumber, layerType, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * 5개 복합키 기반 WCS 알람 삭제
     */
    @Operation(summary = "WCS 알람 삭제", description = "WCS 알람 삭제 및 삭제 이력 적재")
    @DeleteMapping("/{factoryName}/{equipmentName}/{alarmId}/{layerNumber}/{layerType}")
    public ResponseEntity<Void> deleteAlarm(
            @PathVariable String factoryName,
            @PathVariable String equipmentName,
            @PathVariable String alarmId,
            @PathVariable Integer layerNumber,
            @PathVariable String layerType,
            @RequestParam(required = false) String eventUser,
            @RequestParam(required = false) String eventComment) {
        wcsAlarmService.deleteAlarm(factoryName, equipmentName, alarmId, layerNumber, layerType, eventUser, eventComment);
        return ResponseEntity.noContent().build();
    }
}
