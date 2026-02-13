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
public class TransportOrderFacade {
    private final TransportOrderService transportOrderService;
    private final ExternalInterfaceService externalInterfaceService;

    public TransportOrderEntity transferOutbound(Long idocId) {
        log.info("인터페이스 프로세스 시작 : idocId = {}", idocId);

        // 1. DB2 조회 (Read Only 트랜잭션)
        // Pageable.ofSize(1) 등을 이용해 단건 혹은 리스트 조회
        IdocEntity idoc = externalInterfaceService.selectIdocByIdocId(idocId);

        // 리스트 조회 (필요 시 서비스 내 selectH2OrderMByIdocId 등 활용)
        List<H2OrderMEntity> mList = externalInterfaceService.selectH2OrderMByIdocId(idocId);
        List<H2OrderDEntity> dList = externalInterfaceService.selectH2OrderDByIdocId(idocId);

        if(mList.size()!= 1 && dList.size() != 1){
            throw new RuntimeException("잘못된 데이터 기입");
        }

        // 2. 비즈니스 로직 처리 및 MSSQL 저장 호출
        TransportOrderEntity result = transportOrderService.registerOutbound(idoc, mList.get(0), dList.get(0));
        externalInterfaceService.transferedIdocId(idoc.getLineId());
        return result;
    }

    public TransportOrderEntity transferInbound(Long idocId) {
        log.info("인터페이스 프로세스 시작 : idocId = {}", idocId);

        // 1. DB2 조회 (Read Only 트랜잭션)
        // Pageable.ofSize(1) 등을 이용해 단건 혹은 리스트 조회
        IdocEntity idoc = externalInterfaceService.selectIdocByIdocId(idocId);

        // 리스트 조회 (필요 시 서비스 내 selectH2OrderMByIdocId 등 활용)
        List<H2OrderMEntity> mList = externalInterfaceService.selectH2OrderMByIdocId(idocId);
        List<H2OrderDEntity> dList = externalInterfaceService.selectH2OrderDByIdocId(idocId);

        if(mList.size()!= 1 && dList.size() != 1){
            throw new RuntimeException("잘못된 데이터 기입");
        }

        // 2. 비즈니스 로직 처리 및 MSSQL 저장 호출
        TransportOrderEntity result = transportOrderService.registerInbound(idoc, mList.get(0), dList.get(0));
        externalInterfaceService.transferedIdocId(idoc.getLineId());
        return result;
    }

    public TransportOrderEntity transferRelocation(Long idocId) {
        log.info("인터페이스 프로세스 시작 : idocId = {}", idocId);

        // 1. DB2 조회 (Read Only 트랜잭션)
        // Pageable.ofSize(1) 등을 이용해 단건 혹은 리스트 조회
        IdocEntity idoc = externalInterfaceService.selectIdocByIdocId(idocId);

        // 리스트 조회 (필요 시 서비스 내 selectH2OrderMByIdocId 등 활용)
        List<H2OrderMEntity> mList = externalInterfaceService.selectH2OrderMByIdocId(idocId);
        List<H2OrderDEntity> dList = externalInterfaceService.selectH2OrderDByIdocId(idocId);

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
        TransportOrderEntity result = transportOrderService.registerRelocation(idoc, mList.get(0), h2OrderDSourceEntity,h2OrderDTargetEntity);
        externalInterfaceService.transferedIdocId(idoc.getLineId());
        return result;
    }

    public void acceptOutbound(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderService.selectTransportOrder(orderId);

        if(!transportOrder.getTransportStatus().equals(TransportStatus.Create.name())){
            throw new RuntimeException("Status is Not Create");
        }
        List<H2OrderMEntity> mList = externalInterfaceService.selectH2OrderMByOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = externalInterfaceService.selectH2OrderDByOrderId(transportOrder.getTransportOrderId());

        if(mList.size()!= 1 && dList.size() != 1){
            throw new RuntimeException("잘못된 데이터 기입");
        }

        IdocEntity idocEntity = externalInterfaceService.selectIdocByIdocId(mList.get(0).getIdocId());

        externalInterfaceService.acceptOutbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        transportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.Accept);

    }

    public void acceptInbound(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderService.selectTransportOrder(orderId);

        if(!transportOrder.getTransportStatus().equals(TransportStatus.Create.name())){
            throw new RuntimeException("Status is Not Create");
        }

        List<H2OrderMEntity> mList = externalInterfaceService.selectH2OrderMByOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = externalInterfaceService.selectH2OrderDByOrderId(transportOrder.getTransportOrderId());

        if(mList.size()!= 1 && dList.size() != 1){
            throw new RuntimeException("잘못된 데이터 기입");
        }

        IdocEntity idocEntity = externalInterfaceService.selectIdocByIdocId(mList.get(0).getIdocId());

        externalInterfaceService.acceptInbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        transportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.Accept);

    }

    public void acceptRelocation(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderService.selectTransportOrder(orderId);

        if(!transportOrder.getTransportStatus().equals(TransportStatus.Create.name())){
            throw new RuntimeException("Status is Not Create");
        }

        List<H2OrderMEntity> mList = externalInterfaceService.selectH2OrderMByOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = externalInterfaceService.selectH2OrderDByOrderId(transportOrder.getTransportOrderId());

        if(mList.size()!= 1 && dList.size() != 2){
            throw new RuntimeException("잘못된 데이터 기입");
        }

        IdocEntity idocEntity = externalInterfaceService.selectIdocByIdocId(mList.get(0).getIdocId());

        externalInterfaceService.acceptInbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        transportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.Accept);

    }

    public void workStationEmptyInbound(Long orderId) {
        log.info("인터페이스 프로세스 시작 : workStationEmpty Id = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderService.selectTransportOrder(orderId);

        if(!transportOrder.getTransportStatus().equals(TransportStatus.Accept.name())){
            throw new RuntimeException("Status is Not Accept");
        }

        List<H2OrderMEntity> mList = externalInterfaceService.selectH2OrderMByOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = externalInterfaceService.selectH2OrderDByOrderId(transportOrder.getTransportOrderId());

        if(mList.size()!= 1 && dList.size() != 1){
            throw new RuntimeException("잘못된 데이터 기입");
        }

        IdocEntity idocEntity = externalInterfaceService.selectIdocByIdocId(mList.get(0).getIdocId());

        externalInterfaceService.workStationEmptyInbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        transportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.WorkstationEmpty);

    }

    public void arrivedWorkstationErrorInbound(Long orderId) {
        log.info("인터페이스 프로세스 시작 : arrivedWorkstationError Id = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderService.selectTransportOrder(orderId);

        if(!transportOrder.getTransportStatus().equals(TransportStatus.WorkstationEmpty.name())){
            throw new RuntimeException("Status is Not WorkstationEmpty");
        }

        List<H2OrderMEntity> mList = externalInterfaceService.selectH2OrderMByOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = externalInterfaceService.selectH2OrderDByOrderId(transportOrder.getTransportOrderId());

        if(mList.size()!= 1 && dList.size() != 1){
            throw new RuntimeException("잘못된 데이터 기입");
        }

        IdocEntity idocEntity = externalInterfaceService.selectIdocByIdocId(mList.get(0).getIdocId());

        externalInterfaceService.arrivedWorkstationErrorInbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        transportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.ArrivedAtWorkstationWithError);

    }

    public void errorTextInbound(Long orderId, String errorText) {
        log.info("인터페이스 프로세스 시작 : arrivedWorkstationError Id = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderService.selectTransportOrder(orderId);

        if(transportOrder.getTransportStatus().equals(TransportStatus.ArrivedAtWorkstationWithError.name()) || transportOrder.getTransportStatus().equals(TransportStatus.ErrorText.name())){

        } else {
            throw new RuntimeException("Status is Not ArrivedAtWorkstationWithError or ErrorText");
        }

        List<H2OrderMEntity> mList = externalInterfaceService.selectH2OrderMByOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = externalInterfaceService.selectH2OrderDByOrderId(transportOrder.getTransportOrderId());

        if(mList.size()!= 1 && dList.size() != 1){
            throw new RuntimeException("잘못된 데이터 기입");
        }

        IdocEntity idocEntity = externalInterfaceService.selectIdocByIdocId(mList.get(0).getIdocId());

        externalInterfaceService.errorTextInbound(errorText,transportOrder,idocEntity,mList.get(0),dList.get(0));
        transportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.ErrorText);

    }

    public void carrierScannedInbound(Long orderId) {
        log.info("인터페이스 프로세스 시작 : carrierScannedInboundOrder Id = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderService.selectTransportOrder(orderId);

        List<H2OrderMEntity> mList = externalInterfaceService.selectH2OrderMByOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = externalInterfaceService.selectH2OrderDByOrderId(transportOrder.getTransportOrderId());

        if(mList.size()!= 1 && dList.size() != 1){
            throw new RuntimeException("잘못된 데이터 기입");
        }

        IdocEntity idocEntity = externalInterfaceService.selectIdocByIdocId(mList.get(0).getIdocId());

        externalInterfaceService.carrierScannedInbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        transportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.CarrierScanned);

    }

    public void releaseOutbound(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderService.selectTransportOrder(orderId);

        if(!transportOrder.getTransportStatus().equals(TransportStatus.Accept.name())){
            throw new RuntimeException("Status is Not Accept");
        }

        List<H2OrderMEntity> mList = externalInterfaceService.selectH2OrderMByOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = externalInterfaceService.selectH2OrderDByOrderId(transportOrder.getTransportOrderId());

        if(mList.size()!= 1 && dList.size() != 1){
            throw new RuntimeException("잘못된 데이터 기입");
        }

        IdocEntity idocEntity = externalInterfaceService.selectIdocByIdocId(mList.get(0).getIdocId());

        externalInterfaceService.releaseOutbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        transportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.Released);

    }

    public void internalRelocationOutbound(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderService.selectTransportOrder(orderId);

        if(!transportOrder.getTransportStatus().equals(TransportStatus.Released.name())){
            throw new RuntimeException("Status is Not Released");
        }

        List<H2OrderMEntity> mList = externalInterfaceService.selectH2OrderMByOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = externalInterfaceService.selectH2OrderDByOrderId(transportOrder.getTransportOrderId());

        if(mList.size()!= 1 && dList.size() != 1){
            throw new RuntimeException("잘못된 데이터 기입");
        }

        IdocEntity idocEntity = externalInterfaceService.selectIdocByIdocId(mList.get(0).getIdocId());

        externalInterfaceService.internalRelocationOutbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        transportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.InternalRelocation);

    }

    public void internalRelocationRelocation(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderService.selectTransportOrder(orderId);

        if(!transportOrder.getTransportStatus().equals(TransportStatus.Accept.name())){
            throw new RuntimeException("Status is Not Released");
        }

        List<H2OrderMEntity> mList = externalInterfaceService.selectH2OrderMByOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = externalInterfaceService.selectH2OrderDByOrderId(transportOrder.getTransportOrderId());

        if(mList.size()!= 1 ){
            throw new RuntimeException("잘못된 데이터 기입");
        }
        if(dList.size() != 2){
            throw new RuntimeException("잘못된 데이터 기입");
        }

        IdocEntity idocEntity = externalInterfaceService.selectIdocByIdocId(mList.get(0).getIdocId());

        externalInterfaceService.internalRelocationOutbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        transportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.InternalRelocation);

    }

    public void dropOnTunnelRelocation(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderService.selectTransportOrder(orderId);

        if(!transportOrder.getTransportStatus().equals(TransportStatus.Accept.name())){
            throw new RuntimeException("Status is Not Released");
        }

        List<H2OrderMEntity> mList = externalInterfaceService.selectH2OrderMByOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = externalInterfaceService.selectH2OrderDByOrderId(transportOrder.getTransportOrderId());

        if(mList.size()!= 1 ){
            throw new RuntimeException("잘못된 데이터 기입");
        }
        if(dList.size() != 2){
            throw new RuntimeException("잘못된 데이터 기입");
        }

        IdocEntity idocEntity = externalInterfaceService.selectIdocByIdocId(mList.get(0).getIdocId());

        externalInterfaceService.dropOnTunnelRelocation(transportOrder,idocEntity,mList.get(0),dList.get(0));
        transportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.DroppedOnTunnelConveyor);

    }

    public void outOfRackOutbound(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderService.selectTransportOrder(orderId);

        if(transportOrder.getTransportStatus().equals(TransportStatus.Released.name()) || transportOrder.getTransportStatus().equals(TransportStatus.InternalRelocation.name()) ){

        } else{
            throw new RuntimeException("Status is Not Released");
        }

        List<H2OrderMEntity> mList = externalInterfaceService.selectH2OrderMByOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = externalInterfaceService.selectH2OrderDByOrderId(transportOrder.getTransportOrderId());

        if(mList.size()!= 1 && dList.size() != 1){
            throw new RuntimeException("잘못된 데이터 기입");
        }

        IdocEntity idocEntity = externalInterfaceService.selectIdocByIdocId(mList.get(0).getIdocId());

        externalInterfaceService.outOfRackOutbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        transportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.OutOfRack);

    }

    public void outOfRackInbound(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderService.selectTransportOrder(orderId);

        if(transportOrder.getTransportStatus().equals(TransportStatus.CarrierScanned.name()) ){

        } else{
            throw new RuntimeException("Status is Not CarrierScanned");
        }

        List<H2OrderMEntity> mList = externalInterfaceService.selectH2OrderMByOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = externalInterfaceService.selectH2OrderDByOrderId(transportOrder.getTransportOrderId());

        if(mList.size()!= 1 && dList.size() != 1){
            throw new RuntimeException("잘못된 데이터 기입");
        }

        IdocEntity idocEntity = externalInterfaceService.selectIdocByIdocId(mList.get(0).getIdocId());

        externalInterfaceService.outOfRackOutbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        transportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.OutOfRack);

    }

    public void arrivedAtWorkstationOutbound(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderService.selectTransportOrder(orderId);

        if(!transportOrder.getTransportStatus().equals(TransportStatus.OutOfRack.name())  ){
            throw new RuntimeException("Status is Not OutOfRack");
        }

        List<H2OrderMEntity> mList = externalInterfaceService.selectH2OrderMByOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = externalInterfaceService.selectH2OrderDByOrderId(transportOrder.getTransportOrderId());

        if(mList.size()!= 1 && dList.size() != 1){
            throw new RuntimeException("잘못된 데이터 기입");
        }

        IdocEntity idocEntity = externalInterfaceService.selectIdocByIdocId(mList.get(0).getIdocId());

        externalInterfaceService.arrivedAtWorkStationOutbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        transportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.ArrivedAtWorkStation);

    }

    public void completedOutbound(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderService.selectTransportOrder(orderId);

        if(transportOrder.getTransportStatus().equals(TransportStatus.ArrivedAtWorkStation.name()) || transportOrder.getTransportStatus().equals(TransportStatus.Shortage.name()) ){

        } else{
            throw new RuntimeException("Status is Not ArrivedAtWorkStation or Shortage");
        }

        List<H2OrderMEntity> mList = externalInterfaceService.selectH2OrderMByOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = externalInterfaceService.selectH2OrderDByOrderId(transportOrder.getTransportOrderId());

        if(mList.size()!= 1 && dList.size() != 1){
            throw new RuntimeException("잘못된 데이터 기입");
        }

        IdocEntity idocEntity = externalInterfaceService.selectIdocByIdocId(mList.get(0).getIdocId());

        externalInterfaceService.completedOutbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        transportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.OrderDone_Outbound);

    }

    public void completedInbound(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderService.selectTransportOrder(orderId);

        List<H2OrderMEntity> mList = externalInterfaceService.selectH2OrderMByOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = externalInterfaceService.selectH2OrderDByOrderId(transportOrder.getTransportOrderId());

        if(mList.size()!= 1 && dList.size() != 1){
            throw new RuntimeException("잘못된 데이터 기입");
        }

        IdocEntity idocEntity = externalInterfaceService.selectIdocByIdocId(mList.get(0).getIdocId());

        externalInterfaceService.completedInbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        transportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.OrderDone_Inbound);

    }

    public void completedRelocation(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderService.selectTransportOrder(orderId);

        List<H2OrderMEntity> mList = externalInterfaceService.selectH2OrderMByOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = externalInterfaceService.selectH2OrderDByOrderId(transportOrder.getTransportOrderId());

        IdocEntity idocEntity = externalInterfaceService.selectIdocByIdocId(mList.get(0).getIdocId());

        externalInterfaceService.completedRelocation(transportOrder,idocEntity,mList.get(0),dList.get(0));
        transportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.OrderDone_Relocation);

    }

    public void takeOffOutbound(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderService.selectTransportOrder(orderId);

        if(!transportOrder.getTransportStatus().equals(TransportStatus.OrderDone_Outbound.name())  ){
            throw new RuntimeException("Status is Not Released");
        }

        List<H2OrderMEntity> mList = externalInterfaceService.selectH2OrderMByOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = externalInterfaceService.selectH2OrderDByOrderId(transportOrder.getTransportOrderId());

        if(mList.size()!= 1 && dList.size() != 1){
            throw new RuntimeException("잘못된 데이터 기입");
        }

        IdocEntity idocEntity = externalInterfaceService.selectIdocByIdocId(mList.get(0).getIdocId());

        externalInterfaceService.takeOffOutbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        transportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.TakeOff);

    }

    public void binEmptyOutbound(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderService.selectTransportOrder(orderId);

        if(!transportOrder.getTransportStatus().equals(TransportStatus.Released.name())  ){
            throw new RuntimeException("Status is Not Released");
        }

        List<H2OrderMEntity> mList = externalInterfaceService.selectH2OrderMByOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = externalInterfaceService.selectH2OrderDByOrderId(transportOrder.getTransportOrderId());

        if(mList.size()!= 1 && dList.size() != 1){
            throw new RuntimeException("잘못된 데이터 기입");
        }

        IdocEntity idocEntity = externalInterfaceService.selectIdocByIdocId(mList.get(0).getIdocId());

        externalInterfaceService.binEmptyOutbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        transportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.BinEmpty);

    }

    public void shortageOutbound(Long orderId) {
        log.info("인터페이스 프로세스 시작 : shortage = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderService.selectTransportOrder(orderId);

        if(!transportOrder.getTransportStatus().equals(TransportStatus.BinEmpty.name())  ){
            throw new RuntimeException("Status is Not BinEmpty");
        }

        List<H2OrderMEntity> mList = externalInterfaceService.selectH2OrderMByOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = externalInterfaceService.selectH2OrderDByOrderId(transportOrder.getTransportOrderId());

        if(mList.size()!= 1 && dList.size() != 1){
            throw new RuntimeException("잘못된 데이터 기입");
        }

        IdocEntity idocEntity = externalInterfaceService.selectIdocByIdocId(mList.get(0).getIdocId());

        externalInterfaceService.shortageOutbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        transportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.Shortage);

    }

    public void notAllowedPickUpOutbound(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderService.selectTransportOrder(orderId);

        if(!transportOrder.getTransportStatus().equals(TransportStatus.Released.name())  ){
            throw new RuntimeException("Status is Not Released");
        }

        List<H2OrderMEntity> mList = externalInterfaceService.selectH2OrderMByOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = externalInterfaceService.selectH2OrderDByOrderId(transportOrder.getTransportOrderId());

        if(mList.size()!= 1 && dList.size() != 1){
            throw new RuntimeException("잘못된 데이터 기입");
        }

        IdocEntity idocEntity = externalInterfaceService.selectIdocByIdocId(mList.get(0).getIdocId());

        externalInterfaceService.notAllowedPickUpOutbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        transportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.NotAllowedPickUp);

    }

    public void notAllowedPickUpInbound(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderService.selectTransportOrder(orderId);

        List<H2OrderMEntity> mList = externalInterfaceService.selectH2OrderMByOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = externalInterfaceService.selectH2OrderDByOrderId(transportOrder.getTransportOrderId());

        if(mList.size()!= 1 && dList.size() != 1){
            throw new RuntimeException("잘못된 데이터 기입");
        }

        IdocEntity idocEntity = externalInterfaceService.selectIdocByIdocId(mList.get(0).getIdocId());

        externalInterfaceService.notAllowedPickUpInbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        transportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.NotAllowedPickUp);

    }

    public void arrivedAtRackOutbound(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderService.selectTransportOrder(orderId);

        if(!transportOrder.getTransportStatus().equals(TransportStatus.NotAllowedPickUp.name())  ){
            throw new RuntimeException("Status is Not NotAllowedPickUp");
        }

        List<H2OrderMEntity> mList = externalInterfaceService.selectH2OrderMByOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = externalInterfaceService.selectH2OrderDByOrderId(transportOrder.getTransportOrderId());

        if(mList.size()!= 1 && dList.size() != 1){
            throw new RuntimeException("잘못된 데이터 기입");
        }

        IdocEntity idocEntity = externalInterfaceService.selectIdocByIdocId(mList.get(0).getIdocId());

        externalInterfaceService.arrivedAtRackOutbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        transportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.ArrivedAtRack);

    }

    public void arrivedAtRackRelocation(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderService.selectTransportOrder(orderId);

        if(!transportOrder.getTransportStatus().equals(TransportStatus.DroppedOnTunnelConveyor.name())  ){
            throw new RuntimeException("Status is Not DroppedOnTunnelConveyor");
        }

        List<H2OrderMEntity> mList = externalInterfaceService.selectH2OrderMByOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = externalInterfaceService.selectH2OrderDByOrderId(transportOrder.getTransportOrderId());

        if( mList.size()!= 1 ){
            throw new RuntimeException("잘못된 데이터 기입");
        }
        if(dList.size() != 2){
            throw new RuntimeException("잘못된 데이터 기입");
        }

        IdocEntity idocEntity = externalInterfaceService.selectIdocByIdocId(mList.get(0).getIdocId());

        externalInterfaceService.arrivedAtRackOutbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        transportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.ArrivedAtRack);

    }

    public void arrivedAtRackOInbound(Long orderId) {
        log.info("인터페이스 프로세스 시작 : acceptId = {}", orderId);

        TransportOrderEntity transportOrder = transportOrderService.selectTransportOrder(orderId);

        List<H2OrderMEntity> mList = externalInterfaceService.selectH2OrderMByOrderId(transportOrder.getTransportOrderId());
        List<H2OrderDEntity> dList = externalInterfaceService.selectH2OrderDByOrderId(transportOrder.getTransportOrderId());

        if(mList.size()!= 1 && dList.size() != 1){
            throw new RuntimeException("잘못된 데이터 기입");
        }

        IdocEntity idocEntity = externalInterfaceService.selectIdocByIdocId(mList.get(0).getIdocId());

        externalInterfaceService.arrivedAtRackOutbound(transportOrder,idocEntity,mList.get(0),dList.get(0));
        transportOrderService.updateStatusTransportOrder(transportOrder.getTransportOrderId(),TransportStatus.ArrivedAtRack);

    }

    public void stationOccupiedInbound(StationOccupiedDto request) {
        log.info("인터페이스 프로세스 시작 : StationOccupiedDto = {}", request);

        externalInterfaceService.stationOccupiedInbound(request);

    }

}