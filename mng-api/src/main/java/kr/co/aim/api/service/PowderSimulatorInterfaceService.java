package kr.co.aim.api.service;

import kr.co.aim.api.vo.powder.sim.H2TransReportVo;
import kr.co.aim.api.vo.powder.sim.ProductionOrderContext;
import kr.co.aim.common.enums.*;
import kr.co.aim.infra.persistence.db2entity.powder.H2TransPEntity;
import kr.co.aim.infra.persistence.db2entity.powder.IdocPEntity;
import kr.co.aim.infra.persistence.db2springdatajpa.powder.H2OrderDPJpaRepository;
import kr.co.aim.infra.persistence.db2springdatajpa.powder.H2OrderMPJpaRepository;
import kr.co.aim.infra.persistence.db2springdatajpa.powder.H2TransPJpaRepository;
import kr.co.aim.infra.persistence.db2springdatajpa.powder.IdocPJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
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
        return IdocPEntity.builder()
                .lineId(idocPJpaRepository.findMaxLineId())
                .idocTypId(IdocTypeId.Confirmation.getValue())
                .state(IdocState.INITIAL.getValue())
                .errorCode(IdocErrorCode.Init.getValue())
                .source(IdocMachine.MNG.getValue())
                .destination( IdocMachine.GAL.getValue())
                .dtimeCre(now)
                .usrMod(SystemName.MNG.getValue())
                .pgmMod(SystemName.MNG.getValue())
                .modCnt(0L)
                .build();
    }

    private H2TransPEntity buildBaseH2Trans(H2TransReportVo vo) {
        String orderId = "";
        Integer rrn = null;
        Integer lineNo = null;
        Integer lot = null;
        String galKey = "";
        Long h2ordLineId = null;
        String partId = "";
        if(ObjectUtils.isNotEmpty(vo.getDetail())){
            orderId = vo.getDetail().getCOrderId();
            rrn = vo.getDetail().getRrn();
            lineNo = vo.getDetail().getLineNo();
            lot = vo.getDetail().getLot();
            galKey = vo.getDetail().getGalKey();
            h2ordLineId = vo.getDetail().getLineId();
        }
        if(ObjectUtils.isNotEmpty(vo.getPartId())) {
            partId = vo.getPartId();
        }

        return H2TransPEntity.builder()
                .lineId(h2TransPJpaRepository.findMaxLineId())
                .idocId(vo.getNewIdoc().getLineId())
                .dtimeCre(vo.getNewIdoc().getDtimeCre())
                .dtimeMod(vo.getNewIdoc().getDtimeMod())
                .usrMod(vo.getNewIdoc().getUsrMod())
                .pgmMod(vo.getNewIdoc().getPgmMod())
                .modCnt(vo.getNewIdoc().getModCnt())
                .cOrderId(orderId)
                .rrn(rrn)
                .lineNo(lineNo)
                .lot(lot)
                .galKey(galKey)
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
                .h2ordLineId(h2ordLineId)
                .cPartId(partId)
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
        idocPEntity.setErrorCode(IdocErrorCode.Processed.getValue());
        idocPEntity.setDtimeMod(LocalDateTime.now().withNano(0));
        idocPEntity.setPgmMod(SystemName.MNG.getValue());
        Long modifyCount = idocPEntity.getModCnt();
        Long nextCount = (modifyCount == null) ? 1L : modifyCount + 1;
        idocPEntity.setModCnt(nextCount);
        return idocPJpaRepository.save(idocPEntity);
    }

    @Transactional(value = "db2TransactionManager")
    public void accept(ProductionOrderContext ctx) {
        H2TransReportVo vo =
                H2TransReportVo
                        .builder()
                        .status(GALProductionStatus.ACCEPT)
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
                        .status(GALProductionStatus.RELEASE)
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
                        .status(GALProductionStatus.FIBC_ON_PALLET)
                        .sourceIdoc(ctx.getIdoc())
                        .master(ctx.getMaster())
                        .detail(ctx.getDetail())
                        .carrierName(ctx.getCarrierName())
                        .actQty(ctx.getActualQuantity())
                        .build();
        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void whatIsNextRRN(ProductionOrderContext ctx) {
        H2TransReportVo vo =
                H2TransReportVo
                        .builder()
                        .status(GALProductionStatus.WHAT_IS_NEXT_RRN)
                        .sourceIdoc(ctx.getIdoc())
                        .master(ctx.getMaster())
                        .detail(ctx.getDetail())
                        .carrierName(ctx.getCarrierName())
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
                        .status(GALProductionStatus.PRODUCTION_STARTED)
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
                        .status(GALProductionStatus.PRODUCTION_ENDED)
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
                        .status(GALProductionStatus.PALLET_LOAD_COMPLETED_TO_WAREHOUSE)
                        .sourceIdoc(ctx.getIdoc())
                        .master(ctx.getMaster())
                        .detail(ctx.getDetail())
                        .carrierName(ctx.getCarrierName())
                        .actQty(ctx.getActualQuantity())
                        .build();
        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void changedStockPerContainer(ProductionOrderContext ctx) {
        H2TransReportVo vo =
                H2TransReportVo
                        .builder()
                        .status(GALProductionStatus.CHANGED_STOCK_PER_CONTAINER)
                        .sourceIdoc(ctx.getIdoc())
                        .master(ctx.getMaster())
                        .detail(ctx.getDetail())
                        .carrierName(ctx.getCarrierName())
                        .actQty(ctx.getActualQuantity())
                        .build();
        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void reassignRRN(ProductionOrderContext ctx) {
        H2TransReportVo vo =
                H2TransReportVo
                        .builder()
                        .status(GALProductionStatus.REASSIGN_RRN)
                        .sourceIdoc(ctx.getIdoc())
                        .master(ctx.getMaster())
                        .detail(ctx.getDetail())
                        .carrierName(ctx.getCarrierName())
                        .build();
        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void missingQty(ProductionOrderContext ctx) {
        H2TransReportVo vo =
                H2TransReportVo
                        .builder()
                        .status(GALProductionStatus.MISSING_QUANTITY)
                        .sourceIdoc(ctx.getIdoc())
                        .master(ctx.getMaster())
                        .detail(ctx.getDetail())
                        .carrierName(ctx.getCarrierName())
                        .missQty(ctx.getMissingQuantity())
                        .build();
        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void surplusQty(ProductionOrderContext ctx) {
        H2TransReportVo vo =
                H2TransReportVo
                        .builder()
                        .status(GALProductionStatus.SURPLUS_QUANTITY)
                        .sourceIdoc(ctx.getIdoc())
                        .master(ctx.getMaster())
                        .detail(ctx.getDetail())
                        .carrierName(ctx.getCarrierName())
                        .surpQty(ctx.getSurplusQuantity())
                        .build();
        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void moveRRNCompleted(ProductionOrderContext ctx) {
        H2TransReportVo vo =
                H2TransReportVo
                        .builder()
                        .status(GALProductionStatus.MOVE_RRN_COMPLETED)
                        .sourceIdoc(ctx.getIdoc())
                        .master(ctx.getMaster())
                        .detail(ctx.getDetail())
                        .build();
        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void orderLineNoCompleted(ProductionOrderContext ctx) {
        H2TransReportVo vo =
                H2TransReportVo
                        .builder()
                        .status(GALProductionStatus.ORDER_LINE_NO_COMPLETED)
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
                        .status(GALProductionStatus.ORDER_COMPLETED)
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
                        .status(GALProductionStatus.SHORTAGE)
                        .sourceIdoc(ctx.getIdoc())
                        .master(ctx.getMaster())
                        .detail(ctx.getDetail())
                        .missQty(ctx.getMissingQuantity())
                        .build();
        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void partCreated(ProductionOrderContext ctx) {
        H2TransReportVo vo =
                H2TransReportVo
                        .builder()
                        .status(GALProductionStatus.CREATED_PART_MASTER)
                        .sourceIdoc(ctx.getIdoc())
                        .partId(ctx.getPartId())
                        .build();
        saveTransportProgress(vo);
    }

    @Transactional(value = "db2TransactionManager")
    public void partUpdated(ProductionOrderContext ctx) {
        H2TransReportVo vo =
                H2TransReportVo
                        .builder()
                        .status(GALProductionStatus.CHANGED_PART_MASTER)
                        .sourceIdoc(ctx.getIdoc())
                        .partId(ctx.getPartId())
                        .build();
        saveTransportProgress(vo);
    }

}