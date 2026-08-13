package kr.co.aim.api.service;


import kr.co.aim.api.context.WhatDispatchContextFactory;
import kr.co.aim.api.strategy.WhatDispatchStrategy;
import kr.co.aim.api.vo.port.TransportStateChangedVo;
import kr.co.aim.api.context.WhatDispatchContext;
import kr.co.aim.common.Utils.FormatUtils;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.format.CarrierDispatchRequestBody;
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
public class WhatDispatchService {

    private final List<WhatDispatchStrategy> dispatchStrategies;
    private final WhatDispatchContextFactory contextFactory; // 조회 로직 캡슐화
    private final TransportJobService transportJobService;
    private final NamingRuleService namingRuleService;
    private final PortService portService;

    @Transactional(value = "mssqlTransactionManager")
    public BaseMessage<TransportJobRequestBody> whatDispatchRequest(BaseMessage<CarrierDispatchRequestBody> message) {

        // 1. 요청 검증 및 DispatchContext 조회 (Guard Clause 캡슐화)
        WhatDispatchContext context = contextFactory.createContext(message.getBody());

        // 2. Validation
        // 2.1 target Equipment가 Down인경우 return null
        Equipment equipment = context.getEquipment();
        if(ObjectUtils.isEmpty(equipment)){
            throw new RuntimeException("equipment is null");
        }
        if(StringUtils.equals(EquipmentState.DOWN.getValue(),equipment.getEquipmentState()) ){
            throw new RuntimeException("equipment state is DOWN");
        }

        // 3. 적합한 Strategy 탐색 및 목적지 결정
        WhatDispatchStrategy targetStrategy = null;
        for (WhatDispatchStrategy strategy : dispatchStrategies) {
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

        if(ObjectUtils.isEmpty(context.getCarrier())){
            return null;
        }else{
            TransactionInfo tx = TransactionInfo.now(EventName.AUTO_TRANSPORT.getValue(), SystemName.MNG.getValue(), "");
            // 3. 반송 작업 생성 및 BaseMessage 반환
            BaseMessage<TransportJobRequestBody> request = createTransportJobMessage(context,tx);

            // 4. PORT RESERVE TO LOAD 로 변경
            TransportStateChangedVo vo =
                    TransportStateChangedVo
                            .builder()
                            .port(context.getPort())
                            .portTransportState(PortTransportState.RESERVED_TO_LOAD)
                            .tx(tx)
                            .build();
            portService.transportStateChanged(vo);

            return request;
        }


    }

    private BaseMessage<TransportJobRequestBody> createTransportJobMessage(WhatDispatchContext context,TransactionInfo tx) {
        String transactionId = FormatUtils.getTransactionId(tx.eventTime());
        String transportJobName = namingRuleService.getTransportJobName(SystemName.MNG.getValue(), tx.eventTime());

        TransportJobCreateCommand command = TransportJobCreateCommand.builder()
                .transportJobName(transportJobName)
                .carrierName(context.getCarrier().getCarrierName())
                .transportJobState(TransportJobState.REQUESTED.getValue())
                .carrierType(context.getCarrierDef().getCarrierType())
                .sourceEquipmentName(context.getCarrier().getEquipmentName())
                .sourcePortName(context.getCarrier().getPortName())
                .sourceZoneName(context.getCarrier().getZoneName())
                .sourcePositionTypeName(context.getCarrier().getPositionTypeName())
                .sourcePositionName(context.getCarrier().getPositionName())
                .destinationEquipmentName(context.getEquipment().getEquipmentName())
                .destinationPortName(context.getPort().getPortName())
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
