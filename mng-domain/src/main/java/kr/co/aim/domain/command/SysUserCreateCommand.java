package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class SysUserCreateCommand {
    private final TransactionInfo transactionInfo;
    private final String factoryName;
    private final String userId;
    private final String passwordHash;
    private final String userName;
    private final Long departmentId;
    private final String email;
    private final String phoneNumber;
    private final String userState;
}
