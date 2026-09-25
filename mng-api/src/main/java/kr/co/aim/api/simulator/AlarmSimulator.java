package kr.co.aim.api.simulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Component
@EnableScheduling
public class AlarmSimulator {

    private static final Logger log = LoggerFactory.getLogger(AlarmSimulator.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final Random random;
    private long sequenceNumber;

    // 설비 식별자 목록
    private static final String[] EQUIPMENT_LIST = new String[]{
            "STK-01", "STK-02", "CV-101", "CV-102", "CR-1", "RGV-01", "RGV-02"
    };

    // 알람 모의 데이터 셋 (코드, 타입, 레벨, 내용)
    private static final String[][] ALARM_PRESETS = new String[][]{
            {"ALM-1001", "HARDWARE", "CRITICAL", "Emergency Stop Activated"},
            {"ALM-1002", "SYSTEM", "WARN", "Carrier Battery Level Low (< 20%)"},
            {"ALM-1003", "PROCESS", "ERROR", "Tray Load Sensor Timeout"},
            {"ALM-1004", "COMMUNICATION", "CRITICAL", "PLC Connection Lost"},
            {"ALM-1005", "HARDWARE", "ERROR", "Fork Extension Limit Exceeded"},
            {"ALM-1006", "PROCESS", "WARN", "Transfer Wait Timeout Occurred"},
            {"ALM-1007", "COMMUNICATION", "WARN", "Barcode Scan Mismatch"},
            {"ALM-1008", "HARDWARE", "INFO", "Door Interlock Released"},
            {"ALM-1009", "PROCESS", "ERROR", "Conveyor Motor Overload Detected"},
            {"ALM-1010", "SYSTEM", "CRITICAL", "Storage Zone Full - Alternative Required"}
    };

    public AlarmSimulator(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.random = new Random();
        this.sequenceNumber = 1L;
    }

    /**
     * 5초마다 설비 알람을 발생시켜 WebSocket 구독 채널로 브로드캐스트
     */
    @Scheduled(fixedRate = 5000)
    public void simulateAlarm() {
        int eqIndex = this.random.nextInt(EQUIPMENT_LIST.length);
        String selectedEquipment = EQUIPMENT_LIST[eqIndex];

        int alarmIndex = this.random.nextInt(ALARM_PRESETS.length);
        String[] selectedAlarm = ALARM_PRESETS[alarmIndex];

        String alarmCode = selectedAlarm[0];
        String alarmType = selectedAlarm[1];
        String alarmLevel = selectedAlarm[2];
        String alarmText = selectedAlarm[3];

        String alarmId = "ALM-EVT-" + System.currentTimeMillis() + "-" + this.sequenceNumber;
        this.sequenceNumber = this.sequenceNumber + 1L;

        AlarmEventDto alarmDto = new AlarmEventDto(
                alarmId,
                selectedEquipment,
                alarmCode,
                alarmType,
                alarmLevel,
                alarmText,
                LocalDateTime.now()
        );

        String destinationTopic = "/topic/alarms";
        this.messagingTemplate.convertAndSend(destinationTopic, alarmDto);

        log.info("[Alarm Simulator] Broadcast alarm to {}: id={}, eq={}, code={}, level={}, text={}",
                destinationTopic,
                alarmDto.getAlarmId(),
                alarmDto.getEquipmentId(),
                alarmDto.getAlarmCode(),
                alarmDto.getAlarmLevel(),
                alarmDto.getAlarmText()
        );
    }
}