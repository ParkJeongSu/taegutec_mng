package kr.co.aim.api.controller;

import jakarta.validation.Valid;
import kr.co.aim.api.dto.WcsStockerCreateRequestDto;
import kr.co.aim.api.dto.WcsStockerResponse;
import kr.co.aim.api.dto.WcsStockerUpdateRequestDto;
import kr.co.aim.api.service.WcsStockerService;
import kr.co.aim.common.condition.WcsStockerSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/v1/wcs/stocker", "/api/v1/wcs/stockers"})
@RequiredArgsConstructor
public class WcsStockerController {

    private final WcsStockerService wcsStockerService;

    /**
     * WCS 스토커 조건별 목록 조회 (페이징 지원)
     */
    @GetMapping
    public ResponseEntity<Page<WcsStockerResponse>> findStockers(
            WcsStockerSearchCondition condition,
            Pageable pageable) {
        Page<WcsStockerResponse> response = wcsStockerService.findStockers(condition, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 복합키(factoryName, stockerName) 기반 WCS 스토커 단건 상세 조회
     */
    @GetMapping("/{factoryName}/{stockerName}")
    public ResponseEntity<WcsStockerResponse> findById(
            @PathVariable String factoryName,
            @PathVariable String stockerName) {
        WcsStockerResponse response = wcsStockerService.findById(factoryName, stockerName);
        return ResponseEntity.ok(response);
    }

    /**
     * 신규 WCS 스토커 등록
     */
    @PostMapping
    public ResponseEntity<WcsStockerResponse> createStocker(
            @RequestBody @Valid WcsStockerCreateRequestDto dto) {
        WcsStockerResponse response = wcsStockerService.createStocker(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 복합키(factoryName, stockerName) 기반 WCS 스토커 정보 수정
     */
    @PutMapping("/{factoryName}/{stockerName}")
    public ResponseEntity<WcsStockerResponse> updateStocker(
            @PathVariable String factoryName,
            @PathVariable String stockerName,
            @RequestBody @Valid WcsStockerUpdateRequestDto dto) {
        WcsStockerResponse response = wcsStockerService.updateStocker(factoryName, stockerName, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * 복합키(factoryName, stockerName) 기반 WCS 스토커 삭제
     */
    @DeleteMapping("/{factoryName}/{stockerName}")
    public ResponseEntity<Void> deleteStocker(
            @PathVariable String factoryName,
            @PathVariable String stockerName,
            @RequestParam(required = false) String eventUser,
            @RequestParam(required = false) String eventComment) {
        wcsStockerService.deleteStocker(factoryName, stockerName, eventUser, eventComment);
        return ResponseEntity.noContent().build();
    }
}
