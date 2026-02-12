package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.service.WorkOrderService;
import kr.co.aim.common.dto.*;
import kr.co.aim.common.format.response.UserResponse;
import kr.co.aim.domain.model.WorkOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "WorkOrder", description = "WorkOrder 관련 API")
@RestController
@RequestMapping("/api/work-order")
@RequiredArgsConstructor
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    @Operation(summary = "work-order 생성", description = "work-order를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: POST /api/work-order
    @PostMapping
    public ResponseEntity<WorkOrderResponseDto> createWorkOrder(@RequestBody WorkOrderCreateRequestDto requestDto) {
        // 3. 서비스 계층에 작업 위임
        WorkOrder workOrder = workOrderService.createWorkOrder(requestDto);

        // 4. 결과 변환 및 HTTP 응답
        WorkOrderResponseDto responseDto = WorkOrderResponseDto.builder()
                .id(workOrder.getId())
                .workOrderName(workOrder.getWorkOrderName())
                .eventName(workOrder.getEventName())
                .eventTime(workOrder.getEventTime())
                .eventUser(workOrder.getEventUser())
                .eventComment(workOrder.getEventComment())
                .build();
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "포트 정보 변경", description = "ID를 이용하여 특정 포트의 정보를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: PATCH /api/work-order/{work-order-id}
    @PatchMapping("/{work-order-id}")
    public ResponseEntity<WorkOrderResponseDto> changePorts(
            @Parameter(description = "변경할 권한의 ID", required = true, example = "1")
            @PathVariable("work-order-id") Long workOrderId,
            @RequestBody WorkOrderUpdateRequestDto requestDto) {
        // 3. 서비스 계층에 작업 위임
        WorkOrder workOrder = workOrderService.changeWorkOrder(workOrderId,requestDto);
        // 4. 결과 변환 및 HTTP 응답
        WorkOrderResponseDto responseDto = WorkOrderResponseDto.builder()
                .id(workOrder.getId())
                .workOrderName(workOrder.getWorkOrderName())
                .eventName(workOrder.getEventName())
                .eventTime(workOrder.getEventTime())
                .eventUser(workOrder.getEventUser())
                .eventComment(workOrder.getEventComment())
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "work-order 정보 조회", description = "work-order 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: GET /api/work-order/
    @GetMapping
    public ResponseEntity<Page<WorkOrderResponseDto>> getPorts(
            WorkOrderSearchConditionDto condition,
            Pageable pageable) {
        // 3. 서비스 계층에 작업 위임
        Page<WorkOrderResponseDto> page = workOrderService.findWorkOrder(condition, pageable);

        // 4. 결과 변환 및 HTTP 응답
        return ResponseEntity.ok(page);
    }


    @DeleteMapping
    public ResponseEntity<Void> deletePorts(@RequestBody DeleteItemListDto request) {
        workOrderService.deleteAllByIdInBatch(request.getIds());
        // 성공적으로 삭제되었으며, 별도의 본문 내용 없이 응답한다는 의미
        return ResponseEntity.noContent().build();
    }

}
