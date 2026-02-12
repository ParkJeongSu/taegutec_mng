package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.dto.*;
import kr.co.aim.api.service.EquipmentService;
import kr.co.aim.common.format.response.UserResponse;
import kr.co.aim.domain.model.Equipments;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Equipments", description = "설비 정의 관련 API")
@RestController
@RequestMapping("/api/equipments")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @Operation(summary = "설비 생성", description = "설비를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: POST /api/equipments
    @PostMapping
    public ResponseEntity<EquipmentsResponseDto> createEquipments(@RequestBody EquipmentsCreateRequestDto requestDto) {
        // 3. 서비스 계층에 작업 위임
        Equipments equipments = equipmentService.createEquipment(requestDto);

        // 4. 결과 변환 및 HTTP 응답
        EquipmentsResponseDto responseDto =
                EquipmentsResponseDto.builder()
                        .id(equipments.getId())
                        .equipmentName(equipments.getEquipmentName())
                        .equipmentLevel(equipments.getEquipmentLevel())
                        .equipmentDefId(equipments.getEquipmentDefId())
                        .equipmentState(equipments.getEquipmentState())
                        .parentEquipmentId(equipments.getParentEquipmentId())
                        .communicationState(equipments.getCommunicationState())
                        .messageServiceAddress(equipments.getMessageServiceAddress())
                        .processCount(equipments.getProcessCount())
                        .recipeName(equipments.getRecipeName())
                        .eventName(equipments.getEventName())
                        .eventTime(equipments.getEventTime())
                        .eventUser(equipments.getEventUser())
                        .eventComment(equipments.getEventComment())
                        .build();
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "설비 정의 정보 변경", description = "ID를 이용하여 특정 설비 정의의 정보를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: PATCH /api/equipments/{equipments-id}
    @PatchMapping("/{equipments-id}")
    public ResponseEntity<EquipmentsResponseDto> changeEquipments(
            @Parameter(description = "변경할 권한의 ID", required = true, example = "1")
            @PathVariable("equipments-id") Long equipmentsId,
            @RequestBody EquipmentsUpdateRequestDto requestDto) {
        // 3. 서비스 계층에 작업 위임
        Equipments equipments = equipmentService.changeEquipment(equipmentsId,requestDto);
        // 4. 결과 변환 및 HTTP 응답
        EquipmentsResponseDto responseDto = EquipmentsResponseDto.builder()
                .id(equipments.getId())
                .equipmentName(equipments.getEquipmentName())
                .equipmentLevel(equipments.getEquipmentLevel())
                .equipmentDefId(equipments.getEquipmentDefId())
                .equipmentState(equipments.getEquipmentState())
                .parentEquipmentId(equipments.getParentEquipmentId())
                .communicationState(equipments.getCommunicationState())
                .messageServiceAddress(equipments.getMessageServiceAddress())
                .processCount(equipments.getProcessCount())
                .recipeName(equipments.getRecipeName())
                .eventName(equipments.getEventName())
                .eventTime(equipments.getEventTime())
                .eventUser(equipments.getEventUser())
                .eventComment(equipments.getEventComment())
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "설비 정보 조회", description = "설비의 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: GET /api/equipments/
    @GetMapping
    public ResponseEntity<Page<EquipmentsResponseDto>> getEquipments(
            EquipmentsSearchConditionDto condition,
            Pageable pageable) {
        // 3. 서비스 계층에 작업 위임
        Page<EquipmentsResponseDto> page = equipmentService.findEquipments(condition, pageable);

        // 4. 결과 변환 및 HTTP 응답
        return ResponseEntity.ok(page);
    }


    @DeleteMapping
    public ResponseEntity<Void> deleteEquipments(@RequestBody DeleteItemListDto request) {
        equipmentService.deleteAllEquipmentsByIdInBatch(request.getIds());
        // 성공적으로 삭제되었으며, 별도의 본문 내용 없이 응답한다는 의미
        return ResponseEntity.noContent().build();
    }

}
