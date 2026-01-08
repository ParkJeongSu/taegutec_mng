package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@RequiredArgsConstructor
@Builder
public class AuthMenuCreateCommand {
    private final TransactionInfo transactionInfo;
    private final Long id;
    private final Long authorityId;
    private final Long systemDefId;
    private final Long menuId;
    private final String checkOutState;
    private final LocalDateTime checkOutTime;
    private final String checkOutUser;
    private final String dataState;
}
