package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.dto.*;
import kr.co.aim.api.service.CarrierService;
import kr.co.aim.common.format.response.UserResponse;
import kr.co.aim.domain.model.Carriers;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Carriers", description = "캐리어 관련 API")
@RestController
@RequestMapping("/api/v1/mng/carriers")
@RequiredArgsConstructor
public class CarrierController {

    private final CarrierService carrierService;

    @Operation(summary = "캐리어 정의 생성", description = "캐리어 정의를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: POST /api/carriers
    @PostMapping
    public ResponseEntity<CarriersResponseDto> createCarriers(@RequestBody CarriersCreateRequestDto requestDto) {
        // 3. 서비스 계층에 작업 위임
        Carriers carriers = carrierService.createCarriers(requestDto);

        // 4. 결과 변환 및 HTTP 응답
        CarriersResponseDto responseDto = CarriersResponseDto.builder()
                .id(carriers.getId())
                .carrierDefId(carriers.getCarrierDefId())
                .carrierName(carriers.getCarrierName())
                .carrierState(carriers.getCarrierState())
                .containerType(carriers.getContainerType())
                .eventName(carriers.getEventName())
                .eventTime(carriers.getEventTime())
                .eventUser(carriers.getEventUser())
                .eventComment(carriers.getEventComment())
                .build();
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "설비 정의 정보 변경", description = "ID를 이용하여 특정 설비 정의의 정보를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: PATCH /api/carriers/{carrier-id}
    @PatchMapping("/{carrier-id}")
    public ResponseEntity<CarriersResponseDto> changeCarriers(
            @Parameter(description = "변경할 권한의 ID", required = true, example = "1")
            @PathVariable("carrier-id") Long carrierId,
            @RequestBody CarriersUpdateRequestDto requestDto) {
        // 3. 서비스 계층에 작업 위임
        Carriers carriers = carrierService.changeCarriers(carrierId,requestDto);
        // 4. 결과 변환 및 HTTP 응답
        CarriersResponseDto responseDto = CarriersResponseDto.builder()
                .id(carriers.getId())
                .carrierDefId(carriers.getCarrierDefId())
                .carrierName(carriers.getCarrierName())
                .carrierState(carriers.getCarrierState())
                .containerType(carriers.getContainerType())
                .eventName(carriers.getEventName())
                .eventTime(carriers.getEventTime())
                .eventUser(carriers.getEventUser())
                .eventComment(carriers.getEventComment())
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "캐리어 정보 조회", description = "캐리어 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    // 1. 요청 접수: GET /api/carriers/
    @GetMapping
    public ResponseEntity<Page<CarriersResponseDto>> getCarriers(
            CarriersSearchConditionDto condition,
            Pageable pageable) {
        // 3. 서비스 계층에 작업 위임
        Page<CarriersResponseDto> page = carrierService.findCarriers(condition, pageable);

        // 4. 결과 변환 및 HTTP 응답
        return ResponseEntity.ok(page);
    }


    @DeleteMapping
    public ResponseEntity<Void> deleteCarriers(@RequestBody DeleteItemListDto request) {
        carrierService.deleteAllCarriersByIdInBatch(request.getIds());
        // 성공적으로 삭제되었으며, 별도의 본문 내용 없이 응답한다는 의미
        return ResponseEntity.noContent().build();
    }

}
