package kr.co.aim.domain.command;

import kr.co.aim.common.enums.AlarmState;
import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class AlarmCreateCommand {
    private final TransactionInfo transactionInfo;
    private final AlarmState alarmState;
    private final String equipmentName;
    private final Long alarmDefId;
}
