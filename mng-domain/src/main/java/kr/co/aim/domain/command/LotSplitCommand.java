package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
@Builder
public class LotSplitCommand {
    private final TransactionInfo transactionInfo;
    private final Long id;
    private final String lotName;
    private final String originalLotName;
    private final String lotStatus;
    private final String itemId;
    private final BigDecimal splitQuantity;
    private final String holdState;
    private final String reasonCode;
}