package kr.co.aim.api.service;

import kr.co.aim.api.dto.*;
import kr.co.aim.api.vo.H2OrderDetailRelocationVo;
import kr.co.aim.api.vo.H2OrderDetailVo;
import kr.co.aim.common.enums.IdocErrorCode;
import kr.co.aim.common.enums.TransportStatus;
import kr.co.aim.infra.persistence.entity.TransportOrderEntity;
import kr.co.aim.infra.persistence.entitydb2.H2OrderDEntity;
import kr.co.aim.infra.persistence.entitydb2.H2OrderMEntity;
import kr.co.aim.infra.persistence.entitydb2.H2TransEntity;
import kr.co.aim.infra.persistence.entitydb2.IdocEntity;
import kr.co.aim.infra.persistence.springdatajpadb2.H2OrderDJpaRepository;
import kr.co.aim.infra.persistence.springdatajpadb2.H2OrderMJpaRepository;
import kr.co.aim.infra.persistence.springdatajpadb2.H2TransJpaRepository;
import kr.co.aim.infra.persistence.springdatajpadb2.IdocJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile({"scheduler","simulator"})
public class ExternalInterfaceService {

    private final IdocJpaRepository idocJpaRepository;
    private final H2OrderMJpaRepository h2OrderMJpaRepository;
    private final H2OrderDJpaRepository h2OrderDJpaRepository;
    private final H2TransJpaRepository h2TransJpaRepository;

    @Transactional( value = "db2TransactionManager")
    public IdocEntity transferedIdocId(Long idocId) {
        log.info("transferIdocId");
        IdocEntity idoc = idocJpaRepository.findByLineId(idocId)
                .orElseThrow(() -> new RuntimeException("IDOC을 찾을 수 없습니다."));
        idoc.setErrorCode( Integer.parseInt( IdocErrorCode.Processed.getValue() ) );
        return idocJpaRepository.save(idoc);
    }

    @Transactional(value = "db2TransactionManager")
    public void acceptOutbound(TransportOrderEntity transportOrder, IdocEntity selectedIdocEntity, H2OrderMEntity selectedH2OrderMEntity, H2OrderDEntity selectedH2OrderDEntity) {
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0);
        log.info("acceptOutbound");
        // 1. 새로운 IDOC Line ID 생성 (Max + 1)
        Long nextIdocLineId = idocJpaRepository.findMaxLineId() + 1;

        IdocEntity idocEntity =
                IdocEntity.builder()
                        .lineId(nextIdocLineId)
                        .idocTypId(selectedIdocEntity.getIdocTypId())
                        //.state()
                        //.errorCode()
                        .source(selectedIdocEntity.getDestination())
                        .destination(selectedIdocEntity.getSource())
                        //.tidId()
                        .docNum(selectedIdocEntity.getDocNum())
                        //.queueName()
                        //.partnerType()
                        //.partnerName()
                        //.partnerPort()
                        //.msgVariant()
                        //.arcKey()
                        .dtimeCre(localDateTime)
                        .dtimeMod(localDateTime)
                        //.usrMod()
                        //.pgmMod()
                        //.modCnt()
                        .build();

        idocJpaRepository.save(idocEntity);

        // 2. 새로운 H2TRANS Line ID 생성 (Max + 1)
        Long nextTransLineId = h2TransJpaRepository.findMaxLineId() + 1;

        H2TransEntity h2TransEntity = H2TransEntity.builder()
                .lineId(nextTransLineId)
                .idocId(idocEntity.getLineId())
                .dtimeCre(localDateTime)
                .dtimeMod(localDateTime)
                //.usrMod()
                //.pgmMod()
                //.modCnt()
                .dataCode(10L)
                .cTransTy( Long.parseLong(TransportStatus.Accept.getValue()))
                .cClient("999")
                .cOrderId(selectedH2OrderMEntity.getCOrderId())
                .cOrderTy(selectedH2OrderMEntity.getCOrderTy())
                //.cErrId()
                //.cText1()
                //.cTCode()
                //.cOrderLn()
                .cGaId(selectedH2OrderMEntity.getCGalId())
                .cGalWhs(selectedH2OrderMEntity.getCGalWhs())
                .cCoId(selectedH2OrderDEntity.getCCoId())
                .cGrWgAct(30L)
                .cReqZone(selectedH2OrderDEntity.getCZone())
                .cZone("")
                //.cLocId()
                //.cErrDsc()
                .cWcId(selectedH2OrderMEntity.getCWcId())
                .build();
        h2TransJpaRepository.save(h2TransEntity);
    }

    @Transactional(value = "db2TransactionManager")
    public void acceptInbound(TransportOrderEntity transportOrder, IdocEntity selectedIdocEntity, H2OrderMEntity selectedH2OrderMEntity, H2OrderDEntity selectedH2OrderDEntity) {
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0);
        log.info("acceptInbound");
        // 1. 새로운 IDOC Line ID 생성 (Max + 1)
        Long nextIdocLineId = idocJpaRepository.findMaxLineId() + 1;

        IdocEntity idocEntity =
                IdocEntity.builder()
                        .lineId(nextIdocLineId)
                        .idocTypId(selectedIdocEntity.getIdocTypId())
                        //.state()
                        //.errorCode()
                        .source(selectedIdocEntity.getDestination())
                        .destination(selectedIdocEntity.getSource())
                        //.tidId()
                        .docNum(selectedIdocEntity.getDocNum())
                        //.queueName()
                        //.partnerType()
                        //.partnerName()
                        //.partnerPort()
                        //.msgVariant()
                        //.arcKey()
                        .dtimeCre(localDateTime)
                        .dtimeMod(localDateTime)
                        //.usrMod()
                        //.pgmMod()
                        //.modCnt()
                        .build();

        idocJpaRepository.save(idocEntity);

        // 2. 새로운 H2TRANS Line ID 생성 (Max + 1)
        Long nextTransLineId = h2TransJpaRepository.findMaxLineId() + 1;

        H2TransEntity h2TransEntity = H2TransEntity.builder()
                .lineId(nextTransLineId)
                .idocId(idocEntity.getLineId())
                .dtimeCre(localDateTime)
                .dtimeMod(localDateTime)
                //.usrMod()
                //.pgmMod()
                //.modCnt()
                .dataCode(10L)
                .cTransTy( Long.parseLong(TransportStatus.Accept.getValue()))
                .cClient("999")
                .cOrderId(selectedH2OrderMEntity.getCOrderId())
                .cOrderTy(selectedH2OrderMEntity.getCOrderTy())
                //.cErrId()
                //.cText1()
                //.cTCode()
                //.cOrderLn()
                .cGaId(selectedH2OrderMEntity.getCGalId())
                .cGalWhs(selectedH2OrderMEntity.getCGalWhs())
                .cCoId(selectedH2OrderDEntity.getCCoId())
                .cGrWgAct(30L)
                .cReqZone(selectedH2OrderDEntity.getCZone())
                .cZone("B")
                //.cLocId()
                //.cErrDsc()
                .cWcId(selectedH2OrderMEntity.getCWcId())
                .build();
        h2TransJpaRepository.save(h2TransEntity);
    }

    @Transactional(value = "db2TransactionManager")
    public void workStationEmptyInbound(TransportOrderEntity transportOrder, IdocEntity selectedIdocEntity, H2OrderMEntity selectedH2OrderMEntity, H2OrderDEntity selectedH2OrderDEntity) {
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0);
        log.info("workStationEmptyInbound");
        // 1. 새로운 IDOC Line ID 생성 (Max + 1)
        Long nextIdocLineId = idocJpaRepository.findMaxLineId() + 1;

        IdocEntity idocEntity =
                IdocEntity.builder()
                        .lineId(nextIdocLineId)
                        .idocTypId(selectedIdocEntity.getIdocTypId())
                        //.state()
                        //.errorCode()
                        .source(selectedIdocEntity.getDestination())
                        .destination(selectedIdocEntity.getSource())
                        //.tidId()
                        .docNum(selectedIdocEntity.getDocNum())
                        //.queueName()
                        //.partnerType()
                        //.partnerName()
                        //.partnerPort()
                        //.msgVariant()
                        //.arcKey()
                        .dtimeCre(localDateTime)
                        .dtimeMod(localDateTime)
                        //.usrMod()
                        //.pgmMod()
                        //.modCnt()
                        .build();

        idocJpaRepository.save(idocEntity);

        // 2. 새로운 H2TRANS Line ID 생성 (Max + 1)
        Long nextTransLineId = h2TransJpaRepository.findMaxLineId() + 1;

        H2TransEntity h2TransEntity = H2TransEntity.builder()
                .lineId(nextTransLineId)
                .idocId(idocEntity.getLineId())
                .dtimeCre(localDateTime)
                .dtimeMod(localDateTime)
                //.usrMod()
                //.pgmMod()
                //.modCnt()
                .dataCode(10L)
                .cTransTy( Long.parseLong(TransportStatus.WorkstationEmpty.getValue()))
                .cClient("999")
                .cOrderId(selectedH2OrderMEntity.getCOrderId())
                .cOrderTy(selectedH2OrderMEntity.getCOrderTy())
                //.cErrId()
                //.cText1()
                //.cTCode()
                //.cOrderLn()
                .cGaId(selectedH2OrderMEntity.getCGalId())
                .cGalWhs(selectedH2OrderMEntity.getCGalWhs())
                .cCoId(selectedH2OrderDEntity.getCCoId())
                .cGrWgAct(30L)
                .cReqZone(selectedH2OrderDEntity.getCZone())
                .cZone("B")
                //.cLocId()
                //.cErrDsc()
                .cWcId(selectedH2OrderMEntity.getCWcId())
                .build();
        h2TransJpaRepository.save(h2TransEntity);
    }

    @Transactional(value = "db2TransactionManager")
    public void arrivedWorkstationErrorInbound(TransportOrderEntity transportOrder, IdocEntity selectedIdocEntity, H2OrderMEntity selectedH2OrderMEntity, H2OrderDEntity selectedH2OrderDEntity) {
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0);
        log.info("workStationEmptyInbound");
        // 1. 새로운 IDOC Line ID 생성 (Max + 1)
        Long nextIdocLineId = idocJpaRepository.findMaxLineId() + 1;

        IdocEntity idocEntity =
                IdocEntity.builder()
                        .lineId(nextIdocLineId)
                        .idocTypId(selectedIdocEntity.getIdocTypId())
                        //.state()
                        //.errorCode()
                        .source(selectedIdocEntity.getDestination())
                        .destination(selectedIdocEntity.getSource())
                        //.tidId()
                        .docNum(selectedIdocEntity.getDocNum())
                        //.queueName()
                        //.partnerType()
                        //.partnerName()
                        //.partnerPort()
                        //.msgVariant()
                        //.arcKey()
                        .dtimeCre(localDateTime)
                        .dtimeMod(localDateTime)
                        //.usrMod()
                        //.pgmMod()
                        //.modCnt()
                        .build();

        idocJpaRepository.save(idocEntity);

        // 2. 새로운 H2TRANS Line ID 생성 (Max + 1)
        Long nextTransLineId = h2TransJpaRepository.findMaxLineId() + 1;

        H2TransEntity h2TransEntity = H2TransEntity.builder()
                .lineId(nextTransLineId)
                .idocId(idocEntity.getLineId())
                .dtimeCre(localDateTime)
                .dtimeMod(localDateTime)
                //.usrMod()
                //.pgmMod()
                //.modCnt()
                .dataCode(10L)
                .cTransTy( Long.parseLong(TransportStatus.ArrivedAtWorkstationWithError.getValue()))
                .cClient("999")
                .cOrderId(selectedH2OrderMEntity.getCOrderId())
                .cOrderTy(selectedH2OrderMEntity.getCOrderTy())
                //.cErrId()
                //.cText1()
                //.cTCode()
                //.cOrderLn()
                .cGaId(selectedH2OrderMEntity.getCGalId())
                .cGalWhs(selectedH2OrderMEntity.getCGalWhs())
                .cCoId(selectedH2OrderDEntity.getCCoId())
                .cGrWgAct(30L)
                .cReqZone(selectedH2OrderDEntity.getCZone())
                .cZone("")
                //.cLocId()
                //.cErrDsc()
                .cWcId(selectedH2OrderMEntity.getCWcId())
                .build();
        h2TransJpaRepository.save(h2TransEntity);
    }

    @Transactional(value = "db2TransactionManager")
    public void errorTextInbound(String errorText,TransportOrderEntity transportOrder, IdocEntity selectedIdocEntity, H2OrderMEntity selectedH2OrderMEntity, H2OrderDEntity selectedH2OrderDEntity) {
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0);
        log.info("workStationEmptyInbound");
        // 1. 새로운 IDOC Line ID 생성 (Max + 1)
        Long nextIdocLineId = idocJpaRepository.findMaxLineId() + 1;

        IdocEntity idocEntity =
                IdocEntity.builder()
                        .lineId(nextIdocLineId)
                        .idocTypId(selectedIdocEntity.getIdocTypId())
                        //.state()
                        //.errorCode()
                        .source(selectedIdocEntity.getDestination())
                        .destination(selectedIdocEntity.getSource())
                        //.tidId()
                        .docNum(selectedIdocEntity.getDocNum())
                        //.queueName()
                        //.partnerType()
                        //.partnerName()
                        //.partnerPort()
                        //.msgVariant()
                        //.arcKey()
                        .dtimeCre(localDateTime)
                        .dtimeMod(localDateTime)
                        //.usrMod()
                        //.pgmMod()
                        //.modCnt()
                        .build();

        idocJpaRepository.save(idocEntity);

        // 2. 새로운 H2TRANS Line ID 생성 (Max + 1)
        Long nextTransLineId = h2TransJpaRepository.findMaxLineId() + 1;

        H2TransEntity h2TransEntity = H2TransEntity.builder()
                .lineId(nextTransLineId)
                .idocId(idocEntity.getLineId())
                .dtimeCre(localDateTime)
                .dtimeMod(localDateTime)
                //.usrMod()
                //.pgmMod()
                //.modCnt()
                .dataCode(10L)
                .cTransTy( Long.parseLong(TransportStatus.ErrorText.getValue()))
                .cClient("999")
                .cOrderId(selectedH2OrderMEntity.getCOrderId())
                .cOrderTy(selectedH2OrderMEntity.getCOrderTy())
                //.cErrId()
                //.cText1()
                //.cTCode()
                //.cOrderLn()
                .cGaId(selectedH2OrderMEntity.getCGalId())
                .cGalWhs(selectedH2OrderMEntity.getCGalWhs())
                .cCoId(selectedH2OrderDEntity.getCCoId())
                .cGrWgAct(30L)
                .cReqZone(selectedH2OrderDEntity.getCZone())
                .cZone("")
                //.cLocId()
                .cErrDsc(errorText)
                .cWcId(selectedH2OrderMEntity.getCWcId())
                .build();
        h2TransJpaRepository.save(h2TransEntity);
    }

    @Transactional(value = "db2TransactionManager")
    public void carrierScannedInbound(TransportOrderEntity transportOrder, IdocEntity selectedIdocEntity, H2OrderMEntity selectedH2OrderMEntity, H2OrderDEntity selectedH2OrderDEntity) {
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0);
        log.info("workStationEmptyInbound");
        // 1. 새로운 IDOC Line ID 생성 (Max + 1)
        Long nextIdocLineId = idocJpaRepository.findMaxLineId() + 1;

        IdocEntity idocEntity =
                IdocEntity.builder()
                        .lineId(nextIdocLineId)
                        .idocTypId(selectedIdocEntity.getIdocTypId())
                        //.state()
                        //.errorCode()
                        .source(selectedIdocEntity.getDestination())
                        .destination(selectedIdocEntity.getSource())
                        //.tidId()
                        .docNum(selectedIdocEntity.getDocNum())
                        //.queueName()
                        //.partnerType()
                        //.partnerName()
                        //.partnerPort()
                        //.msgVariant()
                        //.arcKey()
                        .dtimeCre(localDateTime)
                        .dtimeMod(localDateTime)
                        //.usrMod()
                        //.pgmMod()
                        //.modCnt()
                        .build();

        idocJpaRepository.save(idocEntity);

        // 2. 새로운 H2TRANS Line ID 생성 (Max + 1)
        Long nextTransLineId = h2TransJpaRepository.findMaxLineId() + 1;

        H2TransEntity h2TransEntity = H2TransEntity.builder()
                .lineId(nextTransLineId)
                .idocId(idocEntity.getLineId())
                .dtimeCre(localDateTime)
                .dtimeMod(localDateTime)
                //.usrMod()
                //.pgmMod()
                //.modCnt()
                .dataCode(10L)
                .cTransTy( Long.parseLong(TransportStatus.CarrierScanned.getValue()))
                .cClient("999")
                .cOrderId(selectedH2OrderMEntity.getCOrderId())
                .cOrderTy(selectedH2OrderMEntity.getCOrderTy())
                //.cErrId()
                //.cText1()
                //.cTCode()
                //.cOrderLn()
                .cGaId(selectedH2OrderMEntity.getCGalId())
                .cGalWhs(selectedH2OrderMEntity.getCGalWhs())
                .cCoId(selectedH2OrderDEntity.getCCoId())
                .cGrWgAct(30L)
                .cReqZone(selectedH2OrderDEntity.getCZone())
                .cZone("")
                //.cLocId()
                //.cErrDsc()
                .cWcId(selectedH2OrderMEntity.getCWcId())
                .build();
        h2TransJpaRepository.save(h2TransEntity);
    }

    @Transactional(value = "db2TransactionManager")
    public void releaseOutbound(TransportOrderEntity transportOrder, IdocEntity selectedIdocEntity, H2OrderMEntity selectedH2OrderMEntity, H2OrderDEntity selectedH2OrderDEntity) {

        LocalDateTime localDateTime = LocalDateTime.now().withNano(0);
        log.info("releaseOutbound");
        // 1. 새로운 IDOC Line ID 생성 (Max + 1)
        Long nextIdocLineId = idocJpaRepository.findMaxLineId() + 1;

        IdocEntity idocEntity =
                IdocEntity.builder()
                        .lineId(nextIdocLineId)
                        .idocTypId(selectedIdocEntity.getIdocTypId())
                        //.state()
                        //.errorCode()
                        .source(selectedIdocEntity.getDestination())
                        .destination(selectedIdocEntity.getSource())
                        //.tidId()
                        .docNum(selectedIdocEntity.getDocNum())
                        //.queueName()
                        //.partnerType()
                        //.partnerName()
                        //.partnerPort()
                        //.msgVariant()
                        //.arcKey()
                        .dtimeCre(localDateTime)
                        .dtimeMod(localDateTime)
                        //.usrMod()
                        //.pgmMod()
                        //.modCnt()
                        .build();

        idocJpaRepository.save(idocEntity);

        // 2. 새로운 H2TRANS Line ID 생성 (Max + 1)
        Long nextTransLineId = h2TransJpaRepository.findMaxLineId() + 1;

        H2TransEntity h2TransEntity = H2TransEntity.builder()
                .lineId(nextTransLineId)
                .idocId(idocEntity.getLineId())
                .dtimeCre(localDateTime)
                .dtimeMod(localDateTime)
                //.usrMod()
                //.pgmMod()
                //.modCnt()
                .dataCode(10L)
                .cTransTy( Long.parseLong(TransportStatus.Released.getValue()))
                .cClient("999")
                .cOrderId(selectedH2OrderMEntity.getCOrderId())
                .cOrderTy(selectedH2OrderMEntity.getCOrderTy())
                //.cErrId()
                //.cText1()
                //.cTCode()
                //.cOrderLn()
                .cGaId(selectedH2OrderMEntity.getCGalId())
                .cGalWhs(selectedH2OrderMEntity.getCGalWhs())
                .cCoId(selectedH2OrderDEntity.getCCoId())
                .cGrWgAct(30L)
                .cReqZone(selectedH2OrderDEntity.getCZone())
                .cZone("B")
                //.cLocId()
                //.cErrDsc()
                .cWcId(selectedH2OrderMEntity.getCWcId())
                .build();
        h2TransJpaRepository.save(h2TransEntity);
    }
    @Transactional(value = "db2TransactionManager")
    public void internalRelocationOutbound(TransportOrderEntity transportOrder, IdocEntity selectedIdocEntity, H2OrderMEntity selectedH2OrderMEntity, H2OrderDEntity selectedH2OrderDEntity) {
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0);
        log.info("internalRelocationOutbound");
        // 1. 새로운 IDOC Line ID 생성 (Max + 1)
        Long nextIdocLineId = idocJpaRepository.findMaxLineId() + 1;

        IdocEntity idocEntity =
                IdocEntity.builder()
                        .lineId(nextIdocLineId)
                        .idocTypId(selectedIdocEntity.getIdocTypId())
                        //.state()
                        //.errorCode()
                        .source(selectedIdocEntity.getDestination())
                        .destination(selectedIdocEntity.getSource())
                        //.tidId()
                        .docNum(selectedIdocEntity.getDocNum())
                        //.queueName()
                        //.partnerType()
                        //.partnerName()
                        //.partnerPort()
                        //.msgVariant()
                        //.arcKey()
                        .dtimeCre(localDateTime)
                        .dtimeMod(localDateTime)
                        //.usrMod()
                        //.pgmMod()
                        //.modCnt()
                        .build();

        idocJpaRepository.save(idocEntity);

        // 2. 새로운 H2TRANS Line ID 생성 (Max + 1)
        Long nextTransLineId = h2TransJpaRepository.findMaxLineId() + 1;

        H2TransEntity h2TransEntity = H2TransEntity.builder()
                .lineId(nextTransLineId)
                .idocId(idocEntity.getLineId())
                .dtimeCre(localDateTime)
                .dtimeMod(localDateTime)
                //.usrMod()
                //.pgmMod()
                //.modCnt()
                .dataCode(10L)
                .cTransTy( Long.parseLong(TransportStatus.InternalRelocation.getValue()))
                .cClient("999")
                .cOrderId(selectedH2OrderMEntity.getCOrderId())
                .cOrderTy(selectedH2OrderMEntity.getCOrderTy())
                //.cErrId()
                //.cText1()
                //.cTCode()
                //.cOrderLn()
                .cGaId(selectedH2OrderMEntity.getCGalId())
                .cGalWhs(selectedH2OrderMEntity.getCGalWhs())
                .cCoId(selectedH2OrderDEntity.getCCoId())
                .cGrWgAct(30L)
                .cReqZone(selectedH2OrderDEntity.getCZone())
                .cZone("B")
                //.cLocId()
                //.cErrDsc()
                .cWcId(selectedH2OrderMEntity.getCWcId())
                .build();
        h2TransJpaRepository.save(h2TransEntity);
    }

    @Transactional(value = "db2TransactionManager")
    public void dropOnTunnelRelocation(TransportOrderEntity transportOrder, IdocEntity selectedIdocEntity, H2OrderMEntity selectedH2OrderMEntity, H2OrderDEntity selectedH2OrderDEntity) {
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0);
        log.info("dropOnTunnelRelocation");
        // 1. 새로운 IDOC Line ID 생성 (Max + 1)
        Long nextIdocLineId = idocJpaRepository.findMaxLineId() + 1;

        IdocEntity idocEntity =
                IdocEntity.builder()
                        .lineId(nextIdocLineId)
                        .idocTypId(selectedIdocEntity.getIdocTypId())
                        //.state()
                        //.errorCode()
                        .source(selectedIdocEntity.getDestination())
                        .destination(selectedIdocEntity.getSource())
                        //.tidId()
                        .docNum(selectedIdocEntity.getDocNum())
                        //.queueName()
                        //.partnerType()
                        //.partnerName()
                        //.partnerPort()
                        //.msgVariant()
                        //.arcKey()
                        .dtimeCre(localDateTime)
                        .dtimeMod(localDateTime)
                        //.usrMod()
                        //.pgmMod()
                        //.modCnt()
                        .build();

        idocJpaRepository.save(idocEntity);

        // 2. 새로운 H2TRANS Line ID 생성 (Max + 1)
        Long nextTransLineId = h2TransJpaRepository.findMaxLineId() + 1;

        H2TransEntity h2TransEntity = H2TransEntity.builder()
                .lineId(nextTransLineId)
                .idocId(idocEntity.getLineId())
                .dtimeCre(localDateTime)
                .dtimeMod(localDateTime)
                //.usrMod()
                //.pgmMod()
                //.modCnt()
                .dataCode(10L)
                .cTransTy( Long.parseLong(TransportStatus.DroppedOnTunnelConveyor.getValue()))
                .cClient("999")
                .cOrderId(selectedH2OrderMEntity.getCOrderId())
                .cOrderTy(selectedH2OrderMEntity.getCOrderTy())
                //.cErrId()
                //.cText1()
                //.cTCode()
                //.cOrderLn()
                .cGaId(selectedH2OrderMEntity.getCGalId())
                .cGalWhs(selectedH2OrderMEntity.getCGalWhs())
                .cCoId(selectedH2OrderDEntity.getCCoId())
                .cGrWgAct(30L)
                .cReqZone(selectedH2OrderDEntity.getCZone())
                .cZone("B")
                //.cLocId()
                //.cErrDsc()
                .cWcId(selectedH2OrderMEntity.getCWcId())
                .build();
        h2TransJpaRepository.save(h2TransEntity);
    }

    @Transactional(value = "db2TransactionManager")
    public void outOfRackOutbound(TransportOrderEntity transportOrder, IdocEntity selectedIdocEntity, H2OrderMEntity selectedH2OrderMEntity, H2OrderDEntity selectedH2OrderDEntity) {
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0);
        log.info("outOfRackOutbound");
        // 1. 새로운 IDOC Line ID 생성 (Max + 1)
        Long nextIdocLineId = idocJpaRepository.findMaxLineId() + 1;

        IdocEntity idocEntity =
                IdocEntity.builder()
                        .lineId(nextIdocLineId)
                        .idocTypId(selectedIdocEntity.getIdocTypId())
                        //.state()
                        //.errorCode()
                        .source(selectedIdocEntity.getDestination())
                        .destination(selectedIdocEntity.getSource())
                        //.tidId()
                        .docNum(selectedIdocEntity.getDocNum())
                        //.queueName()
                        //.partnerType()
                        //.partnerName()
                        //.partnerPort()
                        //.msgVariant()
                        //.arcKey()
                        .dtimeCre(localDateTime)
                        .dtimeMod(localDateTime)
                        //.usrMod()
                        //.pgmMod()
                        //.modCnt()
                        .build();

        idocJpaRepository.save(idocEntity);

        // 2. 새로운 H2TRANS Line ID 생성 (Max + 1)
        Long nextTransLineId = h2TransJpaRepository.findMaxLineId() + 1;

        H2TransEntity h2TransEntity = H2TransEntity.builder()
                .lineId(nextTransLineId)
                .idocId(idocEntity.getLineId())
                .dtimeCre(localDateTime)
                .dtimeMod(localDateTime)
                //.usrMod()
                //.pgmMod()
                //.modCnt()
                .dataCode(10L)
                .cTransTy( Long.parseLong(TransportStatus.OutOfRack.getValue()))
                .cClient("999")
                .cOrderId(selectedH2OrderMEntity.getCOrderId())
                .cOrderTy(selectedH2OrderMEntity.getCOrderTy())
                //.cErrId()
                //.cText1()
                //.cTCode()
                //.cOrderLn()
                .cGaId(selectedH2OrderMEntity.getCGalId())
                .cGalWhs(selectedH2OrderMEntity.getCGalWhs())
                .cCoId(selectedH2OrderDEntity.getCCoId())
                .cGrWgAct(30L)
                .cReqZone(selectedH2OrderDEntity.getCZone())
                .cZone("B")
                //.cLocId()
                //.cErrDsc()
                .cWcId(selectedH2OrderMEntity.getCWcId())
                .build();
        h2TransJpaRepository.save(h2TransEntity);
    }

    @Transactional(value = "db2TransactionManager")
    public void arrivedAtWorkStationOutbound(TransportOrderEntity transportOrder, IdocEntity selectedIdocEntity, H2OrderMEntity selectedH2OrderMEntity, H2OrderDEntity selectedH2OrderDEntity) {
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0);
        log.info("arrivedAtWorkStationOutbound");
        // 1. 새로운 IDOC Line ID 생성 (Max + 1)
        Long nextIdocLineId = idocJpaRepository.findMaxLineId() + 1;

        IdocEntity idocEntity =
                IdocEntity.builder()
                        .lineId(nextIdocLineId)
                        .idocTypId(selectedIdocEntity.getIdocTypId())
                        //.state()
                        //.errorCode()
                        .source(selectedIdocEntity.getDestination())
                        .destination(selectedIdocEntity.getSource())
                        //.tidId()
                        .docNum(selectedIdocEntity.getDocNum())
                        //.queueName()
                        //.partnerType()
                        //.partnerName()
                        //.partnerPort()
                        //.msgVariant()
                        //.arcKey()
                        .dtimeCre(localDateTime)
                        .dtimeMod(localDateTime)
                        //.usrMod()
                        //.pgmMod()
                        //.modCnt()
                        .build();

        idocJpaRepository.save(idocEntity);

        // 2. 새로운 H2TRANS Line ID 생성 (Max + 1)
        Long nextTransLineId = h2TransJpaRepository.findMaxLineId() + 1;

        H2TransEntity h2TransEntity = H2TransEntity.builder()
                .lineId(nextTransLineId)
                .idocId(idocEntity.getLineId())
                .dtimeCre(localDateTime)
                .dtimeMod(localDateTime)
                //.usrMod()
                //.pgmMod()
                //.modCnt()
                .dataCode(10L)
                .cTransTy( Long.parseLong(TransportStatus.ArrivedAtWorkStation.getValue()))
                .cClient("999")
                .cOrderId(selectedH2OrderMEntity.getCOrderId())
                .cOrderTy(selectedH2OrderMEntity.getCOrderTy())
                //.cErrId()
                //.cText1()
                //.cTCode()
                //.cOrderLn()
                .cGaId(selectedH2OrderMEntity.getCGalId())
                .cGalWhs(selectedH2OrderMEntity.getCGalWhs())
                .cCoId(selectedH2OrderDEntity.getCCoId())
                .cGrWgAct(30L)
                .cReqZone(selectedH2OrderDEntity.getCZone())
                .cZone("B")
                //.cLocId()
                //.cErrDsc()
                .cWcId(selectedH2OrderMEntity.getCWcId())
                .build();
        h2TransJpaRepository.save(h2TransEntity);
    }

    @Transactional(value = "db2TransactionManager")
    public void completedInbound(TransportOrderEntity transportOrder, IdocEntity selectedIdocEntity, H2OrderMEntity selectedH2OrderMEntity, H2OrderDEntity selectedH2OrderDEntity) {
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0);
        log.info("completedOutbound");
        // 1. 새로운 IDOC Line ID 생성 (Max + 1)
        Long nextIdocLineId = idocJpaRepository.findMaxLineId() + 1;

        IdocEntity idocEntity =
                IdocEntity.builder()
                        .lineId(nextIdocLineId)
                        .idocTypId(selectedIdocEntity.getIdocTypId())
                        //.state()
                        //.errorCode()
                        .source(selectedIdocEntity.getDestination())
                        .destination(selectedIdocEntity.getSource())
                        //.tidId()
                        .docNum(selectedIdocEntity.getDocNum())
                        //.queueName()
                        //.partnerType()
                        //.partnerName()
                        //.partnerPort()
                        //.msgVariant()
                        //.arcKey()
                        .dtimeCre(localDateTime)
                        .dtimeMod(localDateTime)
                        //.usrMod()
                        //.pgmMod()
                        //.modCnt()
                        .build();

        idocJpaRepository.save(idocEntity);

        // 2. 새로운 H2TRANS Line ID 생성 (Max + 1)
        Long nextTransLineId = h2TransJpaRepository.findMaxLineId() + 1;

        H2TransEntity h2TransEntity = H2TransEntity.builder()
                .lineId(nextTransLineId)
                .idocId(idocEntity.getLineId())
                .dtimeCre(localDateTime)
                .dtimeMod(localDateTime)
                //.usrMod()
                //.pgmMod()
                //.modCnt()
                .dataCode(10L)
                .cTransTy( Long.parseLong(TransportStatus.OrderDone_Inbound.getValue()))
                .cClient("999")
                .cOrderId(selectedH2OrderMEntity.getCOrderId())
                .cOrderTy(selectedH2OrderMEntity.getCOrderTy())
                //.cErrId()
                //.cText1()
                //.cTCode()
                //.cOrderLn()
                .cGaId(selectedH2OrderMEntity.getCGalId())
                .cGalWhs(selectedH2OrderMEntity.getCGalWhs())
                .cCoId(selectedH2OrderDEntity.getCCoId())
                .cGrWgAct(30L)
                .cReqZone(selectedH2OrderDEntity.getCZone())
                .cZone("B")
                //.cLocId()
                //.cErrDsc()
                .cWcId(selectedH2OrderMEntity.getCWcId())
                .build();
        h2TransJpaRepository.save(h2TransEntity);
    }

    @Transactional(value = "db2TransactionManager")
    public void completedRelocation(TransportOrderEntity transportOrder, IdocEntity selectedIdocEntity, H2OrderMEntity selectedH2OrderMEntity, H2OrderDEntity selectedH2OrderDEntity) {
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0);
        log.info("completedOutbound");
        // 1. 새로운 IDOC Line ID 생성 (Max + 1)
        Long nextIdocLineId = idocJpaRepository.findMaxLineId() + 1;

        IdocEntity idocEntity =
                IdocEntity.builder()
                        .lineId(nextIdocLineId)
                        .idocTypId(selectedIdocEntity.getIdocTypId())
                        //.state()
                        //.errorCode()
                        .source(selectedIdocEntity.getDestination())
                        .destination(selectedIdocEntity.getSource())
                        //.tidId()
                        .docNum(selectedIdocEntity.getDocNum())
                        //.queueName()
                        //.partnerType()
                        //.partnerName()
                        //.partnerPort()
                        //.msgVariant()
                        //.arcKey()
                        .dtimeCre(localDateTime)
                        .dtimeMod(localDateTime)
                        //.usrMod()
                        //.pgmMod()
                        //.modCnt()
                        .build();

        idocJpaRepository.save(idocEntity);

        // 2. 새로운 H2TRANS Line ID 생성 (Max + 1)
        Long nextTransLineId = h2TransJpaRepository.findMaxLineId() + 1;

        H2TransEntity h2TransEntity = H2TransEntity.builder()
                .lineId(nextTransLineId)
                .idocId(idocEntity.getLineId())
                .dtimeCre(localDateTime)
                .dtimeMod(localDateTime)
                //.usrMod()
                //.pgmMod()
                //.modCnt()
                .dataCode(10L)
                .cTransTy( Long.parseLong(TransportStatus.OrderDone_Relocation.getValue()))
                .cClient("999")
                .cOrderId(selectedH2OrderMEntity.getCOrderId())
                .cOrderTy(selectedH2OrderMEntity.getCOrderTy())
                //.cErrId()
                //.cText1()
                //.cTCode()
                //.cOrderLn()
                .cGaId(selectedH2OrderMEntity.getCGalId())
                .cGalWhs(selectedH2OrderMEntity.getCGalWhs())
                .cCoId(selectedH2OrderDEntity.getCCoId())
                .cGrWgAct(30L)
                .cReqZone(selectedH2OrderDEntity.getCZone())
                .cZone("B")
                //.cLocId()
                //.cErrDsc()
                .cWcId(selectedH2OrderMEntity.getCWcId())
                .build();
        h2TransJpaRepository.save(h2TransEntity);
    }

    @Transactional(value = "db2TransactionManager")
    public void completedOutbound(TransportOrderEntity transportOrder, IdocEntity selectedIdocEntity, H2OrderMEntity selectedH2OrderMEntity, H2OrderDEntity selectedH2OrderDEntity) {
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0);
        log.info("completedOutbound");
        // 1. 새로운 IDOC Line ID 생성 (Max + 1)
        Long nextIdocLineId = idocJpaRepository.findMaxLineId() + 1;

        IdocEntity idocEntity =
                IdocEntity.builder()
                        .lineId(nextIdocLineId)
                        .idocTypId(selectedIdocEntity.getIdocTypId())
                        //.state()
                        //.errorCode()
                        .source(selectedIdocEntity.getDestination())
                        .destination(selectedIdocEntity.getSource())
                        //.tidId()
                        .docNum(selectedIdocEntity.getDocNum())
                        //.queueName()
                        //.partnerType()
                        //.partnerName()
                        //.partnerPort()
                        //.msgVariant()
                        //.arcKey()
                        .dtimeCre(localDateTime)
                        .dtimeMod(localDateTime)
                        //.usrMod()
                        //.pgmMod()
                        //.modCnt()
                        .build();

        idocJpaRepository.save(idocEntity);

        // 2. 새로운 H2TRANS Line ID 생성 (Max + 1)
        Long nextTransLineId = h2TransJpaRepository.findMaxLineId() + 1;

        H2TransEntity h2TransEntity = H2TransEntity.builder()
                .lineId(nextTransLineId)
                .idocId(idocEntity.getLineId())
                .dtimeCre(localDateTime)
                .dtimeMod(localDateTime)
                //.usrMod()
                //.pgmMod()
                //.modCnt()
                .dataCode(10L)
                .cTransTy( Long.parseLong(TransportStatus.OrderDone_Outbound.getValue()))
                .cClient("999")
                .cOrderId(selectedH2OrderMEntity.getCOrderId())
                .cOrderTy(selectedH2OrderMEntity.getCOrderTy())
                //.cErrId()
                //.cText1()
                //.cTCode()
                //.cOrderLn()
                .cGaId(selectedH2OrderMEntity.getCGalId())
                .cGalWhs(selectedH2OrderMEntity.getCGalWhs())
                .cCoId(selectedH2OrderDEntity.getCCoId())
                .cGrWgAct(30L)
                .cReqZone(selectedH2OrderDEntity.getCZone())
                .cZone("B")
                //.cLocId()
                //.cErrDsc()
                .cWcId(selectedH2OrderMEntity.getCWcId())
                .build();
        h2TransJpaRepository.save(h2TransEntity);
    }

    @Transactional(value = "db2TransactionManager")
    public void takeOffOutbound(TransportOrderEntity transportOrder, IdocEntity selectedIdocEntity, H2OrderMEntity selectedH2OrderMEntity, H2OrderDEntity selectedH2OrderDEntity) {
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0);
        log.info("takeOffOutbound");
        // 1. 새로운 IDOC Line ID 생성 (Max + 1)
        Long nextIdocLineId = idocJpaRepository.findMaxLineId() + 1;

        IdocEntity idocEntity =
                IdocEntity.builder()
                        .lineId(nextIdocLineId)
                        .idocTypId(selectedIdocEntity.getIdocTypId())
                        //.state()
                        //.errorCode()
                        .source(selectedIdocEntity.getDestination())
                        .destination(selectedIdocEntity.getSource())
                        //.tidId()
                        .docNum(selectedIdocEntity.getDocNum())
                        //.queueName()
                        //.partnerType()
                        //.partnerName()
                        //.partnerPort()
                        //.msgVariant()
                        //.arcKey()
                        .dtimeCre(localDateTime)
                        .dtimeMod(localDateTime)
                        //.usrMod()
                        //.pgmMod()
                        //.modCnt()
                        .build();

        idocJpaRepository.save(idocEntity);

        // 2. 새로운 H2TRANS Line ID 생성 (Max + 1)
        Long nextTransLineId = h2TransJpaRepository.findMaxLineId() + 1;

        H2TransEntity h2TransEntity = H2TransEntity.builder()
                .lineId(nextTransLineId)
                .idocId(idocEntity.getLineId())
                .dtimeCre(localDateTime)
                .dtimeMod(localDateTime)
                //.usrMod()
                //.pgmMod()
                //.modCnt()
                .dataCode(10L)
                .cTransTy( Long.parseLong(TransportStatus.TakeOff.getValue()))
                .cClient("999")
                .cOrderId(selectedH2OrderMEntity.getCOrderId())
                .cOrderTy(selectedH2OrderMEntity.getCOrderTy())
                //.cErrId()
                //.cText1()
                //.cTCode()
                //.cOrderLn()
                .cGaId(selectedH2OrderMEntity.getCGalId())
                .cGalWhs(selectedH2OrderMEntity.getCGalWhs())
                .cCoId(selectedH2OrderDEntity.getCCoId())
                .cGrWgAct(30L)
                .cReqZone(selectedH2OrderDEntity.getCZone())
                .cZone("B")
                //.cLocId()
                //.cErrDsc()
                .cWcId(selectedH2OrderMEntity.getCWcId())
                .build();
        h2TransJpaRepository.save(h2TransEntity);
    }

    @Transactional(value = "db2TransactionManager")
    public void binEmptyOutbound(TransportOrderEntity transportOrder, IdocEntity selectedIdocEntity, H2OrderMEntity selectedH2OrderMEntity, H2OrderDEntity selectedH2OrderDEntity) {
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0);
        log.info("outOfRackOutbound");
        // 1. 새로운 IDOC Line ID 생성 (Max + 1)
        Long nextIdocLineId = idocJpaRepository.findMaxLineId() + 1;

        IdocEntity idocEntity =
                IdocEntity.builder()
                        .lineId(nextIdocLineId)
                        .idocTypId(selectedIdocEntity.getIdocTypId())
                        //.state()
                        //.errorCode()
                        .source(selectedIdocEntity.getDestination())
                        .destination(selectedIdocEntity.getSource())
                        //.tidId()
                        .docNum(selectedIdocEntity.getDocNum())
                        //.queueName()
                        //.partnerType()
                        //.partnerName()
                        //.partnerPort()
                        //.msgVariant()
                        //.arcKey()
                        .dtimeCre(localDateTime)
                        .dtimeMod(localDateTime)
                        //.usrMod()
                        //.pgmMod()
                        //.modCnt()
                        .build();

        idocJpaRepository.save(idocEntity);

        // 2. 새로운 H2TRANS Line ID 생성 (Max + 1)
        Long nextTransLineId = h2TransJpaRepository.findMaxLineId() + 1;

        H2TransEntity h2TransEntity = H2TransEntity.builder()
                .lineId(nextTransLineId)
                .idocId(idocEntity.getLineId())
                .dtimeCre(localDateTime)
                .dtimeMod(localDateTime)
                //.usrMod()
                //.pgmMod()
                //.modCnt()
                .dataCode(10L)
                .cTransTy( Long.parseLong(TransportStatus.BinEmpty.getValue()))
                .cClient("999")
                .cOrderId(selectedH2OrderMEntity.getCOrderId())
                .cOrderTy(selectedH2OrderMEntity.getCOrderTy())
                //.cErrId()
                //.cText1()
                //.cTCode()
                //.cOrderLn()
                .cGaId(selectedH2OrderMEntity.getCGalId())
                .cGalWhs(selectedH2OrderMEntity.getCGalWhs())
                .cCoId(selectedH2OrderDEntity.getCCoId())
                .cGrWgAct(30L)
                .cReqZone(selectedH2OrderDEntity.getCZone())
                .cZone("B")
                //.cLocId()
                //.cErrDsc()
                .cWcId(selectedH2OrderMEntity.getCWcId())
                .build();
        h2TransJpaRepository.save(h2TransEntity);
    }

    @Transactional(value = "db2TransactionManager")
    public void shortageOutbound(TransportOrderEntity transportOrder, IdocEntity selectedIdocEntity, H2OrderMEntity selectedH2OrderMEntity, H2OrderDEntity selectedH2OrderDEntity) {
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0);
        log.info("outOfRackOutbound");
        // 1. 새로운 IDOC Line ID 생성 (Max + 1)
        Long nextIdocLineId = idocJpaRepository.findMaxLineId() + 1;

        IdocEntity idocEntity =
                IdocEntity.builder()
                        .lineId(nextIdocLineId)
                        .idocTypId(selectedIdocEntity.getIdocTypId())
                        //.state()
                        //.errorCode()
                        .source(selectedIdocEntity.getDestination())
                        .destination(selectedIdocEntity.getSource())
                        //.tidId()
                        .docNum(selectedIdocEntity.getDocNum())
                        //.queueName()
                        //.partnerType()
                        //.partnerName()
                        //.partnerPort()
                        //.msgVariant()
                        //.arcKey()
                        .dtimeCre(localDateTime)
                        .dtimeMod(localDateTime)
                        //.usrMod()
                        //.pgmMod()
                        //.modCnt()
                        .build();

        idocJpaRepository.save(idocEntity);

        // 2. 새로운 H2TRANS Line ID 생성 (Max + 1)
        Long nextTransLineId = h2TransJpaRepository.findMaxLineId() + 1;

        H2TransEntity h2TransEntity = H2TransEntity.builder()
                .lineId(nextTransLineId)
                .idocId(idocEntity.getLineId())
                .dtimeCre(localDateTime)
                .dtimeMod(localDateTime)
                //.usrMod()
                //.pgmMod()
                //.modCnt()
                .dataCode(10L)
                .cTransTy( Long.parseLong(TransportStatus.Shortage.getValue()))
                .cClient("999")
                .cOrderId(selectedH2OrderMEntity.getCOrderId())
                .cOrderTy(selectedH2OrderMEntity.getCOrderTy())
                //.cErrId()
                //.cText1()
                //.cTCode()
                //.cOrderLn()
                .cGaId(selectedH2OrderMEntity.getCGalId())
                .cGalWhs(selectedH2OrderMEntity.getCGalWhs())
                .cCoId(selectedH2OrderDEntity.getCCoId())
                .cGrWgAct(30L)
                .cReqZone(selectedH2OrderDEntity.getCZone())
                .cZone("B")
                //.cLocId()
                //.cErrDsc()
                .cWcId(selectedH2OrderMEntity.getCWcId())
                .build();
        h2TransJpaRepository.save(h2TransEntity);
    }

    @Transactional(value = "db2TransactionManager")
    public void notAllowedPickUpOutbound(TransportOrderEntity transportOrder, IdocEntity selectedIdocEntity, H2OrderMEntity selectedH2OrderMEntity, H2OrderDEntity selectedH2OrderDEntity) {
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0);
        log.info("outOfRackOutbound");
        // 1. 새로운 IDOC Line ID 생성 (Max + 1)
        Long nextIdocLineId = idocJpaRepository.findMaxLineId() + 1;

        IdocEntity idocEntity =
                IdocEntity.builder()
                        .lineId(nextIdocLineId)
                        .idocTypId(selectedIdocEntity.getIdocTypId())
                        //.state()
                        //.errorCode()
                        .source(selectedIdocEntity.getDestination())
                        .destination(selectedIdocEntity.getSource())
                        //.tidId()
                        .docNum(selectedIdocEntity.getDocNum())
                        //.queueName()
                        //.partnerType()
                        //.partnerName()
                        //.partnerPort()
                        //.msgVariant()
                        //.arcKey()
                        .dtimeCre(localDateTime)
                        .dtimeMod(localDateTime)
                        //.usrMod()
                        //.pgmMod()
                        //.modCnt()
                        .build();

        idocJpaRepository.save(idocEntity);

        // 2. 새로운 H2TRANS Line ID 생성 (Max + 1)
        Long nextTransLineId = h2TransJpaRepository.findMaxLineId() + 1;

        H2TransEntity h2TransEntity = H2TransEntity.builder()
                .lineId(nextTransLineId)
                .idocId(idocEntity.getLineId())
                .dtimeCre(localDateTime)
                .dtimeMod(localDateTime)
                //.usrMod()
                //.pgmMod()
                //.modCnt()
                .dataCode(10L)
                .cTransTy( Long.parseLong(TransportStatus.NotAllowedPickUp.getValue()))
                .cClient("999")
                .cOrderId(selectedH2OrderMEntity.getCOrderId())
                .cOrderTy(selectedH2OrderMEntity.getCOrderTy())
                //.cErrId()
                //.cText1()
                //.cTCode()
                //.cOrderLn()
                .cGaId(selectedH2OrderMEntity.getCGalId())
                .cGalWhs(selectedH2OrderMEntity.getCGalWhs())
                .cCoId(selectedH2OrderDEntity.getCCoId())
                .cGrWgAct(30L)
                .cReqZone(selectedH2OrderDEntity.getCZone())
                .cZone("B")
                //.cLocId()
                //.cErrDsc()
                .cWcId(selectedH2OrderMEntity.getCWcId())
                .build();
        h2TransJpaRepository.save(h2TransEntity);
    }

    @Transactional(value = "db2TransactionManager")
    public void notAllowedPickUpInbound(TransportOrderEntity transportOrder, IdocEntity selectedIdocEntity, H2OrderMEntity selectedH2OrderMEntity, H2OrderDEntity selectedH2OrderDEntity) {
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0);
        log.info("notAllowedPickUpInbound");
        // 1. 새로운 IDOC Line ID 생성 (Max + 1)
        Long nextIdocLineId = idocJpaRepository.findMaxLineId() + 1;

        IdocEntity idocEntity =
                IdocEntity.builder()
                        .lineId(nextIdocLineId)
                        .idocTypId(selectedIdocEntity.getIdocTypId())
                        //.state()
                        //.errorCode()
                        .source(selectedIdocEntity.getDestination())
                        .destination(selectedIdocEntity.getSource())
                        //.tidId()
                        .docNum(selectedIdocEntity.getDocNum())
                        //.queueName()
                        //.partnerType()
                        //.partnerName()
                        //.partnerPort()
                        //.msgVariant()
                        //.arcKey()
                        .dtimeCre(localDateTime)
                        .dtimeMod(localDateTime)
                        //.usrMod()
                        //.pgmMod()
                        //.modCnt()
                        .build();

        idocJpaRepository.save(idocEntity);

        // 2. 새로운 H2TRANS Line ID 생성 (Max + 1)
        Long nextTransLineId = h2TransJpaRepository.findMaxLineId() + 1;

        H2TransEntity h2TransEntity = H2TransEntity.builder()
                .lineId(nextTransLineId)
                .idocId(idocEntity.getLineId())
                .dtimeCre(localDateTime)
                .dtimeMod(localDateTime)
                //.usrMod()
                //.pgmMod()
                //.modCnt()
                .dataCode(10L)
                .cTransTy( Long.parseLong(TransportStatus.NotAllowedPickUp.getValue()))
                .cClient("999")
                .cOrderId(selectedH2OrderMEntity.getCOrderId())
                .cOrderTy(selectedH2OrderMEntity.getCOrderTy())
                //.cErrId()
                //.cText1()
                //.cTCode()
                //.cOrderLn()
                .cGaId(selectedH2OrderMEntity.getCGalId())
                .cGalWhs(selectedH2OrderMEntity.getCGalWhs())
                .cCoId(selectedH2OrderDEntity.getCCoId())
                .cGrWgAct(30L)
                .cReqZone(selectedH2OrderDEntity.getCZone())
                .cZone("B")
                //.cLocId()
                //.cErrDsc()
                .cWcId(selectedH2OrderMEntity.getCWcId())
                .build();
        h2TransJpaRepository.save(h2TransEntity);
    }

    @Transactional(value = "db2TransactionManager")
    public void arrivedAtRackOutbound(TransportOrderEntity transportOrder, IdocEntity selectedIdocEntity, H2OrderMEntity selectedH2OrderMEntity, H2OrderDEntity selectedH2OrderDEntity) {
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0);
        log.info("outOfRackOutbound");
        // 1. 새로운 IDOC Line ID 생성 (Max + 1)
        Long nextIdocLineId = idocJpaRepository.findMaxLineId() + 1;

        IdocEntity idocEntity =
                IdocEntity.builder()
                        .lineId(nextIdocLineId)
                        .idocTypId(selectedIdocEntity.getIdocTypId())
                        //.state()
                        //.errorCode()
                        .source(selectedIdocEntity.getDestination())
                        .destination(selectedIdocEntity.getSource())
                        //.tidId()
                        .docNum(selectedIdocEntity.getDocNum())
                        //.queueName()
                        //.partnerType()
                        //.partnerName()
                        //.partnerPort()
                        //.msgVariant()
                        //.arcKey()
                        .dtimeCre(localDateTime)
                        .dtimeMod(localDateTime)
                        //.usrMod()
                        //.pgmMod()
                        //.modCnt()
                        .build();

        idocJpaRepository.save(idocEntity);

        // 2. 새로운 H2TRANS Line ID 생성 (Max + 1)
        Long nextTransLineId = h2TransJpaRepository.findMaxLineId() + 1;

        H2TransEntity h2TransEntity = H2TransEntity.builder()
                .lineId(nextTransLineId)
                .idocId(idocEntity.getLineId())
                .dtimeCre(localDateTime)
                .dtimeMod(localDateTime)
                //.usrMod()
                //.pgmMod()
                //.modCnt()
                .dataCode(10L)
                .cTransTy( Long.parseLong(TransportStatus.ArrivedAtRack.getValue()))
                .cClient("999")
                .cOrderId(selectedH2OrderMEntity.getCOrderId())
                .cOrderTy(selectedH2OrderMEntity.getCOrderTy())
                //.cErrId()
                //.cText1()
                //.cTCode()
                //.cOrderLn()
                .cGaId(selectedH2OrderMEntity.getCGalId())
                .cGalWhs(selectedH2OrderMEntity.getCGalWhs())
                .cCoId(selectedH2OrderDEntity.getCCoId())
                .cGrWgAct(30L)
                .cReqZone(selectedH2OrderDEntity.getCZone())
                .cZone("B")
                //.cLocId()
                //.cErrDsc()
                .cWcId(selectedH2OrderMEntity.getCWcId())
                .build();
        h2TransJpaRepository.save(h2TransEntity);
    }

    @Transactional(value = "db2TransactionManager")
    public void stationOccupiedInbound(StationOccupiedDto request) {
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0);
        log.info("stationOccupied");
        // 1. 새로운 IDOC Line ID 생성 (Max + 1)
        Long nextIdocLineId = idocJpaRepository.findMaxLineId() + 1;

        IdocEntity idocEntity =
                IdocEntity.builder()
                        .lineId(nextIdocLineId)
                        .idocTypId(11L)
                        //.state()
                        //.errorCode()
                        .source(20L)
                        .destination(1L)
                        //.tidId()
                        //.docNum()
                        //.queueName()
                        //.partnerType()
                        //.partnerName()
                        //.partnerPort()
                        //.msgVariant()
                        //.arcKey()
                        .dtimeCre(localDateTime)
                        .dtimeMod(localDateTime)
                        //.usrMod()
                        //.pgmMod()
                        //.modCnt()
                        .build();

        idocJpaRepository.save(idocEntity);

        // 2. 새로운 H2TRANS Line ID 생성 (Max + 1)
        Long nextTransLineId = h2TransJpaRepository.findMaxLineId() + 1;

        H2TransEntity h2TransEntity = H2TransEntity.builder()
                .lineId(nextTransLineId)
                .idocId(idocEntity.getLineId())
                .dtimeCre(localDateTime)
                .dtimeMod(localDateTime)
                //.usrMod()
                //.pgmMod()
                //.modCnt()
                .dataCode(10L)
                .cTransTy( Long.parseLong(TransportStatus.StationOccupied.getValue()))
                .cClient("999")
                //.cOrderId()
                //.cOrderTy()
                //.cErrId()
                //.cText1()
                //.cTCode()
                //.cOrderLn()
                //.cGaId()
                //.cGalWhs()
                .cCoId(request.getContainerId())
                //.cGrWgAct()
                //.cReqZone()
                //.cZone()
                .cLocId(request.getLocationId())
                //.cErrDsc()
                .cWcId(request.getWorkcenterId())
                .build();
        h2TransJpaRepository.save(h2TransEntity);
    }


    @Transactional("db2TransactionManager")
    public List<IdocEntity> selectIdocAll() {
        log.info("selectIdocAll");
        return idocJpaRepository.findAll();
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

    @Transactional("db2TransactionManager")
    public IdocEntity selectIdocByIdocId(Long idocId) {
        log.info("selectIdocByIdocId");
        Optional<IdocEntity> optionalIdocEntity = idocJpaRepository.findByLineId(idocId);
        if(optionalIdocEntity.isEmpty()){
            throw new RuntimeException("IDOC을 찾을 수 없습니다.");
        }
        return optionalIdocEntity.get();
    }

    @Transactional("db2TransactionManager")
    public List<H2OrderMEntity> selectH2OrderMByIdocId(Long IdocId) {
        log.info("selectH2OrderMByIdocId");
        return h2OrderMJpaRepository.findByIdocId(IdocId);
    }

    @Transactional("db2TransactionManager")
    public List<H2OrderMEntity> selectH2OrderMByOrderId(String orderId) {
        log.info("selectH2OrderMByIdocId");
        return h2OrderMJpaRepository.findByCOrderId(orderId);
    }

    @Transactional("db2TransactionManager")
    public List<H2OrderDEntity> selectH2OrderDByOrderId(String orderId) {
        log.info("selectH2OrderDByOrderId");
        return h2OrderDJpaRepository.findByCOrderId(orderId);
    }

    @Transactional("db2TransactionManager")
    public List<H2OrderDEntity> selectH2OrderDByIdocId(Long IdocId) {
        log.info("selectH2OrderDByIdocId");
        return h2OrderDJpaRepository.findByIdocId(IdocId);
    }

    @Transactional("db2TransactionManager")
    public List<H2TransEntity> selectH2TransByIdocId(Long IdocId) {
        log.info("selectH2TransByIdocId");
        return h2TransJpaRepository.findByIdocId(IdocId);
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