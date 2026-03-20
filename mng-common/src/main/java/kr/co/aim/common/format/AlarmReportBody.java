package kr.co.aim.common.format;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmReportBody {
    private String equipmentName;
    private String alarmCode;
    private String alarmState;
    private String alarmSeverity;
    private String alarmText;
}
