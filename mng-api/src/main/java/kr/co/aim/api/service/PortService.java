package kr.co.aim.api.service;

import kr.co.aim.common.enums.*;
import kr.co.aim.common.format.*;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.*;
import kr.co.aim.domain.model.Carrier;
import kr.co.aim.domain.model.Port;
import kr.co.aim.domain.model.PortDef;
import kr.co.aim.domain.repository.*;
import kr.co.aim.infra.persistence.entity.CarrierHistoryEntity;
import kr.co.aim.infra.persistence.entity.PortDefHistoryEntity;
import kr.co.aim.infra.persistence.entity.PortHistoryEntity;
import kr.co.aim.infra.persistence.mapper.CarrierMapper;
import kr.co.aim.infra.persistence.mapper.PortDefMapper;
import kr.co.aim.infra.persistence.mapper.PortMapper;
import kr.co.aim.infra.persistence.mapper.TransportJobMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
public class PortService {

    private final HistoryService historyService;

    private final PortDefRepository portDefRepository;
    private final PortDefMapper portDefMapper;

    private final PortRepository portRepository;
    private final PortMapper portMapper;

    private final CarrierRepository carrierRepository;
    private final CarrierMapper carrierMapper;

    private final EquipmentRepository equipmentRepository;
    private final EquipmentDefRepository equipmentDefRepository;

    private final TransportJobRepository transportJobRepository;
    private final TransportJobMapper transportJobMapper;

    private final ProductionOrderRepository productionOrderRepository;

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
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public BaseMessage<CarrierDispatchRequestBody> loadRequest(BaseMessage<LoadRequestBody> message) {

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
        if(!StringUtils.equals(PortTransportState.READY_TO_LOAD.getValue(),port.getTransportState())){
            TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
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
        CarrierDispatchRequestBody body = CarrierDispatchRequestBody.builder()
                .equipmentName(equipmentName)
                .portName(portName)
                .build();
        reply.setMessageName(MessageList.CARRIER_DISPATCH_REQUEST.getMessageName());
        reply.setBody(body);

        return reply;
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
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void loadCompleted(BaseMessage<LoadCompletedBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

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

        Optional<Port> optionalPorts = portRepository.findByEquipmentNameAndPortName(equipmentName,portName);
        if(optionalPorts.isEmpty()){
            return;
        }
        Port port = optionalPorts.get();
        port.loadCompleted(command);
        port = portRepository.save(port);
        PortHistoryEntity portHistoryEntity = portMapper.toHistoryEntity(port);
        historyService.saveHistory(portHistoryEntity);

        if(StringUtils.isNotBlank(carrierName)){
            Optional<Carrier> optionalCarriers = carrierRepository.findByCarrierName(carrierName);
            if(optionalCarriers.isEmpty()){
                return;
            }

            Carrier carrier = optionalCarriers.get();
            carrier.loadCompleted(command);
            carrier = carrierRepository.save(carrier);
            CarrierHistoryEntity carrierHistoryEntity = carrierMapper.toHistoryEntity(carrier);
            historyService.saveHistory(carrierHistoryEntity);
        }

    }

    /**
     * unload가 완료 되었음을 보고합니다.
     * 비지니스 로직이 없음 단순히 log 찍음
     * @param message 받은 메시지
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void unLoadCompleted(BaseMessage<UnLoadCompletedBody> message) {
        log.info("Business Logic Nothing");
        log.info("equipmentName : {} ",message.getBody().getEquipmentName());
        log.info("portName : {} ",message.getBody().getPortName());
    }

    /**
     * port 의 접근모드 변경시 보고
     * @param message 받은 메시지
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void portTransportModeChanged(BaseMessage<PortTypeChangedBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String portType = message.getBody().getPortType();
        String portTransportModeName = message.getBody().getPortTransportMode();

        Optional<Port> optionalPorts = portRepository.findByEquipmentNameAndPortName(equipmentName,portName);

        if(optionalPorts.isEmpty()){
            return;
        }

        Port port = optionalPorts.get();
        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
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
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void portStateChanged(BaseMessage<PortStateChangedBody> message) {
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

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
        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
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
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
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
            Optional<Port> optionalPorts = portRepository.findByEquipmentNameAndPortName(equipmentName,portName);

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
            port = portRepository.save(port);
            PortHistoryEntity portHistoryEntity = portMapper.toHistoryEntity(port);
            historyService.saveHistory(portHistoryEntity);
        }
    }

    /**
     * port 의 타입 변경시 보고
     * @param message 받은 메시지
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
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

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        PortType portType = PortType.valueOf(portTypeName);
        PortTypeChangedCommand command = PortTypeChangedCommand
                .builder()
                .transactionInfo(tx)
                .portType(portType)
                .build();
        portDef.portTypeChanged(command);
        portDef = portDefRepository.save(portDef);
        PortDefHistoryEntity portDefHistoryEntity = portDefMapper.toHistoryEntity(portDef);
        historyService.saveHistory(portDefHistoryEntity);
    }

    /**
     * port 의 사용 타입 변경시 보고
     * @param message 받은 메시지
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void portUseTypeChanged(BaseMessage<PortUseTypeChangedBody> message) {
        log.info("Business Logic Nothing");
    }


}