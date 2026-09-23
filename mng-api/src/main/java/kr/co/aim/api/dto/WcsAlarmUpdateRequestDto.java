package kr.co.aim.api.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsAlarmUpdateRequestDto {

    private String alarmLevel;
    private String alarmRecoveryOptions;
    private String alarmText;
    private String layerName;
    private Boolean systemAlarmFlag;

    private String eventName;
    private String eventUser;
    private String eventComment;
}
