package kr.co.aim.api.service;

import kr.co.aim.common.dto.TransportOrderResponseDto;
import kr.co.aim.common.enums.TransportStatus;
import kr.co.aim.domain.model.User;
import kr.co.aim.domain.repository.UserRepository;
import kr.co.aim.infra.persistence.entity.TransportOrderEntity;
import kr.co.aim.infra.persistence.entitydb2.H2OrderDEntity;
import kr.co.aim.infra.persistence.entitydb2.H2OrderMEntity;
import kr.co.aim.infra.persistence.entitydb2.IdocEntity;
import kr.co.aim.infra.persistence.mapper.H2Mapper;
import kr.co.aim.infra.persistence.springdatajpa.TransportOrderJpaRepository;
import kr.co.aim.infra.persistence.springdatajpadb2.H2OrderDJpaRepository;
import kr.co.aim.infra.persistence.springdatajpadb2.H2OrderMJpaRepository;
import kr.co.aim.infra.persistence.springdatajpadb2.H2TransJpaRepository;
import kr.co.aim.infra.persistence.springdatajpadb2.IdocJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Profile({"scheduler","simulator"})
@RequiredArgsConstructor
public class DataTransferService {
    private final IdocJpaRepository idocJpaRepository;
    private final H2OrderMJpaRepository h2OrderMJpaRepository;
    private final H2OrderDJpaRepository h2OrderDJpaRepository;
    private final H2TransJpaRepository h2TransJpaRepository;
    private final TransportOrderJpaRepository transportOrderJpaRepository;
    private final DB2TransportOrderService db2TransportOrderService;

    public TransportOrderEntity transferOutboundOrder(Long idocId) {
        log.info("인터페이스 프로세스 시작 : idocId = {}", idocId);

        // 1. DB2 조회 (Read Only 트랜잭션)
        // Pageable.ofSize(1) 등을 이용해 단건 혹은 리스트 조회
        IdocEntity idoc = idocJpaRepository.findByLineId(idocId)
                .orElseThrow(() -> new RuntimeException("IDOC을 찾을 수 없습니다."));

        // 리스트 조회 (필요 시 서비스 내 selectH2OrderMByIdocId 등 활용)
        List<H2OrderMEntity> mList = h2OrderMJpaRepository.findByIdocId(idocId);
        List<H2OrderDEntity> dList = h2OrderDJpaRepository.findByIdocId(idocId);

        if(mList.size()!= 1 && dList.size() != 1){
            throw new RuntimeException("잘못된 데이터 기입");
        }

        // 2. 비즈니스 로직 처리 및 MSSQL 저장 호출
        TransportOrderEntity result = db2TransportOrderService.outboundTransportOrderToMSSQL(idoc, mList.get(0), dList.get(0));
        db2TransportOrderService.transferIdocId(idoc.getLineId());
        return result;
    }

    public void acceptOutboundOrder(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderJpaRepository.findByTransportOrderId(orderId.toString());

        if(!transportOrder.getTransportStatus().equals(TransportStatus.Create.getValue())){
            throw new RuntimeException("Status is Not Create");
        }

        List<H2OrderMEntity> mList = h2OrderMJpaRepository.findByCOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = h2OrderDJpaRepository.findByCOrderId(transportOrder.getTransportOrderId());

        if(mList.size()!= 1 && dList.size() != 1){
            throw new RuntimeException("잘못된 데이터 기입");
        }
        Optional<IdocEntity> optionalIdocEntity = idocJpaRepository.findById(mList.get(0).getIdocId());
        if(optionalIdocEntity.isEmpty()){
            throw new RuntimeException("잘못된 데이터 기입");
        }
        IdocEntity idocEntity = optionalIdocEntity.get();

        db2TransportOrderService.acceptOutbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        db2TransportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.Accept);

    }

    public void releaseOutboundOrder(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderJpaRepository.findByTransportOrderId(orderId.toString());

        if(!transportOrder.getTransportStatus().equals(TransportStatus.Accept.name())){
            throw new RuntimeException("Status is Not Accept");
        }

        List<H2OrderMEntity> mList = h2OrderMJpaRepository.findByCOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = h2OrderDJpaRepository.findByCOrderId(transportOrder.getTransportOrderId());

        if(mList.size()!= 1 && dList.size() != 1){
            throw new RuntimeException("잘못된 데이터 기입");
        }
        Optional<IdocEntity> optionalIdocEntity = idocJpaRepository.findById(mList.get(0).getIdocId());
        if(optionalIdocEntity.isEmpty()){
            throw new RuntimeException("잘못된 데이터 기입");
        }
        IdocEntity idocEntity = optionalIdocEntity.get();

        db2TransportOrderService.releaseOutbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        db2TransportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.Released);

    }

    public void internalRelocationOutboundOrder(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderJpaRepository.findByTransportOrderId(orderId.toString());

        if(!transportOrder.getTransportStatus().equals(TransportStatus.Released.name())){
            throw new RuntimeException("Status is Not Released");
        }

        List<H2OrderMEntity> mList = h2OrderMJpaRepository.findByCOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = h2OrderDJpaRepository.findByCOrderId(transportOrder.getTransportOrderId());

        if(mList.size()!= 1 && dList.size() != 1){
            throw new RuntimeException("잘못된 데이터 기입");
        }
        Optional<IdocEntity> optionalIdocEntity = idocJpaRepository.findById(mList.get(0).getIdocId());
        if(optionalIdocEntity.isEmpty()){
            throw new RuntimeException("잘못된 데이터 기입");
        }
        IdocEntity idocEntity = optionalIdocEntity.get();

        db2TransportOrderService.internalRelocationOutbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        db2TransportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.InternalRelocation);

    }

    public void outOfRackOutboundOrder(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderJpaRepository.findByTransportOrderId(orderId.toString());

        if(transportOrder.getTransportStatus().equals(TransportStatus.Released.name()) || transportOrder.getTransportStatus().equals(TransportStatus.InternalRelocation.name()) ){

        } else{
            throw new RuntimeException("Status is Not Released");
        }

        List<H2OrderMEntity> mList = h2OrderMJpaRepository.findByCOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = h2OrderDJpaRepository.findByCOrderId(transportOrder.getTransportOrderId());

        if(mList.size()!= 1 && dList.size() != 1){
            throw new RuntimeException("잘못된 데이터 기입");
        }
        Optional<IdocEntity> optionalIdocEntity = idocJpaRepository.findById(mList.get(0).getIdocId());
        if(optionalIdocEntity.isEmpty()){
            throw new RuntimeException("잘못된 데이터 기입");
        }
        IdocEntity idocEntity = optionalIdocEntity.get();

        db2TransportOrderService.outOfRackOutbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        db2TransportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.OutOfRack);

    }

    public void binEmptyOutboundOrder(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderJpaRepository.findByTransportOrderId(orderId.toString());

        if(!transportOrder.getTransportStatus().equals(TransportStatus.Released.name())  ){
            throw new RuntimeException("Status is Not Released");
        }

        List<H2OrderMEntity> mList = h2OrderMJpaRepository.findByCOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = h2OrderDJpaRepository.findByCOrderId(transportOrder.getTransportOrderId());

        if(mList.size()!= 1 && dList.size() != 1){
            throw new RuntimeException("잘못된 데이터 기입");
        }
        Optional<IdocEntity> optionalIdocEntity = idocJpaRepository.findById(mList.get(0).getIdocId());
        if(optionalIdocEntity.isEmpty()){
            throw new RuntimeException("잘못된 데이터 기입");
        }
        IdocEntity idocEntity = optionalIdocEntity.get();

        db2TransportOrderService.binEmptyOutbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        db2TransportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.BinEmpty);

    }

    public void notAllowedPickUpOutboundOrder(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderJpaRepository.findByTransportOrderId(orderId.toString());

        if(!transportOrder.getTransportStatus().equals(TransportStatus.Released.name())  ){
            throw new RuntimeException("Status is Not Released");
        }

        List<H2OrderMEntity> mList = h2OrderMJpaRepository.findByCOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = h2OrderDJpaRepository.findByCOrderId(transportOrder.getTransportOrderId());

        if(mList.size()!= 1 && dList.size() != 1){
            throw new RuntimeException("잘못된 데이터 기입");
        }
        Optional<IdocEntity> optionalIdocEntity = idocJpaRepository.findById(mList.get(0).getIdocId());
        if(optionalIdocEntity.isEmpty()){
            throw new RuntimeException("잘못된 데이터 기입");
        }
        IdocEntity idocEntity = optionalIdocEntity.get();

        db2TransportOrderService.notAllowedPickUpOutbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        db2TransportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.NotAllowedPickUp);

    }

}