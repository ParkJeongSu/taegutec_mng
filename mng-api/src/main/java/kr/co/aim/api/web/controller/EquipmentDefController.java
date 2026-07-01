package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.service.EquipmentDefService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.common.dto.EquipmentDefSaveRequestDto;
import kr.co.aim.common.dto.EquipmentDefSearchConditionDto;
import kr.co.aim.domain.model.EquipmentDef;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Tag(name = "MNG Equipment Definition", description = "기준정보 Equipment Def 관리 API")
@RestController
@RequestMapping("/api/v1/mng/equipment-def")
@RequiredArgsConstructor
@Profile("web")
@ResponseAnnotation
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public class EquipmentDefController {

    private final EquipmentDefService equipmentDefService;

    @Operation(summary = "Equipment Def 목록 조회")
    @GetMapping("")
    public ResponseEntity<Page<EquipmentDef>> getEquipmentDefs(
            EquipmentDefSearchConditionDto condition,
            @ParameterObject Pageable pageable
    ) {
        Page<EquipmentDef> result = equipmentDefService.findEquipmentDefWithConditions(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Equipment Def 상세 조회")
    @GetMapping("/{id}")
    public ResponseEntity<Page<EquipmentDef>> getEquipmentDef(@PathVariable("id") Long id) {
        EquipmentDef result = equipmentDefService.findById(id);
        return ResponseEntity.ok(new PageImpl<>(Collections.singletonList(result), PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "Equipment Def 생성")
    @PostMapping("")
    public ResponseEntity<Page<EquipmentDef>> createEquipmentDef(@RequestBody EquipmentDefSaveRequestDto dto) {
        EquipmentDef created = equipmentDefService.createEquipmentDef(dto);
        return ResponseEntity.ok(new PageImpl<>(Collections.singletonList(created), PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "Equipment Def 수정")
    @PutMapping("/{id}")
    public ResponseEntity<Page<EquipmentDef>> updateEquipmentDef(
            @PathVariable("id") Long id,
            @RequestBody EquipmentDefSaveRequestDto dto
    ) {
        EquipmentDef modified = equipmentDefService.updateEquipmentDef(dto);
        return ResponseEntity.ok(new PageImpl<>(Collections.singletonList(modified), PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "Equipment Def 복수 삭제")
    @DeleteMapping("/batch-delete")
    public ResponseEntity<String> deleteEquipmentDefs(@RequestBody List<Long> ids) {
        equipmentDefService.deleteEquipmentDefs(ids);
        return ResponseEntity.ok("SUCCESS");
    }
}