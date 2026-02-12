package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.service.PortService;
import kr.co.aim.common.dto.*;
import kr.co.aim.common.format.response.UserResponse;
import kr.co.aim.domain.model.Ports;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Port", description = "포트 관련 API")
@RestController
@RequestMapping("/api/ports")
@RequiredArgsConstructor
public class PortController {

    private final PortService portService;

    @Operation(summary = "포트 생성", description = "포트를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: POST /api/ports
    @PostMapping
    public ResponseEntity<PortsResponseDto> createPorts(@RequestBody PortsCreateRequestDto requestDto) {
        // 3. 서비스 계층에 작업 위임
        Ports ports = portService.createPorts(requestDto);

        // 4. 결과 변환 및 HTTP 응답
        PortsResponseDto responseDto = PortsResponseDto.builder()
                .id(ports.getId())
                .equipmentName(ports.getEquipmentName())
                .portName(ports.getPortName())
                .description(ports.getDescription())
                .equipmentName(ports.getEquipmentName())
                .portName(ports.getPortName())
                .portState(ports.getPortState())
                .connectedStocker(ports.getConnectedStocker())
                .transportMode(ports.getTransportMode())
                .transportState(ports.getTransportState())
                .eventName(ports.getEventName())
                .eventTime(ports.getEventTime())
                .eventUser(ports.getEventUser())
                .eventComment(ports.getEventComment())
                .build();
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "포트 정보 변경", description = "ID를 이용하여 특정 포트의 정보를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: PATCH /api/ports/{port-id}
    @PatchMapping("/{port-id}")
    public ResponseEntity<PortsResponseDto> changePorts(
            @Parameter(description = "변경할 권한의 ID", required = true, example = "1")
            @PathVariable("port-id") Long portId,
            @RequestBody PortsUpdateRequestDto requestDto) {
        // 3. 서비스 계층에 작업 위임
        Ports ports = portService.changePort(portId,requestDto);
        // 4. 결과 변환 및 HTTP 응답
        PortsResponseDto responseDto = PortsResponseDto.builder()
                .id(ports.getId())
                .equipmentName(ports.getEquipmentName())
                .portName(ports.getPortName())
                .description(ports.getDescription())
                .equipmentName(ports.getEquipmentName())
                .portName(ports.getPortName())
                .portState(ports.getPortState())
                .connectedStocker(ports.getConnectedStocker())
                .transportMode(ports.getTransportMode())
                .transportState(ports.getTransportState())
                .eventName(ports.getEventName())
                .eventTime(ports.getEventTime())
                .eventUser(ports.getEventUser())
                .eventComment(ports.getEventComment())
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "포트 정보 조회", description = "포트 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: GET /api/ports/
    @GetMapping
    public ResponseEntity<Page<PortsResponseDto>> getPorts(
            PortsSearchConditionDto condition,
            Pageable pageable) {
        // 3. 서비스 계층에 작업 위임
        Page<PortsResponseDto> page = portService.findPorts(condition, pageable);

        // 4. 결과 변환 및 HTTP 응답
        return ResponseEntity.ok(page);
    }


    @DeleteMapping
    public ResponseEntity<Void> deletePorts(@RequestBody DeleteItemListDto request) {
        portService.deleteAllPortDefByIdInBatch(request.getIds());
        // 성공적으로 삭제되었으며, 별도의 본문 내용 없이 응답한다는 의미
        return ResponseEntity.noContent().build();
    }

}
