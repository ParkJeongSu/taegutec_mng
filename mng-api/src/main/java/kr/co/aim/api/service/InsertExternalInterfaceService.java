package kr.co.aim.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.common.error.PreconditionNotMetException;
import kr.co.aim.domain.model.*;
import kr.co.aim.domain.repository.condition.GALDetailInterfaceSearchCondition;
import kr.co.aim.domain.repository.condition.GALH2TransInterfaceSearchCondition;
import kr.co.aim.domain.repository.condition.GALInterfaceSearchCondition;
import kr.co.aim.api.dto.insert.IfEventQueueDto;
import kr.co.aim.api.strategy.FactoryGALInterfaceStrategy;
import kr.co.aim.api.vo.insert.sim.H2TransReportVo;
import kr.co.aim.domain.repository.condition.GALPartSearchCondition;
import kr.co.aim.common.enums.*;
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
import org.apache.commons.collections4.CollectionUtils;
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
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile({"scheduler","web","simulator"})
@ConditionalOnProperty(name = "factory.type", havingValue = "insert")
public class InsertExternalInterfaceService implements FactoryGALInterfaceStrategy {

    private final ObjectMapper objectMapper;
    private final IdocJpaRepository idocJpaRepository;
    private final H2OrderMJpaRepository h2OrderMJpaRepository;
    private final H2OrderDJpaRepository h2OrderDJpaRepository;
    private final H2TransJpaRepository h2TransJpaRepository;
    private final GALInterfaceRepository galInterfaceRepository;

    // --- [Helper Methods] ---

    private IdocEntity buildBaseIdoc(LocalDateTime now) {
        return IdocEntity.builder()
                .lineId(idocJpaRepository.findMaxLineId())
                .idocTypId(IdocTypeId.CONFIRMATION.getValue())
                .state(IdocState.INITIAL.getValue())
                .errorCode(IdocErrorCode.INIT.getValue())
                .source(IdocMachine.MNG.getValue())
                .destination( IdocMachine.GAL.getValue())
                //.dtimeCre(now)
                //.dtimeMod(now)
                .modCnt(0)
                .build();
    }

    private H2TransEntity buildBaseH2Trans(H2TransReportVo vo) {
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

    @Transactional(value = "db2TransactionManager", propagation = Propagation.REQUIRES_NEW)
    public List<IdocEntity> selectAndMarkInProcess(List<Long> idocTypIds, Integer initialState, Integer errorCode) {
        // 1. 대상 조회 (건수가 많을 수 있다면 Repository에 Top 100 등 Limit 적용 권장)
        List<IdocEntity> idocEntities = idocJpaRepository.findByIdocTypIdsAndStateAndErrorCode(
                idocTypIds, initialState, errorCode
        );

        if (idocEntities == null || idocEntities.isEmpty()) {
            return idocEntities;
        }

        // 2. 벌크 업데이트를 위한 ID 리스트 추출 및 반환할 객체의 메모리 상태 동기화
        List<Long> lineIds = new ArrayList<>();
        Long inProcessState = IdocState.IN_PROCESS.getValue();

        for (IdocEntity idoc : idocEntities) {
            lineIds.add(idoc.getLineId());

            // 스케줄러로 반환될 객체 자체에도 변경된 값을 맞춰줌
            idoc.setState(inProcessState);
            Integer currentCnt = idoc.getModCnt();
            int baseCnt = (currentCnt == null) ? 0 : currentCnt;
            idoc.setModCnt(baseCnt + 1);
        }

        // 3. 단 1번의 UPDATE ... WHERE LINEID IN (...) 쿼리로 DB2 일괄 반영!
        int result = idocJpaRepository.bulkUpdateStateToInProcess(lineIds, inProcessState);
        log.info("Bulk update state to InProcess Status count: {} ", result);
        return idocEntities;
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
        //idoc.setDtimeMod(LocalDateTime.now().withNano(0));

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
        //idoc.setDtimeMod(LocalDateTime.now().withNano(0));
        idocJpaRepository.save(idoc);
    }
    // --- [단건 H2trans 보고] ---
    @Transactional(value = "db2TransactionManager", propagation = Propagation.REQUIRES_NEW)
    public void reportH2trans(IfEventQueue ifEventQueue) {
        processSingleH2trans(ifEventQueue);
    }

    // --- [복수건 H2trans 보고: 단일 트랜잭션으로 All-or-Nothing 보장] ---
    @Transactional(value = "db2TransactionManager", propagation = Propagation.REQUIRES_NEW)
    public void reportH2trans(List<IfEventQueue> ifEventQueueList) {
        if (CollectionUtils.isEmpty(ifEventQueueList)) {
            return;
        }
        for (IfEventQueue ifEventQueue : ifEventQueueList) {
            processSingleH2trans(ifEventQueue);
        }
    }

    // --- [공통 단건 H2trans 처리 내부 메서드] ---
    private void processSingleH2trans(IfEventQueue ifEventQueue) {
        IfEventQueueDto dto = null;
        try {
            dto = objectMapper.readValue(ifEventQueue.getPayload(), IfEventQueueDto.class);
        } catch (Exception e) {
            log.error("IfEventQueue 페이로드 역직렬화 중 에러 발생. payload: {}", ifEventQueue.getPayload(), e);
            throw new RuntimeException(e);
        }

        Long currentTransCode = Long.parseLong(dto.getTransactionCode());
        String orderId = ifEventQueue.getOrderId();

        // 1. 선행 이벤트 존재 여부 동적 검증
        if (GALTransportStatusRule.hasPrecondition(currentTransCode)) {
            List<Long> requiredPreCodes = GALTransportStatusRule.getRequiredPreTransCodes(currentTransCode);
            boolean existsPreCondition = h2TransJpaRepository.existsByCTransTyInAndCOrderId(requiredPreCodes, orderId);

            if (!existsPreCondition) {
                log.warn("[보고 대기] 선행 데이터 미존재. currentTransCode: {}, required: {}, orderId: {}",
                        currentTransCode, requiredPreCodes, orderId);
                throw new PreconditionNotMetException(
                        String.format("선행 트랜잭션%s이 존재하지 않습니다. (cOrderId: %s)", requiredPreCodes, orderId)
                );
            }
        }

        String virtualCarrierName = dto.getVirtualCarrierName();
        BigDecimal actualWeight = null;

        if (dto.getActualWeight() != null && !dto.getActualWeight().trim().isEmpty()) {
            actualWeight = new BigDecimal(dto.getActualWeight());
        }

        LocalDateTime now = ifEventQueue.getCreateTime();

        IdocEntity newIdoc = buildBaseIdoc(now);
        newIdoc = idocJpaRepository.save(newIdoc);

        H2TransEntity h2TransEntity =
                H2TransEntity
                        .builder()
                        .lineId(h2TransJpaRepository.findMaxLineId())
                        .idocId(newIdoc.getLineId())
                        .usrMod(SystemName.MNG.getValue())
                        .pgmMod(SystemName.MNG.getValue())
                        .modCnt(0)
                        .dataCode(IdocDataCode.DATA_CODE01.getValue())
                        .cTransTy(Long.parseLong(dto.getTransactionCode()))
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
                        .cErrDsc(dto.getErrorText())
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
    public Page<GALH2TransInterfaceResponse> getH2TransInterfaceList(GALH2TransInterfaceSearchCondition condition, Pageable pageable) {
        return galInterfaceRepository.getH2TransInterfaceList(condition,pageable);
    }

    @Override
    @Transactional(value = "db2TransactionManager")
    public Page<GALPartResponse> getPartList(GALPartSearchCondition condition, Pageable pageable) {
        return null;
    }
}