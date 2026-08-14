package kr.co.aim.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.api.dto.powder.IfEventQueueDto;
import kr.co.aim.api.vo.powder.ops.H2TransPReportVo;
import kr.co.aim.common.condition.GALDetailInterfaceSearchCondition;
import kr.co.aim.common.condition.GALInterfaceSearchCondition;
import kr.co.aim.api.strategy.FactoryGALInterfaceStrategy;
import kr.co.aim.common.condition.GALPartSearchCondition;
import kr.co.aim.common.dto.powder.IdocH2PartMResponseDto;
import kr.co.aim.common.dto.powder.IdocH2TransResponseDto;
import kr.co.aim.common.dto.powder.IdocOrderMasterResponseDto;
import kr.co.aim.common.enums.*;
import kr.co.aim.domain.model.GALDetailInterfaceResponse;
import kr.co.aim.domain.model.GALInterfaceResponse;
import kr.co.aim.domain.model.GALPartResponse;
import kr.co.aim.domain.model.IfEventQueue;
import kr.co.aim.domain.repository.GALInterfaceRepository;
import kr.co.aim.infra.persistence.db2entity.powder.*;
import kr.co.aim.infra.persistence.db2springdatajpa.powder.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile({"scheduler","web","simulator","pex","tex"})
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public class PowderExternalInterfaceService implements FactoryGALInterfaceStrategy {

    private final IdocPJpaRepository idocPJpaRepository;
    private final H2OrderMPJpaRepository h2OrderMPJpaRepository;
    private final H2OrderDPJpaRepository h2OrderDPJpaRepository;
    private final H2TransPJpaRepository h2TransPJpaRepository;
    private final GALInterfaceRepository galInterfaceRepository;
    private final H2PartMPJpaRepository h2PartMPJpaRepository;
    private final ObjectMapper objectMapper;

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
                .cPartId(vo.getCPartId())
                .mngKey(vo.getMngKey())
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
        Optional<IdocPEntity> optionalIdocPEntity = idocPJpaRepository.findByLineId(idocId);
        if(optionalIdocPEntity.isEmpty()){
            throw new RuntimeException("IDOC을 찾을 수 없습니다.");
        }
        IdocPEntity idoc = optionalIdocPEntity.get();
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



    @Transactional(value = "db2TransactionManager",propagation = Propagation.REQUIRES_NEW)
    public void reportH2trans(IfEventQueue ifEventQueue) {
        // 1단계 ifEventQueue 를 역직렬화를 통해 dto객체로 변경
        // idoc, h2orderM, h2orderd 조회 후 로직 처리
        // 결국 idoc , h2trans 데이터 생성
        IfEventQueueDto dto = null;
        try {
            dto = objectMapper.readValue(ifEventQueue.getPayload(),IfEventQueueDto.class);
        } catch (Exception e) {
            log.error("IfEventQueue 페이로드 역직렬화 중 에러 발생. payload: {}", ifEventQueue.getPayload(), e);
            throw new RuntimeException(e);
        }

        //LocalDateTime now = LocalDateTime.now().withNano(0);
        LocalDateTime now = ifEventQueue.getCreateTime();

        // 단 하나의 report 만 하면 되는 경우
        // 이 경우는 단순히 ifEventQueue 의 값에서 h2Trans로 report 하면 된다.

        IdocPEntity newIdoc = buildBaseIdoc(now);
        newIdoc = idocPJpaRepository.save(newIdoc);

        Integer lotName = ObjectUtils.isEmpty(dto.getLotName()) ? null : Integer.valueOf(dto.getLotName());
        String galKey = dto.getGalKey();
        Long cTransTy = ObjectUtils.isEmpty(dto.getTransactionCode()) ? null : Long.valueOf(dto.getTransactionCode());
        String carrierId = dto.getCarrierName();
        BigDecimal actQty = dto.getQuantity();
        BigDecimal missQty = dto.getMissQuantity();

        String resultStat = dto.getResultStatus();
        String errReason = dto.getErrorReason();
        LocalDateTime eventDt = ifEventQueue.getCreateTime();
        String cPartId = dto.getItemName();
        Long mngKey = ObjectUtils.isEmpty(dto.getMngKey()) ? null : Long.valueOf(dto.getMngKey());

        H2TransPEntity h2TransEntity =
                H2TransPEntity
                        .builder()
                        .lineId(h2TransPJpaRepository.findMaxLineId())
                        .idocId(newIdoc.getLineId())
                        .dtimeCre(now)
                        .dtimeMod(now)
                        .usrMod(SystemName.MNG.getValue())
                        .pgmMod(SystemName.MNG.getValue())
                        .modCnt(0L)
                        .cOrderId(dto.getOrderId())
                        .rrn(Integer.parseInt(dto.getOrderLineNumber()))
                        .lineNo(1)
                        .lot(lotName)
                        .galKey(galKey)
                        .cTransTy(cTransTy)
                        .carrierId(carrierId)
                        //.currRrn()
                        //.nextRrn()
                        .actQty(actQty)
                        .missQty(missQty)
                        //.surpQty()
                        .resultStat(resultStat)
                        .errReason(errReason)
                        .eventDt(eventDt)
                        .cPartId(cPartId)
                        .mngKey(mngKey)
                        .build();
        h2TransPJpaRepository.save(h2TransEntity);

    }
}