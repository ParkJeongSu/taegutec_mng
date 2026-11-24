package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.dto.DeleteItemListDto;
import kr.co.aim.api.dto.ZoneFallbackCreateRequestDto;
import kr.co.aim.api.dto.ZoneFallbackResponse;
import kr.co.aim.api.dto.ZoneFallbackUpdateRequestDto;
import kr.co.aim.api.service.ZoneFallbackService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.domain.repository.condition.ZoneFallbackSearchCondition;
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

@Tag(name = "MNG ZoneFallback", description = "존 폴백(ZONE_FALLBACK) 관리 API")
@RestController
@RequestMapping("/api/v1/mng/zone-fallback")
@RequiredArgsConstructor
@Slf4j
@Profile({"web", "simulator"})
@ResponseAnnotation
public class ZoneFallbackController {

    private final ZoneFallbackService zoneFallbackService;

    @Operation(summary = "존 폴백(ZONE_FALLBACK) 목록 조회", description = "동적 조건 및 페이징 기반 존 폴백(ZONE_FALLBACK) 목록 Page 반환")
    @GetMapping("")
    public ResponseEntity<Page<ZoneFallbackResponse>> getZoneFallbacks(
            ZoneFallbackSearchCondition condition,
            @ParameterObject Pageable pageable
    ) {
        Page<ZoneFallbackResponse> result = zoneFallbackService.findZoneFallbacks(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "특정 존 폴백(ZONE_FALLBACK) 상세 조회", description = "TSID(Long id) 기준 존 폴백(ZONE_FALLBACK) 상세 정보 Page 반환")
    @GetMapping("/{id}")
    public ResponseEntity<Page<ZoneFallbackResponse>> getZoneFallback(@PathVariable("id") Long id) {
        ZoneFallbackResponse result = zoneFallbackService.findById(id);
        List<ZoneFallbackResponse> list = new ArrayList<>();
        list.add(result);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "신규 존 폴백(ZONE_FALLBACK) 등록", description = "신규 존 폴백(ZONE_FALLBACK) 등록 및 ZONE_FALLBACK_HISTORY 이력 적재")
    @PostMapping("")
    public ResponseEntity<Page<ZoneFallbackResponse>> createZoneFallback(@RequestBody ZoneFallbackCreateRequestDto dto) {
        ZoneFallbackResponse created = zoneFallbackService.createZoneFallback(dto);
        List<ZoneFallbackResponse> list = new ArrayList<>();
        list.add(created);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "존 폴백(ZONE_FALLBACK) 정보 수정", description = "TSID(Long id) 기준 존 폴백(ZONE_FALLBACK) 정보 수정 및 ZONE_FALLBACK_HISTORY 이력 적재")
    @PutMapping("/{id}")
    public ResponseEntity<Page<ZoneFallbackResponse>> updateZoneFallback(
            @PathVariable("id") Long id,
            @RequestBody ZoneFallbackUpdateRequestDto dto
    ) {
        ZoneFallbackResponse updated = zoneFallbackService.updateZoneFallback(id, dto);
        List<ZoneFallbackResponse> list = new ArrayList<>();
        list.add(updated);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "존 폴백(ZONE_FALLBACK) 단건 삭제", description = "TSID(Long id) 기준 단건 삭제 및 ZONE_FALLBACK_HISTORY 삭제 이력 적재")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteZoneFallback(
            @PathVariable("id") Long id,
            @RequestParam(value = "eventUser", required = false, defaultValue = "SYSTEM") String eventUser,
            @RequestParam(value = "eventComment", required = false, defaultValue = "ZoneFallback deleted") String eventComment
    ) {
        zoneFallbackService.deleteZoneFallback(id, eventUser, eventComment);
        return ResponseEntity.ok("SUCCESS");
    }

    @Operation(summary = "존 폴백(ZONE_FALLBACK) 복수 벌크 삭제", description = "선택된 TSID(ID) 리스트에 대한 In-Batch 벌크 삭제 및 ZONE_FALLBACK_HISTORY 삭제 이력 적재")
    @DeleteMapping("/batch-delete")
    public ResponseEntity<String> deleteZoneFallbacks(@RequestBody DeleteItemListDto deleteDto) {
        if (deleteDto != null && deleteDto.getIds() != null) {
            zoneFallbackService.deleteZoneFallbacks(deleteDto.getIds(), "SYSTEM", "Batch ZoneFallbacks deleted");
        }
        return ResponseEntity.ok("SUCCESS");
    }
}
