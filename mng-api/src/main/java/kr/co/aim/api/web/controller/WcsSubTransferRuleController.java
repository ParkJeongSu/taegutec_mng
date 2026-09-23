package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.aim.api.dto.WcsSubTransferRuleCreateRequestDto;
import kr.co.aim.api.dto.WcsSubTransferRuleResponse;
import kr.co.aim.api.dto.WcsSubTransferRuleUpdateRequestDto;
import kr.co.aim.api.service.WcsSubTransferRuleService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.common.condition.WcsSubTransferRuleSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "WCS Sub Transfer Rule", description = "WCS 세부 반송 룰(SUB_TRANSFER_RULE) 기준정보 관리 API")
@RestController
@Profile("web")
@ResponseAnnotation
@RequestMapping("/api/v1/wcs/sub-transfer-rule")
@RequiredArgsConstructor
public class WcsSubTransferRuleController {

    private final WcsSubTransferRuleService wcsSubTransferRuleService;

    /**
     * WCS 세부 반송 룰 조건별 목록 조회 (페이징 지원)
     */
    @Operation(summary = "WCS 세부 반송 룰 목록 조회", description = "동적 조건 및 페이징 처리를 통한 WCS 세부 반송 룰 목록 조회")
    @GetMapping
    public ResponseEntity<Page<WcsSubTransferRuleResponse>> findSubTransferRules(
            WcsSubTransferRuleSearchCondition condition,
            Pageable pageable) {
        Page<WcsSubTransferRuleResponse> response = wcsSubTransferRuleService.findSubTransferRules(condition, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 4개 복합키(factoryName, equipmentName, moduleName, routeLinkId) 기반 WCS 세부 반송 룰 단건 상세 조회
     */
    @Operation(summary = "WCS 세부 반송 룰 단건 상세 조회", description = "4개 복합키 기반 단건 상세 조회")
    @GetMapping("/{factoryName}/{equipmentName}/{moduleName}/{routeLinkId}")
    public ResponseEntity<WcsSubTransferRuleResponse> findById(
            @PathVariable String factoryName,
            @PathVariable String equipmentName,
            @PathVariable String moduleName,
            @PathVariable Long routeLinkId) {
        WcsSubTransferRuleResponse response = wcsSubTransferRuleService.findById(
                factoryName, equipmentName, moduleName, routeLinkId);
        return ResponseEntity.ok(response);
    }

    /**
     * 공장 및 설비별 WCS 세부 반송 룰 목록 조회
     */
    @Operation(summary = "공장 및 설비별 세부 반송 룰 목록 조회", description = "공장 구분(factoryName) 및 설비 명(equipmentName)별 목록 조회")
    @GetMapping("/by-equipment/{factoryName}/{equipmentName}")
    public ResponseEntity<List<WcsSubTransferRuleResponse>> findByFactoryNameAndEquipmentName(
            @PathVariable String factoryName,
            @PathVariable String equipmentName) {
        List<WcsSubTransferRuleResponse> response = wcsSubTransferRuleService.findByFactoryNameAndEquipmentName(
                factoryName, equipmentName);
        return ResponseEntity.ok(response);
    }

    /**
     * 신규 WCS 세부 반송 룰 등록
     */
    @Operation(summary = "WCS 세부 반송 룰 등록", description = "신규 WCS 세부 반송 룰 정보 등록")
    @PostMapping
    public ResponseEntity<WcsSubTransferRuleResponse> createSubTransferRule(
            @RequestBody @Valid WcsSubTransferRuleCreateRequestDto dto) {
        WcsSubTransferRuleResponse response = wcsSubTransferRuleService.createSubTransferRule(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 4개 복합키 기반 WCS 세부 반송 룰 정보 수정
     */
    @Operation(summary = "WCS 세부 반송 룰 수정", description = "기존 WCS 세부 반송 룰 정보 수정")
    @PutMapping("/{factoryName}/{equipmentName}/{moduleName}/{routeLinkId}")
    public ResponseEntity<WcsSubTransferRuleResponse> updateSubTransferRule(
            @PathVariable String factoryName,
            @PathVariable String equipmentName,
            @PathVariable String moduleName,
            @PathVariable Long routeLinkId,
            @RequestBody @Valid WcsSubTransferRuleUpdateRequestDto dto) {
        WcsSubTransferRuleResponse response = wcsSubTransferRuleService.updateSubTransferRule(
                factoryName, equipmentName, moduleName, routeLinkId, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * 4개 복합키 기반 WCS 세부 반송 룰 삭제
     */
    @Operation(summary = "WCS 세부 반송 룰 삭제", description = "WCS 세부 반송 룰 삭제")
    @DeleteMapping("/{factoryName}/{equipmentName}/{moduleName}/{routeLinkId}")
    public ResponseEntity<Void> deleteSubTransferRule(
            @PathVariable String factoryName,
            @PathVariable String equipmentName,
            @PathVariable String moduleName,
            @PathVariable Long routeLinkId) {
        wcsSubTransferRuleService.deleteSubTransferRule(factoryName, equipmentName, moduleName, routeLinkId);
        return ResponseEntity.noContent().build();
    }
}
