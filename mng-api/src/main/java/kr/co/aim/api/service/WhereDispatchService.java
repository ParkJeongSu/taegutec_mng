package kr.co.aim.api.service;


import kr.co.aim.api.context.WhereDispatchContextFactory;
import kr.co.aim.api.strategy.WhereDispatchStrategy;
import kr.co.aim.api.context.WhereDispatchContext;
import kr.co.aim.common.Utils.FormatUtils;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.format.DestinationDispatchRequestBody;
import kr.co.aim.common.format.TransportJobRequestBody;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.TransportJobCreateCommand;
import kr.co.aim.domain.model.Equipment;
import kr.co.aim.domain.model.TransportJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
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

        if(ObjectUtils.isEmpty(context.getTargetEquipment())){
            throw new RuntimeException("No dispatch strategy found for target equipment");
        }

        if(ObjectUtils.isEmpty(context.getTargetPort())){
            // target port가 비어있다는 이야기는 창고로 들어간다는 이야기
            // targetEquipment 가 창고인지 그리고 targetZoneName이 존재하는지 확인
            if(StringUtils.isEmpty(context.getTargetZoneName())){
                throw new RuntimeException("target zone name is empty");
            }
        }
        else if(ObjectUtils.isNotEmpty(context.getTargetPort())){
            if(!StringUtils.equals(PortTransportState.READY_TO_LOAD.getValue(),context.getTargetPort().getTransportState())){
                throw new RuntimeException("target port is not ready for transport");
            }
        }

        // 3.target 설비가 down 상태라면 반송요청을 하지 않음
        Equipment targetEquipment = context.getTargetEquipment();
        if(StringUtils.equals(EquipmentState.DOWN.getValue(),targetEquipment.getEquipmentState())){
            throw new RuntimeException("target equipment state is DOWN");
        }

        // 4. 반송 작업 생성 및 BaseMessage 반환
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
                .destinationPortName( ObjectUtils.isEmpty(context.getTargetPort()) ? "" : context.getTargetPort().getPortName())
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
