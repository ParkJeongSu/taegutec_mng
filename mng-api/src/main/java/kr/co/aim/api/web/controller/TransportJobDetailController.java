package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.service.TransportJobService;
import kr.co.aim.common.dto.*;
import kr.co.aim.common.format.response.UserResponse;
import kr.co.aim.domain.model.TransportJobDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "TransportJobDetail", description = "TransportJobDetail 관련 API")
@RestController
@RequestMapping("/api/transport-job-detail")
@RequiredArgsConstructor
public class TransportJobDetailController {

    private final TransportJobService transportJobService;

    @Operation(summary = "TransportJobDetail 생성", description = "TransportJobDetail를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: POST /api/transport-job-detail
    @PostMapping
    public ResponseEntity<TransportJobDetailResponseDto> createTransportJobDetail(@RequestBody TransportJobDetailCreateRequestDto requestDto) {
        // 3. 서비스 계층에 작업 위임
        TransportJobDetail transportJobDetail = transportJobService.createTransportJobDetail(requestDto);

        // 4. 결과 변환 및 HTTP 응답
        TransportJobDetailResponseDto responseDto = TransportJobDetailResponseDto.builder()
                .id(transportJobDetail.getId())
                .transportJobDetailName(transportJobDetail.getTransportJobDetailName())
                .eventName(transportJobDetail.getEventName())
                .eventTime(transportJobDetail.getEventTime())
                .eventUser(transportJobDetail.getEventUser())
                .eventComment(transportJobDetail.getEventComment())
                .build();
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "TransportJobDetail 정보 변경", description = "ID를 이용하여 특정 TransportJobDetail의 정보를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: PATCH /api/transport-job-detail/{transport-job-detail-id}
    @PatchMapping("/{transport-job-detail-id}")
    public ResponseEntity<TransportJobDetailResponseDto> changeTransportJobDetail(
            @Parameter(description = "변경할 권한의 ID", required = true, example = "1")
            @PathVariable("transport-job-detail-id") Long transportJobDetailId,
            @RequestBody TransportJobDetailUpdateRequestDto requestDto) {
        // 3. 서비스 계층에 작업 위임
        TransportJobDetail transportJobDetail = transportJobService.changeTransportJobDetail(transportJobDetailId,requestDto);
        // 4. 결과 변환 및 HTTP 응답
        TransportJobDetailResponseDto responseDto = TransportJobDetailResponseDto.builder()
                .id(transportJobDetail.getId())
                .transportJobDetailName(transportJobDetail.getTransportJobDetailName())
                .eventName(transportJobDetail.getEventName())
                .eventTime(transportJobDetail.getEventTime())
                .eventUser(transportJobDetail.getEventUser())
                .eventComment(transportJobDetail.getEventComment())
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "TransportJobDetail 정보 조회", description = "TransportJobDetail 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: GET /api/transport-job-detail/
    @GetMapping
    public ResponseEntity<Page<TransportJobDetailResponseDto>> getTransportJobDetail(
            TransportJobDetailSearchConditionDto condition,
            Pageable pageable) {
        // 3. 서비스 계층에 작업 위임
        Page<TransportJobDetailResponseDto> page = transportJobService.findTransportJobDetail(condition, pageable);

        // 4. 결과 변환 및 HTTP 응답
        return ResponseEntity.ok(page);
    }


    @DeleteMapping
    public ResponseEntity<Void> deleteTransportJobDetail(@RequestBody DeleteItemListDto request) {
        transportJobService.deleteAllTransportJobDetailByIdInBatch(request.getIds());
        // 성공적으로 삭제되었으며, 별도의 본문 내용 없이 응답한다는 의미
        return ResponseEntity.noContent().build();
    }

}
