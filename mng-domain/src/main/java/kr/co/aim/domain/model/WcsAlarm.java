package kr.co.aim.domain.model;

import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.WcsAlarmCreateCommand;
import kr.co.aim.domain.command.WcsAlarmUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsAlarm implements HasTransactionInfo {

    private String alarmId;
    private String equipmentName;
    private String factoryName;
    private Integer layerNumber;
    private String layerType;

    private String alarmLevel;
    private String alarmRecoveryOptions;
    private String alarmText;
    private String layerName;
    private Boolean systemAlarmFlag;

    private String lastEventComment;
    private String lastEventName;
    private LocalDateTime lastEventTime;
    private String lastEventUser;

    public static WcsAlarm create(WcsAlarmCreateCommand command) {
        LocalDateTime now = LocalDateTime.now();
        String eventName = "WcsAlarmCreated";
        String eventUser = "SYSTEM";
        String eventComment = "WcsAlarm created";
        LocalDateTime eventTime = now;

        if (command.getTransactionInfo() != null) {
            if (command.getTransactionInfo().eventName() != null && !command.getTransactionInfo().eventName().trim().isEmpty()) {
                eventName = command.getTransactionInfo().eventName().trim();
            }
            if (command.getTransactionInfo().eventUser() != null && !command.getTransactionInfo().eventUser().trim().isEmpty()) {
                eventUser = command.getTransactionInfo().eventUser().trim();
            }
            if (command.getTransactionInfo().eventComment() != null) {
                eventComment = command.getTransactionInfo().eventComment().trim();
            }
            if (command.getTransactionInfo().eventTime() != null) {
                eventTime = command.getTransactionInfo().eventTime();
            }
        }

        return WcsAlarm.builder()
                .alarmId(command.getAlarmId())
                .equipmentName(command.getEquipmentName())
                .factoryName(command.getFactoryName())
                .layerNumber(command.getLayerNumber())
                .layerType(command.getLayerType())
                .alarmLevel(command.getAlarmLevel())
                .alarmRecoveryOptions(command.getAlarmRecoveryOptions())
                .alarmText(command.getAlarmText())
                .layerName(command.getLayerName())
                .systemAlarmFlag(command.getSystemAlarmFlag())
                .lastEventName(eventName)
                .lastEventTime(eventTime)
                .lastEventUser(eventUser)
                .lastEventComment(eventComment)
                .build();
    }

    public WcsAlarm update(WcsAlarmUpdateCommand command) {
        if (command.getAlarmLevel() != null) {
            this.alarmLevel = command.getAlarmLevel();
        }
        if (command.getAlarmRecoveryOptions() != null) {
            this.alarmRecoveryOptions = command.getAlarmRecoveryOptions();
        }
        if (command.getAlarmText() != null) {
            this.alarmText = command.getAlarmText();
        }
        if (command.getLayerName() != null) {
            this.layerName = command.getLayerName();
        }
        if (command.getSystemAlarmFlag() != null) {
            this.systemAlarmFlag = command.getSystemAlarmFlag();
        }

        if (command.getTransactionInfo() != null) {
            if (command.getTransactionInfo().eventName() != null && !command.getTransactionInfo().eventName().trim().isEmpty()) {
                this.lastEventName = command.getTransactionInfo().eventName().trim();
            }
            if (command.getTransactionInfo().eventUser() != null && !command.getTransactionInfo().eventUser().trim().isEmpty()) {
                this.lastEventUser = command.getTransactionInfo().eventUser().trim();
            }
            if (command.getTransactionInfo().eventComment() != null) {
                this.lastEventComment = command.getTransactionInfo().eventComment().trim();
            }
            this.lastEventTime = (command.getTransactionInfo().eventTime() != null) ? command.getTransactionInfo().eventTime() : LocalDateTime.now();
        }

        return this;
    }

    @Override
    public void setEventName(String v) {
        this.lastEventName = v;
    }

    @Override
    public void setEventTime(LocalDateTime v) {
        this.lastEventTime = v;
    }

    @Override
    public void setEventUser(String v) {
        this.lastEventUser = v;
    }

    @Override
    public void setEventComment(String v) {
        this.lastEventComment = v;
    }
}
