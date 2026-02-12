package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.service.LotService;
import kr.co.aim.common.dto.*;
import kr.co.aim.common.format.response.UserResponse;
import kr.co.aim.domain.model.Lots;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Lot", description = "Lot 관련 API")
@RestController
@RequestMapping("/api/lots")
@RequiredArgsConstructor
public class LotController {

    private final LotService lotService;

    @Operation(summary = "lot 생성", description = "lot를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: POST /api/lots
    @PostMapping
    public ResponseEntity<LotsResponseDto> createLots(@RequestBody LotsCreateRequestDto requestDto) {
        // 3. 서비스 계층에 작업 위임
        Lots lots = lotService.createLots(requestDto);

        // 4. 결과 변환 및 HTTP 응답
        LotsResponseDto responseDto = LotsResponseDto.builder()
                .id(lots.getId())
                .lotName(lots.getLotName())
                .eventName(lots.getEventName())
                .eventTime(lots.getEventTime())
                .eventUser(lots.getEventUser())
                .eventComment(lots.getEventComment())
                .build();
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "lot 정보 변경", description = "ID를 이용하여 특정 lot의 정보를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: PATCH /api/lots/{lot-id}
    @PatchMapping("/{lot-id}")
    public ResponseEntity<LotsResponseDto> changeLots(
            @Parameter(description = "변경할 권한의 ID", required = true, example = "1")
            @PathVariable("lot-id") Long lotId,
            @RequestBody LotsUpdateRequestDto requestDto) {
        // 3. 서비스 계층에 작업 위임
        Lots lots = lotService.changeLots(lotId,requestDto);
        // 4. 결과 변환 및 HTTP 응답
        LotsResponseDto responseDto = LotsResponseDto.builder()
                .id(lots.getId())
                .lotName(lots.getLotName())
                .eventName(lots.getEventName())
                .eventTime(lots.getEventTime())
                .eventUser(lots.getEventUser())
                .eventComment(lots.getEventComment())
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "lot 정보 조회", description = "lot 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: GET /api/lots/
    @GetMapping
    public ResponseEntity<Page<LotsResponseDto>> getLots(
            LotsSearchConditionDto condition,
            Pageable pageable) {
        // 3. 서비스 계층에 작업 위임
        Page<LotsResponseDto> page = lotService.findLots(condition, pageable);

        // 4. 결과 변환 및 HTTP 응답
        return ResponseEntity.ok(page);
    }


    @DeleteMapping
    public ResponseEntity<Void> deleteLots(@RequestBody DeleteItemListDto request) {
        lotService.deleteAllByIdInBatch(request.getIds());
        // 성공적으로 삭제되었으며, 별도의 본문 내용 없이 응답한다는 의미
        return ResponseEntity.noContent().build();
    }

}
