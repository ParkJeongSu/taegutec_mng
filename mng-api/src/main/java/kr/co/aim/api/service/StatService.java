package kr.co.aim.api.service;

import kr.co.aim.common.Utils.FormatUtils;
import kr.co.aim.common.Utils.StatTimeUtils;
import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.domain.model.*;
import kr.co.aim.domain.repository.StatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
public class StatService {
    private final StatRepository statRepository;
    private final EquipmentService equipmentService;

    public void aggregateDailyStatistics(String statDate) {
        // 1. 설비 가동 시간 통계 산출 및 저장 (가장 까다로운 로직)
        calculateAndSaveAvailability(statDate);

        // 2. 생산성 실적 집계 및 저장 (Querydsl 대량 위임)
        statRepository.calculateAndSaveProductivity(statDate);

        // 3. 반송 경로 실적 집계 및 저장 (Querydsl 대량 위임)
        statRepository.calculateAndSaveTransportRoute(statDate);

        // 4. 작업 오더 최종 마감 집계 및 저장 (2번 productivity를 롤업 요약)
        statRepository.calculateAndSaveWorkOrderProcessed(statDate);
    }

    /**
     * 설비 이력을 분석하여 종료 시점 시간대에 가동/대기/다운/유지보수 시간을 몰아주는 핵심 비즈니스 로직
     */
    private void calculateAndSaveAvailability(String statDate) {
        LocalDateTime start = StatTimeUtils.toStartDateTime(statDate);
        LocalDateTime end = StatTimeUtils.toEndDateTime(statDate);

        // 대상 정렬 데이터 조회 (설비명 순, 이벤트 시간 순)
        List<EquipmentHistory> histories = equipmentService.findEquipmentHistoryByPeriod(start, end);

        // 메모리 상에서 설비별, 시간대별 통계를 누적하기 위한 데이터 맵 구조
        // 단일 PK 구조이므로 Key는 "날짜_시간_설비명" 조합 문자열을 고유 키로 활용
        Map<String, EquipmentAvailabilityHourly> aggregateMap = new HashMap<>();

        // 설비별 직전 이력을 추적하기 위한 캐싱 맵
        Map<String, EquipmentHistory> lastHistoryMap = new HashMap<>();

        for (EquipmentHistory current : histories) {
            String eqpName = current.getEquipmentName();
            EquipmentHistory previous = lastHistoryMap.get(eqpName);

            // 직전 이력이 존재할 때만 '종료 시점' 기준 몰아주기 연산 가능
            if (previous != null) {
                // 두 이벤트 사이의 지속 시간(초) 계산
                long seconds = Duration.between(previous.getEventTime(), current.getEventTime()).toSeconds();
                int duration = (int) seconds;

                // 종료(현재) 이벤트가 발생한 시점의 날짜와 시간(시) 추출
                LocalDateTime eventTime = current.getEventTime();
                String targetDate = eventTime.toLocalDate().format(FormatUtils.DATE_FORMATTER);
                String targetHour = eventTime.format(FormatUtils.TIME_FORMATTER);

                // 메모리 집계를 위한 그룹 키 조합 (예: "20260629_13_EQP01")
                String groupKey = targetDate + "_" + targetHour + "_" + eqpName;

                // 기존 누적 구조가 없으면 신규 생성 (이때 단일 PK인 TSID를 발급)
                EquipmentAvailabilityHourly stat = aggregateMap.get(groupKey);
                if (stat == null) {
                    Long nextTsid = TsidUtils.nextId();
                    stat = new EquipmentAvailabilityHourly(nextTsid, eqpName, eventTime);
                    aggregateMap.put(groupKey, stat);
                }

                // 직전 상태(Previous State)가 무엇이었냐에 따라 현재 시간대에 전체 딜레이를 몰아줌
                String prevState = previous.getEquipmentState();
                int run = 0, idle = 0, down = 0, pm = 0;

                if ("RUN".equalsIgnoreCase(prevState)) {
                    run = duration;
                } else if ("IDLE".equalsIgnoreCase(prevState)) {
                    idle = duration;
                } else if ("DOWN".equalsIgnoreCase(prevState)) {
                    down = duration;
                } else if ("PM".equalsIgnoreCase(prevState)) {
                    pm = duration;
                }

                // 엔티티 내부 편의 메서드를 통해 누적 계산 처리
                stat.addDurations(run, idle, down, pm, 0);
            }

            // 현재 이력을 다음 루프의 '직전 이력'으로 캐싱 교체
            lastHistoryMap.put(eqpName, current);
        }

        // 맵에 쌓인 통계 결과를 일괄 List로 전환 후 어댑터 레이어로 패스
        List<EquipmentAvailabilityHourly> resultList = new ArrayList<>(aggregateMap.values());
        if (!resultList.isEmpty()) {
            statRepository.saveAvailabilityAll(resultList);
        }
    }
}