package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.dto.*;
import kr.co.aim.api.service.AlarmService;
import kr.co.aim.common.format.response.UserResponse;
import kr.co.aim.domain.model.AlarmAction;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AlarmAction", description = "알람 액션 관련 API")
@RestController
@RequestMapping("/api/alarm-action")
@RequiredArgsConstructor
public class AlarmActionController {

    private final AlarmService alarmService;

    @Operation(summary = "알람 액션 생성", description = "알람 액션을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: POST /api/alarm-action
    @PostMapping
    public ResponseEntity<AlarmActionResponseDto> createAlarmAction(@RequestBody AlarmActionCreateRequestDto requestDto) {
        // 3. 서비스 계층에 작업 위임
        AlarmAction alarmAction = alarmService.createAlarmAction(requestDto);

        // 4. 결과 변환 및 HTTP 응답
        AlarmActionResponseDto responseDto =
                AlarmActionResponseDto.builder()
                        .id(alarmAction.getId())
                        .alarmActionName(alarmAction.getAlarmActionName())
                        .actionType(alarmAction.getActionType())
                        .eventName(alarmAction.getEventName())
                        .eventTime(alarmAction.getEventTime())
                        .eventUser(alarmAction.getEventUser())
                        .eventComment(alarmAction.getEventComment())
                        .build();
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "특정 Alarm 정보 변경", description = "ID를 이용하여 특정 Alarm의 정보를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: PATCH /api/alarm-action/{alarm-action-id}
    @PatchMapping("/{alarm-action-id}")
    public ResponseEntity<AlarmActionResponseDto> changeAlarmAction(
            @Parameter(description = "변경할 권한의 ID", required = true, example = "1")
            @PathVariable("alarm-action-id") Long alarmActionId,
            @RequestBody AlarmActionUpdateRequestDto requestDto) {
        // 3. 서비스 계층에 작업 위임
        AlarmAction alarmAction = alarmService.changeAlarmActio(alarmActionId,requestDto);
        // 4. 결과 변환 및 HTTP 응답
        AlarmActionResponseDto responseDto = AlarmActionResponseDto.builder()
                .id(alarmAction.getId())
                .alarmActionName(alarmAction.getAlarmActionName())
                .actionType(alarmAction.getActionType())
                .eventName(alarmAction.getEventName())
                .eventTime(alarmAction.getEventTime())
                .eventUser(alarmAction.getEventUser())
                .eventComment(alarmAction.getEventComment())
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "알람 정보 조회", description = "알람의 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: GET /api/alarm-action/
    @GetMapping
    public ResponseEntity<Page<AlarmActionResponseDto>> getAlarmAction(
            AlarmActionSearchConditionDto condition,
            Pageable pageable) {
        // 3. 서비스 계층에 작업 위임
        Page<AlarmActionResponseDto> userPage = alarmService.findAlarmActions(condition, pageable);

        // 4. 결과 변환 및 HTTP 응답
        return ResponseEntity.ok(userPage);
    }


    @DeleteMapping
    public ResponseEntity<Void> deleteAlarmAction(@RequestBody DeleteItemListDto request) {
        alarmService.deleteAllAlarmActioByIdInBatch(request.getIds());
        // 성공적으로 삭제되었으며, 별도의 본문 내용 없이 응답한다는 의미
        return ResponseEntity.noContent().build();
    }

}
