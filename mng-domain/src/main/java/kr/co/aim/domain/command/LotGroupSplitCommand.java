package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
@Builder
public class LotGroupSplitCommand {
    private final TransactionInfo transactionInfo;
    private final Long id;
    private final String lotGroupName;
    private final String originalLotGroupName;
    private final String lotGroupStatus;
    private final String itemId;
    private final BigDecimal splitQuantity;
    private final String holdState;
    private final String reasonCode;
}