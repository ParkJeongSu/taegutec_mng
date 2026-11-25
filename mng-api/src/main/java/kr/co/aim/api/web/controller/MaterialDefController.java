package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.dto.DeleteItemListDto;
import kr.co.aim.api.dto.MaterialDefCreateRequestDto;
import kr.co.aim.api.dto.MaterialDefResponse;
import kr.co.aim.api.dto.MaterialDefUpdateRequestDto;
import kr.co.aim.api.service.MaterialDefService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.domain.repository.condition.MaterialDefSearchCondition;
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

@Tag(name = "MNG MaterialDef", description = "자재 정의(MATERIAL_DEF) 관리 API")
@RestController
@RequestMapping("/api/v1/mng/material-def")
@RequiredArgsConstructor
@Slf4j
@Profile({"web", "simulator"})
@ResponseAnnotation
public class MaterialDefController {

    private final MaterialDefService materialDefService;

    @Operation(summary = "자재 정의(MATERIAL_DEF) 목록 조회", description = "동적 조건 및 페이징 기반 자재 정의(MATERIAL_DEF) 목록 Page 반환")
    @GetMapping("")
    public ResponseEntity<Page<MaterialDefResponse>> getMaterialDefs(
            MaterialDefSearchCondition condition,
            @ParameterObject Pageable pageable
    ) {
        Page<MaterialDefResponse> result = materialDefService.findMaterialDefs(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "특정 자재 정의(MATERIAL_DEF) 상세 조회", description = "TSID(Long id) 기준 자재 정의(MATERIAL_DEF) 상세 정보 Page 반환")
    @GetMapping("/{id}")
    public ResponseEntity<Page<MaterialDefResponse>> getMaterialDef(@PathVariable("id") Long id) {
        MaterialDefResponse result = materialDefService.findById(id);
        List<MaterialDefResponse> list = new ArrayList<>();
        list.add(result);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "신규 자재 정의(MATERIAL_DEF) 등록", description = "신규 자재 정의(MATERIAL_DEF) 등록 및 MATERIAL_DEF_HISTORY 이력 적재")
    @PostMapping("")
    public ResponseEntity<Page<MaterialDefResponse>> createMaterialDef(@RequestBody MaterialDefCreateRequestDto dto) {
        MaterialDefResponse created = materialDefService.createMaterialDef(dto);
        List<MaterialDefResponse> list = new ArrayList<>();
        list.add(created);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "자재 정의(MATERIAL_DEF) 정보 수정", description = "TSID(Long id) 기준 자재 정의(MATERIAL_DEF) 정보 수정 및 MATERIAL_DEF_HISTORY 이력 적재")
    @PutMapping("/{id}")
    public ResponseEntity<Page<MaterialDefResponse>> updateMaterialDef(
            @PathVariable("id") Long id,
            @RequestBody MaterialDefUpdateRequestDto dto
    ) {
        MaterialDefResponse updated = materialDefService.updateMaterialDef(id, dto);
        List<MaterialDefResponse> list = new ArrayList<>();
        list.add(updated);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "자재 정의(MATERIAL_DEF) 단건 삭제", description = "TSID(Long id) 기준 단건 삭제 및 MATERIAL_DEF_HISTORY 삭제 이력 적재")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMaterialDef(
            @PathVariable("id") Long id,
            @RequestParam(value = "eventUser", required = false, defaultValue = "SYSTEM") String eventUser,
            @RequestParam(value = "eventComment", required = false, defaultValue = "MaterialDef deleted") String eventComment
    ) {
        materialDefService.deleteMaterialDef(id, eventUser, eventComment);
        return ResponseEntity.ok("SUCCESS");
    }

    @Operation(summary = "자재 정의(MATERIAL_DEF) 복수 벌크 삭제", description = "선택된 TSID(ID) 리스트에 대한 In-Batch 벌크 삭제 및 MATERIAL_DEF_HISTORY 삭제 이력 적재")
    @DeleteMapping("/batch-delete")
    public ResponseEntity<String> deleteMaterialDefs(@RequestBody DeleteItemListDto deleteDto) {
        if (deleteDto != null && deleteDto.getIds() != null) {
            materialDefService.deleteMaterialDefs(deleteDto.getIds(), "SYSTEM", "Batch MaterialDefs deleted");
        }
        return ResponseEntity.ok("SUCCESS");
    }
}
