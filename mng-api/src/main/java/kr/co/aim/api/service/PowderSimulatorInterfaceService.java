package kr.co.aim.api.service;

import kr.co.aim.api.vo.powder.sim.H2TransReportVo;
import kr.co.aim.api.vo.powder.sim.ProductionOrderContext;
import kr.co.aim.common.dto.powder.IdocH2TransResponseDto;
import kr.co.aim.common.dto.powder.IdocOrderMasterResponseDto;
import kr.co.aim.common.enums.*;
import kr.co.aim.infra.persistence.db2entity.powder.H2OrderDPEntity;
import kr.co.aim.infra.persistence.db2entity.powder.H2TransPEntity;
import kr.co.aim.infra.persistence.db2entity.powder.IdocPEntity;
import kr.co.aim.infra.persistence.db2springdatajpa.powder.H2OrderDPJpaRepository;
import kr.co.aim.infra.persistence.db2springdatajpa.powder.H2OrderMPJpaRepository;
import kr.co.aim.infra.persistence.db2springdatajpa.powder.H2TransPJpaRepository;
import kr.co.aim.infra.persistence.db2springdatajpa.powder.IdocPJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile({"scheduler", "simulator"})
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public class PowderSimulatorInterfaceService {

    private final IdocPJpaRepository idocPJpaRepository;
    private final H2OrderMPJpaRepository h2OrderMPJpaRepository;
    private final H2OrderDPJpaRepository h2OrderDPJpaRepository;
    private final H2TransPJpaRepository h2TransPJpaRepository;

    private IdocPEntity buildBaseIdoc(LocalDateTime now) {
        //TODO : max 값 시퀀스로 수정하기
        return IdocPEntity.builder()
                .lineId(idocPJpaRepository.findMaxLineId() + 1)
                .idocTypId(IdocTypeId.Confirmation.getValue())
                .source(IdocMachine.MNG.getValue())
                .destination( IdocMachine.GAL.getValue())
                .dtimeCre(now)
                .build();
    }

    private H2TransPEntity buildBaseH2Trans(H2TransReportVo vo) {
        // TODO: max 값 시퀀스로 수정
        return H2TransPEntity.builder()
                .lineId(h2TransPJpaRepository.findMaxLineId() + 1)
                .idocId(vo.getNewIdoc().getLineId())
                .dtimeCre(vo.getNewIdoc().getDtimeCre())
                .dtimeMod(vo.getNewIdoc().getDtimeMod())
                .usrMod(vo.getNewIdoc().getUsrMod())
                .pgmMod(vo.getNewIdoc().getPgmMod())
                .modCnt(vo.getNewIdoc().getModCnt())
                .cOrderId(vo.getDetail().getCOrderId())
                .rrn(vo.getDetail().getRrn())
                .lineNo(vo.getDetail().getLineNo())
                .lot(vo.getDetail().getLot())
                .galKey(vo.getDetail().getGalKey())
                .cTransTy(Long.parseLong(vo.getStatus().getValue()))
                .carrierId(vo.getCarrierName())
                //.currRrn()
                //.nextRrn()
                .actQty(vo.getActQty())
                .missQty(vo.getMissQty())
                .surpQty(vo.getSurpQty())
                .resultStat(vo.getResultStat())
                .errReason(vo.getErrReason())
                .eventDt(vo.getNewIdoc().getDtimeCre())
                .h2ordLineId(vo.getDetail().getLineId())
                .build();
    }

    private void saveTransportProgress(H2TransReportVo report) {
        log.info("Reporting Status: {}", report.getStatus());
        LocalDateTime now = LocalDateTime.now().withNano(0);

        IdocPEntity newIdoc = buildBaseIdoc(now);
        idocPJpaRepository.save(newIdoc);

        report.setNewIdoc(newIdoc);

        H2TransPEntity newTrans = buildBaseH2Trans(report);
        h2TransPJpaRepository.save(newTrans);
    }

    @Transactional(value = "db2TransactionManager")
    public IdocPEntity transfer(Long idocId) {
        Optional<IdocPEntity> optionalIdocPEntity = idocPJpaRepository.findByLineId(idocId);
        if(optionalIdocPEntity.isEmpty()){
            throw new RuntimeException("IDOC을 찾을 수 없습니다.");
        }
        IdocPEntity idocPEntity = optionalIdocPEntity.get();
        idocPEntity.setState(IdocState.COMPLETED.getValue());
        idocPEntity.setErrorCode(Long.parseLong(IdocErrorCode.Processed.getValue()));
        idocPEntity.setDtimeMod(LocalDateTime.now().withNano(0));
        return idocPJpaRepository.save(idocPEntity);
    }

    @Transactional(value = "db2TransactionManager")
    public void accept(ProductionOrderContext ctx) {
        H2TransReportVo vo =
                H2TransReportVo
                        .builder()
                        .status(GALProductionStatus.Accept)
                        .sourceIdoc(ctx.getIdoc())
                        .master(ctx.getMaster())
                        .detail(ctx.getDetail())
                        .build();
        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void release(ProductionOrderContext ctx) {
        H2TransReportVo vo =
                H2TransReportVo
                        .builder()
                        .status(GALProductionStatus.Released)
                        .sourceIdoc(ctx.getIdoc())
                        .master(ctx.getMaster())
                        .detail(ctx.getDetail())
                        .build();
        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void fibcOnPallet(ProductionOrderContext ctx) {
        H2TransReportVo vo =
                H2TransReportVo
                        .builder()
                        .status(GALProductionStatus.FibcOnPallet)
                        .sourceIdoc(ctx.getIdoc())
                        .master(ctx.getMaster())
                        .detail(ctx.getDetail())
                        .carrierName(ctx.getCarrierName())
                        .actQty(ctx.getDetail().getDefaultReceiveQty())
                        .build();
        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void productionStart(ProductionOrderContext ctx) {
        BigDecimal actQty = null;
        if(ctx.getIdoc().getIdocTypId().equals(12L)){
            actQty = ctx.getDetail().getDefaultReceiveQty();
        }else{
            actQty = ctx.getActualQuantity();
        }

        H2TransReportVo vo =
                H2TransReportVo
                        .builder()
                        .status(GALProductionStatus.ProductionStarted)
                        .sourceIdoc(ctx.getIdoc())
                        .master(ctx.getMaster())
                        .detail(ctx.getDetail())
                        .carrierName(ctx.getCarrierName())
                        .actQty(actQty)
                        .build();
        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void productionEnd(ProductionOrderContext ctx) {

        BigDecimal actQty = null;
        if(ctx.getIdoc().getIdocTypId().equals(12L)){
            actQty = ctx.getDetail().getDefaultReceiveQty();
        }else{
            actQty = ctx.getActualQuantity();
        }

        H2TransReportVo vo =
                H2TransReportVo
                        .builder()
                        .status(GALProductionStatus.ProductionEnded)
                        .sourceIdoc(ctx.getIdoc())
                        .master(ctx.getMaster())
                        .detail(ctx.getDetail())
                        .carrierName(ctx.getCarrierName())
                        .actQty(actQty)
                        .build();
        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void palletLoadCompletedToWarehouse(ProductionOrderContext ctx) {
        H2TransReportVo vo =
                H2TransReportVo
                        .builder()
                        .status(GALProductionStatus.PalletLoadCompletedToWarehouse)
                        .sourceIdoc(ctx.getIdoc())
                        .master(ctx.getMaster())
                        .detail(ctx.getDetail())
                        .carrierName(ctx.getCarrierName())
                        .build();
        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void orderLineNoCompleted(ProductionOrderContext ctx) {
        H2TransReportVo vo =
                H2TransReportVo
                        .builder()
                        .status(GALProductionStatus.OrderLineNoCompleted)
                        .sourceIdoc(ctx.getIdoc())
                        .master(ctx.getMaster())
                        .detail(ctx.getDetail())
                        .build();
        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void orderCompleted(ProductionOrderContext ctx) {
        H2TransReportVo vo =
                H2TransReportVo
                        .builder()
                        .status(GALProductionStatus.OrderCompleted)
                        .sourceIdoc(ctx.getIdoc())
                        .master(ctx.getMaster())
                        .detail(ctx.getDetail())
                        .build();
        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void shortage(ProductionOrderContext ctx) {
        H2TransReportVo vo =
                H2TransReportVo
                        .builder()
                        .status(GALProductionStatus.Shortage)
                        .sourceIdoc(ctx.getIdoc())
                        .master(ctx.getMaster())
                        .detail(ctx.getDetail())
                        .build();
        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public Page<IdocOrderMasterResponseDto> findIdocWithOrderMasterByIdocTypId(Long idocTypId, Pageable pageable) {
        return idocPJpaRepository.findIdocWithOrderMasterByIdocTypId(idocTypId,pageable);
    }

    @Transactional(value = "db2TransactionManager")
    public Page<IdocH2TransResponseDto> findIdocWithH2TransByGalKey(String galKey, Pageable pageable) {
        return idocPJpaRepository.findIdocWithH2TransByGalKey(galKey,pageable);
    }

    @Transactional(value = "db2TransactionManager")
    public Page<H2OrderDPEntity> findByIdocId(Long idocId, Pageable pageable) {
        return h2OrderDPJpaRepository.findByIdocId(idocId,pageable);
    }

}