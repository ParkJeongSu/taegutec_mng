package kr.co.aim.api.service;

import kr.co.aim.api.vo.carrier.CarrierDispatchRequestVo;
import kr.co.aim.api.vo.carrier.CarrierSelectionResult;
import kr.co.aim.api.vo.insert.ops.InsertEventQueueReportVo;
import kr.co.aim.api.vo.insert.ops.TransportCancelReasonVo;
import kr.co.aim.api.vo.port.TransportStateChangedVo;
import kr.co.aim.api.vo.transportJob.CreateTransportJobVo;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.format.*;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.api.strategy.FactoryProcessStrategy;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.*;
import kr.co.aim.domain.model.*;
import kr.co.aim.domain.repository.*;
import kr.co.aim.infra.persistence.entity.CarrierHistoryEntity;
import kr.co.aim.infra.persistence.entity.PortHistoryEntity;
import kr.co.aim.infra.persistence.entity.TransportJobHistoryEntity;
import kr.co.aim.infra.persistence.mapper.CarrierMapper;
import kr.co.aim.infra.persistence.mapper.PortMapper;
import kr.co.aim.infra.persistence.mapper.ProductionOrderMapper;
import kr.co.aim.infra.persistence.mapper.TransportJobMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

    private final PortMapper portMapper;
    private final PortService portService;

    private final CarrierService carrierService;
    private final CarrierMapper carrierMapper;

    private final EquipmentService equipmentService;

    private final TransportJobService  transportJobService;
    private final TransportJobMapper transportJobMapper;

    private final ProductionOrderRepository productionOrderRepository;
    private final ProductionOrderMapper productionOrderMapper;

    @Override
    @Transactional(value = "mssqlTransactionManager")
    public BaseMessage<TransportJobRequestBody> carrierDispatchRequest(BaseMessage<CarrierDispatchRequestBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String carrierName = message.getBody().getCarrierName();
        String portType = message.getBody().getPortType();
        String portTransportMode = message.getBody().getPortTransportMode();

        Optional<PortDef> optionalPortDef = portService.findPortDefByEquipmentNameAndPortName(equipmentName,portName);
        Optional<Port> optionalPorts =  portService.findWithLockByEquipmentNameAndPortName(equipmentName,portName);
        Optional<Equipment> optionalEquipments = equipmentService.findEquipmentByEquipmentName(equipmentName);
        Optional<EquipmentDef> optionalEquipmentDef = equipmentService.findEquipmentDefByEquipmentName(equipmentName);

        if(optionalPortDef.isEmpty()){
            log.error("Not Exists PortDef [ equipmentName : {} , portName {} ]",equipmentName,portName);
            return null;
        }
        if(optionalPorts.isEmpty()){
            log.error("Not Exists Ports [ equipmentName : {} , portName {} ]",equipmentName,portName);
            return null;
        }
        if(optionalEquipments.isEmpty()){
            log.error("Not Exists Equipments [ equipmentName : {} ]",equipmentName);
            return null;
        }
        if(optionalEquipmentDef.isEmpty()){
            log.error("Not Exists Equipments [ equipmentName : {} ]",equipmentName);
            return null;
        }

        Port port = optionalPorts.get();
        PortDef portDef = optionalPortDef.get();
        EquipmentDef equipmentDef = optionalEquipmentDef.get();
        Equipment equipment = optionalEquipments.get();
        List<CarrierSelectionResult> dispatchCarrierList = null;

        BaseMessage<TransportJobRequestBody> reply = null;
        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);

        CarrierDispatchRequestVo carrierDispatchRequestVo =
                CarrierDispatchRequestVo
                        .builder()
                        .equipment(equipment)
                        .equipmentDef(equipmentDef)
                        .portDef(portDef)
                        .port(port)
                        .build();

        // Validation TransportJob exists and transportJob State
        List<TransportJob> avtiveTransportJobList = transportJobService.findActiveTransportJobs(equipmentName,portName);;

        if(avtiveTransportJobList.isEmpty()){
            if(PortType.INPUT.getValue().equals(portDef.getPortType())){
                dispatchCarrierList = carrierService.selectCarrierByInputPort(carrierDispatchRequestVo);
            }
            else if(PortType.OUTPUT.getValue().equals(portDef.getPortType())){
                dispatchCarrierList = carrierService.selectCarrierByOutputPort(carrierDispatchRequestVo);
            }
            // TODO: mix equipment 보내는 로직 보류

            if(CollectionUtils.isNotEmpty(dispatchCarrierList)){
                Carrier carrier =  dispatchCarrierList.get(0).getCarrier();
                String orderId = dispatchCarrierList.get(0).getOrderId();
                String orderLineNumber = dispatchCarrierList.get(0).getOrderLineNumber();
                TransportJobCreateCommand command =
                        TransportJobCreateCommand.builder()
                                .transportJobName(carrier.getCarrierName() + tx.eventTime().toString().substring(0,12))
                                .carrierName(carrier.getCarrierName())
                                .sourceEquipmentName(carrier.getEquipmentName())
                                .sourcePortName(carrier.getPortName())
                                .sourceZoneName(carrier.getZoneName())
                                .sourcePositionTypeName(carrier.getPositionTypeName())
                                .sourcePositionName(carrier.getPositionName())
                                .destinationEquipmentName(equipment.getEquipmentName())
                                .destinationPortName(port.getPortName())
                                .destinationZoneName("")
                                .destinationPositionTypeName("")
                                .destinationPositionName("")
                                .createTime(tx.eventTime())
                                .requestSource(TransportJobRequestType.EQP.getValue())
                                .orderId( orderId )
                                .transactionInfo(tx)
                                .build();

                TransportJob transportJob = transportJobService.createTransportJob(command);

                reply = new BaseMessage<>();

                reply.setMessageName(MessageList.TRANSPORT_JOB_REQUEST.getMessageName());

                reply.setTransactionId(message.getTransactionId());
                reply.setMessageFrom(SystemName.MNG.getValue());
                reply.setMessageOwner(SystemName.MNG.getValue());
                reply.setMessageTo(SystemName.WCS.getValue());
                reply.setEventTime(message.getEventTime());
                reply.setResultMessage("");
                reply.setResultCode(ResultCode.OK.getValue());

                TransportJobRequestBody body = transportJobService.createTransportJobMessage(transportJob);
                reply.setBody(body);
            }
        }

        TransportStateChangedVo vo =
                TransportStateChangedVo
                        .builder()
                        .port(port)
                        .portTransportState(PortTransportState.RESERVED_TO_LOAD)
                        .tx(tx)
                        .build();
        portService.transportStateChanged(vo);

        return reply;
    }

    @Override
    @Transactional(value = "mssqlTransactionManager")
    public BaseMessage<DestinationDispatchRequestBody> unLoadRequest(BaseMessage<UnLoadRequestBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

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

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
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
    public BaseMessage<CarrierValidationReplyBody> loadCompleted(BaseMessage<LoadCompletedBody> message) {
        // TODO: connectedEQP, connectedPort 가 있으면 해당 port에도 carrier 이름 적용
        // TODO: portdef 에 roleType 에 따라서 EAS 면 CarrierValidationReply 반환
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();
        String fromSystemName = message.getMessageFrom();

        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String carrierName = message.getBody().getCarrierName();
        String portType = message.getBody().getPortType();
        String portTransportMode = message.getBody().getPortTransportMode();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        LoadCompletedCommand command = LoadCompletedCommand.builder()
                .transactionInfo(tx)
                .carrierTransportState(CarrierTransportState.ON_PORT.getValue())
                .carrierName(carrierName)
                .equipmentName(equipmentName)
                .portName(portName)
                .build();

        Optional<Port> optionalPorts = portService.findPortByEquipmentNameAndPortName(equipmentName,portName);
        if(optionalPorts.isEmpty()){
            return null;
        }
        Port port = optionalPorts.get();
        port.loadCompleted(command);
        port = portService.save(port);
        PortHistoryEntity portHistoryEntity = portMapper.toHistoryEntity(port);
        historyService.saveHistory(portHistoryEntity);

        if(StringUtils.isNotBlank(carrierName)){
            Optional<Carrier> optionalCarriers = carrierService.findByCarrierName(carrierName);
            if(optionalCarriers.isEmpty()){
                return null;
            }

            Carrier carrier = optionalCarriers.get();
            carrier.loadCompleted(command);
            carrier = carrierService.save(carrier);
            CarrierHistoryEntity carrierHistoryEntity = carrierMapper.toHistoryEntity(carrier);
            historyService.saveHistory(carrierHistoryEntity);

            // TODO : 만일 systemName == WCS && connectedEQP & connectedPort 가 존재하는 경우
            // TODO : connectedEQP 와 connectedPort 로 port 조회후 carrier 이름 save
        }

        // TODO: SystemName == EAS CarrierValidationReply 메시지 반환
        // 현재 조회한 port에서 CarrierName 을 가져와서 Carrier ValidationReply 메시지 생성 후 반환
        return null;
    }

    @Override
    @Transactional(value = "mssqlTransactionManager")
    public void carrierLocationChanged(BaseMessage<CarrierLocationChangedBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

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
            Optional<PortDef> optionalPortDef = portService.findPortDefByEquipmentNameAndPortName(currentEquipmentName,currentPortName);

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

    @Override
    @Transactional(value = "mssqlTransactionManager")
    public void transportJobReply(BaseMessage<TransportJobReplyBody> message) {
        String messageName = message.getMessageName();
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);

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
        String carrierName = message.getBody().getCarrierName();
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String transportJobName = message.getBody().getTransportJobName();
        String requestSource = message.getBody().getRequestSource();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);

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
}
