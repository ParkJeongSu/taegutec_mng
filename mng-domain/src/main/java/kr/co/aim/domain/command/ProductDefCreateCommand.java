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
public class ProductDefCreateCommand {
    private final TransactionInfo transactionInfo;
    private final Long id;
    private final String productDefName;
    private final String factoryName;
    private final String description1;
    private final String description2;
    private final BigDecimal ratio;
    private final BigDecimal defaultReceiveQuantity;
    private final BigDecimal toleranceVal;

}
