package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.dto.DeleteItemListDto;
import kr.co.aim.api.dto.MaterialClassDefCreateRequestDto;
import kr.co.aim.api.dto.MaterialClassDefResponse;
import kr.co.aim.api.dto.MaterialClassDefUpdateRequestDto;
import kr.co.aim.api.service.MaterialClassDefService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.domain.repository.condition.MaterialClassDefSearchCondition;
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

@Tag(name = "MNG Material Class Def", description = "기준정보 자재 분류 정의(MATERIAL_CLASS_DEF) 관리 API")
@RestController
@RequestMapping("/api/v1/mng/material-class-def")
@RequiredArgsConstructor
@Slf4j
@Profile({"web", "simulator"})
@ResponseAnnotation
public class MaterialClassDefController {

    private final MaterialClassDefService materialClassDefService;

    @Operation(summary = "자재 분류 정의 목록 조회", description = "동적 조건 및 페이징 기반 자재 분류 정의 목록 Page 반환")
    @GetMapping("")
    public ResponseEntity<Page<MaterialClassDefResponse>> getMaterialClassDefs(
            MaterialClassDefSearchCondition condition,
            @ParameterObject Pageable pageable
    ) {
        Page<MaterialClassDefResponse> result = materialClassDefService.findMaterialClassDefs(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "특정 자재 분류 정의 상세 조회", description = "TSID(Long id) 기준 자재 분류 정의 상세 정보 Page 반환")
    @GetMapping("/{id}")
    public ResponseEntity<Page<MaterialClassDefResponse>> getMaterialClassDef(@PathVariable("id") Long id) {
        MaterialClassDefResponse result = materialClassDefService.findById(id);
        List<MaterialClassDefResponse> list = new ArrayList<>();
        list.add(result);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "신규 자재 분류 정의 등록", description = "신규 자재 분류 정의 등록 및 MATERIAL_CLASS_DEF_HISTORY 이력 적재")
    @PostMapping("")
    public ResponseEntity<Page<MaterialClassDefResponse>> createMaterialClassDef(@RequestBody MaterialClassDefCreateRequestDto dto) {
        MaterialClassDefResponse created = materialClassDefService.createMaterialClassDef(dto);
        List<MaterialClassDefResponse> list = new ArrayList<>();
        list.add(created);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "자재 분류 정의 정보 수정", description = "TSID(Long id) 기준 자재 분류 정의 정보 수정 및 이력 적재")
    @PutMapping("/{id}")
    public ResponseEntity<Page<MaterialClassDefResponse>> updateMaterialClassDef(
            @PathVariable("id") Long id,
            @RequestBody MaterialClassDefUpdateRequestDto dto
    ) {
        MaterialClassDefResponse updated = materialClassDefService.updateMaterialClassDef(id, dto);
        List<MaterialClassDefResponse> list = new ArrayList<>();
        list.add(updated);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "자재 분류 정의 단건 삭제", description = "TSID(Long id) 기준 단건 삭제 및 삭제 이력 적재")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMaterialClassDef(
            @PathVariable("id") Long id,
            @RequestParam(value = "eventUser", required = false, defaultValue = "SYSTEM") String eventUser,
            @RequestParam(value = "eventComment", required = false, defaultValue = "Material class def deleted") String eventComment
    ) {
        materialClassDefService.deleteMaterialClassDef(id, eventUser, eventComment);
        return ResponseEntity.ok("SUCCESS");
    }

    @Operation(summary = "자재 분류 정의 복수 벌크 삭제", description = "선택된 TSID(ID) 리스트에 대한 In-Batch 벌크 삭제 및 삭제 이력 적재")
    @DeleteMapping("/batch-delete")
    public ResponseEntity<String> deleteMaterialClassDefs(@RequestBody DeleteItemListDto deleteDto) {
        if (deleteDto != null && deleteDto.getIds() != null) {
            materialClassDefService.deleteMaterialClassDefs(deleteDto.getIds(), "SYSTEM", "Batch material class defs deleted");
        }
        return ResponseEntity.ok("SUCCESS");
    }
}
