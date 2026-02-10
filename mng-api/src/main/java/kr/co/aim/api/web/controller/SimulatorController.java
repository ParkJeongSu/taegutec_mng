package kr.co.aim.api.web.controller;

import kr.co.aim.api.service.DB2TransportOrderService;
import kr.co.aim.api.service.DataTransferService;
import kr.co.aim.common.dto.*;
import kr.co.aim.common.enums.TransportStatus;
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

    @GetMapping("outbound/idocs")
    public ResponseEntity<Page<IdocResponseDto>> getIdocListByOutbound(Pageable pageable) {
        // 3. 서비스 계층에 작업 위임
        Page<IdocResponseDto> idocPage = db2TransportOrderService.selectIdocsByOrderType(pageable,"O");

        return ResponseEntity.ok(idocPage);
    }

    @GetMapping("inbound/idocs")
    public ResponseEntity<Page<IdocResponseDto>> getIdocListByInbound(Pageable pageable) {
        // 3. 서비스 계층에 작업 위임
        Page<IdocResponseDto> idocPage = db2TransportOrderService.selectIdocsByOrderType(pageable,"I");

        return ResponseEntity.ok(idocPage);
    }

    @GetMapping("/h2orderm")
    public ResponseEntity<Page<H2OrderMResponseDto>> getH2OrderMList(@RequestParam Long idocId,Pageable pageable) {
        // 3. 서비스 계층에 작업 위임
        Page<H2OrderMResponseDto> h2orderMPage = db2TransportOrderService.selectH2OrderMByIdocId(idocId,pageable);

        return ResponseEntity.ok(h2orderMPage);
    }

    @GetMapping("/order-detail/{idocId}")
    public ResponseEntity<H2OrderDetailResponseDto> getH2OrderDetailList(@PathVariable("idocId") Long idocId) {
        // 3. 서비스 계층에 작업 위임
        H2OrderDetailResponseDto h2orderMPage = db2TransportOrderService.selectH2OrderDetailByIdocId(idocId);

        return ResponseEntity.ok(h2orderMPage);
    }

    @GetMapping("/h2orderd")
    public ResponseEntity<Page<H2OrderDResponseDto>> getH2OrderDList(@RequestParam Long idocId,Pageable pageable) {
        // 3. 서비스 계층에 작업 위임
        Page<H2OrderDResponseDto> h2orderDPage = db2TransportOrderService.selectH2OrderDByIdocId(idocId,pageable);

        return ResponseEntity.ok(h2orderDPage);
    }

    @GetMapping("/h2trans")
    public ResponseEntity<Page<H2TransResponseDto>> getH2TransList(@RequestParam Long orderId,Pageable pageable) {
        // 3. 서비스 계층에 작업 위임
        Page<H2TransResponseDto> h2TransPage = db2TransportOrderService.selectH2TransByOrderId(orderId,pageable);

        return ResponseEntity.ok(h2TransPage);
    }

    @GetMapping("/transport-order/{orderId}")
    public ResponseEntity<TransportOrderResponseDto> getTransportOrderList(@PathVariable("orderId") Long orderId) {
        // 3. 서비스 계층에 작업 위임
        TransportOrderResponseDto transportOrderPage = db2TransportOrderService.selectTransportOrder(orderId);

        TransportStatus status = TransportStatus.valueOf(transportOrderPage.getTransportStatus());
        String transportStatus = status.getFullStatus();
        transportOrderPage.setTransportStatus(transportStatus);

        return ResponseEntity.ok(transportOrderPage);
    }


    @PostMapping("/outbound/transfer/{idocId}")
    public ResponseEntity<TransportOrderResponseDto> processOutboundTransfer(@PathVariable Long idocId) {
        log.info("인터페이스 수동 실행 요청 - idocId: {}", idocId);

        // 1. 통합 서비스 호출 (DB2 조회 -> MSSQL 저장 -> DB2 상태 변경)
        // 반환값은 MSSQL에 저장된 최종 객체의 DTO입니다.
        TransportOrderEntity entity = dataTransferService.transferOutboundOrder(idocId);
        TransportStatus status = TransportStatus.valueOf(entity.getTransportStatus());
        String transportStatus = status.getFullStatus();

        TransportOrderResponseDto result =
                TransportOrderResponseDto.builder()
                        .id(entity.getId())
                        .transportOrderName(entity.getTransportOrderName())
                        .description(entity.getDescription())
                        .transportType(entity.getTransportType())
                        .transportOrderId(entity.getTransportOrderId())
                        .transportStatus(transportStatus)
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

    @PostMapping("/inbound/transfer/{idocId}")
    public ResponseEntity<TransportOrderResponseDto> processInboundTransfer(@PathVariable Long idocId) {
        log.info("인터페이스 수동 실행 요청 - idocId: {}", idocId);

        // 1. 통합 서비스 호출 (DB2 조회 -> MSSQL 저장 -> DB2 상태 변경)
        // 반환값은 MSSQL에 저장된 최종 객체의 DTO입니다.
        TransportOrderEntity entity = dataTransferService.transferInboundOrder(idocId);
        TransportStatus status = TransportStatus.valueOf(entity.getTransportStatus());
        String transportStatus = status.getFullStatus();

        TransportOrderResponseDto result =
                TransportOrderResponseDto.builder()
                        .id(entity.getId())
                        .transportOrderName(entity.getTransportOrderName())
                        .description(entity.getDescription())
                        .transportType(entity.getTransportType())
                        .transportOrderId(entity.getTransportOrderId())
                        .transportStatus(transportStatus)
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
    @PostMapping("/outbound/accept")
    public ResponseEntity<Void> acceptOutbound(@RequestBody SimulatorIdsDto request) {
        log.info("Accept 요청 수신: {} 건", request.getIds().size());
        dataTransferService.acceptOutboundOrder(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/inbound/accept")
    public ResponseEntity<Void> acceptInbound(@RequestBody SimulatorIdsDto request) {
        log.info("Accept 요청 수신: {} 건", request.getIds().size());
        dataTransferService.acceptInboundOrder(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/inbound/workstation-empty")
    public ResponseEntity<Void> workstationEmptyInbound(@RequestBody SimulatorIdsDto request) {
        log.info("workstationEmpty 요청 수신: {} 건", request.getIds().size());
        dataTransferService.workStationEmptyInboundOrder(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/inbound/arrived-workstation-error")
    public ResponseEntity<Void> arrivalWorkstationErrorInbound(@RequestBody SimulatorIdsDto request) {
        log.info("arrivalWorkstationErrorInbound 요청 수신: {} 건", request.getIds().size());
        dataTransferService.arrivedWorkstationErrorInboundOrder(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/inbound/error-text")
    public ResponseEntity<Void> errorTextInbound(@RequestBody SimulatorIdsDto request) {
        log.info("errorTextInbound 요청 수신: {} 건", request.getIds().size());
        dataTransferService.arrivedWorkstationErrorInboundOrder(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/outbound/release")
    public ResponseEntity<Void> releaseTransportOrder(@RequestBody SimulatorIdsDto request) {
        log.info("Release 요청 수신: {} 건", request.getIds().size());
        dataTransferService.releaseOutboundOrder(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/outbound/internal-relocation")
    public ResponseEntity<Void> internalRelocationTransportOrder(@RequestBody SimulatorIdsDto request) {
        log.info("internal-relocation 요청 수신: {} 건", request.getIds().size());
        dataTransferService.internalRelocationOutboundOrder(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/outbound/out-of-rack")
    public ResponseEntity<Void> outOfRackTransportOrder(@RequestBody SimulatorIdsDto request) {
        log.info("out-of-rack 요청 수신: {} 건", request.getIds().size());
        dataTransferService.outOfRackOutboundOrder(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/outbound/arrived-at-workstation")
    public ResponseEntity<Void> arrivedAtWorkstationTransportOrder(@RequestBody SimulatorIdsDto request) {
        log.info("out-of-rack 요청 수신: {} 건", request.getIds().size());
        dataTransferService.arrivedAtWorkstationOutboundOrder(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/outbound/completed")
    public ResponseEntity<Void> completedTransportOrder(@RequestBody SimulatorIdsDto request) {
        log.info("out-of-rack 요청 수신: {} 건", request.getIds().size());
        dataTransferService.completedOutboundOrder(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/outbound/take-off")
    public ResponseEntity<Void> takeOffTransportOrder(@RequestBody SimulatorIdsDto request) {
        log.info("out-of-rack 요청 수신: {} 건", request.getIds().size());
        dataTransferService.takeOffOutboundOrder(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/outbound/bin-empty")
    public ResponseEntity<Void> binEmptyTransportOrder(@RequestBody SimulatorIdsDto request) {
        log.info("bin-empty 요청 수신: {} 건", request.getIds().size());
        dataTransferService.binEmptyOutboundOrder(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/outbound/shortage")
    public ResponseEntity<Void> shortageTransportOrder(@RequestBody SimulatorIdsDto request) {
        log.info("bin-empty 요청 수신: {} 건", request.getIds().size());
        dataTransferService.shortageOutboundOrder(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/outbound/not-allowed-pick-up")
    public ResponseEntity<Void> notAllowedPickUpTransportOrder(@RequestBody SimulatorIdsDto request) {
        log.info("not-allowed-pick-up 요청 수신: {} 건", request.getIds().size());
        dataTransferService.notAllowedPickUpOutboundOrder(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/outbound/arrived-at-rack")
    public ResponseEntity<Void> arrivedAtRackTransportOrder(@RequestBody SimulatorIdsDto request) {
        log.info("arrived-at-rack 요청 수신: {} 건", request.getIds().size());
        dataTransferService.arrivedAtRackOutboundOrder(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/inbound/station-occupied")
    public ResponseEntity<Void> stationOccupied(@RequestBody StationOccupiedDto request) {
        log.info("stationOccupied 요청 ");
        dataTransferService.stationOccupied(request);
        return ResponseEntity.noContent().build();
    }

}
