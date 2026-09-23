package kr.co.aim.api.dto;

import kr.co.aim.domain.model.WcsAlarm;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsAlarmResponse {

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

    public static WcsAlarmResponse fromDomain(WcsAlarm domain) {
        if (domain == null) {
            return null;
        }

        return WcsAlarmResponse.builder()
                .alarmId(domain.getAlarmId())
                .equipmentName(domain.getEquipmentName())
                .factoryName(domain.getFactoryName())
                .layerNumber(domain.getLayerNumber())
                .layerType(domain.getLayerType())
                .alarmLevel(domain.getAlarmLevel())
                .alarmRecoveryOptions(domain.getAlarmRecoveryOptions())
                .alarmText(domain.getAlarmText())
                .layerName(domain.getLayerName())
                .systemAlarmFlag(domain.getSystemAlarmFlag())
                .lastEventComment(domain.getLastEventComment())
                .lastEventName(domain.getLastEventName())
                .lastEventTime(domain.getLastEventTime())
                .lastEventUser(domain.getLastEventUser())
                .build();
    }
}
