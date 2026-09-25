package kr.co.aim.api.simulator;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class AlarmEventDto {

    private String alarmId;
    private String equipmentId;
    private String alarmCode;
    private String alarmType;
    private String alarmLevel;
    private String alarmText;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    public AlarmEventDto() {
    }

    public AlarmEventDto(String alarmId, String equipmentId, String alarmCode, String alarmType, String alarmLevel, String alarmText, LocalDateTime timestamp) {
        this.alarmId = alarmId;
        this.equipmentId = equipmentId;
        this.alarmCode = alarmCode;
        this.alarmType = alarmType;
        this.alarmLevel = alarmLevel;
        this.alarmText = alarmText;
        this.timestamp = timestamp;
    }

    public String getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getAlarmCode() {
        return alarmCode;
    }

    public void setAlarmCode(String alarmCode) {
        this.alarmCode = alarmCode;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public String getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(String alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public String getAlarmText() {
        return alarmText;
    }

    public void setAlarmText(String alarmText) {
        this.alarmText = alarmText;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "AlarmEventDto{" +
                "alarmId='" + alarmId + '\'' +
                ", equipmentId='" + equipmentId + '\'' +
                ", alarmCode='" + alarmCode + '\'' +
                ", alarmType='" + alarmType + '\'' +
                ", alarmLevel='" + alarmLevel + '\'' +
                ", alarmText='" + alarmText + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}