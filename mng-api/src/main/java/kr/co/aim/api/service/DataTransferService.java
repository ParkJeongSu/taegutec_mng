package kr.co.aim.api.service;

import kr.co.aim.api.dto.StationOccupiedDto;
import kr.co.aim.common.enums.TransportStatus;
import kr.co.aim.infra.persistence.entity.TransportOrderEntity;
import kr.co.aim.infra.persistence.entitydb2.H2OrderDEntity;
import kr.co.aim.infra.persistence.entitydb2.H2OrderMEntity;
import kr.co.aim.infra.persistence.entitydb2.IdocEntity;
import kr.co.aim.infra.persistence.springdatajpa.TransportOrderJpaRepository;
import kr.co.aim.infra.persistence.springdatajpadb2.H2OrderDJpaRepository;
import kr.co.aim.infra.persistence.springdatajpadb2.H2OrderMJpaRepository;
import kr.co.aim.infra.persistence.springdatajpadb2.H2TransJpaRepository;
import kr.co.aim.infra.persistence.springdatajpadb2.IdocJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

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

    public TransportOrderEntity transferInboundOrder(Long idocId) {
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
        TransportOrderEntity result = db2TransportOrderService.inboundTransportOrderToMSSQL(idoc, mList.get(0), dList.get(0));
        db2TransportOrderService.transferIdocId(idoc.getLineId());
        return result;
    }

    public TransportOrderEntity transferRelocationOrder(Long idocId) {
        log.info("인터페이스 프로세스 시작 : idocId = {}", idocId);

        // 1. DB2 조회 (Read Only 트랜잭션)
        // Pageable.ofSize(1) 등을 이용해 단건 혹은 리스트 조회
        IdocEntity idoc = idocJpaRepository.findByLineId(idocId)
                .orElseThrow(() -> new RuntimeException("IDOC을 찾을 수 없습니다."));

        // 리스트 조회 (필요 시 서비스 내 selectH2OrderMByIdocId 등 활용)
        List<H2OrderMEntity> mList = h2OrderMJpaRepository.findByIdocId(idocId);
        List<H2OrderDEntity> dList = h2OrderDJpaRepository.findByIdocId(idocId);

        if(mList.size()!= 1 ){
            throw new RuntimeException("잘못된 데이터 기입");
        }

        if(dList.size() != 2){
            throw new RuntimeException("잘못된 데이터 기입");
        }
        H2OrderDEntity h2OrderDSourceEntity = null;
        H2OrderDEntity h2OrderDTargetEntity = null;
        if(dList.get(0).getLineId() < dList.get(1).getLineId() ){
            h2OrderDSourceEntity = dList.get(0);
            h2OrderDTargetEntity = dList.get(1);
        } else {
            h2OrderDSourceEntity = dList.get(1);
            h2OrderDTargetEntity = dList.get(0);
        }

        // 2. 비즈니스 로직 처리 및 MSSQL 저장 호출
        TransportOrderEntity result = db2TransportOrderService.relocationTransportOrderToMSSQL(idoc, mList.get(0), h2OrderDSourceEntity,h2OrderDTargetEntity);
        db2TransportOrderService.transferIdocId(idoc.getLineId());
        return result;
    }

    public void acceptOutboundOrder(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderJpaRepository.findByTransportOrderId(orderId.toString());

        if(!transportOrder.getTransportStatus().equals(TransportStatus.Create.name())){
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

    public void acceptInboundOrder(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderJpaRepository.findByTransportOrderId(orderId.toString());

        if(!transportOrder.getTransportStatus().equals(TransportStatus.Create.name())){
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

        db2TransportOrderService.acceptInbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        db2TransportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.Accept);

    }

    public void acceptRelocationOrder(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderJpaRepository.findByTransportOrderId(orderId.toString());

        if(!transportOrder.getTransportStatus().equals(TransportStatus.Create.name())){
            throw new RuntimeException("Status is Not Create");
        }

        List<H2OrderMEntity> mList = h2OrderMJpaRepository.findByCOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = h2OrderDJpaRepository.findByCOrderId(transportOrder.getTransportOrderId());

        if(mList.size()!= 1 && dList.size() != 2){
            throw new RuntimeException("잘못된 데이터 기입");
        }
        Optional<IdocEntity> optionalIdocEntity = idocJpaRepository.findById(mList.get(0).getIdocId());
        if(optionalIdocEntity.isEmpty()){
            throw new RuntimeException("잘못된 데이터 기입");
        }
        IdocEntity idocEntity = optionalIdocEntity.get();

        db2TransportOrderService.acceptInbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        db2TransportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.Accept);

    }

    public void workStationEmptyInboundOrder(Long orderId) {
        log.info("인터페이스 프로세스 시작 : workStationEmpty Id = {}", orderId);

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

        db2TransportOrderService.workStationEmptyInbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        db2TransportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.WorkstationEmpty);

    }

    public void arrivedWorkstationErrorInboundOrder(Long orderId) {
        log.info("인터페이스 프로세스 시작 : arrivedWorkstationError Id = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderJpaRepository.findByTransportOrderId(orderId.toString());

        if(!transportOrder.getTransportStatus().equals(TransportStatus.WorkstationEmpty.name())){
            throw new RuntimeException("Status is Not WorkstationEmpty");
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

        db2TransportOrderService.arrivedWorkstationErrorInbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        db2TransportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.ArrivedAtWorkstationWithError);

    }

    public void errorTextInboundOrder(Long orderId,String errorText) {
        log.info("인터페이스 프로세스 시작 : arrivedWorkstationError Id = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderJpaRepository.findByTransportOrderId(orderId.toString());

        if(transportOrder.getTransportStatus().equals(TransportStatus.ArrivedAtWorkstationWithError.name()) || transportOrder.getTransportStatus().equals(TransportStatus.ErrorText.name())){

        } else {
            throw new RuntimeException("Status is Not ArrivedAtWorkstationWithError or ErrorText");
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

        db2TransportOrderService.errorTextInbound(errorText,transportOrder,idocEntity,mList.get(0),dList.get(0));
        db2TransportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.ErrorText);

    }

    public void carrierScannedInboundOrder(Long orderId) {
        log.info("인터페이스 프로세스 시작 : carrierScannedInboundOrder Id = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderJpaRepository.findByTransportOrderId(orderId.toString());

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

        db2TransportOrderService.carrierScannedInbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        db2TransportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.CarrierScanned);

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

    public void internalRelocationRelocationOrder(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderJpaRepository.findByTransportOrderId(orderId.toString());

        if(!transportOrder.getTransportStatus().equals(TransportStatus.Accept.name())){
            throw new RuntimeException("Status is Not Released");
        }

        List<H2OrderMEntity> mList = h2OrderMJpaRepository.findByCOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = h2OrderDJpaRepository.findByCOrderId(transportOrder.getTransportOrderId());

        if(mList.size()!= 1 ){
            throw new RuntimeException("잘못된 데이터 기입");
        }
        if(dList.size() != 2){
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

    public void dropOnTunnelRelocationOrder(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderJpaRepository.findByTransportOrderId(orderId.toString());

        if(!transportOrder.getTransportStatus().equals(TransportStatus.Accept.name())){
            throw new RuntimeException("Status is Not Released");
        }

        List<H2OrderMEntity> mList = h2OrderMJpaRepository.findByCOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = h2OrderDJpaRepository.findByCOrderId(transportOrder.getTransportOrderId());

        if(mList.size()!= 1 ){
            throw new RuntimeException("잘못된 데이터 기입");
        }
        if(dList.size() != 2){
            throw new RuntimeException("잘못된 데이터 기입");
        }
        Optional<IdocEntity> optionalIdocEntity = idocJpaRepository.findById(mList.get(0).getIdocId());
        if(optionalIdocEntity.isEmpty()){
            throw new RuntimeException("잘못된 데이터 기입");
        }
        IdocEntity idocEntity = optionalIdocEntity.get();

        db2TransportOrderService.dropOnTunnelRelocation(transportOrder,idocEntity,mList.get(0),dList.get(0));
        db2TransportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.DroppedOnTunnelConveyor);

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

    public void outOfRackInboundOrder(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderJpaRepository.findByTransportOrderId(orderId.toString());

        if(transportOrder.getTransportStatus().equals(TransportStatus.CarrierScanned.name()) ){

        } else{
            throw new RuntimeException("Status is Not CarrierScanned");
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

    public void arrivedAtWorkstationOutboundOrder(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderJpaRepository.findByTransportOrderId(orderId.toString());

        if(!transportOrder.getTransportStatus().equals(TransportStatus.OutOfRack.name())  ){
            throw new RuntimeException("Status is Not OutOfRack");
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

        db2TransportOrderService.arrivedAtWorkStationOutbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        db2TransportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.ArrivedAtWorkStation);

    }

    public void completedOutboundOrder(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderJpaRepository.findByTransportOrderId(orderId.toString());

        if(transportOrder.getTransportStatus().equals(TransportStatus.ArrivedAtWorkStation.name()) || transportOrder.getTransportStatus().equals(TransportStatus.Shortage.name()) ){

        } else{
            throw new RuntimeException("Status is Not ArrivedAtWorkStation or Shortage");
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

        db2TransportOrderService.completedOutbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        db2TransportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.OrderDone_Outbound);

    }

    public void completedInboundOrder(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderJpaRepository.findByTransportOrderId(orderId.toString());

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

        db2TransportOrderService.completedInbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        db2TransportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.OrderDone_Inbound);

    }

    public void completedRelocationOrder(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderJpaRepository.findByTransportOrderId(orderId.toString());

        List<H2OrderMEntity> mList = h2OrderMJpaRepository.findByCOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = h2OrderDJpaRepository.findByCOrderId(transportOrder.getTransportOrderId());

        Optional<IdocEntity> optionalIdocEntity = idocJpaRepository.findById(mList.get(0).getIdocId());
        if(optionalIdocEntity.isEmpty()){
            throw new RuntimeException("잘못된 데이터 기입");
        }
        IdocEntity idocEntity = optionalIdocEntity.get();

        db2TransportOrderService.completedRelocation(transportOrder,idocEntity,mList.get(0),dList.get(0));
        db2TransportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.OrderDone_Relocation);

    }

    public void takeOffOutboundOrder(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderJpaRepository.findByTransportOrderId(orderId.toString());

        if(!transportOrder.getTransportStatus().equals(TransportStatus.OrderDone_Outbound.name())  ){
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

        db2TransportOrderService.takeOffOutbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        db2TransportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.TakeOff);

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

    public void shortageOutboundOrder(Long orderId) {
        log.info("인터페이스 프로세스 시작 : shortage = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderJpaRepository.findByTransportOrderId(orderId.toString());

        if(!transportOrder.getTransportStatus().equals(TransportStatus.BinEmpty.name())  ){
            throw new RuntimeException("Status is Not BinEmpty");
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

        db2TransportOrderService.shortageOutbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        db2TransportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.Shortage);

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

    public void notAllowedPickUpInboundOrder(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderJpaRepository.findByTransportOrderId(orderId.toString());

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

        db2TransportOrderService.notAllowedPickUpInbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        db2TransportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.NotAllowedPickUp);

    }

    public void arrivedAtRackOutboundOrder(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderJpaRepository.findByTransportOrderId(orderId.toString());

        if(!transportOrder.getTransportStatus().equals(TransportStatus.NotAllowedPickUp.name())  ){
            throw new RuntimeException("Status is Not NotAllowedPickUp");
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

        db2TransportOrderService.arrivedAtRackOutbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        db2TransportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.ArrivedAtRack);

    }

    public void arrivedAtRackRelocationOrder(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderJpaRepository.findByTransportOrderId(orderId.toString());

        if(!transportOrder.getTransportStatus().equals(TransportStatus.DroppedOnTunnelConveyor.name())  ){
            throw new RuntimeException("Status is Not DroppedOnTunnelConveyor");
        }

        List<H2OrderMEntity> mList = h2OrderMJpaRepository.findByCOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = h2OrderDJpaRepository.findByCOrderId(transportOrder.getTransportOrderId());

        if( mList.size()!= 1 ){
            throw new RuntimeException("잘못된 데이터 기입");
        }
        if(dList.size() != 2){
            throw new RuntimeException("잘못된 데이터 기입");
        }
        Optional<IdocEntity> optionalIdocEntity = idocJpaRepository.findById(mList.get(0).getIdocId());
        if(optionalIdocEntity.isEmpty()){
            throw new RuntimeException("잘못된 데이터 기입");
        }
        IdocEntity idocEntity = optionalIdocEntity.get();

        db2TransportOrderService.arrivedAtRackOutbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        db2TransportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.ArrivedAtRack);

    }

    public void arrivedAtRackOInboundOrder(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderJpaRepository.findByTransportOrderId(orderId.toString());

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

        db2TransportOrderService.arrivedAtRackOutbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        db2TransportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.ArrivedAtRack);

    }

    public void stationOccupied(StationOccupiedDto request) {
        log.info("인터페이스 프로세스 시작 : StationOccupiedDto = {}", request);

        db2TransportOrderService.stationOccupied(request);

    }

}