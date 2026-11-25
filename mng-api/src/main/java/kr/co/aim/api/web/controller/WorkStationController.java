package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.dto.DeleteItemListDto;
import kr.co.aim.api.dto.WorkStationCreateRequestDto;
import kr.co.aim.api.dto.WorkStationResponse;
import kr.co.aim.api.dto.WorkStationUpdateRequestDto;
import kr.co.aim.api.service.WorkStationService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.domain.repository.condition.WorkStationSearchCondition;
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

@Tag(name = "MNG WorkStation", description = "작업 스테이션(WORK_STATION) 관리 API")
@RestController
@RequestMapping("/api/v1/mng/work-station")
@RequiredArgsConstructor
@Slf4j
@Profile({"web", "simulator"})
@ResponseAnnotation
public class WorkStationController {

    private final WorkStationService workStationService;

    @Operation(summary = "작업 스테이션(WORK_STATION) 목록 조회", description = "동적 조건 및 페이징 기반 작업 스테이션(WORK_STATION) 목록 Page 반환")
    @GetMapping("")
    public ResponseEntity<Page<WorkStationResponse>> getWorkStations(
            WorkStationSearchCondition condition,
            @ParameterObject Pageable pageable
    ) {
        Page<WorkStationResponse> result = workStationService.findWorkStations(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "특정 작업 스테이션(WORK_STATION) 상세 조회", description = "TSID(Long id) 기준 작업 스테이션(WORK_STATION) 상세 정보 Page 반환")
    @GetMapping("/{id}")
    public ResponseEntity<Page<WorkStationResponse>> getWorkStation(@PathVariable("id") Long id) {
        WorkStationResponse result = workStationService.findById(id);
        List<WorkStationResponse> list = new ArrayList<>();
        list.add(result);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "신규 작업 스테이션(WORK_STATION) 등록", description = "신규 작업 스테이션(WORK_STATION) 등록 및 WORK_STATION_HISTORY 이력 적재")
    @PostMapping("")
    public ResponseEntity<Page<WorkStationResponse>> createWorkStation(@RequestBody WorkStationCreateRequestDto dto) {
        WorkStationResponse created = workStationService.createWorkStation(dto);
        List<WorkStationResponse> list = new ArrayList<>();
        list.add(created);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "작업 스테이션(WORK_STATION) 정보 수정", description = "TSID(Long id) 기준 작업 스테이션(WORK_STATION) 정보 수정 및 WORK_STATION_HISTORY 이력 적재")
    @PutMapping("/{id}")
    public ResponseEntity<Page<WorkStationResponse>> updateWorkStation(
            @PathVariable("id") Long id,
            @RequestBody WorkStationUpdateRequestDto dto
    ) {
        WorkStationResponse updated = workStationService.updateWorkStation(id, dto);
        List<WorkStationResponse> list = new ArrayList<>();
        list.add(updated);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "작업 스테이션(WORK_STATION) 단건 삭제", description = "TSID(Long id) 기준 단건 삭제 및 WORK_STATION_HISTORY 삭제 이력 적재")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteWorkStation(
            @PathVariable("id") Long id,
            @RequestParam(value = "eventUser", required = false, defaultValue = "SYSTEM") String eventUser,
            @RequestParam(value = "eventComment", required = false, defaultValue = "WorkStation deleted") String eventComment
    ) {
        workStationService.deleteWorkStation(id, eventUser, eventComment);
        return ResponseEntity.ok("SUCCESS");
    }

    @Operation(summary = "작업 스테이션(WORK_STATION) 복수 벌크 삭제", description = "선택된 TSID(ID) 리스트에 대한 In-Batch 벌크 삭제 및 WORK_STATION_HISTORY 삭제 이력 적재")
    @DeleteMapping("/batch-delete")
    public ResponseEntity<String> deleteWorkStations(@RequestBody DeleteItemListDto deleteDto) {
        if (deleteDto != null && deleteDto.getIds() != null) {
            workStationService.deleteWorkStations(deleteDto.getIds(), "SYSTEM", "Batch WorkStations deleted");
        }
        return ResponseEntity.ok("SUCCESS");
    }
}
