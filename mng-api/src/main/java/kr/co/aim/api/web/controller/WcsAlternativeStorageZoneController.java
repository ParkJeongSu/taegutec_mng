package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.aim.api.dto.WcsAlternativeStorageZoneCreateRequestDto;
import kr.co.aim.api.dto.WcsAlternativeStorageZoneResponse;
import kr.co.aim.api.dto.WcsAlternativeStorageZoneUpdateRequestDto;
import kr.co.aim.api.service.WcsAlternativeStorageZoneService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.common.condition.WcsAlternativeStorageZoneSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "WCS Alternative Storage Zone", description = "WCS 대체 보관 존(ALTERNATIVESTORAGEZONE) 기준정보 관리 API")
@RestController
@Profile("web")
@ResponseAnnotation
@RequestMapping("/api/v1/wcs/alternative-storage-zone")
@RequiredArgsConstructor
public class WcsAlternativeStorageZoneController {

    private final WcsAlternativeStorageZoneService wcsAlternativeStorageZoneService;

    /**
     * WCS 대체 보관 존 조건별 목록 조회 (페이징 지원)
     */
    @Operation(summary = "WCS 대체 보관 존 목록 조회", description = "동적 조건 및 페이징 처리를 통한 WCS 대체 보관 존 목록 조회")
    @GetMapping
    public ResponseEntity<Page<WcsAlternativeStorageZoneResponse>> findAlternativeStorageZones(
            WcsAlternativeStorageZoneSearchCondition condition,
            Pageable pageable) {
        Page<WcsAlternativeStorageZoneResponse> response = wcsAlternativeStorageZoneService.findAlternativeStorageZones(condition, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 4개 복합키(factoryName, sourceZoneName, alternativeZoneName, priority) 기반 WCS 대체 보관 존 단건 상세 조회
     */
    @Operation(summary = "WCS 대체 보관 존 단건 상세 조회", description = "4개 복합키 기반 단건 상세 조회")
    @GetMapping("/{factoryName}/{sourceZoneName}/{alternativeZoneName}/{priority}")
    public ResponseEntity<WcsAlternativeStorageZoneResponse> findById(
            @PathVariable String factoryName,
            @PathVariable String sourceZoneName,
            @PathVariable String alternativeZoneName,
            @PathVariable Integer priority) {
        WcsAlternativeStorageZoneResponse response = wcsAlternativeStorageZoneService.findById(
                factoryName, sourceZoneName, alternativeZoneName, priority);
        return ResponseEntity.ok(response);
    }

    /**
     * 공장 및 원본 존 별 WCS 대체 보관 존 목록 조회
     */
    @Operation(summary = "공장 및 원본 존 별 대체 보관 존 목록 조회", description = "공장 구분(factoryName) 및 원본 보관 존 명(sourceZoneName)별 목록 조회")
    @GetMapping("/by-source-zone/{factoryName}/{sourceZoneName}")
    public ResponseEntity<List<WcsAlternativeStorageZoneResponse>> findByFactoryNameAndSourceZoneName(
            @PathVariable String factoryName,
            @PathVariable String sourceZoneName) {
        List<WcsAlternativeStorageZoneResponse> response = wcsAlternativeStorageZoneService.findByFactoryNameAndSourceZoneName(
                factoryName, sourceZoneName);
        return ResponseEntity.ok(response);
    }

    /**
     * 신규 WCS 대체 보관 존 등록
     */
    @Operation(summary = "WCS 대체 보관 존 등록", description = "신규 WCS 대체 보관 존 정보 등록")
    @PostMapping
    public ResponseEntity<WcsAlternativeStorageZoneResponse> createAlternativeStorageZone(
            @RequestBody @Valid WcsAlternativeStorageZoneCreateRequestDto dto) {
        WcsAlternativeStorageZoneResponse response = wcsAlternativeStorageZoneService.createAlternativeStorageZone(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 4개 복합키 기반 WCS 대체 보관 존 정보 수정
     */
    @Operation(summary = "WCS 대체 보관 존 수정", description = "기존 WCS 대체 보관 존 정보 수정")
    @PutMapping("/{factoryName}/{sourceZoneName}/{alternativeZoneName}/{priority}")
    public ResponseEntity<WcsAlternativeStorageZoneResponse> updateAlternativeStorageZone(
            @PathVariable String factoryName,
            @PathVariable String sourceZoneName,
            @PathVariable String alternativeZoneName,
            @PathVariable Integer priority,
            @RequestBody @Valid WcsAlternativeStorageZoneUpdateRequestDto dto) {
        WcsAlternativeStorageZoneResponse response = wcsAlternativeStorageZoneService.updateAlternativeStorageZone(
                factoryName, sourceZoneName, alternativeZoneName, priority, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * 4개 복합키 기반 WCS 대체 보관 존 삭제
     */
    @Operation(summary = "WCS 대체 보관 존 삭제", description = "WCS 대체 보관 존 삭제")
    @DeleteMapping("/{factoryName}/{sourceZoneName}/{alternativeZoneName}/{priority}")
    public ResponseEntity<Void> deleteAlternativeStorageZone(
            @PathVariable String factoryName,
            @PathVariable String sourceZoneName,
            @PathVariable String alternativeZoneName,
            @PathVariable Integer priority) {
        wcsAlternativeStorageZoneService.deleteAlternativeStorageZone(factoryName, sourceZoneName, alternativeZoneName, priority);
        return ResponseEntity.noContent().build();
    }
}
