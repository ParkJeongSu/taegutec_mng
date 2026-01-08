package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Getter
@RequiredArgsConstructor
@Builder
public class AlarmDefCreateCommand {
    private final TransactionInfo transactionInfo;
    private final Long id;
    private final String alarmDefName;
    private final String alarmType;
    private final String description;
    private final String alarmLevel;
}
