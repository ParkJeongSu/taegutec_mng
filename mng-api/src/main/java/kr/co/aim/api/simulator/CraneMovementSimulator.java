package kr.co.aim.api.simulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
@EnableScheduling
public class CraneMovementSimulator {

    private static final Logger log = LoggerFactory.getLogger(CraneMovementSimulator.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final Random random;

    // 시뮬레이션 대상 전체 창고 목록
    private final String[] warehouseIds = new String[]{"1", "2", "3", "4", "5", "6", "7", "2.1", "2.2"};

    // 각 크레인의 현재 위치 및 이동 방향 상태 저장소 (0.0 ~ 1000.0)
    private final Map<String, Double> currentPositions = new HashMap<>();
    private final Map<String, Boolean> moveDirections = new HashMap<>(); // true: 증가, false: 감소

    public CraneMovementSimulator(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.random = new Random();

        // 초기 위치 및 주행 방향 설정
        for (int i = 0; i < warehouseIds.length; i++) {
            String whId = warehouseIds[i];
            currentPositions.put(whId, 100.0 + (this.random.nextDouble() * 800.0));
            moveDirections.put(whId, this.random.nextBoolean());
        }
    }

    @Scheduled(fixedRate = 2000)
    public void simulateCraneMove() {
        for (int i = 0; i < warehouseIds.length; i++) {
            String whId = warehouseIds[i];

            double currentPos = currentPositions.get(whId);
            boolean movingForward = moveDirections.get(whId);

            // 이동 보폭 (30.0 ~ 90.0)
            double step = 30.0 + (this.random.nextDouble() * 60.0);

            if (movingForward) {
                currentPos += step;
                if (currentPos >= 950.0) {
                    currentPos = 950.0;
                    moveDirections.put(whId, false); // 반대 방향 전환
                }
            } else {
                currentPos -= step;
                if (currentPos <= 50.0) {
                    currentPos = 50.0;
                    moveDirections.put(whId, true); // 반대 방향 전환
                }
            }
            currentPositions.put(whId, currentPos);

            // 주행 축(X 또는 Y)에 일괄 대응하도록 x, y 모두에 주행 위치 주입
            CranePositionDto cranePositionDto = new CranePositionDto(
                    "CR-" + whId,
                    currentPos,
                    currentPos,
                    "MOVING",
                    LocalDateTime.now()
            );

            String destinationTopic = "/topic/warehouse/" + whId + "/crane";
            this.messagingTemplate.convertAndSend(destinationTopic, cranePositionDto);

            log.debug("[Crane Simulator] Sent crane movement to {}: pos={}", destinationTopic, currentPos);
        }
    }
}