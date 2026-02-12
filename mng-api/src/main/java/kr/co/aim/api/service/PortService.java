package kr.co.aim.api.service;

import kr.co.aim.common.dto.*;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.error.EntityExistException;
import kr.co.aim.common.error.EntityNotFoundException;
import kr.co.aim.common.format.*;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.*;
import kr.co.aim.domain.model.*;
import kr.co.aim.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
public class PortService {

    private final PortDefRepository portDefRepository;
    private final PortsRepository portsRepository;
    private final CarriersRepository carriersRepository;
    private final TaskJobDetailRepository taskJobDetailRepository;
    private final TaskJobRepository taskJobRepository;
    private final TransportJobService transportJobService;

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
    public BaseMessage<LoadRequestTEXBody> loadRequest(BaseMessage<LoadRequestBody> message) {

        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String carrierName = message.getBody().getCarrierName();
        String portType = message.getBody().getPortType();
        String portTransportMode = message.getBody().getPortTransportMode();

        Optional<Ports> optionalPorts =  portsRepository.findByEquipmentNameAndPortName(equipmentName,portName);

        if(optionalPorts.isEmpty()){
            return null;
        }

        Ports port = optionalPorts.get();
        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        LoadRequestCommand command = LoadRequestCommand.builder().transactionInfo(tx).build();
        port.loadRequest(command);
        portsRepository.save(port);

        BaseMessage<LoadRequestTEXBody> reply = new BaseMessage<>();
        LoadRequestTEXBody body = LoadRequestTEXBody.builder()
                .equipmentName(equipmentName)
                .portName(portName)
                .build();
        reply.setMessageName(MessageList.LOAD_REQUEST_TEX.getMessageName());
        reply.setBody(body);

        return reply;
    }
    /**
     * 포트의 새로운 캐리어를 요청합니다.
     * 1. 설비명으로 TaskJob Find
     *
     * 2. TaskJob 반환
     *
     * @param message 받은 메시지
     * @return TEX 로 보낼 메시지 객체
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public BaseMessage<TransportJobRequestBody> loadRequestTEX(BaseMessage<LoadRequestTEXBody> message) {

        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();

        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String carrierName = message.getBody().getCarrierName();
        String portType = message.getBody().getPortType();
        String portTransportMode = message.getBody().getPortTransportMode();
        // TODO: 아래의 PORT List 조회는 비관적락으로 변경 ex) select * from ports for update
        Optional<Ports> optionalPorts =  portsRepository.findByEquipmentNameAndPortName(equipmentName,portName);
        Optional<PortDef> optionalPortDef = portDefRepository.findByEquipmentNameAndPortName(equipmentName,portName);
        if(optionalPorts.isEmpty()){
            return null;
        }
        if(optionalPortDef.isEmpty()){
            return null;
        }
        
        // TODO: 해당 Port와 Equipment 로 보내는 반송 job이 있는지 체크, job 이 있다면 종료. 없으면 아래 로직 수행
        // Validation TransportJop exists and transportJob State


        Ports port = optionalPorts.get();
        PortDef portDef = optionalPortDef.get();
        // TODO : Input port 와 output port 의 로직이 조금 변경되야할듯

        if(PortType.INPUT.getValue().equals(portDef.getPortType())){
            // input 포트는 taskJob을 토대로 full container 를 보냄
        }
        else if(PortType.OUTPUT.getValue().equals(portDef.getPortType())){
            // output 포트는 empty container 를 보내야하는데, 이건 아직 미정
        }

        // TaskJob, TaskJobDetail 조회 후 데이터 존재한다면
        // BaseMessage<TransportJobRequestBody> reply 반환
        Optional<TaskJob> optionalTaskJob = taskJobRepository.findByEquipmentName(equipmentName); // TODO: state 부분 추가
        if(optionalTaskJob.isEmpty()){
            return null;
        }
        TaskJob taskJob = optionalTaskJob.get();
        List<TaskJobDetail> taskJobDetailList = taskJobDetailRepository.findAll();
        // TODO: 현재는 findAll 의 List를 가져왔지만, 최종목적지와 taskJobId, State 를 통해서 List를 가져와서 메시지 생성

        if(taskJobDetailList.isEmpty()){
            return null;
        }
        TaskJobDetail taskJobDetail = taskJobDetailList.get(0);

        LocalDateTime currentDateTime = LocalDateTime.now();
        // TODO : Create TransportJob
        TransportJobCreateRequestDto transportJobCreateRequestDto =
                TransportJobCreateRequestDto.builder()
                        .transportJobName(currentDateTime.toString() +"_"+ taskJobDetail.getCarrierName())
                        .eventTime(currentDateTime)
                        .requestType(MNGProcessName.TEX.getValue())
                        .carrierName(taskJobDetail.getCarrierName())
                        .destinationEquipmentName(taskJob.getEquipmentName())
                        .destinationPortName(port.getPortName())
                        .taskJobId(taskJob.getId())
                        .eventName(EventName.CREATED.getValue())
                        .eventUser(MNGProcessName.TEX.getValue())
                        .transportJobState(TransportJobState.REQUESTED.getValue())
                        .createTime(currentDateTime)
                        .build();
        TransportJob transportJob = transportJobService.createTransportJob(transportJobCreateRequestDto);


        BaseMessage<TransportJobRequestBody> reply = new BaseMessage<>();
        TransportJobRequestBody body = TransportJobRequestBody.builder()
                // TODO : 아래 부분 좀더 확인 후 수정
                .sourceEquipmentName(transportJob.getSourceEquipmentName())
                .destinationEquipmentName(transportJob.getDestinationEquipmentName())
                .carrierName(transportJob.getCarrierName())
                .build();
        reply.setMessageName(MessageList.TRANSPORT_JOB_REQUEST.getMessageName());
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


        Optional<Ports> optionalPorts = portsRepository.findByEquipmentNameAndPortName(equipmentName,portName);
        if(optionalPorts.isEmpty()){
            return;
        }
        Optional<Carriers> optionalCarriers = carriersRepository.findByCarrierName(carrierName);
        if(optionalCarriers.isEmpty()){
            return;
        }

        Ports port = optionalPorts.get();
        Carriers carrier = optionalCarriers.get();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        // TODO: CarrierTransportState 를 도착한 설비의 type을 보고 판단 고민
        LoadCompletedCommand command = LoadCompletedCommand.builder()
                .transactionInfo(tx)
                .carrierTransportState(CarrierTransportState.ON_PORT.getValue())
                .carrierName(carrierName)
                .equipmentName(equipmentName)
                .portName(portName)
                .build();
        port.loadCompleted(command);
        carrier.loadCompleted(command);

        carriersRepository.save(carrier);
        portsRepository.save(port);

        // TODO: Add History Carrier, Ports

    }

    /**
     * 포트 위의 Carrier 를 Unload 요청합니다.
     * 1. Carrier 의 위치정보를 port 로 변경합니다.
     * 2. Carrier 정보와 port 정보를 WhereNext로 메시지를 반환
     *
     * @param message 받은 메시지
     * @return RTD 로 보낼 메시지 객체
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public BaseMessage<WhereNextBody> unLoadRequest(BaseMessage<UnLoadRequestBody> message) {

        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();
        

        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String carrierName = message.getBody().getCarrierName();
        String portType = message.getBody().getPortType();
        String portTransportMode = message.getBody().getPortTransportMode();

        Optional<Ports> optionalPorts =  portsRepository.findByEquipmentNameAndPortName(equipmentName,portName);

        if(optionalPorts.isEmpty()){
            return null;
        }

        Optional<Carriers> optionalCarriers = carriersRepository.findByCarrierName(carrierName);
        if(optionalCarriers.isEmpty()){
            return null;
        }

        Ports port = optionalPorts.get();
        Carriers carrier = optionalCarriers.get();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        UnLoadRequestCommand command = UnLoadRequestCommand.builder()
                .transactionInfo(tx)
                .carrierName(carrierName)
                .equipmentName(equipmentName)
                .portName(portName)
                .build();

        port.unloadRequest(command);
        carrier.unloadRequest(command);

        carriersRepository.save(carrier);
        portsRepository.save(port);

        BaseMessage<WhereNextBody> reply = new BaseMessage<>();
        WhereNextBody body = WhereNextBody.builder().equipmentName(equipmentName).portName(portName).carrierName(carrierName).portType(portType).portTransportMode(portTransportMode).build();
        reply.setMessageName(MessageList.WHERE_NEXT.getMessageName());
        reply.setBody(body);

        return reply;
    }

    /**
     * unload가 완료 되었음을 보고합니다.
     * 1. port의 transferState -> ReservedToUnload 로 변경합니다 << 이거 조금 고민
     * -> 아무런 동작을 하지 않아도 될것 같기도 함.. 이거 고민
     * @param message 받은 메시지
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void unLoadCompleted(BaseMessage<UnLoadCompletedBody> message) {
        log.info("do not anything");
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

        Optional<Ports> optionalPorts = portsRepository.findByEquipmentNameAndPortName(equipmentName,portName);

        if(optionalPorts.isEmpty()){
            return;
        }

        Ports port = optionalPorts.get();
        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        PortTransportModeChangedCommand command = PortTransportModeChangedCommand.builder().transactionInfo(tx).portTransportModeName(portTransportModeName).build();

        port.transportModeChanged(command);
        portsRepository.save(port);

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

        Optional<Ports> optionalPorts = portsRepository.findByEquipmentNameAndPortName(equipmentName,portName);

        if(optionalPorts.isEmpty()){
            return;
        }
        if(!PortState.isExist(portStateName)){
            return;
        }

        Ports port = optionalPorts.get();
        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        PortState state = PortState.valueOf(portStateName);
        PortStateChangedCommand command = PortStateChangedCommand.builder().transactionInfo(tx).portState(state).build();
        port.portStateChanged(command);
        portsRepository.save(port);
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
            Optional<Ports> optionalPorts = portsRepository.findByEquipmentNameAndPortName(equipmentName,portName);

            if(optionalPorts.isEmpty()){
                continue;
            }
            if(!PortState.isExist(portStateName)){
                continue;
            }

            Ports port = optionalPorts.get();

            PortState state = PortState.valueOf(portStateName);
            PortStateChangedCommand command = PortStateChangedCommand.builder().transactionInfo(tx).portState(state).build();
            port.portStateChanged(command);
            portsRepository.save(port);
        }
    }

    /**
     * port 의 타입 변경시 보고
     * @param message 받은 메시지
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void portTypeChanged(BaseMessage<PortTypeChangedBody> message) {
        // TODO: 일단 Port Type Input, Output, InputOutput 이런게 바뀌는 경우가 생기는지 문의
    }

    /**
     * port 의 사용 타입 변경시 보고
     * @param message 받은 메시지
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void portUseTypeChanged(BaseMessage<PortUseTypeChangedBody> message) {
        // TODO: 일단 Port Use Type GG,NG.. 사용 type 따로 관리할건지 문의
    }


    // ============== [PortDef] ==============

    /**
     * 사용자의 데이터를 생성합니다.
     * @param requestDto 사용자의 생성 데이터
     * @return 생성된 사용자 도메인 객체
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public PortDef createPortDef(PortDefCreateRequestDto requestDto) {
        // 1. Repository를 통해 Domain 객체를 가져온다.
        Optional<PortDef> optionalPortDef = portDefRepository.findByEquipmentNameAndPortName(requestDto.getEquipmentName(),requestDto.getPortName());
        if(optionalPortDef.isPresent()){
            throw new EntityExistException("이미 생성된 포트정의입니다. equipment :{"+requestDto.getEquipmentName() +"} portName :{"+requestDto.getPortName()+"} ");
        }

        String eventName = EventName.CREATED.getValue();

        TransactionInfo tx = TransactionInfo.now(eventName,requestDto.getEventUser(),requestDto.getEventComment());
        PortDefCreateCommand command =
                PortDefCreateCommand.builder()
                        .equipmentName(requestDto.getEquipmentName())
                        .portName(requestDto.getPortName())
                        .description(requestDto.getDescription())
                        .portType(requestDto.getPortType())
                        .portUseType(requestDto.getPortUseType())
                        .useCarrierDefId(requestDto.getUseCarrierDefId())
                        .transactionInfo(tx)
                        .build();

        PortDef portDef = PortDef.create(command);

        return portDefRepository.save(portDef);
    }

    @Transactional(readOnly = true)
    public Page<PortDefResponseDto> findPortDefs(PortDefSearchConditionDto condition, Pageable pageable) {
        //1. Repository에서 Page<Entity>를 조회합니다.
        Page<PortDefResponseDto> page = portDefRepository.findPortDefWithConditions(condition,pageable);

        return page;
    }

    /**
     * 사용자의 데이터를 변경합니다.
     * @param requestDto 사용자의 변경 데이터
     * @return 변경된 사용자 도메인 객체
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public PortDef changePortDef(Long id, PortDefUpdateRequestDto requestDto) {
        // 1. Repository를 통해 Domain 객체를 가져온다.
        PortDef portDef;
        Optional<PortDef> optionalPortDef = portDefRepository.findById(id);
        if(optionalPortDef.isPresent()){
            portDef = optionalPortDef.get();
        }
        else {
            throw new EntityNotFoundException("존재하지 않는 포트 정의입니다. ID: " + requestDto.getId());
        }
        String eventName = EventName.UPDATED.getValue();

        TransactionInfo tx = TransactionInfo.now(eventName,requestDto.getEventUser(),requestDto.getEventComment());
        PortDefUpdateCommand command =
                PortDefUpdateCommand.builder()
                        .description(requestDto.getDescription())
                        .portType(requestDto.getPortType())
                        .portUseType(requestDto.getPortUseType())
                        .useCarrierDefId(requestDto.getUseCarrierDefId())
                        .transactionInfo(tx)
                        .build();

        portDef.changePortDef(command);

        return portDefRepository.save(portDef);
    }


    @Transactional
    public void deleteAllPortDefByIdInBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return; // 삭제할 ID가 없으면 아무 작업도 하지 않음
        }
        // 여러 건을 삭제할 때는 이 메서드가 성능상 가장 효율적입니다.
        // DELETE ... WHERE id IN (...) 쿼리를 한 번에 실행합니다.
        portDefRepository.deleteAllByIdInBatch(ids);
    }
    // ============== [PortDef] ==============


    // ============== [Ports] ==============

    /**
     * 사용자의 데이터를 생성합니다.
     * @param requestDto 사용자의 생성 데이터
     * @return 생성된 사용자 도메인 객체
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public Ports createPorts(PortsCreateRequestDto requestDto) {
        // 1. Repository를 통해 Domain 객체를 가져온다.
        Optional<Ports> optionalPorts = portsRepository.findByEquipmentNameAndPortName(requestDto.getEquipmentName(),requestDto.getPortName());
        if(optionalPorts.isPresent()){
            throw new EntityExistException("이미 생성된 포트정의입니다. ID: " + requestDto.getId());
        }

        String eventName = EventName.CREATED.getValue();

        TransactionInfo tx = TransactionInfo.now(eventName,requestDto.getEventUser(),requestDto.getEventComment());
        PortsCreateCommand command =
                PortsCreateCommand.builder()
                        .equipmentName(requestDto.getEquipmentName())
                        .portName(requestDto.getPortName())
                        .portDefId(requestDto.getPortDefId())
                        .description(requestDto.getDescription())
                        .connectedStocker(requestDto.getConnectedStocker())
                        .transportMode(requestDto.getTransportMode())
                        .portState(requestDto.getPortState())
                        .resourceState(requestDto.getResourceState())
                        .transportState(requestDto.getTransportState())
                        .carrierName(requestDto.getCarrierName())
                        .transactionInfo(tx)
                        .build();

        Ports ports = Ports.create(command);

        return portsRepository.save(ports);
    }

    @Transactional(readOnly = true)
    public Page<PortsResponseDto> findPorts(PortsSearchConditionDto condition, Pageable pageable) {
        //1. Repository에서 Page<Entity>를 조회합니다.
        Page<PortsResponseDto> page = portsRepository.findPortsWithConditions(condition,pageable);

        return page;
    }

    @Transactional(readOnly = true)
    public List<Ports> findPortsByTransportIsReadyToLoad() {
        // TODO: Select transportState is ReadyToLoad PortList 아래 변경
        return  portsRepository.findAll();
    }

    /**
     * 사용자의 데이터를 변경합니다.
     * @param requestDto 사용자의 변경 데이터
     * @return 변경된 사용자 도메인 객체
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public Ports changePort(Long id, PortsUpdateRequestDto requestDto) {
        // 1. Repository를 통해 Domain 객체를 가져온다.
        Ports ports;
        Optional<Ports> optionalPorts = portsRepository.findById(id);
        if(optionalPorts.isPresent()){
            ports = optionalPorts.get();
        }
        else {
            throw new EntityNotFoundException("존재하지 않는 포트 정의입니다. ID: " + requestDto.getId());
        }
        String eventName = EventName.UPDATED.getValue();

        TransactionInfo tx = TransactionInfo.now(eventName,requestDto.getEventUser(),requestDto.getEventComment());
        PortsUpdateCommand command =
                PortsUpdateCommand.builder()
                        .portDefId(requestDto.getPortDefId())
                        .description(requestDto.getDescription())
                        .connectedStocker(requestDto.getConnectedStocker())
                        .transportMode(requestDto.getTransportMode())
                        .portState(requestDto.getPortState())
                        .resourceState(requestDto.getResourceState())
                        .transportState(requestDto.getTransportState())
                        .carrierName(requestDto.getCarrierName())
                        .transactionInfo(tx)
                        .build();

        ports.changePort(command);

        return portsRepository.save(ports);
    }


    @Transactional
    public void deleteAllPortsByIdInBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return; // 삭제할 ID가 없으면 아무 작업도 하지 않음
        }
        // 여러 건을 삭제할 때는 이 메서드가 성능상 가장 효율적입니다.
        // DELETE ... WHERE id IN (...) 쿼리를 한 번에 실행합니다.
        portsRepository.deleteAllByIdInBatch(ids);
    }
    // ============== [Ports] ==============

}