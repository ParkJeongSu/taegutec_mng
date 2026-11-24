package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.dto.DeleteItemListDto;
import kr.co.aim.api.dto.FactoryCreateRequestDto;
import kr.co.aim.api.dto.FactoryResponse;
import kr.co.aim.api.dto.FactoryUpdateRequestDto;
import kr.co.aim.api.service.FactoryService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.domain.repository.condition.FactorySearchCondition;
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

@Tag(name = "MNG Factory", description = "공장(FACTORY) 관리 API")
@RestController
@RequestMapping("/api/v1/mng/factory")
@RequiredArgsConstructor
@Slf4j
@Profile({"web", "simulator"})
@ResponseAnnotation
public class FactoryController {

    private final FactoryService factoryService;

    @Operation(summary = "공장(FACTORY) 목록 조회", description = "동적 조건 및 페이징 기반 공장(FACTORY) 목록 Page 반환")
    @GetMapping("")
    public ResponseEntity<Page<FactoryResponse>> getFactorys(
            FactorySearchCondition condition,
            @ParameterObject Pageable pageable
    ) {
        Page<FactoryResponse> result = factoryService.findFactorys(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "특정 공장(FACTORY) 상세 조회", description = "TSID(Long id) 기준 공장(FACTORY) 상세 정보 Page 반환")
    @GetMapping("/{id}")
    public ResponseEntity<Page<FactoryResponse>> getFactory(@PathVariable("id") Long id) {
        FactoryResponse result = factoryService.findById(id);
        List<FactoryResponse> list = new ArrayList<>();
        list.add(result);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "신규 공장(FACTORY) 등록", description = "신규 공장(FACTORY) 등록 및 FACTORY_HISTORY 이력 적재")
    @PostMapping("")
    public ResponseEntity<Page<FactoryResponse>> createFactory(@RequestBody FactoryCreateRequestDto dto) {
        FactoryResponse created = factoryService.createFactory(dto);
        List<FactoryResponse> list = new ArrayList<>();
        list.add(created);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "공장(FACTORY) 정보 수정", description = "TSID(Long id) 기준 공장(FACTORY) 정보 수정 및 FACTORY_HISTORY 이력 적재")
    @PutMapping("/{id}")
    public ResponseEntity<Page<FactoryResponse>> updateFactory(
            @PathVariable("id") Long id,
            @RequestBody FactoryUpdateRequestDto dto
    ) {
        FactoryResponse updated = factoryService.updateFactory(id, dto);
        List<FactoryResponse> list = new ArrayList<>();
        list.add(updated);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "공장(FACTORY) 단건 삭제", description = "TSID(Long id) 기준 단건 삭제 및 FACTORY_HISTORY 삭제 이력 적재")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFactory(
            @PathVariable("id") Long id,
            @RequestParam(value = "eventUser", required = false, defaultValue = "SYSTEM") String eventUser,
            @RequestParam(value = "eventComment", required = false, defaultValue = "Factory deleted") String eventComment
    ) {
        factoryService.deleteFactory(id, eventUser, eventComment);
        return ResponseEntity.ok("SUCCESS");
    }

    @Operation(summary = "공장(FACTORY) 복수 벌크 삭제", description = "선택된 TSID(ID) 리스트에 대한 In-Batch 벌크 삭제 및 FACTORY_HISTORY 삭제 이력 적재")
    @DeleteMapping("/batch-delete")
    public ResponseEntity<String> deleteFactorys(@RequestBody DeleteItemListDto deleteDto) {
        if (deleteDto != null && deleteDto.getIds() != null) {
            factoryService.deleteFactorys(deleteDto.getIds(), "SYSTEM", "Batch Factorys deleted");
        }
        return ResponseEntity.ok("SUCCESS");
    }
}
