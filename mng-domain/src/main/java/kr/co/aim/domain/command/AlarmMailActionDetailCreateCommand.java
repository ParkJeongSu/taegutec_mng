package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class AlarmMailActionDetailCreateCommand {
    private final TransactionInfo transactionInfo;
    private final Long id;
    private final Long alarmActionId;
    private final Long alarmActionUserGroupId;
    private final String subject;
    private final String contents;

}
