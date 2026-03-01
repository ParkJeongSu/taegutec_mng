package kr.co.aim.api.web.controller;

import kr.co.aim.api.dto.*;
import kr.co.aim.api.service.ExternalInterfaceService;
import kr.co.aim.api.service.TransportOrderFacade;
import kr.co.aim.api.service.TransportOrderService;
import kr.co.aim.api.vo.H2OrderDetailRelocationVo;
import kr.co.aim.api.vo.H2OrderDetailVo;
import kr.co.aim.common.enums.TransportStatus;
import kr.co.aim.infra.persistence.entity.TransportOrderEntity;
import kr.co.aim.infra.persistence.entitydb2.H2OrderDEntity;
import kr.co.aim.infra.persistence.entitydb2.H2OrderMEntity;
import kr.co.aim.infra.persistence.entitydb2.H2TransEntity;
import kr.co.aim.infra.persistence.entitydb2.IdocEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/simulator")
@RequiredArgsConstructor
@Slf4j
@Profile({"scheduler","simulator"})
public class SimulatorController {

    private final TransportOrderService transportOrderService;
    private final TransportOrderFacade transportOrderFacade;
    private final ExternalInterfaceService externalInterfaceService;

    @GetMapping("/h2orderm")
    public ResponseEntity<Page<H2OrderMResponseDto>> getH2OrderMList(@RequestParam Long idocId, Pageable pageable) {
        // 3. 서비스 계층에 작업 위임
        Page<H2OrderMEntity> h2orderMPageEntity = externalInterfaceService.selectH2OrderMByIdocId(idocId,pageable);

        return ResponseEntity.ok(h2orderMPageEntity.map(H2OrderMResponseDto::from));
    }

    @GetMapping("/order-detail/{idocId}")
    public ResponseEntity<H2OrderDetailResponseDto> getH2OrderDetailList(@PathVariable("idocId") Long idocId) {
        // 3. 서비스 계층에 작업 위임
        H2OrderDetailVo vo = externalInterfaceService.selectH2OrderDetailByIdocId(idocId);

        return ResponseEntity.ok(H2OrderDetailResponseDto.from(vo));
    }

    @GetMapping("/h2orderd")
    public ResponseEntity<Page<H2OrderDResponseDto>> getH2OrderDList(@RequestParam Long idocId, Pageable pageable) {
        // 3. 서비스 계층에 작업 위임
        Page<H2OrderDEntity> h2OrderDEntities = externalInterfaceService.selectH2OrderDByIdocId(idocId,pageable);

        return ResponseEntity.ok(h2OrderDEntities.map(H2OrderDResponseDto::from));
    }

    @GetMapping("/h2trans")
    public ResponseEntity<Page<H2TransResponseDto>> getH2TransList(@RequestParam Long orderId, Pageable pageable) {
        // 3. 서비스 계층에 작업 위임
        Page<H2TransEntity> h2TransPage = externalInterfaceService.selectH2TransByOrderId(orderId,pageable);

        return ResponseEntity.ok(h2TransPage.map(H2TransResponseDto::from));
    }

    @GetMapping("/transport-order/{orderId}")
    public ResponseEntity<TransportOrderResponseDto> getTransportOrderList(@PathVariable("orderId") Long orderId) {
        // 3. 서비스 계층에 작업 위임
        TransportOrderEntity entity = transportOrderService.selectTransportOrder(orderId);

        TransportStatus status = TransportStatus.valueOf(entity.getTransportStatus());
        String transportStatus = status.getFullStatus();

        TransportOrderResponseDto result = TransportOrderResponseDto.from(entity);
        result.setTransportStatus(transportStatus);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/outbound/idocs")
    public ResponseEntity<Page<IdocResponseDto>> getOutboundIdocList(Pageable pageable) {
        // 3. 서비스 계층에 작업 위임
        Page<IdocEntity> idocEntities = externalInterfaceService.selectIdocListByOrderType(pageable,"O");

        return ResponseEntity.ok(idocEntities.map(IdocResponseDto::from));
    }

    @PostMapping("/outbound/transfer/{idocId}")
    public ResponseEntity<TransportOrderResponseDto> transferOutbound(@PathVariable Long idocId) {
        log.info("인터페이스 수동 실행 요청 - idocId: {}", idocId);

        // 1. 통합 서비스 호출 (DB2 조회 -> MSSQL 저장 -> DB2 상태 변경)
        // 반환값은 MSSQL에 저장된 최종 객체의 DTO입니다.
        TransportOrderEntity entity = transportOrderFacade.transferOutbound(idocId);
        TransportStatus status = TransportStatus.valueOf(entity.getTransportStatus());
        String transportStatus = status.getFullStatus();

        TransportOrderResponseDto result = TransportOrderResponseDto.from(entity);
        result.setTransportStatus(transportStatus);

        return ResponseEntity.ok(result);
    }

    // SimulatorController.java 에 추가
    @PostMapping("/outbound/accept")
    public ResponseEntity<Void> acceptOutbound(@RequestBody SimulatorIdsDto request) {
        log.info("Accept 요청 수신: {} 건", request.getIds().size());
        transportOrderFacade.acceptOutbound(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/outbound/release")
    public ResponseEntity<Void> releaseOutbound(@RequestBody SimulatorIdsDto request) {
        log.info("Release 요청 수신: {} 건", request.getIds().size());
        transportOrderFacade.releaseOutbound(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/outbound/internal-relocation")
    public ResponseEntity<Void> internalRelocationOutbound(@RequestBody SimulatorIdsDto request) {
        log.info("internal-relocation 요청 수신: {} 건", request.getIds().size());
        transportOrderFacade.internalRelocationOutbound(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/outbound/out-of-rack")
    public ResponseEntity<Void> outOfRackOutbound(@RequestBody SimulatorIdsDto request) {
        log.info("out-of-rack 요청 수신: {} 건", request.getIds().size());
        transportOrderFacade.outOfRackOutbound(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/outbound/arrived-at-workstation")
    public ResponseEntity<Void> arrivedAtWorkstationOutbound(@RequestBody SimulatorIdsDto request) {
        log.info("out-of-rack 요청 수신: {} 건", request.getIds().size());
        transportOrderFacade.arrivedAtWorkstationOutbound(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/outbound/completed")
    public ResponseEntity<Void> completedOutbound(@RequestBody SimulatorIdsDto request) {
        log.info("out-of-rack 요청 수신: {} 건", request.getIds().size());
        transportOrderFacade.completedOutbound(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/outbound/take-off")
    public ResponseEntity<Void> takeOffOutbound(@RequestBody SimulatorIdsDto request) {
        log.info("out-of-rack 요청 수신: {} 건", request.getIds().size());
        transportOrderFacade.takeOffOutbound(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/outbound/bin-empty")
    public ResponseEntity<Void> binEmptyOutbound(@RequestBody SimulatorIdsDto request) {
        log.info("bin-empty 요청 수신: {} 건", request.getIds().size());
        transportOrderFacade.binEmptyOutbound(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/outbound/shortage")
    public ResponseEntity<Void> shortageOutbound(@RequestBody SimulatorIdsDto request) {
        log.info("bin-empty 요청 수신: {} 건", request.getIds().size());
        transportOrderFacade.shortageOutbound(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/outbound/not-allowed-pick-up")
    public ResponseEntity<Void> notAllowedPickUpOutbound(@RequestBody SimulatorIdsDto request) {
        log.info("not-allowed-pick-up 요청 수신: {} 건", request.getIds().size());
        transportOrderFacade.notAllowedPickUpOutbound(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/outbound/arrived-at-rack")
    public ResponseEntity<Void> arrivedAtRackOutbound(@RequestBody SimulatorIdsDto request) {
        log.info("arrived-at-rack 요청 수신: {} 건", request.getIds().size());
        transportOrderFacade.arrivedAtRackOutbound(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/inbound/idocs")
    public ResponseEntity<Page<IdocResponseDto>> getInboundIdocList(Pageable pageable) {
        // 3. 서비스 계층에 작업 위임
        Page<IdocEntity> idocEntities = externalInterfaceService.selectIdocListByOrderType(pageable,"I");

        return ResponseEntity.ok(idocEntities.map(IdocResponseDto::from));
    }

    @PostMapping("/inbound/transfer/{idocId}")
    public ResponseEntity<TransportOrderResponseDto> transferInbound(@PathVariable Long idocId) {
        log.info("인터페이스 수동 실행 요청 - idocId: {}", idocId);

        // 1. 통합 서비스 호출 (DB2 조회 -> MSSQL 저장 -> DB2 상태 변경)
        // 반환값은 MSSQL에 저장된 최종 객체의 DTO입니다.
        TransportOrderEntity entity = transportOrderFacade.transferInbound(idocId);
        TransportStatus status = TransportStatus.valueOf(entity.getTransportStatus());
        String transportStatus = status.getFullStatus();

        TransportOrderResponseDto result = TransportOrderResponseDto.from(entity);
        result.setTransportStatus(transportStatus);

        return ResponseEntity.ok(result);
    }

    // SimulatorController.java 에 추가
    @PostMapping("/inbound/accept")
    public ResponseEntity<Void> acceptInbound(@RequestBody SimulatorIdsDto request) {
        log.info("Accept 요청 수신: {} 건", request.getIds().size());
        transportOrderFacade.acceptInbound(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/inbound/workstation-empty")
    public ResponseEntity<Void> workstationEmptyInbound(@RequestBody SimulatorIdsDto request) {
        log.info("workstationEmpty 요청 수신: {} 건", request.getIds().size());
        transportOrderFacade.workStationEmptyInbound(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/inbound/arrived-workstation-error")
    public ResponseEntity<Void> arrivalWorkstationErrorInbound(@RequestBody SimulatorIdsDto request) {
        log.info("arrivalWorkstationErrorInbound 요청 수신: {} 건", request.getIds().size());
        transportOrderFacade.arrivedWorkstationErrorInbound(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/inbound/error-text")
    public ResponseEntity<Void> errorTextInbound(@RequestBody SimulatorIdsDto request) {
        log.info("errorTextInbound 요청 수신: {} 건", request.getIds().size());
        transportOrderFacade.errorTextInbound(request.getIds().get(0), request.getErrorText());
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/inbound/scanned-carrier")
    public ResponseEntity<Void> carrierScannedInbound(@RequestBody SimulatorIdsDto request) {
        log.info("carrierScannedInbound 요청 수신: {} 건", request.getIds().size());
        transportOrderFacade.carrierScannedInbound(request);
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/inbound/out-of-rack")
    public ResponseEntity<Void> outOfRackInbound(@RequestBody SimulatorIdsDto request) {
        log.info("out-of-rack 요청 수신: {} 건", request.getIds().size());
        transportOrderFacade.outOfRackInbound(request);
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/inbound/not-allowed-pick-up")
    public ResponseEntity<Void> notAllowedPickUpInbound(@RequestBody SimulatorIdsDto request) {
        log.info("not-allowed-pick-up 요청 수신: {} 건", request.getIds().size());
        transportOrderFacade.notAllowedPickUpInbound(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/inbound/arrived-at-rack")
    public ResponseEntity<Void> arrivedAtRackInbound(@RequestBody SimulatorIdsDto request) {
        log.info("arrived-at-rack 요청 수신: {} 건", request.getIds().size());
        transportOrderFacade.arrivedAtRackOInbound(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/inbound/station-occupied")
    public ResponseEntity<Void> stationOccupiedInbound(@RequestBody StationOccupiedDto request) {
        log.info("stationOccupied 요청 ");
        transportOrderFacade.stationOccupiedInbound(request);
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/inbound/completed")
    public ResponseEntity<Void> completedInbound(@RequestBody SimulatorIdsDto request) {
        log.info("out-of-rack 요청 수신: {} 건", request.getIds().size());
        transportOrderFacade.completedInbound(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/relocation/idocs")
    public ResponseEntity<Page<IdocResponseDto>> getRelocationIdocList(Pageable pageable) {
        // 3. 서비스 계층에 작업 위임
        Page<IdocEntity> idocEntities = externalInterfaceService.selectIdocListByOrderType(pageable,"R");

        return ResponseEntity.ok(idocEntities.map(IdocResponseDto::from));
    }

    @PostMapping("/relocation/transfer/{idocId}")
    public ResponseEntity<TransportOrderResponseDto> transferRelocation(@PathVariable Long idocId) {
        log.info("인터페이스 수동 실행 요청 - idocId: {}", idocId);

        // 1. 통합 서비스 호출 (DB2 조회 -> MSSQL 저장 -> DB2 상태 변경)
        // 반환값은 MSSQL에 저장된 최종 객체의 DTO입니다.
        TransportOrderEntity entity = transportOrderFacade.transferRelocation(idocId);
        TransportStatus status = TransportStatus.valueOf(entity.getTransportStatus());
        String transportStatus = status.getFullStatus();

        TransportOrderResponseDto result = TransportOrderResponseDto.from(entity);
        result.setTransportStatus(transportStatus);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/relocation/order-detail/{idocId}")
    public ResponseEntity<H2OrderDetailResponseDto> getH2OrderDetailListForRelocation(@PathVariable("idocId") Long idocId) {
        // 3. 서비스 계층에 작업 위임
        H2OrderDetailRelocationVo vo = externalInterfaceService.selectH2OrderDetailByIdocIdForRelocation(idocId);

        return ResponseEntity.ok(H2OrderDetailResponseDto.form(vo));
    }

    // SimulatorController.java 에 추가
    @PostMapping("/relocation/accept")
    public ResponseEntity<Void> acceptRelocation(@RequestBody SimulatorIdsDto request) {
        log.info("Accept 요청 수신: {} 건", request.getIds().size());
        transportOrderFacade.acceptRelocation(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/relocation/internal-relocation")
    public ResponseEntity<Void> internalRelocationRelocation(@RequestBody SimulatorIdsDto request) {
        log.info("internal-relocation 요청 수신: {} 건", request.getIds().size());
        transportOrderFacade.internalRelocationRelocation(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/relocation/drop-on-tunnel")
    public ResponseEntity<Void> dropOnTunnelRelocation(@RequestBody SimulatorIdsDto request) {
        log.info("internal-dropOnTunnelRelocation 요청 수신: {} 건", request.getIds().size());
        transportOrderFacade.dropOnTunnelRelocation(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/relocation/completed")
    public ResponseEntity<Void> completedRelocation(@RequestBody SimulatorIdsDto request) {
        log.info("out-of-rack 요청 수신: {} 건", request.getIds().size());
        transportOrderFacade.completedRelocation(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    // SimulatorController.java 에 추가
    @PostMapping("/relocation/arrived-at-rack")
    public ResponseEntity<Void> arrivedAtRackRelocation(@RequestBody SimulatorIdsDto request) {
        log.info("arrived-at-rack 요청 수신: {} 건", request.getIds().size());
        transportOrderFacade.arrivedAtRackRelocation(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

}
