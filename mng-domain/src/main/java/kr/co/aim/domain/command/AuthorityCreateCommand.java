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
public class AuthorityCreateCommand {
    private final TransactionInfo transactionInfo;
    private final Long id;
    private final String authorityName;
    private final String description;
    private final String checkOutState;
    private final LocalDateTime checkOutTime;
    private final String checkOutUser;
    private final String dataState;

}
