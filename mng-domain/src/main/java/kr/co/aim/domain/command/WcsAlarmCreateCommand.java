package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsAlarmCreateCommand {

    private TransactionInfo transactionInfo;

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
}
