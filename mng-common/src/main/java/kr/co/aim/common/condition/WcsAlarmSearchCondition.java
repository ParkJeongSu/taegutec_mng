package kr.co.aim.common.condition;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsAlarmSearchCondition {

    private String factoryName;
    private String equipmentName;
    private String alarmId;
    private Integer layerNumber;
    private String layerType;
    private String alarmLevel;
    private String alarmRecoveryOptions;
    private String alarmText;
    private String layerName;
    private Boolean systemAlarmFlag;
}
