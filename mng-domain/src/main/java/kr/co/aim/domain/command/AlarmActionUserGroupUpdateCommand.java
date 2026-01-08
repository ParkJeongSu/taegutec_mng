package kr.co.aim.domain.command;

import kr.co.aim.common.enums.AlarmState;
import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Getter
@RequiredArgsConstructor
@Builder
public class AlarmActionUserGroupUpdateCommand {
    private final TransactionInfo transactionInfo;
    private final Long id;
    private final String userGroupName;
}
