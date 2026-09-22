package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.dto.DeleteItemListDto;
import kr.co.aim.api.dto.UserGroupMenuAuthBatchSaveRequestDto;
import kr.co.aim.api.dto.UserGroupMenuAuthCreateRequestDto;
import kr.co.aim.api.dto.UserGroupMenuAuthResponse;
import kr.co.aim.api.dto.UserGroupMenuAuthUpdateRequestDto;
import kr.co.aim.api.service.UserGroupMenuAuthService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.common.condition.UserGroupMenuAuthSearchCondition;
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

@Tag(name = "MNG User Group Menu Auth", description = "사용자 그룹별 메뉴 권한(USER_GROUP_MENU_AUTH) 관리 API")
@RestController
@RequestMapping("/api/v1/mng/user-group-menu-auth")
@RequiredArgsConstructor
@Slf4j
@Profile("web")
@ResponseAnnotation
public class UserGroupMenuAuthController {

    private final UserGroupMenuAuthService userGroupMenuAuthService;

    @Operation(summary = "사용자 그룹별 메뉴 권한 목록 조회", description = "동적 조건 및 페이징 기반 메뉴 권한 목록 Page 반환")
    @GetMapping("")
    public ResponseEntity<Page<UserGroupMenuAuthResponse>> getUserGroupMenuAuths(
            UserGroupMenuAuthSearchCondition condition,
            @ParameterObject Pageable pageable
    ) {
        Page<UserGroupMenuAuthResponse> result = userGroupMenuAuthService.findUserGroupMenuAuths(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "특정 메뉴 권한 상세 조회", description = "TSID(Long id) 기준 단건 메뉴 권한 상세 정보 Page 반환")
    @GetMapping("/{id}")
    public ResponseEntity<Page<UserGroupMenuAuthResponse>> getUserGroupMenuAuth(@PathVariable("id") Long id) {
        UserGroupMenuAuthResponse result = userGroupMenuAuthService.findById(id);
        List<UserGroupMenuAuthResponse> list = new ArrayList<>();
        list.add(result);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "특정 사용자 그룹의 전체 메뉴 권한 목록 조회", description = "사용자 그룹 ID(userGroupId) 기준 전체 메뉴 권한 목록 반환")
    @GetMapping("/by-group/{userGroupId}")
    public ResponseEntity<List<UserGroupMenuAuthResponse>> getAuthsByUserGroupId(@PathVariable("userGroupId") Long userGroupId) {
        List<UserGroupMenuAuthResponse> result = userGroupMenuAuthService.findByUserGroupId(userGroupId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "신규 사용자 그룹별 메뉴 권한 등록", description = "신규 메뉴 권한 등록 및 USER_GROUP_MENU_AUTH_HISTORY 이력 적재")
    @PostMapping("")
    public ResponseEntity<Page<UserGroupMenuAuthResponse>> createUserGroupMenuAuth(@RequestBody UserGroupMenuAuthCreateRequestDto dto) {
        UserGroupMenuAuthResponse created = userGroupMenuAuthService.createUserGroupMenuAuth(dto);
        List<UserGroupMenuAuthResponse> list = new ArrayList<>();
        list.add(created);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "사용자 그룹별 메뉴 권한 수정", description = "TSID(Long id) 기준 권한 정보(조회/저장/삭제) 수정 및 동일 TSID 기준 이력 적재")
    @PutMapping("/{id}")
    public ResponseEntity<Page<UserGroupMenuAuthResponse>> updateUserGroupMenuAuth(
            @PathVariable("id") Long id,
            @RequestBody UserGroupMenuAuthUpdateRequestDto dto
    ) {
        UserGroupMenuAuthResponse updated = userGroupMenuAuthService.updateUserGroupMenuAuth(id, dto);
        List<UserGroupMenuAuthResponse> list = new ArrayList<>();
        list.add(updated);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "사용자 그룹별 메뉴 권한 일괄 저장", description = "특정 사용자 그룹에 대한 여러 메뉴 권한을 일괄 등록/수정하고 이력을 적재")
    @PostMapping("/batch-save")
    public ResponseEntity<List<UserGroupMenuAuthResponse>> saveBatch(@RequestBody UserGroupMenuAuthBatchSaveRequestDto dto) {
        List<UserGroupMenuAuthResponse> result = userGroupMenuAuthService.saveBatch(dto);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "메뉴 권한 단건 삭제", description = "TSID(Long id) 기준 단건 삭제 및 삭제 이력 적재")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserGroupMenuAuth(
            @PathVariable("id") Long id,
            @RequestParam(value = "eventUser", required = false, defaultValue = "SYSTEM") String eventUser,
            @RequestParam(value = "eventComment", required = false, defaultValue = "User group menu auth deleted") String eventComment
    ) {
        userGroupMenuAuthService.deleteUserGroupMenuAuth(id, eventUser, eventComment);
        return ResponseEntity.ok("SUCCESS");
    }

    @Operation(summary = "메뉴 권한 복수 벌크 삭제", description = "선택된 TSID(ID) 리스트에 대한 In-Batch 벌크 삭제 및 이력 적재")
    @DeleteMapping("/batch-delete")
    public ResponseEntity<String> deleteUserGroupMenuAuths(@RequestBody DeleteItemListDto deleteDto) {
        if (deleteDto != null && deleteDto.getIds() != null) {
            userGroupMenuAuthService.deleteUserGroupMenuAuths(deleteDto.getIds(), "SYSTEM", "Batch user group menu auth deleted");
        }
        return ResponseEntity.ok("SUCCESS");
    }
}
