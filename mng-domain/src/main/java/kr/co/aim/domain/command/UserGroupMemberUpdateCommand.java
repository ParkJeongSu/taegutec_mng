package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class UserGroupMemberUpdateCommand {
    private final TransactionInfo transactionInfo;
    private final String factoryName;
    private final Long userId;
    private final Long userGroupId;
}
