package kr.co.aim.api.web.controller;

import kr.co.aim.api.service.DB2TransportOrderService;
import kr.co.aim.api.service.DataTransferService;
import kr.co.aim.common.dto.*;
import kr.co.aim.domain.model.Alarm;
import kr.co.aim.infra.persistence.entity.TransportOrderEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/simulator")
@RequiredArgsConstructor
@Slf4j
public class SimulatorController {

    private final DB2TransportOrderService db2TransportOrderService;
    private final DataTransferService dataTransferService;

    @GetMapping("/idocs")
    public ResponseEntity<Page<IdocResponseDto>> getIdocList(Pageable pageable) {
        // 3. 서비스 계층에 작업 위임
        Page<IdocResponseDto> idocPage = db2TransportOrderService.selectIdocs(pageable);

        return ResponseEntity.ok(idocPage);
    }

    @GetMapping("/h2orderm")
    public ResponseEntity<Page<H2OrderMResponseDto>> getH2OrderMList(@RequestParam Long idocId,Pageable pageable) {
        // 3. 서비스 계층에 작업 위임
        Page<H2OrderMResponseDto> h2orderMPage = db2TransportOrderService.selectH2OrderMByIdocId(idocId,pageable);

        return ResponseEntity.ok(h2orderMPage);
    }

    @GetMapping("/h2orderd")
    public ResponseEntity<Page<H2OrderDResponseDto>> getH2OrderDList(@RequestParam Long idocId,Pageable pageable) {
        // 3. 서비스 계층에 작업 위임
        Page<H2OrderDResponseDto> h2orderDPage = db2TransportOrderService.selectH2OrderDByIdocId(idocId,pageable);

        return ResponseEntity.ok(h2orderDPage);
    }

    @GetMapping("/h2trans")
    public ResponseEntity<Page<H2TransResponseDto>> getH2TransList(@RequestParam Long idocId,Pageable pageable) {
        // 3. 서비스 계층에 작업 위임
        Page<H2TransResponseDto> h2TransPage = db2TransportOrderService.selectH2TransByIdocId(idocId,pageable);

        return ResponseEntity.ok(h2TransPage);
    }

    @GetMapping("/transport-order")
    public ResponseEntity<Page<TransportOrderResponseDto>> getTransportOrderList(Pageable pageable) {
        // 3. 서비스 계층에 작업 위임
        Page<TransportOrderResponseDto> transportOrderPage = db2TransportOrderService.selectTransportOrder(pageable);

        return ResponseEntity.ok(transportOrderPage);
    }


    @PostMapping("/outbound/{idocId}")
    public ResponseEntity<TransportOrderResponseDto> processOutboundTransfer(@PathVariable Long idocId) {
        log.info("인터페이스 수동 실행 요청 - idocId: {}", idocId);

        // 1. 통합 서비스 호출 (DB2 조회 -> MSSQL 저장 -> DB2 상태 변경)
        // 반환값은 MSSQL에 저장된 최종 객체의 DTO입니다.
        TransportOrderEntity entity = dataTransferService.transferOutboundOrder(idocId);

        TransportOrderResponseDto result =
                TransportOrderResponseDto.builder()
                        .id(entity.getId())
                        .transportOrderName(entity.getTransportOrderName())
                        .description(entity.getDescription())
                        .transportType(entity.getTransportType())
                        .transportOrderId(entity.getTransportOrderId())
                        .priority(entity.getPriority())
                        .galId(entity.getGalId())
                        .galWarehouse(entity.getGalWarehouse())
                        .fromWarehouse(entity.getFromWarehouse())
                        .fromZoneName(entity.getFromZoneName())
                        .fromLocationId(entity.getFromLocationId())
                        .toWarehouse(entity.getToWarehouse())
                        .toZoneName(entity.getToZoneName())
                        .toLocationId(entity.getToLocationId())
                        .carrierName(entity.getCarrierName())
                        .carrierType(entity.getCarrierType())
                        .drivingProfile(entity.getDrivingProfile())
                        .createTime(entity.getCreateTime())
                        .releaseTime(entity.getReleaseTime())
                        .completeTime(entity.getCompleteTime())
                        .createUser(entity.getCreateUser())
                        .releaseUser(entity.getReleaseUser())
                        .completeUser(entity.getCompleteUser())
                        .build();
        return ResponseEntity.ok(result);
    }

    // SimulatorController.java 에 추가
    @PostMapping("/accept")
    public ResponseEntity<Void> acceptTransportOrder(@RequestBody SimulatorIdsDto request) {
        log.info("Accept 요청 수신: {} 건", request.getIds().size());
        dataTransferService.acceptOutboundOrder(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }



}
