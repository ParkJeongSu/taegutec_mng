package kr.co.aim.api.service;

import kr.co.aim.common.Utils.FormatUtils;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.format.TransportJobRequestBody;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.TransportJobCreateCommand;
import kr.co.aim.domain.model.PortDef;
import kr.co.aim.domain.model.TransportJob;
import kr.co.aim.domain.model.TransportOrder;
import kr.co.aim.domain.repository.PortDefRepository;
import kr.co.aim.domain.repository.TransportOrderRepository;
import kr.co.aim.infra.config.RabbitConfig;
import kr.co.aim.infra.persistence.entity.TransportOrderHistoryEntity;
import kr.co.aim.infra.persistence.mapper.TransportOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransportOrderDispatcherService {

    private final TransportOrderRepository transportOrderRepository;
    private final PortDefRepository portDefRepository;
    private final NamingRuleService namingRuleService;
    private final TransportJobService transportJobService;
    private final HistoryService historyService;
    private final RabbitTemplate rabbitTemplate;
    private final TransportOrderMapper transportOrderMapper;

    // 1건 단위로 완전히 독립된 트랜잭션 보장
    @Transactional(value = "mssqlTransactionManager", propagation = Propagation.REQUIRES_NEW)
    public void processAndSendOrder(Long transportOrderPk) {
        TransportOrder transportOrder = transportOrderRepository.findWithLockById(transportOrderPk)
                .orElseThrow(() -> new IllegalArgumentException("TransportOrder 없음: " + transportOrderPk));

        if (!StringUtils.equals(transportOrder.getTransportStatus(), TransportOrderStatus.CREATED.getValue())) {
            return;
        }

        TransactionInfo tx = TransactionInfo.now(
                transportOrder.getEventName(),
                transportOrder.getEventUser(),
                transportOrder.getEventComment()
        );

        // 1. 포트 매핑 및 Command 생성
        TransportJobCreateCommand command = buildJobCommand(transportOrder, tx);

        // 2. TransportJob 생성 및 저장
        TransportJob transportJob = transportJobService.createTransportJob(command);

        // 3. TransportOrder 상태 변경 (REQUESTED)
        transportOrder.setTransportStatus(TransportOrderStatus.REQUESTED.getValue());
        transportOrderRepository.save(transportOrder);

        // 4. 이력 저장
        TransportOrderHistoryEntity historyEntity = transportOrderMapper.toHistoryEntity(transportOrder);
        historyService.saveHistory(historyEntity);

        // 5. WCS 전송 메시지 생성
        BaseMessage<TransportJobRequestBody> request = new BaseMessage<>();
        request.setMessageName(MessageList.TRANSPORT_JOB_REQUEST.getMessageName());
        request.setTransactionId(FormatUtils.getTransactionId(tx.eventTime()));
        request.setMessageFrom(SystemName.MNG.getValue());
        request.setMessageOwner(SystemName.MNG.getValue());
        request.setMessageTo(SystemName.WCS.getValue());
        request.setEventTime(LocalDateTime.now().toString());
        request.setResultCode(ResultCode.OK.getValue());
        request.setResultMessage("");

        TransportJobRequestBody body = transportJobService.createTransportJobMessage(transportJob);
        request.setBody(body);

        // 6. MQ 전송
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_WCS, RabbitConfig.ROUTING_WCS, request);
    }

    // 실패 시 retryCount 증가 및 한계 초과 시 FAILED 상태 전이
    @Transactional(value = "mssqlTransactionManager", propagation = Propagation.REQUIRES_NEW)
    public void handleFailure(Long transportOrderPk, int maxRetryCount, Exception ex) {
        Optional<TransportOrder> optionalOrder = transportOrderRepository.findById(transportOrderPk);
        if (optionalOrder.isEmpty()) return;

        TransportOrder order = optionalOrder.get();
        int currentCount = order.getRetryCnt() == null ? 0 : order.getRetryCnt();
        int nextCount = currentCount + 1;
        order.setRetryCnt(nextCount);

        if (nextCount >= maxRetryCount) {
            order.setTransportStatus(TransportOrderStatus.FAILED.getValue());
            log.error("🚨 [치명적] Order ID: {} 최대 재시도 횟수({}) 초과로 FAILED 처리. 관리자 개입 필요.",
                    order.getTransportOrderId(), maxRetryCount);
        } else {
            log.warn("⚠️ Order ID: {} 발송 실패 (재시도 횟수: {}/{}) - 원인: {}",
                    order.getTransportOrderId(), nextCount, maxRetryCount, ex.getMessage());
        }

        transportOrderRepository.save(order);
    }

    private TransportJobCreateCommand buildJobCommand(TransportOrder order, TransactionInfo tx) {
        String transportJobName = namingRuleService.getTransportJobName(SystemName.GAL.getValue(), tx.eventTime());
        String transportType = order.getTransportType();

        String sourceEquipmentName = "";
        String sourcePortName = "";
        String sourcePositionTypeName = "";
        String sourcePositionName = "";
        String destinationEquipmentName = "";
        String destinationPortName = "";
        String destinationPositionTypeName = "";
        String destinationPositionName = "";
        String destinationZoneName = order.getRequestedZoneName();

        if (StringUtils.equals(TransportOrderType.INBOUND.getValue(), transportType)) {
            if (StringUtils.isNotEmpty(order.getLocationId())) {
                Optional<PortDef> optionalPortDef = portDefRepository.findByLocationId(order.getLocationId());
                if (optionalPortDef.isPresent()) {
                    PortDef portDef = optionalPortDef.get();
                    sourceEquipmentName = portDef.getEquipmentName();
                    sourcePortName = portDef.getPortName();
                    sourcePositionTypeName = PositionTypeName.PORT.getValue();
                    sourcePositionName = portDef.getPortName();
                    destinationPositionTypeName = PositionTypeName.ZONE.getValue();
                }
            }
        } else if (StringUtils.equals(TransportOrderType.RELOCATION.getValue(), transportType)) {
            sourcePositionTypeName = PositionTypeName.ZONE.getValue();
            destinationPositionTypeName = PositionTypeName.ZONE.getValue();
            destinationZoneName = "";
        } else if (StringUtils.equals(TransportOrderType.OUTBOUND.getValue(), transportType)) {
            List<String> detailPortTypes = List.of(DetailPortType.CRANE_BOTH_PND.getValue(), DetailPortType.CRANE_OUT_PND.getValue());
            List<String> portTypes = List.of(PortType.BOTH.getValue(), PortType.OUTPUT.getValue());

            List<PortDef> portDefList = portDefRepository.findByWorkCenterNameAndDetailPortTypeInAndPortTypeIn(
                    order.getWorkStationId(), detailPortTypes, portTypes
            );

            if (CollectionUtils.isEmpty(portDefList)) {
                throw new IllegalStateException("workCenter not found: " + order.getWorkStationId());
            }

            PortDef portDef = portDefList.get(0);
            sourcePositionTypeName = PositionTypeName.ZONE.getValue();
            sourcePositionName = order.getLocationId();
            destinationEquipmentName = portDef.getEquipmentName();
            destinationPortName = portDef.getPortName();
            destinationPositionTypeName = PositionTypeName.PORT.getValue();
            destinationPositionName = portDef.getPortName();
            destinationZoneName = "";
        }

        return TransportJobCreateCommand.builder()
                .transportJobName(transportJobName)
                .carrierName(order.getCarrierName())
                .transportType(transportType)
                .transportJobState(TransportJobState.REQUESTED.getValue())
                .carrierType(order.getCarrierType())
                .travelProfile(order.getTravelProfile())
                .sourceEquipmentName(sourceEquipmentName)
                .sourcePortName(sourcePortName)
                .sourceZoneName(order.getSourceZoneName())
                .sourcePositionTypeName(sourcePositionTypeName)
                .sourcePositionName(sourcePositionName)
                .destinationEquipmentName(destinationEquipmentName)
                .destinationPortName(destinationPortName)
                .destinationZoneName(destinationZoneName)
                .destinationPositionTypeName(destinationPositionTypeName)
                .destinationPositionName(destinationPositionName)
                .priority(order.getPriority())
                .requestSource(TransportJobRequestType.GAL.getValue())
                .createTime(tx.eventTime())
                .orderId(order.getTransportOrderId())
                .transactionInfo(tx)
                .build();
    }
}