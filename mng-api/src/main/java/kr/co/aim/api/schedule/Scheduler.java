package kr.co.aim.api.schedule;

import kr.co.aim.api.service.TransportOrderFacade;
import kr.co.aim.api.service.IF_DB2WorkOrderService;
import kr.co.aim.api.service.IF_MSSQLWorkOrderService;
import kr.co.aim.infra.persistence.entitydb2.IF_WorkOrderEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("scheduler")
public class Scheduler {

    private final IF_DB2WorkOrderService if_db2WorkOrderService;
    private final IF_MSSQLWorkOrderService if_mssqlWorkOrderService;
    private final TransportOrderFacade transportOrderFacade;

    @Scheduled(fixedDelay = 5000) // 5초마다 실행
    @SchedulerLock(name = "DB2ToMSSQL",
            lockAtMostFor = "PT2M",     // 작업 최장 소요시간 + 버퍼
            lockAtLeastFor = "PT5S")    // 최소 간격(선택)
    public void DB2ToMSSQL() {
        // 1단계: DB2에서 처리할 데이터를 선점하고 가져온다. (DB2 트랜잭션)
        List<IF_WorkOrderEntity> db2WorkOrderlist = if_db2WorkOrderService.selectAndMarkAsProcessing();

        if (db2WorkOrderlist.isEmpty()) {
            return;
        }

        for(IF_WorkOrderEntity db2WorkOrder : db2WorkOrderlist) {
            try {
                if_mssqlWorkOrderService.transferWorkOrderToMSSQL(db2WorkOrder);
                if_db2WorkOrderService.updateDb2StatusToDoneInNewTransaction(db2WorkOrder.getId());
            } catch (Exception e) {
                log.error(db2WorkOrder.getWorkOrderName() + db2WorkOrder.getId() + "Error");
                if_db2WorkOrderService.updateDb2StatusToErrorInNewTransaction(db2WorkOrder.getId());
            }
        }
    }


//    @Scheduled(fixedDelay = 5000) // 5초마다 실행
//    @SchedulerLock(name = "transferData",
//            lockAtMostFor = "PT2M",     // 작업 최장 소요시간 + 버퍼
//            lockAtLeastFor = "PT5S")    // 최소 간격(선택)
//    public void transferData() {
//        dataTransferService.transferUsersToDb2();
//    }

}