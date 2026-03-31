package kr.co.aim.api.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//@Component
@Slf4j
@RequiredArgsConstructor
@Profile("scheduler")
public class WorkOrderScheduler {

    //private final DB2TransportOrderService db2TransportOrderService;
    //private final WorkOrderService workOrderService;

    @Scheduled(fixedDelay = 5000) // 5초마다 실행
    @SchedulerLock(name = "WorkOrderFromDB2ToMSSQL",
            lockAtMostFor = "PT2M",     // 작업 최장 소요시간 + 버퍼
            lockAtLeastFor = "PT5S")    // 최소 간격(선택)
    public void WorkOrderFromDB2ToMSSQL() {
        // 1단계: DB2에서 처리할 데이터를 선점하고 가져온다. (DB2 트랜잭션)
        //List<IdocEntity> idocEntityList = db2TransportOrderService.selectIdocList();
        /*
        if (idocEntityList.isEmpty()) {
            return;
        }
        */
        /*
        for(IdocEntity idocEntity : idocEntityList) {
            try {
                H2OrderDEntity h2OrderdEntity = db2TransportOrderService.selectH2OrderdByLineId(idocEntity.getLineId());
                H2TransEntity h2TransEntity = db2TransportOrderService.selectH2TransByLineId(idocEntity.getLineId());

                WorkOrderCreateRequestDto workOrderCreateRequestDto =
                        WorkOrderCreateRequestDto.builder()
                                .workOrderName(h2OrderdEntity.getOrder())
                                .lotName(h2OrderdEntity.getLot().toString())
                                //.description()
                                //.vendorName()
                                //.productDefName()
                                //.processFlowName()
                                .processOperationName(h2OrderdEntity.getRrn().toString())
                                //.recipeName()
                                .workOrderState(WorkOrderState.CREATED.getValue())
                                .holdState(HoldState.NOT_ON_HOLD.getValue())
                                //.reasonCode()
                                .equipmentName(h2OrderdEntity.getMachine())
                                .planQuantity(h2OrderdEntity.getQty().intValue())
                                .createdQuantity(h2OrderdEntity.getQty().intValue())
                                //.releasedQuantity()
                                //.finishedQuantity()
                                //.scrappedQuantity()
                                //.workOrderCount()
                                .createTime(LocalDateTime.now())
                                //.releaseTime()
                                //.completeTime()
                                .createUser("MNG")
                                //.releaseUser()
                                //.completeUser()
                                //.dueDate()
                                .eventUser("MNG")
                                .eventComment("DB2 -> MSSQL DB Transfer")
                                .build();

                workOrderService.createWorkOrder(workOrderCreateRequestDto);
                db2WorkOrderService.updateDb2StatusToDoneInNewTransaction(idocEntity.getLineId());

                log.info("LineId : {} transfer completed",idocEntity.getLineId());
            } catch (Exception e) {
                log.error(idocEntity.getLineId() + "Error");
                // TODO: 만일 가져오는데 실패하면 어떤 transactionCode 로 작성할건지 고민
            }
        }
        */
    }

}