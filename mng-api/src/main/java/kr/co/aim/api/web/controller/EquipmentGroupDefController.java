package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.service.EquipmentGroupDefService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.common.dto.EquipmentGroupDefSaveRequestDto;
import kr.co.aim.common.dto.EquipmentGroupDefSearchConditionDto;
import kr.co.aim.domain.model.EquipmentGroupDef;
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

@Tag(name = "MNG Equipment Group Definition", description = "기준정보 Equipment Group Def 관리 API")
@RestController
@RequestMapping("/api/v1/mng/equipment-group-def")
@RequiredArgsConstructor
@Profile("web")
@ResponseAnnotation
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public class EquipmentGroupDefController {

    private final EquipmentGroupDefService equipmentGroupDefService;

    @Operation(summary = "Equipment Group Def 목록 조회")
    @GetMapping("")
    public ResponseEntity<Page<EquipmentGroupDef>> getEquipmentGroupDefs(
            EquipmentGroupDefSearchConditionDto condition,
            @ParameterObject Pageable pageable
    ) {
        Page<EquipmentGroupDef> result = equipmentGroupDefService.findEquipmentGroupDefWithConditions(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Equipment Group Def 상세 조회")
    @GetMapping("/{id}")
    public ResponseEntity<Page<EquipmentGroupDef>> getEquipmentGroupDef(@PathVariable("id") Long id) {
        EquipmentGroupDef result = equipmentGroupDefService.findById(id);
        return ResponseEntity.ok(new PageImpl<>(Collections.singletonList(result), PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "Equipment Group Def 생성")
    @PostMapping("")
    public ResponseEntity<Page<EquipmentGroupDef>> createEquipmentGroupDef(@RequestBody EquipmentGroupDefSaveRequestDto dto) {
        EquipmentGroupDef created = equipmentGroupDefService.createEquipmentGroupDef(dto);
        return ResponseEntity.ok(new PageImpl<>(Collections.singletonList(created), PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "Equipment Group Def 수정")
    @PutMapping("/{id}")
    public ResponseEntity<Page<EquipmentGroupDef>> updateEquipmentGroupDef(
            @PathVariable("id") Long id,
            @RequestBody EquipmentGroupDefSaveRequestDto dto
    ) {
        EquipmentGroupDef modified = equipmentGroupDefService.updateEquipmentGroupDef(dto);
        return ResponseEntity.ok(new PageImpl<>(Collections.singletonList(modified), PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "Equipment Group Def 복수 삭제")
    @DeleteMapping("/batch-delete")
    public ResponseEntity<String> deleteEquipmentGroupDefs(@RequestBody List<Long> ids) {
        equipmentGroupDefService.deleteEquipmentGroupDefs(ids);
        return ResponseEntity.ok("SUCCESS");
    }
}