package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.dto.DeleteItemListDto;
import kr.co.aim.api.dto.PasswordPolicyCreateRequestDto;
import kr.co.aim.api.dto.PasswordPolicyResponse;
import kr.co.aim.api.dto.PasswordPolicyUpdateRequestDto;
import kr.co.aim.api.service.PasswordPolicyService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.common.condition.PasswordPolicySearchCondition;
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

@Tag(name = "MNG Password Policy", description = "패스워드 정책(PASSWORD_POLICY) 관리 API")
@RestController
@RequestMapping("/api/v1/mng/password-policy")
@RequiredArgsConstructor
@Slf4j
@Profile("web")
@ResponseAnnotation
public class PasswordPolicyController {

    private final PasswordPolicyService passwordPolicyService;

    @Operation(summary = "패스워드 정책 목록 조회", description = "동적 조건 및 페이징 기반 패스워드 정책 목록 Page 반환")
    @GetMapping("")
    public ResponseEntity<Page<PasswordPolicyResponse>> getPasswordPolicies(
            PasswordPolicySearchCondition condition,
            @ParameterObject Pageable pageable
    ) {
        Page<PasswordPolicyResponse> result = passwordPolicyService.findPasswordPolicies(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "특정 패스워드 정책 상세 조회", description = "TSID(Long id) 기준 패스워드 정책 상세 정보 Page 반환")
    @GetMapping("/{id}")
    public ResponseEntity<Page<PasswordPolicyResponse>> getPasswordPolicy(@PathVariable("id") Long id) {
        PasswordPolicyResponse result = passwordPolicyService.findById(id);
        List<PasswordPolicyResponse> list = new ArrayList<>();
        list.add(result);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "공장별 패스워드 정책 조회", description = "공장 구분(factoryName) 기준 패스워드 정책 상세 정보 Page 반환")
    @GetMapping("/by-factory/{factoryName}")
    public ResponseEntity<Page<PasswordPolicyResponse>> getPasswordPolicyByFactory(@PathVariable("factoryName") String factoryName) {
        PasswordPolicyResponse result = passwordPolicyService.findByFactoryName(factoryName);
        List<PasswordPolicyResponse> list = new ArrayList<>();
        list.add(result);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "신규 패스워드 정책 등록", description = "신규 패스워드 정책 등록 및 PASSWORD_POLICY_HISTORY 이력 적재")
    @PostMapping("")
    public ResponseEntity<Page<PasswordPolicyResponse>> createPasswordPolicy(@RequestBody PasswordPolicyCreateRequestDto dto) {
        PasswordPolicyResponse created = passwordPolicyService.createPasswordPolicy(dto);
        List<PasswordPolicyResponse> list = new ArrayList<>();
        list.add(created);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "패스워드 정책 수정", description = "TSID(Long id) 기준 패스워드 정책 정보 수정 및 동일 TSID 기준 이력 적재")
    @PutMapping("/{id}")
    public ResponseEntity<Page<PasswordPolicyResponse>> updatePasswordPolicy(
            @PathVariable("id") Long id,
            @RequestBody PasswordPolicyUpdateRequestDto dto
    ) {
        PasswordPolicyResponse updated = passwordPolicyService.updatePasswordPolicy(id, dto);
        List<PasswordPolicyResponse> list = new ArrayList<>();
        list.add(updated);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "패스워드 정책 단건 삭제", description = "TSID(Long id) 기준 단건 삭제 및 삭제 이력 적재")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePasswordPolicy(
            @PathVariable("id") Long id,
            @RequestParam(value = "eventUser", required = false, defaultValue = "SYSTEM") String eventUser,
            @RequestParam(value = "eventComment", required = false, defaultValue = "Password policy deleted") String eventComment
    ) {
        passwordPolicyService.deletePasswordPolicy(id, eventUser, eventComment);
        return ResponseEntity.ok("SUCCESS");
    }

    @Operation(summary = "패스워드 정책 복수 벌크 삭제", description = "선택된 TSID(ID) 리스트에 대한 In-Batch 벌크 삭제 및 이력 적재")
    @DeleteMapping("/batch-delete")
    public ResponseEntity<String> deletePasswordPolicies(@RequestBody DeleteItemListDto deleteDto) {
        if (deleteDto != null && deleteDto.getIds() != null) {
            passwordPolicyService.deletePasswordPolicies(deleteDto.getIds(), "SYSTEM", "Batch password policies deleted");
        }
        return ResponseEntity.ok("SUCCESS");
    }
}
