package kr.co.aim.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.common.condition.GALDetailInterfaceSearchCondition;
import kr.co.aim.common.condition.GALInterfaceSearchCondition;
import kr.co.aim.api.dto.insert.IfEventQueueDto;
import kr.co.aim.api.strategy.FactoryGALInterfaceStrategy;
import kr.co.aim.api.vo.insert.sim.H2TransReportVo;
import kr.co.aim.common.condition.GALPartSearchCondition;
import kr.co.aim.common.enums.*;
import kr.co.aim.domain.model.GALDetailInterfaceResponse;
import kr.co.aim.domain.model.GALInterfaceResponse;
import kr.co.aim.domain.model.GALPartResponse;
import kr.co.aim.domain.model.IfEventQueue;
import kr.co.aim.domain.repository.GALInterfaceRepository;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile({"scheduler","web","simulator"})
@ConditionalOnProperty(name = "factory.type", havingValue = "insert")
public class InsertExternalInterfaceService implements FactoryGALInterfaceStrategy {

    private final IdocJpaRepository idocJpaRepository;
    private final H2OrderMJpaRepository h2OrderMJpaRepository;
    private final H2OrderDJpaRepository h2OrderDJpaRepository;
    private final H2TransJpaRepository h2TransJpaRepository;
    private final ObjectMapper objectMapper;
    private final GALInterfaceRepository galInterfaceRepository;

    // --- [Helper Methods] ---

    private IdocEntity buildBaseIdoc(LocalDateTime now) {
        //TODO : max 값 시퀀스로 수정하기
        return IdocEntity.builder()
                .lineId(idocJpaRepository.findMaxLineId())
                .idocTypId(IdocTypeId.CONFIRMATION.getValue())
                .state(IdocState.INITIAL.getValue())
                .errorCode(IdocErrorCode.INIT.getValue())
                .source(IdocMachine.MNG.getValue())
                .destination( IdocMachine.GAL.getValue())
                .dtimeCre(now)
                .dtimeMod(now)
                .modCnt(0)
                .build();
    }

    private H2TransEntity buildBaseH2Trans(H2TransReportVo vo) {
        // TODO: max 값 시퀀스로 수정
        return H2TransEntity.builder()
                .lineId(h2TransJpaRepository.findMaxLineId())
                .idocId(vo.getNewIdoc().getLineId())
                .dtimeCre(vo.getNewIdoc().getDtimeCre())
                .dataCode(vo.getIdocDataCode().getValue())
                .cTransTy(Long.parseLong(vo.getStatus().getValue()))
                .cClient(IdocClient.MNG.getValue())
                .cOrderId(vo.getMaster().getCOrderId())
                .cOrderTy(vo.getMaster().getCOrderTy())
                .cGalId(vo.getMaster().getCGalId())
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

    @Transactional(value = "db2TransactionManager")
    public void reportH2trans(H2TransReportVo vo){
        saveTransportProgress(vo);
    }

    private void saveTransportProgress(H2TransReportVo report) {
        log.info("Reporting Status: {}", report.getStatus());
        LocalDateTime now = LocalDateTime.now().withNano(0);

        IdocEntity newIdoc = buildBaseIdoc(now);
        idocJpaRepository.save(newIdoc);

        H2TransEntity newTrans = buildBaseH2Trans(report);
        h2TransJpaRepository.save(newTrans);
        log.info("Report completed Status: {}", report.getStatus());
    }


    @Transactional(value = "db2TransactionManager")
    public List<IdocEntity> selectByIdocTypIdsAndErrorCode(List<Long> idocTypIds, Integer errorCode) {
        return idocJpaRepository.findByIdocTypIdsAndErrorCode(idocTypIds, errorCode);
    }

    @Transactional(value = "db2TransactionManager")
    public List<IdocEntity> selectByIdocTypIdsAndStateAndErrorCode(List<Long> idocTypIds, Integer state ,Integer errorCode) {
        return idocJpaRepository.findByIdocTypIdsAndStateAndErrorCode(idocTypIds,state,errorCode);
    }

    @Transactional(value = "db2TransactionManager")
    public List<H2OrderMEntity> selectH2OrderMEntityByIdocId(Long idocId) {
        return h2OrderMJpaRepository.findByIdocId(idocId);
    }

    @Transactional(value = "db2TransactionManager")
    public List<H2OrderDEntity> selectH2OrderDEntityByIdocId(Long idocId) {
        return h2OrderDJpaRepository.findByIdocIdOrderByLineIdAsc(idocId);
    }

    @Transactional(value = "db2TransactionManager",propagation = Propagation.REQUIRES_NEW)
    public void transferCompleted(Long idocId) {
        IdocEntity idoc = idocJpaRepository.findByLineId(idocId)
                .orElseThrow(() -> new RuntimeException("IDOC을 찾을 수 없습니다."));
        idoc.setState(IdocState.COMPLETED.getValue());
        idoc.setErrorCode(IdocErrorCode.PROCESSED.getValue());
        idoc.setDtimeMod(LocalDateTime.now().withNano(0));

        Integer currentCnt = idoc.getModCnt();
        int baseCnt = (currentCnt == null) ? 0 : currentCnt;
        idoc.setModCnt(baseCnt + 1);

        idocJpaRepository.save(idoc);
    }

    @Transactional(value = "db2TransactionManager",propagation = Propagation.REQUIRES_NEW)
    public void transferFail(Long idocId) {
        IdocEntity idoc = idocJpaRepository.findByLineId(idocId)
                .orElseThrow(() -> new RuntimeException("IDOC을 찾을 수 없습니다."));
        idoc.setErrorCode(IdocErrorCode.ERROR.getValue());
        idoc.setDtimeMod(LocalDateTime.now().withNano(0));
        idocJpaRepository.save(idoc);
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
        String virtualCarrierName = dto.getVirtualCarrierName();
        BigDecimal actualWeight = null;

        if (dto.getActualWeight() != null && !dto.getActualWeight().trim().isEmpty()) {
            actualWeight = new BigDecimal(dto.getActualWeight());
        }

        //LocalDateTime now = LocalDateTime.now().withNano(0);
        LocalDateTime now = ifEventQueue.getCreateTime();

        // 단 하나의 report 만 하면 되는 경우
        // 이 경우는 단순히 ifEventQueue 의 값에서 h2Trans로 report 하면 된다.

        IdocEntity newIdoc = buildBaseIdoc(now);
        newIdoc = idocJpaRepository.save(newIdoc);

        H2TransEntity h2TransEntity =
                H2TransEntity
                        .builder()
                        .lineId(h2TransJpaRepository.findMaxLineId())
                        .idocId(newIdoc.getLineId())
                        .dtimeCre(now)
                        .dtimeMod(now)
                        .usrMod(SystemName.MNG.getValue())
                        .pgmMod(SystemName.MNG.getValue())
                        .modCnt(0)
                        .dataCode(IdocDataCode.DATA_CODE01.getValue())
                        .cTransTy( Long.parseLong(dto.getTransactionCode()))
                        .cClient(IdocClient.MNG.getValue())
                        .cOrderId(ifEventQueue.getOrderId())
                        .cOrderTy(dto.getOrderType())
                        .cGalId(StringUtils.isNotBlank(dto.getGalId()) ? dto.getGalId() : "")
                        .cGalWhs(dto.getGalWarehouse())
                        .cCoId(ifEventQueue.getCarrierName())
                        .cText1(virtualCarrierName)
                        .cGrWgAct(actualWeight)
                        .cReqZone(dto.getRequestedZoneName())
                        .cZone(dto.getActualZoneName())
                        .cLocId(dto.getActualLocationId())
                        .cErrDsc(dto.getErrorText()) // n개의 보고
                        .cWcId(dto.getActualWorkStationId())
                        .build();
        h2TransJpaRepository.save(h2TransEntity);

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
        return null;
    }
}