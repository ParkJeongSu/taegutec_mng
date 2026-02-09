package kr.co.aim.api.service;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.dto.*;
import kr.co.aim.common.enums.IdocErrorCode;
import kr.co.aim.common.enums.TransportStatus;
import kr.co.aim.infra.persistence.entity.IF_WorkOrderEntity;
import kr.co.aim.infra.persistence.entity.TransportOrderEntity;
import kr.co.aim.infra.persistence.entitydb2.H2OrderDEntity;
import kr.co.aim.infra.persistence.entitydb2.H2OrderMEntity;
import kr.co.aim.infra.persistence.entitydb2.H2TransEntity;
import kr.co.aim.infra.persistence.entitydb2.IdocEntity;
import kr.co.aim.infra.persistence.mapper.H2Mapper;
import kr.co.aim.infra.persistence.mapper.IdocMapper;
import kr.co.aim.infra.persistence.mapper.TransportOrderMapper;
import kr.co.aim.infra.persistence.springdatajpa.TransportOrderJpaRepository;
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

@Service
@RequiredArgsConstructor
@Slf4j
@Profile({"scheduler","simulator"})
public class DB2TransportOrderService {

    private final IdocJpaRepository idocJpaRepository;
    private final H2OrderMJpaRepository h2OrderMJpaRepository;
    private final H2OrderDJpaRepository h2OrderDJpaRepository;
    private final H2TransJpaRepository h2TransJpaRepository;
    private final TransportOrderJpaRepository transportOrderJpaRepository;
    private final IdocMapper idocMapper;
    private final H2Mapper h2Mapper;
    private final TransportOrderMapper transportOrderMapper;




    @Transactional("mssqlTransactionManager")
    public TransportOrderEntity outboundTransportOrderToMSSQL(IdocEntity idocEntity,H2OrderMEntity h2OrderMEntity,H2OrderDEntity h2OrderDEntity) {
        log.info("outboundTransportOrderToMSSQL");
        TransportOrderEntity transportOrderEntity = TransportOrderEntity
                .builder()
                .id(TsidUtils.nextId())
                .transportOrderName(h2OrderMEntity.getCOrderId())
                .description("outbound")
                .transportType(h2OrderMEntity.getCOrderTy())
                .transportOrderId(h2OrderMEntity.getCOrderId())
                .transportStatus(TransportStatus.Create.name())
                .priority(h2OrderMEntity.getCOrderPrio())
                .galId(h2OrderMEntity.getCGalId().toString())
                .galWarehouse(h2OrderMEntity.getCGalWhs())
                //.fromWarehouse()
                //.fromZoneName()
                //.fromLocationId()
                //.toWarehouse()
                //.toZoneName()
                .toLocationId(h2OrderMEntity.getCWcId())
                .carrierName(h2OrderDEntity.getCCoId())
                .carrierType(h2OrderDEntity.getCCoTy())
                //.drivingProfile()
                .createTime(idocEntity.getDtimeCre())
                //.releaseTime()
                //.completeTime()
                //.createUser()
                //.releaseUser()
                //.completeUser()
                .build();
        return transportOrderJpaRepository.save(transportOrderEntity);
    }

    @Transactional("mssqlTransactionManager")
    public TransportOrderEntity updateStatusTransportOrder(String orderId,TransportStatus status) {
        log.info("updateStatusTransportOrder");
        TransportOrderEntity transportOrderEntity = transportOrderJpaRepository.findByTransportOrderId(orderId);
        transportOrderEntity.setTransportStatus(status.name());
        return transportOrderJpaRepository.save(transportOrderEntity);
    }

    @Transactional("mssqlTransactionManager")
    public TransportOrderResponseDto selectTransportOrder(Long orderId) {
        log.info("selectTransportOrder");
        TransportOrderEntity entity = transportOrderJpaRepository.findByTransportOrderId(orderId.toString());
        return transportOrderMapper.toDto(entity);
    }

    @Transactional( value = "db2TransactionManager")
    public IdocEntity transferIdocId(Long idocId) {
        log.info("transferIdocId");
        IdocEntity idoc = idocJpaRepository.findByLineId(idocId)
                .orElseThrow(() -> new RuntimeException("IDOC을 찾을 수 없습니다."));
        idoc.setErrorCode( Integer.parseInt( IdocErrorCode.Processed.getValue() ) );
        return idocJpaRepository.save(idoc);
    }

    @Transactional(readOnly = true, value = "db2TransactionManager")
    public Page<IdocResponseDto> selectOutboundIdocs(Pageable pageable) {
        log.info("selectIdocs");
        Page<IdocEntity> idocPage = idocJpaRepository.findIdocsByOrderType("O",pageable);
        return idocPage.map(idocMapper::toDto);
    }

    @Transactional(readOnly = true, value = "db2TransactionManager")
    public Page<H2OrderMResponseDto> selectH2OrderMByIdocId(Long IdocId, Pageable pageable) {
        log.info("selectH2OrderMByIdocId");
        Page<H2OrderMEntity> h2OrderMPage = h2OrderMJpaRepository.findByIdocId(IdocId,pageable);
        return h2OrderMPage.map(h2Mapper::toDto);
    }

    @Transactional(readOnly = true, value = "db2TransactionManager")
    public Page<H2OrderDResponseDto> selectH2OrderDByIdocId(Long IdocId, Pageable pageable) {
        log.info("selectH2OrderDByIdocId");
        Page<H2OrderDEntity> h2OrderDPage = h2OrderDJpaRepository.findByIdocId(IdocId,pageable);
        return h2OrderDPage.map(h2Mapper::toDto);
    }

    @Transactional(value = "db2TransactionManager")
    public Page<H2TransResponseDto> selectH2TransByOrderId(Long orderId, Pageable pageable) {
        log.info("selectH2TransByIdocId");
        Page<H2TransEntity> h2TransPage = h2TransJpaRepository.selectByCOrderId(orderId.toString(),pageable);
        return h2TransPage.map(h2Mapper::toDto);
    }

    @Transactional(value = "db2TransactionManager")
    public void acceptOutbound(TransportOrderEntity transportOrder, IdocEntity selectedIdocEntity, H2OrderMEntity selectedH2OrderMEntity, H2OrderDEntity selectedH2OrderDEntity) {
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0);;
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
                .cZone("B")
                //.cLocId()
                //.cErrDsc()
                .cWcId(selectedH2OrderMEntity.getCWcId())
                .build();
        h2TransJpaRepository.save(h2TransEntity);
    }

    @Transactional(value = "db2TransactionManager")
    public void releaseOutbound(TransportOrderEntity transportOrder, IdocEntity selectedIdocEntity, H2OrderMEntity selectedH2OrderMEntity, H2OrderDEntity selectedH2OrderDEntity) {
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0);;
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
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0);;
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
    public void outOfRackOutbound(TransportOrderEntity transportOrder, IdocEntity selectedIdocEntity, H2OrderMEntity selectedH2OrderMEntity, H2OrderDEntity selectedH2OrderDEntity) {
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0);;
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
    public void binEmptyOutbound(TransportOrderEntity transportOrder, IdocEntity selectedIdocEntity, H2OrderMEntity selectedH2OrderMEntity, H2OrderDEntity selectedH2OrderDEntity) {
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0);;
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
    public void notAllowedPickUpOutbound(TransportOrderEntity transportOrder, IdocEntity selectedIdocEntity, H2OrderMEntity selectedH2OrderMEntity, H2OrderDEntity selectedH2OrderDEntity) {
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0);;
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


    @Transactional("db2TransactionManager")
    public List<IdocEntity> selectIdocAll() {
        log.info("selectIdocAll");
        return idocJpaRepository.findAll();
    }

    @Transactional("db2TransactionManager")
    public H2OrderDetailResponseDto selectH2OrderDetailByIdocId(Long IdocId) {
        log.info("selectH2OrderDetailByIdocId");
        List<H2OrderMEntity> h2OrderMEntities = h2OrderMJpaRepository.findByIdocId(IdocId);
        List<H2OrderDEntity> h2OrderDEntities = h2OrderDJpaRepository.findByIdocId(IdocId);
        if(h2OrderMEntities.size() != 1 && h2OrderDEntities.size() != 1){
            throw new RuntimeException("H2orderm H2orderd size Error");
        }
        H2OrderMEntity h2OrderM = h2OrderMEntities.get(0);
        H2OrderDEntity h2OrderD = h2OrderDEntities.get(0);
        H2OrderDetailResponseDto responseDto =
                H2OrderDetailResponseDto.builder()
                        .lineId(h2OrderM.getIdocId())
                        .idocId(h2OrderM.getIdocId())
                        .dtimeCre(h2OrderM.getDtimeCre())
                        .dtimeMod(h2OrderM.getDtimeMod())
                        .usrMod(h2OrderM.getUsrMod())
                        .pgmMod(h2OrderM.getPgmMod())
                        .modCnt(h2OrderM.getModCnt())
                        .dataCode(h2OrderM.getDataCode())
                        .bookCtrl(h2OrderM.getBookCtrl())
                        .cClient(h2OrderM.getCClient())
                        .cOrderId(h2OrderM.getCOrderId())
                        .cOrderTy(h2OrderM.getCOrderTy())
                        .cDtPick(h2OrderM.getCDtPick())
                        .cOrderPrio(h2OrderM.getCOrderPrio())
                        .cTCode(h2OrderM.getCTCode())
                        .cLocId(h2OrderM.getCLocId())
                        .cWcId(h2OrderM.getCWcId())
                        .cGalId(h2OrderM.getCGalId())
                        .cGalWhs(h2OrderM.getCGalWhs())
                        .cHostUsr(h2OrderM.getCHostUsr())
                        .cUsrNo(h2OrderM.getCUsrNo())
                        .cOrderLn(h2OrderD.getCOrderLn())
                        .cCoId(h2OrderD.getCCoId())
                        .cCoTy(h2OrderD.getCOrderTy())
                        .cZone(h2OrderD.getCZone())
                        .cDrivingProfile(h2OrderD.getCDrivingProfile())
                        .build();
        return responseDto;
    }

    @Transactional("db2TransactionManager")
    public List<H2OrderMEntity> selectH2OrderMByIdocId(Long IdocId) {
        log.info("selectH2OrderMByIdocId");
        return h2OrderMJpaRepository.findByIdocId(IdocId);
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

    // Propagation.REQUIRES_NEW: 항상 새로운 트랜잭션을 시작하도록 강제
    @Transactional(value = "db2TransactionManager", propagation = Propagation.REQUIRES_NEW)
    public void updateDb2StatusToDoneInNewTransaction(Long lineId) {
        IdocEntity idocEntity = idocJpaRepository.findByLineId(lineId).orElseThrow();
        idocEntity.setState(2);
        idocJpaRepository.save(idocEntity);
    }

}