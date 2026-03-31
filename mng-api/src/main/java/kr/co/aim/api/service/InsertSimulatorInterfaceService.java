package kr.co.aim.api.service;

import kr.co.aim.api.dto.SimulatorIdsDto;
import kr.co.aim.api.vo.insert.*;
import kr.co.aim.common.enums.*;
import kr.co.aim.domain.model.Port;
import kr.co.aim.domain.model.PortDef;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile({"scheduler", "simulator"})
@ConditionalOnProperty(name = "factory.type", havingValue = "insert")
public class InsertSimulatorInterfaceService {

    private final IdocJpaRepository idocJpaRepository;
    private final H2OrderMJpaRepository h2OrderMJpaRepository;
    private final H2OrderDJpaRepository h2OrderDJpaRepository;
    private final H2TransJpaRepository h2TransJpaRepository;

    // --- [Helper Methods] ---

    private IdocEntity buildBaseIdoc(LocalDateTime now) {
        //TODO : max 값 시퀀스로 수정하기
        return IdocEntity.builder()
                .lineId(idocJpaRepository.findMaxLineId() + 1)
                .idocTypId(IdocTypeId.Confirmation.getValue())
                .source(IdocMachine.MNG.getValue())
                .destination( IdocMachine.GAL.getValue())
                .dtimeCre(now)
                .build();
    }

    private H2TransEntity buildBaseH2Trans(H2TransReportVo vo) {
        // TODO: max 값 시퀀스로 수정
        return H2TransEntity.builder()
                .lineId(h2TransJpaRepository.findMaxLineId() + 1)
                .idocId(vo.getNewIdoc().getLineId())
                .dtimeCre(vo.getNewIdoc().getDtimeCre())
                .dataCode(vo.getIdocDataCode().getValue())
                .cTransTy(Long.parseLong(vo.getStatus().getValue()))
                .cClient(IdocClient.MNG.getValue())
                .cOrderId(vo.getMaster().getCOrderId())
                .cOrderTy(vo.getMaster().getCOrderTy())
                .cGaId(vo.getMaster().getCGalId())
                .cGalWhs(vo.getMaster().getCGalWhs())
                .cCoId(vo.getCarrierName() != null ? vo.getCarrierName() : vo.getFirstDetail().getCCoId())
                .cGrWgAct(vo.getWeight())
                .cReqZone(vo.getFirstDetail().getCZone())
                .cZone( vo.getActualZone())
                .cLocId(vo.getLocationCode())
                .cErrDsc(vo.getErrorText())
                .cWcId(vo.getMaster().getCWcId())
                .build();
    }

    private void saveTransportProgress(H2TransReportVo report) {
        log.info("Reporting Status: {}", report.getStatus());
        LocalDateTime now = LocalDateTime.now().withNano(0);

        IdocEntity newIdoc = buildBaseIdoc(now);
        idocJpaRepository.save(newIdoc);

        H2TransEntity newTrans = buildBaseH2Trans(report);
        h2TransJpaRepository.save(newTrans);
    }

    // --- [Public API] Context를 사용하여 극도로 단순화된 메서드들 ---
    @Transactional(value = "db2TransactionManager")
    public void acceptOutbound(TransportOrderContext ctx) {
        H2TransReportVo vo =
                H2TransReportVo
                        .builder()
                        .status(GALTransportStatus.Accept)
                        .sourceIdoc(ctx.getIdoc())
                        .master(ctx.getMaster())
                        .details(ctx.getDetails())
                        .actualZone("")
                        .build();
        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void acceptInbound(TransportOrderContext ctx) {
        H2TransReportVo vo = H2TransReportVo.builder()
                .status(GALTransportStatus.Accept)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .actualZone("B")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void workStationEmptyInbound(TransportOrderContext ctx) {
        H2TransReportVo vo = H2TransReportVo.builder()
                .status(GALTransportStatus.WorkstationEmpty)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .actualZone("B")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void arrivedWorkstationErrorInbound(TransportOrderContext ctx) {
        H2TransReportVo vo = H2TransReportVo.builder()
                .status(GALTransportStatus.ArrivedAtWorkstationWithError)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .actualZone("")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void errorTextInbound(String errorText, TransportOrderContext ctx) {
        H2TransReportVo vo = H2TransReportVo.builder()
                .status(GALTransportStatus.ErrorText)
                .errorText(errorText)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .actualZone("")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void carrierScannedInbound(TransportOrderContext ctx, SimulatorIdsDto dto) {
        String carrierName = StringUtils.isNotBlank(dto.getCarrierId()) ? dto.getCarrierId() : ctx.getFirstDetail().getCCoId();

        H2TransReportVo vo = H2TransReportVo.builder()
                .status(GALTransportStatus.CarrierScanned)
                .carrierName(carrierName)
                .locationCode(dto.getLocationCode())
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .actualZone("")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void releaseOutbound(TransportOrderContext ctx) {
        H2TransReportVo vo = H2TransReportVo.builder()
                .status(GALTransportStatus.Released)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .actualZone("B")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void internalRelocationOutbound(TransportOrderContext ctx) {
        H2TransReportVo vo = H2TransReportVo.builder()
                .status(GALTransportStatus.InternalRelocation)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .actualZone("B")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void dropOnTunnelRelocation(TransportOrderContext ctx) {
        H2TransReportVo vo = H2TransReportVo.builder()
                .status(GALTransportStatus.DroppedOnTunnelConveyor)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .actualZone("B")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void outOfRackOutbound(TransportOrderContext ctx) {
        H2TransReportVo vo = H2TransReportVo.builder()
                .status(GALTransportStatus.OutOfRack)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .actualZone("")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void outOfRackInbound(TransportOrderContext ctx, SimulatorIdsDto dto) {
        H2TransReportVo vo = H2TransReportVo.builder()
                .status(GALTransportStatus.OutOfRack)
                .actualZone(dto.getRackActualPosition())
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void arrivedAtWorkStationOutbound(TransportOrderContext ctx) {
        H2TransReportVo vo = H2TransReportVo.builder()
                .status(GALTransportStatus.ArrivedAtWorkStation)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .actualZone("B")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void completedInbound(TransportOrderContext ctx) {
        H2TransReportVo vo = H2TransReportVo.builder()
                .status(GALTransportStatus.OrderDone_Inbound)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .actualZone("B")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void completedRelocation(TransportOrderContext ctx) {
        H2TransReportVo vo = H2TransReportVo.builder()
                .status(GALTransportStatus.OrderDone_Relocation)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .actualZone("B")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void completedOutbound(TransportOrderContext ctx) {
        H2TransReportVo vo = H2TransReportVo.builder()
                .status(GALTransportStatus.OrderDone_Outbound)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .actualZone("B")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void takeOffOutbound(TransportOrderContext ctx) {
        H2TransReportVo vo = H2TransReportVo.builder()
                .status(GALTransportStatus.TakeOff)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .actualZone("B")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void binEmptyOutbound(TransportOrderContext ctx) {
        H2TransReportVo vo = H2TransReportVo.builder()
                .status(GALTransportStatus.BinEmpty)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .actualZone("B")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void shortageOutbound(TransportOrderContext ctx) {
        H2TransReportVo vo = H2TransReportVo.builder()
                .status(GALTransportStatus.Shortage)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .actualZone("B")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void notAllowedPickUpOutbound(TransportOrderContext ctx) {
        H2TransReportVo vo = H2TransReportVo.builder()
                .status(GALTransportStatus.NotAllowedPickUp)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .actualZone("B")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void notAllowedPickUpInbound(TransportOrderContext ctx) {
        H2TransReportVo vo = H2TransReportVo.builder()
                .status(GALTransportStatus.NotAllowedPickUp)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .actualZone("B")
                .build();

        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void arrivedAtRackOutbound(TransportOrderContext ctx) {
        H2TransReportVo vo = H2TransReportVo.builder()
                .status(GALTransportStatus.ArrivedAtRack)
                .sourceIdoc(ctx.getIdoc())
                .master(ctx.getMaster())
                .details(ctx.getDetails())
                .actualZone("B")
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
                        .cCoId(vo.getCarrierName())
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