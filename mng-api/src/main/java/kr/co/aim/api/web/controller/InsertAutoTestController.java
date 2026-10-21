package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.dto.DeleteItemListDto;
import kr.co.aim.api.dto.InsertAutoTestCreateRequestDto;
import kr.co.aim.api.dto.InsertAutoTestResponse;
import kr.co.aim.api.dto.InsertAutoTestResultCreateRequestDto;
import kr.co.aim.api.dto.InsertAutoTestResultResponse;
import kr.co.aim.api.dto.InsertAutoTestUpdateRequestDto;
import kr.co.aim.api.service.InsertAutoTestService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.domain.repository.condition.InsertAutoTestResultSearchCondition;
import kr.co.aim.domain.repository.condition.InsertAutoTestSearchCondition;
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

@Tag(name = "MNG Insert Auto Test", description = "INSERT 공장 WCS <-> PLC 자동 입출고 왕복 테스트 관리 API")
@RestController
@RequestMapping("/api/v1/mng/insert-auto-test")
@RequiredArgsConstructor
@Slf4j
@Profile({"web","simulator"})
@ResponseAnnotation
public class InsertAutoTestController {

    private final InsertAutoTestService insertAutoTestService;

    @Operation(summary = "자동 테스트 목록 조회", description = "동적 조건 및 페이징 기반 INSERT 자동 테스트 목록 Page 반환")
    @GetMapping("")
    public ResponseEntity<Page<InsertAutoTestResponse>> getAutoTests(
            InsertAutoTestSearchCondition condition,
            @ParameterObject Pageable pageable
    ) {
        Page<InsertAutoTestResponse> result = insertAutoTestService.findAutoTests(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "특정 자동 테스트 상세 조회", description = "TSID(Long id) 기준 자동 테스트 상세 정보 Page 반환")
    @GetMapping("/{id}")
    public ResponseEntity<Page<InsertAutoTestResponse>> getAutoTest(@PathVariable("id") Long id) {
        InsertAutoTestResponse result = insertAutoTestService.findById(id);
        List<InsertAutoTestResponse> list = new ArrayList<>();
        list.add(result);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "신규 자동 테스트 등록", description = "신규 INSERT 자동 테스트 시나리오 등록")
    @PostMapping("")
    public ResponseEntity<Page<InsertAutoTestResponse>> createAutoTest(@RequestBody InsertAutoTestCreateRequestDto dto) {
        InsertAutoTestResponse created = insertAutoTestService.createAutoTest(dto);
        List<InsertAutoTestResponse> list = new ArrayList<>();
        list.add(created);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "자동 테스트 정보 수정", description = "TSID(Long id) 기준 자동 테스트 설정 정보 수정")
    @PutMapping("/{id}")
    public ResponseEntity<Page<InsertAutoTestResponse>> updateAutoTest(
            @PathVariable("id") Long id,
            @RequestBody InsertAutoTestUpdateRequestDto dto
    ) {
        InsertAutoTestResponse updated = insertAutoTestService.updateAutoTest(id, dto);
        List<InsertAutoTestResponse> list = new ArrayList<>();
        list.add(updated);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "자동 테스트 단건 삭제", description = "TSID(Long id) 기준 자동 테스트 및 연관 결과 이력 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAutoTest(@PathVariable("id") Long id) {
        insertAutoTestService.deleteAutoTest(id);
        return ResponseEntity.ok("SUCCESS");
    }

    @Operation(summary = "자동 테스트 복수 벌크 삭제", description = "선택된 TSID(ID) 리스트에 대한 In-Batch 벌크 삭제 및 연관 결과 이력 삭제")
    @DeleteMapping("/batch-delete")
    public ResponseEntity<String> deleteAutoTests(@RequestBody DeleteItemListDto deleteDto) {
        if (deleteDto != null && deleteDto.getIds() != null) {
            insertAutoTestService.deleteAutoTests(deleteDto.getIds());
        }
        return ResponseEntity.ok("SUCCESS");
    }

    @Operation(summary = "자동 테스트 결과 이력 등록", description = "특정 자동 테스트의 왕복 테스트 수행 결과 이력 적재")
    @PostMapping("/{id}/results")
    public ResponseEntity<Page<InsertAutoTestResultResponse>> addTestResult(
            @PathVariable("id") Long id,
            @RequestBody InsertAutoTestResultCreateRequestDto dto
    ) {
        InsertAutoTestResultResponse created = insertAutoTestService.addTestResult(id, dto);
        List<InsertAutoTestResultResponse> list = new ArrayList<>();
        list.add(created);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "특정 자동 테스트의 결과 이력 목록 조회", description = "특정 자동 테스트 ID(TSID)에 대한 결과 이력 리스트 조회")
    @GetMapping("/{id}/results")
    public ResponseEntity<List<InsertAutoTestResultResponse>> getTestResultsByAutoTestId(@PathVariable("id") Long id) {
        List<InsertAutoTestResultResponse> results = insertAutoTestService.findTestResultsByAutoTestId(id);
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "전체 자동 테스트 결과 이력 목록 조회 (조건/페이징)", description = "동적 조건 및 페이징 기반 전체 자동 테스트 수행 결과 이력 Page 반환")
    @GetMapping("/results")
    public ResponseEntity<Page<InsertAutoTestResultResponse>> getTestResults(
            InsertAutoTestResultSearchCondition condition,
            @ParameterObject Pageable pageable
    ) {
        Page<InsertAutoTestResultResponse> result = insertAutoTestService.findTestResults(condition, pageable);
        return ResponseEntity.ok(result);
    }
}
