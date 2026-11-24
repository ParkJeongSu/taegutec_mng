package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.dto.DeleteItemListDto;
import kr.co.aim.api.dto.ShelfCreateRequestDto;
import kr.co.aim.api.dto.ShelfResponse;
import kr.co.aim.api.dto.ShelfUpdateRequestDto;
import kr.co.aim.api.service.ShelfService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.domain.repository.condition.ShelfSearchCondition;
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

@Tag(name = "MNG Shelf", description = "선반(SHELF) 관리 API")
@RestController
@RequestMapping("/api/v1/mng/shelf")
@RequiredArgsConstructor
@Slf4j
@Profile({"web", "simulator"})
@ResponseAnnotation
public class ShelfController {

    private final ShelfService shelfService;

    @Operation(summary = "선반(SHELF) 목록 조회", description = "동적 조건 및 페이징 기반 선반(SHELF) 목록 Page 반환")
    @GetMapping("")
    public ResponseEntity<Page<ShelfResponse>> getShelfs(
            ShelfSearchCondition condition,
            @ParameterObject Pageable pageable
    ) {
        Page<ShelfResponse> result = shelfService.findShelfs(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "특정 선반(SHELF) 상세 조회", description = "TSID(Long id) 기준 선반(SHELF) 상세 정보 Page 반환")
    @GetMapping("/{id}")
    public ResponseEntity<Page<ShelfResponse>> getShelf(@PathVariable("id") Long id) {
        ShelfResponse result = shelfService.findById(id);
        List<ShelfResponse> list = new ArrayList<>();
        list.add(result);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "신규 선반(SHELF) 등록", description = "신규 선반(SHELF) 등록 및 SHELF_HISTORY 이력 적재")
    @PostMapping("")
    public ResponseEntity<Page<ShelfResponse>> createShelf(@RequestBody ShelfCreateRequestDto dto) {
        ShelfResponse created = shelfService.createShelf(dto);
        List<ShelfResponse> list = new ArrayList<>();
        list.add(created);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "선반(SHELF) 정보 수정", description = "TSID(Long id) 기준 선반(SHELF) 정보 수정 및 SHELF_HISTORY 이력 적재")
    @PutMapping("/{id}")
    public ResponseEntity<Page<ShelfResponse>> updateShelf(
            @PathVariable("id") Long id,
            @RequestBody ShelfUpdateRequestDto dto
    ) {
        ShelfResponse updated = shelfService.updateShelf(id, dto);
        List<ShelfResponse> list = new ArrayList<>();
        list.add(updated);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "선반(SHELF) 단건 삭제", description = "TSID(Long id) 기준 단건 삭제 및 SHELF_HISTORY 삭제 이력 적재")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteShelf(
            @PathVariable("id") Long id,
            @RequestParam(value = "eventUser", required = false, defaultValue = "SYSTEM") String eventUser,
            @RequestParam(value = "eventComment", required = false, defaultValue = "Shelf deleted") String eventComment
    ) {
        shelfService.deleteShelf(id, eventUser, eventComment);
        return ResponseEntity.ok("SUCCESS");
    }

    @Operation(summary = "선반(SHELF) 복수 벌크 삭제", description = "선택된 TSID(ID) 리스트에 대한 In-Batch 벌크 삭제 및 SHELF_HISTORY 삭제 이력 적재")
    @DeleteMapping("/batch-delete")
    public ResponseEntity<String> deleteShelfs(@RequestBody DeleteItemListDto deleteDto) {
        if (deleteDto != null && deleteDto.getIds() != null) {
            shelfService.deleteShelfs(deleteDto.getIds(), "SYSTEM", "Batch Shelfs deleted");
        }
        return ResponseEntity.ok("SUCCESS");
    }
}
