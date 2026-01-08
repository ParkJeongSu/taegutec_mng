package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.service.TransportJobService;
import kr.co.aim.api.service.WorkOrderService;
import kr.co.aim.common.dto.*;
import kr.co.aim.common.format.response.UserResponse;
import kr.co.aim.domain.model.TransportJob;
import kr.co.aim.domain.model.WorkOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "TransportJob", description = "TransportJob 관련 API")
@RestController
@RequestMapping("/api/transport-job")
@RequiredArgsConstructor
public class TransportJobController {

    private final TransportJobService transportJobService;

    @Operation(summary = "TransportJob 생성", description = "TransportJob를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: POST /api/transport-job
    @PostMapping
    public ResponseEntity<TransportJobResponseDto> createTransportJob(@RequestBody TransportJobCreateRequestDto requestDto) {
        // 3. 서비스 계층에 작업 위임
        TransportJob transportJob = transportJobService.createTransportJob(requestDto);

        // 4. 결과 변환 및 HTTP 응답
        TransportJobResponseDto responseDto = TransportJobResponseDto.builder()
                .id(transportJob.getId())
                .transportJobName(transportJob.getTransportJobName())
                .eventName(transportJob.getEventName())
                .eventTime(transportJob.getEventTime())
                .eventUser(transportJob.getEventUser())
                .eventComment(transportJob.getEventComment())
                .build();
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "TransportJob 정보 변경", description = "ID를 이용하여 특정 TransportJob의 정보를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: PATCH /api/transport-job/{transport-job-id}
    @PatchMapping("/{transport-job-id}")
    public ResponseEntity<TransportJobResponseDto> changeTransportJob(
            @Parameter(description = "변경할 권한의 ID", required = true, example = "1")
            @PathVariable("transport-job-id") Long transportJobId,
            @RequestBody TransportJobUpdateRequestDto requestDto) {
        // 3. 서비스 계층에 작업 위임
        TransportJob transportJob = transportJobService.changeTransportJob(transportJobId,requestDto);
        // 4. 결과 변환 및 HTTP 응답
        TransportJobResponseDto responseDto = TransportJobResponseDto.builder()
                .id(transportJob.getId())
                .transportJobName(transportJob.getTransportJobName())
                .eventName(transportJob.getEventName())
                .eventTime(transportJob.getEventTime())
                .eventUser(transportJob.getEventUser())
                .eventComment(transportJob.getEventComment())
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "TransportJob 정보 조회", description = "TransportJob 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: GET /api/transport-job/
    @GetMapping
    public ResponseEntity<Page<TransportJobResponseDto>> getTransportJob(
            TransportJobSearchConditionDto condition,
            Pageable pageable) {
        // 3. 서비스 계층에 작업 위임
        Page<TransportJobResponseDto> page = transportJobService.findTransportJob(condition, pageable);

        // 4. 결과 변환 및 HTTP 응답
        return ResponseEntity.ok(page);
    }


    @DeleteMapping
    public ResponseEntity<Void> deleteTransportJob(@RequestBody DeleteItemListDto request) {
        transportJobService.deleteAllTransportJobByIdInBatch(request.getIds());
        // 성공적으로 삭제되었으며, 별도의 본문 내용 없이 응답한다는 의미
        return ResponseEntity.noContent().build();
    }

}
