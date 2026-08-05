package kr.co.aim.api.service;

import kr.co.aim.common.enums.*;
import kr.co.aim.common.format.*;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.api.strategy.FactoryProcessStrategy;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.*;
import kr.co.aim.domain.model.*;
import kr.co.aim.domain.model.ProductionOrder;
import kr.co.aim.infra.config.RabbitConfig;
import kr.co.aim.infra.persistence.entity.CarrierHistoryEntity;
import kr.co.aim.infra.persistence.entity.LotCarrierMappingHistoryEntity;
import kr.co.aim.infra.persistence.entity.PortHistoryEntity;
import kr.co.aim.infra.persistence.entity.TransportJobHistoryEntity;
import kr.co.aim.infra.persistence.mapper.CarrierMapper;
import kr.co.aim.infra.persistence.mapper.LotCarrierMappingMapper;
import kr.co.aim.infra.persistence.mapper.PortMapper;
import kr.co.aim.infra.persistence.mapper.TransportJobMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
@Profile({"pex","tex","scheduler"})
public class PowderFactoryProcessService implements FactoryProcessStrategy {

    private final HistoryService historyService;

    private final WhereDispatchService whereDispatchService;
    private final WhatDispatchService whatDispatchService;

    private final PortMapper portMapper;
    private final PortDefService portDefService;
    private final PortService portService;

    private final ProductDefService productDefService;

    private final CarrierService carrierService;
    private final CarrierMapper carrierMapper;

    private final EquipmentService equipmentService;
    private final EquipmentDefService equipmentDefService;

    private final TransportJobService  transportJobService;
    private final TransportJobMapper transportJobMapper;
    private final ProductionOrderService productionOrderService;
    private final CarrierSelectionService carrierSelectionService;
    private final LotCarrierMappingService lotCarrierMappingService;
    private final LotCarrierMappingMapper lotCarrierMappingMapper;

    private final PowderExternalInterfaceService powderExternalInterfaceService;
    private final IfEventQueueService ifEventQueueService;

    private final RabbitTemplate rabbitTemplate;

    @Override
    @Transactional(value = "mssqlTransactionManager")
    public BaseMessage<TransportJobRequestBody> carrierDispatchRequest(BaseMessage<CarrierDispatchRequestBody> message) {
        return whatDispatchService.whatDispatchRequest(message);
    }

    @Override
    @Transactional(value = "mssqlTransactionManager")
    public BaseMessage<DestinationDispatchRequestBody> unLoadRequest(BaseMessage<UnLoadRequestBody> message) {
        String messageName = message.getMessageName();
        String messageOwner = message.getMessageOwner();
        String resultMessage =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String carrierName = message.getBody().getCarrierName();
        String portType = message.getBody().getPortType();
        String portTransportMode = message.getBody().getPortTransportMode();

        Optional<Port> optionalPorts =  portService.findPortByEquipmentNameAndPortName(equipmentName,portName);

        if(optionalPorts.isEmpty()){
            return null;
        }

        Port port = optionalPorts.get();

        TransactionInfo tx = TransactionInfo.now(messageName,messageOwner,resultMessage);
        UnLoadRequestCommand command = UnLoadRequestCommand.builder()
                .transactionInfo(tx)
                .carrierName(carrierName)
                .equipmentName(equipmentName)
                .portName(portName)
                .build();

        port.unloadRequest(command);
        port = portService.save(port);
        PortHistoryEntity portHistoryEntity = portMapper.toHistoryEntity(port);
        historyService.saveHistory(portHistoryEntity);

        BaseMessage<DestinationDispatchRequestBody> reply = new BaseMessage<>();

        reply.setMessageName(MessageList.DESTINATION_DISPATCH_REQUEST.getMessageName());
        reply.setTransactionId(message.getTransactionId());
        reply.setMessageFrom(SystemName.MNG.getValue());
        reply.setMessageOwner(SystemName.MNG.getValue());
        reply.setMessageTo(SystemName.MNG.getValue());
        reply.setEventTime(message.getEventTime());
        reply.setResultMessage("");
        reply.setResultCode(ResultCode.OK.getValue());

        DestinationDispatchRequestBody body = DestinationDispatchRequestBody
                .builder()
                .equipmentName(equipmentName)
                .portName(portName)
                .carrierName(carrierName)
                .portType(portType)
                .portTransportMode(portTransportMode)
                .build();
        reply.setBody(body);

        return reply;
    }

    @Override
    @Transactional(value = "mssqlTransactionManager")
    public BaseMessage<TransportJobRequestBody> transportOrderRequest(BaseMessage<TransportOrderRequestBody> message) {
        return null;
    }

    @Override
    @Transactional(value = "mssqlTransactionManager")
    public void unLoadCompleted(BaseMessage<UnLoadCompletedBody> message) {

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
        String messageName = message.getMessageName();
        String messageOwner = message.getMessageOwner();
        String resultMessage =  message.getResultMessage();
        String messageFrom = message.getMessageFrom();

        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String carrierName = message.getBody().getCarrierName();
        String portType = message.getBody().getPortType();
        String portTransportMode = message.getBody().getPortTransportMode();

        LotCarrierMapping lotCarrierMapping = null;

        if(StringUtils.isEmpty(carrierName)){
            return null;
        }

        TransactionInfo tx = TransactionInfo.now(messageName,messageOwner,resultMessage);
        LoadCompletedCommand command = LoadCompletedCommand.builder()
                .transactionInfo(tx)
                .carrierTransportState(CarrierTransportState.ON_PORT.getValue())
                .carrierName(carrierName)
                .equipmentName(equipmentName)
                .portName(portName)
                .build();

        Optional<PortDef> optionalPortDef = portDefService.findPortDefByEquipmentNameAndPortName(equipmentName,portName);
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

        Optional<Carrier> optionalCarriers = carrierService.findByCarrierName(carrierName);
        if(optionalCarriers.isEmpty()){
            return null;
        }

        Carrier carrier = optionalCarriers.get();
        carrier.loadCompleted(command);
        carrier = carrierService.save(carrier);
        CarrierHistoryEntity carrierHistoryEntity = carrierMapper.toHistoryEntity(carrier);
        historyService.saveHistory(carrierHistoryEntity);

        Optional<LotCarrierMapping> optionalLotCarrierMapping = lotCarrierMappingService.findByCarrierName(carrierName);
        if(optionalLotCarrierMapping.isPresent()){
            lotCarrierMapping = optionalLotCarrierMapping.get();
            lotCarrierMapping.loadCompleted(command);
            lotCarrierMapping = lotCarrierMappingService.save(lotCarrierMapping);
            LotCarrierMappingHistoryEntity historyEntity = lotCarrierMappingMapper.toHistoryEntity(lotCarrierMapping);
            historyService.saveHistory(historyEntity);
        }


        // OUTPUT PORT 의 경우 EMPTY CONTAINER 인걸 체크 하고, CarrierInfoDownLoadSend
        // INPUT PORT 의 경우 MANTI 로 RECIPE Parameter 요청

        if(StringUtils.equals(PortType.OUTPUT.getValue(),portDef.getPortType())){
            BaseMessage<CarrierInfoDownloadSendBody> reply = new BaseMessage<>();

            reply.setMessageName(MessageList.CARRIER_INFO_DOWNLOAD_SEND.getMessageName());
            reply.setTransactionId(message.getTransactionId());
            reply.setMessageFrom(SystemName.MNG.getValue());
            reply.setMessageOwner(SystemName.MNG.getValue());
            reply.setMessageTo(SystemName.EAS.getValue());
            reply.setEventTime(message.getEventTime());
            reply.setResultMessage("");
            reply.setResultCode(ResultCode.OK.getValue());

            RecipeBody recipeBody = new RecipeBody();
            List<RecipeParameterListBody> recipeParameterListBodyList = new ArrayList<>();
            recipeBody.setParameterList(recipeParameterListBodyList);

            CarrierInfoDownloadSendBody body = CarrierInfoDownloadSendBody
                    .builder()
                    .equipmentName(equipmentName)
                    .portName(portName)
                    .carrierName(carrierName)
                    .recipe(recipeBody)
                    .build();
            reply.setBody(body);
            return reply;
        }
        else if(StringUtils.equals(PortType.INPUT.getValue(),portDef.getPortType())){
            // TODO: 환원로 케이스 개발하기
            // 환원로 설비의 경우 seq가 1인 carrier에 대해서만 RECIPE_REQUEST를 보낸다.
            // 그외의 경우에는 바로 EAS로 CarrierInfoDownloadSend를 보낸다.

            BaseMessage<RecipeRequestBody> mantiRequestMessage = new BaseMessage<>();
            mantiRequestMessage.setMessageName(MessageList.RECIPE_REQUEST.getMessageName());
            mantiRequestMessage.setTransactionId(message.getTransactionId());
            mantiRequestMessage.setMessageFrom(SystemName.MNG.getValue());
            mantiRequestMessage.setMessageOwner(SystemName.MNG.getValue());
            mantiRequestMessage.setMessageTo(SystemName.MANTI.getValue());
            mantiRequestMessage.setEventTime(message.getEventTime());
            mantiRequestMessage.setResultMessage("");
            mantiRequestMessage.setResultCode(ResultCode.OK.getValue());
            RecipeRequestBody body =
                    RecipeRequestBody
                            .builder()
                            .equipmentName(equipmentName)
                            .portName(portName)
                            .carrierName(carrierName)
                            .orderId(lotCarrierMapping.getOrderId())
                            .orderLineNumber(lotCarrierMapping.getOrderLineNumber())
                            .transactionId(lotCarrierMapping.getMngKey().toString())
                            .build();

            mantiRequestMessage.setBody(body);

            rabbitTemplate.convertAndSend(
                    RabbitConfig.EXCHANGE_MANTI,
                    RabbitConfig.ROUTING_MANTI,
                    mantiRequestMessage
            );

            return null;
        }

        return null;
    }

    @Override
    @Transactional(value = "mssqlTransactionManager")
    public void carrierLocationChanged(BaseMessage<CarrierLocationChangedBody> message) {
        String messageName = message.getMessageName();
        String messageOwner = message.getMessageOwner();
        String resultMessage =  message.getResultMessage();

        String transportJobName = message.getBody().getTransportJobName();
        String carrierName = message.getBody().getCarrierName();
        String carrierType = message.getBody().getCarrierType();
        String currentEquipmentName = message.getBody().getCurrentEquipmentName();
        String currentZoneName = message.getBody().getCurrentZoneName();
        String currentPositionType = message.getBody().getCurrentPositionType();
        String currentPositionName = message.getBody().getCurrentPositionName();
        String currentPortName = "";
        if(StringUtils.equals(PositionTypeName.PORT.getValue(), currentPositionType)){
            currentPortName = currentPositionName;
        }

        Optional<Carrier> optionalCarriers = carrierService.findByCarrierName(carrierName);
        if(optionalCarriers.isEmpty()){
            return;
        }
        Carrier carrier = optionalCarriers.get();

        TransactionInfo tx = TransactionInfo.now(messageName,messageOwner,resultMessage);
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

    @Override
    @Transactional(value = "mssqlTransactionManager")
    public void transportJobCancelCompleted(BaseMessage<TransportJobCancelCompletedBody> message) {
        String messageName = message.getMessageName();
        String messageOwner = message.getMessageOwner();
        String resultMessage =  message.getResultMessage();

        String carrierName = message.getBody().getCarrierName();
        String currentEquipmentName = message.getBody().getCurrentEquipmentName();
        String currentPositionType = message.getBody().getCurrentPositionType();
        String currentPositionName = message.getBody().getCurrentPositionName();
        String currentPortName = null;
        String transportJobName = message.getBody().getTransportJobName();
        String transportType =  message.getBody().getTransportType();
        String orderId =  message.getBody().getOrderId();
        String requestSource =  message.getBody().getRequestSource();
        String actualWeight = message.getBody().getActualWeight();
        String travelProfile =  message.getBody().getTravelProfile();
        List<TransportJobCancelCompletedReasonBody> reasons = message.getBody().getReasons();

        if(StringUtils.equals(PositionTypeName.PORT.getValue(),currentPositionType)){
            currentPortName = currentPositionName;
        }

        TransactionInfo tx = TransactionInfo.now(messageName,messageOwner,resultMessage);
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

        }
    }

    @Override
    @Transactional(value = "mssqlTransactionManager")
    public void transportJobCompleted(BaseMessage<TransportJobCompletedBody> message) {
        String messageName = message.getMessageName();
        String messageOwner = message.getMessageOwner();
        String resultMessage =  message.getResultMessage();

        String carrierName = message.getBody().getCarrierName();
        String transportJobName = message.getBody().getTransportJobName();

        String actualWeight = message.getBody().getActualWeight();
        String actualZoneName = message.getBody().getDestinationZoneName();

        TransactionInfo tx = TransactionInfo.now(messageName,messageOwner,resultMessage);

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

    @Override
    @Transactional(value = "mssqlTransactionManager")
    public void transportJobReply(BaseMessage<TransportJobReplyBody> message) {
        String messageName = message.getMessageName();
        String messageOwner = message.getMessageOwner();
        String resultMessage =  message.getResultMessage();

        TransactionInfo tx = TransactionInfo.now(messageName,messageOwner,resultMessage);

        String transportJobName = message.getBody().getTransportJobName();
        String carrierName = message.getBody().getCarrierName();
        // 비관적 Lock 으로 조회시 문제 발생
        //Optional<TransportJob> optionalTransportJob = transportJobService.findWithLockByTransportJobName(transportJobName);
        Optional<TransportJob> optionalTransportJob = transportJobService.findByTransportJobName(transportJobName);

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
        }
    }

    @Override
    @Transactional(value = "mssqlTransactionManager")
    public void transportJobStarted(BaseMessage<TransportJobStartedBody> message) {
        String messageName = message.getMessageName();
        String messageOwner = message.getMessageOwner();
        String resultMessage =  message.getResultMessage();
        String carrierName = message.getBody().getCarrierName();

        String transportJobName = message.getBody().getTransportJobName();
        String requestSource = message.getBody().getRequestSource();

        TransactionInfo tx = TransactionInfo.now(messageName,messageOwner,resultMessage);

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
        }
    }

    @Override
    @Transactional(value = "mssqlTransactionManager")
    public BaseMessage<CarrierDispatchRequestBody> loadRequest(BaseMessage<LoadRequestBody> message) {
        String messageName = message.getMessageName();
        String messageOwner = message.getMessageOwner();
        String resultMessage =  message.getResultMessage();

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
            TransactionInfo tx = TransactionInfo.now(messageName,messageOwner,resultMessage);
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

    @Override
    public BaseMessage<TransportJobValidationRequestBody> transportOrderValidationRequest(BaseMessage<TransportOrderRequestBody> message) {
        return null;
    }

    @Override
    public void transportJobValidationReply(BaseMessage<TransportJobValidationReplyBody> message) {

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
            powderExternalInterfaceService.reportH2trans(ifEventQueue);
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

    @Override
    public void orderAllocateRequest(BaseMessage<OrderAllocateRequestBody> message) {
        OrderAllocateRequestBody body = message.getBody();
        if (body == null || body.getId() == null) {
            log.warn("Invalid OrderAllocateRequest message body");
            return;
        }

        // 1. ProductionOrder 조회
        Optional<ProductionOrder> optionalOrder = productionOrderService.findById(body.getId());
        if (optionalOrder.isEmpty()) {
            log.error("ProductionOrder not found. ID: {}", body.getId());
            return;
        }
        ProductionOrder productionOrder = optionalOrder.get();

        // 2. ProductDef 조회 (ITEM_NAME으로 TOLERANCE_VAL 가져옴)
        Optional<ProductDef> optionalProductDef = productDefService.findByProductDefName(productionOrder.getItemName());
        BigDecimal toleranceVal = BigDecimal.ZERO;
        if (optionalProductDef.isPresent()) {
            ProductDef productDef = optionalProductDef.get();
            if (productDef.getToleranceVal() != null) {
                toleranceVal = productDef.getToleranceVal();
            }
        }

        // 3. 할당 가능한 LotCarrierMapping 목록 조회 (Inbound 시간순/생성순 정렬 데이터)
        List<LotCarrierMapping> availableMappings = lotCarrierMappingService.findByOrderIdAndOrderLineNumber(
                productionOrder.getOrderId(),
                productionOrder.getOrderLineNumber()
        );

        if (CollectionUtils.isEmpty(availableMappings)) {
            log.warn("No available LotCarrierMappings found for OrderId: {}, LineNo: {}", productionOrder.getOrderId(), productionOrder.getOrderLineNumber());
            return;
        }

        // 4. DP 서비스를 통한 캐리어 최적 조합 선택
        List<LotCarrierMapping> selectedMappings = carrierSelectionService.selectCarriers(
                availableMappings,
                productionOrder.getPlanQuantity(),
                toleranceVal
        );

        // 5. 선택된 캐리어 및 오더 상태 변경
        if (CollectionUtils.isNotEmpty(selectedMappings)) {
            TransactionInfo transactionInfo = TransactionInfo.now(EventName.ALLOCATE.getValue(), SystemName.MNG.getValue(), "Carrier Allocated by DP Knapsack");
            int seq = 0;
            for (LotCarrierMapping mapping : selectedMappings) {
                AllocatedCommand command =
                        AllocatedCommand
                                .builder()
                                .orderId(productionOrder.getOrderId())
                                .orderLineNumber(productionOrder.getOrderLineNumber())
                                .productionOrderId(productionOrder.getId())
                                .seq(seq++)
                                .productionStatus(ProductionStatus.ALLOCATED.getValue())
                                .build();
                mapping.allocated(command);
                mapping = lotCarrierMappingService.save(mapping);
                LotCarrierMappingHistoryEntity historyEntity = lotCarrierMappingMapper.toHistoryEntity(mapping);
                historyService.saveHistory(historyEntity);
            }

            // ProductionOrder 상태를 ALLOCATED로 변경
            Optional<ProductionOrder> optionalProductionOrder = productionOrderService.updateOrderState(transactionInfo,productionOrder.getId(), ProductionOrderState.ALLOCATE_COMPLETED.getValue());

            log.info("Successfully allocated {} carriers for ProductionOrder ID: {}", selectedMappings.size(), productionOrder.getId());
        } else {
            log.warn("Failed to allocate carriers for ProductionOrder ID: {}. Holding allocation.", productionOrder.getId());
            // 조합 실패 시 오더 상태 원복 또는 별도 에러 처리 진행
        }
    }
}
