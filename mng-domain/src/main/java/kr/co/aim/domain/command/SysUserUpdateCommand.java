package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Builder
public class SysUserUpdateCommand {
    private final TransactionInfo transactionInfo;
    private final String factoryName;
    private final String passwordHash;
    private final String userName;
    private final Long departmentId;
    private final String email;
    private final String phoneNumber;
    private final String userState;
    private final Integer failedLoginCount;
    private final LocalDateTime passwordChangeTime;
}
