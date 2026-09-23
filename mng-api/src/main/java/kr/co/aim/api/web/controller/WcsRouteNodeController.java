package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.aim.api.dto.WcsRouteNodeCreateRequestDto;
import kr.co.aim.api.dto.WcsRouteNodeResponse;
import kr.co.aim.api.dto.WcsRouteNodeUpdateRequestDto;
import kr.co.aim.api.service.WcsRouteNodeService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.common.condition.WcsRouteNodeSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "WCS Route Node", description = "WCS 경로 노드(ROUTE_NODE) 기준정보 관리 API")
@RestController
@Profile("web")
@ResponseAnnotation
@RequestMapping("/api/v1/wcs/route-node")
@RequiredArgsConstructor
public class WcsRouteNodeController {

    private final WcsRouteNodeService wcsRouteNodeService;

    /**
     * WCS 경로 노드 조건별 목록 조회 (페이징 지원)
     */
    @Operation(summary = "WCS 경로 노드 목록 조회", description = "동적 조건 및 페이징 처리를 통한 WCS 경로 노드 목록 조회")
    @GetMapping
    public ResponseEntity<Page<WcsRouteNodeResponse>> findRouteNodes(
            WcsRouteNodeSearchCondition condition,
            Pageable pageable) {
        Page<WcsRouteNodeResponse> response = wcsRouteNodeService.findRouteNodes(condition, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 2개 복합키(factoryName, routeNodeId) 기반 WCS 경로 노드 단건 상세 조회
     */
    @Operation(summary = "WCS 경로 노드 단건 상세 조회", description = "2개 복합키(factoryName, routeNodeId) 기반 단건 상세 조회")
    @GetMapping("/{factoryName}/{routeNodeId}")
    public ResponseEntity<WcsRouteNodeResponse> findById(
            @PathVariable String factoryName,
            @PathVariable Long routeNodeId) {
        WcsRouteNodeResponse response = wcsRouteNodeService.findById(factoryName, routeNodeId);
        return ResponseEntity.ok(response);
    }

    /**
     * 공장별 WCS 경로 노드 목록 조회
     */
    @Operation(summary = "공장별 WCS 경로 노드 목록 조회", description = "공장 구분(factoryName)별 경로 노드 목록 조회")
    @GetMapping("/by-factory/{factoryName}")
    public ResponseEntity<List<WcsRouteNodeResponse>> findByFactoryName(
            @PathVariable String factoryName) {
        List<WcsRouteNodeResponse> response = wcsRouteNodeService.findByFactoryName(factoryName);
        return ResponseEntity.ok(response);
    }

    /**
     * 신규 WCS 경로 노드 등록
     */
    @Operation(summary = "WCS 경로 노드 등록", description = "신규 WCS 경로 노드 정보 등록")
    @PostMapping
    public ResponseEntity<WcsRouteNodeResponse> createRouteNode(
            @RequestBody @Valid WcsRouteNodeCreateRequestDto dto) {
        WcsRouteNodeResponse response = wcsRouteNodeService.createRouteNode(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 2개 복합키 기반 WCS 경로 노드 정보 수정
     */
    @Operation(summary = "WCS 경로 노드 수정", description = "기존 WCS 경로 노드 정보 수정")
    @PutMapping("/{factoryName}/{routeNodeId}")
    public ResponseEntity<WcsRouteNodeResponse> updateRouteNode(
            @PathVariable String factoryName,
            @PathVariable Long routeNodeId,
            @RequestBody @Valid WcsRouteNodeUpdateRequestDto dto) {
        WcsRouteNodeResponse response = wcsRouteNodeService.updateRouteNode(factoryName, routeNodeId, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * 2개 복합키 기반 WCS 경로 노드 삭제
     */
    @Operation(summary = "WCS 경로 노드 삭제", description = "WCS 경로 노드 삭제")
    @DeleteMapping("/{factoryName}/{routeNodeId}")
    public ResponseEntity<Void> deleteRouteNode(
            @PathVariable String factoryName,
            @PathVariable Long routeNodeId) {
        wcsRouteNodeService.deleteRouteNode(factoryName, routeNodeId);
        return ResponseEntity.noContent().build();
    }
}
