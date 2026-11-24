package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.dto.DeleteItemListDto;
import kr.co.aim.api.dto.ZoneMappingCreateRequestDto;
import kr.co.aim.api.dto.ZoneMappingResponse;
import kr.co.aim.api.dto.ZoneMappingUpdateRequestDto;
import kr.co.aim.api.service.ZoneMappingService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.domain.repository.condition.ZoneMappingSearchCondition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "MNG ZoneMapping", description = "존 매핑(ZONE_MAPPING) 관리 API")
@RestController
@RequestMapping("/api/v1/mng/zone-mapping")
@RequiredArgsConstructor
@Slf4j
@Profile({"web", "simulator"})
@ResponseAnnotation
public class ZoneMappingController {

    private final ZoneMappingService zoneMappingService;

    @Operation(summary = "존 매핑(ZONE_MAPPING) 목록 조회", description = "동적 조건 및 페이징 기반 존 매핑(ZONE_MAPPING) 목록 Page 반환")
    @GetMapping("")
    public ResponseEntity<Page<ZoneMappingResponse>> getZoneMappings(
            ZoneMappingSearchCondition condition,
            @ParameterObject Pageable pageable
    ) {
        Page<ZoneMappingResponse> result = zoneMappingService.findZoneMappings(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "특정 존 매핑(ZONE_MAPPING) 상세 조회", description = "TSID(Long id) 기준 존 매핑(ZONE_MAPPING) 상세 정보 Page 반환")
    @GetMapping("/{id}")
    public ResponseEntity<Page<ZoneMappingResponse>> getZoneMapping(@PathVariable("id") Long id) {
        ZoneMappingResponse result = zoneMappingService.findById(id);
        List<ZoneMappingResponse> list = new ArrayList<>();
        list.add(result);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "신규 존 매핑(ZONE_MAPPING) 등록", description = "신규 존 매핑(ZONE_MAPPING) 등록 및 ZONE_MAPPING_HISTORY 이력 적재")
    @PostMapping("")
    public ResponseEntity<Page<ZoneMappingResponse>> createZoneMapping(@RequestBody ZoneMappingCreateRequestDto dto) {
        ZoneMappingResponse created = zoneMappingService.createZoneMapping(dto);
        List<ZoneMappingResponse> list = new ArrayList<>();
        list.add(created);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "존 매핑(ZONE_MAPPING) 정보 수정", description = "TSID(Long id) 기준 존 매핑(ZONE_MAPPING) 정보 수정 및 ZONE_MAPPING_HISTORY 이력 적재")
    @PutMapping("/{id}")
    public ResponseEntity<Page<ZoneMappingResponse>> updateZoneMapping(
            @PathVariable("id") Long id,
            @RequestBody ZoneMappingUpdateRequestDto dto
    ) {
        ZoneMappingResponse updated = zoneMappingService.updateZoneMapping(id, dto);
        List<ZoneMappingResponse> list = new ArrayList<>();
        list.add(updated);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "존 매핑(ZONE_MAPPING) 단건 삭제", description = "TSID(Long id) 기준 단건 삭제 및 ZONE_MAPPING_HISTORY 삭제 이력 적재")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteZoneMapping(
            @PathVariable("id") Long id,
            @RequestParam(value = "eventUser", required = false, defaultValue = "SYSTEM") String eventUser,
            @RequestParam(value = "eventComment", required = false, defaultValue = "ZoneMapping deleted") String eventComment
    ) {
        zoneMappingService.deleteZoneMapping(id, eventUser, eventComment);
        return ResponseEntity.ok("SUCCESS");
    }

    @Operation(summary = "존 매핑(ZONE_MAPPING) 복수 벌크 삭제", description = "선택된 TSID(ID) 리스트에 대한 In-Batch 벌크 삭제 및 ZONE_MAPPING_HISTORY 삭제 이력 적재")
    @DeleteMapping("/batch-delete")
    public ResponseEntity<String> deleteZoneMappings(@RequestBody DeleteItemListDto deleteDto) {
        if (deleteDto != null && deleteDto.getIds() != null) {
            zoneMappingService.deleteZoneMappings(deleteDto.getIds(), "SYSTEM", "Batch ZoneMappings deleted");
        }
        return ResponseEntity.ok("SUCCESS");
    }
}
