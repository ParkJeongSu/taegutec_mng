package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.dto.DeleteItemListDto;
import kr.co.aim.api.dto.AreaCreateRequestDto;
import kr.co.aim.api.dto.AreaResponse;
import kr.co.aim.api.dto.AreaUpdateRequestDto;
import kr.co.aim.api.service.AreaService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.domain.repository.condition.AreaSearchCondition;
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

@Tag(name = "MNG Area", description = "구역(AREA) 관리 API")
@RestController
@RequestMapping("/api/v1/mng/area")
@RequiredArgsConstructor
@Slf4j
@Profile({"web", "simulator"})
@ResponseAnnotation
public class AreaController {

    private final AreaService areaService;

    @Operation(summary = "구역(AREA) 목록 조회", description = "동적 조건 및 페이징 기반 구역(AREA) 목록 Page 반환")
    @GetMapping("")
    public ResponseEntity<Page<AreaResponse>> getAreas(
            AreaSearchCondition condition,
            @ParameterObject Pageable pageable
    ) {
        Page<AreaResponse> result = areaService.findAreas(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "특정 구역(AREA) 상세 조회", description = "TSID(Long id) 기준 구역(AREA) 상세 정보 Page 반환")
    @GetMapping("/{id}")
    public ResponseEntity<Page<AreaResponse>> getArea(@PathVariable("id") Long id) {
        AreaResponse result = areaService.findById(id);
        List<AreaResponse> list = new ArrayList<>();
        list.add(result);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "신규 구역(AREA) 등록", description = "신규 구역(AREA) 등록 및 AREA_HISTORY 이력 적재")
    @PostMapping("")
    public ResponseEntity<Page<AreaResponse>> createArea(@RequestBody AreaCreateRequestDto dto) {
        AreaResponse created = areaService.createArea(dto);
        List<AreaResponse> list = new ArrayList<>();
        list.add(created);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "구역(AREA) 정보 수정", description = "TSID(Long id) 기준 구역(AREA) 정보 수정 및 AREA_HISTORY 이력 적재")
    @PutMapping("/{id}")
    public ResponseEntity<Page<AreaResponse>> updateArea(
            @PathVariable("id") Long id,
            @RequestBody AreaUpdateRequestDto dto
    ) {
        AreaResponse updated = areaService.updateArea(id, dto);
        List<AreaResponse> list = new ArrayList<>();
        list.add(updated);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "구역(AREA) 단건 삭제", description = "TSID(Long id) 기준 단건 삭제 및 AREA_HISTORY 삭제 이력 적재")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteArea(
            @PathVariable("id") Long id,
            @RequestParam(value = "eventUser", required = false, defaultValue = "SYSTEM") String eventUser,
            @RequestParam(value = "eventComment", required = false, defaultValue = "Area deleted") String eventComment
    ) {
        areaService.deleteArea(id, eventUser, eventComment);
        return ResponseEntity.ok("SUCCESS");
    }

    @Operation(summary = "구역(AREA) 복수 벌크 삭제", description = "선택된 TSID(ID) 리스트에 대한 In-Batch 벌크 삭제 및 AREA_HISTORY 삭제 이력 적재")
    @DeleteMapping("/batch-delete")
    public ResponseEntity<String> deleteAreas(@RequestBody DeleteItemListDto deleteDto) {
        if (deleteDto != null && deleteDto.getIds() != null) {
            areaService.deleteAreas(deleteDto.getIds(), "SYSTEM", "Batch Areas deleted");
        }
        return ResponseEntity.ok("SUCCESS");
    }
}
