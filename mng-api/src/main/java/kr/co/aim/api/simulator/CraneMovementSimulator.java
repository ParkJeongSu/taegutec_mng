package kr.co.aim.api.simulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

//@Component
@EnableScheduling
public class CraneMovementSimulator {

    private static final Logger log = LoggerFactory.getLogger(CraneMovementSimulator.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final Random random;

    public CraneMovementSimulator(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.random = new Random();
    }

    @Scheduled(fixedRate = 3000)
    public void simulateCraneMove() {
        // 주행 레일 범위 (80 ~ 380) 내에서 랜덤 Y 좌표 생성
        int minY = 80;
        int maxY = 380;
        int randomY = minY + this.random.nextInt(maxY - minY + 1);

        CranePositionDto cranePositionDto = new CranePositionDto(
                "CR-1",
                200.0,
                (double) randomY,
                "MOVING",
                LocalDateTime.now()
        );

        String destinationTopic = "/topic/warehouse/1/crane";
        this.messagingTemplate.convertAndSend(destinationTopic, cranePositionDto);

        log.info("[Crane Simulator] Sent crane movement payload to {}: craneId={}, x={}, y={}, status={}",
                destinationTopic,
                cranePositionDto.getCraneId(),
                cranePositionDto.getX(),
                cranePositionDto.getY(),
                cranePositionDto.getStatus()
        );
    }
}
