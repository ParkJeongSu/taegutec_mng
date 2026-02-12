package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Builder
public class AlarmActionUpdateCommand {
    private final TransactionInfo transactionInfo;
    private final Long id;
    private final String alarmActionName;
    private final String actionType;
    private final Long alarmDefId;
    private final String description;
    private final String dataState;
    private final String checkOutState;
    private final LocalDateTime checkOutTime;
    private final String checkOutUser;
}
