package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.service.AlarmService;
import kr.co.aim.common.dto.*;
import kr.co.aim.common.format.response.UserResponse;
import kr.co.aim.domain.model.AlarmActionUserGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AlarmUserGroup", description = "알람 유저 그룹 정의 관련 API")
@RestController
@RequestMapping("/api/alarm-user-group")
@RequiredArgsConstructor
public class AlarmUserGroupController {

    private final AlarmService alarmService;

    @Operation(summary = "알람 유저그룹 생성", description = "알람 유저 그룹을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: POST /api/alarm-user-group
    @PostMapping
    public ResponseEntity<AlarmActionUserGroupResponseDto> createAlarmUserGroup(@RequestBody AlarmActionUserGroupCreateRequestDto requestDto) {
        // 3. 서비스 계층에 작업 위임
        AlarmActionUserGroup alarmActionUserGroup = alarmService.createAlarmActionUserGroup(requestDto);

        // 4. 결과 변환 및 HTTP 응답
        AlarmActionUserGroupResponseDto responseDto =
                AlarmActionUserGroupResponseDto.builder()
                        .id(alarmActionUserGroup.getId())
                        .userGroupName(alarmActionUserGroup.getUserGroupName())
                        .eventName(alarmActionUserGroup.getEventName())
                        .eventTime(alarmActionUserGroup.getEventTime())
                        .eventUser(alarmActionUserGroup.getEventUser())
                        .eventComment(alarmActionUserGroup.getEventComment())
                        .build();
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "특정 Alarm 정보 변경", description = "ID를 이용하여 특정 Alarm의 정보를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: PATCH /api/alarm-user-group/{alarm-user-group-id}
    @PatchMapping("/{alarm-user-group-id}")
    public ResponseEntity<AlarmActionUserGroupResponseDto> changeAlarmUserGroup(
            @Parameter(description = "변경할 권한의 ID", required = true, example = "1")
            @PathVariable("alarm-user-group-id") Long alarmUserGroupId,
            @RequestBody AlarmActionUserGroupUpdateRequestDto requestDto) {
        // 3. 서비스 계층에 작업 위임
        AlarmActionUserGroup alarmActionUserGroup = alarmService.changeAlarmActionUserGroup(alarmUserGroupId,requestDto);
        // 4. 결과 변환 및 HTTP 응답
        AlarmActionUserGroupResponseDto responseDto = AlarmActionUserGroupResponseDto.builder()
                .id(alarmActionUserGroup.getId())
                .userGroupName(alarmActionUserGroup.getUserGroupName())
                .eventName(alarmActionUserGroup.getEventName())
                .eventTime(alarmActionUserGroup.getEventTime())
                .eventUser(alarmActionUserGroup.getEventUser())
                .eventComment(alarmActionUserGroup.getEventComment())
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "알람 유저 그룹 정보 조회", description = "알람 유저 그룹을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: GET /api/alarm-user-group/
    @GetMapping
    public ResponseEntity<Page<AlarmActionUserGroupResponseDto>> getAlarmUserGroup(
            AlarmActionUserGroupSearchConditionDto condition,
            Pageable pageable) {
        // 3. 서비스 계층에 작업 위임
        Page<AlarmActionUserGroupResponseDto> userPage = alarmService.findAlarmActionUserGroups(condition, pageable);

        // 4. 결과 변환 및 HTTP 응답
        return ResponseEntity.ok(userPage);
    }


    @DeleteMapping
    public ResponseEntity<Void> deleteAlarmUserGroup(@RequestBody DeleteItemListDto request) {
        alarmService.deleteAllAlarmActionUserGroupByIdInBatch(request.getIds());
        // 성공적으로 삭제되었으며, 별도의 본문 내용 없이 응답한다는 의미
        return ResponseEntity.noContent().build();
    }

}
