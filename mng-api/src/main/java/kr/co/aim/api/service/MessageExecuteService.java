package kr.co.aim.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.api.strategy.FactoryProcessStrategy;
import kr.co.aim.api.vo.insert.ops.InsertEventLogReportVo;
import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.error.EntityNotFoundException;
import kr.co.aim.common.format.*;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.payload.MaterialDeassignFromCarrier;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.*;
import kr.co.aim.domain.model.*;
import kr.co.aim.infra.persistence.entity.*;
import kr.co.aim.infra.persistence.mapper.*;
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
public class MessageExecuteService {

    private final ObjectMapper objectMapper;
    private final HistoryService historyService;
    private final CarrierService carrierService;
    private final TransportOrderService transportOrderService;
    private final EquipmentService equipmentService;
    private final PortService portService;
    private final TransportJobService transportJobService;
    private final IfEventQueueService ifEventQueueService;

    private final FactoryProcessStrategy factoryProcessStrategy;

    private final PortMapper portMapper;
    private final EquipmentMapper equipmentMapper;
    private final PortDefMapper portDefMapper;
    private final CarrierMapper carrierMapper;
    private final TransportJobMapper transportJobMapper;

    /**
     * 알람은 log 만 찍음
     *
     *
     * @param message 받은 메시지
     */
    @Transactional
    public void alarmReport(BaseMessage<AlarmReportBody> message) {
        String alarmCode = message.getBody().getAlarmCode();
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();
        String equipmentName = message.getBody().getEquipmentName();
        String alarmState = message.getBody().getAlarmState();

        log.info("equipmentName : {}", equipmentName);
        log.info("alarmCode : {}", alarmCode);
        log.info("alarmState : {}", alarmState);
        log.info("alarmSeverity : {}", message.getBody().getAlarmSeverity());
        log.info("alarmText : {}", message.getBody().getAlarmText());

    }

    /**
     * Carrier 의 세정작업이 취소되었음을 보고
     * 1. Carrier 의 상태를 변하는건 없음
     *
     * @param message 받은 메시지
     * @return WMS 로 보낼 메시지 객체
     */
    @Transactional
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
    @Transactional
    public BaseMessage<CarrierCleanJobStartedBody> carrierCleanJobStarted(BaseMessage<CarrierCleanJobStartedBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();


        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String carrierName = message.getBody().getCarrierName();

        Optional<Carrier> optionalCarriers = carrierService.findByCarrierName(carrierName);
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
        carrierService.save(carrier);

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
    @Transactional
    public BaseMessage<CarrierCleanJobEndedBody> carrierCleanJobEnded(BaseMessage<CarrierCleanJobEndedBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();


        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String carrierName = message.getBody().getCarrierName();

        Optional<Carrier> optionalCarriers = carrierService.findByCarrierName(carrierName);
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
        carrierService.save(carrier);

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
    @Transactional
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
            Optional<Carrier> optionalCarriers = carrierService.findByCarrierName(carrierName);
            if(optionalCarriers.isEmpty()){
                throw new EntityNotFoundException(Carrier.class,carrierName);
            }
            Carrier carrier = optionalCarriers.get();

            Optional<Port> optionalPorts = portService.findPortByEquipmentNameAndPortName(equipmentName,portName);
            if(optionalPorts.isEmpty()){
                throw new EntityNotFoundException(Port.class,equipmentName + "_" +portName);
            }
            Port port = optionalPorts.get();

            Optional<PortDef> optionalPortDef = portService.findPortDefByEquipmentNameAndPortName(port.getEquipmentName(),port.getPortName());
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
    @Transactional
    public void carrierLocationChanged(BaseMessage<CarrierLocationChangedBody> message) {
        // TODO: FactoryProcessStrategy 로 변경 이유는 insert만 enqueue를 넣으면 됨
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String transportJobName = message.getBody().getTransportJobName();
        String carrierName = message.getBody().getCarrierName();
        String carrierType = message.getBody().getCarrierType();
        String currentEquipmentName = message.getBody().getCurrentEquipmentName();
        String currentPortName = message.getBody().getCurrentPortName();
        String currentZoneName = message.getBody().getCurrentZoneName();
        String currentPositionType = message.getBody().getCurrentPositionType();
        String currentPositionName = message.getBody().getCurrentPositionName();

        Optional<Carrier> optionalCarriers = carrierService.findByCarrierName(carrierName);
        if(optionalCarriers.isEmpty()){
            return;
        }
        Carrier carrier = optionalCarriers.get();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        LocationChangedCommand command = LocationChangedCommand.builder()
                .transactionInfo(tx)
                .equipmentName(currentEquipmentName)
                .portName(currentPortName)
                .zoneName(currentZoneName)
                .positionType(currentPositionType)
                .positionName(currentPositionName)
                .build();

        carrier.locationChanged(command);
        carrier = carrierService.save(carrier);
        CarrierHistoryEntity carrierHistoryEntity = carrierMapper.toHistoryEntity(carrier);
        historyService.saveHistory(carrierHistoryEntity);

    }

    /**
     * WareHouse 에 관리하는 Carrier 데이터 생성
     * 1. Carrier 의 정보 조회
     * @param message 받은 메시지
     */
    @Transactional
    public void carrierDataInstall(BaseMessage<CarrierDataInstalledBody> message) {
        // TODO: Warehouse 입장에서 관리하는 Carrier 추가될때, 보고 이게 필요한지 고민..
    }

    /**
     * WareHouse 에 관리하는 Carrier 데이터 삭제
     * 1. Carrier 의 정보 조회
     * @param message 받은 메시지
     */
    @Transactional
    public void carrierDataRemoved(BaseMessage<CarrierDataRemovedBody> message) {
        // TODO: Warehouse 입장에서 관리하는 Carrier 삭제시 보고, 이게 필요할까..
    }

    /**
     * WareHouse 에 관리하는 CarrierList 데이터 생성
     * 1. Carrier 의 정보 조회
     * @param message 받은 메시지
     */
    @Transactional
    public void carrierDataReport(BaseMessage<CarrierDataReportBody> message) {
        // TODO: Warehouse 입장에서 관리하고 있는 CarrierList를 MNG로 보내기 이게 필요한지 고민..
    }

    /**
     * Carrier 와 Lot 데이터 분리
     * 1. Carrier 의 정보 조회
     * @param message 받은 메시지
     */
    @Transactional
    public void materialDeassignedFromCarrier(BaseMessage<MaterialDeassignedFromCarrierBody> message) throws Exception{
        // TODO: Carrier가 설비에 투입 후에 보고 Lot과의 관계를 끊고 Carrier 의 상태를 Empty로 변경 확인
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String carrierName = message.getBody().getCarrierName();
        Optional<Carrier> optionalCarriers = carrierService.findByCarrierName(carrierName);
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
        carrier = carrierService.save(carrier);
        // TODO: Add history

        // TODO: 자재를 Carrier 로부터 빼내고 Gal로 i/f
        MaterialDeassignFromCarrier materialDeassignFromCarrier =
                MaterialDeassignFromCarrier.builder()
                        .carrierName(carrierName)
                        .equipmentName("")
                        .build();
        String jsonPayload = objectMapper.writeValueAsString(materialDeassignFromCarrier);
        // TODO: 아래의 로직을 factoryProcessStrategy 로 변경
        IfEventQueue ifEventQueue = IfEventQueue.builder()
                .id(TsidUtils.nextId())
                .eventType(IfEventQueueEventType.MATERIAL_ASSIGN_TO_CARRIER.getValue())
                .payload(jsonPayload)
                .ifStatus(IfEventQueueState.READY.getValue())
                .errMSG("")
                .createTime(tx.eventTime())
                .retryCNT(0)
                .build();
        ifEventQueueService.save(ifEventQueue);

    }

    /**
     * Carrier 와 Lot 데이터 분리
     * 1. Carrier 의 정보 조회
     * @param message 받은 메시지
     */
    @Transactional
    public void materialAssignedToCarrier(BaseMessage<MaterialAssignedToCarrierBody> message) {
        // TODO: Carrier가 설비에 투입 후에 보고 Lot과의 관계를 끊고 Carrier 의 상태를 Empty로 변경 확인
    }

    /**
     *
     */
    @Transactional
    public void takeOffCarrier(BaseMessage<TakeOffCarrierBody> message) {
        // TODO: 비지니스 로직은 없음
        // TO GAL TakeOffCarrier report
    }

    @Transactional
    public BaseMessage<DestinationReplyBody> destinationRequest(BaseMessage<DestinationRequestBody> message) {

        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String transportJobName = message.getBody().getTransportJobName();
        String carrierName = message.getBody().getCarrierName();

        BaseMessage<DestinationReplyBody> reply = null;

        if( StringUtils.isBlank(transportJobName)){
            Optional<Carrier> optionalCarriers = carrierService.findByCarrierName(carrierName);
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
                List<TransportJob> transportJobList = transportJobService.findByCarrierNameAndTransportJobStateIn(carrierName, transportJobStates);
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
            Optional<TransportJob> optionalTransportJob = transportJobService.findByTransportJobName(transportJobName);
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

    /**
     * 포트의 새로운 캐리어를 요청합니다.
     * 1. 포트에 반송중인 job 조회
     * transferState -> ReservedToLoad로 변경
     *
     * 2. 설비명으로 TaskJob Find
     *
     * 3. TaskJob 반환
     *
     * @param message 받은 메시지
     * @return TEX 로 보낼 메시지 객체
     */
    @Transactional
    public BaseMessage<CarrierDispatchRequestBody> loadRequest(BaseMessage<LoadRequestBody> message) {
        // TODO: insert 와 powder 가 로직이 다름 초기 로직은 같은데, 뒤쪽에 IfEventQueue 에 insert만 넣음
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String carrierName = message.getBody().getCarrierName();
        String portType = message.getBody().getPortType();
        String portTransportMode = message.getBody().getPortTransportMode();

        // TODO: 비관적 lock으로 변경
        Optional<Port> optionalPorts =  portService.findPortByEquipmentNameAndPortName(equipmentName,portName);

        if(optionalPorts.isEmpty()){
            return null;
        }

        Port port = optionalPorts.get();
        if(!StringUtils.equals(PortTransportState.READY_TO_LOAD.getValue(),port.getTransportState())){
            TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
            LoadRequestCommand command = LoadRequestCommand
                    .builder()
                    .transactionInfo(tx)
                    .build();
            port.loadRequest(command);
            port = portService.save(port);
            PortHistoryEntity portHistoryEntity = portMapper.toHistoryEntity(port);
            historyService.saveHistory(portHistoryEntity);
        }

        BaseMessage<CarrierDispatchRequestBody> reply = new BaseMessage<>();
        CarrierDispatchRequestBody body = CarrierDispatchRequestBody.builder()
                .equipmentName(equipmentName)
                .portName(portName)
                .build();
        reply.setMessageName(MessageList.CARRIER_DISPATCH_REQUEST.getMessageName());
        reply.setBody(body);

        return reply;
    }

    public BaseMessage<TransportJobRequestListBody> carrierDispatchRequest(BaseMessage<CarrierDispatchRequestBody> message){
        return factoryProcessStrategy.carrierDispatchRequest(message);
    }
    public BaseMessage<DestinationDispatchRequestBody> unLoadRequest(BaseMessage<UnLoadRequestBody> message){
        return factoryProcessStrategy.unLoadRequest(message);
    }
    public BaseMessage<TransportJobRequestListBody> transportOrderRequest(BaseMessage<TransportOrderRequestBody> message){
        return factoryProcessStrategy.transportOrderRequest(message);
    }

    /**
     * unload가 완료 되었음을 보고합니다.
     * 비지니스 로직이 없음 단순히 log 찍음
     * @param message 받은 메시지
     */
    @Transactional
    public void unLoadCompleted(BaseMessage<UnLoadCompletedBody> message) {
        log.info("Business Logic Nothing");
        log.info("equipmentName : {} ",message.getBody().getEquipmentName());
        log.info("portName : {} ",message.getBody().getPortName());
    }

    @Transactional
    public void loadCompleted(BaseMessage<LoadCompletedBody> message) {
        factoryProcessStrategy.loadCompleted(message);
    }

    /**
     * port 의 접근모드 변경시 보고
     * @param message 받은 메시지
     */
    @Transactional
    public void portTransportModeChanged(BaseMessage<PortTypeChangedBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String portType = message.getBody().getPortType();
        String portTransportModeName = message.getBody().getPortTransportMode();

        Optional<Port> optionalPorts = portService.findPortByEquipmentNameAndPortName(equipmentName,portName);

        if(optionalPorts.isEmpty()){
            return;
        }

        Port port = optionalPorts.get();
        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        PortTransportModeChangedCommand command = PortTransportModeChangedCommand.builder().transactionInfo(tx).portTransportModeName(portTransportModeName).build();

        port.transportModeChanged(command);
        port = portService.save(port);
        PortHistoryEntity portHistoryEntity = portMapper.toHistoryEntity(port);
        historyService.saveHistory(portHistoryEntity);

    }

    /**
     * port 의 상태 변경시 보고
     * @param message 받은 메시지
     */
    @Transactional
    public void portStateChanged(BaseMessage<PortStateChangedBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String portType = message.getBody().getPortType();
        String portStateName = message.getBody().getPortStateName();

        Optional<Port> optionalPorts = portService.findPortByEquipmentNameAndPortName(equipmentName,portName);

        if(optionalPorts.isEmpty()){
            return;
        }
        if(!PortState.isExist(portStateName)){
            return;
        }

        Port port = optionalPorts.get();
        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        PortState state = PortState.valueOf(portStateName);
        PortStateChangedCommand command = PortStateChangedCommand
                .builder()
                .transactionInfo(tx)
                .portState(state)
                .build();
        port.portStateChanged(command);
        port = portService.save(port);
        PortHistoryEntity portHistoryEntity = portMapper.toHistoryEntity(port);
        historyService.saveHistory(portHistoryEntity);
    }
    /**
     * port 의 상태 변경시 보고
     * @param message 받은 메시지
     */
    @Transactional
    public void portStateReport(BaseMessage<PortStateReportBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);

        for(PortList portData : message.getBody().getPortList()){
            String equipmentName = portData.getEquipmentName();
            String portName = portData.getPortName();
            String portStateName = portData.getPortStateName();
            String portType = portData.getPortType();
            String portTransportMode = portData.getPortTransportMode();
            String carrierName = portData.getCarrierName();
            Optional<Port> optionalPorts = portService.findPortByEquipmentNameAndPortName(equipmentName,portName);

            if(optionalPorts.isEmpty()){
                continue;
            }
            if(!PortState.isExist(portStateName)){
                continue;
            }

            Port port = optionalPorts.get();

            PortState state = PortState.valueOf(portStateName);
            PortStateChangedCommand command = PortStateChangedCommand.builder().transactionInfo(tx).portState(state).build();
            port.portStateChanged(command);
            port = portService.save(port);
            PortHistoryEntity portHistoryEntity = portMapper.toHistoryEntity(port);
            historyService.saveHistory(portHistoryEntity);
        }
    }

    /**
     * port 의 타입 변경시 보고
     * @param message 받은 메시지
     */
    @Transactional
    public void portTypeChanged(BaseMessage<PortTypeChangedBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String portTypeName = message.getBody().getPortType();

        if(!PortType.isExist(portTypeName)){
            return;
        }

        Optional<Port> optionalPorts = portService.findPortByEquipmentNameAndPortName(equipmentName,portName);

        if(optionalPorts.isEmpty()){
            return;
        }
        Port port = optionalPorts.get();

        Optional<PortDef> optionalPortDef = portService.findPortDefByEquipmentNameAndPortName(equipmentName,portName);
        if(optionalPortDef.isEmpty()){
            return;
        }
        PortDef portDef = optionalPortDef.get();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        PortType portType = PortType.valueOf(portTypeName);
        PortTypeChangedCommand command = PortTypeChangedCommand
                .builder()
                .transactionInfo(tx)
                .portType(portType)
                .build();
        portDef.portTypeChanged(command);
        portDef = portService.save(portDef);
        PortDefHistoryEntity portDefHistoryEntity = portDefMapper.toHistoryEntity(portDef);
        historyService.saveHistory(portDefHistoryEntity);
    }

    /**
     * port 의 사용 타입 변경시 보고
     * @param message 받은 메시지
     */
    @Transactional
    public void portUseTypeChanged(BaseMessage<PortUseTypeChangedBody> message) {
        log.info("Business Logic Nothing");
    }


    /**
     * 설비에 투입 후 생산 중단 보고
     *
     * @param message 받은 메시지
     */
    @Transactional
    public void processJobAborted(BaseMessage<ProcessJobAbortedBody> message) {
        // TODO: Container 의 원자재를 설비에 넣다가 중단되고 설비의 투입된 Lot과 split 됨
        // 이 경우 어떤식으로 보고가 오는지 확인...
        // Container 에 있는게 신규 Lot 으로 만들어져야 할 것 같고
        // 이때 어떤 naming rule 에 의해서 만들어질건지
        // 그리고 wms나 gal 에는 보고를 할것같은데 어떤식으로 보고를 할건지.
    }

    /**
     * 설비에 투입 후 보고
     *
     * @param message 받은 메시지
     */
    @Transactional
    public void processJobStarted(BaseMessage<ProcessJobStartedBody> message) {

        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();
        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String lotName = message.getBody().getLotName();
        String carrierName = message.getBody().getCarrierName();
        String recipeName = message.getBody().getRecipeName();
        // TODO: ProcessJobStarted 보고 후 wms에 보고 해야한다면 어떤식으로 할지 논의
    }


    /**
     * 설비에 투입 후 완료 보고
     *
     * @param message 받은 메시지
     */
    @Transactional
    public void processJobEnded(BaseMessage<ProcessJobEndedBody> message) {
        // TODO : 작업종료와 Carrier Assign 을 분리하는지 이때 recipe 보고 하는지..
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();
        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String lotName = message.getBody().getLotName();

        // TODO: ProcessJobStarted 보고 후 wms에 보고 해야한다면 어떤식으로 할지 논의
    }

    /**
     * 설비에 투입 후 완료 후 데이터 보고
     *
     * @param message 받은 메시지
     */
    @Transactional
    public void processJobDataReport(BaseMessage<ProcessJobDataReportBody> message) {
        // TODO: 이걸 sv 데이터라고 하나...? 어째든 기록만 한다고 하는데.. 어떻게 테이블 구조 될지 문의
    }

    /**
     * SCS 시스템이 켜질때 보고
     * 1. transportJob 조회
     * 2. 없으면 데이터 생성
     *
     * @param message 받은 메시지
     */
    @Transactional
    public void activeTransportJobReport(BaseMessage<ActiveTransportJobReportBody> message) {
        log.info("activeTransportJobReport");
        // TODO: 이런 시나리오가 있는지 일단 확인
    }

    /**
     * SCS 의 목적지 변경 후 보고 메시지
     * 1. transportJob 조회
     * 2. 없으면 데이터 생성
     *
     * @param message 받은 메시지
     */
    @Transactional
    public void destinationChanged(BaseMessage<DestinationChangedBody> message) {
        log.info("destinationChanged");

        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();
        String transportJobDetailName = message.getBody().getTransportJobName(); // DetailName
        String carrierName = message.getBody().getCarrierName();
        String oldDestinationEquipmentName = message.getBody().getOldDestinationEquipmentName();
        String oldDestinationPositionType = message.getBody().getOldDestinationPositionType();
        String oldDestinationPositionName = message.getBody().getOldDestinationPositionName();
        String oldDestinationZoneName = message.getBody().getOldDestinationZoneName();
        String newDestinationEquipmentName = message.getBody().getNewDestinationEquipmentName();
        String newDestinationPositionType = message.getBody().getNewDestinationPositionType();
        String newDestinationPositionName = message.getBody().getNewDestinationPositionName();
        String newDestinationZoneName = message.getBody().getNewDestinationZoneName();

    }

    /**
     * SCS 에서 Warehouse 의 Zone 정보 변경시 보고
     *
     * @param message 받은 메시지
     */
    @Transactional
    public void inventoryZoneDataReport(BaseMessage<InventoryZoneDataReport> message) {
        // TODO: 이걸 MNG 가 알아야할 필요가 있을까? 그냥 테이블 공유 하고 실시간으로 사용하면 되는걸까..
    }

    /**
     * 반송 취소처리 완료 보고
     *
     */
    @Transactional
    public void transportJobCancelCompleted() {
        // TODO: 반송이 취소완료 되는 시나리오
        try{
            factoryProcessStrategy.enqueueIfEventQueue(null);
        }
        catch(Exception e){
            log.error("EventQueue enqueue error",e);
        }
    }

    /**
     * 반송 취소처리 실패 보고 이 경우 그러면 최종적으로 어떻게 되는거지?
     *
     */
    @Transactional
    public void transportJobCancelFailed() {
        // TODO: 반송 취소가 failed 되는 시나리오 현재는 확인중
        try{
            factoryProcessStrategy.enqueueIfEventQueue(null);
        }
        catch(Exception e){
            log.error("EventQueue enqueue error",e);
        }
    }

    /**
     * 반송 취소처리 시작 보고
     * 내가 걱정하는게 이 취소 시나리오가 Mixing 같이 여러개의 job이 만들어진 시나리오에서 어떻게 해야할지 고민..
     */
    @Transactional
    public void transportJobCancelStarted() {
        // TODO: 반송이 취소처리가 시작 되는 시나리오
        try{
            factoryProcessStrategy.enqueueIfEventQueue(null);
        }
        catch(Exception e){
            log.error("EventQueue enqueue error",e);
        }
    }

    /**
     * 반송 잡이 정상적으로 종료되었음을 보고
     * 1. transportDetailJob 조회
     * 2. transportDetailJob 상태 completed 로 변경
     * 3. 모든 transportDetailJob 이 completed 라면, transportJob -> completed 변경
     */
    @Transactional
    public void transportJobCompleted(BaseMessage<TransportJobCompletedBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String transportJobName = message.getBody().getTransportJobName();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);

        Optional<TransportJob> optionalTransportJob = transportJobService.findByTransportJobName(transportJobName);
        if(optionalTransportJob.isPresent()){
            TransportJob transportJob = optionalTransportJob.get();
            TransportJobUpdateCommand command =
                    TransportJobUpdateCommand
                            .builder()
                            .transportJobState(TransportJobState.COMPLETED.getValue())
                            .transactionInfo(tx)
                            .build();
            transportJob.changeTransportJob(command);

            transportJob = transportJobService.save(transportJob);
            TransportJobHistoryEntity transportJobHistoryEntity = transportJobMapper.toHistoryEntity(transportJob);
            historyService.saveHistory(transportJobHistoryEntity);
        }
    }

    /**
     * 반송 잡의 처리 여부를 반환
     * reply 가 정상처리라면,
     * 반송잡의 상태를 Accepted 로 변경
     *
     * reply 가 실패처리라면
     * 반송잡의 상태를 rejected로 변경
     */
    @Transactional
    public void transportJobReply(BaseMessage<TransportJobReplyListBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        List<TransportJobReplyBody> transportJobList = message.getBody().getTransportJobList();

        for(TransportJobReplyBody transportJobReplyBody : transportJobList){
            Optional<TransportJob> optionalTransportJob = transportJobService.findByTransportJobName(transportJobReplyBody.getTransportJobName());

            if(optionalTransportJob.isPresent()){
                TransportJob transportJob = optionalTransportJob.get();
                TransportJobUpdateCommand command =
                        TransportJobUpdateCommand
                                .builder()
                                .transportJobState(TransportJobState.ACCEPTED.getValue())
                                .transactionInfo(tx)
                                .build();
                transportJob.changeTransportJob(command);

                transportJob = transportJobService.save(transportJob);
                TransportJobHistoryEntity transportJobHistoryEntity = transportJobMapper.toHistoryEntity(transportJob);
                historyService.saveHistory(transportJobHistoryEntity);
                // TODO : 신규 IfEventQueueService.enqueue 호출
                try{
                    InsertEventLogReportVo insertEventLogReportVo
                            = InsertEventLogReportVo
                            .builder()
//                            .transportJobName()
//                            .messageName()
//                            .port()
//                            .portDef()
//                            .carrierName()
//                            .actualZoneName()
//                            .actualWeight()
//                            .actualRackLocationId()
//                            .errorTexts()
                            .tx(tx)
                            .build();
                    factoryProcessStrategy.enqueueIfEventQueue(insertEventLogReportVo);
                }
                catch(Exception e){
                    log.error("EventQueue enqueue error",e);
                }
            }
        }



    }

    /**
     * 요청한 반송잡이 첫시작되는 시점 보고
     */
    @Transactional
    public void transportJobStarted(BaseMessage<TransportJobStartedBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String transportJobName = message.getBody().getTransportJobName();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        Optional<TransportJob> optionalTransportJob = transportJobService.findByTransportJobName(transportJobName);
        if(optionalTransportJob.isPresent()){
            TransportJob transportJob = optionalTransportJob.get();
            TransportJobUpdateCommand command =
                    TransportJobUpdateCommand
                            .builder()
                            .transportJobState(TransportJobState.STARTED.getValue())
                            .transactionInfo(tx)
                            .build();
            transportJob.changeTransportJob(command);

            transportJob = transportJobService.save(transportJob);
            TransportJobHistoryEntity transportJobHistoryEntity = transportJobMapper.toHistoryEntity(transportJob);
            historyService.saveHistory(transportJobHistoryEntity);
            // TODO : 신규 IfEventQueueService.enqueue 호출
        }
    }

    /**
     * 설비들의 communicationState 를 보고 받음
     * 1. 설비 데이터 조회
     * 2. 설비의 상태 변경 < 이건 좀 고민
     * 3. history 생성
     * @param message 받은 메시지
     */
    @Transactional
    public void communicationStateReport(BaseMessage<CommunicationStateReportBody> message) {

        String communicationState = message.getBody().getCommunicationState();
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();

        Optional<Equipment> optionalEquipments = equipmentService.findByEquipmentName(equipmentName);

        if(optionalEquipments.isEmpty()){
            return;
        }
        if(!CommunicationState.isExist(communicationState)){
            return;
        }
        Equipment equipment = optionalEquipments.get();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        CommunicationState state = CommunicationState.valueOf(communicationState);
        CommunicationStateChangeCommand command = CommunicationStateChangeCommand.builder().transactionInfo(tx).communicationState(state).build();
        equipment.communicationStateChange(command);

        equipment = equipmentService.save(equipment);
        EquipmentHistoryEntity equipmentHistoryEntity = equipmentMapper.toHistoryEntity(equipment);
        historyService.saveHistory(equipmentHistoryEntity);

    }

    /**
     * 설비의 communicationState 를 보고 받음
     * 1. 설비 데이터 조회
     * 2. 설비의 상태 변경 < 이건 좀 고민
     * 3. history 생성
     * @param message 받은 메시지
     */
    @Transactional
    public void communicationStateChanged(BaseMessage<CommunicationStateChangedBody> message) {
        String communicationState = message.getBody().getCommunicationState();
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();

        Optional<Equipment> optionalEquipments = equipmentService.findByEquipmentName(equipmentName);

        if(optionalEquipments.isEmpty()){
            return;
        }

        if(!CommunicationState.isExist(communicationState)){
            return;
        }
        Equipment equipment = optionalEquipments.get();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        CommunicationState state = CommunicationState.valueOf(communicationState);
        CommunicationStateChangeCommand command = CommunicationStateChangeCommand.builder().transactionInfo(tx).communicationState(state).build();

        equipment.communicationStateChange(command);
        equipment = equipmentService.save(equipment);
        EquipmentHistoryEntity equipmentHistoryEntity = equipmentMapper.toHistoryEntity(equipment);
        historyService.saveHistory(equipmentHistoryEntity);
    }

    /**
     * equipment의 State 를 보고 받음
     * 1. 설비 데이터 조회
     * 2. 설비의 상태 변경 < 이건 좀 고민
     * 3. history 생성
     * @param message 받은 메시지
     */
    @Transactional
    public void equipmentStateChanged(BaseMessage<EquipmentStateChangedBody> message) {

        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();
        String equipmentState = message.getBody().getEquipmentStateName();

        Optional<Equipment> optionalEquipments = equipmentService.findByEquipmentName(equipmentName);

        if(optionalEquipments.isEmpty()){
            return;
        }

        if(!EquipmentState.isExist(equipmentState)){
            return;
        }
        Equipment equipment = optionalEquipments.get();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        EquipmentState state = EquipmentState.valueOf(equipmentState);
        EquipmentStateChangeCommand command =EquipmentStateChangeCommand.builder().equipmentState(state).transactionInfo(tx).build();
        equipment.equipmentStateChange(command);
        equipment = equipmentService.save(equipment);
        EquipmentHistoryEntity equipmentHistoryEntity = equipmentMapper.toHistoryEntity(equipment);
        historyService.saveHistory(equipmentHistoryEntity);

    }

    /**
     * 설비의 communicationState 를 보고 받음
     * 1. 설비 데이터 조회
     * 2. 설비의 상태 변경 < 이건 좀 고민
     * 3. history 생성
     * @param message 받은 메시지
     */
    @Transactional
    public void equipmentStateReport(BaseMessage<EquipmentStateReportBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();
        String equipmentState = message.getBody().getEquipmentStateName();

        Optional<Equipment> optionalEquipments = equipmentService.findByEquipmentName(equipmentName);

        if(optionalEquipments.isEmpty()){
            return;
        }

        if(!EquipmentState.isExist(equipmentState)){
            return;
        }
        Equipment equipment = optionalEquipments.get();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        EquipmentState state = EquipmentState.valueOf(equipmentState);
        EquipmentStateChangeCommand command =EquipmentStateChangeCommand.builder().equipmentState(state).transactionInfo(tx).build();
        equipment.equipmentStateChange(command);
        equipment = equipmentService.save(equipment);
        EquipmentHistoryEntity equipmentHistoryEntity = equipmentMapper.toHistoryEntity(equipment);
        historyService.saveHistory(equipmentHistoryEntity);
    }

    /**
     * 설비의 communicationState 를 보고 받음
     * 1. 설비 데이터 조회
     * 2. 설비의 상태 변경 < 이건 좀 고민
     * 3. history 생성
     * @param message 받은 메시지
     */
    @Transactional
    public void operationModeChanged(BaseMessage<OperationModeChangedBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();
        String operationModeName = message.getBody().getOperationModeName();

        Optional<Equipment> optionalEquipments = equipmentService.findByEquipmentName(equipmentName);

        if(optionalEquipments.isEmpty()){
            return;
        }

        if(!EquipmentOperationMode.isExist(operationModeName)){
            return;
        }
        Equipment equipment = optionalEquipments.get();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        EquipmentOperationMode mode = EquipmentOperationMode.valueOf(operationModeName);
        EquipmentOperationModeChangeCommand command = EquipmentOperationModeChangeCommand.builder().equipmentOperationMode(mode).transactionInfo(tx).build();
        equipment.operationModeChange(command);
        equipment = equipmentService.save(equipment);
        EquipmentHistoryEntity equipmentHistoryEntity = equipmentMapper.toHistoryEntity(equipment);
        historyService.saveHistory(equipmentHistoryEntity);
    }

    /**
     * 설비의 모드 변경 보고
     * 1. 설비 데이터 조회
     * 2. 설비의 상태 변경 < 이건 좀 고민
     * 3. history 생성
     * @param message 받은 메시지
     */
    @Transactional
    public void operationModeReport(BaseMessage<OperationModeReportBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();
        String operationModeName = message.getBody().getOperationModeName();

        Optional<Equipment> optionalEquipments = equipmentService.findByEquipmentName(equipmentName);

        if(optionalEquipments.isEmpty()){
            return;
        }

        if(!EquipmentOperationMode.isExist(operationModeName)){
            return;
        }
        Equipment equipment = optionalEquipments.get();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        EquipmentOperationMode mode = EquipmentOperationMode.valueOf(operationModeName);
        EquipmentOperationModeChangeCommand command = EquipmentOperationModeChangeCommand.builder().equipmentOperationMode(mode).transactionInfo(tx).build();
        equipment.operationModeChange(command);
        equipment = equipmentService.save(equipment);
        EquipmentHistoryEntity equipmentHistoryEntity = equipmentMapper.toHistoryEntity(equipment);
        historyService.saveHistory(equipmentHistoryEntity);
    }
}