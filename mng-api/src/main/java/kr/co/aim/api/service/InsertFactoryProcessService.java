package kr.co.aim.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.api.strategy.FactoryIfEventQueueStrategy;
import kr.co.aim.api.vo.insert.ops.InsertEventQueueReportVo;
import kr.co.aim.api.vo.insert.ops.TransportCancelReasonVo;
import kr.co.aim.api.vo.port.TransportStateChangedVo;
import kr.co.aim.common.Utils.FormatUtils;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.format.*;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.api.strategy.FactoryProcessStrategy;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.*;
import kr.co.aim.domain.model.*;
import kr.co.aim.infra.persistence.entity.PortHistoryEntity;
import kr.co.aim.infra.persistence.entity.TransportJobHistoryEntity;
import kr.co.aim.infra.persistence.entity.TransportOrderHistoryEntity;
import kr.co.aim.infra.persistence.mapper.PortMapper;
import kr.co.aim.infra.persistence.mapper.TransportJobMapper;
import kr.co.aim.infra.persistence.mapper.TransportOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
@ConditionalOnProperty(name = "factory.type", havingValue = "insert")
@Profile({"pex","tex","scheduler"})
public class InsertFactoryProcessService implements FactoryProcessStrategy {

    private final HistoryService historyService;
    private final ObjectMapper objectMapper;

    private final PortService portService;
    private final PortDefService portDefService;
    private final PortMapper portMapper;

    private final TransportJobService transportJobService;
    private final TransportJobMapper transportJobMapper;

    private final TransportOrderService transportOrderService;
    private final TransportOrderMapper transportOrderMapper;

    private final IfEventQueueService ifEventQueueService;
    private final FactoryIfEventQueueStrategy factoryIfEventQueueStrategy;

    private final NamingRuleService namingRuleService;

    private final InsertExternalInterfaceService insertExternalInterfaceService;


    @Override
    @Transactional(value = "mssqlTransactionManager")
    public BaseMessage<TransportJobRequestBody> carrierDispatchRequest(BaseMessage<CarrierDispatchRequestBody> message) {
        // 1. Port, PortDef 를 조회
        // 2. PortDef 의 workStaionName 을 기준으로 outbound 명령이 있는지 조회
        // 3. 있다면, port의 상태를 reserveToLoad 로 변경 후 반송메시지 반환

        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String carrierName = message.getBody().getCarrierName();
        String portType =  message.getBody().getPortType();
        String portTransportMode =  message.getBody().getPortTransportMode();

        Optional<Port> optionalPort = portService.findPortByEquipmentNameAndPortName(equipmentName,portName);
        Optional<PortDef> optionalPortDef = portDefService.findPortDefByEquipmentNameAndPortName(equipmentName,portName);
        PortDef portDef = null;
        Port port = null;
        TransportJob transportJob = null;

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
            if(StringUtils.isNotBlank(portDef.getWorkCenterName())){

                // 이 쿼리가 WORK_CENTER를 기준으로 FIFO 로 ORDER를 가져오는 로직
                List<TransportOrder> transportOrders = transportOrderService.findOutboundOrderForTransportRequest(
                        TransportOrderType.OUTBOUND.getValue(),
                        TransportOrderStatus.ACCEPTED.getValue(),
                        portDef.getWorkCenterName()
                );

                if(CollectionUtils.isNotEmpty(transportOrders)){
                    TransactionInfo tx = TransactionInfo.now(EventName.AUTO_TRANSPORT.getValue(), SystemName.MNG.getValue(), EventName.AUTO_TRANSPORT.getValue());
                    TransportOrder transportOrder = transportOrders.get(0);

                    Optional<TransportJob> optionalTransportJob = transportJobService.findByOrderId(transportOrder.getTransportOrderId());

                    if(optionalTransportJob.isPresent()){

                        transportJob = optionalTransportJob.get();
                        TransportJobStartRequestCommand startRequestCommand =
                                TransportJobStartRequestCommand
                                        .builder()
                                        .transportJobState(TransportJobState.START_REQUEST.getValue())
                                        .destinationEquipmentName(port.getEquipmentName())
                                        .destinationPortName(port.getPortName())
                                        .transactionInfo(tx)
                                        .build();

                        transportJob.startRequestTransportJob(startRequestCommand);

                        transportJob = transportJobService.save(transportJob);
                        TransportJobHistoryEntity transportJobHistoryEntity = transportJobMapper.toHistoryEntity(transportJob);
                        historyService.saveHistory(transportJobHistoryEntity);
                        String transactionId = FormatUtils.getTransactionId(tx.eventTime());;
                        // create message
                        BaseMessage<TransportJobRequestBody> request = new BaseMessage<>();

                        request.setMessageName(MessageList.TRANSPORT_JOB_REQUEST.getMessageName());
                        request.setTransactionId(transactionId);
                        request.setEventTime(transactionId);
                        request.setMessageOwner(SystemName.MNG.getValue());
                        request.setMessageFrom(SystemName.MNG.getValue());
                        request.setMessageTo(SystemName.WCS.getValue());
                        request.setResultCode(ResultCode.OK.getValue());
                        request.setResultMessage("");

                        TransportJobRequestBody body = transportJobService.createTransportJobMessage(transportJob);
                        request.setBody(body);

                        // port update
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
    public BaseMessage<TransportJobRequestBody> transportOrderRequest(BaseMessage<TransportOrderRequestBody> message) {
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
        String galWarehouse = transportOrder.getGalWarehouse();
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

            String transportJobName = namingRuleService.getTransportJobName(SystemName.GAL.getValue(),tx.eventTime());
            String sourceEquipmentName = "";
            String sourcePortName = "";
            String sourcePositionTypeName = "";
            String sourcePositionName = "";
            String destinationEquipmentName = "";
            if(StringUtils.isNotEmpty(locationId)){
                Optional<PortDef> optionalPortDef = portDefService.findByLocationId(locationId);
                if(optionalPortDef.isPresent()){
                    PortDef portDef = optionalPortDef.get();
//                    sourceEquipmentName = portDef.getId().getEquipmentName();
//                    sourcePortName = portDef.getId().getPortName();
//                    sourcePositionTypeName = PositionTypeName.PORT.getValue();
//                    sourcePositionName = portDef.getId().getPortName();

                    sourceEquipmentName = portDef.getEquipmentName();
                    sourcePortName = portDef.getPortName();
                    sourcePositionTypeName = PositionTypeName.PORT.getValue();
                    sourcePositionName = portDef.getPortName();
                }
            }
            if(StringUtils.isNotEmpty(galWarehouse)){
                //destinationEquipmentName = galWarehouse;
            }
            // transportOrder 를 REQUESTED 상태로 변경해서 다시 보내는 로직이 없도록 수정
            transportOrder.setTransportStatus(TransportOrderStatus.REQUESTED.getValue());
            transportOrder = transportOrderService.save(transportOrder);

            TransportJobCreateCommand command =
                    TransportJobCreateCommand.builder()
                            .transportJobName(transportJobName)
                            .carrierName(carrierName)
                            .transportType(transportType)
                            .transportJobState(TransportJobState.REQUESTED.getValue())
                            .carrierType(carrierType)
                            .travelProfile(travelProfile)
                            .sourceEquipmentName(sourceEquipmentName)
                            .sourcePortName(sourcePortName)
                            .sourceZoneName(sourceZoneName)
                            .sourcePositionTypeName(sourcePositionTypeName)
                            .sourcePositionName(sourcePositionName)
                            .destinationEquipmentName(destinationEquipmentName)
                            .destinationZoneName(requestedZoneName)
                            .priority(priority)
                            .requestSource(TransportJobRequestType.GAL.getValue())
                            .createTime(tx.eventTime())
                            .orderId(transportOrderId)
                            .transactionInfo(tx)
                            .build();

            TransportJob transportJob = transportJobService.createTransportJob(command);
            TransportOrderHistoryEntity transportOrderHistoryEntity = transportOrderMapper.toHistoryEntity(transportOrder);
            historyService.saveHistory(transportOrderHistoryEntity);

            // create message
            BaseMessage<TransportJobRequestBody> request = new BaseMessage<>();

            request.setMessageName(MessageList.TRANSPORT_JOB_REQUEST.getMessageName());
            request.setTransactionId(message.getTransactionId());
            request.setMessageFrom(SystemName.MNG.getValue());
            request.setMessageOwner(SystemName.MNG.getValue());
            request.setMessageTo(SystemName.WCS.getValue());
            request.setEventTime(message.getEventTime());
            request.setResultCode(ResultCode.OK.getValue());
            request.setResultMessage("");

            TransportJobRequestBody body = transportJobService.createTransportJobMessage(transportJob);
            request.setBody(body);

            return request;
        }

        return null;
    }

    @Override
    @Transactional(value = "mssqlTransactionManager")
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
        Optional<PortDef> optionalPortDef = portDefService.findPortDefByEquipmentNameAndPortName(equipmentName, portName);
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
            log.info("enqueueIfEventQueue start");
            InsertEventQueueReportVo insertEventQueueReportVo
                    = InsertEventQueueReportVo
                    .builder()
                    .transportJobName(transportJobName)
                    .messageName(messageName)
                    .optionalPort(optionalPorts)
                    .optionalPortDef(optionalPortDef)
                    .carrierName(carrierName)
                    .actualWeight(actualWeight)
                    .actualRackLocationId(actualLocationId)
                    .tx(tx)
                    .build();
            factoryIfEventQueueStrategy.enqueueIfEventQueue(insertEventQueueReportVo);
            log.info("enqueueIfEventQueue end");
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
    @Transactional(value = "mssqlTransactionManager")
    public BaseMessage<CarrierInfoDownloadSendBody> loadCompleted(BaseMessage<LoadCompletedBody> message) {
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
        Optional<PortDef> optionalPortDef = portDefService.findPortDefByEquipmentNameAndPortName(equipmentName, portName);
        if(optionalPortDef.isEmpty()){
            return null;
        }
        PortDef portDef = optionalPortDef.get();
        Optional<Port> optionalPorts = portService.findPortByEquipmentNameAndPortName(equipmentName,portName);
        if(optionalPorts.isEmpty()){
            return null;
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
                    .actualWeight(actualWeight)
                    .tx(tx)
                    .build();
            factoryIfEventQueueStrategy.enqueueIfEventQueue(insertEventQueueReportVo);
        }
        catch(Exception e){
            log.error("EventQueue enqueue error",e);
        }
        return null;

    }

    @Override
    @Transactional(value = "mssqlTransactionManager")
    public void carrierLocationChanged(BaseMessage<CarrierLocationChangedBody> message) {

        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String messageName = message.getMessageName();
        String transportJobName = message.getBody().getTransportJobName();
        String carrierName = message.getBody().getCarrierName();
        String carrierType = message.getBody().getCarrierType();
        String currentEquipmentName = message.getBody().getCurrentEquipmentName();
        String currentZoneName = message.getBody().getCurrentZoneName();
        String currentPositionType = message.getBody().getCurrentPositionType();
        String currentPositionName = message.getBody().getCurrentPositionName();
        log.info("messageName : {}",messageName);
        log.info("carrierName : {}",carrierName);

    }

    @Override
    @Transactional(value = "mssqlTransactionManager")
    public void transportJobCancelCompleted(BaseMessage<TransportJobCancelCompletedBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String messageName =  message.getMessageName();
        String carrierName = message.getBody().getCarrierName();
        String currentEquipmentName = message.getBody().getCurrentEquipmentName();
        String currentPositionType = message.getBody().getCurrentPositionType();
        String currentPositionName = message.getBody().getCurrentPositionName();
        String currentPortName = currentPositionName;
        String transportJobName = message.getBody().getTransportJobName();
        String transportType =  message.getBody().getTransportType();
        String orderId =  message.getBody().getOrderId();
        String requestSource =  message.getBody().getRequestSource();
        String actualWeight = message.getBody().getActualWeight();
        String travelProfile =  message.getBody().getTravelProfile();
        List<TransportJobCancelCompletedReasonBody> reasons = message.getBody().getReasons();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        Optional<TransportJob> optionalTransportJob = transportJobService.findByTransportJobName(transportJobName);
        if(optionalTransportJob.isPresent()){
            TransportJob transportJob = optionalTransportJob.get();
            TransportJobUpdateCommand command =
                    TransportJobUpdateCommand
                            .builder()
                            .transportJobState(TransportJobState.CANCELLED.getValue())
                            .transactionInfo(tx)
                            .build();
            transportJob.changeTransportJob(command);
            transportJob = transportJobService.save(transportJob);
            TransportJobHistoryEntity transportJobHistoryEntity = transportJobMapper.toHistoryEntity(transportJob);
            historyService.saveHistory(transportJobHistoryEntity);
            Optional<Port> optionalPort = portService.findPortByEquipmentNameAndPortName(currentEquipmentName,currentPortName);
            Optional<PortDef> optionalPortDef = portDefService.findPortDefByEquipmentNameAndPortName(currentEquipmentName,currentPortName);
            // insert EventQueue

            try{
                if(CollectionUtils.isNotEmpty(reasons)){
                    List<TransportCancelReasonVo> cancelReasonVoList = new ArrayList<>();
                    for(TransportJobCancelCompletedReasonBody reason : reasons){
                        TransportCancelReasonVo reasonVo = TransportCancelReasonVo
                                .builder()
                                .code(reason.getCode())
                                .message(reason.getMessage())
                                .build();
                        cancelReasonVoList.add(reasonVo);
                    }
                    InsertEventQueueReportVo insertEventQueueReportVo
                            = InsertEventQueueReportVo
                            .builder()
                            .transportJobName(transportJob.getTransportJobName())
                            .messageName(messageName)
                            .optionalPortDef(optionalPortDef)
                            .optionalPort(optionalPort)
                            .carrierName(carrierName)
//                            .actualZoneName()
                            .actualWeight(actualWeight)
//                            .actualRackLocationId()
                            .reasonList(cancelReasonVoList)
                            .tx(tx)
                            .build();
                    factoryIfEventQueueStrategy.enqueueIfEventQueue(insertEventQueueReportVo);

                }

            }
            catch(Exception e){
                log.error("EventQueue enqueue error",e);
            }
        }
    }

    @Override
    @Transactional(value = "mssqlTransactionManager")
    public void transportJobCompleted(BaseMessage<TransportJobCompletedBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String messageName =  message.getMessageName();
        String carrierName = message.getBody().getCarrierName();
        String transportJobName = message.getBody().getTransportJobName();

        String actualWeight = message.getBody().getActualWeight();
        String actualZoneName = message.getBody().getDestinationZoneName();
        String destinationEquipmentName = message.getBody().getDestinationEquipmentName();
        String destinationPositionTypeName = message.getBody().getDestinationPositionTypeName();
        String destinationPositionName = message.getBody().getDestinationPositionName();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        Optional<Port> optionalPort = Optional.empty();
        Optional<PortDef> optionalPortDef = Optional.empty();

        Optional<TransportJob> optionalTransportJob = transportJobService.findByTransportJobName(transportJobName);
        if(optionalTransportJob.isPresent()){
            if(StringUtils.equals(destinationPositionTypeName,PositionTypeName.PORT.getValue())){
                optionalPort = portService.findPortByEquipmentNameAndPortName(destinationEquipmentName,destinationPositionName);
                optionalPortDef = portDefService.findPortDefByEquipmentNameAndPortName(destinationEquipmentName,destinationPositionName);
            }

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

            Optional<TransportOrder> optionalTransportOrder = transportOrderService.findByTransportOrderId(transportJob.getOrderId());
            if(optionalTransportOrder.isPresent()){
                TransportOrder transportOrder = optionalTransportOrder.get();

                TransportOrderStatusChangeCommand completedCommand =
                        TransportOrderStatusChangeCommand
                                .builder()
                                .transactionInfo(tx)
                                .transportStatus(TransportOrderStatus.COMPLETED.getValue())
                                .build();
                transportOrder.completed(completedCommand);
                transportOrder = transportOrderService.save(transportOrder);
                TransportOrderHistoryEntity transportOrderHistoryEntity = transportOrderMapper.toHistoryEntity(transportOrder);
                historyService.saveHistory(transportOrderHistoryEntity);
            }

            // insert EventQueue
            try{
                InsertEventQueueReportVo insertEventQueueReportVo
                        = InsertEventQueueReportVo
                        .builder()
                        .transportJobName(transportJob.getTransportJobName())
                        .messageName(messageName)
                        .optionalPort(optionalPort)
                        .optionalPortDef(optionalPortDef)
                        .carrierName(carrierName)
                        .actualZoneName(actualZoneName)
                        .actualWeight(actualWeight)
                        .actualRackLocationId(destinationPositionName)
                        .tx(tx)
                        .build();
                factoryIfEventQueueStrategy.enqueueIfEventQueue(insertEventQueueReportVo);
            }
            catch(Exception e){
                log.error("EventQueue enqueue error",e);
            }
        }
    }

    @Override
    @Transactional(value = "mssqlTransactionManager")
    public void transportJobReply(BaseMessage<TransportJobReplyBody> message) {
        String messageName = message.getMessageName();
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();
        String resultCode = message.getResultCode();
        String resultMessage = message.getResultMessage();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);

        String transportJobName = message.getBody().getTransportJobName();
        String carrierName = message.getBody().getCarrierName();
        String sourceEquipmentName = message.getBody().getSourceEquipmentName();
        String sourcePortName = message.getBody().getSourcePositionName();
        Optional<TransportJob> optionalTransportJob = transportJobService.findByTransportJobName(transportJobName);
        Optional<Port> optionalPort = portService.findPortByEquipmentNameAndPortName(sourceEquipmentName,sourcePortName);
        Optional<PortDef> optionalPortDef = portDefService.findPortDefByEquipmentNameAndPortName(sourceEquipmentName,sourcePortName);

        if(optionalTransportJob.isPresent()){
            TransportJob transportJob = optionalTransportJob.get();
            String transportJobState = "";
            if(StringUtils.equals(resultCode,ResultCode.OK.getValue())){
                transportJobState = TransportJobState.ACCEPTED.getValue();
            }else{
                transportJobState = TransportJobState.REJECTED.getValue();
            }
            TransportJobUpdateCommand command =
                    TransportJobUpdateCommand
                            .builder()
                            .transportJobState(transportJobState)
                            .transactionInfo(tx)
                            .build();
            transportJob.changeTransportJob(command);
            transportJob = transportJobService.save(transportJob);
            TransportJobHistoryEntity transportJobHistoryEntity = transportJobMapper.toHistoryEntity(transportJob);
            historyService.saveHistory(transportJobHistoryEntity);

            Optional<TransportOrder> optionalTransportOrder = transportOrderService.findByTransportOrderId(transportJob.getOrderId());
            if(optionalTransportOrder.isPresent()){
                TransportOrder transportOrder = optionalTransportOrder.get();
                TransportOrderStatusChangeCommand acceptCommand =
                        TransportOrderStatusChangeCommand
                                .builder()
                                .transactionInfo(tx)
                                .transportStatus(TransportOrderStatus.ACCEPTED.getValue())
                                .build();
                transportOrder.accept(acceptCommand);
                transportOrder = transportOrderService.save(transportOrder);
                TransportOrderHistoryEntity transportOrderHistoryEntity = transportOrderMapper.toHistoryEntity(transportOrder);
                historyService.saveHistory(transportOrderHistoryEntity);
            }

            // insert EventQueue
            try{
                InsertEventQueueReportVo insertEventQueueReportVo
                        = InsertEventQueueReportVo
                        .builder()
                        .transportJobName(transportJob.getTransportJobName())
                        .messageName(messageName)
                        .optionalPort(optionalPort)
                        .optionalPortDef(optionalPortDef)
                        .carrierName(carrierName)
                        .resultCode(resultCode)
                        .resultMessage(resultMessage)
                        .tx(tx)
                        .build();
                factoryIfEventQueueStrategy.enqueueIfEventQueue(insertEventQueueReportVo);
            }
            catch(Exception e){
                log.error("EventQueue enqueue error",e);
            }
        }
    }

    @Override
    @Transactional(value = "mssqlTransactionManager")
    public void transportJobStarted(BaseMessage<TransportJobStartedBody> message) {
        String messageName = message.getMessageName();
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String transportJobName = message.getBody().getTransportJobName();
        String transportType = message.getBody().getTransportType();
        String carrierName = message.getBody().getCarrierName();
        String sourceEquipmentName = message.getBody().getSourceEquipmentName();
        String sourceZoneName = message.getBody().getSourceZoneName();
        String sourcePositionTypeName = message.getBody().getSourcePositionTypeName();
        String sourcePositionName = message.getBody().getSourcePositionName();
        String destinationEquipmentName = message.getBody().getDestinationEquipmentName();
        String destinationZoneName = message.getBody().getDestinationZoneName();
        String destinationPositionTypeName = message.getBody().getDestinationPositionTypeName();
        String destinationPositionName = message.getBody().getDestinationPositionName();
        String priority = message.getBody().getPriority();
        String orderId = message.getBody().getOrderId();
        String orderLineNumber = message.getBody().getOrderLineNumber();
        String productionType = message.getBody().getProductionType();
        String lotName = message.getBody().getLotName();
        String itemName = message.getBody().getItemName();
        String requestSource = message.getBody().getRequestSource();
        String travelProfile = message.getBody().getTravelProfile();
        String actualWeight = message.getBody().getActualWeight();
        String carrierType = message.getBody().getCarrierType();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);

        if(StringUtils.equals(SystemName.GAL.getValue(),requestSource)){
            // 비관적 lock 시 EventQueue 넣으면서 에러 발생
            //Optional<TransportJob> optionalTransportJob = transportJobService.findWithLockByTransportJobName(transportJobName);
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
                try{
                    InsertEventQueueReportVo insertEventQueueReportVo
                            = InsertEventQueueReportVo
                            .builder()
                            .transportJobName(transportJob.getTransportJobName())
                            .messageName(messageName)
//                            .port()
//                            .portDef()
                            .carrierName(carrierName)
//                            .actualZoneName()
//                            .actualWeight()
//                            .actualRackLocationId()
//                            .errorTexts()
                            .orderType(transportJob.getTransportType()) // I | O | R
                            .requestSource(requestSource) // WCS | GAL
                            .tx(tx)
                            .build();
                    factoryIfEventQueueStrategy.enqueueIfEventQueue(insertEventQueueReportVo);
                }
                catch(Exception e){
                    log.error("EventQueue enqueue error",e);
                }
            }
        }
        else if(StringUtils.equals(SystemName.WCS.getValue(),requestSource)){
            // 반송jobName으로 반송잡을 찾음
            // 없으면 생성
            // 있으면 started 상태로 변경후 InsertEventQueueReportVo 생성

            Optional<TransportJob> optionalTransportJob = transportJobService.findByTransportJobName(transportJobName);
            TransportJob transportJob = null;
            if(optionalTransportJob.isPresent()){
                transportJob = optionalTransportJob.get();
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

            }
            else {
                TransportJobCreateCommand command =
                        TransportJobCreateCommand.builder()
                                .transportJobName(transportJobName)
                                .carrierName(carrierName)
                                .transportType(transportType)
                                .transportJobState(TransportJobState.STARTED.getValue())
                                .carrierType(carrierType)
                                .travelProfile(travelProfile)
                                .sourceEquipmentName(sourceEquipmentName)
                                .sourcePortName(sourcePositionName)
                                .sourceZoneName(sourceZoneName)
                                .sourcePositionTypeName(sourcePositionTypeName)
                                .sourcePositionName(sourcePositionName)
                                .destinationEquipmentName(destinationEquipmentName)
                                .destinationPortName(destinationPositionName)
                                .destinationZoneName(destinationZoneName)
                                .destinationPositionTypeName(destinationPositionTypeName)
                                .destinationPositionName(destinationPositionName)
                                .priority( StringUtils.isBlank(priority) ? 0 : Integer.parseInt(priority))
                                .requestSource(TransportJobRequestType.WCS.getValue())
                                .createTime(tx.eventTime())
                                .orderId(orderId)
                                .transactionInfo(tx)
                                .build();

                transportJob = transportJobService.createTransportJob(command);
            }
            try{
                InsertEventQueueReportVo insertEventQueueReportVo
                        = InsertEventQueueReportVo
                        .builder()
                        .transportJobName(transportJob.getTransportJobName())
                        .messageName(messageName)
//                            .port()
//                            .portDef()
                        .carrierName(carrierName)
//                            .actualZoneName()
//                            .actualWeight()
//                            .actualRackLocationId()
//                            .errorTexts()
                        .orderType(transportJob.getTransportType()) // I | O | R
                        .requestSource(requestSource) // WCS | GAL
                        .tx(tx)
                        .build();
                factoryIfEventQueueStrategy.enqueueIfEventQueue(insertEventQueueReportVo);
            }
            catch(Exception e){
                log.error("EventQueue enqueue error",e);
            }
        }
    }

    @Override
    @Transactional(value = "mssqlTransactionManager")
    public BaseMessage<CarrierDispatchRequestBody> loadRequest(BaseMessage<LoadRequestBody> message) {

        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String carrierName = message.getBody().getCarrierName();
        String portType = message.getBody().getPortType();
        String portTransportMode = message.getBody().getPortTransportMode();

        Optional<Port> optionalPorts =
                portService.findWithLockByEquipmentNameAndPortName(equipmentName,portName);

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

        Optional<PortDef> optionalPortDef = portDefService.findByEquipmentNameAndPortName(equipmentName,portName);
        if(optionalPortDef.isEmpty()){
            return null;
        }

        PortDef  portDef = optionalPortDef.get();

        if(
                StringUtils.equals(portDef.getDetailPortType(),DetailPortType.CRANE_BOTH_PND.getValue())
                || StringUtils.equals(portDef.getDetailPortType(),DetailPortType.CRANE_OUT_PND.getValue())

        ){
            BaseMessage<CarrierDispatchRequestBody> reply = new BaseMessage<>();
            reply.setTransactionId(message.getTransactionId());
            reply.setMessageFrom(SystemName.MNG.getValue());
            reply.setMessageOwner(SystemName.MNG.getValue());
            reply.setMessageTo(SystemName.MNG.getValue());
            reply.setEventTime(message.getEventTime());
            reply.setResultMessage("");
            reply.setResultCode(ResultCode.OK.getValue());
            reply.setMessageName(MessageList.CARRIER_DISPATCH_REQUEST.getMessageName());

            CarrierDispatchRequestBody body = CarrierDispatchRequestBody.builder()
                    .equipmentName(equipmentName)
                    .portName(portName)
                    .build();
            reply.setBody(body);
            return reply;
        }

        return null;
    }

    @Override
    public BaseMessage<TransportJobValidationRequestBody> transportOrderValidationRequest(BaseMessage<TransportOrderRequestBody> message) {
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
        String galWarehouse = transportOrder.getGalWarehouse();
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

            if(StringUtils.equals(TransportOrderType.OUTBOUND.getValue(),transportType)){
                String transportJobName = namingRuleService.getTransportJobName(SystemName.GAL.getValue(),tx.eventTime());
                String sourceEquipmentName = "";
                String sourcePortName = "";
                String sourcePositionTypeName = "";
                String sourcePositionName = "";
                String destinationEquipmentName = "";
                String destinationPortName = "";
                // find by workStationId By PortDef

                List<String> detailPortTypes = new ArrayList<>();
                List<String> portTypes = new ArrayList<>();

                detailPortTypes.add(DetailPortType.CRANE_BOTH_PND.getValue());
                detailPortTypes.add(DetailPortType.CRANE_OUT_PND.getValue());

                portTypes.add(PortType.BOTH.getValue());
                portTypes.add(PortType.OUTPUT.getValue());

                List<PortDef> portDefList = portDefService.findByWorkCenterNameAndDetailPortTypeInAndPortTypeIn(
                        workStationId,
                        detailPortTypes,
                        portTypes
                );

                if(CollectionUtils.isEmpty(portDefList)){
                    log.info("workCenter not found : workCenterName : {}" , workStationId);
                    return null;
                }
                PortDef portDef = portDefList.get(0);
                destinationEquipmentName = portDef.getEquipmentName();
                destinationPortName =  portDef.getPortName();

                sourceZoneName = requestedZoneName;
                sourcePositionTypeName = PositionTypeName.SHELF.getValue();
                sourcePositionName = locationId;

                // transportOrder 를 REQUESTED 상태로 변경해서 다시 보내는 로직이 없도록 수정
                transportOrder.setTransportStatus(TransportOrderStatus.REQUESTED.getValue());
                transportOrder = transportOrderService.save(transportOrder);

                TransportJobCreateCommand command =
                        TransportJobCreateCommand.builder()
                                .transportJobName(transportJobName)
                                .carrierName(carrierName)
                                .transportType(transportType)
                                .transportJobState(TransportJobState.REQUESTED.getValue())
                                .carrierType(carrierType)
                                .travelProfile(travelProfile)
                                .sourceEquipmentName(sourceEquipmentName)
                                .sourcePortName(sourcePortName)
                                .sourceZoneName(sourceZoneName)
                                .sourcePositionTypeName(sourcePositionTypeName)
                                .sourcePositionName(sourcePositionName)
                                .destinationEquipmentName(destinationEquipmentName)
                                .destinationPortName(destinationPortName)
                                .priority(priority)
                                .requestSource(TransportJobRequestType.GAL.getValue())
                                .createTime(tx.eventTime())
                                .orderId(transportOrderId)
                                .transactionInfo(tx)
                                .build();

                TransportJob transportJob = transportJobService.createTransportJob(command);
                TransportOrderHistoryEntity transportOrderHistoryEntity = transportOrderMapper.toHistoryEntity(transportOrder);
                historyService.saveHistory(transportOrderHistoryEntity);

                // create message
                BaseMessage<TransportJobValidationRequestBody> request = new BaseMessage<>();

                request.setMessageName(MessageList.TRANSPORT_JOB_VALIDATION_REQUEST.getMessageName());
                request.setTransactionId(message.getTransactionId());
                request.setMessageFrom(SystemName.MNG.getValue());
                request.setMessageOwner(SystemName.MNG.getValue());
                request.setMessageTo(SystemName.WCS.getValue());
                request.setEventTime(message.getEventTime());
                request.setResultCode(ResultCode.OK.getValue());
                request.setResultMessage("");

                TransportJobValidationRequestBody body = transportJobService.createTransportJobValidationMessage(transportJob);
                request.setBody(body);

                return request;
            }
        }

        return null;
    }

    @Override
    public void transportJobValidationReply(BaseMessage<TransportJobValidationReplyBody> message) {
        String messageName = message.getMessageName();
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();
        String resultCode = message.getResultCode();
        String resultMessage = message.getResultMessage();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);

        String transportJobName = message.getBody().getTransportJobName();
        String carrierName = message.getBody().getCarrierName();
        String sourceEquipmentName = message.getBody().getSourceEquipmentName();
        String sourcePortName = message.getBody().getSourcePositionName();
        Optional<TransportJob> optionalTransportJob = transportJobService.findByTransportJobName(transportJobName);
        Optional<Port> optionalPort = portService.findPortByEquipmentNameAndPortName(sourceEquipmentName,sourcePortName);
        Optional<PortDef> optionalPortDef = portDefService.findPortDefByEquipmentNameAndPortName(sourceEquipmentName,sourcePortName);

        if(optionalTransportJob.isPresent()){
            TransportJob transportJob = optionalTransportJob.get();
            String transportJobState = "";
            if(StringUtils.equals(resultCode,ResultCode.OK.getValue())){
                transportJobState = TransportJobState.ACCEPTED.getValue();
            }else{
                transportJobState = TransportJobState.REJECTED.getValue();
            }
            TransportJobUpdateCommand command =
                    TransportJobUpdateCommand
                            .builder()
                            .transportJobState(transportJobState)
                            .transactionInfo(tx)
                            .build();
            transportJob.changeTransportJob(command);
            transportJob = transportJobService.save(transportJob);
            TransportJobHistoryEntity transportJobHistoryEntity = transportJobMapper.toHistoryEntity(transportJob);
            historyService.saveHistory(transportJobHistoryEntity);

            Optional<TransportOrder> optionalTransportOrder = transportOrderService.findByTransportOrderId(transportJob.getOrderId());
            if(optionalTransportOrder.isPresent()){
                TransportOrder transportOrder = optionalTransportOrder.get();
                TransportOrderStatusChangeCommand acceptCommand =
                        TransportOrderStatusChangeCommand
                                .builder()
                                .transactionInfo(tx)
                                .transportStatus(TransportOrderStatus.ACCEPTED.getValue())
                                .build();
                transportOrder.accept(acceptCommand);
                transportOrder = transportOrderService.save(transportOrder);
                TransportOrderHistoryEntity transportOrderHistoryEntity = transportOrderMapper.toHistoryEntity(transportOrder);
                historyService.saveHistory(transportOrderHistoryEntity);
            }

            // insert EventQueue
            try{
                InsertEventQueueReportVo insertEventQueueReportVo
                        = InsertEventQueueReportVo
                        .builder()
                        .transportJobName(transportJob.getTransportJobName())
                        .messageName(messageName)
                        .optionalPort(optionalPort)
                        .optionalPortDef(optionalPortDef)
                        .carrierName(carrierName)
                        .resultCode(resultCode)
                        .resultMessage(resultMessage)
                        .tx(tx)
                        .build();
                factoryIfEventQueueStrategy.enqueueIfEventQueue(insertEventQueueReportVo);
            }
            catch(Exception e){
                log.error("EventQueue enqueue error",e);
            }
        }
    }

    @Override
    public void eventQueueReport(BaseMessage<EventQueueReportBody> message) {

        EventQueueReportBody body = message.getBody();

        IfEventQueue ifEventQueue =
                IfEventQueue
                        .builder()
                        .id(body.getId())
                        .eventType(body.getEventType())
                        .payload(body.getPayload())
                        .ifStatus(body.getIfStatus())
                        .carrierName(body.getCarrierName())
                        .idocId(body.getIdocId())
                        .orderId(body.getOrderId())
                        .orderLineNumber(body.getOrderLineNumber())
                        .retryCNT(body.getRetryCNT())
                        .errMSG(body.getErrMSG())
                        .createTime(body.getCreateTime())
                        .updateTime(body.getUpdateTime())
                        .build();

        try {
            // DB2 H2transReport
            insertExternalInterfaceService.reportH2trans(ifEventQueue);
            // ifEventQueue 상태를 Success 로 변경
            ifEventQueueService.reportCompleted(ifEventQueue.getId());

        } catch (Exception e) {
            // retry cnt ++
            // 만일 3초과면, ready -> fail 로 데이터 변경
            log.error("reportFail id {} ",ifEventQueue.getId());
            try {
                Optional<IfEventQueue> optionalIfEventQueue
                        = ifEventQueueService.increaseRetryCnt(ifEventQueue.getId());
                if(optionalIfEventQueue.isPresent()){
                    if(optionalIfEventQueue.get().getRetryCNT() > 3){
                        ifEventQueueService.reportFailed(ifEventQueue.getId());
                    }
                }
            } catch (Exception e1){
                log.error("final report error", e1);
                log.error("increase & reportFail id {} ",ifEventQueue.getId());
            }
        }
    }
}
