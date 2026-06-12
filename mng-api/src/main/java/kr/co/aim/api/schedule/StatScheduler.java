package kr.co.aim.api.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.api.service.PortService;
import kr.co.aim.api.service.StatService;
import kr.co.aim.common.Utils.FormatUtils;
import kr.co.aim.common.Utils.JsonUtils;
import kr.co.aim.common.enums.MessageList;
import kr.co.aim.common.enums.PortTransportState;
import kr.co.aim.common.enums.ResultCode;
import kr.co.aim.common.enums.SystemName;
import kr.co.aim.common.format.LoadRequestBody;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.domain.model.Port;
import kr.co.aim.infra.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile({"scheduler"})
public class StatScheduler {

    private final StatService statService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 매일 새벽 2시에 어제 일자의 공장 가동 및 생산 통계 데이터를 집계/마감합니다.
     */
    @Scheduled(cron = "0 0 2 * * *")
    @SchedulerLock(
            name = "dailyStatAggregationLock",
            lockAtMostFor = "PT10M",  // 배치가 멈췄을 때 락이 유지되는 최대 시간 (10분)
            lockAtLeastFor = "PT1M"   // 다른 서버가 동시 실행되는 것을 방지하는 최소 시간 (1:59~2:01 간극 커버)
    )
    public void aggregateDailyStatistics() {
        log.info("== [START] 일일 생산/물류 통계 배치 집계 시작 ==");

        try {
            // 새벽 2시 실행 시점 기준, 어제 날짜(Yesterday) 계산 (ex: 20260610에 돌면 20260609 집계)
            LocalDate yesterday = LocalDate.now().minusDays(1);
            String statDate = yesterday.format(DATE_FORMATTER);

            log.info(">> 집계 대상 일자: {}", statDate);

            // 비즈니스 서비스 레이어로 마감 처리 위임
            statService.aggregateDailyStatistics(statDate);

            log.info("== [SUCCESS] 일일 생산/물류 통계 배치 집계 완료 (대상일자: {}) ==", statDate);
        } catch (Exception e) {
            log.error("== [FAIL] 일일 생산/물류 통계 배치 집계 중 치명적 오류 발생 ==", e);
            // 필요 시 알람 발송(SMS/Email) 이나 인프라 큐 통지 로직 추가 가능
        }
    }
}