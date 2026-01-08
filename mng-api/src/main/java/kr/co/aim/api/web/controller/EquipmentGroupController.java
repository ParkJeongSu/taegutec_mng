package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.service.EquipmentService;
import kr.co.aim.common.dto.*;
import kr.co.aim.common.format.response.UserResponse;
import kr.co.aim.domain.model.EquipmentDef;
import kr.co.aim.domain.model.EquipmentGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "EquipmentGroup", description = "설비 정의 관련 API")
@RestController
@RequestMapping("/api/equipment-group")
@RequiredArgsConstructor
public class EquipmentGroupController {

    private final EquipmentService equipmentService;

    @Operation(summary = "설비 그룹 생성", description = "설비 그룹을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: POST /api/equipment-group
    @PostMapping
    public ResponseEntity<EquipmentGroupResponseDto> createEquipmentGroup(@RequestBody EquipmentGroupCreateRequestDto requestDto) {
        // 3. 서비스 계층에 작업 위임
        EquipmentGroup equipmentGroup = equipmentService.createEquipmentGroup(requestDto);

        // 4. 결과 변환 및 HTTP 응답
        EquipmentGroupResponseDto responseDto =
                EquipmentGroupResponseDto.builder()
                        .id(equipmentGroup.getId())
                        .equipmentGroupName(equipmentGroup.getEquipmentGroupName())
                        .description(equipmentGroup.getDescription())
                        .eventName(equipmentGroup.getEventName())
                        .eventTime(equipmentGroup.getEventTime())
                        .eventUser(equipmentGroup.getEventUser())
                        .eventComment(equipmentGroup.getEventComment())
                        .build();
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "설비 그룹 정보 변경", description = "ID를 이용하여 특정 설비 그룹의 정보를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: PATCH /api/equipment-group/{equipment-group-id}
    @PatchMapping("/{equipment-group-id}")
    public ResponseEntity<EquipmentGroupResponseDto> changeEquipmentGroup(
            @Parameter(description = "변경할 권한의 ID", required = true, example = "1")
            @PathVariable("equipment-group-id") Long equipmentGroupId,
            @RequestBody EquipmentGroupUpdateRequestDto requestDto) {
        // 3. 서비스 계층에 작업 위임
        EquipmentGroup equipmentGroup = equipmentService.changeEquipmentGroup(equipmentGroupId,requestDto);
        // 4. 결과 변환 및 HTTP 응답
        EquipmentGroupResponseDto responseDto = EquipmentGroupResponseDto.builder()
                .id(equipmentGroup.getId())
                .equipmentGroupName(equipmentGroup.getEquipmentGroupName())
                .description(equipmentGroup.getDescription())
                .eventName(equipmentGroup.getEventName())
                .eventTime(equipmentGroup.getEventTime())
                .eventUser(equipmentGroup.getEventUser())
                .eventComment(equipmentGroup.getEventComment())
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "설비 그룹 정보 조회", description = "설비 그룹의 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: GET /api/equipment-group
    @GetMapping
    public ResponseEntity<Page<EquipmentGroupResponseDto>> getEquipmentGroup(
            EquipmentGroupSearchCondtionDto condition,
            Pageable pageable) {
        // 3. 서비스 계층에 작업 위임
        Page<EquipmentGroupResponseDto> page = equipmentService.findEquipmentGroups(condition, pageable);

        // 4. 결과 변환 및 HTTP 응답
        return ResponseEntity.ok(page);
    }


    @DeleteMapping
    public ResponseEntity<Void> deleteEquipmentGroup(@RequestBody DeleteItemListDto request) {
        equipmentService.deleteAllEquipmentGroupByIdInBatch(request.getIds());
        // 성공적으로 삭제되었으며, 별도의 본문 내용 없이 응답한다는 의미
        return ResponseEntity.noContent().build();
    }

}
