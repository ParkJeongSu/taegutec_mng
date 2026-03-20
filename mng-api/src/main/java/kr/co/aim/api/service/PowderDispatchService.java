package kr.co.aim.api.service;

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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

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

    private final CarrierRepository carrierRepository;
    private final CarrierMapper carrierMapper;

    private final EquipmentRepository equipmentRepository;
    private final EquipmentDefRepository equipmentDefRepository;

    private final TransportJobRepository transportJobRepository;
    private final TransportJobMapper transportJobMapper;

    private final ProductionOrderRepository productionOrderRepository;
    private final ProductionOrderMapper productionOrderMapper;
    @Override
    public BaseMessage<TransportJobRequestBody> requestDispatch(BaseMessage<CarrierDispatchRequestBody> message) {
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
        Carrier carrier = null;

        BaseMessage<TransportJobRequestBody> reply = null;
        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);

        // TODO: 해당 Port와 Equipment 로 보내는 반송 job이 있는지 체크, job 이 있다면 Port의 상태를 ReservedToLoad로 변경 후 종료
        // TODO: 없으면 아래 로직 수행
        List<String> transportJobStateList = new ArrayList<>();
        transportJobStateList.add(TransportJobState.REQUESTED.getValue());
        transportJobStateList.add(TransportJobState.ACCEPTED.getValue());
        transportJobStateList.add(TransportJobState.STARTED.getValue());
        // Validation TransportJob exists and transportJob State
        List<TransportJob> transportJobList = transportJobRepository.findByDestinationEquipmentNameAndDestinationPortNameAndTransportJobStateIn(
                equipmentName,
                portName,
                transportJobStateList
        );

        if(transportJobList.isEmpty()){
            // 현재 반송중인 job이 없을 경우
            // 방어 로직

            // TODO : Input Port 와 Output Port
            // Input Port :
            // (1) 설비에서 Production Order Select
            // (2) 존재하면, 해당 order Select
            // (3) 존재하지 않으면, 설비명으로 신규 Production Order Select
            // (4) Order 에서 가장 우선순위가 높은 Carrier Select
            // Output Port :
            // (1) EquipmentDef 에서 ContainerType을 Select
            // (2) ContainerType None 이거나 위에서 찾은 type으로 가장 우선 순위가 높은 Carrier 찾기

            if(PortType.INPUT.getValue().equals(portDef.getPortType())){
                // input 포트는 production_order_Job을 토대로 full container 를 보냄
                Long productionOrderId = equipment.getProductionOrderId();
                List<String> productionOrderStateList = new ArrayList<>();
                productionOrderStateList.add(ProductionOrderState.REQUESTED.getValue());
                productionOrderStateList.add(ProductionOrderState.RELEASED.getValue());
                ProductionOrder productionOrder = null;
                List<ProductionOrder> productionOrderList = productionOrderRepository.findByEquipmentNameAndProductionOrderStateInOrderByCreateTimeAsc(
                        equipment.getEquipmentName(),
                        productionOrderStateList
                );
                if(productionOrderList.isEmpty()){
                    productionOrderStateList = new ArrayList<>();
                    productionOrderStateList.add(ProductionOrderState.CREATED.getValue());
                    productionOrderList = productionOrderRepository.findByEquipmentNameAndProductionOrderStateInOrderByCreateTimeAsc(
                            equipment.getEquipmentName(),
                            productionOrderStateList
                    );
                    if(productionOrderList.isEmpty()){
                        return null;
                    }
                    else{
                        productionOrder =  productionOrderList.get(0);
                    }
                }else{
                    productionOrder =  productionOrderList.get(0);
                }
                List<Carrier> carriers = carrierRepository.findCarriersForFullContainer(
                        CarrierCleanState.CLEAN.getValue(),
                        CarrierTransportState.IN_WAREHOUSE.getValue(),
                        "",
                        CarrierUseState.IN_USE.getValue(),
                        productionOrder.getOrderId(),
                        productionOrder.getOrderLineNumber()
                );
                carrier = carriers.get(0);
            }
            else if(PortType.OUTPUT.getValue().equals(portDef.getPortType())){
                List<String> containerTypes = new ArrayList<>();
                containerTypes.add(ContainerType.NONE.getValue());
                containerTypes.add(equipmentDef.getContainerType());
                List<Carrier> carriers = carrierRepository.findCarriersForEmptyContainer(
                        CarrierCleanState.CLEAN.getValue(),
                        CarrierTransportState.IN_WAREHOUSE.getValue(),
                        "",
                        CarrierUseState.AVAILABLE.getValue(),
                        0,
                        containerTypes
                );
                if(carriers.isEmpty()){
                    return null;
                }
                else{
                    carrier = carriers.get(0);
                }
            }
            // TODO: JOB 정보 추가하기
            String transportJobName = "";
            TransportJobCreateCommand command =
                    TransportJobCreateCommand.builder()
                            .transportJobName(transportJobName)
                            .carrierName(carrier.getCarrierName())
                            .transactionInfo(tx)
                            .build();

            TransportJob transportJob = TransportJob.create(command);
            transportJob = transportJobRepository.save(transportJob);
            TransportJobHistoryEntity transportJobHistoryEntity = transportJobMapper.toHistoryEntity(transportJob);
            historyService.saveHistory(transportJobHistoryEntity);

            reply = new BaseMessage<>();
            TransportJobRequestBody body = TransportJobRequestBody.builder()
                    // TODO : TransportJob 에서 정보 가져와서 WCS로 보낼 정보 만들기
                    .sourceEquipmentName(transportJob.getSourceEquipmentName())
                    .destinationEquipmentName(transportJob.getDestinationEquipmentName())
                    .carrierName(transportJob.getCarrierName())
                    .build();
            reply.setMessageName(MessageList.TRANSPORT_JOB_REQUEST.getMessageName());
            reply.setBody(body);


            PortTransportStateChangedCommand portCommand =
                    PortTransportStateChangedCommand
                            .builder()
                            .transactionInfo(tx)
                            .portTransportStateName(PortTransportState.RESERVED_TO_LOAD.getValue())
                            .build();
            port.transportStateChanged(portCommand);
            port = portRepository.save(port);
            PortHistoryEntity portHistoryEntity = portMapper.toHistoryEntity(port);
            historyService.saveHistory(portHistoryEntity);
        }
        else{
            PortTransportStateChangedCommand portCommand =
                    PortTransportStateChangedCommand
                            .builder()
                            .transactionInfo(tx)
                            .portTransportStateName(PortTransportState.RESERVED_TO_LOAD.getValue())
                            .build();
            port.transportStateChanged(portCommand);
            port = portRepository.save(port);
            PortHistoryEntity portHistoryEntity = portMapper.toHistoryEntity(port);
            historyService.saveHistory(portHistoryEntity);
        }

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
}
