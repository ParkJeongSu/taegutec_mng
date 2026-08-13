package kr.co.aim.api.service;


import kr.co.aim.api.context.DownloadContextFactory;
import kr.co.aim.api.strategy.DownloadStrategy;
import kr.co.aim.api.context.DownloadContext;
import kr.co.aim.common.format.CarrierInfoDownloadSendBody;
import kr.co.aim.common.format.LoadCompletedBody;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.record.TransactionInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DownloadService {

    private final List<DownloadStrategy> dispatchStrategies;
    private final DownloadContextFactory contextFactory; // 조회 로직 캡슐화

    @Transactional(value = "mssqlTransactionManager")
    public BaseMessage<CarrierInfoDownloadSendBody> downloadRequest(TransactionInfo transactionInfo,BaseMessage<LoadCompletedBody> message) {

        // 1. 요청 검증 및 DispatchContext 조회 (Guard Clause 캡슐화)
        DownloadContext context = contextFactory.createContext(transactionInfo,message.getBody());

        // 2. 적합한 Strategy 탐색 및 목적지 결정

        DownloadStrategy targetStrategy = null;
        for (DownloadStrategy strategy : dispatchStrategies) {
            if (strategy.supports(context)) {
                targetStrategy = strategy;
                break; // 적합한 전략을 찾았으므로 루프 탈출
            }
        }

        // 조건에 맞는 전략을 찾지 못한 경우 예외 처리
        if (targetStrategy == null) {
            throw new IllegalArgumentException("No dispatch strategy found for context");
        }
        return targetStrategy.determineCarrierInfo(context);
    }

}
