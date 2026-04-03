package kr.co.aim.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.api.dto.insert.IfEventQueueDto;
import kr.co.aim.api.vo.insert.ops.InsertEventLogReportVo;
import kr.co.aim.api.vo.port.TransportStateChangedVo;
import kr.co.aim.api.vo.transportJob.CreateTransportJobVo;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.format.*;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.api.strategy.FactoryProcessStrategy;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.IfEventQueueCreateCommand;
import kr.co.aim.domain.command.LoadCompletedCommand;
import kr.co.aim.domain.command.TransportJobCreateCommand;
import kr.co.aim.domain.model.*;
import kr.co.aim.domain.repository.*;
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
import org.springframework.transaction.annotation.Propagation;
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


    @Override
    @Transactional(value = "mssqlTransactionManager") // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
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
                    String drivingProfile = transportOrder.getDrivingProfile();
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
                                    .drivingProfile(drivingProfile)
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
                                    .requestType(TransportJobRequestType.GAL.getValue())
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
    @Transactional(value = "mssqlTransactionManager") // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public BaseMessage<DestinationDispatchRequestBody> unLoadRequest(BaseMessage<UnLoadRequestBody> message) {
        return null;
    }

    @Override
    @Transactional(value = "mssqlTransactionManager") // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
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
        String drivingProfile = transportOrder.getDrivingProfile();
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
                            //TODO: TransportJobNaming rule check
                            .transportJobName(carrierName + tx.eventTime().toString().substring(0,12))
                            .carrierName(carrierName)
                            .transportType(transportType)
                            .transportJobState(TransportJobState.REQUESTED.getValue())
                            .carrierType(carrierType)
                            .drivingProfile(drivingProfile)
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
                            .requestType(TransportJobRequestType.GAL.getValue())
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
        // TODO : 신규로 만든 IfEventQueueService.enqueue 호출로 변경
    }

    /**
     * 1. 큐에 처음 넣을 때 (신규 생성)
     * try{
     * InterfaceEventLogService.enqueue(vo);
     * }
     * catch(Exception e){
     * log.error("로그 저장 실패");
     * }
     * 위 방식으로 호출 해야함
     */
    @Override
    @Transactional(value = "mssqlTransactionManager",propagation = Propagation.REQUIRES_NEW)
    public void enqueueIfEventQueue(Object vo) {
        // Java 17의 Pattern Matching 사용
        if (vo instanceof InsertEventLogReportVo reportVo) {
            // save EventLog로 변경
            Optional<IfEventQueueDto> optionalIfEventQueueDto = createEventLogDto(reportVo);
            if(optionalIfEventQueueDto.isEmpty()){
                return;
            }
            IfEventQueueDto dto = optionalIfEventQueueDto.get();
            TransactionInfo tx = TransactionInfo.now("saveInterfaceEventLog",SystemName.MNG.getValue(), "",reportVo.getTx().eventTime());

            // DTO 객체를 JSON 문자열로 직접 변환합니다.
            String jsonPayload = "";
            try {
                jsonPayload = objectMapper.writeValueAsString(dto);
            } catch (JsonProcessingException e) {
                log.error("dto -> String error");
                // 로깅 및 예외 처리
                throw new RuntimeException("InterfaceEventLogDto를 JSON으로 변환하는 중 오류가 발생했습니다.", e);
            }
            log.info("Sending JSON Payload: {}", jsonPayload);
            IfEventQueueCreateCommand command =
                    IfEventQueueCreateCommand
                            .builder()
                            .transactionInfo(tx)
                            .eventType(dto.getEventType())
                            .payload(jsonPayload)
                            .ifStatus(IfEventQueueState.READY.getValue())
                            .carrierName(dto.getCarrierName())
                            .idocId(dto.getIdocId())
                            .orderId(dto.getOrderId())
                            .orderLineNumber(dto.getOrderLineNumber())
                            .retryCNT(0)
                            .errMSG("")
                            .createTime(tx.eventTime())
                            .build();
            IfEventQueue interfaceEventLog = IfEventQueue.create(command);
            ifEventQueueService.save(interfaceEventLog);
        }else {
            log.error("잘못된 객체 타입이 전달되었습니다: {}", vo != null ? vo.getClass().getName() : "null");
        }

    }

    private Optional<IfEventQueueDto> createEventLogDto(InsertEventLogReportVo vo) {
        String messageName = vo.getMessageName();
        PortDef portDef = vo.getPortDef();
        Port port = vo.getPort();
        String transportJobName =  vo.getTransportJobName();
        String eventType = "";
        String transactionCode ="";
        String carrierName = vo.getCarrierName(); // 어떠한 경우에도 공백이 없네
        String idocId = "";
        String orderId = "";
        String orderLineNumber = "";
        String orderType = "";
        if (StringUtils.equals(MessageList.LOAD_COMPLETE.getMessageName(), messageName)) {
            if (StringUtils.equals(PortDetailType.INBOUND.getValue(), portDef.getDetailPortType())) {
                // Inbound Station Occupied case
                // 106 report
                eventType = GALTransportStatus.StationOccupied.name();
                transactionCode = GALTransportStatus.StationOccupied.getValue();
                idocId = "";
                orderId = "";
                orderLineNumber = "";
                orderType = TransportOrderType.INBOUND.getValue();
            } else if (StringUtils.equals(PortDetailType.WORKSTATION.getValue(), portDef.getDetailPortType())) {
                // 반송잡이 있으면 해당 반송잡으로 아래보고
                // outbound case
                // 108 Outbound Arrival At workStation report
                // 90 outbound order Done report
                // 반송잡이 없다면,
                // 가장 최신 변경된 transportOrder 으로 108,90 보고
                TransportOrder transportOrder = null;
                if(StringUtils.isNotBlank(transportJobName)){
                    Optional<TransportOrder> optionalTransportOrder = transportOrderService.findByTransportJobName(transportJobName);
                    if(optionalTransportOrder.isPresent()){
                        transportOrder = optionalTransportOrder.get();
                    }
                }
                if(transportOrder==null){
                    List<String> transportStatus = new ArrayList<>();
                    transportStatus.add(TransportOrderStatus.STARTED.getValue());
                    List<TransportOrder> transportOrders = transportOrderService.findTransportOrderByCondition(
                            carrierName,
                            TransportOrderType.OUTBOUND.getValue(),
                            transportStatus);
                    if(transportOrders.isEmpty()){
                        throw new RuntimeException("Not Exists TransportOrder");
                    }
                    transportOrder = transportOrders.get(0);
                }
                eventType = GALTransportStatus.ArrivedAtWorkStation.name();
                transactionCode = GALTransportStatus.ArrivedAtWorkStation.getValue();
                idocId = transportOrder.getIdocId().toString();
                orderId = transportOrder.getTransportOrderId();
                orderLineNumber = "";
                orderType = TransportOrderType.OUTBOUND.getValue();
            }
        } else if (StringUtils.equals(MessageList.UNLOAD_COMPLETE.getMessageName(), messageName)) {
            if (StringUtils.equals(PortDetailType.INBOUND.getValue(), portDef.getDetailPortType())) {
                // Inbound Workstation empty
                // 105 repot
                // transportJobName 은 존재
            }
        } else if (StringUtils.equals(MessageList.CARRIER_SCANNED.getMessageName(), messageName)) {
            // Inbound ContainerId is Scanned
            // 126 repot
        } else if (StringUtils.equals(MessageList.CARRIER_LOCATION_CHANGED.getMessageName(), messageName)) {
            // 이 경우는 TransportOrder가 있을수도 없을수도 있음
            // orderId가 있을수도 없을 수도 있다는 이야기
            if (StringUtils.equals(PortDetailType.OUT_OF_RACK.getValue(), portDef.getDetailPortType())) {
                // Out of Rack
                // 109 repot
            } else if (StringUtils.equals(PortDetailType.TUNNEL.getValue(), portDef.getDetailPortType())) {
                // S/R Machine dropped container on tunnel conveyor
                // 109 report
            }

        } else if (StringUtils.equals(MessageList.TRANSPORT_JOB_COMPLETED.getMessageName(), messageName)) {
            // 무조건 TransportJob 은 존재

            // Type : Inbound Case
            // 107 Arrival at Rack report
            // 92 Inbound order Done report

            // Type : Outbound Case
            // 109 Out of Rack report

            // Type : Relocation Case
            // #1 orderId 가 존재하면
            // 107 Arrival at Rack report
            // 94 Relocation order confirmation report

            // #2 orderId 가 존재하지 않는다면
            // 114 internal Relocation report
        } else if (StringUtils.equals(MessageList.TRANSPORT_JOB_REPLY.getMessageName(), messageName)) {
            // 무조건 TransportJob 은 존재
            // Type : Inbound Case
            // Type : Outbound Case
            // Type : Relocation Case
            // 2 Accept report
        } else if (StringUtils.equals(MessageList.TRANSPORT_JOB_STARTED.getMessageName(), messageName)) {
            // 무조건 TransportJob 은 존재
            // Type : Inbound Case
            // Type : Outbound Case
            // Type : Relocation Case
            // 2 Accept report
        }
        else{
            return Optional.empty();
        }
        // TODO: 추가되는 dto 관련된건 여기다가 추가하기
        IfEventQueueDto dto = IfEventQueueDto
                .builder()
                .messageName(messageName)
                .eventType(eventType)
                .transactionCode(transactionCode)
                .carrierName(carrierName)
                .idocId(idocId)
                .orderId(orderId)
                .orderLineNumber(orderLineNumber)
                .orderType(orderType)
                .errorTexts(vo.getErrorTexts())
                .actualWeight(vo.getActualWeight())
                .actualZoneName(vo.getActualZoneName())
                .actualRackLocationId(vo.getActualRackLocationId())
                .build();
        return Optional.ofNullable(dto);
    }
}
