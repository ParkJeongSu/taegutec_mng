package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.dto.DeleteItemListDto;
import kr.co.aim.api.dto.UserGroupCreateRequestDto;
import kr.co.aim.api.dto.UserGroupResponse;
import kr.co.aim.api.dto.UserGroupUpdateRequestDto;
import kr.co.aim.api.service.UserGroupService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.common.condition.UserGroupSearchCondition;
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

@Tag(name = "MNG User Group", description = "기준정보 사용자 그룹(USER_GROUP) 관리 API")
@RestController
@RequestMapping("/api/v1/mng/user-group")
@RequiredArgsConstructor
@Slf4j
@Profile("web")
@ResponseAnnotation
public class UserGroupController {

    private final UserGroupService userGroupService;

    @Operation(summary = "사용자 그룹 목록 조회", description = "동적 조건 및 페이징 사용자 그룹 목록 Page 반환")
    @GetMapping("")
    public ResponseEntity<Page<UserGroupResponse>> getUserGroups(
            UserGroupSearchCondition condition,
            @ParameterObject Pageable pageable
    ) {
        Page<UserGroupResponse> result = userGroupService.findUserGroups(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "특정 사용자 그룹 상세 조회", description = "TSID(Long id) 기준 사용자 그룹 상세 정보 Page 반환")
    @GetMapping("/{id}")
    public ResponseEntity<Page<UserGroupResponse>> getUserGroup(@PathVariable("id") Long id) {
        UserGroupResponse result = userGroupService.findById(id);
        List<UserGroupResponse> list = new ArrayList<>();
        list.add(result);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "신규 사용자 그룹 등록", description = "신규 사용자 그룹 등록 및 USER_GROUP_HISTORY 이력 적재")
    @PostMapping("")
    public ResponseEntity<Page<UserGroupResponse>> createUserGroup(@RequestBody UserGroupCreateRequestDto dto) {
        UserGroupResponse created = userGroupService.createUserGroup(dto);
        List<UserGroupResponse> list = new ArrayList<>();
        list.add(created);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "사용자 그룹 정보 수정", description = "TSID(Long id) 기준 사용자 그룹 정보 수정 및 동일 TSID 기준 이력 적재")
    @PutMapping("/{id}")
    public ResponseEntity<Page<UserGroupResponse>> updateUserGroup(
            @PathVariable("id") Long id,
            @RequestBody UserGroupUpdateRequestDto dto
    ) {
        UserGroupResponse updated = userGroupService.updateUserGroup(id, dto);
        List<UserGroupResponse> list = new ArrayList<>();
        list.add(updated);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "사용자 그룹 단건 삭제", description = "TSID(Long id) 기준 단건 삭제 및 삭제 이력 적재")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserGroup(
            @PathVariable("id") Long id,
            @RequestParam(value = "eventUser", required = false, defaultValue = "SYSTEM") String eventUser,
            @RequestParam(value = "eventComment", required = false, defaultValue = "User group deleted") String eventComment
    ) {
        userGroupService.deleteUserGroup(id, eventUser, eventComment);
        return ResponseEntity.ok("SUCCESS");
    }

    @Operation(summary = "사용자 그룹 복수 벌크 삭제", description = "선택된 TSID(ID) 리스트에 대한 In-Batch 벌크 삭제 및 이력 적재")
    @DeleteMapping("/batch-delete")
    public ResponseEntity<String> deleteUserGroups(@RequestBody DeleteItemListDto deleteDto) {
        if (deleteDto != null && deleteDto.getIds() != null) {
            userGroupService.deleteUserGroups(deleteDto.getIds(), "SYSTEM", "Batch user groups deleted");
        }
        return ResponseEntity.ok("SUCCESS");
    }
}
