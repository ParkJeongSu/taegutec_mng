package kr.co.aim.api.service;


import kr.co.aim.api.strategy.WhereDispatchStrategy;
import kr.co.aim.api.vo.powder.ops.WhereDispatchContext;
import kr.co.aim.common.Utils.FormatUtils;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.format.DestinationDispatchRequestBody;
import kr.co.aim.common.format.TransportJobRequestBody;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.TransportJobCreateCommand;
import kr.co.aim.domain.model.TransportJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WhereDispatchService {

    private final List<WhereDispatchStrategy> dispatchStrategies;
    private final WhereDispatchContextFactory contextFactory; // 조회 로직 캡슐화
    private final TransportJobService transportJobService;
    private final NamingRuleService namingRuleService;

    @Transactional(value = "mssqlTransactionManager")
    public BaseMessage<TransportJobRequestBody> whereDispatchRequest(BaseMessage<DestinationDispatchRequestBody> message) {

        // 1. 요청 검증 및 DispatchContext 조회 (Guard Clause 캡슐화)
        WhereDispatchContext context = contextFactory.createContext(message.getBody());

        // 2. 적합한 Strategy 탐색 및 목적지 결정

        WhereDispatchStrategy targetStrategy = null;
        for (WhereDispatchStrategy strategy : dispatchStrategies) {
            if (strategy.supports(context)) {
                targetStrategy = strategy;
                break; // 적합한 전략을 찾았으므로 루프 탈출
            }
        }

        // 조건에 맞는 전략을 찾지 못한 경우 예외 처리
        if (targetStrategy == null) {
            throw new IllegalArgumentException("No dispatch strategy found for context");
        }
        targetStrategy.determineDestination(context);

        // 3. 반송 작업 생성 및 BaseMessage 반환
        return createTransportJobMessage(context);
    }

    private BaseMessage<TransportJobRequestBody> createTransportJobMessage(WhereDispatchContext context) {
        TransactionInfo tx = TransactionInfo.now(EventName.AUTO_TRANSPORT.getValue(), SystemName.MNG.getValue(), "");
        String transactionId = FormatUtils.getTransactionId(tx.eventTime());
        String transportJobName = namingRuleService.getTransportJobName(SystemName.MNG.getValue(), tx.eventTime());

        TransportJobCreateCommand command = TransportJobCreateCommand.builder()
                .transportJobName(transportJobName)
                .carrierName(context.getCarrier().getCarrierName())
                .transportJobState(TransportJobState.REQUESTED.getValue())
                .carrierType(context.getCarrierDef().getCarrierType())
                .sourceEquipmentName(context.getSourceEquipment().getEquipmentName())
                .sourcePortName(context.getSourcePort().getPortName())
                .sourceZoneName(context.getCarrier().getZoneName())
                .sourcePositionTypeName(context.getCarrier().getPositionTypeName())
                .sourcePositionName(context.getCarrier().getPositionName())
                .destinationEquipmentName(context.getTargetEquipment().getEquipmentName())
                .destinationPortName(context.getTargetPort().getPortName())
                .destinationZoneName(context.getTargetZoneName())
                .createTime(tx.eventTime())
                .transactionInfo(tx)
                .build();

        TransportJob transportJob = transportJobService.createTransportJob(command);

        BaseMessage<TransportJobRequestBody> request = new BaseMessage<>();
        request.setTransactionId(transactionId);
        request.setMessageFrom(SystemName.MNG.getValue());
        request.setMessageOwner(SystemName.MNG.getValue());
        request.setMessageTo(SystemName.WCS.getValue());
        request.setEventTime(transactionId);
        request.setMessageName(MessageList.TRANSPORT_JOB_REQUEST.getMessageName());
        request.setResultCode(ResultCode.OK.getValue());
        request.setBody(transportJobService.createTransportJobMessage(transportJob));

        return request;
    }
}
