package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.service.PortDefService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.common.dto.PortDefSaveRequestDto;
import kr.co.aim.common.dto.PortDefSearchConditionDto;
import kr.co.aim.domain.model.PortDef;
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

@Tag(name = "MNG Port Definition", description = "기준정보 Port Def 관리 API")
@RestController
@RequestMapping("/api/v1/mng/port-def")
@RequiredArgsConstructor
@Profile("web")
@ResponseAnnotation
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public class PortDefController {

    private final PortDefService portDefService;

    @Operation(summary = "Port Def 목록 조회")
    @GetMapping("")
    public ResponseEntity<Page<PortDef>> getPortDefs(
            PortDefSearchConditionDto condition,
            @ParameterObject Pageable pageable
    ) {
        Page<PortDef> result = portDefService.findPortDefWithConditions(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Port Def 상세 조회")
    @GetMapping("/{id}")
    public ResponseEntity<Page<PortDef>> getPortDef(@PathVariable("id") Long id) {
        PortDef result = portDefService.findById(id);
        return ResponseEntity.ok(new PageImpl<>(Collections.singletonList(result), PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "Port Def 생성")
    @PostMapping("")
    public ResponseEntity<Page<PortDef>> createPortDef(@RequestBody PortDefSaveRequestDto dto) {
        PortDef created = portDefService.createPortDef(dto);
        return ResponseEntity.ok(new PageImpl<>(Collections.singletonList(created), PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "Port Def 수정")
    @PutMapping("/{id}")
    public ResponseEntity<Page<PortDef>> updatePortDef(
            @PathVariable("id") Long id,
            @RequestBody PortDefSaveRequestDto dto
    ) {
        PortDef modified = portDefService.updatePortDef(dto);
        return ResponseEntity.ok(new PageImpl<>(Collections.singletonList(modified), PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "Port Def 복수 삭제")
    @DeleteMapping("/batch-delete")
    public ResponseEntity<String> deletePortDefs(@RequestBody List<Long> ids) {
        portDefService.deletePortDefs(ids);
        return ResponseEntity.ok("SUCCESS");
    }
}
