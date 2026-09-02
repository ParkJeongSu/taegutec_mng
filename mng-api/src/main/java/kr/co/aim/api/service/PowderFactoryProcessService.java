package kr.co.aim.api.service;

import kr.co.aim.api.strategy.SelectStrategy;
import kr.co.aim.api.strategy.FactoryIfEventQueueStrategy;
import kr.co.aim.api.vo.powder.ops.PowderEventQueueReportVo;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.format.*;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.api.strategy.FactoryProcessStrategy;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.*;
import kr.co.aim.domain.model.*;
import kr.co.aim.domain.model.ProductionOrder;
import kr.co.aim.domain.repository.*;
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
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
@Profile({"pex","tex"})
public class PowderFactoryProcessService implements FactoryProcessStrategy {

    private final HistoryService historyService;
    private final WhatDispatchService whatDispatchService;

    private final PortMapper portMapper;
    private final PortDefRepository portDefRepository;
    private final PortRepository portRepository;

    private final CarrierRepository carrierRepository;
    private final CarrierMapper carrierMapper;

    private final EquipmentDefRepository equipmentDefRepository;
    private final DownloadService downloadService;

    private final FactoryIfEventQueueStrategy factoryIfEventQueueStrategy;
    private final TransportJobRepository transportJobRepository;
    private final TransportJobMapper transportJobMapper;
    private final ProductionOrderRepository productionOrderRepository;
    private final LotCarrierMappingRepository lotCarrierMappingRepository;
    private final LotCarrierMappingMapper lotCarrierMappingMapper;


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

        Optional<Port> optionalPorts =  portRepository.findByEquipmentNameAndPortName(equipmentName,portName);

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
        port = portRepository.save(port);
        PortHistoryEntity portHistoryEntity = portMapper.toHistoryEntity(port);
        historyService.saveHistory(portHistoryEntity);

        Optional<EquipmentDef> optionalEquipmentDef = equipmentDefRepository.findByEquipmentName(equipmentName);
        if(optionalEquipmentDef.isPresent()){
            EquipmentDef equipmentDef = optionalEquipmentDef.get();
            if(StringUtils.equals(EquipmentDetailType.MAGAZINE.getValue(),equipmentDef.getDetailEquipmentType())){
                // Magazine 설비에서는 무조건 8단으로 unload 됨
                Optional<Carrier> optionalCarrier = carrierRepository.findByCarrierName(carrierName);
                if(optionalCarrier.isPresent()){
                    Carrier carrier = optionalCarrier.get();
                    command = UnLoadRequestCommand.builder()
                            .transactionInfo(tx)
                            .carrierName(carrierName)
                            .equipmentName(equipmentName)
                            .carrierTransportState(CarrierTransportState.ON_PORT.getValue())
                            .quantity(new BigDecimal(8))
                            .portName(portName)
                            .build();
                    carrier.unloadRequest(command);
                    carrier = carrierRepository.save(carrier);
                    CarrierHistoryEntity carrierHistoryEntity = carrierMapper.toHistoryEntity(carrier);
                    historyService.saveHistory(carrierHistoryEntity);
                }
            }
        }

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
        String messageName = message.getMessageName();
        String messageOwner = message.getMessageOwner();
        String resultMessage =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String carrierName = message.getBody().getCarrierName();
        String portType = message.getBody().getPortType();
        String portTransportMode = message.getBody().getPortTransportMode();
        String transportJobName = message.getBody().getTransportJobName();
        String actualWeight = message.getBody().getActualWeight();

        TransactionInfo tx = TransactionInfo.now(messageName,messageOwner,resultMessage);
        UnLoadCompletedCommand command = UnLoadCompletedCommand.builder()
                .transactionInfo(tx)
                .carrierName(carrierName)
                .equipmentName(equipmentName)
                .portName(portName)
                .build();
        Optional<PortDef> optionalPortDef = portDefRepository.findByEquipmentNameAndPortName(equipmentName, portName);
        if(optionalPortDef.isEmpty()){
            return;
        }
        PortDef portDef = optionalPortDef.get();
        String actualLocationId = portDef.getLocationId();
        Optional<Port> optionalPorts = portRepository.findByEquipmentNameAndPortName(equipmentName,portName);
        if(optionalPorts.isEmpty()){
            return;
        }
        Port port = optionalPorts.get();
        port.unloadCompleted(command);
        port = portRepository.save(port);
        PortHistoryEntity portHistoryEntity = portMapper.toHistoryEntity(port);
        historyService.saveHistory(portHistoryEntity);
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

        Optional<PortDef> optionalPortDef = portDefRepository.findByEquipmentNameAndPortName(equipmentName,portName);
        if(optionalPortDef.isEmpty()){
            return null;
        }
        PortDef portDef = optionalPortDef.get();

        Optional<Port> optionalPorts = portRepository.findByEquipmentNameAndPortName(equipmentName,portName);
        if(optionalPorts.isEmpty()){
            return null;
        }
        Port port = optionalPorts.get();
        port.loadCompleted(command);
        port = portRepository.save(port);
        PortHistoryEntity portHistoryEntity = portMapper.toHistoryEntity(port);
        historyService.saveHistory(portHistoryEntity);

        Optional<Carrier> optionalCarriers = carrierRepository.findByCarrierName(carrierName);
        if(optionalCarriers.isEmpty()){
            return null;
        }

        Carrier carrier = optionalCarriers.get();
        carrier.loadCompleted(command);
        carrier = carrierRepository.save(carrier);
        CarrierHistoryEntity carrierHistoryEntity = carrierMapper.toHistoryEntity(carrier);
        historyService.saveHistory(carrierHistoryEntity);

        Optional<LotCarrierMapping> optionalLotCarrierMapping = lotCarrierMappingRepository.findByCarrierName(carrierName);
        if(optionalLotCarrierMapping.isPresent()){
            lotCarrierMapping = optionalLotCarrierMapping.get();
            lotCarrierMapping.loadCompleted(command);
            lotCarrierMapping = lotCarrierMappingRepository.save(lotCarrierMapping);
            LotCarrierMappingHistoryEntity historyEntity = lotCarrierMappingMapper.toHistoryEntity(lotCarrierMapping);
            historyService.saveHistory(historyEntity);
        }

        return downloadService.downloadRequest(tx,message);
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

        Optional<Carrier> optionalCarriers = carrierRepository.findByCarrierName(carrierName);
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
        carrier = carrierRepository.save(carrier);
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
        Optional<TransportJob> optionalTransportJob = transportJobRepository.findByTransportJobName(transportJobName);
        if(optionalTransportJob.isPresent()){
            TransportJob transportJob = optionalTransportJob.get();
            TransportJobUpdateCommand command =
                    TransportJobUpdateCommand
                            .builder()
                            .transportJobState(TransportJobState.CANCELLED.getValue())
                            .transactionInfo(tx)
                            .build();
            transportJob.changeTransportJob(command);
            transportJob = transportJobRepository.save(transportJob);
            TransportJobHistoryEntity transportJobHistoryEntity = transportJobMapper.toHistoryEntity(transportJob);
            historyService.saveHistory(transportJobHistoryEntity);
            Optional<Port> optionalPort = portRepository.findByEquipmentNameAndPortName(currentEquipmentName,currentPortName);
            Optional<PortDef> optionalPortDef = portDefRepository.findByEquipmentNameAndPortName(currentEquipmentName,currentPortName);

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

        Optional<TransportJob> optionalTransportJob = transportJobRepository.findByTransportJobName(transportJobName);
        if(optionalTransportJob.isPresent()){
            TransportJob transportJob = optionalTransportJob.get();
            TransportJobUpdateCommand command =
                    TransportJobUpdateCommand
                            .builder()
                            .transportJobState(TransportJobState.COMPLETED.getValue())
                            .transactionInfo(tx)
                            .build();
            transportJob.changeTransportJob(command);

            transportJob = transportJobRepository.save(transportJob);
            TransportJobHistoryEntity transportJobHistoryEntity = transportJobMapper.toHistoryEntity(transportJob);
            historyService.saveHistory(transportJobHistoryEntity);

            Long productionOrderId = null;
            if(ObjectUtils.isNotEmpty(transportJob.getOrderId())){
                productionOrderId = Long.parseLong(transportJob.getOrderId());
                Optional<ProductionOrder> optionalProductionOrder = productionOrderRepository.findById( productionOrderId );
                if(optionalProductionOrder.isPresent()){
                    ProductionOrder productionOrder = optionalProductionOrder.get();
                    // powder EventQueue
                    try{
                        PowderEventQueueReportVo powderEventQueueReportVo
                                = PowderEventQueueReportVo
                                .builder()
                                .messageName(messageName)
                                .productionOrder(productionOrder)
                                .carrierName(carrierName)
                                .tx(tx)
                                .build();
                        factoryIfEventQueueStrategy.enqueueIfEventQueue(powderEventQueueReportVo);
                    }
                    catch(Exception e){
                        log.error("EventQueue enqueue error",e);
                    }
                }
            }
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
        Optional<TransportJob> optionalTransportJob = transportJobRepository.findByTransportJobName(transportJobName);

        if(optionalTransportJob.isPresent()){
            TransportJob transportJob = optionalTransportJob.get();
            TransportJobUpdateCommand command =
                    TransportJobUpdateCommand
                            .builder()
                            .transportJobState(TransportJobState.ACCEPTED.getValue())
                            .transactionInfo(tx)
                            .build();
            transportJob.changeTransportJob(command);
            transportJob = transportJobRepository.save(transportJob);
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

        Optional<TransportJob> optionalTransportJob = transportJobRepository.findByTransportJobName(transportJobName);
        if(optionalTransportJob.isPresent()){
            TransportJob transportJob = optionalTransportJob.get();
            TransportJobUpdateCommand command =
                    TransportJobUpdateCommand
                            .builder()
                            .transportJobState(TransportJobState.STARTED.getValue())
                            .transactionInfo(tx)
                            .build();
            transportJob.changeTransportJob(command);

            transportJob = transportJobRepository.save(transportJob);
            TransportJobHistoryEntity transportJobHistoryEntity = transportJobMapper.toHistoryEntity(transportJob);
            historyService.saveHistory(transportJobHistoryEntity);

            Long productionOrderId = null;
            if(ObjectUtils.isNotEmpty(transportJob.getOrderId())){
                productionOrderId = Long.parseLong(transportJob.getOrderId());
                Optional<ProductionOrder> optionalProductionOrder = productionOrderRepository.findById( productionOrderId );
                if(optionalProductionOrder.isPresent()){
                    ProductionOrder productionOrder = optionalProductionOrder.get();
                    // powder EventQueue
                    try{
                        PowderEventQueueReportVo powderEventQueueReportVo
                                = PowderEventQueueReportVo
                                .builder()
                                .messageName(messageName)
                                .productionOrder(productionOrder)
                                .carrierName(carrierName)
                                .tx(tx)
                                .build();
                        factoryIfEventQueueStrategy.enqueueIfEventQueue(powderEventQueueReportVo);
                    }
                    catch(Exception e){
                        log.error("EventQueue enqueue error",e);
                    }
                }
            }
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
                portRepository.findWithLockByEquipmentNameAndPortName(equipmentName,portName);

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
            port = portRepository.save(port);
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
}
