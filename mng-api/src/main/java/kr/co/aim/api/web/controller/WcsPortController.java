package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.aim.api.dto.WcsPortCreateRequestDto;
import kr.co.aim.api.dto.WcsPortResponse;
import kr.co.aim.api.dto.WcsPortUpdateRequestDto;
import kr.co.aim.api.service.WcsPortService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.common.condition.WcsPortSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "WCS Port", description = "WCS 포트(PORT) 설비 인터페이스 상태 및 제어 관리 API")
@RestController
@Profile("web")
@ResponseAnnotation
@RequestMapping("/api/v1/wcs/port")
@RequiredArgsConstructor
public class WcsPortController {

    private final WcsPortService wcsPortService;

    /**
     * WCS 포트 조건별 목록 조회 (페이징 지원)
     */
    @Operation(summary = "WCS 포트 목록 조회", description = "동적 조건 및 페이징 처리를 통한 WCS 포트 목록 조회")
    @GetMapping
    public ResponseEntity<Page<WcsPortResponse>> findPorts(
            WcsPortSearchCondition condition,
            Pageable pageable) {
        Page<WcsPortResponse> response = wcsPortService.findPorts(condition, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 4개 복합키 기반 WCS 포트 단건 상세 조회
     */
    @Operation(summary = "WCS 포트 단건 상세 조회", description = "4개 복합키(factoryName, equipmentName, localNo, portNumber) 기반 단건 상세 조회")
    @GetMapping("/{factoryName}/{equipmentName}/{localNo}/{portNumber}")
    public ResponseEntity<WcsPortResponse> findById(
            @PathVariable String factoryName,
            @PathVariable String equipmentName,
            @PathVariable Integer localNo,
            @PathVariable Integer portNumber) {
        WcsPortResponse response = wcsPortService.findById(factoryName, equipmentName, localNo, portNumber);
        return ResponseEntity.ok(response);
    }

    /**
     * 신규 WCS 포트 등록
     */
    @Operation(summary = "WCS 포트 등록", description = "신규 WCS 포트 정보 등록 및 이력 적재")
    @PostMapping
    public ResponseEntity<WcsPortResponse> createPort(
            @RequestBody @Valid WcsPortCreateRequestDto dto) {
        WcsPortResponse response = wcsPortService.createPort(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 4개 복합키 기반 WCS 포트 정보 수정
     */
    @Operation(summary = "WCS 포트 수정", description = "기존 WCS 포트 정보 수정 및 변경 이력 적재")
    @PutMapping("/{factoryName}/{equipmentName}/{localNo}/{portNumber}")
    public ResponseEntity<WcsPortResponse> updatePort(
            @PathVariable String factoryName,
            @PathVariable String equipmentName,
            @PathVariable Integer localNo,
            @PathVariable Integer portNumber,
            @RequestBody @Valid WcsPortUpdateRequestDto dto) {
        WcsPortResponse response = wcsPortService.updatePort(factoryName, equipmentName, localNo, portNumber, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * 4개 복합키 기반 WCS 포트 삭제
     */
    @Operation(summary = "WCS 포트 삭제", description = "WCS 포트 삭제 및 삭제 이력 적재")
    @DeleteMapping("/{factoryName}/{equipmentName}/{localNo}/{portNumber}")
    public ResponseEntity<Void> deletePort(
            @PathVariable String factoryName,
            @PathVariable String equipmentName,
            @PathVariable Integer localNo,
            @PathVariable Integer portNumber,
            @RequestParam(required = false) String eventUser,
            @RequestParam(required = false) String eventComment) {
        wcsPortService.deletePort(factoryName, equipmentName, localNo, portNumber, eventUser, eventComment);
        return ResponseEntity.noContent().build();
    }
}
