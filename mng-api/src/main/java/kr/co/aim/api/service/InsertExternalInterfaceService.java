package kr.co.aim.api.service;

import kr.co.aim.api.vo.insert.H2TransReportVo;
import kr.co.aim.api.vo.insert.TransportOrderContext;
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
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile({"scheduler"})
@ConditionalOnProperty(name = "factory.type", havingValue = "insert")
public class InsertExternalInterfaceService {

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

    private void reportH2trans(H2TransReportVo vo){
        saveTransportProgress(vo);
    }

    private void saveTransportProgress(H2TransReportVo report) {
        log.info("Reporting Status: {}", report.getStatus());
        LocalDateTime now = LocalDateTime.now().withNano(0);

        IdocEntity newIdoc = buildBaseIdoc(now);
        idocJpaRepository.save(newIdoc);

        H2TransEntity newTrans = buildBaseH2Trans(report);
        h2TransJpaRepository.save(newTrans);
    }


    @Transactional(value = "db2TransactionManager")
    public List<IdocEntity> selectByIdocTypIdsAndErrorCode(List<Long> idocTypIds, Integer errorCode) {
        return idocJpaRepository.findByIdocTypIdsAndErrorCode(idocTypIds, errorCode);
    }

    @Transactional(value = "db2TransactionManager")
    public List<H2OrderMEntity> selectH2OrderMEntityByIdocId(Long idocId) {
        return h2OrderMJpaRepository.findByIdocId(idocId);
    }

    @Transactional(value = "db2TransactionManager")
    public List<H2OrderDEntity> selectH2OrderDEntityByIdocId(Long idocId) {
        return h2OrderDJpaRepository.findByIdocIdOrderByLineIdAsc(idocId);
    }

    @Transactional(value = "db2TransactionManager")
    public IdocEntity transferCompleted(Long idocId) {
        IdocEntity idoc = idocJpaRepository.findByLineId(idocId)
                .orElseThrow(() -> new RuntimeException("IDOC을 찾을 수 없습니다."));
        idoc.setErrorCode(Integer.parseInt(IdocErrorCode.Processed.getValue()));
        idoc.setDtimeMod(LocalDateTime.now().withNano(0));
        return idocJpaRepository.save(idoc);
    }

    @Transactional(value = "db2TransactionManager")
    public IdocEntity transferFail(Long idocId) {
        IdocEntity idoc = idocJpaRepository.findByLineId(idocId)
                .orElseThrow(() -> new RuntimeException("IDOC을 찾을 수 없습니다."));
        idoc.setErrorCode(Integer.parseInt(IdocErrorCode.Error.getValue()));
        idoc.setDtimeMod(LocalDateTime.now().withNano(0));
        return idocJpaRepository.save(idoc);
    }


}