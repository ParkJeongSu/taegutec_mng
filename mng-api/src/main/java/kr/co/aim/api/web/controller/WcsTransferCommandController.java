package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.aim.api.dto.WcsTransferCommandCreateRequestDto;
import kr.co.aim.api.dto.WcsTransferCommandHistoryResponse;
import kr.co.aim.api.dto.WcsTransferCommandResponse;
import kr.co.aim.api.dto.WcsTransferCommandUpdateRequestDto;
import kr.co.aim.api.service.WcsTransferCommandService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.common.condition.WcsTransferCommandHistorySearchCondition;
import kr.co.aim.common.condition.WcsTransferCommandSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "WCS Transfer Command", description = "WCS 반송 명령(TRANSFERCOMMAND) 지시 및 상태 추적 관리 API")
@RestController
@Profile("web")
@ResponseAnnotation
@RequestMapping("/api/v1/wcs/transfer-command")
@RequiredArgsConstructor
public class WcsTransferCommandController {

    private final WcsTransferCommandService wcsTransferCommandService;

    /**
     * WCS 반송 명령 이력 조건별 목록 조회 (페이징 지원)
     */
    @Operation(summary = "WCS 반송 명령 이력 목록 조회", description = "동적 조건 및 페이징 처리를 통한 WCS 반송 명령 이력 목록 조회")
    @GetMapping("/history")
    public ResponseEntity<Page<WcsTransferCommandHistoryResponse>> findHistory(
            WcsTransferCommandHistorySearchCondition condition,
            Pageable pageable) {
        Page<WcsTransferCommandHistoryResponse> response = wcsTransferCommandService.findHistory(condition, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * WCS 반송 명령 조건별 목록 조회 (페이징 지원)
     */
    @Operation(summary = "WCS 반송 명령 목록 조회", description = "동적 조건 및 페이징 처리를 통한 WCS 반송 명령 목록 조회")
    @GetMapping
    public ResponseEntity<Page<WcsTransferCommandResponse>> findTransferCommands(
            WcsTransferCommandSearchCondition condition,
            Pageable pageable) {
        Page<WcsTransferCommandResponse> response = wcsTransferCommandService.findTransferCommands(condition, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 단일 PK 기반 WCS 반송 명령 단건 상세 조회
     */
    @Operation(summary = "WCS 반송 명령 단건 상세 조회", description = "반송 명령 명(transferCommandName) 기반 단건 상세 조회")
    @GetMapping("/{transferCommandName}")
    public ResponseEntity<WcsTransferCommandResponse> findById(
            @PathVariable String transferCommandName) {
        WcsTransferCommandResponse response = wcsTransferCommandService.findById(transferCommandName);
        return ResponseEntity.ok(response);
    }

    /**
     * 신규 WCS 반송 명령 등록
     */
    @Operation(summary = "WCS 반송 명령 등록", description = "신규 WCS 반송 명령 정보 등록 및 이력 적재")
    @PostMapping
    public ResponseEntity<WcsTransferCommandResponse> createTransferCommand(
            @RequestBody @Valid WcsTransferCommandCreateRequestDto dto) {
        WcsTransferCommandResponse response = wcsTransferCommandService.createTransferCommand(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 단일 PK 기반 WCS 반송 명령 정보 수정
     */
    @Operation(summary = "WCS 반송 명령 수정", description = "기존 WCS 반송 명령 정보 수정 및 변경 이력 적재")
    @PutMapping("/{transferCommandName}")
    public ResponseEntity<WcsTransferCommandResponse> updateTransferCommand(
            @PathVariable String transferCommandName,
            @RequestBody @Valid WcsTransferCommandUpdateRequestDto dto) {
        WcsTransferCommandResponse response = wcsTransferCommandService.updateTransferCommand(transferCommandName, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * 단일 PK 기반 WCS 반송 명령 삭제
     */
    @Operation(summary = "WCS 반송 명령 삭제", description = "WCS 반송 명령 삭제 및 삭제 이력 적재")
    @DeleteMapping("/{transferCommandName}")
    public ResponseEntity<Void> deleteTransferCommand(
            @PathVariable String transferCommandName,
            @RequestParam(required = false) String eventUser,
            @RequestParam(required = false) String eventComment) {
        wcsTransferCommandService.deleteTransferCommand(transferCommandName, eventUser, eventComment);
        return ResponseEntity.noContent().build();
    }
}
