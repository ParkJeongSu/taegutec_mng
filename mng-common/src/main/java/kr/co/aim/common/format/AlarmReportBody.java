package kr.co.aim.common.format;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class AlarmReportBody {
    private String equipmentName;
    private String alarmCode;
    private String alarmState;
    private String alarmSeverity;
    private String alarmText;
}
