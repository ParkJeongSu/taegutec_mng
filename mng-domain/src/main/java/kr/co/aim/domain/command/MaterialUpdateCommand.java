package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
@Builder
public class MaterialUpdateCommand {
    private final TransactionInfo transactionInfo;
    private final String materialId;
    private final String description;
    private final Long materialClassId;
    private final Long materialGroupId;
    private final String carrierName;
    private final BigDecimal quantity;
}
