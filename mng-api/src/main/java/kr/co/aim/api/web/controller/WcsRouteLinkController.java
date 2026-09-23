package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.aim.api.dto.WcsRouteLinkCreateRequestDto;
import kr.co.aim.api.dto.WcsRouteLinkResponse;
import kr.co.aim.api.dto.WcsRouteLinkUpdateRequestDto;
import kr.co.aim.api.service.WcsRouteLinkService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.common.condition.WcsRouteLinkSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "WCS Route Link", description = "WCS 경로 링크(ROUTE_LINK) 기준정보 관리 API")
@RestController
@Profile("web")
@ResponseAnnotation
@RequestMapping("/api/v1/wcs/route-link")
@RequiredArgsConstructor
public class WcsRouteLinkController {

    private final WcsRouteLinkService wcsRouteLinkService;

    /**
     * WCS 경로 링크 조건별 목록 조회 (페이징 지원)
     */
    @Operation(summary = "WCS 경로 링크 목록 조회", description = "동적 조건 및 페이징 처리를 통한 WCS 경로 링크 목록 조회")
    @GetMapping
    public ResponseEntity<Page<WcsRouteLinkResponse>> findRouteLinks(
            WcsRouteLinkSearchCondition condition,
            Pageable pageable) {
        Page<WcsRouteLinkResponse> response = wcsRouteLinkService.findRouteLinks(condition, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 2개 복합키(factoryName, routeLinkId) 기반 WCS 경로 링크 단건 상세 조회
     */
    @Operation(summary = "WCS 경로 링크 단건 상세 조회", description = "2개 복합키(factoryName, routeLinkId) 기반 단건 상세 조회")
    @GetMapping("/{factoryName}/{routeLinkId}")
    public ResponseEntity<WcsRouteLinkResponse> findById(
            @PathVariable String factoryName,
            @PathVariable Long routeLinkId) {
        WcsRouteLinkResponse response = wcsRouteLinkService.findById(factoryName, routeLinkId);
        return ResponseEntity.ok(response);
    }

    /**
     * 공장별 WCS 경로 링크 목록 조회
     */
    @Operation(summary = "공장별 WCS 경로 링크 목록 조회", description = "공장 구분(factoryName)별 경로 링크 목록 조회")
    @GetMapping("/by-factory/{factoryName}")
    public ResponseEntity<List<WcsRouteLinkResponse>> findByFactoryName(
            @PathVariable String factoryName) {
        List<WcsRouteLinkResponse> response = wcsRouteLinkService.findByFactoryName(factoryName);
        return ResponseEntity.ok(response);
    }

    /**
     * 신규 WCS 경로 링크 등록
     */
    @Operation(summary = "WCS 경로 링크 등록", description = "신규 WCS 경로 링크 정보 등록")
    @PostMapping
    public ResponseEntity<WcsRouteLinkResponse> createRouteLink(
            @RequestBody @Valid WcsRouteLinkCreateRequestDto dto) {
        WcsRouteLinkResponse response = wcsRouteLinkService.createRouteLink(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 2개 복합키 기반 WCS 경로 링크 정보 수정
     */
    @Operation(summary = "WCS 경로 링크 수정", description = "기존 WCS 경로 링크 정보 수정")
    @PutMapping("/{factoryName}/{routeLinkId}")
    public ResponseEntity<WcsRouteLinkResponse> updateRouteLink(
            @PathVariable String factoryName,
            @PathVariable Long routeLinkId,
            @RequestBody @Valid WcsRouteLinkUpdateRequestDto dto) {
        WcsRouteLinkResponse response = wcsRouteLinkService.updateRouteLink(factoryName, routeLinkId, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * 2개 복합키 기반 WCS 경로 링크 삭제
     */
    @Operation(summary = "WCS 경로 링크 삭제", description = "WCS 경로 링크 삭제")
    @DeleteMapping("/{factoryName}/{routeLinkId}")
    public ResponseEntity<Void> deleteRouteLink(
            @PathVariable String factoryName,
            @PathVariable Long routeLinkId) {
        wcsRouteLinkService.deleteRouteLink(factoryName, routeLinkId);
        return ResponseEntity.noContent().build();
    }
}
