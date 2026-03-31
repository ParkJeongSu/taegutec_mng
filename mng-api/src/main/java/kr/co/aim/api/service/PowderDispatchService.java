package kr.co.aim.api.service;

import kr.co.aim.api.vo.carrier.CarrierDispatchRequestVo;
import kr.co.aim.api.vo.carrier.CarrierSelectionResult;
import kr.co.aim.api.vo.port.TransportStateChangedVo;
import kr.co.aim.api.vo.transportJob.CreateTransportJobVo;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.format.*;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.handler.DispatchStrategy;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.PortTransportStateChangedCommand;
import kr.co.aim.domain.command.TransportJobCreateCommand;
import kr.co.aim.domain.command.UnLoadRequestCommand;
import kr.co.aim.domain.model.*;
import kr.co.aim.domain.repository.*;
import kr.co.aim.infra.persistence.entity.PortHistoryEntity;
import kr.co.aim.infra.persistence.entity.TransportJobHistoryEntity;
import kr.co.aim.infra.persistence.mapper.CarrierMapper;
import kr.co.aim.infra.persistence.mapper.PortMapper;
import kr.co.aim.infra.persistence.mapper.ProductionOrderMapper;
import kr.co.aim.infra.persistence.mapper.TransportJobMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public class PowderDispatchService implements DispatchStrategy {

    private final HistoryService historyService;
    private final PortDefRepository portDefRepository;

    private final PortRepository portRepository;
    private final PortMapper portMapper;
    private final PortService portService;

    private final CarrierService carrierService;
    private final CarrierRepository carrierRepository;
    private final CarrierMapper carrierMapper;

    private final EquipmentRepository equipmentRepository;
    private final EquipmentDefRepository equipmentDefRepository;

    private final TransportJobRepository transportJobRepository;
    private final TransportJobMapper transportJobMapper;
    private final TransportJobService  transportJobService;

    private final ProductionOrderRepository productionOrderRepository;
    private final ProductionOrderMapper productionOrderMapper;

    @Override
    public BaseMessage<TransportJobRequestListBody> carrierDispatchRequest(BaseMessage<CarrierDispatchRequestBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String carrierName = message.getBody().getCarrierName();
        String portType = message.getBody().getPortType();
        String portTransportMode = message.getBody().getPortTransportMode();

        Optional<PortDef> optionalPortDef = portDefRepository.findByEquipmentNameAndPortName(equipmentName,portName);
        Optional<Port> optionalPorts =  portRepository.findWithLockByEquipmentNameAndPortName(equipmentName,portName);
        Optional<Equipment> optionalEquipments = equipmentRepository.findByEquipmentName(equipmentName);
        Optional<EquipmentDef> optionalEquipmentDef = equipmentDefRepository.findByEquipmentDefName(equipmentName);

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

        BaseMessage<TransportJobRequestListBody> reply = null;
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
            List<TransportJob> newTransportJobList;
            if(PortType.INPUT.getValue().equals(portDef.getPortType())){
                dispatchCarrierList = carrierService.selectCarrierByInputPort(carrierDispatchRequestVo);
            }
            else if(PortType.OUTPUT.getValue().equals(portDef.getPortType())){
                dispatchCarrierList = carrierService.selectCarrierByOutputPort(carrierDispatchRequestVo);
            }

            if(CollectionUtils.isNotEmpty(dispatchCarrierList)){

                List<TransportJobCreateCommand> commandList = new ArrayList<>();

                for(CarrierSelectionResult selectionResult : dispatchCarrierList){
                    Carrier carrier =  selectionResult.getCarrier();
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
                                    .requestType(TransportJobRequestType.EQP.getValue())
                                    .orderId( selectionResult.getOrderId() )
                                    .transactionInfo(tx)
                                    .build();
                    commandList.add(command);
                }
                CreateTransportJobVo vo = CreateTransportJobVo
                        .builder()
                        .transportJobCreateCommandList(commandList)
                        .build();
                newTransportJobList = transportJobService.createTransportJob(vo);
                reply = new BaseMessage<>();
                TransportJobRequestListBody body = transportJobService.createTransportJobMessage(newTransportJobList);
                reply.setMessageName(MessageList.TRANSPORT_JOB_REQUEST.getMessageName());
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
    public BaseMessage<DestinationDispatchRequestBody> unLoadRequest(BaseMessage<UnLoadRequestBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

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

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
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

        BaseMessage<DestinationDispatchRequestBody> reply = new BaseMessage<>();
        DestinationDispatchRequestBody body = DestinationDispatchRequestBody.builder().equipmentName(equipmentName).portName(portName).carrierName(carrierName).portType(portType).portTransportMode(portTransportMode).build();
        reply.setMessageName(MessageList.DESTINATION_DISPATCH_REQUEST.getMessageName());
        reply.setBody(body);

        return reply;
    }

    @Override
    public BaseMessage<TransportJobRequestListBody> transportOrderRequest(BaseMessage<TransportOrderRequestBody> message) {
        return null;
    }
}
