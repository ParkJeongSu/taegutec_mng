package kr.co.aim.api.service;

import kr.co.aim.api.dto.SimulatorIdsDto;
import kr.co.aim.api.vo.insert.*;
import kr.co.aim.common.enums.GALTransportStatus;
import kr.co.aim.common.enums.IdocErrorCode;
import kr.co.aim.common.enums.IdocTypeId;
import kr.co.aim.infra.persistence.db2entity.insert.H2OrderDEntity;
import kr.co.aim.infra.persistence.db2entity.insert.H2OrderMEntity;
import kr.co.aim.infra.persistence.db2entity.insert.H2TransEntity;
import kr.co.aim.infra.persistence.db2entity.insert.IdocEntity;
import kr.co.aim.infra.persistence.db2springdatajpa.insert.H2OrderDJpaRepository;
import kr.co.aim.infra.persistence.db2springdatajpa.insert.H2OrderMJpaRepository;
import kr.co.aim.infra.persistence.db2springdatajpa.insert.H2TransJpaRepository;
import kr.co.aim.infra.persistence.db2springdatajpa.insert.IdocJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile({"scheduler", "simulator"})
public class InsertExternalInterfaceService {

    private final IdocJpaRepository idocJpaRepository;
    private final H2OrderMJpaRepository h2OrderMJpaRepository;
    private final H2OrderDJpaRepository h2OrderDJpaRepository;
    private final H2TransJpaRepository h2TransJpaRepository;

    // --- [Helper Methods] ---

    private IdocEntity buildBaseIdoc(IdocEntity selectedIdoc, LocalDateTime now) {
        return IdocEntity.builder()
                .lineId(idocJpaRepository.findMaxLineId() + 1)
                .idocTypId(Long.parseLong(IdocTypeId.Confirmation.getValue()))
                .source(selectedIdoc.getDestination())
                .destination(selectedIdoc.getSource())
                .docNum(selectedIdoc.getDocNum())
                .dtimeCre(now)
                .build();
    }

    private H2TransEntity buildBaseH2Trans(H2TransEntityVo vo) {
        // TODO: MAX 값 수정
        return H2TransEntity.builder()
                .lineId(h2TransJpaRepository.findMaxLineId() + 1)
                .idocId(vo.getNewIdoc().getLineId())
                .dtimeCre(vo.getNewIdoc().getDtimeCre())
                .dataCode(10L)
                .cTransTy(Long.parseLong(vo.getStatus().getValue()))
                .cClient("999")
                .cOrderId(vo.getMaster().getCOrderId())
                .cOrderTy(vo.getMaster().getCOrderTy())
                .cGaId(vo.getMaster().getCGalId())
                .cGalWhs(vo.getMaster().getCGalWhs())
                .cCoId(vo.getContainerId() != null ? vo.getContainerId() : vo.getFirstDetail().getCCoId())
                .cGrWgAct(30L)
                .cReqZone(vo.getFirstDetail().getCZone())
                .cZone(vo.getZone())
                .cLocId(vo.getLocationCode())
                .cErrDsc(vo.getErrorText())
                .cWcId(vo.getMaster().getCWcId())
                .build();
    }

    private void saveTransportProgress(TransportStatusReportVo report) {
        log.info("Reporting Status: {}", report.getStatus());
        LocalDateTime now = LocalDateTime.now().withNano(0);

        IdocEntity newIdoc = buildBaseIdoc(report.getSourceIdoc(), now);
        idocJpaRepository.save(newIdoc);

        H2TransEntityVo h2TransVo = H2TransEntityVo.builder()
                .newIdoc(newIdoc)
                .status(report.getStatus())
                .master(report.getMaster())
                .details(report.getDetails())
                .containerId(report.getContainerId())
                .locationCode(report.getLocationCode())
                .zone(report.getZone())
                .errorText(report.getErrorText())
                .build();

        H2TransEntity newTrans = buildBaseH2Trans(h2TransVo);
        h2TransJpaRepository.save(newTrans);
    }

    // --- [Public API] Context를 사용하여 극도로 단순화된 메서드들 ---
    @Transactional(value = "db2TransactionManager")
    public void acceptOutbound(TransportOrderContext ctx) {
        TransportStatusReportVo vo =
                TransportStatusReportVo
                        .builder()
                        .status(GALTransportStatus.Accept)
                        .sourceIdoc(ctx.getIdoc())
                        .master(ctx.getMaster())
                        .details(ctx.getDetails())
                        .zone("")
                        .build();
        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void acceptInbound(TransportOrderContext ctx) {
        TransportStatusReportVo vo = TransportStatusReportVo.builder()
                .status(GALTransportStatus.Accept)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .zone("B")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void workStationEmptyInbound(TransportOrderContext ctx) {
        TransportStatusReportVo vo = TransportStatusReportVo.builder()
                .status(GALTransportStatus.WorkstationEmpty)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .zone("B")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void arrivedWorkstationErrorInbound(TransportOrderContext ctx) {
        TransportStatusReportVo vo = TransportStatusReportVo.builder()
                .status(GALTransportStatus.ArrivedAtWorkstationWithError)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .zone("")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void errorTextInbound(String errorText, TransportOrderContext ctx) {
        TransportStatusReportVo vo = TransportStatusReportVo.builder()
                .status(GALTransportStatus.ErrorText)
                .errorText(errorText)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .zone("")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void carrierScannedInbound(TransportOrderContext ctx, SimulatorIdsDto dto) {
        String containerId = StringUtils.hasText(dto.getCarrierId()) ? dto.getCarrierId() : ctx.getFirstDetail().getCCoId();

        TransportStatusReportVo vo = TransportStatusReportVo.builder()
                .status(GALTransportStatus.CarrierScanned)
                .containerId(containerId)
                .locationCode(dto.getLocationCode())
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .zone("")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void releaseOutbound(TransportOrderContext ctx) {
        TransportStatusReportVo vo = TransportStatusReportVo.builder()
                .status(GALTransportStatus.Released)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .zone("B")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void internalRelocationOutbound(TransportOrderContext ctx) {
        TransportStatusReportVo vo = TransportStatusReportVo.builder()
                .status(GALTransportStatus.InternalRelocation)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .zone("B")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void dropOnTunnelRelocation(TransportOrderContext ctx) {
        TransportStatusReportVo vo = TransportStatusReportVo.builder()
                .status(GALTransportStatus.DroppedOnTunnelConveyor)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .zone("B")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void outOfRackOutbound(TransportOrderContext ctx) {
        TransportStatusReportVo vo = TransportStatusReportVo.builder()
                .status(GALTransportStatus.OutOfRack)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .zone("")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void outOfRackInbound(TransportOrderContext ctx, SimulatorIdsDto dto) {
        TransportStatusReportVo vo = TransportStatusReportVo.builder()
                .status(GALTransportStatus.OutOfRack)
                .zone(dto.getRackActualPosition())
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void arrivedAtWorkStationOutbound(TransportOrderContext ctx) {
        TransportStatusReportVo vo = TransportStatusReportVo.builder()
                .status(GALTransportStatus.ArrivedAtWorkStation)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .zone("B")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void completedInbound(TransportOrderContext ctx) {
        TransportStatusReportVo vo = TransportStatusReportVo.builder()
                .status(GALTransportStatus.OrderDone_Inbound)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .zone("B")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void completedRelocation(TransportOrderContext ctx) {
        TransportStatusReportVo vo = TransportStatusReportVo.builder()
                .status(GALTransportStatus.OrderDone_Relocation)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .zone("B")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void completedOutbound(TransportOrderContext ctx) {
        TransportStatusReportVo vo = TransportStatusReportVo.builder()
                .status(GALTransportStatus.OrderDone_Outbound)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .zone("B")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void takeOffOutbound(TransportOrderContext ctx) {
        TransportStatusReportVo vo = TransportStatusReportVo.builder()
                .status(GALTransportStatus.TakeOff)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .zone("B")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void binEmptyOutbound(TransportOrderContext ctx) {
        TransportStatusReportVo vo = TransportStatusReportVo.builder()
                .status(GALTransportStatus.BinEmpty)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .zone("B")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void shortageOutbound(TransportOrderContext ctx) {
        TransportStatusReportVo vo = TransportStatusReportVo.builder()
                .status(GALTransportStatus.Shortage)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .zone("B")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void notAllowedPickUpOutbound(TransportOrderContext ctx) {
        TransportStatusReportVo vo = TransportStatusReportVo.builder()
                .status(GALTransportStatus.NotAllowedPickUp)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .zone("B")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void notAllowedPickUpInbound(TransportOrderContext ctx) {
        TransportStatusReportVo vo = TransportStatusReportVo.builder()
                .status(GALTransportStatus.NotAllowedPickUp)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .zone("B")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void arrivedAtRackOutbound(TransportOrderContext ctx) {
        TransportStatusReportVo vo = TransportStatusReportVo.builder()
                .status(GALTransportStatus.ArrivedAtRack)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .zone("B")
                .build();

        saveTransportProgress(vo);
    }

    // --- [Other Utilities] ---
    @Transactional(value = "db2TransactionManager")
    public IdocEntity transferedIdocId(Long idocId) {
        IdocEntity idoc = idocJpaRepository.findByLineId(idocId)
                .orElseThrow(() -> new RuntimeException("IDOC을 찾을 수 없습니다."));
        idoc.setErrorCode(Integer.parseInt(IdocErrorCode.Processed.getValue()));
        idoc.setDtimeMod(LocalDateTime.now().withNano(0));
        return idocJpaRepository.save(idoc);
    }

    @Transactional(value = "db2TransactionManager")
    public void stationOccupiedInbound(StationOccupiedVo vo) {
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0);
        IdocEntity idocEntity =
                IdocEntity
                        .builder()
                        .lineId(idocJpaRepository.findMaxLineId() + 1)
                        .idocTypId(11L)
                        .source(20L)
                        .destination(1L)
                        .dtimeCre(localDateTime)
                        .build();
        idocJpaRepository.save(idocEntity);

        H2TransEntity h2TransEntity =
                H2TransEntity.builder()
                        .lineId(h2TransJpaRepository.findMaxLineId() + 1)
                        .idocId(idocEntity.getLineId())
                        .dtimeCre(localDateTime).dataCode(10L)
                        .cTransTy(Long.parseLong(GALTransportStatus.StationOccupied.getValue()))
                        .cClient("999")
                        .cCoId(vo.getContainerId())
                        .cLocId(vo.getLocationId())
                        .cWcId(vo.getWorkCenterId())
                        .build();
        h2TransJpaRepository.save(h2TransEntity);
    }

    @Transactional("db2TransactionManager")
    public H2OrderDetailVo selectH2OrderDetailByIdocId(Long IdocId) {
        log.info("selectH2OrderDetailByIdocId");
        List<H2OrderMEntity> h2OrderMEntities = h2OrderMJpaRepository.findByIdocId(IdocId);
        List<H2OrderDEntity> h2OrderDEntities = h2OrderDJpaRepository.findByIdocId(IdocId);
        if(h2OrderMEntities.size() != 1 && h2OrderDEntities.size() != 1){
            throw new RuntimeException("H2orderm H2orderd size Error");
        }
        H2OrderMEntity h2OrderM = h2OrderMEntities.get(0);
        H2OrderDEntity h2OrderD = h2OrderDEntities.get(0);
        return H2OrderDetailVo.builder().master(h2OrderM).detail(h2OrderD).build();
    }

    @Transactional("db2TransactionManager")
    public H2OrderDetailRelocationVo selectH2OrderDetailByIdocIdForRelocation(Long IdocId) {
        log.info("selectH2OrderDetailByIdocId");
        List<H2OrderMEntity> h2OrderMEntities = h2OrderMJpaRepository.findByIdocId(IdocId);
        List<H2OrderDEntity> h2OrderDEntities = h2OrderDJpaRepository.findByIdocId(IdocId);
        if(h2OrderMEntities.size() != 1 ){
            throw new RuntimeException("H2orderm size Error");
        }
        if(h2OrderDEntities.size() != 2 ){
            throw new RuntimeException("H2orderd size Error");
        }
        H2OrderMEntity h2OrderM = h2OrderMEntities.get(0);
        H2OrderDEntity h2OrderDSource = h2OrderDEntities.get(0);
        H2OrderDEntity h2OrderDTarget = h2OrderDEntities.get(1);
        return H2OrderDetailRelocationVo.builder()
                .master(h2OrderM)
                .source(h2OrderDSource)
                .target(h2OrderDTarget)
                .build();
    }

    @Transactional(readOnly = true, value = "db2TransactionManager")
    public Page<IdocEntity> selectIdocListByOrderType(Pageable pageable, String orderType) {
        log.info("selectIdocListByOrderType");
        return idocJpaRepository.findIdocsByOrderType(orderType,pageable);
    }

    @Transactional(readOnly = true, value = "db2TransactionManager")
    public Page<H2OrderMEntity> selectH2OrderMByIdocId(Long IdocId, Pageable pageable) {
        log.info("selectH2OrderMByIdocId");
        // 1. DB에서 엔티티 페이지를 조회합니다.
        return h2OrderMJpaRepository.findByIdocId(IdocId,pageable);
    }

    @Transactional(readOnly = true, value = "db2TransactionManager")
    public Page<H2OrderDEntity> selectH2OrderDByIdocId(Long IdocId, Pageable pageable) {
        log.info("selectH2OrderDByIdocId");
        return  h2OrderDJpaRepository.findByIdocId(IdocId,pageable);
    }

    @Transactional(value = "db2TransactionManager")
    public Page<H2TransEntity> selectH2TransByOrderId(Long orderId, Pageable pageable) {
        log.info("selectH2TransByOrderId");
        return h2TransJpaRepository.selectByCOrderId(orderId.toString(),pageable);
    }
}