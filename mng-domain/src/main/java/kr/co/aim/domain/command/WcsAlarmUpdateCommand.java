package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsAlarmUpdateCommand {

    private TransactionInfo transactionInfo;

    private String alarmLevel;
    private String alarmRecoveryOptions;
    private String alarmText;
    private String layerName;
    private Boolean systemAlarmFlag;
}
