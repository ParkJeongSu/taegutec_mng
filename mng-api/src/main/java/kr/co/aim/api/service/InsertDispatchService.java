package kr.co.aim.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.api.vo.carrier.CarrierSelectionResult;
import kr.co.aim.api.vo.transportJob.CreateTransportJobVo;
import kr.co.aim.common.enums.MessageList;
import kr.co.aim.common.enums.TransportJobRequestType;
import kr.co.aim.common.enums.TransportOrderStatus;
import kr.co.aim.common.format.*;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.handler.DispatchStrategy;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.TransportJobCreateCommand;
import kr.co.aim.domain.command.UnLoadRequestCommand;
import kr.co.aim.domain.model.Carrier;
import kr.co.aim.domain.model.Port;
import kr.co.aim.domain.model.TransportJob;
import kr.co.aim.domain.model.TransportOrder;
import kr.co.aim.domain.repository.*;
import kr.co.aim.infra.config.RabbitConfig;
import kr.co.aim.infra.persistence.entity.TransportOrderHistoryEntity;
import kr.co.aim.infra.persistence.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
public class InsertDispatchService implements DispatchStrategy {

    private final HistoryService historyService;
    private final PortDefRepository portDefRepository;

    private final PortRepository portRepository;
    private final PortMapper portMapper;

    private final CarrierRepository carrierRepository;
    private final CarrierMapper carrierMapper;

    private final EquipmentRepository equipmentRepository;
    private final EquipmentDefRepository equipmentDefRepository;

    private final TransportJobService transportJobService;
    private final TransportJobRepository transportJobRepository;
    private final TransportJobMapper transportJobMapper;

    private final TransportOrderService transportOrderService;
    private final TransportOrderRepository transportOrderRepository;
    private final TransportOrderMapper transportOrderMapper;



    @Override
    public BaseMessage<TransportJobRequestListBody> carrierDispatchRequest(BaseMessage<CarrierDispatchRequestBody> message) {
        return null;
    }

    /**
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
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
                            .transportJobName(carrierName + tx.eventTime().toString().substring(0,12))
                            .carrierName(carrierName)
                            //.sourceEquipmentName()
                            //.sourcePortName()
                            .sourceZoneName(sourceZoneName)
                            //.sourcePositionType()
                            //.sourcePositionName()
                            //.destinationEquipmentName()
                            //.destinationPortName()
                            .destinationZoneName(destinationZoneName)
                            //.destinationPositionType()
                            //.destinationPositionName()
                            .createTime(tx.eventTime())
                            .requestType(TransportJobRequestType.GAL.getValue())
                            .orderId(transportOrderId )
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
            transportOrder = transportOrderRepository.save(transportOrder);
            TransportOrderHistoryEntity transportOrderHistoryEntity = transportOrderMapper.toHistoryEntity(transportOrder);
            historyService.saveHistory(transportOrderHistoryEntity);

            return request;
        }

        return null;
    }
}
