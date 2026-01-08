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
public class PortDefUpdateCommand {
    private final TransactionInfo transactionInfo;
    private final Long id;
    private final String portDefName;
    private final String description;
    private final String portType;
    private final String portUseType;
    private final Long useCarrierDefId;
    private final String checkOutState;
    private final LocalDateTime checkOutTime;
    private final String checkOutUser;
    private final String dataState;
}
