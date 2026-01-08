package kr.co.aim.api.schedule;

import kr.co.aim.api.service.DB2WorkOrderService;
import kr.co.aim.api.service.WorkOrderService;
import kr.co.aim.common.dto.WorkOrderCreateRequestDto;
import kr.co.aim.common.enums.HoldState;
import kr.co.aim.common.enums.WorkOrderState;
import kr.co.aim.infra.persistence.entitydb2.H2OrderdEntity;
import kr.co.aim.infra.persistence.entitydb2.H2TransEntity;
import kr.co.aim.infra.persistence.entitydb2.IdocEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("scheduler")
public class RequestCarrierScheduler {

    @Scheduled(fixedDelay = 5000) // 5초마다 실행
    @SchedulerLock(name = "RequestCarrierListToWMS",
            lockAtMostFor = "PT2M",     // 작업 최장 소요시간 + 버퍼
            lockAtLeastFor = "PT5S")    // 최소 간격(선택)
    public void RequestCarrierListToWMS() {
        /*
        * 1) create 상태인 work-order 조회
        * 
        * 2) work-order 별 request to WMS 어떤 식으로 I/F 하는지 협의 필요
        *
        * 3) I/F 후 carrier List를 받고 TaskJob과 TaskJobDetail 을 생성
        *
        * 
        * 
        * */

    }

}