package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
@Builder
public class UnLoadRequestCommand {
    private final TransactionInfo transactionInfo;
    private final String equipmentName;
    private final String portName;
    private final String carrierName;
    private final String carrierTransportState;
    private final BigDecimal quantity;

}
