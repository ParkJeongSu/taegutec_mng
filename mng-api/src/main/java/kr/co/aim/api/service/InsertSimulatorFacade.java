package kr.co.aim.api.service;

import kr.co.aim.api.dto.SimulatorIdsDto;
import kr.co.aim.api.vo.insert.sim.StationOccupiedVo;
import kr.co.aim.api.vo.insert.sim.TransportOrderContext;
import kr.co.aim.api.vo.insert.sim.H2TransReportVo;
import kr.co.aim.common.enums.GALTransportStatus;
import kr.co.aim.domain.model.TransportOrder;
import kr.co.aim.domain.repository.TransportOrderRepository;
import kr.co.aim.infra.persistence.db2entity.insert.H2OrderDEntity;
import kr.co.aim.infra.persistence.db2entity.insert.H2OrderMEntity;
import kr.co.aim.infra.persistence.db2entity.insert.IdocEntity;
import kr.co.aim.infra.persistence.db2springdatajpa.insert.H2OrderDJpaRepository;
import kr.co.aim.infra.persistence.db2springdatajpa.insert.H2OrderMJpaRepository;
import kr.co.aim.infra.persistence.db2springdatajpa.insert.IdocJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Profile({"simulator"})
@RequiredArgsConstructor
@ConditionalOnProperty(name = "factory.type", havingValue = "insert")
public class InsertSimulatorFacade {

    private final TransportOrderService transportOrderService;
    private final InsertSimulatorInterfaceService insertSimulatorInterfaceService;
    private final IdocJpaRepository idocJpaRepository;
    private final H2OrderMJpaRepository h2OrderMJpaRepository;
    private final H2OrderDJpaRepository h2OrderDJpaRepository;
    private final TransportOrderRepository transportOrderRepository;

    // --- [Private Helper Methods] 조회 및 검증 공통화 ---

    // 1. IDOC ID 기준으로 전체 컨텍스트 준비 (최초 주문 생성용)
    private TransportOrderContext prepareByIdocId(Long idocId, int expectedDetailSize) {
        Optional<IdocEntity> optionalIdoc = idocJpaRepository.findByLineId(idocId);
        if (optionalIdoc.isEmpty()) throw new RuntimeException("IDOC을 찾을 수 없습니다. ID:" + idocId);

        List<H2OrderMEntity> mList = h2OrderMJpaRepository.findByIdocId(idocId);
        List<H2OrderDEntity> dList = h2OrderDJpaRepository.findByIdocId(idocId);

        validateDataSize(mList, dList, expectedDetailSize);

        return TransportOrderContext.builder()
                .idoc(optionalIdoc.get())
                .master(mList.get(0))
                .details(dList)
                .build();
    }

    // 2. Order ID 기준으로 전체 컨텍스트 준비 (상태 보고용)
    private TransportOrderContext prepareByOrderId(Long orderId, int expectedDetailSize, GALTransportStatus requiredStatus) {
        Optional<TransportOrder> optionalTransportOrder = transportOrderRepository.findByTransportOrderId(orderId.toString());
        if(optionalTransportOrder.isEmpty()){
            throw new RuntimeException("TransportOrder를 찾을 수 없습니다. (요청 ID: " + orderId + ")");
        }
        TransportOrder transportOrder = optionalTransportOrder.get();

        // 상태 검증 (필요한 경우)
        if (requiredStatus != null && !transportOrder.getTransportStatus().equals(requiredStatus.name())) {
            throw new RuntimeException("잘못된 상태입니다. Expected: " + requiredStatus.name() + ", Actual: " + transportOrder.getTransportStatus());
        }

        String orderNo = transportOrder.getTransportOrderId();
        List<H2OrderMEntity> mList = h2OrderMJpaRepository.findByCOrderId(orderNo);
        List<H2OrderDEntity> dList = h2OrderDJpaRepository.findByCOrderId(orderNo);

        validateDataSize(mList, dList, expectedDetailSize);

        IdocEntity idoc = idocJpaRepository.findByLineId(mList.get(0).getIdocId())
                .orElseThrow(() -> new RuntimeException("IDOC을 찾을 수 없습니다."));

        return TransportOrderContext.builder()
                .transportOrder(transportOrder)
                .idoc(idoc)
                .master(mList.get(0))
                .details(dList)
                .build();
    }

    private void validateDataSize(List<H2OrderMEntity> m, List<H2OrderDEntity> d, int dSize) {
        if (m.size() != 1 || d.size() != dSize) {
            throw new RuntimeException("데이터 기입 오류: M_SIZE=" + m.size() + ", D_SIZE=" + d.size());
        }
    }

    // --- [Public API] 리팩토링된 메서드들 ---

    public TransportOrder transferOutbound(Long idocId) {
        TransportOrderContext ctx = prepareByIdocId(idocId, 1);
        TransportOrder result = transportOrderService.registerTransportOrder(ctx);
        insertSimulatorInterfaceService.transferedIdocId(ctx.getIdoc().getLineId());
        return result;
    }

    public TransportOrder transferInbound(Long idocId) {
        TransportOrderContext ctx = prepareByIdocId(idocId, 1);
        TransportOrder result = transportOrderService.registerTransportOrder(ctx);
        insertSimulatorInterfaceService.transferedIdocId(ctx.getIdoc().getLineId());
        return result;
    }

    public TransportOrder transferRelocation(Long idocId) {
        TransportOrderContext ctx = prepareByIdocId(idocId, 2);

        // Relocation 특유의 Source/Target 정렬 로직
        H2OrderDEntity source = ctx.getDetails().get(0);
        H2OrderDEntity target = ctx.getDetails().get(1);
        if (source.getLineId() > target.getLineId()) {
            source = ctx.getDetails().get(1);
            target = ctx.getDetails().get(0);
        }

        TransportOrder result = transportOrderService.registerTransportOrder(ctx);
        insertSimulatorInterfaceService.transferedIdocId(ctx.getIdoc().getLineId());
        return result;
    }

    public void acceptOutbound(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, GALTransportStatus.CREATED);
        
        insertSimulatorInterfaceService.acceptOutbound(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.ACCEPT)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void acceptInbound(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, GALTransportStatus.CREATED);
        insertSimulatorInterfaceService.acceptInbound(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.ACCEPT)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void acceptRelocation(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 2, GALTransportStatus.CREATED);
        insertSimulatorInterfaceService.acceptInbound(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.ACCEPT)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void workStationEmptyInbound(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, GALTransportStatus.ACCEPT);
        insertSimulatorInterfaceService.workStationEmptyInbound(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.WORKSTATION_EMPTY)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void arrivedWorkstationErrorInbound(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, GALTransportStatus.WORKSTATION_EMPTY);
        insertSimulatorInterfaceService.arrivedWorkstationErrorInbound(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.ARRIVED_AT_WORKSTATION_WITH_ERROR)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void errorTextInbound(Long orderId, String errorText) {
        // 에러 텍스트는 두 가지 상태에서 올 수 있으므로 null 전달 후 수동 체크
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, null);
        insertSimulatorInterfaceService.errorTextInbound(errorText, ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.ERROR_TEXT)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void carrierScannedInbound(SimulatorIdsDto dto) {
        TransportOrderContext ctx = prepareByOrderId(dto.getIds().get(0), 1, null);
        insertSimulatorInterfaceService.carrierScannedInbound(ctx, dto);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .orderId(dto.getIds().get(0).toString())
                .status(GALTransportStatus.CARRIER_SCANNED)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void releaseOutbound(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, GALTransportStatus.ACCEPT);
        insertSimulatorInterfaceService.releaseOutbound(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.RELEASED)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void outOfRackOutbound(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, null);
        insertSimulatorInterfaceService.outOfRackOutbound(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.OUT_OF_RACK)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void outOfRackInbound(SimulatorIdsDto dto) {
        TransportOrderContext ctx = prepareByOrderId(dto.getIds().get(0), 1, GALTransportStatus.CARRIER_SCANNED);
        insertSimulatorInterfaceService.outOfRackInbound(ctx, dto);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .orderId(dto.getIds().get(0).toString())
                .status(GALTransportStatus.OUT_OF_RACK)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void arrivedAtWorkstationOutbound(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, GALTransportStatus.OUT_OF_RACK);
        insertSimulatorInterfaceService.arrivedAtWorkStationOutbound(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.ARRIVED_AT_WORK_STATION)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void completedOutbound(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, null);
        insertSimulatorInterfaceService.completedOutbound(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.ORDER_DONE_OUTBOUND)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void completedInbound(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, null);
        insertSimulatorInterfaceService.completedInbound(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.ORDER_DONE_INBOUND)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void stationOccupiedInbound(StationOccupiedVo vo) {
        // 이 메서드는 조회 과정이 없으므로 유지
        insertSimulatorInterfaceService.stationOccupiedInbound(vo);
    }

    public void internalRelocationOutbound(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, GALTransportStatus.RELEASED);
        insertSimulatorInterfaceService.internalRelocationOutbound(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.INTERNAL_RELOCATION)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void takeOffOutbound(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, GALTransportStatus.ORDER_DONE_OUTBOUND);
        insertSimulatorInterfaceService.takeOffOutbound(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.TAKE_OFF)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void binEmptyOutbound(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, GALTransportStatus.RELEASED);
        insertSimulatorInterfaceService.binEmptyOutbound(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.BIN_EMPTY)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void shortageOutbound(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, GALTransportStatus.BIN_EMPTY);
        insertSimulatorInterfaceService.shortageOutbound(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.SHORTAGE_OUTBOUND)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void notAllowedPickUpOutbound(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, GALTransportStatus.RELEASED);
        insertSimulatorInterfaceService.notAllowedPickUpOutbound(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.NOT_ALLOWED_PICK_UP)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void arrivedAtRackOutbound(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, GALTransportStatus.NOT_ALLOWED_PICK_UP);
        insertSimulatorInterfaceService.arrivedAtRackOutbound(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.ARRIVED_AT_RACK)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void notAllowedPickUpInbound(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, null); // Inbound는 흐름에 따라 유동적일 수 있어 null 처리
        insertSimulatorInterfaceService.notAllowedPickUpInbound(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.NOT_ALLOWED_PICK_UP)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void arrivedAtRackOInbound(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, null);
        insertSimulatorInterfaceService.arrivedAtRackOutbound(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.ARRIVED_AT_RACK)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    // --- Relocation(이송) 관련: 상세데이터(Detail)가 2개인 그룹 ---

    public void internalRelocationRelocation(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 2, GALTransportStatus.ACCEPT);
        insertSimulatorInterfaceService.internalRelocationOutbound(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.INTERNAL_RELOCATION)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void dropOnTunnelRelocation(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 2, GALTransportStatus.ACCEPT);
        insertSimulatorInterfaceService.dropOnTunnelRelocation(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.DROPPED_ON_TUNNEL_CONVEYOR)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void arrivedAtRackRelocation(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 2, GALTransportStatus.DROPPED_ON_TUNNEL_CONVEYOR);
        insertSimulatorInterfaceService.arrivedAtRackOutbound(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.ARRIVED_AT_RACK)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void completedRelocation(Long orderId) {
        // Relocation 완료 시에도 마스터 정보를 기준으로 보고하므로 상세 1개 혹은 2개 상황에 맞춰 조회
        TransportOrderContext ctx = prepareByOrderId(orderId, 2, null);
        insertSimulatorInterfaceService.completedRelocation(ctx);
        H2TransReportVo vo = H2TransReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.ORDER_DONE_RELOCATION)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }
}