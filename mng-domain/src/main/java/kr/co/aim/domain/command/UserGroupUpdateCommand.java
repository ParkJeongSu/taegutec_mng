package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class UserGroupUpdateCommand {
    private final TransactionInfo transactionInfo;
    private final String factoryName;
    private final String userGroupName;
    private final String description;
    private final String useState;
}
