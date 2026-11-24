package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.dto.DeleteItemListDto;
import kr.co.aim.api.dto.MaterialCreateRequestDto;
import kr.co.aim.api.dto.MaterialResponse;
import kr.co.aim.api.dto.MaterialUpdateRequestDto;
import kr.co.aim.api.service.MaterialService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.domain.repository.condition.MaterialSearchCondition;
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

@Tag(name = "MNG Material", description = "자재(MATERIAL) 관리 API")
@RestController
@RequestMapping("/api/v1/mng/material")
@RequiredArgsConstructor
@Slf4j
@Profile({"web", "simulator"})
@ResponseAnnotation
public class MaterialController {

    private final MaterialService materialService;

    @Operation(summary = "자재(MATERIAL) 목록 조회", description = "동적 조건 및 페이징 기반 자재(MATERIAL) 목록 Page 반환")
    @GetMapping("")
    public ResponseEntity<Page<MaterialResponse>> getMaterials(
            MaterialSearchCondition condition,
            @ParameterObject Pageable pageable
    ) {
        Page<MaterialResponse> result = materialService.findMaterials(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "특정 자재(MATERIAL) 상세 조회", description = "TSID(Long id) 기준 자재(MATERIAL) 상세 정보 Page 반환")
    @GetMapping("/{id}")
    public ResponseEntity<Page<MaterialResponse>> getMaterial(@PathVariable("id") Long id) {
        MaterialResponse result = materialService.findById(id);
        List<MaterialResponse> list = new ArrayList<>();
        list.add(result);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "신규 자재(MATERIAL) 등록", description = "신규 자재(MATERIAL) 등록 및 MATERIAL_HISTORY 이력 적재")
    @PostMapping("")
    public ResponseEntity<Page<MaterialResponse>> createMaterial(@RequestBody MaterialCreateRequestDto dto) {
        MaterialResponse created = materialService.createMaterial(dto);
        List<MaterialResponse> list = new ArrayList<>();
        list.add(created);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "자재(MATERIAL) 정보 수정", description = "TSID(Long id) 기준 자재(MATERIAL) 정보 수정 및 MATERIAL_HISTORY 이력 적재")
    @PutMapping("/{id}")
    public ResponseEntity<Page<MaterialResponse>> updateMaterial(
            @PathVariable("id") Long id,
            @RequestBody MaterialUpdateRequestDto dto
    ) {
        MaterialResponse updated = materialService.updateMaterial(id, dto);
        List<MaterialResponse> list = new ArrayList<>();
        list.add(updated);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "자재(MATERIAL) 단건 삭제", description = "TSID(Long id) 기준 단건 삭제 및 MATERIAL_HISTORY 삭제 이력 적재")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMaterial(
            @PathVariable("id") Long id,
            @RequestParam(value = "eventUser", required = false, defaultValue = "SYSTEM") String eventUser,
            @RequestParam(value = "eventComment", required = false, defaultValue = "Material deleted") String eventComment
    ) {
        materialService.deleteMaterial(id, eventUser, eventComment);
        return ResponseEntity.ok("SUCCESS");
    }

    @Operation(summary = "자재(MATERIAL) 복수 벌크 삭제", description = "선택된 TSID(ID) 리스트에 대한 In-Batch 벌크 삭제 및 MATERIAL_HISTORY 삭제 이력 적재")
    @DeleteMapping("/batch-delete")
    public ResponseEntity<String> deleteMaterials(@RequestBody DeleteItemListDto deleteDto) {
        if (deleteDto != null && deleteDto.getIds() != null) {
            materialService.deleteMaterials(deleteDto.getIds(), "SYSTEM", "Batch Materials deleted");
        }
        return ResponseEntity.ok("SUCCESS");
    }
}
