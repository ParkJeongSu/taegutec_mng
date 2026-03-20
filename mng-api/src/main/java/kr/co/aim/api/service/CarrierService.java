package kr.co.aim.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.error.EntityNotFoundException;
import kr.co.aim.common.format.*;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.payload.MaterialDeassignFromCarrier;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.CarrierDeassignCommand;
import kr.co.aim.domain.command.CleanJobEndedCommand;
import kr.co.aim.domain.command.CleanJobStartedCommand;
import kr.co.aim.domain.command.LocationChangedCommand;
import kr.co.aim.domain.model.Carrier;
import kr.co.aim.domain.model.Port;
import kr.co.aim.domain.model.PortDef;
import kr.co.aim.domain.model.TransportJob;
import kr.co.aim.domain.repository.*;
import kr.co.aim.infra.persistence.entity.IF_EVENT_LOGEntity;
import kr.co.aim.infra.persistence.springdatajpa.IF_EVENT_LOGJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
public class CarrierService {
    private final ObjectMapper objectMapper;
    private final CarrierDefRepository carrierDefRepository;
    private final CarrierRepository carrierRepository;
    private final PortRepository portRepository;
    private final PortDefRepository portDefRepository;
    private final TransportJobRepository transportJobRepository;
    private final IF_EVENT_LOGJpaRepository if_event_logJpaRepository;

    /**
     * Carrier 의 세정작업이 취소되었음을 보고
     * 1. Carrier 의 상태를 변하는건 없음
     *
     * @param message 받은 메시지
     * @return WMS 로 보낼 메시지 객체
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public BaseMessage<CarrierCleanJobCanceledBody> carrierCleanJobCanceled(BaseMessage<CarrierCleanJobCanceledBody> message) {
        // TODO: 세정작업이 취소 되는 시나리오가 있는지 있다면, WMS에 보고해야하는지 확인
        return message;
    }

    /**
     * Carrier 의 세정 작업이 시작되었음을 보고
     * 1. Carrier 조회
     * 2. Carrier Clean Start time 을 현재 시간으로 변경
     *
     * @param message 받은 메시지
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public BaseMessage<CarrierCleanJobStartedBody> carrierCleanJobStarted(BaseMessage<CarrierCleanJobStartedBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();
        

        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String carrierName = message.getBody().getCarrierName();

        Optional<Carrier> optionalCarriers = carrierRepository.findByCarrierName(carrierName);
        if(optionalCarriers.isEmpty()){
            // TODO: 추후 확인 후 나중에 try catch 로 수정할지 고민
            return null;
        }
        Carrier carrier = optionalCarriers.get();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        CleanJobStartedCommand command = CleanJobStartedCommand.builder()
                .transactionInfo(tx)
                .equipmentName(equipmentName)
                .portName(portName)
                .build();

        carrier.cleanJobStarted(command);
        carrierRepository.save(carrier);
        
        // TODO: 반환 후 wms에 어떤식으로 i/f 를 할건지 확인
        return message;
    }

    /**
     * Carrier 의 세정작업이 종료 되었음을 보고
     * 1. Carrier 의 Clean State 를 Clean 으로 수정
     * 2. Carrier 의 Clean End Time 을 현재 시간으로 수정
     * 3 만일 Container 라면, CarrierDetailType 의 값을 null 로 수정
     *
     * @param message 받은 메시지
     * @return RTD 로 보낼 메시지 객체
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public BaseMessage<CarrierCleanJobEndedBody> carrierCleanJobEnded(BaseMessage<CarrierCleanJobEndedBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();
        

        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String carrierName = message.getBody().getCarrierName();

        Optional<Carrier> optionalCarriers = carrierRepository.findByCarrierName(carrierName);
        if(optionalCarriers.isEmpty()){
            // TODO: 추후 확인 후 나중에 try catch 로 수정할지 고민
            return null;
        }
        Carrier carrier = optionalCarriers.get();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        CleanJobEndedCommand command = CleanJobEndedCommand.builder()
                .transactionInfo(tx)
                .equipmentName(equipmentName)
                .portName(portName)
                .build();

        carrier.cleanJobEnded(command);
        carrierRepository.save(carrier);

        // TODO: 반환 후 wms에 어떤식으로 i/f 를 할건지 확인
        return message;
    }

    /**
     * port 위에서 Carrier 의 투입 가능 여부를 요청
     * 1. Carrier 의 정보 조회
     * 2. task 정보 조회
     * @param message 받은 메시지
     * @return EAS로 반환해야할 Carrier,Lot,Task Info
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public BaseMessage<CarrierValidationReplyBody> carrierValidationRequest(BaseMessage<CarrierValidationRequestBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String carrierName = message.getBody().getCarrierName();

        BaseMessage<CarrierValidationReplyBody> reply = new BaseMessage<>();
        CarrierValidationReplyBody body = CarrierValidationReplyBody.builder()
                .equipmentName(equipmentName)
                .carrierName(carrierName)
                .build();
        reply.setMessageName(MessageList.CARRIER_VALIDATION_REPLY.getMessageName());
        reply.setResultCode(ResultCode.OK.getValue());
        reply.setBody(body);

        try {
            Optional<Carrier> optionalCarriers = carrierRepository.findByCarrierName(carrierName);
            if(optionalCarriers.isEmpty()){
                throw new EntityNotFoundException(Carrier.class,carrierName);
            }
            Carrier carrier = optionalCarriers.get();

            Optional<Port> optionalPorts = portRepository.findByEquipmentNameAndPortName(equipmentName,portName);
            if(optionalPorts.isEmpty()){
                throw new EntityNotFoundException(Port.class,equipmentName + "_" +portName);
            }
            Port port = optionalPorts.get();

            Optional<PortDef> optionalPortDef = portDefRepository.findByEquipmentNameAndPortName(port.getEquipmentName(),port.getPortName());
            if(optionalPortDef.isEmpty()){
                throw new EntityNotFoundException(PortDef.class,equipmentName + "_" +portName);
            }
            PortDef portDef = optionalPortDef.get();


            if( StringUtils.equals(PortType.INPUT.getValue(),portDef.getPortType())){
                log.info("full Container Logic Start");
                // List로 받지만 하나의 Carrier 는 단 하나의 Wait인 TaskJobDetail 을 가지고 있음을 전제
//                List<TaskJobDetail> taskJobDetailList = taskJobDetailRepository.findByCarrierNameAndState(carrierName, TaskJobDetailState.WAIT.getValue());
//                if(taskJobDetailList.isEmpty()){
//                    throw new TaskJobException("Not Found TaskJobDetail");
//                }
//                else if(taskJobDetailList.size() > 1){
//                    throw new TaskJobException("One or more TaskJobs were found.");
//                }
//                TaskJobDetail taskJobDetail = taskJobDetailList.get(0);
//                Optional<TaskJob> optionalTaskJob = taskJobRepository.findById(taskJobDetail.getTaskJobId());
//                TaskJob taskJob;
//                if(optionalTaskJob.isPresent()){
//                    taskJob = optionalTaskJob.get();
//                }
//                else{
//                    throw new TaskJobException("Not Found TaskJob");
//                }
//                // TODO: 실제로 이 부분에 EAS 의 필요한 recipe라든가, 정보를 담기
//                body.setCarrierName(taskJobDetail.getCarrierName());
            }
            else if(StringUtils.equals(PortType.OUTPUT.getValue(),portDef.getPortType())){
                log.info("Empty Container Logic Start");
                // TODO : PortDef 의 containerType과 현재 Carrier 의 Container Type의 비교 같으면 ok 틀리면 ng
                if(StringUtils.isEmpty(carrier.getContainerType())){
                    // Carrier 의 ContainerType이 비어있단 이야기는
                    // Clean 후 처음으로 Carrier에 자재를 담는다는 이야기
                    // ok
                    message.setResultCode(ResultCode.OK.getValue());
                }
                else{
                    // Carrier 의 ContainerType에 값이 있단 이야기는
                    // 이전에 특정 자재를 담았다는 이야기
                    // Container 내부의 찌거기와 혼합되면 안되기때문에 철저한 Validation
//                    if(StringUtils.equals(portDef.getContainerType(), carrier.getContainerType())){
//                        // ok
//                        message.setResultCode(ResultCode.OK.getValue());
//                    }
//                    else {
//                        // ng
//                        message.setResultCode(ResultCode.NG.getValue());
//                    }
                }
            }
            return reply;
        } catch (Exception e) {
            message.setResultCode(ResultCode.NG.getValue());
            return reply;
        }
    }

    /**
     * port 위에서 Carrier 의 투입 가능 여부를 요청
     * 1. Carrier 의 정보 조회
     * 2. carrier 의 위치정보를 message 의 값으로 수정
     * @param message 받은 메시지
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void carrierLocationChanged(BaseMessage<CarrierLocationChangedBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();
        

        String transportJobName = message.getBody().getTransportJobName();
        String carrierName = message.getBody().getCarrierName();
        String carrierType = message.getBody().getCarrierType();
        // TODO : Carrier 의 현재 EquipmentName, PositionName, ZoneName 을 컬럼을 추가할지 고민
        String currentEquipmentName = message.getBody().getCurrentEquipmentName();
        String currentPositionType = message.getBody().getCurrentPositionType();
        String currentPositionName = message.getBody().getCurrentPositionName();
        String currentZoneName = message.getBody().getCurrentZoneName();

        Optional<Carrier> optionalCarriers = carrierRepository.findByCarrierName(carrierName);
        if(optionalCarriers.isEmpty()){
            return;
        }
        Carrier carrier = optionalCarriers.get();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        LocationChangedCommand command = LocationChangedCommand.builder()
                .transactionInfo(tx)
                .equipmentName(currentEquipmentName)
                .build();

        carrier.locationChanged(command);
        carrierRepository.save(carrier);
        // TODO: Carrier History add

    }

    /**
     * WareHouse 에 관리하는 Carrier 데이터 생성
     * 1. Carrier 의 정보 조회
     * @param message 받은 메시지
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void carrierDataInstall(BaseMessage<CarrierDataInstalledBody> message) {
        // TODO: Warehouse 입장에서 관리하는 Carrier 추가될때, 보고 이게 필요한지 고민..
    }

    /**
     * WareHouse 에 관리하는 Carrier 데이터 삭제
     * 1. Carrier 의 정보 조회
     * @param message 받은 메시지
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void carrierDataRemoved(BaseMessage<CarrierDataRemovedBody> message) {
        // TODO: Warehouse 입장에서 관리하는 Carrier 삭제시 보고, 이게 필요할까..
    }

    /**
     * WareHouse 에 관리하는 CarrierList 데이터 생성
     * 1. Carrier 의 정보 조회
     * @param message 받은 메시지
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void carrierDataReport(BaseMessage<CarrierDataReportBody> message) {
        // TODO: Warehouse 입장에서 관리하고 있는 CarrierList를 MNG로 보내기 이게 필요한지 고민..
    }

    /**
     * Carrier 와 Lot 데이터 분리
     * 1. Carrier 의 정보 조회
     * @param message 받은 메시지
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void materialDeassignedFromCarrier(BaseMessage<MaterialDeassignedFromCarrierBody> message) throws Exception{
        // TODO: Carrier가 설비에 투입 후에 보고 Lot과의 관계를 끊고 Carrier 의 상태를 Empty로 변경 확인
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String carrierName = message.getBody().getCarrierName();
        Optional<Carrier> optionalCarriers = carrierRepository.findByCarrierName(carrierName);
        Carrier carrier;
        if(optionalCarriers.isEmpty()){
            return;
        }
        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        carrier = optionalCarriers.get();
        CarrierDeassignCommand command = CarrierDeassignCommand.builder()
                .transactionInfo(tx)
                .quantity(0)
                .carrierName(carrierName)
                .capaState(CarrierCapaState.EMPTY.getValue())
                .useState(CarrierUseState.AVAILABLE.getValue())
                .build();
        carrier.deassigned(command);
        carrier = carrierRepository.save(carrier);
        // TODO: Add history

        // TODO: 자재를 Carrier 로부터 빼내고 Gal로 i/f
        MaterialDeassignFromCarrier materialDeassignFromCarrier =
                MaterialDeassignFromCarrier.builder()
                        .carrierName(carrierName)
                        .equipmentName("")
                        .build();
        String jsonPayload = objectMapper.writeValueAsString(materialDeassignFromCarrier);
        IF_EVENT_LOGEntity ifEventLogEntity = IF_EVENT_LOGEntity.builder()
                .seq(TsidUtils.nextId())
                .eventType(IF_EVENT_LOGEventType.MATERIAL_ASSIGN_TO_CARRIER.getValue())
                .payload(jsonPayload)
                .ifStatus(IF_EVENT_LOGState.READY.getValue())
                .errMSG("")
                .createTime(tx.eventTime())
                .retryCNT(0)
                .build();
        if_event_logJpaRepository.save(ifEventLogEntity);

    }

    /**
     * Carrier 와 Lot 데이터 분리
     * 1. Carrier 의 정보 조회
     * @param message 받은 메시지
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void materialAssignedToCarrier(BaseMessage<MaterialAssignedToCarrierBody> message) {
        // TODO: Carrier가 설비에 투입 후에 보고 Lot과의 관계를 끊고 Carrier 의 상태를 Empty로 변경 확인
    }

    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public BaseMessage<DestinationReplyBody> destinationRequest(BaseMessage<DestinationRequestBody> message) {

        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String transportJobName = message.getBody().getTransportJobName();
        String carrierName = message.getBody().getCarrierName();

        BaseMessage<DestinationReplyBody> reply = null;


        if( StringUtils.isBlank(transportJobName)){
            Optional<Carrier> optionalCarriers = carrierRepository.findByCarrierName(carrierName);
            Carrier carrier;
            if(optionalCarriers.isEmpty()){
                reply = new BaseMessage<>();
                reply.setMessageName(MessageList.TRANSPORT_JOB_REQUEST.getMessageName());
                reply.setTransactionId(message.getTransactionId());
                reply.setResultCode(ResultCode.NG.getValue());
                reply.setResultMessage("No carrier job found");
                DestinationReplyBody body = DestinationReplyBody.builder()
                        .build();
                reply.setBody(body);
            }
            else {
                carrier = optionalCarriers.get();
                List<String> transportJobStates = new ArrayList<>();
                transportJobStates.add(TransportJobState.REQUESTED.getValue());
                transportJobStates.add(TransportJobState.ACCEPTED.getValue());
                transportJobStates.add(TransportJobState.STARTED.getValue());
                List<TransportJob> transportJobList = transportJobRepository.findByCarrierNameAndTransportJobStateIn(carrierName, transportJobStates);
                if(transportJobList.isEmpty()){
                    reply = new BaseMessage<>();
                    reply.setMessageName(MessageList.TRANSPORT_JOB_REQUEST.getMessageName());
                    reply.setTransactionId(message.getTransactionId());
                    reply.setResultCode(ResultCode.NG.getValue());
                    reply.setResultMessage("No transport job found");
                    DestinationReplyBody body = DestinationReplyBody.builder()
                            .build();
                    reply.setBody(body);
                }
                else if(transportJobList.size() == 1){
                    TransportJob transportJob = null;
                    transportJob =  transportJobList.get(0);
                    reply = new BaseMessage<>();
                    reply.setMessageName(MessageList.TRANSPORT_JOB_REQUEST.getMessageName());
                    reply.setTransactionId(message.getTransactionId());
                    reply.setResultCode(ResultCode.OK.getValue());
                    reply.setResultMessage("");
                    DestinationReplyBody body = DestinationReplyBody.builder()
                            .transportJobName(transportJobName)
                            .destinationEquipmentName(transportJob.getDestinationEquipmentName())
                            .destinationZoneName(transportJob.getDestinationZoneName())
                            .carrierName(transportJob.getCarrierName())
                            .build();
                    reply.setBody(body);
                }
                else{
                    reply = new BaseMessage<>();
                    reply.setMessageName(MessageList.TRANSPORT_JOB_REQUEST.getMessageName());
                    reply.setTransactionId(message.getTransactionId());
                    reply.setResultCode(ResultCode.NG.getValue());
                    reply.setResultMessage("transport job found more 2");
                    DestinationReplyBody body = DestinationReplyBody.builder()
                            .build();
                    reply.setBody(body);
                }

            }

        }
        else {
            Optional<TransportJob> optionalTransportJob = transportJobRepository.findByTransportJobName(transportJobName);
            TransportJob transportJob = null;
            if(optionalTransportJob.isEmpty()){
                reply = new BaseMessage<>();
                reply.setMessageName(MessageList.TRANSPORT_JOB_REQUEST.getMessageName());
                reply.setTransactionId(message.getTransactionId());
                reply.setResultCode(ResultCode.NG.getValue());
                reply.setResultMessage("No transport job found");
                DestinationReplyBody body = DestinationReplyBody.builder()
                        .transportJobName(transportJobName)
                        .carrierName(transportJob.getCarrierName())
                        .build();
                reply.setBody(body);
            }
            else {
                transportJob =  optionalTransportJob.get();
                reply = new BaseMessage<>();
                reply.setMessageName(MessageList.TRANSPORT_JOB_REQUEST.getMessageName());
                reply.setTransactionId(message.getTransactionId());
                reply.setResultCode(ResultCode.OK.getValue());
                reply.setResultMessage("");
                DestinationReplyBody body = DestinationReplyBody.builder()
                        .transportJobName(transportJobName)
                        .destinationEquipmentName(transportJob.getDestinationEquipmentName())
                        .destinationZoneName(transportJob.getDestinationZoneName())
                        .carrierName(transportJob.getCarrierName())
                        .build();
                reply.setBody(body);
            }
        }
        return reply;
    }


    @Transactional
    public void deleteAllCarriersByIdInBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return; // 삭제할 ID가 없으면 아무 작업도 하지 않음
        }
        // 여러 건을 삭제할 때는 이 메서드가 성능상 가장 효율적입니다.
        // DELETE ... WHERE id IN (...) 쿼리를 한 번에 실행합니다.
        carrierRepository.deleteAllByIdInBatch(ids);
    }



}