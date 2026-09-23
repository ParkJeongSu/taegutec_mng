package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.aim.api.dto.WcsCarrierCreateRequestDto;
import kr.co.aim.api.dto.WcsCarrierResponse;
import kr.co.aim.api.dto.WcsCarrierUpdateRequestDto;
import kr.co.aim.api.service.WcsCarrierService;
import kr.co.aim.common.condition.WcsCarrierSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "WCS Carrier", description = "WCS 캐리어(CARRIER) 반송 및 상태 관리 API")
@RestController
@RequestMapping("/api/v1/wcs/carrier")
@RequiredArgsConstructor
public class WcsCarrierController {

    private final WcsCarrierService wcsCarrierService;

    /**
     * WCS 캐리어 조건별 목록 조회 (페이징 지원)
     */
    @Operation(summary = "WCS 캐리어 목록 조회", description = "동적 조건 및 페이징 처리를 통한 WCS 캐리어 목록 조회")
    @GetMapping
    public ResponseEntity<Page<WcsCarrierResponse>> findCarriers(
            WcsCarrierSearchCondition condition,
            Pageable pageable) {
        Page<WcsCarrierResponse> response = wcsCarrierService.findCarriers(condition, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 복합키(factoryName, carrierName) 기반 WCS 캐리어 단건 상세 조회
     */
    @Operation(summary = "WCS 캐리어 단건 상세 조회", description = "공장 구분 및 캐리어 명 기반 단건 상세 조회")
    @GetMapping("/{factoryName}/{carrierName}")
    public ResponseEntity<WcsCarrierResponse> findById(
            @PathVariable String factoryName,
            @PathVariable String carrierName) {
        WcsCarrierResponse response = wcsCarrierService.findById(carrierName, factoryName);
        return ResponseEntity.ok(response);
    }

    /**
     * 신규 WCS 캐리어 등록
     */
    @Operation(summary = "WCS 캐리어 등록", description = "신규 WCS 캐리어 정보 등록 및 이력 적재")
    @PostMapping
    public ResponseEntity<WcsCarrierResponse> createCarrier(
            @RequestBody @Valid WcsCarrierCreateRequestDto dto) {
        WcsCarrierResponse response = wcsCarrierService.createCarrier(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 복합키(factoryName, carrierName) 기반 WCS 캐리어 정보 수정
     */
    @Operation(summary = "WCS 캐리어 수정", description = "기존 WCS 캐리어 정보 수정 및 변경 이력 적재")
    @PutMapping("/{factoryName}/{carrierName}")
    public ResponseEntity<WcsCarrierResponse> updateCarrier(
            @PathVariable String factoryName,
            @PathVariable String carrierName,
            @RequestBody @Valid WcsCarrierUpdateRequestDto dto) {
        WcsCarrierResponse response = wcsCarrierService.updateCarrier(carrierName, factoryName, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * 복합키(factoryName, carrierName) 기반 WCS 캐리어 삭제
     */
    @Operation(summary = "WCS 캐리어 삭제", description = "WCS 캐리어 삭제 및 삭제 이력 적재")
    @DeleteMapping("/{factoryName}/{carrierName}")
    public ResponseEntity<Void> deleteCarrier(
            @PathVariable String factoryName,
            @PathVariable String carrierName,
            @RequestParam(required = false) String eventUser,
            @RequestParam(required = false) String eventComment) {
        wcsCarrierService.deleteCarrier(carrierName, factoryName, eventUser, eventComment);
        return ResponseEntity.noContent().build();
    }
}
