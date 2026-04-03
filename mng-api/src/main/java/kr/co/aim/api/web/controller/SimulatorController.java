package kr.co.aim.api.web.controller;

import kr.co.aim.api.dto.*;
import kr.co.aim.api.dto.insert.*;
import kr.co.aim.api.service.InsertSimulatorInterfaceService;
import kr.co.aim.api.service.InsertSimulatorFacade;
import kr.co.aim.api.service.TransportOrderService;
import kr.co.aim.api.vo.insert.sim.H2OrderDetailRelocationVo;
import kr.co.aim.api.vo.insert.sim.H2OrderDetailVo;
import kr.co.aim.api.vo.insert.sim.StationOccupiedVo;
import kr.co.aim.common.enums.GALTransportStatus;
import kr.co.aim.domain.model.TransportOrder;
import kr.co.aim.infra.persistence.db2entity.insert.H2OrderDEntity;
import kr.co.aim.infra.persistence.db2entity.insert.H2OrderMEntity;
import kr.co.aim.infra.persistence.db2entity.insert.H2TransEntity;
import kr.co.aim.infra.persistence.db2entity.insert.IdocEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/simulator")
@RequiredArgsConstructor
@Slf4j
@Profile({"simulator"})
public class SimulatorController {

    private final TransportOrderService transportOrderService;
    private final InsertSimulatorFacade insertSimulatorFacade;
    private final InsertSimulatorInterfaceService insertSimulatorInterfaceService;

    @GetMapping("/h2orderm")
    public ResponseEntity<Page<H2OrderMResponseDto>> getH2OrderMList(@RequestParam Long idocId, Pageable pageable) {
        Page<H2OrderMEntity> h2orderMPageEntity = insertSimulatorInterfaceService.selectH2OrderMByIdocId(idocId,pageable);

        return ResponseEntity.ok(h2orderMPageEntity.map(H2OrderMResponseDto::from));
    }

    @GetMapping("/order-detail/{idocId}")
    public ResponseEntity<H2OrderDetailResponseDto> getH2OrderDetailList(@PathVariable("idocId") Long idocId) {
        H2OrderDetailVo vo = insertSimulatorInterfaceService.selectH2OrderDetailByIdocId(idocId);

        return ResponseEntity.ok(H2OrderDetailResponseDto.from(vo));
    }

    @GetMapping("/h2orderd")
    public ResponseEntity<Page<H2OrderDResponseDto>> getH2OrderDList(@RequestParam Long idocId, Pageable pageable) {
        Page<H2OrderDEntity> h2OrderDEntities = insertSimulatorInterfaceService.selectH2OrderDByIdocId(idocId,pageable);

        return ResponseEntity.ok(h2OrderDEntities.map(H2OrderDResponseDto::from));
    }

    @GetMapping("/h2trans")
    public ResponseEntity<Page<H2TransResponseDto>> getH2TransList(@RequestParam Long orderId, Pageable pageable) {
        Page<H2TransEntity> h2TransPage = insertSimulatorInterfaceService.selectH2TransByOrderId(orderId,pageable);

        return ResponseEntity.ok(h2TransPage.map(H2TransResponseDto::from));
    }

    @GetMapping("/transport-order/{orderId}")
    public ResponseEntity<TransportOrderResponseDto> getTransportOrderList(@PathVariable("orderId") Long orderId) {
        Optional<TransportOrder> optionalTransportOrder = transportOrderService.findByTransportOrderId(orderId.toString());
        if(optionalTransportOrder.isEmpty()){
            throw new RuntimeException("TransportOrder를 찾을 수 없습니다. (요청 ID: " + orderId + ")");
        }
        TransportOrder transportOrder = optionalTransportOrder.get();

        GALTransportStatus status = GALTransportStatus.valueOf(transportOrder.getTransportStatus());
        String transportStatus = status.getFullStatus();

        TransportOrderResponseDto result = TransportOrderResponseDto.from(transportOrder);
        result.setTransportStatus(transportStatus);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/outbound/idocs")
    public ResponseEntity<Page<IdocResponseDto>> getOutboundIdocList(Pageable pageable) {
        Page<IdocEntity> idocEntities = insertSimulatorInterfaceService.selectIdocListByOrderType(pageable,"O");

        return ResponseEntity.ok(idocEntities.map(IdocResponseDto::from));
    }

    @PostMapping("/outbound/transfer/{idocId}")
    public ResponseEntity<TransportOrderResponseDto> transferOutbound(@PathVariable Long idocId) {
        TransportOrder transportOrder = insertSimulatorFacade.transferOutbound(idocId);
        GALTransportStatus status = GALTransportStatus.valueOf(transportOrder.getTransportStatus());
        String transportStatus = status.getFullStatus();

        TransportOrderResponseDto result = TransportOrderResponseDto.from(transportOrder);
        result.setTransportStatus(transportStatus);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/outbound/accept")
    public ResponseEntity<Void> acceptOutbound(@RequestBody SimulatorIdsDto request) {
        insertSimulatorFacade.acceptOutbound(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/outbound/release")
    public ResponseEntity<Void> releaseOutbound(@RequestBody SimulatorIdsDto request) {
        insertSimulatorFacade.releaseOutbound(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/outbound/internal-relocation")
    public ResponseEntity<Void> internalRelocationOutbound(@RequestBody SimulatorIdsDto request) {
        insertSimulatorFacade.internalRelocationOutbound(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/outbound/out-of-rack")
    public ResponseEntity<Void> outOfRackOutbound(@RequestBody SimulatorIdsDto request) {
        insertSimulatorFacade.outOfRackOutbound(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/outbound/arrived-at-workstation")
    public ResponseEntity<Void> arrivedAtWorkstationOutbound(@RequestBody SimulatorIdsDto request) {
        insertSimulatorFacade.arrivedAtWorkstationOutbound(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/outbound/completed")
    public ResponseEntity<Void> completedOutbound(@RequestBody SimulatorIdsDto request) {
        insertSimulatorFacade.completedOutbound(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/outbound/take-off")
    public ResponseEntity<Void> takeOffOutbound(@RequestBody SimulatorIdsDto request) {
        insertSimulatorFacade.takeOffOutbound(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/outbound/bin-empty")
    public ResponseEntity<Void> binEmptyOutbound(@RequestBody SimulatorIdsDto request) {
        insertSimulatorFacade.binEmptyOutbound(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/outbound/shortage")
    public ResponseEntity<Void> shortageOutbound(@RequestBody SimulatorIdsDto request) {
        insertSimulatorFacade.shortageOutbound(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/outbound/not-allowed-pick-up")
    public ResponseEntity<Void> notAllowedPickUpOutbound(@RequestBody SimulatorIdsDto request) {
        insertSimulatorFacade.notAllowedPickUpOutbound(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/outbound/arrived-at-rack")
    public ResponseEntity<Void> arrivedAtRackOutbound(@RequestBody SimulatorIdsDto request) {
        insertSimulatorFacade.arrivedAtRackOutbound(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/inbound/idocs")
    public ResponseEntity<Page<IdocResponseDto>> getInboundIdocList(Pageable pageable) {
        Page<IdocEntity> idocEntities = insertSimulatorInterfaceService.selectIdocListByOrderType(pageable,"I");

        return ResponseEntity.ok(idocEntities.map(IdocResponseDto::from));
    }

    @PostMapping("/inbound/transfer/{idocId}")
    public ResponseEntity<TransportOrderResponseDto> transferInbound(@PathVariable Long idocId) {
        TransportOrder transportOrder = insertSimulatorFacade.transferInbound(idocId);
        GALTransportStatus status = GALTransportStatus.valueOf(transportOrder.getTransportStatus());
        String transportStatus = status.getFullStatus();

        TransportOrderResponseDto result = TransportOrderResponseDto.from(transportOrder);
        result.setTransportStatus(transportStatus);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/inbound/accept")
    public ResponseEntity<Void> acceptInbound(@RequestBody SimulatorIdsDto request) {
        insertSimulatorFacade.acceptInbound(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/inbound/workstation-empty")
    public ResponseEntity<Void> workstationEmptyInbound(@RequestBody SimulatorIdsDto request) {
        insertSimulatorFacade.workStationEmptyInbound(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/inbound/arrived-workstation-error")
    public ResponseEntity<Void> arrivalWorkstationErrorInbound(@RequestBody SimulatorIdsDto request) {
        insertSimulatorFacade.arrivedWorkstationErrorInbound(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/inbound/error-text")
    public ResponseEntity<Void> errorTextInbound(@RequestBody SimulatorIdsDto request) {
        insertSimulatorFacade.errorTextInbound(request.getIds().get(0), request.getErrorText());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/inbound/scanned-carrier")
    public ResponseEntity<Void> carrierScannedInbound(@RequestBody SimulatorIdsDto request) {
        insertSimulatorFacade.carrierScannedInbound(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/inbound/out-of-rack")
    public ResponseEntity<Void> outOfRackInbound(@RequestBody SimulatorIdsDto request) {
        insertSimulatorFacade.outOfRackInbound(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/inbound/not-allowed-pick-up")
    public ResponseEntity<Void> notAllowedPickUpInbound(@RequestBody SimulatorIdsDto request) {
        insertSimulatorFacade.notAllowedPickUpInbound(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/inbound/arrived-at-rack")
    public ResponseEntity<Void> arrivedAtRackInbound(@RequestBody SimulatorIdsDto request) {
        insertSimulatorFacade.arrivedAtRackOInbound(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/inbound/station-occupied")
    public ResponseEntity<Void> stationOccupiedInbound(@RequestBody StationOccupiedDto request) {
        StationOccupiedVo vo =
                StationOccupiedVo
                        .builder()
                        .workCenterId(request.getWorkCenterId())
                        .carrierName(request.getContainerId())
                        .locationId(request.getLocationId())
                        .build();
        insertSimulatorFacade.stationOccupiedInbound(vo);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/inbound/completed")
    public ResponseEntity<Void> completedInbound(@RequestBody SimulatorIdsDto request) {
        insertSimulatorFacade.completedInbound(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/relocation/idocs")
    public ResponseEntity<Page<IdocResponseDto>> getRelocationIdocList(Pageable pageable) {
        Page<IdocEntity> idocEntities = insertSimulatorInterfaceService.selectIdocListByOrderType(pageable,"R");

        return ResponseEntity.ok(idocEntities.map(IdocResponseDto::from));
    }

    @PostMapping("/relocation/transfer/{idocId}")
    public ResponseEntity<TransportOrderResponseDto> transferRelocation(@PathVariable Long idocId) {

        TransportOrder transportOrder = insertSimulatorFacade.transferRelocation(idocId);
        GALTransportStatus status = GALTransportStatus.valueOf(transportOrder.getTransportStatus());
        String transportStatus = status.getFullStatus();

        TransportOrderResponseDto result = TransportOrderResponseDto.from(transportOrder);
        result.setTransportStatus(transportStatus);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/relocation/order-detail/{idocId}")
    public ResponseEntity<H2OrderDetailResponseDto> getH2OrderDetailListForRelocation(@PathVariable("idocId") Long idocId) {
        H2OrderDetailRelocationVo vo = insertSimulatorInterfaceService.selectH2OrderDetailByIdocIdForRelocation(idocId);

        return ResponseEntity.ok(H2OrderDetailResponseDto.form(vo));
    }

    @PostMapping("/relocation/accept")
    public ResponseEntity<Void> acceptRelocation(@RequestBody SimulatorIdsDto request) {
        insertSimulatorFacade.acceptRelocation(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/relocation/internal-relocation")
    public ResponseEntity<Void> internalRelocationRelocation(@RequestBody SimulatorIdsDto request) {
        log.info("internal-relocation 요청 수신: {} 건", request.getIds().size());
        insertSimulatorFacade.internalRelocationRelocation(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/relocation/drop-on-tunnel")
    public ResponseEntity<Void> dropOnTunnelRelocation(@RequestBody SimulatorIdsDto request) {
        insertSimulatorFacade.dropOnTunnelRelocation(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/relocation/completed")
    public ResponseEntity<Void> completedRelocation(@RequestBody SimulatorIdsDto request) {
        insertSimulatorFacade.completedRelocation(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/relocation/arrived-at-rack")
    public ResponseEntity<Void> arrivedAtRackRelocation(@RequestBody SimulatorIdsDto request) {
        insertSimulatorFacade.arrivedAtRackRelocation(request.getIds().get(0));
        return ResponseEntity.noContent().build();
    }

}
