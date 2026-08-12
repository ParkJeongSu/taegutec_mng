package kr.co.aim.api.service;


import kr.co.aim.api.strategy.FactoryIfEventQueueStrategy;
import kr.co.aim.api.strategy.ProductionOrderProcessStrategy;
import kr.co.aim.api.strategy.WhatDispatchStrategy;
import kr.co.aim.api.vo.port.TransportStateChangedVo;
import kr.co.aim.api.vo.powder.ops.PowderEventQueueReportVo;
import kr.co.aim.api.vo.powder.ops.ProductionOrderProcessContext;
import kr.co.aim.api.vo.powder.ops.WhatDispatchContext;
import kr.co.aim.common.Utils.FormatUtils;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.format.CarrierDispatchRequestBody;
import kr.co.aim.common.format.ProductionOrderProcessRequestBody;
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
public class ProductionOrderProcessService {

    private final List<ProductionOrderProcessStrategy>  processStrategies;
    private final ProductionOrderProcessContextFactory contextFactory;
    private final FactoryIfEventQueueStrategy factoryIfEventQueueStrategy;

    @Transactional(value = "mssqlTransactionManager")
    public void productionOrderProcessRequest(BaseMessage<ProductionOrderProcessRequestBody> message) {

        String messageName = message.getMessageName();
        String transactionId = message.getTransactionId();
        String messageFrom = message.getMessageFrom();
        String messageOwner = message.getMessageOwner();
        TransactionInfo tx = TransactionInfo.now(messageName, messageFrom, messageOwner);
        // 1. 요청 검증 및 DispatchContext 조회 (Guard Clause 캡슐화)
        ProductionOrderProcessContext context = contextFactory.createContext(tx,message.getBody());

        // 3. 적합한 Strategy 탐색 및 목적지 결정
        ProductionOrderProcessStrategy targetStrategy = null;
        for (ProductionOrderProcessStrategy strategy : processStrategies) {
            if (strategy.supports(context)) {
                targetStrategy = strategy;
                break; // 적합한 전략을 찾았으므로 루프 탈출
            }
        }

        // 조건에 맞는 전략을 찾지 못한 경우 예외 처리
        if (targetStrategy == null) {
            throw new IllegalArgumentException("No process strategy found for context");
        }
        targetStrategy.productionOrderProcess(context);

        // powder EventQueue
        try{
            PowderEventQueueReportVo powderEventQueueReportVo
                    = PowderEventQueueReportVo
                    .builder()
                    .messageName(messageName)
                    .productionOrder(context.getProductionOrder())
                    .tx(tx)
                    .build();
            factoryIfEventQueueStrategy.enqueueIfEventQueue(powderEventQueueReportVo);
        }
        catch(Exception e){
            log.error("EventQueue enqueue error",e);
        }

    }

}
