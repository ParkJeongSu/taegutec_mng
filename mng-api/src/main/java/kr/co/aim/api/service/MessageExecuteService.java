package kr.co.aim.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.api.strategy.FactoryIfEventQueueStrategy;
import kr.co.aim.api.strategy.FactoryProcessStrategy;
import kr.co.aim.api.vo.insert.ops.InsertEventQueueReportVo;
import kr.co.aim.api.vo.transportJob.CreateTransportJobVo;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.error.EntityNotFoundException;
import kr.co.aim.common.format.*;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.payload.MaterialDeassignFromCarrier;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.*;
import kr.co.aim.domain.model.*;
import kr.co.aim.domain.model.ProductionOrder;
import kr.co.aim.infra.config.RabbitConfig;
import kr.co.aim.infra.persistence.entity.*;
import kr.co.aim.infra.persistence.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
@Profile({"pex","tex","scheduler"})
public class MessageExecuteService {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final HistoryService historyService;
    private final CarrierService carrierService;
    private final EquipmentService equipmentService;
    private final PortService portService;
    private final ProductionOrderService productionOrderService;
    private final TransportJobService transportJobService;
    private final IfEventQueueService ifEventQueueService;

    private final FactoryProcessStrategy factoryProcessStrategy;
    private final FactoryIfEventQueueStrategy factoryIfEventQueueStrategy;

    private final LotCarrierMappingMapper lotCarrierMappingMapper;
    private final LotMapper lotMapper;
    private final PortMapper portMapper;
    private final EquipmentMapper equipmentMapper;
    private final PortDefMapper portDefMapper;
    private final CarrierMapper carrierMapper;
    private final TransportJobMapper transportJobMapper;
    private final LotService lotService;
    private final LotCarrierMappingService lotCarrierMappingService;

    /**
     * 알람은 log 만 찍음
     *
     *
     * @param message 받은 메시지
     */
    @Transactional(value = "mssqlTransactionManager")
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
    @Transactional(value = "mssqlTransactionManager")
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
    @Transactional(value = "mssqlTransactionManager")
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
    @Transactional(value = "mssqlTransactionManager")
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
    @Transactional(value = "mssqlTransactionManager")
    public BaseMessage<CarrierValidationReplyBody> carrierValidationRequest(BaseMessage<CarrierValidationRequestBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String carrierName = message.getBody().getCarrierName();

        BaseMessage<CarrierValidationReplyBody> reply = new BaseMessage<>();
        reply.setTransactionId(message.getTransactionId());
        reply.setMessageFrom(SystemName.MNG.getValue());
        reply.setMessageOwner(SystemName.MNG.getValue());
        reply.setMessageTo(SystemName.EAS.getValue());
        reply.setEventTime(message.getEventTime());
        reply.setResultMessage("");
        reply.setMessageName(MessageList.CARRIER_VALIDATION_REPLY.getMessageName());
        reply.setResultCode(ResultCode.OK.getValue());
        CarrierValidationReplyBody body = CarrierValidationReplyBody.builder()
                .equipmentName(equipmentName)
                .carrierName(carrierName)
                .build();
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
    @Transactional(value = "mssqlTransactionManager")
    public void carrierLocationChanged(BaseMessage<CarrierLocationChangedBody> message) {
        factoryProcessStrategy.carrierLocationChanged(message);
    }

    @Transactional(value = "mssqlTransactionManager")
    public void carrierScanned(BaseMessage<CarrierScannedBody> message) {
        String messageName = message.getMessageName();

        String transportJobName = message.getBody().getTransportJobName();
        String carrierName = message.getBody().getCarrierName();
        String virtualCarrierName = message.getBody().getVirtualCarrierName();
        String currentEquipmentName = message.getBody().getCurrentEquipmentName();
        String currentZoneName = message.getBody().getCurrentZoneName();
        String currentPositionType = message.getBody().getCurrentPositionType();
        String currentPositionName = message.getBody().getCurrentPositionName();
        String orderId = message.getBody().getOrderId();
        String travelProfile = message.getBody().getTravelProfile();
        String actualWeight = message.getBody().getActualWeight();
        String carrierType = message.getBody().getCarrierType();

        TransactionInfo tx = TransactionInfo.now(messageName,message.getMessageOwner(),message.getResultMessage());
        // insert EventQueue
        try{
            InsertEventQueueReportVo insertEventQueueReportVo
                    = InsertEventQueueReportVo
                    .builder()
                    .transportJobName(transportJobName)
                    .messageName(messageName)
                    //.optionalPort(optionalPort)
                    //.optionalPortDef(optionalPortDef)
                    .carrierName(carrierName)
                    .virtualCarrierName(virtualCarrierName)
                    .actualZoneName(currentZoneName)
                    .actualWeight(actualWeight)
                    .actualRackLocationId(currentPositionName)
//                    .errorTexts()
                    .tx(tx)
                    .build();
            factoryIfEventQueueStrategy.enqueueIfEventQueue(insertEventQueueReportVo);
        }
        catch(Exception e){
            log.error("EventQueue enqueue error",e);
        }
    }

    @Transactional(value = "mssqlTransactionManager")
    public void carrierBlocked(BaseMessage<CarrierBlockedBody> message) {
        String messageName = message.getMessageName();
        String carrierName = message.getBody().getCarrierName();
        String currentPositionName =  message.getBody().getCurrentPositionName();
        TransactionInfo tx = TransactionInfo.now(messageName,message.getMessageOwner(),message.getResultMessage());
        // insert EventQueue
        try{
            InsertEventQueueReportVo insertEventQueueReportVo
                    = InsertEventQueueReportVo
                    .builder()
                    //.transportJobName(transportJobName)
                    .messageName(messageName)
                    //.optionalPort(optionalPort)
                    //.optionalPortDef(optionalPortDef)
                    .carrierName(carrierName)
//                    .actualZoneName()
//                    .actualWeight()
                    .actualRackLocationId(currentPositionName)
//                    .errorTexts()
                    .tx(tx)
                    .build();
            factoryIfEventQueueStrategy.enqueueIfEventQueue(insertEventQueueReportVo);
        }
        catch(Exception e){
            log.error("EventQueue enqueue error",e);
        }
    }

    @Transactional(value = "mssqlTransactionManager")
    public void carrierUnBlocked(BaseMessage<CarrierUnBlockedBody> message) {
        String messageName = message.getMessageName();
        String carrierName = message.getBody().getCarrierName();
        String currentPositionName =  message.getBody().getCurrentPositionName();
        TransactionInfo tx = TransactionInfo.now(messageName,message.getMessageOwner(),message.getResultMessage());
        // insert EventQueue
        try{
            InsertEventQueueReportVo insertEventQueueReportVo
                    = InsertEventQueueReportVo
                    .builder()
                    //.transportJobName(transportJobName)
                    .messageName(messageName)
                    //.optionalPort(optionalPort)
                    //.optionalPortDef(optionalPortDef)
                    .carrierName(carrierName)
//                    .actualZoneName()
//                    .actualWeight()
                    .actualRackLocationId(currentPositionName)
//                    .errorTexts()
                    .tx(tx)
                    .build();
            factoryIfEventQueueStrategy.enqueueIfEventQueue(insertEventQueueReportVo);
        }
        catch(Exception e){
            log.error("EventQueue enqueue error",e);
        }
    }

    /**
     * WareHouse 에 관리하는 Carrier 데이터 생성
     * 1. Carrier 의 정보 조회
     * @param message 받은 메시지
     */
    @Transactional(value = "mssqlTransactionManager")
    public void carrierDataInstall(BaseMessage<CarrierDataInstalledBody> message) {
    }

    /**
     * WareHouse 에 관리하는 Carrier 데이터 삭제
     * 1. Carrier 의 정보 조회
     * @param message 받은 메시지
     */
    @Transactional(value = "mssqlTransactionManager")
    public void carrierDataRemoved(BaseMessage<CarrierDataRemovedBody> message) {
    }

    /**
     * WareHouse 에 관리하는 CarrierList 데이터 생성
     * 1. Carrier 의 정보 조회
     * @param message 받은 메시지
     */
    @Transactional(value = "mssqlTransactionManager")
    public void carrierDataReport(BaseMessage<CarrierDataReportBody> message) {
    }

    /**
     * Carrier 와 Lot 데이터 분리
     * 1. Carrier 의 정보 조회
     * @param message 받은 메시지
     */
    @Transactional(value = "mssqlTransactionManager")
    public void materialDeAssignedFromCarrier(BaseMessage<MaterialDeassignedFromCarrierBody> message) throws Exception{
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
                .quantity(BigDecimal.ZERO)
                .carrierName(carrierName)
                .capaState(CarrierCapaState.EMPTY.getValue())
                .useState(CarrierUseState.AVAILABLE.getValue())
                .build();
        carrier.deAssigned(command);
        carrier = carrierService.save(carrier);
        CarrierHistoryEntity carrierHistoryEntity = carrierMapper.toHistoryEntity(carrier);
        historyService.saveHistory(carrierHistoryEntity);

        // TODO: 자재를 Carrier 로부터 빼내고 Gal로 i/f
        MaterialDeassignFromCarrier materialDeassignFromCarrier =
                MaterialDeassignFromCarrier.builder()
                        .carrierName(carrierName)
                        .equipmentName("")
                        .build();
        String jsonPayload = objectMapper.writeValueAsString(materialDeassignFromCarrier);
        // TODO: 아래의 로직을 factoryProcessStrategy 로 변경
        // powder EventQueue
        try{
            InsertEventQueueReportVo insertEventQueueReportVo
                    = InsertEventQueueReportVo
                    .builder()
//                    .transportJobName(transportJobName)
//                    .messageName(messageName)
//                    .port(port)
//                    .portDef(portDef)
                    .carrierName(carrierName)
//                            .actualZoneName()
//                            .actualWeight()
//                            .actualRackLocationId()
//                            .errorTexts()
                    .tx(tx)
                    .build();
            factoryIfEventQueueStrategy.enqueueIfEventQueue(insertEventQueueReportVo);
        }
        catch(Exception e){
            log.error("EventQueue enqueue error",e);
        }

    }

    /**
     * Carrier 와 Lot 데이터 분리
     * 1. Carrier 의 정보 조회
     * @param message 받은 메시지
     */
    @Transactional(value = "mssqlTransactionManager")
    public void materialAssignedToCarrier(BaseMessage<MaterialAssignedToCarrierBody> message) {
        // TODO: Carrier가 설비에 투입 후에 보고 Lot과의 관계를 끊고 Carrier 의 상태를 Empty로 변경 확인
    }

    /**
     *
     */
    @Transactional(value = "mssqlTransactionManager")
    public void takeOffCarrier(BaseMessage<TakeOffCarrierBody> message) {
        // 비지니스 로직은 없음
        // TO GAL TakeOffCarrier report

        String messageName =  message.getMessageName();
        String transportJobName = message.getBody().getTransportJobName();
        String carrierName = message.getBody().getCarrierName();

        String equipmentName = message.getBody().getCurrentEquipmentName();
        String currentPositionType = message.getBody().getCurrentPositionType();
        String currentPositionName = message.getBody().getCurrentPositionName();

        String portName = "";
        if(StringUtils.equals(PositionTypeName.PORT.getValue(),currentPositionType)){
            portName = currentPositionName;
        }
        TransactionInfo tx = TransactionInfo.now(messageName,SystemName.MNG.getValue(), message.getResultMessage());

        Optional<PortDef> optionalPortDef = portService.findPortDefByEquipmentNameAndPortName(equipmentName, portName);
        PortDef portDef;
        if(optionalPortDef.isEmpty()){
            return;
        }
        portDef = optionalPortDef.get();
        Optional<Port> optionalPort = portService.findPortByEquipmentNameAndPortName(equipmentName, portName);
        Port port;
        if(optionalPort.isEmpty()){
            return;
        }
        port = optionalPort.get();

        // insert EventQueue
        try{
            InsertEventQueueReportVo insertEventQueueReportVo
                    = InsertEventQueueReportVo
                    .builder()
                    .transportJobName(transportJobName)
                    .messageName(messageName)
                    .optionalPort(optionalPort)
                    .optionalPortDef(optionalPortDef)
                    .carrierName(carrierName)
//                    .actualZoneName()
//                    .actualWeight()
//                    .actualRackLocationId()
//                    .errorTexts()
                    .tx(tx)
                    .build();
            factoryIfEventQueueStrategy.enqueueIfEventQueue(insertEventQueueReportVo);
        }
        catch(Exception e){
            log.error("EventQueue enqueue error",e);
        }
    }

    @Transactional(value = "mssqlTransactionManager")
    public BaseMessage<DestinationReplyBody> destinationRequest(BaseMessage<DestinationRequestBody> message) {
        // 과거에 중간 목적지가 있을때, 필요했던 메시지 현재로선 필요없음
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();
        String transportJobName = message.getBody().getTransportJobName();
        String carrierName = message.getBody().getCarrierName();

        return null;
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
    @Transactional(value = "mssqlTransactionManager")
    public BaseMessage<CarrierDispatchRequestBody> loadRequest(BaseMessage<LoadRequestBody> message) {
        return factoryProcessStrategy.loadRequest(message);
    }

    public BaseMessage<TransportJobRequestBody> carrierDispatchRequest(BaseMessage<CarrierDispatchRequestBody> message){
        return factoryProcessStrategy.carrierDispatchRequest(message);
    }
    public BaseMessage<DestinationDispatchRequestBody> unLoadRequest(BaseMessage<UnLoadRequestBody> message){
        return factoryProcessStrategy.unLoadRequest(message);
    }

    public BaseMessage<TransportJobRequestBody> transportOrderRequest(BaseMessage<TransportOrderRequestBody> message){
        return factoryProcessStrategy.transportOrderRequest(message);
    }
    @Transactional(value = "mssqlTransactionManager")
    public BaseMessage<TransportJobRequestBody> destinationDispatchRequest(BaseMessage<DestinationDispatchRequestBody> message){
        // powder Logic
        // 현재 equipment 와 port 위의 carrier 를 창고 혹은 다음 설비로 이동하는 메시지

        // loader 와 unloader port 로직 구분
        // loader port case
        // 현재 containerType을 토대로 바로 WMS ZoneRequest 문의 후 WCS로 반송요청

        // unloader case
        // 1) 시간내에 gal에서 요청을 받지 못한 경우 ex 3 minute 안에 gal에서 요청을 받지 못한 경우
        // 바로 WMS에 문의후 창고에 반환

        // 2) 바로 설비가 아닌 창고로 가야하는 case
        // 바로 WMS에 문의 후 창고에 반환

        // 3) 바로 설비로 가야하는 case
        // WMS 에 문의하는게 아니고 바로 해당 설비로 반송 요청 메시지 생성

        // TODO: WMS 담당자와 message 협의

        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String carrierName = message.getBody().getCarrierName();
        String portType = message.getBody().getPortType();
        String portTransportModeName = message.getBody().getPortTransportMode();

        BaseMessage<CarrierDestinationZoneRequestBody> wmsRequestMessage = zoneRequestBodyBaseMessage();

        Object wmsReply = rabbitTemplate.convertSendAndReceive(
                RabbitConfig.EXCHANGE_WMS,
                RabbitConfig.ROUTING_WMS,
                wmsRequestMessage
        );

        if(ObjectUtils.isEmpty(wmsReply)){
            return null;
        }else if(ObjectUtils.isNotEmpty(wmsReply)){
            BaseMessage<CarrierDestinationZoneReplyBody> replyData = null;
            // 1. 자신에게 맞는 DTO로 역직렬화 TypeReference
            TypeReference<BaseMessage<CarrierDestinationZoneReplyBody>> typeRef = new TypeReference<>() {};
            try {
                // 2. 만약 응답이 byte[]로 왔다면
                if (wmsReply instanceof byte[]) {
                    replyData = objectMapper.readValue((byte[]) wmsReply, typeRef);
                }
                // 3. 만약 응답이 String으로 왔다면
                else if (wmsReply instanceof String) {
                    replyData = objectMapper.readValue((String) wmsReply, typeRef);
                }
                // 4. 컨버터에 의해 이미 변환된 경우 (LinkedHashMap 등)
                else {
                    replyData = objectMapper.convertValue(wmsReply, typeRef);
                }
            } catch (Exception e) {
                throw new RuntimeException("convert BaseMessage<CarrierDestinationZoneReplyBody> error");
            }

            BaseMessage<TransportJobRequestBody> request = new  BaseMessage<>();
            request.setTransactionId(message.getTransactionId());
            request.setMessageFrom(SystemName.MNG.getValue());
            request.setMessageOwner(SystemName.MNG.getValue());
            request.setMessageTo(SystemName.WCS.getValue());
            request.setEventTime(message.getEventTime());
            request.setMessageName(MessageList.TRANSPORT_JOB_REQUEST.getMessageName());
            request.setResultCode(ResultCode.OK.getValue());

            TransportJobRequestBody body =
                    TransportJobRequestBody
                            .builder()
                            .carrierName(carrierName)
                            .build();
            request.setBody(body);
            return request;
        }

        return null;
    }
    private BaseMessage<CarrierDestinationZoneRequestBody> zoneRequestBodyBaseMessage() {
        return null;
    }

    /**
     * unload가 완료 되었음을 보고합니다.
     * 비지니스 로직이 없음 단순히 log 찍음
     * @param message 받은 메시지
     */
    @Transactional(value = "mssqlTransactionManager")
    public void unLoadCompleted(BaseMessage<UnLoadCompletedBody> message) {
        factoryProcessStrategy.unLoadCompleted(message);
    }

    @Transactional(value = "mssqlTransactionManager")
    public BaseMessage<CarrierInfoDownloadSendBody> loadCompleted(BaseMessage<LoadCompletedBody> message) {
        return factoryProcessStrategy.loadCompleted(message);
    }

    /**
     * port 의 접근모드 변경시 보고
     * @param message 받은 메시지
     */
    @Transactional(value = "mssqlTransactionManager")
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
    @Transactional(value = "mssqlTransactionManager")
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
    @Transactional(value = "mssqlTransactionManager")
    public void portStateReport(BaseMessage<PortStateReportBodyList> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        List<PortStateReportEquipment> equipmentList = message.getBody().getEquipmentList();
        for(PortStateReportEquipment equipment : equipmentList){
            String equipmentName = equipment.getEquipmentName();
            List<PortList>portList = equipment.getPortList();
            for(PortList portData : portList){
                String portName = portData.getPortName();
                String portStateName = portData.getPortStateName();
                String portType = portData.getPortType();
                String portTransportMode = portData.getPortTransportMode();
                String carrierName = portData.getCarrierName();
                Optional<Port> optionalPorts = portService.findPortByEquipmentNameAndPortName(equipmentName,portName);

                if(optionalPorts.isEmpty()){
                    //throw new RuntimeException("port not found");
                    //TODO : 추후 수정
                    continue;
                }
                if(!PortState.isExist(portStateName)){
                    //throw new RuntimeException("portState not found");
                    //TODO : 추후 수정
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
    }

    /**
     * port 의 타입 변경시 보고
     * @param message 받은 메시지
     */
    @Transactional(value = "mssqlTransactionManager")
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
        port.portTypeChanged(command);
        port = portService.save(port);
        PortHistoryEntity portHistoryEntity = portMapper.toHistoryEntity(port);
        historyService.saveHistory(portHistoryEntity);
    }

    /**
     * port 의 사용 타입 변경시 보고
     * @param message 받은 메시지
     */
    @Transactional(value = "mssqlTransactionManager")
    public void portUseTypeChanged(BaseMessage<PortUseTypeChangedBody> message) {
        log.info("Business Logic Nothing");
    }


    /**
     * 설비에 투입 후 생산 중단 보고
     *
     * @param message 받은 메시지
     */
    @Transactional(value = "mssqlTransactionManager")
    public void processJobAborted(BaseMessage<ProcessJobAbortedBody> message) {
    }

    /**
     * 설비에 투입 후 보고
     *
     * @param message 받은 메시지
     */
    @Transactional(value = "mssqlTransactionManager")
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

    @Transactional(value = "mssqlTransactionManager")
    public BaseMessage<CarrierInfoDownloadSendBody> recipeReply(BaseMessage<RecipeReplyBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();
        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String carrierName = message.getBody().getCarrierName();
        String orderId = message.getBody().getOrderId();
        String orderLineNumber = message.getBody().getOrderLineNumber();
        String mngKey = message.getBody().getTransactionId();
        RecipeBody recipeBody = message.getBody().getRecipe();

        Optional<LotCarrierMapping> optionalLotCarrierMapping = lotCarrierMappingService.findByCarrierName(carrierName);
        if(optionalLotCarrierMapping.isEmpty()){
            return null;
        }
        LotCarrierMapping lotCarrierMapping = optionalLotCarrierMapping.get();
        Optional<Lot> optionalLot = lotService.findByLotName(lotCarrierMapping.getLotName());
        if(optionalLot.isEmpty()){
            return null;
        }
        Lot lot = optionalLot.get();

        // TODO:내일 아래거 수정
        // 내일 저거 orderid 와 rrn 조회하고 gal requststate가 completed인게 하나면
        // y이고 여러개면 n



        BaseMessage<CarrierInfoDownloadSendBody> request = new BaseMessage<>();
        request.setMessageName(MessageList.CARRIER_INFO_DOWNLOAD_SEND.getMessageName());
        request.setTransactionId(message.getTransactionId());
        request.setMessageFrom(SystemName.MNG.getValue());
        request.setMessageOwner(SystemName.MNG.getValue());
        request.setMessageTo(SystemName.EAS.getValue());
        request.setEventTime(message.getEventTime());
        request.setResultMessage("");
        request.setResultCode(ResultCode.OK.getValue());
        CarrierInfoDownloadSendBody body = CarrierInfoDownloadSendBody
                .builder()
                .equipmentName(equipmentName)
                .portName(portName)
                .carrierName(carrierName)
                .lotName(lot.getLotName())
                .itemName(lot.getItemId())
                .orderId(orderId)
                .orderLineNumber(orderLineNumber)
                .quantity(lotCarrierMapping.getQuantity().toString())
                .totalQuantity(lot.getTotalQuantity().toString())
                .mngKey(mngKey)
                //.lastCarrierFlag()
                .recipe(recipeBody)
                .build();
        request.setBody(body);
        return request;
    }
    /**
     * 설비에 투입 후 완료 보고
     *
     * @param message 받은 메시지
     */
    @Transactional(value = "mssqlTransactionManager")
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
    @Transactional(value = "mssqlTransactionManager")
    public void processJobDataReport(BaseMessage<ProcessJobDataReportBody> message) {
    }

    /**
     * SCS 시스템이 켜질때 보고
     * 1. transportJob 조회
     * 2. 없으면 데이터 생성
     *
     * @param message 받은 메시지
     */
    @Transactional(value = "mssqlTransactionManager")
    public void activeTransportJobReport(BaseMessage<ActiveTransportJobReportBody> message) {
        log.info("activeTransportJobReport");
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();
        List<TransportJobList> transportJobList = message.getBody().getTransportJobList();
        LocalDateTime now = LocalDateTime.now();
        if(ObjectUtils.isNotEmpty(transportJobList)){
            for(TransportJobList transportJob : transportJobList){
                String transportJobName = transportJob.getTransportJobName();
                String transportType = transportJob.getTransportType();
                String carrierName = transportJob.getCarrierName();
                String sourceEquipmentName=transportJob.getSourceEquipmentName();
                String sourcePositionType = transportJob.getSourcePositionType();
                String sourcePositionName = transportJob.getSourcePositionName();
                String sourceZoneName = transportJob.getSourceZoneName();
                String currentEquipmentName = transportJob.getCurrentEquipmentName();
                String currentPositionType = transportJob.getCurrentPositionType();
                String currentPositionName = transportJob.getCurrentPositionName();
                String currentZoneName  = transportJob.getCurrentZoneName();
                String destinationEquipmentName = transportJob.getDestinationEquipmentName();
                String destinationPositionType = transportJob.getDestinationPositionType();
                String destinationPositionName = transportJob.getDestinationPositionName();
                String destinationZoneName = transportJob.getDestinationZoneName();
                String priority = transportJob.getPriority();
                String orderId = transportJob.getOrderId();
                String requestSource = transportJob.getRequestSource();
                String travelProfile = transportJob.getTravelProfile();
                String actualWeight = transportJob.getActualWeight();
                String carrierType  = transportJob.getCarrierType();

                Optional<TransportJob> optionalTransportJob = transportJobService.findByTransportJobName(transportJobName);

                if(optionalTransportJob.isPresent()){
                    continue;
                }
                else{
                    TransportJobCreateCommand command =
                            TransportJobCreateCommand
                                    .builder()
                                    .transportJobName(transportJobName)
                                    .carrierName(carrierName)
                                    .transportType(transportType)
                                    .transportJobState(TransportJobState.STARTED.getValue())
                                    .carrierType(carrierType)
                                    .travelProfile(travelProfile)
                                    .sourceEquipmentName(sourceEquipmentName)
                                    .sourcePortName(sourcePositionName)
                                    .sourceZoneName(sourceZoneName)
                                    .sourcePositionTypeName(sourcePositionType)
                                    .sourcePositionName(sourcePositionName)
                                    .destinationEquipmentName(destinationEquipmentName)
                                    .destinationPortName(destinationPositionName)
                                    .destinationZoneName(destinationZoneName)
                                    .destinationPositionTypeName(destinationPositionType)
                                    .destinationPositionName(destinationPositionName)
                                    .priority( StringUtils.isNotBlank(priority) ? Integer.parseInt(priority) : 0 )
                                    //.errorCode()
                                    //.errorText()
                                    .requestSource(requestSource)
                                    .createTime(now)
                                    //.departedTime()
                                    //.arrivedTime()
                                    //.reasonCode()
                                    //.orderId()
                                    .build();
                    List<TransportJobCreateCommand> transportJobCreateCommandList =  new ArrayList<>();
                    transportJobCreateCommandList.add(command);
                    CreateTransportJobVo vo =
                            CreateTransportJobVo
                                    .builder()
                                    .transportJobCreateCommandList(transportJobCreateCommandList)
                                    .build();
                    transportJobService.createTransportJob(vo);
                }
            }
        }
    }

    /**
     * SCS 의 목적지 변경 후 보고 메시지
     * 1. transportJob 조회
     * 2. 없으면 데이터 생성
     *
     * @param message 받은 메시지
     */
    @Transactional(value = "mssqlTransactionManager")
    public void destinationChanged(BaseMessage<DestinationChangedBody> message) {
        log.info("destinationChanged");

        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();
        String transportJobName = message.getBody().getTransportJobName(); // DetailName
        String carrierName = message.getBody().getCarrierName();
        String oldDestinationEquipmentName = message.getBody().getOldDestinationEquipmentName();
        String oldDestinationZoneName = message.getBody().getOldDestinationZoneName();
        String oldDestinationPositionType = message.getBody().getOldDestinationPositionType();
        String oldDestinationPositionName = message.getBody().getOldDestinationPositionName();

        String newDestinationEquipmentName = message.getBody().getNewDestinationEquipmentName();
        String newDestinationPortName = "";
        String newDestinationZoneName = message.getBody().getNewDestinationZoneName();
        String newDestinationPositionType = message.getBody().getNewDestinationPositionType();
        String newDestinationPositionName = message.getBody().getNewDestinationPositionName();

        if(StringUtils.equals(PositionTypeName.PORT.getValue(),newDestinationPositionType)){
            newDestinationPortName = newDestinationPositionName;
        }

        Optional<TransportJob> optionalTransportJob
                = transportJobService.findByTransportJobName(transportJobName);
        if(optionalTransportJob.isEmpty()){
            return;
        }
        TransportJob transportJob = optionalTransportJob.get();

        TransactionInfo tx =  TransactionInfo.now(eventName,eventUser,eventComment);

        TransportJobUpdateCommand command =
                TransportJobUpdateCommand
                        .builder()
                        .transportJobState(transportJob.getTransportJobState())
                        .destinationEquipmentName(newDestinationEquipmentName)
                        .destinationPortName(newDestinationPortName)
                        .destinationZoneName(newDestinationZoneName)
                        .destinationPositionTypeName(newDestinationPositionType)
                        .destinationPositionName(newDestinationPositionName)
                        .transactionInfo(tx)
                        .build();
        transportJob.changeDestination(command);
        transportJob = transportJobService.save(transportJob);
        TransportJobHistoryEntity transportJobHistoryEntity = transportJobMapper.toHistoryEntity(transportJob);
        historyService.saveHistory(transportJobHistoryEntity);
    }

    /**
     * SCS 에서 Warehouse 의 Zone 정보 변경시 보고
     *
     * @param message 받은 메시지
     */
    @Transactional(value = "mssqlTransactionManager")
    public void inventoryZoneDataReport(BaseMessage<InventoryZoneDataReport> message) {
    }

    /**
     * 반송 취소처리 완료 보고
     *
     */
    @Transactional(value = "mssqlTransactionManager")
    public void transportJobCancelCompleted(BaseMessage<TransportJobCancelCompletedBody> message) {
        factoryProcessStrategy.transportJobCancelCompleted(message);
    }

    /**
     * 반송 취소처리 실패 보고 이 경우 그러면 최종적으로 어떻게 되는거지?
     *
     */
    @Transactional(value = "mssqlTransactionManager")
    public void transportJobCancelFailed() {
        // 반송잡 취소가 fail 되는 상황 확인 후 추가
    }

    /**
     * 반송 취소처리 시작 보고
     * 내가 걱정하는게 이 취소 시나리오가 Mixing 같이 여러개의 job이 만들어진 시나리오에서 어떻게 해야할지 고민..
     */
    @Transactional(value = "mssqlTransactionManager")
    public void transportJobCancelStarted(BaseMessage<TransportJobCancelStartedBody> message) {
        // 반송job 이 취소가 시작 되는 시나리오 현재 없음
    }

    /**
     * 반송 잡이 정상적으로 종료되었음을 보고
     * 1. transportDetailJob 조회
     * 2. transportDetailJob 상태 completed 로 변경
     * 3. 모든 transportDetailJob 이 completed 라면, transportJob -> completed 변경
     */
    @Transactional(value = "mssqlTransactionManager")
    public void transportJobCompleted(BaseMessage<TransportJobCompletedBody> message) {
        factoryProcessStrategy.transportJobCompleted(message);
    }

    /**
     * 반송 잡의 처리 여부를 반환
     * reply 가 정상처리라면,
     * 반송잡의 상태를 Accepted 로 변경
     *
     * reply 가 실패처리라면
     * 반송잡의 상태를 rejected로 변경
     */
    @Transactional(value = "mssqlTransactionManager")
    public void transportJobReply(BaseMessage<TransportJobReplyBody> message) {
        factoryProcessStrategy.transportJobReply(message);
    }

    /**
     * 요청한 반송잡이 첫시작되는 시점 보고
     */
    @Transactional(value = "mssqlTransactionManager")
    public void transportJobStarted(BaseMessage<TransportJobStartedBody> message) {
        factoryProcessStrategy.transportJobStarted(message);
    }

    /**
     * 설비들의 communicationState 를 보고 받음
     * 1. 설비 데이터 조회
     * 2. 설비의 상태 변경 < 이건 좀 고민
     * 3. history 생성
     * @param message 받은 메시지
     */
    @Transactional(value = "mssqlTransactionManager")
    public void communicationStateReport(BaseMessage<CommunicationStateReportBodyList> message) {

        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        List<CommunicationStateReportBody> equipmentList = message.getBody().getEquipmentList();
        for(CommunicationStateReportBody body : equipmentList){
            String equipmentName = body.getEquipmentName();
            String communicationState = body.getCommunicationState();

            Optional<Equipment> optionalEquipments = equipmentService.findEquipmentByEquipmentName(equipmentName);

            if(optionalEquipments.isEmpty()){
                throw new RuntimeException("Equipment not found");
            }
            if(!CommunicationState.isExist(communicationState)){
                throw new RuntimeException("CommunicationState not found");
            }
            Equipment equipment = optionalEquipments.get();

            CommunicationState state = CommunicationState.valueOf(communicationState);
            CommunicationStateChangeCommand command = CommunicationStateChangeCommand.builder().transactionInfo(tx).communicationState(state).build();
            equipment.communicationStateChange(command);

            equipment = equipmentService.save(equipment);
            EquipmentHistoryEntity equipmentHistoryEntity = equipmentMapper.toHistoryEntity(equipment);
            historyService.saveHistory(equipmentHistoryEntity);
        }
    }

    /**
     * 설비의 communicationState 를 보고 받음
     * 1. 설비 데이터 조회
     * 2. 설비의 상태 변경 < 이건 좀 고민
     * 3. history 생성
     * @param message 받은 메시지
     */
    @Transactional(value = "mssqlTransactionManager")
    public void communicationStateChanged(BaseMessage<CommunicationStateChangedBody> message) {
        String communicationState = message.getBody().getCommunicationState();
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();

        Optional<Equipment> optionalEquipments = equipmentService.findEquipmentByEquipmentName(equipmentName);

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
    @Transactional(value = "mssqlTransactionManager")
    public void equipmentStateChanged(BaseMessage<EquipmentStateChangedBody> message) {

        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();
        String equipmentState = message.getBody().getEquipmentStateName();

        Optional<Equipment> optionalEquipments = equipmentService.findEquipmentByEquipmentName(equipmentName);

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
    @Transactional(value = "mssqlTransactionManager")
    public void equipmentStateReport(BaseMessage<EquipmentStateReportBodyList> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();
        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);

        List<EquipmentStateReportBody> bodyList = message.getBody().getEquipmentList();
        for(EquipmentStateReportBody body : bodyList){
            String equipmentName = body.getEquipmentName();
            String equipmentType = body.getEquipmentType();
            String equipmentState = body.getEquipmentStateName();
            String communicationState = body.getCommunicationState();

            // TODO: 비관적 LOCK 고민
            Optional<Equipment> optionalEquipments = equipmentService.findEquipmentByEquipmentName(equipmentName);

            if(optionalEquipments.isEmpty()){
                //throw new RuntimeException("Equipment not found");
                // TODO : 현재는 continue 추후 고민
                continue;
            }

            if(!EquipmentState.isExist(equipmentState)){
                // TODO : 현재는 continue 추후 고민
                //throw new RuntimeException("EquipmentState not found");
                continue;
            }
            Equipment equipment = optionalEquipments.get();
            EquipmentState state = EquipmentState.valueOf(equipmentState);
            EquipmentStateReportCommand command =
                    EquipmentStateReportCommand
                            .builder()
                            .equipmentState(state)
                            .communicationState(communicationState)
                            .transactionInfo(tx).build();
            equipment.equipmentStateReport(command);
            equipment = equipmentService.save(equipment);
            EquipmentHistoryEntity equipmentHistoryEntity = equipmentMapper.toHistoryEntity(equipment);
            historyService.saveHistory(equipmentHistoryEntity);
        }

    }

    /**
     * 설비의 communicationState 를 보고 받음
     * 1. 설비 데이터 조회
     * 2. 설비의 상태 변경 < 이건 좀 고민
     * 3. history 생성
     * @param message 받은 메시지
     */
    @Transactional(value = "mssqlTransactionManager")
    public void operationModeChanged(BaseMessage<OperationModeChangedBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();
        String operationModeName = message.getBody().getOperationModeName();

        Optional<Equipment> optionalEquipments = equipmentService.findEquipmentByEquipmentName(equipmentName);

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
    @Transactional(value = "mssqlTransactionManager")
    public void operationModeReport(BaseMessage<OperationModeReportBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();
        String operationModeName = message.getBody().getOperationModeName();

        Optional<Equipment> optionalEquipments = equipmentService.findEquipmentByEquipmentName(equipmentName);

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

    public BaseMessage<AreYouThereReplyBody> areYouThereRequest(BaseMessage<AreYouThereRequestBody> message) {
        BaseMessage<AreYouThereReplyBody> reply = new BaseMessage<>();
        AreYouThereReplyBody body = new AreYouThereReplyBody();

        reply.setEventTime(message.getEventTime());
        reply.setMessageFrom(SystemName.MNG.getValue());
        reply.setMessageName(MessageList.ARE_YOU_THERE_REPLY.getMessageName());
        reply.setMessageOwner(message.getMessageOwner());
        reply.setMessageTo(message.getMessageFrom());
        reply.setResultCode(ResultCode.OK.getValue());
        reply.setResultMessage("");
        reply.setTransactionId(message.getTransactionId());
        reply.setBody(body);
        return reply;
    }

    public BaseMessage<ConnectionBody> connectionCheckRequest(BaseMessage<ConnectionCheckBody> message) {
        BaseMessage<ConnectionBody> reply = new BaseMessage<>();
        ConnectionBody body = new  ConnectionBody();

        reply.setEventTime(message.getEventTime());
        reply.setMessageFrom(SystemName.MNG.getValue());
        reply.setMessageName(MessageList.CONNECTION.getMessageName());
        reply.setMessageOwner(message.getMessageOwner());
        reply.setMessageTo(message.getMessageFrom());
        reply.setResultCode(ResultCode.OK.getValue());
        reply.setResultMessage("");
        reply.setTransactionId(message.getTransactionId());
        reply.setBody(body);
        return reply;
    }



    /**
     * WMS 의 오더의 시작보고
     *
     * @param message 받은 메시지
     */
    @Transactional(value = "mssqlTransactionManager")
    public BaseMessage<OrderReleaseReplyBody> orderReleaseRequest(BaseMessage<OrderReleaseRequestBody> message) {
        String messageName = message.getMessageName();
        String transactionId = message.getTransactionId();
        String messageFrom = message.getMessageFrom();
        String messageOwner = message.getMessageOwner();
        String messageTo = message.getMessageTo();
        String eventTime = message.getEventTime();
        String resultCode = message.getResultCode();
        String resultMessage = message.getResultMessage();

        Long productionOrderId = message.getBody().getId();
        String orderId = message.getBody().getOrderId();

        Optional<ProductionOrder> optionalProductionOrder = productionOrderService.findById(productionOrderId);

        // 🌟 [핵심 수정] 상대방에게 돌려줄 응답용 Body 객체를 먼저 생성합니다.
        OrderReleaseReplyBody replyBody = OrderReleaseReplyBody.builder()
                .id(productionOrderId)
                .orderId(orderId)
                .build();

        // [검증 1] 데이터가 없는 경우 -> 비즈니스 NG 반환 (롤백 불필요)
        if (optionalProductionOrder.isEmpty()) {
            return createReplyMessage(message, ResultCode.NG, "Production Order를 찾을 수 없습니다.",replyBody);
        }

        ProductionOrder productionOrder = optionalProductionOrder.get();

        // [검증 2] 타입이 일치하지 않는 경우 -> 비즈니스 NG 반환 (롤백 불필요)
        if (!StringUtils.equals(ProductionOrderType.MATERIAL_INBOUND.getValue(), productionOrder.getProductionOrderType())) {
            return createReplyMessage(message, ResultCode.NG, "올바르지 않은 오더 타입입니다.",replyBody);
        }

        // ----------------------------------------------------
        // 실질적인 DB 상태 변경 로직 위치 (예: 오더 시작 상태 변경 등)
        // ----------------------------------------------------
        // 만약 이 아래 로직을 수행하다가 NullPointerException이나 DB 락 등의
        // 예기치 못한 에러가 발생하면, 잡지 않고 던져지므로 트랜잭션은 자동으로 롤백됩니다.
        TransactionInfo tx = TransactionInfo.now(messageName,SystemName.MNG.getValue(),resultMessage);
        LotCreateCommand command
                =
                LotCreateCommand
                        .builder()
                        .transactionInfo(tx)
                        .lotName(productionOrder.getLotName())
                        .originalLotName(productionOrder.getLotName())
                        .lotStatus(LotStatus.STOCK.getValue())
                        .itemId(productionOrder.getItemName())
                        .totalQuantity(productionOrder.getPlanQuantity())
                        .holdState(HoldState.NOT_ON_HOLD.getValue())
                        .reasonCode("")
                        .build();
        Lot lot = Lot.create(command);
        lot = lotService.save(lot);
        LotHistoryEntity historyEntity = lotMapper.toHistoryEntity(lot);
        historyService.saveHistory(historyEntity);

        // 모든 검증과 로직이 성공하면 OK 반환
        return createReplyMessage(message, ResultCode.OK, "",replyBody);
    }

    /**
     * 설비의 communicationState 를 보고 받음
     * 1. 설비 데이터 조회
     * 2. 설비의 상태 변경 < 이건 좀 고민
     * 3. history 생성
     * @param message 받은 메시지
     */
    @Transactional(value = "mssqlTransactionManager")
    public BaseMessage<MaterialAssignCarrierReplyBody> materialAssignCarrierRequest(BaseMessage<MaterialAssignCarrierRequestBody> message) {

        String messageName = message.getMessageName();
        String transactionId = message.getTransactionId();
        String messageFrom = message.getMessageFrom();
        String messageOwner = message.getMessageOwner();
        String messageTo = message.getMessageTo();
        String eventTime = message.getEventTime();
        String resultCode = message.getResultCode();
        String resultMessage = message.getResultMessage();

        String carrierName = message.getBody().getCarrierName();
        Long productionOrderId = message.getBody().getId();
        String orderId = message.getBody().getOrderId();
        List<Material> materialList = message.getBody().getMaterialList();

        Optional<ProductionOrder> optionalProductionOrder = productionOrderService.findById(productionOrderId);

        // 🌟 [핵심 수정] 상대방에게 돌려줄 응답용 Body 객체를 먼저 생성합니다.
        MaterialAssignCarrierReplyBody replyBody =
                MaterialAssignCarrierReplyBody
                        .builder()
                        .id(productionOrderId)
                        .carrierName(carrierName)
                        .orderId(orderId)
                        .materialList(materialList)
                        .build();

        // [검증 1] 데이터가 없는 경우 -> 비즈니스 NG 반환 (롤백 불필요)
        if (optionalProductionOrder.isEmpty()) {
            return createReplyMessage(message, ResultCode.NG, "Production Order를 찾을 수 없습니다.",replyBody);
        }

        Optional<Carrier> optionalCarrier = carrierService.findByCarrierName(carrierName);

        if(materialList.isEmpty()){
            return createReplyMessage(message,ResultCode.NG,"CarrierName 을 찾을수 없습니다.",replyBody);
        }

        Carrier carrier = optionalCarrier.get();

        ProductionOrder productionOrder = optionalProductionOrder.get();
        TransactionInfo tx = TransactionInfo.now(messageName,SystemName.MNG.getValue(),resultMessage);
        for(Material material : materialList){
            String materialName = material.getMaterialName();
            String materialType = material.getMaterialType();
            String quantityStr = material.getQuantity();
            String item = material.getItem();
            String lotName = material.getLotName();
            BigDecimal quantity = null;

            if (quantityStr != null && !quantityStr.trim().isEmpty()) {
                try {
                    quantity = new BigDecimal(quantityStr.trim());
                } catch (NumberFormatException e) {
                    // 로그를 남기거나 기본값(예: BigDecimal.ZERO)을 설정할 수 있습니다.
                    quantity = BigDecimal.ZERO;
                }
            }

            LotCarrierMappingCreateCommand command
                    = LotCarrierMappingCreateCommand
                    .builder()
                    .transactionInfo(tx)
                    .lotName(lotName)
                    .carrierName(carrierName)
                    .orderId(orderId)
                    //.orderLineNumber()
                    .productionOrderId(productionOrderId)
                    .productionStatus(ProductionStatus.WAIT.getValue())
                    .processStatus(ProcessStatus.WAIT.getValue())
                    .quantity(quantity)
                    .galQuantity(quantity)
                    //.mngKey()
                    //.jobStartTime()
                    //.jobEndTime()
                    //.mantiRequestState()
                    //.mantiRequestTime()
                    //.mantiReplyTime()
                    //.rrnRequestState()
                    //.rrnRequestTime()
                    //.rrnReplyTime()
                    //.holdState()
                    //.reasonCode()
                    .build();
            LotCarrierMapping lotCarrierMapping = LotCarrierMapping.create(command);
            lotCarrierMapping = lotCarrierMappingService.save(lotCarrierMapping);
            LotCarrierMappingHistoryEntity lotCarrierMappingHistoryEntity = lotCarrierMappingMapper.toHistoryEntity(lotCarrierMapping);
            historyService.saveHistory(lotCarrierMappingHistoryEntity);

            CarrierChangeCommand carrierChangeCommand
                    =
                    CarrierChangeCommand
                            .builder()
                            .transactionInfo(tx)
                            .quantity(quantity)
                            .galQuantity(quantity)
                            .build();
            carrier.change(carrierChangeCommand);
            carrier = carrierService.save(carrier);
            CarrierHistoryEntity carrierHistoryEntity = carrierMapper.toHistoryEntity(carrier);
            historyService.saveHistory(carrierHistoryEntity);
        }

        return createReplyMessage(message, ResultCode.OK, "",replyBody);
    }

    /**
     * 설비의 communicationState 를 보고 받음
     * 1. 설비 데이터 조회
     * 2. 설비의 상태 변경 < 이건 좀 고민
     * 3. history 생성
     * @param message 받은 메시지
     */
    @Transactional(value = "mssqlTransactionManager")
    public BaseMessage<TransportJobReplyByWMSBody> transportJobRequestByWMS(BaseMessage<TransportJobRequestByWMSBody> message) {

        // 1. message parsing
        // 2. WCS Message Request (TransportJobRequest)
        // 3. WMS Message Return


        BaseMessage<TransportJobReplyByWMSBody> reply = new BaseMessage<>();
        TransportJobReplyByWMSBody body = new TransportJobReplyByWMSBody();

        body.setTransportJobName(message.getBody().getTransportJobName());
        body.setCarrierName(message.getBody().getCarrierName());
        body.setSourceEquipmentName(message.getBody().getSourceEquipmentName());
        body.setSourceZoneName(message.getBody().getSourceZoneName());
        body.setSourcePositionType(message.getBody().getSourcePositionType());
        body.setSourcePositionName(message.getBody().getSourcePositionName());
        body.setDestinationEquipmentName(message.getBody().getDestinationEquipmentName());
        body.setDestinationZoneName(message.getBody().getDestinationZoneName());
        body.setDestinationPositionType(message.getBody().getDestinationPositionType());
        body.setDestinationPositionName(message.getBody().getDestinationPositionName());
        body.setPriority(message.getBody().getPriority());
        body.setCarrierType(message.getBody().getCarrierType());
        body.setOrderId(message.getBody().getOrderId());
        body.setOrderLineNumber(message.getBody().getOrderLineNumber());

        reply.setEventTime(message.getEventTime());
        reply.setMessageFrom(SystemName.MNG.getValue());
        reply.setMessageName(MessageList.TRANSPORT_JOB_REPLY_BY_WMS.getMessageName());
        reply.setMessageOwner(message.getMessageOwner());
        reply.setMessageTo(message.getMessageFrom());
        reply.setResultCode(ResultCode.OK.getValue());
        reply.setResultMessage("");
        reply.setTransactionId(message.getTransactionId());
        reply.setBody(body);
        return reply;
    }


    /**
     * 응답 메시지 빌더 공통 메서드
     */
    private <T> BaseMessage<T> createReplyMessage(BaseMessage<?> message, ResultCode resultCode, String resultMessage,T replyBody) {
        String messageName = message.getMessageName();
        String replyMessageName = "";
        if (messageName != null && messageName.endsWith("Request")) {
            replyMessageName = messageName.replace("Request", "Reply");
        } else if (messageName != null && messageName.contains("_REQUEST")) {
            replyMessageName = messageName.replace("_REQUEST", "_REPLY");
        }

        BaseMessage<T> reply = new BaseMessage<>();
        reply.setEventTime(message.getEventTime());
        reply.setMessageFrom(SystemName.MNG.getValue());
        reply.setMessageName(replyMessageName);
        reply.setMessageOwner(message.getMessageOwner());
        reply.setMessageTo(message.getMessageFrom());
        reply.setResultCode(resultCode.getValue());
        reply.setResultMessage(resultMessage);
        reply.setTransactionId(message.getTransactionId());
        reply.setBody(replyBody);

        return reply;
    }


}