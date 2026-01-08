package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.service.AlarmService;
import kr.co.aim.api.service.EquipmentService;
import kr.co.aim.common.dto.*;
import kr.co.aim.common.format.response.UserResponse;
import kr.co.aim.domain.model.AlarmAction;
import kr.co.aim.domain.model.EquipmentDef;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "EquipmentDef", description = "설비 정의 관련 API")
@RestController
@RequestMapping("/api/equipment-def")
@RequiredArgsConstructor
public class EquipmentDefController {

    private final EquipmentService equipmentService;

    @Operation(summary = "설비 정의 생성", description = "설비 정의를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: POST /api/equipment-def
    @PostMapping
    public ResponseEntity<EquipmentDefResponseDto> createEquipmentDef(@RequestBody EquipmentDefCreateRequestDto requestDto) {
        // 3. 서비스 계층에 작업 위임
        EquipmentDef equipmentDef = equipmentService.createEquipmentDef(requestDto);

        // 4. 결과 변환 및 HTTP 응답
        EquipmentDefResponseDto responseDto =
                EquipmentDefResponseDto.builder()
                        .id(equipmentDef.getId())
                        .equipmentDefName(equipmentDef.getEquipmentDefName())
                        .description(equipmentDef.getDescription())
                        .equipmentGroupId(equipmentDef.getEquipmentGroupId())
                        .equipmentType(equipmentDef.getEquipmentType())
                        .detailEquipmentType(equipmentDef.getDetailEquipmentType())
                        .eventName(equipmentDef.getEventName())
                        .eventTime(equipmentDef.getEventTime())
                        .eventUser(equipmentDef.getEventUser())
                        .eventComment(equipmentDef.getEventComment())
                        .build();
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "설비 정의 정보 변경", description = "ID를 이용하여 특정 설비 정의의 정보를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: PATCH /api/equipment-def/{equipment-def-id}
    @PatchMapping("/{equipment-def-id}")
    public ResponseEntity<EquipmentDefResponseDto> changeEquipmentDef(
            @Parameter(description = "변경할 권한의 ID", required = true, example = "1")
            @PathVariable("equipment-def-id") Long equipmentDefId,
            @RequestBody EquipmentDefUpdateRequestDto requestDto) {
        // 3. 서비스 계층에 작업 위임
        EquipmentDef equipmentDef = equipmentService.changeEquipmentDef(equipmentDefId,requestDto);
        // 4. 결과 변환 및 HTTP 응답
        EquipmentDefResponseDto responseDto = EquipmentDefResponseDto.builder()
                .id(equipmentDef.getId())
                .equipmentDefName(equipmentDef.getEquipmentDefName())
                .description(equipmentDef.getDescription())
                .equipmentGroupId(equipmentDef.getEquipmentGroupId())
                .equipmentType(equipmentDef.getEquipmentType())
                .detailEquipmentType(equipmentDef.getDetailEquipmentType())
                .eventName(equipmentDef.getEventName())
                .eventTime(equipmentDef.getEventTime())
                .eventUser(equipmentDef.getEventUser())
                .eventComment(equipmentDef.getEventComment())
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "알람 정보 조회", description = "알람의 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: GET /api/equipment-def
    @GetMapping
    public ResponseEntity<Page<EquipmentDefResponseDto>> getEquipmentDef(
            EquipmentDefSearchConditionDto condition,
            Pageable pageable) {
        // 3. 서비스 계층에 작업 위임
        Page<EquipmentDefResponseDto> page = equipmentService.findEquipmentDefs(condition, pageable);

        // 4. 결과 변환 및 HTTP 응답
        return ResponseEntity.ok(page);
    }


    @DeleteMapping
    public ResponseEntity<Void> deleteEquipmentDef(@RequestBody DeleteItemListDto request) {
        equipmentService.deleteAllEquipmentDefByIdInBatch(request.getIds());
        // 성공적으로 삭제되었으며, 별도의 본문 내용 없이 응답한다는 의미
        return ResponseEntity.noContent().build();
    }

}
