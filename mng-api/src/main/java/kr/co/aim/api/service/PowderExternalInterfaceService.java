package kr.co.aim.api.service;

import kr.co.aim.api.vo.powder.ops.H2TransPReportVo;
import kr.co.aim.common.condition.GALDetailInterfaceSearchCondition;
import kr.co.aim.common.condition.GALInterfaceSearchCondition;
import kr.co.aim.api.strategy.FactoryGALInterfaceStrategy;
import kr.co.aim.common.condition.GALPartSearchCondition;
import kr.co.aim.common.dto.powder.H2OrderMDetailResponseDto;
import kr.co.aim.common.dto.powder.IdocH2PartMResponseDto;
import kr.co.aim.common.dto.powder.IdocH2TransResponseDto;
import kr.co.aim.common.dto.powder.IdocOrderMasterResponseDto;
import kr.co.aim.common.enums.*;
import kr.co.aim.domain.model.GALDetailInterfaceResponse;
import kr.co.aim.domain.model.GALInterfaceResponse;
import kr.co.aim.domain.model.GALPartResponse;
import kr.co.aim.domain.repository.GALInterfaceRepository;
import kr.co.aim.infra.persistence.db2entity.powder.*;
import kr.co.aim.infra.persistence.db2springdatajpa.powder.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile({"scheduler","web","simulator"})
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public class PowderExternalInterfaceService implements FactoryGALInterfaceStrategy {

    private final IdocPJpaRepository idocPJpaRepository;
    private final H2OrderMPJpaRepository h2OrderMPJpaRepository;
    private final H2OrderDPJpaRepository h2OrderDPJpaRepository;
    private final H2TransPJpaRepository h2TransPJpaRepository;
    private final GALInterfaceRepository galInterfaceRepository;
    private final H2PartMPJpaRepository h2PartMPJpaRepository;

    private IdocPEntity buildBaseIdoc(LocalDateTime now) {
        return IdocPEntity.builder()
                .lineId(idocPJpaRepository.findMaxLineId())
                .idocTypId(IdocTypeId.CONFIRMATION.getValue())
                .source(IdocMachine.MNG.getValue())
                .destination( IdocMachine.GAL.getValue())
                .dtimeCre(now)
                .state(10L)
                .errorCode(0L)
                .usrMod(SystemName.MNG.getValue())
                .pgmMod(SystemName.MNG.getValue())
                .modCnt(0L)
                .build();
    }

    private H2TransPEntity buildBaseH2Trans(H2TransPReportVo vo) {
        return H2TransPEntity.builder()
                .lineId(h2TransPJpaRepository.findMaxLineId())
                .idocId(vo.getNewIdoc().getLineId())
                .dtimeCre(vo.getNewIdoc().getDtimeCre())
                //.dtimeMod()
                .usrMod(SystemName.MNG.getValue())
                .pgmMod(SystemName.MNG.getValue())
                .modCnt(0L)
                .cOrderId(vo.getOrderId())
                .rrn( Integer.parseInt(vo.getOrderLineNumber()) )
                //.lineNo()
                .lot(vo.getLot())
                .galKey(vo.getGalKey())
                .cTransTy( Long.parseLong( vo.getStatus().getValue() ) )
                .carrierId(vo.getCarrierId())
                .currRrn(vo.getCurrRrn())
                .nextRrn(vo.getNextRrn())
                .actQty(vo.getActQty())
                .missQty(vo.getMissQty())
                .surpQty(vo.getSurpQty())
                .resultStat(vo.getResultStat())
                .errReason(vo.getErrReason())
                .eventDt(vo.getEventDt())
                .h2ordLineId(vo.getH2ordLineId())
                .cPartId(vo.getCPartId())
                .refLineId(vo.getRefLineId())
                .build();
    }


    private void saveTransportProgress(H2TransPReportVo report) {
        log.info("Reporting Status: {}", report.getStatus());
        LocalDateTime now = LocalDateTime.now().withNano(0);

        IdocPEntity newIdoc = buildBaseIdoc(now);
        newIdoc = idocPJpaRepository.save(newIdoc);
        report.setNewIdoc(newIdoc);

        H2TransPEntity newTrans = buildBaseH2Trans(report);
        h2TransPJpaRepository.save(newTrans);
        log.info("Report completed Status: {}", report.getStatus());
    }

    @Override
    @Transactional(value = "db2TransactionManager")
    public Page<GALInterfaceResponse> getInterfaceList(GALInterfaceSearchCondition condition, Pageable pageable) {
        return galInterfaceRepository.getInterfaceList(condition,pageable);
    }

    @Override
    @Transactional(value = "db2TransactionManager")
    public Page<GALDetailInterfaceResponse> getDetailInterfaceList(GALDetailInterfaceSearchCondition condition, Pageable pageable) {
        return galInterfaceRepository.getDetailInterfaceList(condition,pageable);
    }

    @Override
    @Transactional(value = "db2TransactionManager")
    public Page<GALPartResponse> getPartList(GALPartSearchCondition condition, Pageable pageable) {
        return galInterfaceRepository.getPartList(condition,pageable);
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
    public Page<IdocH2TransResponseDto> findIdocWithH2TransByPartIsNotNull(Pageable pageable) {
        return idocPJpaRepository.findIdocWithH2TransByPartIsNotNull(pageable);
    }

    @Transactional(value = "db2TransactionManager")
    public Page<H2OrderDPEntity> findByIdocId(Long idocId, Pageable pageable) {
        return h2OrderDPJpaRepository.findByIdocId(idocId,pageable);
    }

    @Transactional(value = "db2TransactionManager")
    Page<H2OrderMDetailResponseDto> findH2OrderMDetailByIdocId(Long idocId, Pageable pageable){
        return h2OrderMPJpaRepository.findH2OrderMDetailByIdocId(idocId,pageable);
    }

    @Transactional(value = "db2TransactionManager")
    public Page<IdocH2PartMResponseDto> findIdocWithPartMasterByIdocId(
            Long idocId,
            Pageable pageable
    ){
        return idocPJpaRepository.findIdocWithPartMasterByIdocId(idocId,pageable);
    }

    @Transactional(value = "db2TransactionManager")
    public Page<IdocPEntity> findByIdocTypId(
            Long idocTyId,
            Pageable pageable
    ){
        return idocPJpaRepository.findByIdocTypId(idocTyId,pageable);
    }

    @Transactional(value = "db2TransactionManager")
    public Page<IdocPEntity> findByIdocTypIdWithPartMaster(
            Long idocTypId,
            Pageable pageable
    ){
        return idocPJpaRepository.findByIdocTypIdWithPartMaster(idocTypId,pageable);
    }

    @Transactional(value = "db2TransactionManager")
    public Page<H2PartMPEntity> getPartList(Long idocId, Pageable pageable) {
        return h2PartMPJpaRepository.findByIdocId(idocId,pageable);
    }

    @Transactional(value = "db2TransactionManager")
    public List<IdocPEntity> findByStateAndErrorCode(Long state,Long errorCode){
        return idocPJpaRepository.findByStateAndErrorCode(state,errorCode);
    }

    @Transactional(value = "db2TransactionManager",propagation = Propagation.REQUIRES_NEW)
    public void transferCompleted(Long idocId) {
        IdocPEntity idoc = idocPJpaRepository.findByLineId(idocId)
                .orElseThrow(() -> new RuntimeException("IDOC을 찾을 수 없습니다."));
        idoc.setErrorCode(IdocErrorCode.PROCESSED.getValue());
        idoc.setDtimeMod(LocalDateTime.now().withNano(0));
        idocPJpaRepository.save(idoc);
    }

    @Transactional(value = "db2TransactionManager",propagation = Propagation.REQUIRES_NEW)
    public void reportPartMPCreated(H2TransPReportVo report) {
        saveTransportProgress(report);
    }

    @Transactional(value = "db2TransactionManager",propagation = Propagation.REQUIRES_NEW)
    public void reportPartMPModified(H2TransPReportVo report) {
        saveTransportProgress(report);
    }

    @Transactional(value = "db2TransactionManager",propagation = Propagation.REQUIRES_NEW)
    public void transferFail(Long idocId) {
        IdocPEntity idoc = idocPJpaRepository.findByLineId(idocId)
                .orElseThrow(() -> new RuntimeException("IDOC을 찾을 수 없습니다."));
        idoc.setErrorCode(IdocErrorCode.ERROR.getValue());
        idoc.setDtimeMod(LocalDateTime.now().withNano(0));
        idocPJpaRepository.save(idoc);
    }

    @Transactional(value = "db2TransactionManager")
    public H2OrderMPEntity selectH2OrderMEntityByIdocId(Long idocId) {
        List<H2OrderMPEntity> h2OrderMPEntityList = h2OrderMPJpaRepository.findByIdocId(idocId);
        if(h2OrderMPEntityList.isEmpty()){
            throw new RuntimeException("H2OrderM 을 찾을 수 없습니다.");
        }
        else if(h2OrderMPEntityList.size() > 1){
            throw new RuntimeException("H2OrderM이 2개 이상입니다.");
        }
        return h2OrderMPEntityList.get(0);
    }

    @Transactional(value = "db2TransactionManager")
    public List<H2OrderDPEntity> selectH2OrderDEntityByIdocId(Long idocId) {
        return h2OrderDPJpaRepository.findByIdocIdOrderByLineIdAsc(idocId);
    }
}