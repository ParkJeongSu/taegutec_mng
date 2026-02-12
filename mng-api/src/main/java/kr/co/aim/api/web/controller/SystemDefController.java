package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.dto.*;
import kr.co.aim.api.service.SystemDefService;
import kr.co.aim.common.format.response.UserResponse;
import kr.co.aim.domain.model.SystemDef;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "SystemDef", description = "SystemDef 관련 API")
@RestController
@RequestMapping("/api/system-def")
@RequiredArgsConstructor
public class SystemDefController {

    private final SystemDefService systemDefService;

    @Operation(summary = "SystemDef 생성", description = "SystemDef를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: POST /api/system-def
    @PostMapping
    public ResponseEntity<SystemDefResponseDto> createSystemDef(@RequestBody SystemDefCreateRequestDto requestDto) {
        // 3. 서비스 계층에 작업 위임
        SystemDef systemDef = systemDefService.createSystemDef(requestDto);

        // 4. 결과 변환 및 HTTP 응답
        SystemDefResponseDto responseDto =
                SystemDefResponseDto.builder()
                        .id(systemDef.getId())
                        .systemDefName(systemDef.getSystemDefName())
                        .build();
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "SystemDef 조회", description = "SystemDef를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: GET /api/system-def
    @GetMapping
    public ResponseEntity<Page<SystemDefResponseDto>> getSystemDef(
            SystemDefSearchConditionDto condition,
            Pageable pageable) {
        // 3. 서비스 계층에 작업 위임
        Page<SystemDefResponseDto> page = systemDefService.findSystemDefs(condition,pageable);

        // 4. 결과 변환 및 HTTP 응답
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "SystemDef 변경", description = "ID를 이용하여 SystemDef를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: PATCH /api/system-def/{system-id}
    @PatchMapping("/{system-id}")
    public ResponseEntity<SystemDefResponseDto> changeSystemDef(
            @Parameter(description = "변경할 권한의 ID", required = true, example = "1")
            @PathVariable("system-id") Long systemId,
            @RequestBody SystemDefUpdateRequestDto requestDto) {
        // 3. 서비스 계층에 작업 위임
        SystemDef systemDef = systemDefService.changeSystemDef(systemId,requestDto);
        // 4. 결과 변환 및 HTTP 응답
        SystemDefResponseDto responseDto =
                SystemDefResponseDto.builder()
                        .id(systemDef.getId())
                        .systemDefName(systemDef.getSystemDefName())
                        .build();
        return ResponseEntity.ok(responseDto);
    }


    @DeleteMapping
    public ResponseEntity<Void> deleteSystemDef(@RequestBody DeleteItemListDto request) {
        systemDefService.deleteAllByIdInBatch(request.getIds());
        // 성공적으로 삭제되었으며, 별도의 본문 내용 없이 응답한다는 의미
        return ResponseEntity.noContent().build();
    }
}
