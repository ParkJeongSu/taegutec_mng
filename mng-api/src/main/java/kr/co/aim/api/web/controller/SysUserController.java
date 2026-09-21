package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.dto.DeleteItemListDto;
import kr.co.aim.api.dto.SysUserCreateRequestDto;
import kr.co.aim.api.dto.SysUserResponseDto;
import kr.co.aim.api.dto.SysUserUpdateRequestDto;
import kr.co.aim.api.service.SysUserService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.common.condition.SysUserSearchCondition;
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

@Tag(name = "MNG System User", description = "기준정보 사용자(SYS_USER) 관리 API")
@RestController
@RequestMapping("/api/v1/mng/user")
@RequiredArgsConstructor
@Slf4j
@Profile("web")
@ResponseAnnotation
public class SysUserController {

    private final SysUserService sysUserService;

    @Operation(summary = "사용자 목록 조회", description = "동적 조건 및 페이징 처리를 통한 사용자 기준정보 목록 조회")
    @GetMapping("")
    public ResponseEntity<Page<SysUserResponseDto>> getUsers(
            SysUserSearchCondition condition,
            @ParameterObject Pageable pageable
    ) {
        Page<SysUserResponseDto> result = sysUserService.findUsers(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "사용자 단건 상세 조회", description = "ID를 이용한 단건 사용자 세부 정보 조회 (Page 기반 표준 규격)")
    @GetMapping("/{id}")
    public ResponseEntity<Page<SysUserResponseDto>> getUser(@PathVariable("id") Long id) {
        SysUserResponseDto result = sysUserService.findById(id);
        List<SysUserResponseDto> list = new ArrayList<>();
        list.add(result);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "신규 사용자 등록", description = "신규 사용자 기준정보 마스터 등록 및 이력 적재")
    @PostMapping("")
    public ResponseEntity<Page<SysUserResponseDto>> createUser(@RequestBody SysUserCreateRequestDto dto) {
        SysUserResponseDto created = sysUserService.createUser(dto);
        List<SysUserResponseDto> list = new ArrayList<>();
        list.add(created);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "사용자 정보 수정", description = "기존 사용자 기준정보 내역 변경 및 이력 적재")
    @PutMapping("/{id}")
    public ResponseEntity<Page<SysUserResponseDto>> updateUser(
            @PathVariable("id") Long id,
            @RequestBody SysUserUpdateRequestDto dto
    ) {
        SysUserResponseDto updated = sysUserService.updateUser(id, dto);
        List<SysUserResponseDto> list = new ArrayList<>();
        list.add(updated);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "사용자 단건 삭제", description = "ID 기반 사용자 삭제 및 삭제 이력 적재")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable("id") Long id,
            @RequestParam(value = "eventUser", required = false, defaultValue = "SYSTEM") String eventUser,
            @RequestParam(value = "eventComment", required = false, defaultValue = "User deleted") String eventComment
    ) {
        sysUserService.deleteUser(id, eventUser, eventComment);
        return ResponseEntity.ok("SUCCESS");
    }

    @Operation(summary = "사용자 복수 삭제", description = "선택된 ID 리스트에 대한 In-Batch 벌크 삭제 및 이력 적재")
    @DeleteMapping("/batch-delete")
    public ResponseEntity<String> deleteUsers(@RequestBody DeleteItemListDto deleteDto) {
        if (deleteDto != null && deleteDto.getIds() != null) {
            sysUserService.deleteUsers(deleteDto.getIds(), "SYSTEM", "Batch users deleted");
        }
        return ResponseEntity.ok("SUCCESS");
    }
}
