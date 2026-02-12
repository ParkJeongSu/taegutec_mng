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
import kr.co.aim.api.service.HistoryService;
import kr.co.aim.common.format.response.UserResponse;
import kr.co.aim.common.handler.IBaseHistoryEntity;
import kr.co.aim.domain.model.Alarm;
import kr.co.aim.infra.persistence.entity.AlarmHistoryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Alarm", description = "알람 정의 관련 API")
@RestController
@RequestMapping("/api/alarm")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;
    private final HistoryService historyService;

    @Operation(summary = "알람 생성", description = "알람을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: POST /api/alarm
    @PostMapping
    public ResponseEntity<AlarmResponseDto> createAlarm(@RequestBody AlarmCreateRequestDto requestDto) {
        // 3. 서비스 계층에 작업 위임
        Alarm alarm = alarmService.createAlarm(requestDto);

        // 4. 결과 변환 및 HTTP 응답
        AlarmResponseDto responseDto =
                AlarmResponseDto.builder()
                        .id(alarm.getId())
                        .alarmDefId(alarm.getAlarmDefId())
                        .alarmState(alarm.getAlarmState())
                        .eventName(alarm.getEventName())
                        .eventTime(alarm.getEventTime())
                        .eventUser(alarm.getEventUser())
                        .eventComment(alarm.getEventComment())
                        .build();
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "특정 Alarm 정보 변경", description = "ID를 이용하여 특정 Alarm의 정보를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: PATCH /api/alarm/{alarm-id}
    @PatchMapping("/{alarm-id}")
    public ResponseEntity<AlarmResponseDto> changeAlarm(
            @Parameter(description = "변경할 권한의 ID", required = true, example = "1")
            @PathVariable("alarm-id") Long alarmId,
            @RequestBody AlarmUpdateRequestDto requestDto) {
        // 3. 서비스 계층에 작업 위임
        Alarm alarm = alarmService.changeAlarm(alarmId,requestDto);
        // 4. 결과 변환 및 HTTP 응답
        AlarmResponseDto responseDto = AlarmResponseDto.builder()
                .id(alarm.getId())
                .alarmDefId(alarm.getAlarmDefId())
                .alarmState(alarm.getAlarmState())
                .eventName(alarm.getEventName())
                .eventTime(alarm.getEventTime())
                .eventUser(alarm.getEventUser())
                .eventComment(alarm.getEventComment())
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "알람 정보 조회", description = "알람의 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: GET /api/alarm/
    @GetMapping
    public ResponseEntity<Page<AlarmResponseDto>> getAlarm(
            AlarmSearchConditionDto condition,
            Pageable pageable) {
        // 3. 서비스 계층에 작업 위임
        Page<AlarmResponseDto> userPage = alarmService.findAlarms(condition, pageable);

        // 4. 결과 변환 및 HTTP 응답
        return ResponseEntity.ok(userPage);
    }


    @DeleteMapping
    public ResponseEntity<Void> deleteAlarm(@RequestBody DeleteItemListDto request) {
        alarmService.deleteAllAlarmByIdInBatch(request.getIds());
        // 성공적으로 삭제되었으며, 별도의 본문 내용 없이 응답한다는 의미
        return ResponseEntity.noContent().build();
    }


    // 1. 요청 접수: GET /api/alarm/
    @GetMapping("/history")
    public ResponseEntity<Page<? extends IBaseHistoryEntity>> getAlarmHistory(
            AlarmSearchConditionDto condition,
            Pageable pageable) {
        // 컨트롤러가 서비스에 '어떤 Entity'의 히스토리인지 명시적으로 전달
        Page<? extends IBaseHistoryEntity> historyPage = historyService.getHistory(AlarmHistoryEntity.class,condition,pageable);
        // 4. 결과 변환 및 HTTP 응답
        return ResponseEntity.ok(historyPage);
    }

}
