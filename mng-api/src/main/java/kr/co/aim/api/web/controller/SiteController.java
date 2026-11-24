package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.dto.DeleteItemListDto;
import kr.co.aim.api.dto.SiteCreateRequestDto;
import kr.co.aim.api.dto.SiteResponse;
import kr.co.aim.api.dto.SiteUpdateRequestDto;
import kr.co.aim.api.service.SiteService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.domain.repository.condition.SiteSearchCondition;
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

@Tag(name = "MNG Site", description = "사이트(SITE) 관리 API")
@RestController
@RequestMapping("/api/v1/mng/site")
@RequiredArgsConstructor
@Slf4j
@Profile({"web", "simulator"})
@ResponseAnnotation
public class SiteController {

    private final SiteService siteService;

    @Operation(summary = "사이트(SITE) 목록 조회", description = "동적 조건 및 페이징 기반 사이트(SITE) 목록 Page 반환")
    @GetMapping("")
    public ResponseEntity<Page<SiteResponse>> getSites(
            SiteSearchCondition condition,
            @ParameterObject Pageable pageable
    ) {
        Page<SiteResponse> result = siteService.findSites(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "특정 사이트(SITE) 상세 조회", description = "TSID(Long id) 기준 사이트(SITE) 상세 정보 Page 반환")
    @GetMapping("/{id}")
    public ResponseEntity<Page<SiteResponse>> getSite(@PathVariable("id") Long id) {
        SiteResponse result = siteService.findById(id);
        List<SiteResponse> list = new ArrayList<>();
        list.add(result);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "신규 사이트(SITE) 등록", description = "신규 사이트(SITE) 등록 및 SITE_HISTORY 이력 적재")
    @PostMapping("")
    public ResponseEntity<Page<SiteResponse>> createSite(@RequestBody SiteCreateRequestDto dto) {
        SiteResponse created = siteService.createSite(dto);
        List<SiteResponse> list = new ArrayList<>();
        list.add(created);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "사이트(SITE) 정보 수정", description = "TSID(Long id) 기준 사이트(SITE) 정보 수정 및 SITE_HISTORY 이력 적재")
    @PutMapping("/{id}")
    public ResponseEntity<Page<SiteResponse>> updateSite(
            @PathVariable("id") Long id,
            @RequestBody SiteUpdateRequestDto dto
    ) {
        SiteResponse updated = siteService.updateSite(id, dto);
        List<SiteResponse> list = new ArrayList<>();
        list.add(updated);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "사이트(SITE) 단건 삭제", description = "TSID(Long id) 기준 단건 삭제 및 SITE_HISTORY 삭제 이력 적재")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSite(
            @PathVariable("id") Long id,
            @RequestParam(value = "eventUser", required = false, defaultValue = "SYSTEM") String eventUser,
            @RequestParam(value = "eventComment", required = false, defaultValue = "Site deleted") String eventComment
    ) {
        siteService.deleteSite(id, eventUser, eventComment);
        return ResponseEntity.ok("SUCCESS");
    }

    @Operation(summary = "사이트(SITE) 복수 벌크 삭제", description = "선택된 TSID(ID) 리스트에 대한 In-Batch 벌크 삭제 및 SITE_HISTORY 삭제 이력 적재")
    @DeleteMapping("/batch-delete")
    public ResponseEntity<String> deleteSites(@RequestBody DeleteItemListDto deleteDto) {
        if (deleteDto != null && deleteDto.getIds() != null) {
            siteService.deleteSites(deleteDto.getIds(), "SYSTEM", "Batch Sites deleted");
        }
        return ResponseEntity.ok("SUCCESS");
    }
}
