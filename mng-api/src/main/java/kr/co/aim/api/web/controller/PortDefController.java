package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.service.PortService;
import kr.co.aim.common.dto.*;
import kr.co.aim.common.format.response.UserResponse;
import kr.co.aim.domain.model.PortDef;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "PortDef", description = "포트 정의 관련 API")
@RestController
@RequestMapping("/api/port-def")
@RequiredArgsConstructor
public class PortDefController {

    private final PortService portService;

    @Operation(summary = "포트 정의 생성", description = "포트 정의를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: POST /api/port-def
    @PostMapping
    public ResponseEntity<PortDefResponseDto> createPortDef(@RequestBody PortDefCreateRequestDto requestDto) {
        // 3. 서비스 계층에 작업 위임
        PortDef portDef = portService.createPortDef(requestDto);

        // 4. 결과 변환 및 HTTP 응답
        PortDefResponseDto responseDto = PortDefResponseDto.builder()
                .id(portDef.getId())
                .equipmentName("")
                .portName("")
                .portType(portDef.getPortType())
                .description(portDef.getDescription())
                .eventName(portDef.getEventName())
                .eventTime(portDef.getEventTime())
                .eventUser(portDef.getEventUser())
                .eventComment(portDef.getEventComment())
                .build();
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "설비 정의 정보 변경", description = "ID를 이용하여 특정 설비 정의의 정보를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: PATCH /api/port-def/{port-def-id}
    @PatchMapping("/{port-def-id}")
    public ResponseEntity<PortDefResponseDto> changePortDef(
            @Parameter(description = "변경할 권한의 ID", required = true, example = "1")
            @PathVariable("port-def-id") Long portDefId,
            @RequestBody PortDefUpdateRequestDto requestDto) {
        // 3. 서비스 계층에 작업 위임
        PortDef portDef = portService.changePortDef(portDefId,requestDto);
        // 4. 결과 변환 및 HTTP 응답
        PortDefResponseDto responseDto = PortDefResponseDto.builder()
                .id(portDef.getId())
                .equipmentName("")
                .portName("")
                .portType(portDef.getPortType())
                .description(portDef.getDescription())
                .eventName(portDef.getEventName())
                .eventTime(portDef.getEventTime())
                .eventUser(portDef.getEventUser())
                .eventComment(portDef.getEventComment())
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "포트 정의 정보 조회", description = "포트 정의 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: GET /api/port-def/
    @GetMapping
    public ResponseEntity<Page<PortDefResponseDto>> getPortDef(
            PortDefSearchConditionDto condition,
            Pageable pageable) {
        // 3. 서비스 계층에 작업 위임
        Page<PortDefResponseDto> page = portService.findPortDefs(condition, pageable);

        // 4. 결과 변환 및 HTTP 응답
        return ResponseEntity.ok(page);
    }


    @DeleteMapping
    public ResponseEntity<Void> deletePortDef(@RequestBody DeleteItemListDto request) {
        portService.deleteAllPortDefByIdInBatch(request.getIds());
        // 성공적으로 삭제되었으며, 별도의 본문 내용 없이 응답한다는 의미
        return ResponseEntity.noContent().build();
    }

}
