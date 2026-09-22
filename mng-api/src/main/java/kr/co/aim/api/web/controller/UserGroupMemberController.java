package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.dto.DeleteItemListDto;
import kr.co.aim.api.dto.UserGroupMemberCreateRequestDto;
import kr.co.aim.api.dto.UserGroupMemberResponse;
import kr.co.aim.api.dto.UserGroupMemberUpdateRequestDto;
import kr.co.aim.api.service.UserGroupMemberService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.common.condition.UserGroupMemberSearchCondition;
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

@Tag(name = "MNG User Group Member", description = "사용자-그룹 매핑(USER_GROUP_MEMBER) 관리 API")
@RestController
@RequestMapping({"/api/v1/mng/user-group-member", "/api/v1/mng/user-group-members", "/api/user-group-members"})
@RequiredArgsConstructor
@Slf4j
@Profile("web")
@ResponseAnnotation
public class UserGroupMemberController {

    private final UserGroupMemberService userGroupMemberService;

    @Operation(summary = "사용자-그룹 매핑 목록 조회", description = "3-Way JOIN 기반 사용자 및 그룹 정보가 포함된 동적 조건 및 페이징 목록 Page 반환")
    @GetMapping("")
    public ResponseEntity<Page<UserGroupMemberResponse>> getUserGroupMembers(
            UserGroupMemberSearchCondition condition,
            @ParameterObject Pageable pageable
    ) {
        Page<UserGroupMemberResponse> result = userGroupMemberService.findUserGroupMembers(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "특정 매핑 상세 조회", description = "TSID(Long id) 기준 사용자-그룹 매핑 상세 정보 Page 반환")
    @GetMapping("/{id}")
    public ResponseEntity<Page<UserGroupMemberResponse>> getUserGroupMember(@PathVariable("id") Long id) {
        UserGroupMemberResponse result = userGroupMemberService.findById(id);
        List<UserGroupMemberResponse> list = new ArrayList<>();
        list.add(result);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "특정 사용자의 소속 그룹 매핑 목록 조회", description = "사용자 ID(userId) 기준 소속 그룹 목록 Page 반환")
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<Page<UserGroupMemberResponse>> getMembersByUserId(
            @PathVariable("userId") Long userId,
            @ParameterObject Pageable pageable
    ) {
        Page<UserGroupMemberResponse> result = userGroupMemberService.findByUserId(userId, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "특정 그룹의 소속 사용자 매핑 목록 조회", description = "그룹 ID(userGroupId) 기준 소속 사용자 목록 Page 반환")
    @GetMapping("/by-group/{userGroupId}")
    public ResponseEntity<Page<UserGroupMemberResponse>> getMembersByUserGroupId(
            @PathVariable("userGroupId") Long userGroupId,
            @ParameterObject Pageable pageable
    ) {
        Page<UserGroupMemberResponse> result = userGroupMemberService.findByUserGroupId(userGroupId, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "신규 사용자-그룹 매핑 등록", description = "신규 매핑 등록 및 USER_GROUP_MEMBER_HISTORY 이력 적재")
    @PostMapping("")
    public ResponseEntity<Page<UserGroupMemberResponse>> createUserGroupMember(@RequestBody UserGroupMemberCreateRequestDto dto) {
        UserGroupMemberResponse created = userGroupMemberService.createUserGroupMember(dto);
        List<UserGroupMemberResponse> list = new ArrayList<>();
        list.add(created);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "사용자-그룹 매핑 수정", description = "TSID(Long id) 기준 매핑 정보 수정 및 동일 TSID 기준 이력 적재")
    @PutMapping("/{id}")
    public ResponseEntity<Page<UserGroupMemberResponse>> updateUserGroupMember(
            @PathVariable("id") Long id,
            @RequestBody UserGroupMemberUpdateRequestDto dto
    ) {
        UserGroupMemberResponse updated = userGroupMemberService.updateUserGroupMember(id, dto);
        List<UserGroupMemberResponse> list = new ArrayList<>();
        list.add(updated);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "사용자-그룹 매핑 단건 삭제", description = "TSID(Long id) 기준 단건 삭제 및 삭제 이력 적재")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserGroupMember(
            @PathVariable("id") Long id,
            @RequestParam(value = "eventUser", required = false, defaultValue = "SYSTEM") String eventUser,
            @RequestParam(value = "eventComment", required = false, defaultValue = "User group member deleted") String eventComment
    ) {
        userGroupMemberService.deleteUserGroupMember(id, eventUser, eventComment);
        return ResponseEntity.ok("SUCCESS");
    }

    @Operation(summary = "사용자-그룹 매핑 복수 벌크 삭제", description = "선택된 TSID(ID) 리스트에 대한 In-Batch 벌크 삭제 및 이력 적재")
    @DeleteMapping("/batch-delete")
    public ResponseEntity<String> deleteUserGroupMembers(@RequestBody DeleteItemListDto deleteDto) {
        if (deleteDto != null && deleteDto.getIds() != null) {
            userGroupMemberService.deleteUserGroupMembers(deleteDto.getIds(), "SYSTEM", "Batch user group members deleted");
        }
        return ResponseEntity.ok("SUCCESS");
    }
}
