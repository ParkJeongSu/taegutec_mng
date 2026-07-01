package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Builder
public class LotCreateCommand {
    private final TransactionInfo transactionInfo;
    private final Long id;
    private final String lotName;
    private final String originalLotName;
    private final String lotStatus;
    private final String itemId;
    private final BigDecimal totalQuantity;
    private final String holdState;
    private final String reasonCode;
}