package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.dto.DeleteItemListDto;
import kr.co.aim.api.dto.MaterialLotCreateRequestDto;
import kr.co.aim.api.dto.MaterialLotResponse;
import kr.co.aim.api.dto.MaterialLotUpdateRequestDto;
import kr.co.aim.api.service.MaterialLotService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.domain.repository.condition.MaterialLotSearchCondition;
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

@Tag(name = "MNG MaterialLot", description = "자재 로트(MATERIAL_LOT) 관리 API")
@RestController
@RequestMapping("/api/v1/mng/material-lot")
@RequiredArgsConstructor
@Slf4j
@Profile({"web", "simulator"})
@ResponseAnnotation
public class MaterialLotController {

    private final MaterialLotService materialLotService;

    @Operation(summary = "자재 로트(MATERIAL_LOT) 목록 조회", description = "동적 조건 및 페이징 기반 자재 로트(MATERIAL_LOT) 목록 Page 반환")
    @GetMapping("")
    public ResponseEntity<Page<MaterialLotResponse>> getMaterialLots(
            MaterialLotSearchCondition condition,
            @ParameterObject Pageable pageable
    ) {
        Page<MaterialLotResponse> result = materialLotService.findMaterialLots(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "특정 자재 로트(MATERIAL_LOT) 상세 조회", description = "TSID(Long id) 기준 자재 로트(MATERIAL_LOT) 상세 정보 Page 반환")
    @GetMapping("/{id}")
    public ResponseEntity<Page<MaterialLotResponse>> getMaterialLot(@PathVariable("id") Long id) {
        MaterialLotResponse result = materialLotService.findById(id);
        List<MaterialLotResponse> list = new ArrayList<>();
        list.add(result);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "신규 자재 로트(MATERIAL_LOT) 등록", description = "신규 자재 로트(MATERIAL_LOT) 등록 및 MATERIAL_LOT_HISTORY 이력 적재")
    @PostMapping("")
    public ResponseEntity<Page<MaterialLotResponse>> createMaterialLot(@RequestBody MaterialLotCreateRequestDto dto) {
        MaterialLotResponse created = materialLotService.createMaterialLot(dto);
        List<MaterialLotResponse> list = new ArrayList<>();
        list.add(created);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "자재 로트(MATERIAL_LOT) 정보 수정", description = "TSID(Long id) 기준 자재 로트(MATERIAL_LOT) 정보 수정 및 MATERIAL_LOT_HISTORY 이력 적재")
    @PutMapping("/{id}")
    public ResponseEntity<Page<MaterialLotResponse>> updateMaterialLot(
            @PathVariable("id") Long id,
            @RequestBody MaterialLotUpdateRequestDto dto
    ) {
        MaterialLotResponse updated = materialLotService.updateMaterialLot(id, dto);
        List<MaterialLotResponse> list = new ArrayList<>();
        list.add(updated);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "자재 로트(MATERIAL_LOT) 단건 삭제", description = "TSID(Long id) 기준 단건 삭제 및 MATERIAL_LOT_HISTORY 삭제 이력 적재")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMaterialLot(
            @PathVariable("id") Long id,
            @RequestParam(value = "eventUser", required = false, defaultValue = "SYSTEM") String eventUser,
            @RequestParam(value = "eventComment", required = false, defaultValue = "MaterialLot deleted") String eventComment
    ) {
        materialLotService.deleteMaterialLot(id, eventUser, eventComment);
        return ResponseEntity.ok("SUCCESS");
    }

    @Operation(summary = "자재 로트(MATERIAL_LOT) 복수 벌크 삭제", description = "선택된 TSID(ID) 리스트에 대한 In-Batch 벌크 삭제 및 MATERIAL_LOT_HISTORY 삭제 이력 적재")
    @DeleteMapping("/batch-delete")
    public ResponseEntity<String> deleteMaterialLots(@RequestBody DeleteItemListDto deleteDto) {
        if (deleteDto != null && deleteDto.getIds() != null) {
            materialLotService.deleteMaterialLots(deleteDto.getIds(), "SYSTEM", "Batch MaterialLots deleted");
        }
        return ResponseEntity.ok("SUCCESS");
    }
}
