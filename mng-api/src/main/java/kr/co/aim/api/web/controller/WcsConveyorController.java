package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.aim.api.dto.WcsConveyorCreateRequestDto;
import kr.co.aim.api.dto.WcsConveyorResponse;
import kr.co.aim.api.dto.WcsConveyorUpdateRequestDto;
import kr.co.aim.api.service.WcsConveyorService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.common.condition.WcsConveyorSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "WCS Conveyor", description = "WCS 컨베이어(CONVEYOR) 라인 설비 상태 및 제어 관리 API")
@RestController
@Profile("web")
@ResponseAnnotation
@RequestMapping({"/api/v1/wcs/conveyor", "/api/v1/wcs/conveyors"})
@RequiredArgsConstructor
public class WcsConveyorController {

    private final WcsConveyorService wcsConveyorService;

    /**
     * WCS 컨베이어 조건별 목록 조회 (페이징 지원)
     */
    @Operation(summary = "WCS 컨베이어 목록 조회", description = "동적 조건 및 페이징 처리를 통한 WCS 컨베이어 목록 조회")
    @GetMapping
    public ResponseEntity<Page<WcsConveyorResponse>> findConveyors(
            WcsConveyorSearchCondition condition,
            Pageable pageable) {
        Page<WcsConveyorResponse> response = wcsConveyorService.findConveyors(condition, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 5개 복합키 기반 WCS 컨베이어 단건 상세 조회
     */
    @Operation(summary = "WCS 컨베이어 단건 상세 조회", description = "5개 복합키(factoryName, conveyorGroup, conveyorName, conveyorNumber, localNo) 기반 단건 상세 조회")
    @GetMapping("/{factoryName}/{conveyorGroup}/{conveyorName}/{conveyorNumber}/{localNo}")
    public ResponseEntity<WcsConveyorResponse> findById(
            @PathVariable String factoryName,
            @PathVariable String conveyorGroup,
            @PathVariable String conveyorName,
            @PathVariable Integer conveyorNumber,
            @PathVariable Integer localNo) {
        WcsConveyorResponse response = wcsConveyorService.findById(factoryName, conveyorGroup, conveyorName, conveyorNumber, localNo);
        return ResponseEntity.ok(response);
    }

    /**
     * 신규 WCS 컨베이어 등록
     */
    @Operation(summary = "WCS 컨베이어 등록", description = "신규 WCS 컨베이어 정보 등록 및 이력 적재")
    @PostMapping
    public ResponseEntity<WcsConveyorResponse> createConveyor(
            @RequestBody @Valid WcsConveyorCreateRequestDto dto) {
        WcsConveyorResponse response = wcsConveyorService.createConveyor(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 5개 복합키 기반 WCS 컨베이어 정보 수정
     */
    @Operation(summary = "WCS 컨베이어 수정", description = "기존 WCS 컨베이어 정보 수정 및 변경 이력 적재")
    @PutMapping("/{factoryName}/{conveyorGroup}/{conveyorName}/{conveyorNumber}/{localNo}")
    public ResponseEntity<WcsConveyorResponse> updateConveyor(
            @PathVariable String factoryName,
            @PathVariable String conveyorGroup,
            @PathVariable String conveyorName,
            @PathVariable Integer conveyorNumber,
            @PathVariable Integer localNo,
            @RequestBody @Valid WcsConveyorUpdateRequestDto dto) {
        WcsConveyorResponse response = wcsConveyorService.updateConveyor(factoryName, conveyorGroup, conveyorName, conveyorNumber, localNo, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * 5개 복합키 기반 WCS 컨베이어 삭제
     */
    @Operation(summary = "WCS 컨베이어 삭제", description = "WCS 컨베이어 삭제 및 삭제 이력 적재")
    @DeleteMapping("/{factoryName}/{conveyorGroup}/{conveyorName}/{conveyorNumber}/{localNo}")
    public ResponseEntity<Void> deleteConveyor(
            @PathVariable String factoryName,
            @PathVariable String conveyorGroup,
            @PathVariable String conveyorName,
            @PathVariable Integer conveyorNumber,
            @PathVariable Integer localNo,
            @RequestParam(required = false) String eventUser,
            @RequestParam(required = false) String eventComment) {
        wcsConveyorService.deleteConveyor(factoryName, conveyorGroup, conveyorName, conveyorNumber, localNo, eventUser, eventComment);
        return ResponseEntity.noContent().build();
    }
}
