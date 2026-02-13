package kr.co.aim.api.service;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.enums.TransportStatus;
import kr.co.aim.infra.persistence.entity.TransportOrderEntity;
import kr.co.aim.infra.persistence.entitydb2.H2OrderDEntity;
import kr.co.aim.infra.persistence.entitydb2.H2OrderMEntity;
import kr.co.aim.infra.persistence.entitydb2.IdocEntity;
import kr.co.aim.infra.persistence.springdatajpa.TransportOrderJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile({"scheduler","simulator"})
public class TransportOrderService {

    private final TransportOrderJpaRepository transportOrderJpaRepository;

    @Transactional("mssqlTransactionManager")
    public TransportOrderEntity registerOutbound(IdocEntity idocEntity, H2OrderMEntity h2OrderMEntity, H2OrderDEntity h2OrderDEntity) {
        log.info("registerOutbound");
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
    public TransportOrderEntity registerInbound(IdocEntity idocEntity, H2OrderMEntity h2OrderMEntity, H2OrderDEntity h2OrderDEntity) {
        log.info("outboundTransportOrderToMSSQL");
        TransportOrderEntity transportOrderEntity = TransportOrderEntity
                .builder()
                .id(TsidUtils.nextId())
                .transportOrderName(h2OrderMEntity.getCOrderId())
                .description("inbound")
                .transportType(h2OrderMEntity.getCOrderTy())
                .transportOrderId(h2OrderMEntity.getCOrderId())
                .transportStatus(TransportStatus.Create.name())
                .priority(h2OrderMEntity.getCOrderPrio())
                .galId(h2OrderMEntity.getCGalId().toString())
                .galWarehouse(h2OrderMEntity.getCGalWhs())
                //.fromWarehouse()
                //.fromZoneName()
                .fromLocationId(h2OrderMEntity.getCWcId())
                //.toWarehouse()
                //.toZoneName()
                .toLocationId(h2OrderDEntity.getCZone())
                .carrierName(h2OrderDEntity.getCCoId())
                .carrierType(h2OrderDEntity.getCCoTy())
                .drivingProfile(h2OrderDEntity.getCDrivingProfile())
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
    public TransportOrderEntity registerRelocation(IdocEntity idocEntity, H2OrderMEntity h2OrderMEntity, H2OrderDEntity h2OrderDSourceEntity, H2OrderDEntity h2OrderDTargetEntity) {
        log.info("relocationTransportOrderToMSSQL");
        TransportOrderEntity transportOrderEntity = TransportOrderEntity
                .builder()
                .id(TsidUtils.nextId())
                .transportOrderName(h2OrderMEntity.getCOrderId())
                .description("relocation")
                .transportType(h2OrderMEntity.getCOrderTy())
                .transportOrderId(h2OrderMEntity.getCOrderId())
                .transportStatus(TransportStatus.Create.name())
                .priority(h2OrderMEntity.getCOrderPrio())
                .galId(h2OrderMEntity.getCGalId().toString())
                .galWarehouse(h2OrderMEntity.getCGalWhs())
                //.fromWarehouse()
                //.fromZoneName()
                .fromLocationId(h2OrderDSourceEntity.getCZone())
                //.toWarehouse()
                //.toZoneName()
                .toLocationId(h2OrderDTargetEntity.getCZone())
                .carrierName(h2OrderDSourceEntity.getCCoId())
                .carrierType(h2OrderDSourceEntity.getCCoTy())
                .drivingProfile(h2OrderDSourceEntity.getCDrivingProfile())
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
        Optional<TransportOrderEntity> optionalTransportOrderEntity = transportOrderJpaRepository.findByTransportOrderId(orderId.toString());
        if(optionalTransportOrderEntity.isEmpty()){
            throw new RuntimeException("TransportOrder를 찾을 수 없습니다. (요청 ID: " + orderId + ")");
        }

        TransportOrderEntity transportOrderEntity = optionalTransportOrderEntity.get();
        transportOrderEntity.setTransportStatus(status.name());
        return transportOrderJpaRepository.save(transportOrderEntity);
    }

    @Transactional("mssqlTransactionManager")
    public TransportOrderEntity selectTransportOrder(Long orderId) {
        log.info("selectTransportOrder");
        Optional<TransportOrderEntity> optionalTransportOrderEntity = transportOrderJpaRepository.findByTransportOrderId(orderId.toString());
        if(optionalTransportOrderEntity.isEmpty()){
            throw new RuntimeException("TransportOrder를 찾을 수 없습니다. (요청 ID: " + orderId + ")");
        }
        return optionalTransportOrderEntity.get();
    }



}