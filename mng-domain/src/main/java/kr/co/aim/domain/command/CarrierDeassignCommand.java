package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class CarrierDeassignCommand {
    private final TransactionInfo transactionInfo;
    private final String carrierName;
    private final String useState;
    private final Integer lotQuantity;
    private final Integer quantity;
    private final String capaState;
}
