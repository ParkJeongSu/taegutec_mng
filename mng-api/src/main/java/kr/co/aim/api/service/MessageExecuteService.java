package kr.co.aim.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.api.strategy.FactoryIfEventQueueStrategy;
import kr.co.aim.api.strategy.FactoryProcessStrategy;
import kr.co.aim.api.vo.insert.ops.InsertEventQueueReportVo;
import kr.co.aim.api.vo.powder.ops.PowderEventQueueReportVo;
import kr.co.aim.api.vo.transportJob.CreateTransportJobVo;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.format.*;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.*;
import kr.co.aim.domain.model.*;
import kr.co.aim.domain.model.ProductionOrder;
import kr.co.aim.domain.repository.*;
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
import java.util.Objects;
import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
@Profile({"pex","tex"})
public class MessageExecuteService {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final WhereDispatchService whereDispatchService;
    private final ProcessJobService processJobService;
    private final HistoryService historyService;
    private final CarrierRepository carrierRepository;
    private final EquipmentRepository equipmentRepository;
    private final PortRepository portRepository;
    private final PortDefRepository portDefRepository;
    private final ProductionOrderRepository productionOrderRepository;
    private final ProductionOrderProcessService productionOrderProcessService;
    private final TransportJobService transportJobService;
    private final TransportJobRepository transportJobRepository;
    private final IfEventQueueService ifEventQueueService;

    private final FactoryProcessStrategy factoryProcessStrategy;
    private final FactoryIfEventQueueStrategy factoryIfEventQueueStrategy;

    private final LotCarrierMappingMapper lotCarrierMappingMapper;
    private final LotMapper lotMapper;
    private final PortMapper portMapper;
    private final ProductionOrderMapper productionOrderMapper;
    private final EquipmentMapper equipmentMapper;
    private final PortDefMapper portDefMapper;
    private final CarrierMapper carrierMapper;
    private final TransportJobMapper transportJobMapper;
    private final LotRepository lotRepository;
    private final LotCarrierMappingRepository lotCarrierMappingRepository;
    private final NamingRuleService namingRuleService;

    /**
     * 알람은 log 만 찍음
     *
     *
     * @param message 받은 메시지
     */
    @Transactional(value = "mssqlTransactionManager")
    public void alarmReport(BaseMessage<AlarmReportBody> message) {
        String alarmCode = message.getBody().getAlarmCode();
        String messageName = message.getMessageName();
        String messageOwner = message.getMessageOwner();
        String resultMessage =  message.getResultMessage();
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
     */
    @Transactional(value = "mssqlTransactionManager")
    public void carrierCleanJobCanceled(BaseMessage<CarrierCleanJobCanceledBody> message) {
        // 현재 clean 설비는 예정되어있지 않음
    }

    /**
     * Carrier 의 세정 작업이 시작되었음을 보고
     * 1. Carrier 조회
     * 2. Carrier Clean Start time 을 현재 시간으로 변경
     *
     * @param message 받은 메시지
     */
    @Transactional(value = "mssqlTransactionManager")
    public void carrierCleanJobStarted(BaseMessage<CarrierCleanJobStartedBody> message) {
        // 현재 clean 설비는 예정되어있지 않음
        String messageName = message.getMessageName();
        String messageOwner = message.getMessageOwner();
        String resultMessage =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String carrierName = message.getBody().getCarrierName();

        Optional<Carrier> optionalCarriers = carrierRepository.findByCarrierName(carrierName);
        if(optionalCarriers.isEmpty()){
            return;
        }
        Carrier carrier = optionalCarriers.get();

        TransactionInfo tx = TransactionInfo.now(messageName,messageOwner,resultMessage);
        CleanJobStartedCommand command = CleanJobStartedCommand.builder()
                .transactionInfo(tx)
                .equipmentName(equipmentName)
                .portName(portName)
                .build();

        carrier.cleanJobStarted(command);
        carrierRepository.save(carrier);
    }

    /**
     * Carrier 의 세정작업이 종료 되었음을 보고
     * 1. Carrier 의 Clean State 를 Clean 으로 수정
     * 2. Carrier 의 Clean End Time 을 현재 시간으로 수정
     * 3 만일 Container 라면, CarrierDetailType 의 값을 null 로 수정
     *
     * @param message 받은 메시지
     */
    @Transactional(value = "mssqlTransactionManager")
    public void carrierCleanJobEnded(BaseMessage<CarrierCleanJobEndedBody> message) {
        // 현재 clean 설비는 예정되어있지 않음
        String messageName = message.getMessageName();
        String messageOwner = message.getMessageOwner();
        String resultMessage =  message.getResultMessage();


        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String carrierName = message.getBody().getCarrierName();

        Optional<Carrier> optionalCarriers = carrierRepository.findByCarrierName(carrierName);
        if(optionalCarriers.isEmpty()){
            return;
        }
        Carrier carrier = optionalCarriers.get();

        TransactionInfo tx = TransactionInfo.now(messageName,messageOwner,resultMessage);
        CleanJobEndedCommand command = CleanJobEndedCommand.builder()
                .transactionInfo(tx)
                .equipmentName(equipmentName)
                .portName(portName)
                .build();

        carrier.cleanJobEnded(command);
        carrierRepository.save(carrier);
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
        return null;
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
    public void carrierInfoDownloadSendReply(BaseMessage<CarrierInfoDownloadSendReplyBody> message) {
        log.info("business log nothing");
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
        Optional<PortDef> optionalPortDef = portDefRepository.findByEquipmentNameAndPortName(currentEquipmentName,currentPositionName);
        Optional<Port> optionalPort = portRepository.findByEquipmentNameAndPortName(currentEquipmentName,currentPositionName);
        PortDef portDef = null;
        Port port = null;
        if(optionalPortDef.isPresent()){
            portDef = optionalPortDef.get();
        }
        if(optionalPort.isPresent()){
            port = optionalPort.get();
        }

        Optional<TransportJob> optionalTransportJob = transportJobRepository.findByTransportJobName(transportJobName);
        if(optionalTransportJob.isPresent()){
            TransportJob transportJob = optionalTransportJob.get();
            TransportJobUpdateCommand command =
                    TransportJobUpdateCommand
                            .builder()
                            .transactionInfo(tx)
                            .build();
            transportJob.changeTransportJob(command);
            transportJob = transportJobRepository.save(transportJob);
            TransportJobHistoryEntity transportJobHistoryEntity = transportJobMapper.toHistoryEntity(transportJob);
            historyService.saveHistory(transportJobHistoryEntity);
        }

        // insert EventQueue
        try{
            InsertEventQueueReportVo insertEventQueueReportVo
                    = InsertEventQueueReportVo
                    .builder()
                    .transportJobName(transportJobName)
                    .messageName(messageName)
                    .portDef(portDef)
                    .port(port)
                    .carrierName(carrierName)
                    .virtualCarrierName(virtualCarrierName)
                    .actualZoneName(currentZoneName)
                    .actualWeight(actualWeight)
                    .actualRackLocationId(currentPositionName)
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
                    .messageName(messageName)
                    .carrierName(carrierName)
                    .actualRackLocationId(currentPositionName)
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
                    .messageName(messageName)
                    .carrierName(carrierName)
                    .actualRackLocationId(currentPositionName)
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
    public void materialUnAssignedFromCarrier(BaseMessage<MaterialUnassignedFromCarrierBody> message){
        String messageName = message.getMessageName();
        String messageOwner = message.getMessageOwner();
        String resultMessage =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String carrierName = message.getBody().getCarrierName();
        String productionTaskId = message.getBody().getProductionTaskId();
        String orderId = message.getBody().getOrderId();
        String orderLineNumber = message.getBody().getOrderLineNumber();
        BigDecimal quantity = message.getBody().getQuantity();
        String carrierStatus = message.getBody().getCarrierStatus();
        String mngKey = message.getBody().getMngKey();

        Optional<Carrier> optionalCarriers = carrierRepository.findByCarrierName(carrierName);
        Carrier carrier;
        if(optionalCarriers.isEmpty()){
            return;
        }
        TransactionInfo tx = TransactionInfo.now(messageName,messageOwner,resultMessage);
        carrier = optionalCarriers.get();
        CarrierDeassignCommand command = null;
        if(Objects.equals(quantity, BigDecimal.ZERO)){
            command = CarrierDeassignCommand.builder()
                    .transactionInfo(tx)
                    .quantity(BigDecimal.ZERO)
                    .carrierName(carrierName)
                    .capaState(CarrierCapaState.EMPTY.getValue())
                    .useState(CarrierUseState.AVAILABLE.getValue())
                    .build();
        }
        else{
            command = CarrierDeassignCommand.builder()
                    .transactionInfo(tx)
                    .quantity(quantity)
                    .carrierName(carrierName)
                    .capaState(CarrierCapaState.FULL.getValue())
                    .useState(CarrierUseState.IN_USE.getValue())
                    .build();
        }
        carrier.deAssigned(command);
        carrier = carrierRepository.save(carrier);
        CarrierHistoryEntity carrierHistoryEntity = carrierMapper.toHistoryEntity(carrier);
        historyService.saveHistory(carrierHistoryEntity);

        Optional<LotCarrierMapping> optionalLotCarrierMapping = lotCarrierMappingRepository.findByCarrierName(carrierName);
        if(optionalLotCarrierMapping.isPresent()){
            LotCarrierMapping lotCarrierMapping = optionalLotCarrierMapping.get();
            Optional<LotCarrierMapping> optionalNewLotCarrierMapping = lotCarrierMapping.deAssignedAndSplit(command);
            lotCarrierMapping = lotCarrierMappingRepository.save(lotCarrierMapping);
            LotCarrierMappingHistoryEntity lotCarrierMappingHistoryEntity = lotCarrierMappingMapper.toHistoryEntity(lotCarrierMapping);
            historyService.saveHistory(lotCarrierMappingHistoryEntity);
            if(optionalNewLotCarrierMapping.isPresent()){
                LotCarrierMapping newLotCarrierMapping = optionalNewLotCarrierMapping.get();
                newLotCarrierMapping = lotCarrierMappingRepository.save(newLotCarrierMapping);
                LotCarrierMappingHistoryEntity newLotCarrierMappingHistoryEntity = lotCarrierMappingMapper.toHistoryEntity(newLotCarrierMapping);
                historyService.saveHistory(newLotCarrierMappingHistoryEntity);
            }
        }

    }

    /**
     * Carrier 와 Lot 데이터 결합
     * 1. --
     * @param message 받은 메시지
     */
    @Transactional(value = "mssqlTransactionManager")
    public void materialAssignedToCarrier(BaseMessage<MaterialAssignedToCarrierBody> message) {
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

        Optional<PortDef> optionalPortDef = portDefRepository.findByEquipmentNameAndPortName(equipmentName, portName);
        Optional<Port> optionalPort = portRepository.findByEquipmentNameAndPortName(equipmentName, portName);
        PortDef portDef = null;
        Port port = null;
        if(optionalPortDef.isPresent()){
            portDef = optionalPortDef.get();
        }
        if(optionalPort.isPresent()){
            port = optionalPort.get();
        }

        // insert EventQueue
        try{
            InsertEventQueueReportVo insertEventQueueReportVo
                    = InsertEventQueueReportVo
                    .builder()
                    .transportJobName(transportJobName)
                    .messageName(messageName)
                    .portDef(portDef)
                    .port(port)
                    .carrierName(carrierName)
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
        return null;
    }

    /**
     * 포트의 새로운 캐리어를 요청합니다.
     * 1. 포트에 반송중인 job 조회
     * transportState -> READY_TO_LOAD 변경
     * 2. 설비명으로 TaskJob Find
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

    @Transactional(value = "mssqlTransactionManager")
    public BaseMessage<TransportJobRequestBody> destinationDispatchRequest(BaseMessage<DestinationDispatchRequestBody> message){
        return whereDispatchService.whereDispatchRequest(message);
    }

    public BaseMessage<TransportJobRequestBody> transportOrderRequest(BaseMessage<TransportOrderRequestBody> message){
        return factoryProcessStrategy.transportOrderRequest(message);
    }

    public void productionOrderProcessRequest(BaseMessage<ProductionOrderProcessRequestBody> message){
        productionOrderProcessService.productionOrderProcessRequest(message);
    }

    public void productionOrderValidationRequest(BaseMessage<ProductionOrderBody> message){
        String messageName = message.getMessageName();
        String messageOwner = message.getMessageOwner();
        String resultMessage =  message.getResultMessage();
        String messageFrom = message.getMessageFrom();
        Long id = message.getBody().getId();
        String resultCode= "";
        String productionOrderState = "";

        Optional<ProductionOrder> optionalProductionOrder =  productionOrderRepository.findById(id);
        ProductionOrder productionOrder = null;
        if(optionalProductionOrder.isPresent()){
            productionOrder = optionalProductionOrder.get();
        }else{
            return;
        }

        TransactionInfo tx = TransactionInfo.now(messageName,SystemName.MNG.getValue(), resultMessage);

        try {
            // Validation 이 존재한다면 여기 부분에 Validation 추가

            resultCode = ResultCode.OK.getValue();
            productionOrderState = ProductionOrderState.ACCEPTED.getValue();
        }catch (Exception e){
            resultCode = ResultCode.NG.getValue();
            productionOrderState = ProductionOrderState.REJECTED.getValue();
        }

        ProductionOrderUpdateStateCommand command =
                ProductionOrderUpdateStateCommand
                        .builder()
                        .transactionInfo(tx)
                        .productionOrderState(productionOrderState)
                        .build();

        productionOrder.updateState(command);
        productionOrder = productionOrderRepository.save(productionOrder);
        ProductionOrderHistoryEntity historyEntity = productionOrderMapper.toHistoryEntity(productionOrder);
        historyService.saveHistory(historyEntity);

        // powder EventQueue
        try{
            PowderEventQueueReportVo powderEventQueueReportVo
                    = PowderEventQueueReportVo
                    .builder()
                    .messageName(messageName)
                    .productionOrder(productionOrder)
                    .resultCode(resultCode)
                    .tx(tx)
                    .build();
            factoryIfEventQueueStrategy.enqueueIfEventQueue(powderEventQueueReportVo);
        }
        catch(Exception e){
            log.error("EventQueue enqueue error",e);
        }


    }

    public BaseMessage<TransportJobValidationRequestBody> transportOrderValidationRequest(BaseMessage<TransportOrderRequestBody> message){
        return factoryProcessStrategy.transportOrderValidationRequest(message);
    }

    private void sendToWMSZoneRequest(){
        // TODO : WMS 와 협의 할건데, 일단 이건 뒤로 미루기
        // WMS 에 목적지를 물어봐야하는 케이스 최대한 안물어볼 생각
        BaseMessage<CarrierDestinationZoneRequestBody> wmsRequestMessage = zoneRequestBodyBaseMessage();

        Object wmsReply = rabbitTemplate.convertSendAndReceive(
                RabbitConfig.EXCHANGE_WMS,
                RabbitConfig.ROUTING_WMS,
                wmsRequestMessage
        );

        if(ObjectUtils.isEmpty(wmsReply)){
            //return null;
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

//            BaseMessage<TransportJobRequestBody> request = new  BaseMessage<>();
//            request.setTransactionId(message.getTransactionId());
//            request.setMessageFrom(SystemName.MNG.getValue());
//            request.setMessageOwner(SystemName.MNG.getValue());
//            request.setMessageTo(SystemName.WCS.getValue());
//            request.setEventTime(message.getEventTime());
//            request.setMessageName(MessageList.TRANSPORT_JOB_REQUEST.getMessageName());
//            request.setResultCode(ResultCode.OK.getValue());
//
//            TransportJobRequestBody body =
//                    TransportJobRequestBody
//                            .builder()
//                            .carrierName(carrierName)
//                            .build();
//            request.setBody(body);
            //return request;
        }
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

    @Transactional(value = "mssqlTransactionManager")
    public BaseMessage<CarrierInfoDownloadSendBody> recipeTimeOutRequest(BaseMessage<RecipeTimeOutRequestBody> message) {
        String messageName = message.getMessageName();
        String messageOwner = message.getMessageOwner();
        String resultMessage =  message.getResultMessage();
        String messageFrom = message.getMessageFrom();

        String equipmentName = "";
        String portName ="";
        String carrierName = "";
        String lastCarrierFlag = "";
        String lotName = "";
        String itemName = "";
        String productionTaskId = "";
        String totalQuantity ="";

        Long lotCarrierMappingId = message.getBody().getId();

        Optional<LotCarrierMapping> optionalLotCarrierMapping = lotCarrierMappingRepository.findById(lotCarrierMappingId);
        if (optionalLotCarrierMapping.isEmpty()) {
            throw new IllegalArgumentException("해당 Lot Carrier Mapping 정보가 존재하지 않습니다. ID: " + lotCarrierMappingId);
        }

        LotCarrierMapping lotCarrierMapping = optionalLotCarrierMapping.get();

        TransactionInfo tx = TransactionInfo.now(messageName,messageOwner,resultMessage);

        RecipeReplyCommand command = RecipeReplyCommand.builder()
                .transactionInfo(tx)
                .carrierName(lotCarrierMapping.getCarrierName())
                .build();

        lotCarrierMapping.recipeTimeOut(command);
        lotCarrierMapping = lotCarrierMappingRepository.save(lotCarrierMapping);
        LotCarrierMappingHistoryEntity historyEntity = lotCarrierMappingMapper.toHistoryEntity(lotCarrierMapping);
        historyService.saveHistory(historyEntity);



        List<String> productionStatus = new ArrayList<>();
        productionStatus.add(ProductionStatus.WAIT.getValue());
        productionStatus.add(ProductionStatus.ALLOCATED.getValue());
        List<LotCarrierMapping> lotCarrierMappingList = lotCarrierMappingRepository.findByOrderIdAndOrderLineNumberAndProductionStatusIn(
                lotCarrierMapping.getOrderId(),
                lotCarrierMapping.getOrderLineNumber(),
                productionStatus
        );

        if(lotCarrierMappingList.isEmpty() || lotCarrierMappingList.size() == 1){
            lastCarrierFlag = YN.Y.name();
        }
        else{
            lastCarrierFlag = YN.N.name();
        }

        Optional<Carrier> optionalCarrier = carrierRepository.findByCarrierName(lotCarrierMapping.getCarrierName());
        Optional<Lot> optionalLot = lotRepository.findByLotName(lotCarrierMapping.getLotName());
        Optional<ProductionOrder> optionalProductionOrder = productionOrderRepository.findById(lotCarrierMapping.getProductionOrderId());


        if(optionalCarrier.isPresent()){
            Carrier carrier = optionalCarrier.get();
            equipmentName = carrier.getEquipmentName();
            portName = carrier.getPortName();
            carrierName = carrier.getCarrierName();
        }

        if(optionalLot.isPresent()){
            Lot lot = optionalLot.get();
            lotName =  lot.getLotName();
        }

        if(optionalProductionOrder.isPresent()){
            ProductionOrder productionOrder = optionalProductionOrder.get();
            productionTaskId = productionOrder.getId().toString();
            totalQuantity =productionOrder.getPlanQuantity().toString();
        }

        BaseMessage<CarrierInfoDownloadSendBody> reply = new BaseMessage<>();
        reply.setMessageName(MessageList.CARRIER_INFO_DOWNLOAD_SEND.getMessageName());
        reply.setTransactionId(message.getTransactionId());
        reply.setMessageFrom(SystemName.MNG.getValue());
        reply.setMessageOwner(SystemName.MNG.getValue());
        reply.setMessageTo(SystemName.EAS.getValue());
        reply.setEventTime(message.getEventTime());
        reply.setResultMessage("");
        reply.setResultCode(ResultCode.NG.getValue());

        RecipeBody recipeBody = new RecipeBody();
        List<RecipeParameterListBody> recipeParameterListBodyList = new ArrayList<>();
        recipeBody.setParameterList(recipeParameterListBodyList);

        CarrierInfoDownloadSendBody body = CarrierInfoDownloadSendBody
                .builder()
                .equipmentName(equipmentName)
                .portName(portName)
                .carrierName(carrierName)
                .productionTaskId(productionTaskId)
                .lotName(lotName)
                .itemName(itemName)
                .orderId(lotCarrierMapping.getOrderId())
                .orderLineNumber(lotCarrierMapping.getOrderLineNumber())
                .quantity(lotCarrierMapping.getQuantity().toString())
                .totalQuantity(totalQuantity)
                .mngKey(lotCarrierMapping.getMngKey().toString())
                .lastCarrierFlag(lastCarrierFlag)
                .recipe(recipeBody)
                .build();
        reply.setBody(body);

        return reply;
    }

    /**
     * port 의 접근모드 변경시 보고
     * @param message 받은 메시지
     */
    @Transactional(value = "mssqlTransactionManager")
    public void portTransportModeChanged(BaseMessage<PortTypeChangedBody> message) {
        String messageName = message.getMessageName();
        String messageOwner = message.getMessageOwner();
        String resultMessage =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String portType = message.getBody().getPortType();
        String portTransportModeName = message.getBody().getPortTransportMode();

        Optional<Port> optionalPorts = portRepository.findByEquipmentNameAndPortName(equipmentName,portName);

        if(optionalPorts.isEmpty()){
            return;
        }

        Port port = optionalPorts.get();
        TransactionInfo tx = TransactionInfo.now(messageName,messageOwner,resultMessage);
        PortTransportModeChangedCommand command = PortTransportModeChangedCommand.builder().transactionInfo(tx).portTransportModeName(portTransportModeName).build();

        port.transportModeChanged(command);
        port = portRepository.save(port);
        PortHistoryEntity portHistoryEntity = portMapper.toHistoryEntity(port);
        historyService.saveHistory(portHistoryEntity);

    }

    /**
     * port 의 상태 변경시 보고
     * @param message 받은 메시지
     */
    @Transactional(value = "mssqlTransactionManager")
    public void portStateChanged(BaseMessage<PortStateChangedBody> message) {
        String messageName = message.getMessageName();
        String messageOwner = message.getMessageOwner();
        String resultMessage =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String portType = message.getBody().getPortType();
        String portStateName = message.getBody().getPortStateName();

        Optional<Port> optionalPorts = portRepository.findByEquipmentNameAndPortName(equipmentName,portName);

        if(optionalPorts.isEmpty()){
            return;
        }
        if(!PortState.isExist(portStateName)){
            return;
        }

        Port port = optionalPorts.get();
        TransactionInfo tx = TransactionInfo.now(messageName,messageOwner,resultMessage);
        PortState state = PortState.valueOf(portStateName);
        PortStateChangedCommand command = PortStateChangedCommand
                .builder()
                .transactionInfo(tx)
                .portState(state)
                .build();
        port.portStateChanged(command);
        port = portRepository.save(port);
        PortHistoryEntity portHistoryEntity = portMapper.toHistoryEntity(port);
        historyService.saveHistory(portHistoryEntity);
    }
    /**
     * port 의 상태 변경시 보고
     * @param message 받은 메시지
     */
    @Transactional(value = "mssqlTransactionManager")
    public void portStateReport(BaseMessage<PortStateReportBodyList> message) {
        String messageName = message.getMessageName();
        String messageOwner = message.getMessageOwner();
        String resultMessage =  message.getResultMessage();

        TransactionInfo tx = TransactionInfo.now(messageName,messageOwner,resultMessage);
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
                Optional<Port> optionalPorts = portRepository.findByEquipmentNameAndPortName(equipmentName,portName);

                if(optionalPorts.isEmpty()){
                    continue;
                }
                if(PortState.isNotExist(portStateName)){
                    continue;
                }

                Port port = optionalPorts.get();

                PortState state = PortState.valueOf(portStateName);
                PortStateChangedCommand command =
                        PortStateChangedCommand
                                .builder()
                                .transactionInfo(tx)
                                .portState(state)
                                .build();
                port.portStateChanged(command);
                port = portRepository.save(port);
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
        String messageName = message.getMessageName();
        String messageOwner = message.getMessageOwner();
        String resultMessage =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String portTypeName = message.getBody().getPortType();

        if(!PortType.isExist(portTypeName)){
            return;
        }

        Optional<Port> optionalPorts = portRepository.findByEquipmentNameAndPortName(equipmentName,portName);

        if(optionalPorts.isEmpty()){
            return;
        }
        Port port = optionalPorts.get();

        Optional<PortDef> optionalPortDef = portDefRepository.findByEquipmentNameAndPortName(equipmentName,portName);
        if(optionalPortDef.isEmpty()){
            return;
        }
        PortDef portDef = optionalPortDef.get();

        TransactionInfo tx = TransactionInfo.now(messageName,messageOwner,resultMessage);
        PortType portType = PortType.valueOf(portTypeName);
        PortTypeChangedCommand command = PortTypeChangedCommand
                .builder()
                .transactionInfo(tx)
                .portType(portType)
                .build();
        port.portTypeChanged(command);
        port = portRepository.save(port);
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
        log.info("Business Logic Nothing");
    }

    /**
     * 설비 생산 시작 보고
     *
     * @param message 받은 메시지
     */
    @Transactional(value = "mssqlTransactionManager")
    public void processJobStarted(BaseMessage<ProcessJobStartedBody> message) {
        processJobService.processJobStarted(message);
    }

    /**
     * 설비 생산 완료 보고
     *
     * @param message 받은 메시지
     */
    @Transactional(value = "mssqlTransactionManager")
    public void processJobEnded(BaseMessage<ProcessJobEndedBody> message) {
        processJobService.processJobEnded(message);
    }

    /**
     * 설비에 투입 후 완료 후 데이터 보고
     *
     * @param message 받은 메시지
     */
    @Transactional(value = "mssqlTransactionManager")
    public void processJobDataReport(BaseMessage<ProcessJobDataReportBody> message) {
    }

    @Transactional(value = "mssqlTransactionManager")
    public BaseMessage<CarrierInfoDownloadSendBody> recipeReply(BaseMessage<RecipeReplyBody> message) {
        String messageName = message.getMessageName();
        String messageOwner = message.getMessageOwner();
        String resultMessage =  message.getResultMessage();
        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String carrierName = message.getBody().getCarrierName();
        String orderId = message.getBody().getOrderId();
        String orderLineNumber = message.getBody().getOrderLineNumber();
        String mngKey = message.getBody().getTransactionId();
        RecipeBody recipeBody = message.getBody().getRecipe();
        Long mngKeyToLong = Long.parseLong(mngKey);
        String lastCarrierFlag = "";
        String lotName = "";
        String itemName = "";
        String totalQuantity = "";

        List<LotCarrierMapping> lotCarrierMappingList = lotCarrierMappingRepository.findByMngKey(mngKeyToLong);
        if( ObjectUtils.isEmpty(lotCarrierMappingList)){
            return null;
        }
        LotCarrierMapping lotCarrierMapping = lotCarrierMappingList.get(0);
        TransactionInfo tx = TransactionInfo.now(messageName,messageOwner,resultMessage);
        RecipeReplyCommand command =
                RecipeReplyCommand
                        .builder()
                        .transactionInfo(tx)
                        .carrierName(lotCarrierMapping.getCarrierName())
                        .build();
        lotCarrierMapping.recipeRely(command);
        lotCarrierMapping = lotCarrierMappingRepository.save(lotCarrierMapping);
        LotCarrierMappingHistoryEntity historyEntity = lotCarrierMappingMapper.toHistoryEntity(lotCarrierMapping);
        historyService.saveHistory(historyEntity);

        Optional<Lot> optionalLot = lotRepository.findByLotName(lotCarrierMapping.getLotName());
        if(optionalLot.isPresent()){
            Lot lot = optionalLot.get();
            lotName = lot.getLotName();
            itemName = lot.getItemId();
            totalQuantity = lot.getTotalQuantity().toString();
        }


        List<String> productionStatus = new ArrayList<>();
        productionStatus.add(ProductionStatus.WAIT.getValue());
        productionStatus.add(ProductionStatus.ALLOCATED.getValue());
        List<LotCarrierMapping> lotCarrierMappingListByOrderInfo = lotCarrierMappingRepository.findByOrderIdAndOrderLineNumberAndProductionStatusIn(
                orderId,
                orderLineNumber,
                productionStatus
        );

        if(lotCarrierMappingListByOrderInfo.isEmpty() || lotCarrierMappingListByOrderInfo.size() == 1){
            lastCarrierFlag = YN.Y.name();
        }
        else{
            lastCarrierFlag = YN.N.name();
        }


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
                .lotName(lotName)
                .itemName(itemName)
                .orderId(orderId)
                .orderLineNumber(orderLineNumber)
                .quantity(lotCarrierMapping.getQuantity().toString())
                .totalQuantity(totalQuantity)
                .mngKey(mngKey)
                .lastCarrierFlag(lastCarrierFlag)
                .recipe(recipeBody)
                .build();
        request.setBody(body);
        return request;
    }

    @Transactional(value = "mssqlTransactionManager")
    public BaseMessage<RecipeChangedRequestForEASBody> recipeChangedRequest(BaseMessage<RecipeChangedRequestForMANTIBody> message) {
        String messageName = message.getMessageName();
        String messageOwner = message.getMessageOwner();
        String resultMessage =  message.getResultMessage();
        String equipmentName = message.getBody().getEquipmentName();
        String orderId = message.getBody().getOrderId();
        String orderLineNumber = message.getBody().getOrderLineNumber();
        RecipeBody recipeBody = message.getBody().getRecipe();
        String productionTaskId ="";

        Optional<ProductionOrder> optionalProductionOrder = productionOrderRepository.findByOrderIdAndOrderLineNumber(orderId,orderLineNumber);
        if(optionalProductionOrder.isPresent()){
            ProductionOrder productionOrder = optionalProductionOrder.get();
            productionTaskId = productionOrder.getId().toString();
        }

        BaseMessage<RecipeChangedRequestForEASBody> request = new BaseMessage<>();
        request.setMessageName(MessageList.RECIPE_CHANGED_REQUEST.getMessageName());
        request.setTransactionId(message.getTransactionId());
        request.setMessageFrom(SystemName.MNG.getValue());
        request.setMessageOwner(SystemName.MNG.getValue());
        request.setMessageTo(SystemName.EAS.getValue());
        request.setEventTime(message.getEventTime());
        request.setResultMessage("");
        request.setResultCode(ResultCode.OK.getValue());
        RecipeChangedRequestForEASBody body = RecipeChangedRequestForEASBody
                .builder()
                .productionTaskId(productionTaskId)
                .equipmentName(equipmentName)
                .orderId(orderId)
                .orderLineNumber(orderLineNumber)
                .recipe(recipeBody)
                .build();
        request.setBody(body);
        return request;
    }

    @Transactional(value = "mssqlTransactionManager")
    public BaseMessage<RecipeChangedReplyForMANTIBody> recipeChangedReply(BaseMessage<RecipeChangedReplyForEASBody> message) {
        String messageName = message.getMessageName();
        String messageOwner = message.getMessageOwner();
        String resultMessage =  message.getResultMessage();
        String equipmentName = message.getBody().getEquipmentName();
        String orderId = message.getBody().getOrderId();
        String orderLineNumber = message.getBody().getOrderLineNumber();
        RecipeBody recipeBody = message.getBody().getRecipe();

        BaseMessage<RecipeChangedReplyForMANTIBody> request = new BaseMessage<>();
        request.setMessageName(MessageList.RECIPE_CHANGED_REPLY.getMessageName());
        request.setTransactionId(message.getTransactionId());
        request.setMessageFrom(SystemName.MNG.getValue());
        request.setMessageOwner(SystemName.MNG.getValue());
        request.setMessageTo(SystemName.MANTI.getValue());
        request.setEventTime(message.getEventTime());
        request.setResultMessage("");
        request.setResultCode(ResultCode.OK.getValue());
        RecipeChangedReplyForMANTIBody body = RecipeChangedReplyForMANTIBody
                .builder()
                .equipmentName(equipmentName)
                .orderId(orderId)
                .orderLineNumber(orderLineNumber)
                .recipe(recipeBody)
                .build();
        request.setBody(body);
        return request;
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
        String messageName = message.getMessageName();
        String messageOwner = message.getMessageOwner();
        String resultMessage =  message.getResultMessage();
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

                Optional<TransportJob> optionalTransportJob = transportJobRepository.findByTransportJobName(transportJobName);

                if(optionalTransportJob.isPresent()){

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
        String messageName = message.getMessageName();
        String messageOwner = message.getMessageOwner();
        String resultMessage =  message.getResultMessage();
        String transportJobName = message.getBody().getTransportJobName();
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
                = transportJobRepository.findByTransportJobName(transportJobName);
        if(optionalTransportJob.isEmpty()){
            return;
        }
        TransportJob transportJob = optionalTransportJob.get();

        TransactionInfo tx =  TransactionInfo.now(messageName,messageOwner,resultMessage);

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
        transportJob = transportJobRepository.save(transportJob);
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
     * 반송 취소처리 실패 필요한지 확인필요
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

    @Transactional(value = "mssqlTransactionManager")
    public void transportJobValidationReply(BaseMessage<TransportJobValidationReplyBody> message) {
        factoryProcessStrategy.transportJobValidationReply(message);
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

        String messageName = message.getMessageName();
        String messageOwner = message.getMessageOwner();
        String resultMessage =  message.getResultMessage();

        TransactionInfo tx = TransactionInfo.now(messageName,messageOwner,resultMessage);
        List<CommunicationStateReportBody> equipmentList = message.getBody().getEquipmentList();
        for(CommunicationStateReportBody body : equipmentList){
            String equipmentName = body.getEquipmentName();
            String communicationState = body.getCommunicationState();

            Optional<Equipment> optionalEquipments = equipmentRepository.findByEquipmentName(equipmentName);

            if(optionalEquipments.isEmpty()){
                continue;
            }
            if(!CommunicationState.isExist(communicationState)){
                continue;
            }
            Equipment equipment = optionalEquipments.get();

            CommunicationState state = CommunicationState.valueOf(communicationState);
            CommunicationStateChangeCommand command = CommunicationStateChangeCommand.builder().transactionInfo(tx).communicationState(state).build();
            equipment.communicationStateChange(command);

            equipment = equipmentRepository.save(equipment);
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
        String messageName = message.getMessageName();
        String messageOwner = message.getMessageOwner();
        String resultMessage =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();

        Optional<Equipment> optionalEquipments = equipmentRepository.findByEquipmentName(equipmentName);

        if(optionalEquipments.isEmpty()){
            return;
        }

        if(!CommunicationState.isExist(communicationState)){
            return;
        }
        Equipment equipment = optionalEquipments.get();

        TransactionInfo tx = TransactionInfo.now(messageName,messageOwner,resultMessage);
        CommunicationState state = CommunicationState.valueOf(communicationState);
        CommunicationStateChangeCommand command = CommunicationStateChangeCommand.builder().transactionInfo(tx).communicationState(state).build();

        equipment.communicationStateChange(command);
        equipment = equipmentRepository.save(equipment);
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

        String messageName = message.getMessageName();
        String messageOwner = message.getMessageOwner();
        String resultMessage =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();
        String equipmentState = message.getBody().getEquipmentStateName();

        Optional<Equipment> optionalEquipments = equipmentRepository.findByEquipmentName(equipmentName);

        if(optionalEquipments.isEmpty()){
            return;
        }

        if(!EquipmentState.isExist(equipmentState)){
            return;
        }
        Equipment equipment = optionalEquipments.get();

        TransactionInfo tx = TransactionInfo.now(messageName,messageOwner,resultMessage);
        EquipmentState state = EquipmentState.valueOf(equipmentState);
        EquipmentStateChangeCommand command = EquipmentStateChangeCommand.builder().equipmentState(state).transactionInfo(tx).build();
        equipment.equipmentStateChange(command);
        equipment = equipmentRepository.save(equipment);
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
        String messageName = message.getMessageName();
        String messageOwner = message.getMessageOwner();
        String resultMessage =  message.getResultMessage();
        TransactionInfo tx = TransactionInfo.now(messageName,messageOwner,resultMessage);

        List<EquipmentStateReportBody> bodyList = message.getBody().getEquipmentList();
        for(EquipmentStateReportBody body : bodyList){
            String equipmentName = body.getEquipmentName();
            String equipmentType = body.getEquipmentType();
            String equipmentState = body.getEquipmentStateName();
            String communicationState = body.getCommunicationState();

            Optional<Equipment> optionalEquipments = equipmentRepository.findByEquipmentName(equipmentName);

            if(optionalEquipments.isEmpty()){
                continue;
            }

            if(!EquipmentState.isExist(equipmentState)){
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
            equipment = equipmentRepository.save(equipment);
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
        String messageName = message.getMessageName();
        String messageOwner = message.getMessageOwner();
        String resultMessage =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();
        String operationModeName = message.getBody().getOperationModeName();

        Optional<Equipment> optionalEquipments = equipmentRepository.findByEquipmentName(equipmentName);

        if(optionalEquipments.isEmpty()){
            return;
        }

        if(!EquipmentOperationMode.isExist(operationModeName)){
            return;
        }
        Equipment equipment = optionalEquipments.get();

        TransactionInfo tx = TransactionInfo.now(messageName,messageOwner,resultMessage);
        EquipmentOperationMode mode = EquipmentOperationMode.valueOf(operationModeName);
        EquipmentOperationModeChangeCommand command = EquipmentOperationModeChangeCommand.builder().equipmentOperationMode(mode).transactionInfo(tx).build();
        equipment.operationModeChange(command);
        equipment = equipmentRepository.save(equipment);
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
        String messageName = message.getMessageName();
        String messageOwner = message.getMessageOwner();
        String resultMessage =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();
        String operationModeName = message.getBody().getOperationModeName();

        Optional<Equipment> optionalEquipments = equipmentRepository.findByEquipmentName(equipmentName);

        if(optionalEquipments.isEmpty()){
            return;
        }

        if(!EquipmentOperationMode.isExist(operationModeName)){
            return;
        }
        Equipment equipment = optionalEquipments.get();

        TransactionInfo tx = TransactionInfo.now(messageName,messageOwner,resultMessage);
        EquipmentOperationMode mode = EquipmentOperationMode.valueOf(operationModeName);
        EquipmentOperationModeChangeCommand command = EquipmentOperationModeChangeCommand.builder().equipmentOperationMode(mode).transactionInfo(tx).build();
        equipment.operationModeChange(command);
        equipment = equipmentRepository.save(equipment);
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

        Optional<ProductionOrder> optionalProductionOrder = productionOrderRepository.findById(productionOrderId);

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
        lot = lotRepository.save(lot);
        LotHistoryEntity historyEntity = lotMapper.toHistoryEntity(lot);
        historyService.saveHistory(historyEntity);

        // 모든 검증과 로직이 성공하면 OK 반환
        return createReplyMessage(message, ResultCode.OK, "",replyBody);
    }

    /**
     * 최초 입고시 자재와 Carrier 의 Assign 보고
     * 1. --
     * 2. --
     * 3. --
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

        Optional<ProductionOrder> optionalProductionOrder = productionOrderRepository.findById(productionOrderId);

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

        Optional<Carrier> optionalCarrier = carrierRepository.findByCarrierName(carrierName);

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
            lotCarrierMapping = lotCarrierMappingRepository.save(lotCarrierMapping);
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
            carrier = carrierRepository.save(carrier);
            CarrierHistoryEntity carrierHistoryEntity = carrierMapper.toHistoryEntity(carrier);
            historyService.saveHistory(carrierHistoryEntity);
        }

        return createReplyMessage(message, ResultCode.OK, "",replyBody);
    }

    /**
     * 자재와 Carrier Assign 후 창고 보관 요청 메시지
     * 1. --
     * 2. --
     * 3. --
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