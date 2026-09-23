package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.aim.api.dto.WcsShelfCreateRequestDto;
import kr.co.aim.api.dto.WcsShelfResponse;
import kr.co.aim.api.dto.WcsShelfUpdateRequestDto;
import kr.co.aim.api.service.WcsShelfService;
import kr.co.aim.common.condition.WcsShelfSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "WCS Shelf", description = "WCS 셸프(SHELF) 설비 위치 및 상태 관리 API")
@RestController
@RequestMapping("/api/v1/wcs/shelf")
@RequiredArgsConstructor
public class WcsShelfController {

    private final WcsShelfService wcsShelfService;

    /**
     * WCS 셸프 조건별 목록 조회 (페이징 지원)
     */
    @Operation(summary = "WCS 셸프 목록 조회", description = "동적 조건 및 페이징 처리를 통한 WCS 셸프 목록 조회")
    @GetMapping
    public ResponseEntity<Page<WcsShelfResponse>> findShelves(
            WcsShelfSearchCondition condition,
            Pageable pageable) {
        Page<WcsShelfResponse> response = wcsShelfService.findShelves(condition, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 복합키(factoryName, stockerName, shelfName) 기반 WCS 셸프 단건 상세 조회
     */
    @Operation(summary = "WCS 셸프 단건 상세 조회", description = "공장 구분, 스토커 명, 셸프 명 기반 단건 상세 조회")
    @GetMapping("/{factoryName}/{stockerName}/{shelfName}")
    public ResponseEntity<WcsShelfResponse> findById(
            @PathVariable String factoryName,
            @PathVariable String stockerName,
            @PathVariable String shelfName) {
        WcsShelfResponse response = wcsShelfService.findById(factoryName, stockerName, shelfName);
        return ResponseEntity.ok(response);
    }

    /**
     * 신규 WCS 셸프 등록
     */
    @Operation(summary = "WCS 셸프 등록", description = "신규 WCS 셸프 정보 등록 및 이력 적재")
    @PostMapping
    public ResponseEntity<WcsShelfResponse> createShelf(
            @RequestBody @Valid WcsShelfCreateRequestDto dto) {
        WcsShelfResponse response = wcsShelfService.createShelf(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 복합키(factoryName, stockerName, shelfName) 기반 WCS 셸프 정보 수정
     */
    @Operation(summary = "WCS 셸프 수정", description = "기존 WCS 셸프 정보 수정 및 변경 이력 적재")
    @PutMapping("/{factoryName}/{stockerName}/{shelfName}")
    public ResponseEntity<WcsShelfResponse> updateShelf(
            @PathVariable String factoryName,
            @PathVariable String stockerName,
            @PathVariable String shelfName,
            @RequestBody @Valid WcsShelfUpdateRequestDto dto) {
        WcsShelfResponse response = wcsShelfService.updateShelf(factoryName, stockerName, shelfName, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * 복합키(factoryName, stockerName, shelfName) 기반 WCS 셸프 삭제
     */
    @Operation(summary = "WCS 셸프 삭제", description = "WCS 셸프 삭제 및 삭제 이력 적재")
    @DeleteMapping("/{factoryName}/{stockerName}/{shelfName}")
    public ResponseEntity<Void> deleteShelf(
            @PathVariable String factoryName,
            @PathVariable String stockerName,
            @PathVariable String shelfName,
            @RequestParam(required = false) String eventUser,
            @RequestParam(required = false) String eventComment) {
        wcsShelfService.deleteShelf(factoryName, stockerName, shelfName, eventUser, eventComment);
        return ResponseEntity.noContent().build();
    }
}
