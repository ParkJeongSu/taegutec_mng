package kr.co.aim.api.service;

import kr.co.aim.api.dto.SimulatorIdsDto;
import kr.co.aim.api.dto.insert.StationOccupiedDto;
import kr.co.aim.api.vo.insert.StationOccupiedVo;
import kr.co.aim.api.vo.insert.TransportOrderContext;
import kr.co.aim.api.vo.insert.TransportStatusReportVo;
import kr.co.aim.common.enums.GALTransportStatus;
import kr.co.aim.infra.persistence.db2entity.insert.H2OrderDEntity;
import kr.co.aim.infra.persistence.db2entity.insert.H2OrderMEntity;
import kr.co.aim.infra.persistence.db2entity.insert.IdocEntity;
import kr.co.aim.infra.persistence.db2springdatajpa.insert.H2OrderDJpaRepository;
import kr.co.aim.infra.persistence.db2springdatajpa.insert.H2OrderMJpaRepository;
import kr.co.aim.infra.persistence.db2springdatajpa.insert.IdocJpaRepository;
import kr.co.aim.infra.persistence.entity.TransportOrderEntity;
import kr.co.aim.infra.persistence.springdatajpa.TransportOrderJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Profile({"scheduler", "simulator"})
@RequiredArgsConstructor
public class InsertTransportOrderFacade {

    private final TransportOrderService transportOrderService;
    private final InsertExternalInterfaceService insertExternalInterfaceService;
    private final IdocJpaRepository idocJpaRepository;
    private final H2OrderMJpaRepository h2OrderMJpaRepository;
    private final H2OrderDJpaRepository h2OrderDJpaRepository;
    private final TransportOrderJpaRepository transportOrderJpaRepository;

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
        TransportOrderEntity transportOrder = null;
        Optional<TransportOrderEntity> optionalTransportOrderEntity = transportOrderJpaRepository.findByTransportOrderId(orderId.toString());
        if(optionalTransportOrderEntity.isEmpty()){
            throw new RuntimeException("TransportOrder를 찾을 수 없습니다. (요청 ID: " + orderId + ")");
        }
        transportOrder = optionalTransportOrderEntity.get();

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

    public TransportOrderEntity transferOutbound(Long idocId) {
        TransportOrderContext ctx = prepareByIdocId(idocId, 1);
        TransportOrderEntity result = transportOrderService.registerTransportOrder(ctx);
        insertExternalInterfaceService.transferedIdocId(ctx.getIdoc().getLineId());
        return result;
    }

    public TransportOrderEntity transferInbound(Long idocId) {
        TransportOrderContext ctx = prepareByIdocId(idocId, 1);
        TransportOrderEntity result = transportOrderService.registerTransportOrder(ctx);
        insertExternalInterfaceService.transferedIdocId(ctx.getIdoc().getLineId());
        return result;
    }

    public TransportOrderEntity transferRelocation(Long idocId) {
        TransportOrderContext ctx = prepareByIdocId(idocId, 2);

        // Relocation 특유의 Source/Target 정렬 로직
        H2OrderDEntity source = ctx.getDetails().get(0);
        H2OrderDEntity target = ctx.getDetails().get(1);
        if (source.getLineId() > target.getLineId()) {
            source = ctx.getDetails().get(1);
            target = ctx.getDetails().get(0);
        }

        TransportOrderEntity result = transportOrderService.registerTransportOrder(ctx);
        insertExternalInterfaceService.transferedIdocId(ctx.getIdoc().getLineId());
        return result;
    }

    public void acceptOutbound(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, GALTransportStatus.Create);
        
        insertExternalInterfaceService.acceptOutbound(ctx);
        TransportStatusReportVo vo = TransportStatusReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.Accept)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void acceptInbound(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, GALTransportStatus.Create);
        insertExternalInterfaceService.acceptInbound(ctx);
        TransportStatusReportVo vo = TransportStatusReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.Accept)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void acceptRelocation(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 2, GALTransportStatus.Create);
        insertExternalInterfaceService.acceptInbound(ctx);
        TransportStatusReportVo vo = TransportStatusReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.Accept)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void workStationEmptyInbound(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, GALTransportStatus.Accept);
        insertExternalInterfaceService.workStationEmptyInbound(ctx);
        TransportStatusReportVo vo = TransportStatusReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.WorkstationEmpty)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void arrivedWorkstationErrorInbound(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, GALTransportStatus.WorkstationEmpty);
        insertExternalInterfaceService.arrivedWorkstationErrorInbound(ctx);
        TransportStatusReportVo vo = TransportStatusReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.ArrivedAtWorkstationWithError)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void errorTextInbound(Long orderId, String errorText) {
        // 에러 텍스트는 두 가지 상태에서 올 수 있으므로 null 전달 후 수동 체크
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, null);
        insertExternalInterfaceService.errorTextInbound(errorText, ctx);
        TransportStatusReportVo vo = TransportStatusReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.ErrorText)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void carrierScannedInbound(SimulatorIdsDto dto) {
        TransportOrderContext ctx = prepareByOrderId(dto.getIds().get(0), 1, null);
        insertExternalInterfaceService.carrierScannedInbound(ctx, dto);
        TransportStatusReportVo vo = TransportStatusReportVo
                .builder()
                .orderId(dto.getIds().get(0).toString())
                .status(GALTransportStatus.CarrierScanned)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void releaseOutbound(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, GALTransportStatus.Accept);
        insertExternalInterfaceService.releaseOutbound(ctx);
        TransportStatusReportVo vo = TransportStatusReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.Released)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void outOfRackOutbound(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, null);
        insertExternalInterfaceService.outOfRackOutbound(ctx);
        TransportStatusReportVo vo = TransportStatusReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.OutOfRack)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void outOfRackInbound(SimulatorIdsDto dto) {
        TransportOrderContext ctx = prepareByOrderId(dto.getIds().get(0), 1, GALTransportStatus.CarrierScanned);
        insertExternalInterfaceService.outOfRackInbound(ctx, dto);
        TransportStatusReportVo vo = TransportStatusReportVo
                .builder()
                .orderId(dto.getIds().get(0).toString())
                .status(GALTransportStatus.OutOfRack)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void arrivedAtWorkstationOutbound(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, GALTransportStatus.OutOfRack);
        insertExternalInterfaceService.arrivedAtWorkStationOutbound(ctx);
        TransportStatusReportVo vo = TransportStatusReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.ArrivedAtWorkStation)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void completedOutbound(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, null);
        insertExternalInterfaceService.completedOutbound(ctx);
        TransportStatusReportVo vo = TransportStatusReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.OrderDone_Outbound)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void completedInbound(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, null);
        insertExternalInterfaceService.completedInbound(ctx);
        TransportStatusReportVo vo = TransportStatusReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.OrderDone_Inbound)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void stationOccupiedInbound(StationOccupiedVo vo) {
        // 이 메서드는 조회 과정이 없으므로 유지
        insertExternalInterfaceService.stationOccupiedInbound(vo);
    }

    public void internalRelocationOutbound(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, GALTransportStatus.Released);
        insertExternalInterfaceService.internalRelocationOutbound(ctx);
        TransportStatusReportVo vo = TransportStatusReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.InternalRelocation)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void takeOffOutbound(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, GALTransportStatus.OrderDone_Outbound);
        insertExternalInterfaceService.takeOffOutbound(ctx);
        TransportStatusReportVo vo = TransportStatusReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.TakeOff)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void binEmptyOutbound(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, GALTransportStatus.Released);
        insertExternalInterfaceService.binEmptyOutbound(ctx);
        TransportStatusReportVo vo = TransportStatusReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.BinEmpty)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void shortageOutbound(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, GALTransportStatus.BinEmpty);
        insertExternalInterfaceService.shortageOutbound(ctx);
        TransportStatusReportVo vo = TransportStatusReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.Shortage)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void notAllowedPickUpOutbound(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, GALTransportStatus.Released);
        insertExternalInterfaceService.notAllowedPickUpOutbound(ctx);
        TransportStatusReportVo vo = TransportStatusReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.NotAllowedPickUp)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void arrivedAtRackOutbound(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, GALTransportStatus.NotAllowedPickUp);
        insertExternalInterfaceService.arrivedAtRackOutbound(ctx);
        TransportStatusReportVo vo = TransportStatusReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.ArrivedAtRack)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void notAllowedPickUpInbound(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, null); // Inbound는 흐름에 따라 유동적일 수 있어 null 처리
        insertExternalInterfaceService.notAllowedPickUpInbound(ctx);
        TransportStatusReportVo vo = TransportStatusReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.NotAllowedPickUp)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void arrivedAtRackOInbound(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 1, null);
        insertExternalInterfaceService.arrivedAtRackOutbound(ctx);
        TransportStatusReportVo vo = TransportStatusReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.ArrivedAtRack)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    // --- Relocation(이송) 관련: 상세데이터(Detail)가 2개인 그룹 ---

    public void internalRelocationRelocation(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 2, GALTransportStatus.Accept);
        insertExternalInterfaceService.internalRelocationOutbound(ctx);
        TransportStatusReportVo vo = TransportStatusReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.InternalRelocation)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void dropOnTunnelRelocation(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 2, GALTransportStatus.Accept);
        insertExternalInterfaceService.dropOnTunnelRelocation(ctx);
        TransportStatusReportVo vo = TransportStatusReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.DroppedOnTunnelConveyor)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void arrivedAtRackRelocation(Long orderId) {
        TransportOrderContext ctx = prepareByOrderId(orderId, 2, GALTransportStatus.DroppedOnTunnelConveyor);
        insertExternalInterfaceService.arrivedAtRackOutbound(ctx);
        TransportStatusReportVo vo = TransportStatusReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.ArrivedAtRack)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }

    public void completedRelocation(Long orderId) {
        // Relocation 완료 시에도 마스터 정보를 기준으로 보고하므로 상세 1개 혹은 2개 상황에 맞춰 조회
        TransportOrderContext ctx = prepareByOrderId(orderId, 2, null);
        insertExternalInterfaceService.completedRelocation(ctx);
        TransportStatusReportVo vo = TransportStatusReportVo
                .builder()
                .orderId(orderId.toString())
                .status(GALTransportStatus.OrderDone_Relocation)
                .build();
        transportOrderService.updateStatusTransportOrder(vo);
    }
}