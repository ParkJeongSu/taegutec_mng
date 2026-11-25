package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.service.LotGroupService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.domain.repository.condition.LotGroupSearchCondition;
import kr.co.aim.domain.model.LotGroup;
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

@Tag(name = "MNG LotGroup Definition", description = "기준정보 LotGroup 관리 API")
@RestController
@RequestMapping("/api/v1/mng/lot-group")
@RequiredArgsConstructor
@Profile("web")
@ResponseAnnotation
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public class LotGroupController {

    private final LotGroupService lotGroupService;

    @Operation(summary = "LotGroup 목록 조회")
    @GetMapping("")
    public ResponseEntity<Page<LotGroup>> getLotGroups(
            LotGroupSearchCondition condition,
            @ParameterObject Pageable pageable
    ) {
        Page<LotGroup> result = lotGroupService.findLotGroupWithConditions(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "LotGroup 상세 조회")
    @GetMapping("/{id}")
    public ResponseEntity<Page<LotGroup>> getLotGroup(@PathVariable("id") Long id) {
        LotGroup result = lotGroupService.findById(id);
        return ResponseEntity.ok(new PageImpl<>(Collections.singletonList(result), PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "LotGroup 복수 삭제")
    @DeleteMapping("/batch-delete")
    public ResponseEntity<String> deleteLotGroups(@RequestBody List<Long> ids) {
        lotGroupService.deleteAllByIdInBatch(ids);
        return ResponseEntity.ok("SUCCESS");
    }
}