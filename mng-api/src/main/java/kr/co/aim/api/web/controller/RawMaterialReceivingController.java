package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.dto.powder.*;
import kr.co.aim.api.service.RawMaterialReceivingService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "MNG 원자재 입고", description = "원자재 입고 API")
@RestController
@RequestMapping("/api/v1/mng/raw-material-receive")
@RequiredArgsConstructor
@Profile("web")
@ResponseAnnotation
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public class RawMaterialReceivingController {

    private final RawMaterialReceivingService rawMaterialReceivingService;

    /**
     * 1. 원자재 입고 시작
     * 작업자가 입고 오더를 선택하고 입고 작업을 개시합니다.
     */
    @Operation(summary = "원자재 입고 시작", description = "입고 오더를 선택하여 원자재 입고 작업을 개시합니다.")
    @PostMapping("/start")
    public ResponseEntity<Page<RawMaterialReceivingStartResponse>> startReceiving(
            @RequestBody RawMaterialReceivingStartRequest request
    ) {
        Page<RawMaterialReceivingStartResponse> result = rawMaterialReceivingService.startReceiving(request);
        return ResponseEntity.ok(result);
    }

    /**
     * 2. 원자재 팔레트 및 가방(Bag) 결합
     * 입고대의 팔레트 바코드/ID와 원자재 가방(Lot/Bag) 정보를 매핑합니다.
     */
    @Operation(summary = "원자재 팔레트 및 가방 결합", description = "입고대의 팔레트와 원자재 가방(Bag) 정보를 바인딩합니다.")
    @PostMapping("/bind-pallet-bag")
    public ResponseEntity<Page<PalletBagBindingResponse>> bindPalletAndBag(
            @RequestBody PalletBagBindingRequest request
    ) {
        Page<PalletBagBindingResponse> result = rawMaterialReceivingService.bindPalletAndBag(request);
        return ResponseEntity.ok(result);
    }

    /**
     * 3. 창고로 입고 시작 (창고 이송 요청)
     * 결합이 완료된 팔레트를 창고(자동창고/랙 등)로 입고/이송 처리합니다.
     */
    @Operation(summary = "창고 입고 시작", description = "결합이 완료된 팔레트를 창고로 입고/이송 처리합니다.")
    @PostMapping("/warehouse-inbound/start")
    public ResponseEntity<Page<WarehouseInboundStartResponse>> startWarehouseInbound(
            @RequestBody WarehouseInboundStartRequest request
    ) {
        Page<WarehouseInboundStartResponse> result = rawMaterialReceivingService.startWarehouseInbound(request);
        return ResponseEntity.ok(result);
    }
}