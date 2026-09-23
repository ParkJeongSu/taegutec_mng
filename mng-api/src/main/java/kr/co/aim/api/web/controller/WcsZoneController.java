package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.aim.api.dto.WcsZoneCreateRequestDto;
import kr.co.aim.api.dto.WcsZoneResponse;
import kr.co.aim.api.dto.WcsZoneUpdateRequestDto;
import kr.co.aim.api.service.WcsZoneService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.common.condition.WcsZoneSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "WCS Zone", description = "WCS 보관 존(ZONE) 구역 및 정책 관리 API")
@RestController
@Profile("web")
@ResponseAnnotation
@RequestMapping("/api/v1/wcs/zone")
@RequiredArgsConstructor
public class WcsZoneController {

    private final WcsZoneService wcsZoneService;

    /**
     * WCS 보관 존 조건별 목록 조회 (페이징 지원)
     */
    @Operation(summary = "WCS 보관 존 목록 조회", description = "동적 조건 및 페이징 처리를 통한 WCS 보관 존 목록 조회")
    @GetMapping
    public ResponseEntity<Page<WcsZoneResponse>> findZones(
            WcsZoneSearchCondition condition,
            Pageable pageable) {
        Page<WcsZoneResponse> response = wcsZoneService.findZones(condition, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 복합키(factoryName, zoneName) 기반 WCS 보관 존 단건 상세 조회
     */
    @Operation(summary = "WCS 보관 존 단건 상세 조회", description = "공장 구분 및 존 명 기반 단건 상세 조회")
    @GetMapping("/{factoryName}/{zoneName}")
    public ResponseEntity<WcsZoneResponse> findById(
            @PathVariable String factoryName,
            @PathVariable String zoneName) {
        WcsZoneResponse response = wcsZoneService.findById(factoryName, zoneName);
        return ResponseEntity.ok(response);
    }

    /**
     * 신규 WCS 보관 존 등록
     */
    @Operation(summary = "WCS 보관 존 등록", description = "신규 WCS 보관 존 정보 등록 및 이력 적재")
    @PostMapping
    public ResponseEntity<WcsZoneResponse> createZone(
            @RequestBody @Valid WcsZoneCreateRequestDto dto) {
        WcsZoneResponse response = wcsZoneService.createZone(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 복합키(factoryName, zoneName) 기반 WCS 보관 존 정보 수정
     */
    @Operation(summary = "WCS 보관 존 수정", description = "기존 WCS 보관 존 정보 수정 및 변경 이력 적재")
    @PutMapping("/{factoryName}/{zoneName}")
    public ResponseEntity<WcsZoneResponse> updateZone(
            @PathVariable String factoryName,
            @PathVariable String zoneName,
            @RequestBody @Valid WcsZoneUpdateRequestDto dto) {
        WcsZoneResponse response = wcsZoneService.updateZone(factoryName, zoneName, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * 복합키(factoryName, zoneName) 기반 WCS 보관 존 삭제
     */
    @Operation(summary = "WCS 보관 존 삭제", description = "WCS 보관 존 삭제 및 삭제 이력 적재")
    @DeleteMapping("/{factoryName}/{zoneName}")
    public ResponseEntity<Void> deleteZone(
            @PathVariable String factoryName,
            @PathVariable String zoneName,
            @RequestParam(required = false) String eventUser,
            @RequestParam(required = false) String eventComment) {
        wcsZoneService.deleteZone(factoryName, zoneName, eventUser, eventComment);
        return ResponseEntity.noContent().build();
    }
}
