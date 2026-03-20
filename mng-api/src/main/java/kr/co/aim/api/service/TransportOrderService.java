package kr.co.aim.api.service;

import kr.co.aim.api.vo.insert.TransportOrderContext;
import kr.co.aim.api.vo.insert.TransportStatusReportVo;
import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.enums.GALTransportStatus;
import kr.co.aim.infra.persistence.entity.TransportOrderEntity;
import kr.co.aim.infra.persistence.db2entity.insert.H2OrderDEntity;
import kr.co.aim.infra.persistence.db2entity.insert.H2OrderMEntity;
import kr.co.aim.infra.persistence.db2entity.insert.IdocEntity;
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

    public TransportOrderEntity createBaseBuilder(TransportOrderContext context) {
        return TransportOrderEntity.builder()
                .id(TsidUtils.nextId())
                .transportOrderName( context.getMaster().getCOrderId())
                .transportType(context.getMaster().getCOrderTy())
                .transportOrderId(context.getMaster().getCOrderId())
                .transportStatus(GALTransportStatus.Create.name())
                .priority(context.getMaster().getCOrderPrio())
                .galId(context.getMaster().getCGalId().toString())
                .galWarehouse(context.getMaster().getCGalWhs())
                .createTime(context.getIdoc().getDtimeCre())
                .build();
    }

    @Transactional("mssqlTransactionManager")
    public TransportOrderEntity registerTransportOrder(TransportOrderContext context) {
        TransportOrderEntity transportOrderEntity = createBaseBuilder(context);
        return transportOrderJpaRepository.save(transportOrderEntity);
    }

    @Transactional("mssqlTransactionManager")
    public TransportOrderEntity updateStatusTransportOrder(TransportStatusReportVo vo) {
        log.info("updateStatusTransportOrder");
        Optional<TransportOrderEntity> optionalTransportOrderEntity = transportOrderJpaRepository.findByTransportOrderId(vo.getOrderId());
        if(optionalTransportOrderEntity.isEmpty()){
            throw new RuntimeException("TransportOrder를 찾을 수 없습니다. (요청 ID: " + vo.getOrderId() + ")");
        }

        TransportOrderEntity transportOrderEntity = optionalTransportOrderEntity.get();
        transportOrderEntity.setTransportStatus( vo.getStatus().name());
        return transportOrderJpaRepository.save(transportOrderEntity);
    }


}