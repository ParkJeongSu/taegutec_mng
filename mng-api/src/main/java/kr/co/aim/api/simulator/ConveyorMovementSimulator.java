package kr.co.aim.api.simulator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@EnableScheduling
@Slf4j
@Profile({"web"})
@ConditionalOnProperty(name = "factory.type", havingValue = "insert")
public class ConveyorMovementSimulator {

    private final SimpMessagingTemplate messagingTemplate;

    // WS311 컨베이어 노드 이동 경로 정의 (IN CV -> 메인 버퍼 -> 작업대 -> OUT CV)
    private final String[] ws311Path = new String[]{
            "WH1-31101",
            "311-31101",
            "311-31104",
            "311-31111",
            "311-31114",
            "WH1-31114"

    };

    // 현재 WS311에서 가동 중인 트레이의 진행 단계 인덱스 (0 ~ 4, 5는 배출)
    private int currentStepIndex = 0;
    private final String activeCarrierName = "TRAY-311-01";

    public ConveyorMovementSimulator(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * 3초마다 컨베이어 트레이의 위치를 다음 노드로 전진 전송
     */
    @Scheduled(fixedRate = 3000)
    public void simulateTrayMove() {
        String destinationTopic = "/topic/warehouse/1/conveyor";

        // 마지막 스텝을 넘어가면 트레이 반출(REMOVE) 후 다시 처음부터 재시작
        if (currentStepIndex >= ws311Path.length) {
            ConveyorTrayMovementDto removeDto = ConveyorTrayMovementDto.builder()
                    .warehouseId("1")
                    .workStationId("311")
                    .carrierName(activeCarrierName)
                    .positionId(ws311Path[ws311Path.length - 1])
                    .action("REMOVE")
                    .state("COMPLETED")
                    .eventTime(LocalDateTime.now())
                    .build();

            this.messagingTemplate.convertAndSend(destinationTopic, removeDto);
            log.info("[CV Simulator] Tray removed from line: carrier={}", activeCarrierName);

            // 0번 위치(최초 진입)로 리셋
            currentStepIndex = 0;
            return;
        }

        String currentPos = ws311Path[currentStepIndex];
        String prevPos = (currentStepIndex > 0) ? ws311Path[currentStepIndex - 1] : null;
        String action = (currentStepIndex == 0) ? "ENTER" : "MOVE";

        ConveyorTrayMovementDto moveDto = ConveyorTrayMovementDto.builder()
                .warehouseId("1")
                .workStationId("311")
                .carrierName(activeCarrierName)
                .positionId(currentPos)
                .previousPosition(prevPos)
                .action(action)
                .state("MOVING")
                .eventTime(LocalDateTime.now())
                .build();

        this.messagingTemplate.convertAndSend(destinationTopic, moveDto);
        log.info("[CV Simulator] Sent tray movement to {}: carrier={}, step=[{}]",
                destinationTopic, activeCarrierName, currentPos);

        currentStepIndex++;
    }
}