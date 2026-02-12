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
import kr.co.aim.domain.model.AlarmDef;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AlarmDef", description = "알람 정의 관련 API")
@RestController
@RequestMapping("/api/alarm-def")
@RequiredArgsConstructor
public class AlarmDefController {

    private final AlarmService alarmService;

    @Operation(summary = "알람 정의 생성", description = "알람정의를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: POST /api/alarm-def
    @PostMapping
    public ResponseEntity<AlarmDefResponseDto> createAlarmDef(@RequestBody AlarmDefCreateRequestDto requestDto) {
        // 3. 서비스 계층에 작업 위임
        AlarmDef alarmDef = alarmService.createAlarmDef(requestDto);

        // 4. 결과 변환 및 HTTP 응답
        AlarmDefResponseDto responseDto =
                AlarmDefResponseDto.builder()
                        .id(alarmDef.getId())
                        .alarmDefName(alarmDef.getAlarmDefName())
                        .alarmLevel(alarmDef.getAlarmLevel())
                        .alarmType(alarmDef.getAlarmType())
                        .eventName(alarmDef.getEventName())
                        .eventTime(alarmDef.getEventTime())
                        .eventUser(alarmDef.getEventUser())
                        .eventComment(alarmDef.getEventComment())
                        .build();
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "특정 Alarm_Def 정보 변경", description = "ID를 이용하여 특정 Alarm_Def의 정보를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: PATCH /api/alarm-def/{alarm-def-id}
    @PatchMapping("/{alarm-def-id}")
    public ResponseEntity<AlarmDefResponseDto> changeAlarmDef(
            @Parameter(description = "변경할 권한의 ID", required = true, example = "1")
            @PathVariable("alarm-def-id") Long alarmDefId,
            @RequestBody AlarmDefUpdateRequestDto requestDto) {
        // 3. 서비스 계층에 작업 위임
        AlarmDef alarmDef = alarmService.changeAlarmDef(alarmDefId,requestDto);
        // 4. 결과 변환 및 HTTP 응답
        AlarmDefResponseDto responseDto = AlarmDefResponseDto.builder()
                .id(alarmDef.getId())
                .alarmDefName(alarmDef.getAlarmDefName())
                .alarmLevel(alarmDef.getAlarmLevel())
                .alarmType(alarmDef.getAlarmType())
                .eventName(alarmDef.getEventName())
                .eventTime(alarmDef.getEventTime())
                .eventUser(alarmDef.getEventUser())
                .eventComment(alarmDef.getEventComment())
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "사용자 정보 조회", description = "사용자의 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: GET /api/alarm-def/
    @GetMapping
    public ResponseEntity<Page<AlarmDefResponseDto>> getAlarmDef(
            AlarmDefSearchConditionDto condition,
            Pageable pageable) {
        // 3. 서비스 계층에 작업 위임
        Page<AlarmDefResponseDto> userPage = alarmService.findAlarmDefs(condition, pageable);

        // 4. 결과 변환 및 HTTP 응답
        return ResponseEntity.ok(userPage);
    }

    @Operation(summary = "사용자 정보 조회", description = "사용자의 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: GET /api/alarm-def/
    @GetMapping("/options")
    public ResponseEntity<Page<AlarmDefResponseDto>> getAlarmDef(
            AlarmDefSearchConditionDto condition) {
        // 3. 서비스 계층에 작업 위임
        Page<AlarmDefResponseDto> userPage = alarmService.findAlarmDefs(condition, Pageable.unpaged());

        // 4. 결과 변환 및 HTTP 응답
        return ResponseEntity.ok(userPage);
    }


    @DeleteMapping
    public ResponseEntity<Void> deleteAlarmDef(@RequestBody DeleteItemListDto request) {
        alarmService.deleteAllAlarmDefByIdInBatch(request.getIds());
        // 성공적으로 삭제되었으며, 별도의 본문 내용 없이 응답한다는 의미
        return ResponseEntity.noContent().build();
    }

}
