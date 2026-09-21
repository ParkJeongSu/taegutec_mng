package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.dto.DeleteItemListDto;
import kr.co.aim.api.dto.SysUserCreateRequestDto;
import kr.co.aim.api.dto.SysUserResponse;
import kr.co.aim.api.dto.SysUserUpdateRequestDto;
import kr.co.aim.api.dto.UserGroupMemberResponse;
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

@Tag(name = "MNG System User", description = "기준정보 사용자(SYS_USER) 및 그룹 매핑 관리 API")
@RestController
@RequestMapping({"/api/users", "/api/v1/mng/users", "/api/v1/mng/user"})
@RequiredArgsConstructor
@Slf4j
@Profile("web")
@ResponseAnnotation
public class SysUserController {

    private final SysUserService sysUserService;

    @Operation(summary = "사용자 목록 조회", description = "부서명(departmentName)이 조인된 동적 조건 및 페이징 사용자 목록 Page 반환")
    @GetMapping("")
    public ResponseEntity<Page<SysUserResponse>> getUsers(
            SysUserSearchCondition condition,
            @ParameterObject Pageable pageable
    ) {
        Page<SysUserResponse> result = sysUserService.findUsers(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "특정 사용자 상세 조회", description = "TSID(Long id) 기준 사용자 상세 정보 Page 반환 (부서명 포함)")
    @GetMapping("/{id}")
    public ResponseEntity<Page<SysUserResponse>> getUser(@PathVariable("id") Long id) {
        SysUserResponse result = sysUserService.findById(id);
        List<SysUserResponse> list = new ArrayList<>();
        list.add(result);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "신규 사용자 등록", description = "신규 사용자 등록 및 SYS_USER_HISTORY 이력 적재")
    @PostMapping("")
    public ResponseEntity<Page<SysUserResponse>> createUser(@RequestBody SysUserCreateRequestDto dto) {
        SysUserResponse created = sysUserService.createUser(dto);
        List<SysUserResponse> list = new ArrayList<>();
        list.add(created);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "사용자 정보 수정", description = "TSID(Long id) 기준 사용자 정보/비밀번호 수정 및 동일 TSID 기준 이력 적재")
    @PutMapping("/{id}")
    public ResponseEntity<Page<SysUserResponse>> updateUser(
            @PathVariable("id") Long id,
            @RequestBody SysUserUpdateRequestDto dto
    ) {
        SysUserResponse updated = sysUserService.updateUser(id, dto);
        List<SysUserResponse> list = new ArrayList<>();
        list.add(updated);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "사용자 단건 삭제", description = "TSID(Long id) 기준 단건 삭제 및 삭제 이력 적재")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable("id") Long id,
            @RequestParam(value = "eventUser", required = false, defaultValue = "SYSTEM") String eventUser,
            @RequestParam(value = "eventComment", required = false, defaultValue = "User deleted") String eventComment
    ) {
        sysUserService.deleteUser(id, eventUser, eventComment);
        return ResponseEntity.ok("SUCCESS");
    }

    @Operation(summary = "사용자 복수 벌크 삭제", description = "선택된 TSID(ID) 리스트에 대한 In-Batch 벌크 삭제 및 이력 적재")
    @DeleteMapping("/batch-delete")
    public ResponseEntity<String> deleteUsers(@RequestBody DeleteItemListDto deleteDto) {
        if (deleteDto != null && deleteDto.getIds() != null) {
            sysUserService.deleteUsers(deleteDto.getIds(), "SYSTEM", "Batch users deleted");
        }
        return ResponseEntity.ok("SUCCESS");
    }

    @Operation(summary = "특정 사용자의 소속 그룹 매핑 목록 조회", description = "TSID(Long id) 기준 사용자가 소속된 그룹 정보 3-Way JOIN 목록 조회")
    @GetMapping("/{id}/groups")
    public ResponseEntity<Page<UserGroupMemberResponse>> getUserGroups(
            @PathVariable("id") Long id,
            @RequestParam(value = "factoryName", required = false) String factoryName,
            @ParameterObject Pageable pageable
    ) {
        Page<UserGroupMemberResponse> result = sysUserService.findUserGroupMembers(id, null, factoryName, pageable);
        return ResponseEntity.ok(result);
    }
}

