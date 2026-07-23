package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.service.LotCarrierMappingService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.common.condition.LotCarrierMappingSearchCondition;
import kr.co.aim.domain.model.LotCarrierMapping;
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

@Tag(name = "MNG Lot Carrier Mapping", description = "기준정보 Lot Carrier Mapping 관리 API")
@RestController
@RequestMapping("/api/v1/mng/lot-carrier-mapping")
@RequiredArgsConstructor
@Profile("web")
@ResponseAnnotation
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public class LotCarrierMappingController {

    private final LotCarrierMappingService lotCarrierMappingService;

    @Operation(summary = "Lot Carrier Mapping 목록 조회")
    @GetMapping("")
    public ResponseEntity<Page<LotCarrierMapping>> getLotCarrierMappings(
            LotCarrierMappingSearchCondition condition,
            @ParameterObject Pageable pageable
    ) {
        Page<LotCarrierMapping> result = lotCarrierMappingService.findLotCarrierMappingWithConditions(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Lot Carrier Mapping 상세 조회")
    @GetMapping("/{id}")
    public ResponseEntity<Page<LotCarrierMapping>> getLotCarrierMapping(@PathVariable("id") Long id) {
        LotCarrierMapping result = lotCarrierMappingService.findById(id);
        return ResponseEntity.ok(new PageImpl<>(Collections.singletonList(result), PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "Lot Carrier Mapping 복수 삭제")
    @DeleteMapping("/batch-delete")
    public ResponseEntity<String> deleteLotCarrierMappings(@RequestBody List<Long> ids) {
        lotCarrierMappingService.deleteAllByIdInBatch(ids);
        return ResponseEntity.ok("SUCCESS");
    }
}