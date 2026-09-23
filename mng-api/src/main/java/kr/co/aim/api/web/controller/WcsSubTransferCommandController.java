package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.aim.api.dto.WcsSubTransferCommandCreateRequestDto;
import kr.co.aim.api.dto.WcsSubTransferCommandResponse;
import kr.co.aim.api.dto.WcsSubTransferCommandUpdateRequestDto;
import kr.co.aim.api.service.WcsSubTransferCommandService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.common.condition.WcsSubTransferCommandSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "WCS Sub Transfer Command", description = "WCS 하위 반송 명령(SUBTRANSFERCOMMAND) 실행 및 상태 추적 관리 API")
@RestController
@Profile("web")
@ResponseAnnotation
@RequestMapping("/api/v1/wcs/sub-transfer-command")
@RequiredArgsConstructor
public class WcsSubTransferCommandController {

    private final WcsSubTransferCommandService wcsSubTransferCommandService;

    /**
     * WCS 하위 반송 명령 조건별 목록 조회 (페이징 지원)
     */
    @Operation(summary = "WCS 하위 반송 명령 목록 조회", description = "동적 조건 및 페이징 처리를 통한 WCS 하위 반송 명령 목록 조회")
    @GetMapping
    public ResponseEntity<Page<WcsSubTransferCommandResponse>> findSubTransferCommands(
            WcsSubTransferCommandSearchCondition condition,
            Pageable pageable) {
        Page<WcsSubTransferCommandResponse> response = wcsSubTransferCommandService.findSubTransferCommands(condition, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 복합키 기반 WCS 하위 반송 명령 단건 상세 조회
     */
    @Operation(summary = "WCS 하위 반송 명령 단건 상세 조회", description = "2개 복합키(transferCommandName, jobNo) 기반 단건 상세 조회")
    @GetMapping("/{transferCommandName}/{jobNo}")
    public ResponseEntity<WcsSubTransferCommandResponse> findById(
            @PathVariable String transferCommandName,
            @PathVariable Integer jobNo) {
        WcsSubTransferCommandResponse response = wcsSubTransferCommandService.findById(transferCommandName, jobNo);
        return ResponseEntity.ok(response);
    }

    /**
     * 상위 반송 명령 명(transferCommandName)에 속한 하위 반송 명령 목록 조회
     */
    @Operation(summary = "상위 반송 명령별 하위 반송 명령 목록 조회", description = "상위 반송 명령 명(transferCommandName)을 기준으로 연계된 모든 하위 반송 명령 목록 조회")
    @GetMapping("/by-command/{transferCommandName}")
    public ResponseEntity<List<WcsSubTransferCommandResponse>> findByTransferCommandName(
            @PathVariable String transferCommandName) {
        List<WcsSubTransferCommandResponse> response = wcsSubTransferCommandService.findByTransferCommandName(transferCommandName);
        return ResponseEntity.ok(response);
    }

    /**
     * 신규 WCS 하위 반송 명령 등록
     */
    @Operation(summary = "WCS 하위 반송 명령 등록", description = "신규 WCS 하위 반송 명령 정보 등록 및 이력 적재")
    @PostMapping
    public ResponseEntity<WcsSubTransferCommandResponse> createSubTransferCommand(
            @RequestBody @Valid WcsSubTransferCommandCreateRequestDto dto) {
        WcsSubTransferCommandResponse response = wcsSubTransferCommandService.createSubTransferCommand(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 복합키 기반 WCS 하위 반송 명령 정보 수정
     */
    @Operation(summary = "WCS 하위 반송 명령 수정", description = "기존 WCS 하위 반송 명령 정보 수정 및 변경 이력 적재")
    @PutMapping("/{transferCommandName}/{jobNo}")
    public ResponseEntity<WcsSubTransferCommandResponse> updateSubTransferCommand(
            @PathVariable String transferCommandName,
            @PathVariable Integer jobNo,
            @RequestBody @Valid WcsSubTransferCommandUpdateRequestDto dto) {
        WcsSubTransferCommandResponse response = wcsSubTransferCommandService.updateSubTransferCommand(transferCommandName, jobNo, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * 복합키 기반 WCS 하위 반송 명령 삭제
     */
    @Operation(summary = "WCS 하위 반송 명령 삭제", description = "WCS 하위 반송 명령 삭제 및 삭제 이력 적재")
    @DeleteMapping("/{transferCommandName}/{jobNo}")
    public ResponseEntity<Void> deleteSubTransferCommand(
            @PathVariable String transferCommandName,
            @PathVariable Integer jobNo,
            @RequestParam(required = false) String eventUser,
            @RequestParam(required = false) String eventComment) {
        wcsSubTransferCommandService.deleteSubTransferCommand(transferCommandName, jobNo, eventUser, eventComment);
        return ResponseEntity.noContent().build();
    }
}
