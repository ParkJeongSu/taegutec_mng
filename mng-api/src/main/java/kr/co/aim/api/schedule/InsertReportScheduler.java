package kr.co.aim.api.schedule;

import kr.co.aim.api.service.IfEventQueueService;
import kr.co.aim.api.service.InsertExternalInterfaceService;
import kr.co.aim.common.enums.*;
import kr.co.aim.domain.model.IfEventQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("scheduler")
@ConditionalOnProperty(name = "factory.type", havingValue = "insert")
public class InsertReportScheduler {

    private final InsertExternalInterfaceService insertExternalInterfaceService;
    private final IfEventQueueService ifEventQueueService;

    @Scheduled(fixedDelay = 5000) // 5초마다 실행
    @SchedulerLock(name = "insertReportH2Trans",
            lockAtMostFor = "PT2M",     // 작업 최장 소요시간 + 버퍼
            lockAtLeastFor = "PT5S")    // 최소 간격(선택)
    public void insertReportH2Trans() {
        // 1단계 : EventLog 조회 MSSQL 트랜잭션 Ready 상태 조회 비관적 lock 으로 조회

        // 2단계 : try catch for문
        // EventLog 마다 DB2 report
        // 정상적으로 성공하면, EventLog 를 Success 로 변경
        // catch 문으로 빠지고, 재시도 횟수 증가 재시도 횟수가 3미만이면, Ready 상태로 변경
        // catch 문으로 빠지고, 재시도 횟수 증가 재시도 횟수가 3초과하면, Fail 상태로 변경

        // 1단계 EvengLog 조회
        // 1.1 단계 << 고민 만약에 트랜잭션을 강하게 유지하고 싶으면,
        // Ready -> Processing 으로 상태 변경 << 이건 추후 고민
        List<IfEventQueue> ifEventQueues = ifEventQueueService.findByIfStatusOrderByCreateTimeAsc(IfEventQueueState.READY.getValue());

        if(CollectionUtils.isNotEmpty(ifEventQueues)){
            for(IfEventQueue ifEventQueue : ifEventQueues){
                try {
                    // DB2 H2transReport
                    insertExternalInterfaceService.reportH2trans(ifEventQueue);
                    // ifEventQueue 상태를 Success 로 변경
                    ifEventQueueService.reportCompleted(ifEventQueue.getId());

                } catch (Exception e) {
                    // retry cnt ++
                    // 만일 3초과면, ready -> fail 로 데이터 변경
                    log.error("reportFail id {} ",ifEventQueue.getId());
                    try {
                        Optional<IfEventQueue> optionalIfEventQueue
                                = ifEventQueueService.increaseRetryCnt(ifEventQueue.getId());
                        if(optionalIfEventQueue.isPresent()){
                            if(optionalIfEventQueue.get().getRetryCNT() > 3){
                                ifEventQueueService.reportFailed(ifEventQueue.getId());
                            }
                        }
                    } catch (Exception e1){
                        log.error("increase & reportFail id {} ",ifEventQueue.getId());
                    }
                }
            }
        }
    }

}