package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.dto.DeleteItemListDto;
import kr.co.aim.api.dto.InsertAutoRelocationTestCreateRequestDto;
import kr.co.aim.api.dto.InsertAutoRelocationTestResponse;
import kr.co.aim.api.dto.InsertAutoRelocationTestUpdateRequestDto;
import kr.co.aim.api.service.InsertAutoRelocationTestService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.domain.repository.condition.InsertAutoRelocationTestSearchCondition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "MNG Insert Auto Relocation Test", description = "INSERT 공장 자동 재배치 테스트 관리 API")
@RestController
@RequestMapping("/api/v1/mng/insert-auto-relocation-test")
@RequiredArgsConstructor
@Slf4j
@Profile({"web", "simulator"})
@ResponseAnnotation
@ConditionalOnProperty(name = "factory.type", havingValue = "insert")
public class InsertAutoRelocationTestController {

    private final InsertAutoRelocationTestService insertAutoRelocationTestService;

    @Operation(summary = "자동 재배치 테스트 목록 조회", description = "동적 조건 및 페이징 기반 INSERT 자동 재배치 테스트 목록 Page 반환")
    @GetMapping("")
    public ResponseEntity<Page<InsertAutoRelocationTestResponse>> getAutoRelocationTests(
            InsertAutoRelocationTestSearchCondition condition,
            @ParameterObject Pageable pageable
    ) {
        Page<InsertAutoRelocationTestResponse> result = insertAutoRelocationTestService.findAutoRelocationTests(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "특정 자동 재배치 테스트 상세 조회", description = "TSID(Long id) 기준 자동 재배치 테스트 상세 정보 Page 반환")
    @GetMapping("/{id}")
    public ResponseEntity<Page<InsertAutoRelocationTestResponse>> getAutoRelocationTest(@PathVariable("id") Long id) {
        InsertAutoRelocationTestResponse result = insertAutoRelocationTestService.findById(id);
        List<InsertAutoRelocationTestResponse> list = new ArrayList<>();
        list.add(result);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "신규 자동 재배치 테스트 등록", description = "신규 INSERT 자동 재배치 테스트 시나리오 등록")
    @PostMapping("")
    public ResponseEntity<Page<InsertAutoRelocationTestResponse>> createAutoRelocationTest(@RequestBody InsertAutoRelocationTestCreateRequestDto dto) {
        InsertAutoRelocationTestResponse created = insertAutoRelocationTestService.createAutoRelocationTest(dto);
        List<InsertAutoRelocationTestResponse> list = new ArrayList<>();
        list.add(created);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "자동 재배치 테스트 정보 수정", description = "TSID(Long id) 기준 자동 재배치 테스트 설정 정보 수정")
    @PutMapping("/{id}")
    public ResponseEntity<Page<InsertAutoRelocationTestResponse>> updateAutoRelocationTest(
            @PathVariable("id") Long id,
            @RequestBody InsertAutoRelocationTestUpdateRequestDto dto
    ) {
        InsertAutoRelocationTestResponse updated = insertAutoRelocationTestService.updateAutoRelocationTest(id, dto);
        List<InsertAutoRelocationTestResponse> list = new ArrayList<>();
        list.add(updated);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "자동 재배치 테스트 단건 삭제", description = "TSID(Long id) 기준 자동 재배치 테스트 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAutoRelocationTest(@PathVariable("id") Long id) {
        insertAutoRelocationTestService.deleteAutoRelocationTest(id);
        return ResponseEntity.ok("SUCCESS");
    }

    @Operation(summary = "자동 재배치 테스트 복수 벌크 삭제", description = "선택된 TSID(ID) 리스트에 대한 In-Batch 벌크 삭제")
    @DeleteMapping("/batch-delete")
    public ResponseEntity<String> deleteAutoRelocationTests(@RequestBody DeleteItemListDto deleteDto) {
        if (deleteDto != null && deleteDto.getIds() != null) {
            insertAutoRelocationTestService.deleteAutoRelocationTests(deleteDto.getIds());
        }
        return ResponseEntity.ok("SUCCESS");
    }
}
