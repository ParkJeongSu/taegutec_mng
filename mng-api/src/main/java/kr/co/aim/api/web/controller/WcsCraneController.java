package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.aim.api.dto.WcsCraneCreateRequestDto;
import kr.co.aim.api.dto.WcsCraneResponse;
import kr.co.aim.api.dto.WcsCraneUpdateRequestDto;
import kr.co.aim.api.service.WcsCraneService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.common.condition.WcsCraneSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "WCS Crane", description = "WCS 크레인(CRANE) 입출고 이송기구 상태 및 제어 관리 API")
@RestController
@Profile("web")
@ResponseAnnotation
@RequestMapping("/api/v1/wcs/crane")
@RequiredArgsConstructor
public class WcsCraneController {

    private final WcsCraneService wcsCraneService;

    /**
     * WCS 크레인 조건별 목록 조회 (페이징 지원)
     */
    @Operation(summary = "WCS 크레인 목록 조회", description = "동적 조건 및 페이징 처리를 통한 WCS 크레인 목록 조회")
    @GetMapping
    public ResponseEntity<Page<WcsCraneResponse>> findCranes(
            WcsCraneSearchCondition condition,
            Pageable pageable) {
        Page<WcsCraneResponse> response = wcsCraneService.findCranes(condition, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 5개 복합키 기반 WCS 크레인 단건 상세 조회
     */
    @Operation(summary = "WCS 크레인 단건 상세 조회", description = "5개 복합키(factoryName, stockerName, craneName, craneNumber, localNo) 기반 단건 상세 조회")
    @GetMapping("/{factoryName}/{stockerName}/{craneName}/{craneNumber}/{localNo}")
    public ResponseEntity<WcsCraneResponse> findById(
            @PathVariable String factoryName,
            @PathVariable String stockerName,
            @PathVariable String craneName,
            @PathVariable Integer craneNumber,
            @PathVariable Integer localNo) {
        WcsCraneResponse response = wcsCraneService.findById(factoryName, stockerName, craneName, craneNumber, localNo);
        return ResponseEntity.ok(response);
    }

    /**
     * 신규 WCS 크레인 등록
     */
    @Operation(summary = "WCS 크레인 등록", description = "신규 WCS 크레인 정보 등록 및 이력 적재")
    @PostMapping
    public ResponseEntity<WcsCraneResponse> createCrane(
            @RequestBody @Valid WcsCraneCreateRequestDto dto) {
        WcsCraneResponse response = wcsCraneService.createCrane(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 5개 복합키 기반 WCS 크레인 정보 수정
     */
    @Operation(summary = "WCS 크레인 수정", description = "기존 WCS 크레인 정보 수정 및 변경 이력 적재")
    @PutMapping("/{factoryName}/{stockerName}/{craneName}/{craneNumber}/{localNo}")
    public ResponseEntity<WcsCraneResponse> updateCrane(
            @PathVariable String factoryName,
            @PathVariable String stockerName,
            @PathVariable String craneName,
            @PathVariable Integer craneNumber,
            @PathVariable Integer localNo,
            @RequestBody @Valid WcsCraneUpdateRequestDto dto) {
        WcsCraneResponse response = wcsCraneService.updateCrane(factoryName, stockerName, craneName, craneNumber, localNo, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * 5개 복합키 기반 WCS 크레인 삭제
     */
    @Operation(summary = "WCS 크레인 삭제", description = "WCS 크레인 삭제 및 삭제 이력 적재")
    @DeleteMapping("/{factoryName}/{stockerName}/{craneName}/{craneNumber}/{localNo}")
    public ResponseEntity<Void> deleteCrane(
            @PathVariable String factoryName,
            @PathVariable String stockerName,
            @PathVariable String craneName,
            @PathVariable Integer craneNumber,
            @PathVariable Integer localNo,
            @RequestParam(required = false) String eventUser,
            @RequestParam(required = false) String eventComment) {
        wcsCraneService.deleteCrane(factoryName, stockerName, craneName, craneNumber, localNo, eventUser, eventComment);
        return ResponseEntity.noContent().build();
    }
}
