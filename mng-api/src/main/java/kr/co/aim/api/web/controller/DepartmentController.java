package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.dto.DeleteItemListDto;
import kr.co.aim.api.dto.DepartmentCreateRequestDto;
import kr.co.aim.api.dto.DepartmentResponse;
import kr.co.aim.api.dto.DepartmentUpdateRequestDto;
import kr.co.aim.api.service.DepartmentService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.common.condition.DepartmentSearchCondition;
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

@Tag(name = "MNG Department", description = "기준정보 부서(DEPARTMENT) 관리 API")
@RestController
@RequestMapping("/api/v1/mng/department")
@RequiredArgsConstructor
@Slf4j
@Profile("web")
@ResponseAnnotation
public class DepartmentController {

    private final DepartmentService departmentService;

    @Operation(summary = "부서 목록 조회", description = "동적 조건 및 페이징 부서 목록 Page 반환")
    @GetMapping("")
    public ResponseEntity<Page<DepartmentResponse>> getDepartments(
            DepartmentSearchCondition condition,
            @ParameterObject Pageable pageable
    ) {
        Page<DepartmentResponse> result = departmentService.findDepartments(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "특정 부서 상세 조회", description = "TSID(Long id) 기준 부서 상세 정보 Page 반환")
    @GetMapping("/{id}")
    public ResponseEntity<Page<DepartmentResponse>> getDepartment(@PathVariable("id") Long id) {
        DepartmentResponse result = departmentService.findById(id);
        List<DepartmentResponse> list = new ArrayList<>();
        list.add(result);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "신규 부서 등록", description = "신규 부서 등록 및 DEPARTMENT_HISTORY 이력 적재")
    @PostMapping("")
    public ResponseEntity<Page<DepartmentResponse>> createDepartment(@RequestBody DepartmentCreateRequestDto dto) {
        DepartmentResponse created = departmentService.createDepartment(dto);
        List<DepartmentResponse> list = new ArrayList<>();
        list.add(created);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "부서 정보 수정", description = "TSID(Long id) 기준 부서 정보 수정 및 동일 TSID 기준 이력 적재")
    @PutMapping("/{id}")
    public ResponseEntity<Page<DepartmentResponse>> updateDepartment(
            @PathVariable("id") Long id,
            @RequestBody DepartmentUpdateRequestDto dto
    ) {
        DepartmentResponse updated = departmentService.updateDepartment(id, dto);
        List<DepartmentResponse> list = new ArrayList<>();
        list.add(updated);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "부서 단건 삭제", description = "TSID(Long id) 기준 단건 삭제 및 삭제 이력 적재")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDepartment(
            @PathVariable("id") Long id,
            @RequestParam(value = "eventUser", required = false, defaultValue = "SYSTEM") String eventUser,
            @RequestParam(value = "eventComment", required = false, defaultValue = "Department deleted") String eventComment
    ) {
        departmentService.deleteDepartment(id, eventUser, eventComment);
        return ResponseEntity.ok("SUCCESS");
    }

    @Operation(summary = "부서 복수 벌크 삭제", description = "선택된 TSID(ID) 리스트에 대한 In-Batch 벌크 삭제 및 이력 적재")
    @DeleteMapping("/batch-delete")
    public ResponseEntity<String> deleteDepartments(@RequestBody DeleteItemListDto deleteDto) {
        if (deleteDto != null && deleteDto.getIds() != null) {
            departmentService.deleteDepartments(deleteDto.getIds(), "SYSTEM", "Batch departments deleted");
        }
        return ResponseEntity.ok("SUCCESS");
    }
}
