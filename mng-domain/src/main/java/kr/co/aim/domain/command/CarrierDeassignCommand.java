package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
@Builder
public class CarrierDeassignCommand {
    private final TransactionInfo transactionInfo;
    private final String carrierName;
    private final String useState;
    private final BigDecimal quantity;
    private final BigDecimal galQuantity;
    private final String capaState;
}
