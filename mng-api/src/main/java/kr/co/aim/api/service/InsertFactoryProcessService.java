package kr.co.aim.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.api.strategy.FactoryIfEventQueueStrategy;
import kr.co.aim.api.vo.insert.ops.InsertEventQueueReportVo;
import kr.co.aim.api.vo.port.TransportStateChangedVo;
import kr.co.aim.api.vo.transportJob.CreateTransportJobVo;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.format.*;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.api.strategy.FactoryProcessStrategy;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.*;
import kr.co.aim.domain.model.*;
import kr.co.aim.infra.persistence.entity.PortHistoryEntity;
import kr.co.aim.infra.persistence.entity.TransportOrderHistoryEntity;
import kr.co.aim.infra.persistence.mapper.PortMapper;
import kr.co.aim.infra.persistence.mapper.TransportJobMapper;
import kr.co.aim.infra.persistence.mapper.TransportOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
@ConditionalOnProperty(name = "factory.type", havingValue = "insert")
public class InsertFactoryProcessService implements FactoryProcessStrategy {

    private final HistoryService historyService;
    private final ObjectMapper objectMapper;

    private final PortService portService;
    private final PortMapper portMapper;

    private final TransportJobService transportJobService;
    private final TransportJobMapper transportJobMapper;

    private final TransportOrderService transportOrderService;
    private final TransportOrderMapper transportOrderMapper;

    private final IfEventQueueService ifEventQueueService;
    private final FactoryIfEventQueueStrategy factoryIfEventQueueStrategy;


    @Override
    @Transactional(value = "mssqlTransactionManager")
    public BaseMessage<TransportJobRequestListBody> carrierDispatchRequest(BaseMessage<CarrierDispatchRequestBody> message) {
        // 1. Port, PortDef 를 조회
        // 2. PortDef 의 workStaionName 을 기준으로 outbound 명령이 있는지 조회
        // 3. 있다면, port의 상태를 reserveToLoad 로 변경 후 반송메시지 반환

        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String carrierName = message.getBody().getCarrierName();
        String portType =  message.getBody().getPortType();
        String portTransportMode =  message.getBody().getPortTransportMode();

        // TODO: 비관적 lock 으로 변경
        Optional<Port> optionalPort = portService.findPortByEquipmentNameAndPortName(equipmentName,portName);
        Optional<PortDef> optionalPortDef = portService.findPortDefByEquipmentNameAndPortName(equipmentName,portName);
        PortDef portDef = null;
        Port port = null;

        if(optionalPort.isEmpty()){
            return null;
        }
        port = optionalPort.get();
        if(optionalPortDef.isEmpty()){
            return null;
        }
        portDef = optionalPortDef.get();

        if(StringUtils.equals(port.getTransportState(), PortTransportState.READY_TO_LOAD.getValue())){
            // readyToLoad 일 경우만
            // portDef workstationName 을 통해서 outbound order를 찾음
            // 만약 outbound order가 있다면, reserveToLoad 로 변경 후 반송요청 메시지 빈환
            // TODO: WORKCENTERNAME -> WORKSTATIONAME 으로 변경
            if(StringUtils.isNotBlank(portDef.getWorkCenterName())){
                List<TransportOrder> transportOrders = transportOrderService.findOutboundOrderForTransportRequest(
                        TransportOrderType.OUTBOUND.getValue(),
                        TransportOrderStatus.CREATED.getValue(),
                        portDef.getWorkCenterName()
                );

                if(CollectionUtils.isNotEmpty(transportOrders)){
                    TransactionInfo tx = TransactionInfo.now("autoTransport",SystemName.MNG.getValue(), "auto Transport");
                    TransportOrder transportOrder = transportOrders.get(0);
                    carrierName =  transportOrder.getCarrierName();
                    String transportType = transportOrder.getTransportType();
                    String carrierType = transportOrder.getCarrierType();
                    String travelProfile = transportOrder.getTravelProfile();
                    Integer priority =  transportOrder.getPriority();
                    String transportOrderId = transportOrder.getTransportOrderId();
                    List<TransportJobCreateCommand> commandList = new ArrayList<>();
                    TransportJobCreateCommand command =
                            TransportJobCreateCommand.builder()
                                    //TODO: TransportJobNaming rule check
                                    .transportJobName(carrierName + tx.eventTime().toString().substring(0,12))
                                    .carrierName(carrierName)
                                    .transportType(transportType)
                                    .transportJobState(TransportJobState.REQUESTED.getValue())
                                    .carrierType(carrierType)
                                    .travelProfile(travelProfile)
                                    //.sourceEquipmentName()
                                    //.sourcePortName()
                                    //.sourceZoneName()
                                    //.sourcePositionTypeName()
                                    //.sourcePositionName()
                                    .destinationEquipmentName(port.getEquipmentName())
                                    .destinationPortName(port.getPortName())
                                    //.destinationZoneName()
                                    //.destinationPositionTypeName()
                                    //.destinationPositionName()
                                    .priority(priority)
                                    //.errorCode()
                                    //.errorText()
                                    .requestSource(TransportJobRequestType.GAL.getValue())
                                    .createTime(tx.eventTime())
                                    //.departedTime()
                                    //.arrivedTime()
                                    //.reasonCode()
                                    .orderId(transportOrderId)
                                    .transactionInfo(tx)
                                    .build();
                    commandList.add(command);
                    CreateTransportJobVo vo = CreateTransportJobVo
                            .builder()
                            .transportJobCreateCommandList(commandList)
                            .build();
                    List<TransportJob> transportJobs = transportJobService.createTransportJob(vo);
                    BaseMessage<TransportJobRequestListBody> request = new BaseMessage<>();
                    TransportJobRequestListBody body = transportJobService.createTransportJobMessage(transportJobs);
                    request.setMessageName(MessageList.TRANSPORT_JOB_REQUEST.getMessageName());
                    // 1. 현재 시간 가져오기 (2026년 기준)
                    LocalDateTime now = LocalDateTime.now();
                    // 2. 18자리 포맷 정의 (연4, 월2, 일2, 시2, 분2, 초2, 소수점4)
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSS");
                    // 3. 포맷 적용 및 출력
                    String timestamp = now.format(formatter);
                    request.setTransactionId(timestamp);
                    request.setBody(body);

                    transportOrder.setTransportStatus(TransportOrderStatus.REQUESTED.getValue());
                    transportOrder = transportOrderService.save(transportOrder);
                    TransportOrderHistoryEntity transportOrderHistoryEntity = transportOrderMapper.toHistoryEntity(transportOrder);
                    historyService.saveHistory(transportOrderHistoryEntity);


                    TransportStateChangedVo transportStateChangedVo =
                            TransportStateChangedVo
                                    .builder()
                                    .port(port)
                                    .portTransportState(PortTransportState.RESERVED_TO_LOAD)
                                    .tx(tx)
                                    .build();
                    portService.transportStateChanged(transportStateChangedVo);

                    return request;
                }
            }

        }
        return null;
    }

    /**
     */
    @Transactional(value = "mssqlTransactionManager")
    public BaseMessage<DestinationDispatchRequestBody> unLoadRequest(BaseMessage<UnLoadRequestBody> message) {
        // insert 는 unloadRequest 에서 비지니스 로직 없음
        log.info("Nothing business Logic");
        return null;
    }

    @Override
    @Transactional(value = "mssqlTransactionManager")
    public BaseMessage<TransportJobRequestListBody> transportOrderRequest(BaseMessage<TransportOrderRequestBody> message) {
        // 1. TransportOrder 비관적 Lock 조회
        // 2. Created 상태인지 체크
        // 3. Created 상태라면, TransportJob 생성 후 TEX 로 전송
        // 4. 전송 후 Request 상태로 변경
        Long id = message.getBody().getId();
        Optional<TransportOrder> optionalTransportOrder = transportOrderService.findWithLockById(id);

        if(optionalTransportOrder.isEmpty()){
            return null;
        }
        TransportOrder transportOrder = optionalTransportOrder.get();

        String transportOrderId = transportOrder.getTransportOrderId();
        String carrierName = transportOrder.getCarrierName();
        String transportType = transportOrder.getTransportType();
        String carrierType = transportOrder.getCarrierType();
        Integer priority = transportOrder.getPriority();
        String locationId = transportOrder.getLocationId();
        String workStationId = transportOrder.getWorkStationId();
        String sourceZoneName = transportOrder.getSourceZoneName();
        String destinationZoneName = transportOrder.getDestinationZoneName();
        String requestedZoneName = transportOrder.getRequestedZoneName();
        String actualZoneName = transportOrder.getActualZoneName();
        String actualLocationId = transportOrder.getActualLocationId();
        String travelProfile = transportOrder.getTravelProfile();
        LocalDateTime createTime = transportOrder.getCreateTime();
        LocalDateTime retrievalTime = transportOrder.getRetrievalTime();
        String createUser = transportOrder.getCreateUser();
        String eventName = transportOrder.getEventName();
        LocalDateTime eventTime = transportOrder.getEventTime();
        String eventUser = transportOrder.getEventUser();
        String eventComment = transportOrder.getEventComment();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        if(StringUtils.equals(transportOrder.getTransportStatus(), TransportOrderStatus.CREATED.getValue())){
            List<TransportJobCreateCommand> commandList = new ArrayList<>();
            TransportJobCreateCommand command =
                    TransportJobCreateCommand.builder()
                            .transportJobName(SystemName.GAL.getValue().charAt(0) + "_"+ tx.eventTime().toString().substring(0,12))
                            .carrierName(carrierName)
                            .transportType(transportType)
                            .transportJobState(TransportJobState.REQUESTED.getValue())
                            .carrierType(carrierType)
                            .travelProfile(travelProfile)
                            //.sourceEquipmentName()
                            //.sourcePortName()
                            //.sourceZoneName()
                            //.sourcePositionTypeName()
                            //.sourcePositionName()
                            //.destinationEquipmentName()
                            //.destinationPortName()
                            .destinationZoneName(requestedZoneName)
                            //.destinationPositionTypeName()
                            //.destinationPositionName()
                            .priority(priority)
                            //.errorCode()
                            //.errorText()
                            .requestSource(TransportJobRequestType.GAL.getValue())
                            .createTime(tx.eventTime())
                            //.departedTime()
                            //.arrivedTime()
                            //.reasonCode()
                            .orderId(transportOrderId)
                            .transactionInfo(tx)
                            .build();
            commandList.add(command);
            CreateTransportJobVo vo = CreateTransportJobVo
                    .builder()
                    .transportJobCreateCommandList(commandList)
                    .build();
            List<TransportJob> transportJobs = transportJobService.createTransportJob(vo);
            BaseMessage<TransportJobRequestListBody> request = new BaseMessage<>();
            TransportJobRequestListBody body = transportJobService.createTransportJobMessage(transportJobs);
            request.setMessageName(MessageList.TRANSPORT_JOB_REQUEST.getMessageName());
            // 1. 현재 시간 가져오기 (2026년 기준)
            LocalDateTime now = LocalDateTime.now();
            // 2. 18자리 포맷 정의 (연4, 월2, 일2, 시2, 분2, 초2, 소수점4)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSS");
            // 3. 포맷 적용 및 출력
            String timestamp = now.format(formatter);
            request.setTransactionId(timestamp);
            request.setBody(body);

            transportOrder.setTransportStatus(TransportOrderStatus.REQUESTED.getValue());
            transportOrder = transportOrderService.save(transportOrder);
            TransportOrderHistoryEntity transportOrderHistoryEntity = transportOrderMapper.toHistoryEntity(transportOrder);
            historyService.saveHistory(transportOrderHistoryEntity);

            return request;
        }

        return null;
    }

    @Override
    public void unLoadCompleted(BaseMessage<UnLoadCompletedBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String carrierName = message.getBody().getCarrierName();
        String portType = message.getBody().getPortType();
        String portTransportMode = message.getBody().getPortTransportMode();
        String messageName =  message.getMessageName();
        String transportJobName = message.getBody().getTransportJobName();
        String actualWeight = message.getBody().getActualWeight();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        UnLoadCompletedCommand command = UnLoadCompletedCommand.builder()
                .transactionInfo(tx)
                .carrierName(carrierName)
                .equipmentName(equipmentName)
                .portName(portName)
                .build();
        Optional<PortDef> optionalPortDef = portService.findPortDefByEquipmentNameAndPortName(equipmentName, portName);
        if(optionalPortDef.isEmpty()){
            return;
        }
        PortDef portDef = optionalPortDef.get();
        String actualLocationId = portDef.getLocationId();
        Optional<Port> optionalPorts = portService.findPortByEquipmentNameAndPortName(equipmentName,portName);
        if(optionalPorts.isEmpty()){
            return;
        }
        Port port = optionalPorts.get();
        port.unloadCompleted(command);
        port = portService.save(port);
        PortHistoryEntity portHistoryEntity = portMapper.toHistoryEntity(port);
        historyService.saveHistory(portHistoryEntity);

        try{
            InsertEventQueueReportVo insertEventQueueReportVo
                    = InsertEventQueueReportVo
                    .builder()
                    .transportJobName(transportJobName)
                    .messageName(messageName)
                    .optionalPort(optionalPorts)
                    .optionalPortDef(optionalPortDef)
                    .carrierName(carrierName)
//                    .actualZoneName()
                    .actualWeight(actualWeight)
                    .actualRackLocationId(actualLocationId)
//                    .errorTexts()
//                    .jobType(jobType)
                    .tx(tx)
                    .build();
            factoryIfEventQueueStrategy.enqueueIfEventQueue(insertEventQueueReportVo);
        }
        catch(Exception e){
            log.error("EventQueue enqueue error",e);
        }
    }


    /**
     * 포트의 캐리어가 도착했음을 보고
     * 1. 포트 테이블 조회
     * 포트의 transferState -> ReadyToProcess 변경
     * 2. Carrier 조회
     * Carrier 의 위치 정보를 Port 로 변경
     *
     * @param message 받은 메시지
     */
    @Override
    @Transactional
    public void loadCompleted(BaseMessage<LoadCompletedBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String carrierName = message.getBody().getCarrierName();
        String portType = message.getBody().getPortType();
        String portTransportMode = message.getBody().getPortTransportMode();
        String messageName =  message.getMessageName();
        String transportJobName = message.getBody().getTransportJobName();
        String actualWeight = message.getBody().getActualWeight();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        LoadCompletedCommand command = LoadCompletedCommand.builder()
                .transactionInfo(tx)
                .carrierTransportState(CarrierTransportState.ON_PORT.getValue())
                .carrierName(carrierName)
                .equipmentName(equipmentName)
                .portName(portName)
                .build();
        Optional<PortDef> optionalPortDef = portService.findPortDefByEquipmentNameAndPortName(equipmentName, portName);
        if(optionalPortDef.isEmpty()){
            return;
        }
        PortDef portDef = optionalPortDef.get();
        Optional<Port> optionalPorts = portService.findPortByEquipmentNameAndPortName(equipmentName,portName);
        if(optionalPorts.isEmpty()){
            return;
        }
        Port port = optionalPorts.get();
        port.loadCompleted(command);
        port = portService.save(port);
        PortHistoryEntity portHistoryEntity = portMapper.toHistoryEntity(port);
        historyService.saveHistory(portHistoryEntity);

        try{
            InsertEventQueueReportVo insertEventQueueReportVo
                    = InsertEventQueueReportVo
                    .builder()
                    .transportJobName(transportJobName)
                    .messageName(messageName)
                    .optionalPort(optionalPorts)
                    .optionalPortDef(optionalPortDef)
                    .carrierName(carrierName)
//                    .actualZoneName()
                    .actualWeight(actualWeight)
//                    .actualRackLocationId()
//                    .errorTexts()
//                    .jobType(jobType)
                    .tx(tx)
                    .build();
            factoryIfEventQueueStrategy.enqueueIfEventQueue(insertEventQueueReportVo);
        }
        catch(Exception e){
            log.error("EventQueue enqueue error",e);
        }

    }

    @Override
    public void carrierLocationChanged(BaseMessage<CarrierLocationChangedBody> message) {

        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String messageName = message.getMessageName();
        String transportJobName = message.getBody().getTransportJobName();
        String carrierName = message.getBody().getCarrierName();
        String carrierType = message.getBody().getCarrierType();
        String currentEquipmentName = message.getBody().getCurrentEquipmentName();
        String currentPortName = message.getBody().getCurrentPortName();
        String currentZoneName = message.getBody().getCurrentZoneName();
        String currentPositionType = message.getBody().getCurrentPositionType();
        String currentPositionName = message.getBody().getCurrentPositionName();
        log.info("messageName : {}",messageName);
        log.info("carrierName : {}",carrierName);

    }
}
