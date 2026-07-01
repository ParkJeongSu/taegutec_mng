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
public class ProductionOrderDetailCreateCommand {
    private final TransactionInfo transactionInfo;
    private final Long id;
    private final Long productionOrderId;
    private final String orderId;
    private final String orderLineNumber;
    private final Integer seq;
    private final String carrierName;
    private final String jobState;
    private final BigDecimal allocatedQuantity;
    private final BigDecimal actualQuantity;
    private final LocalDateTime sendTime;
    private final LocalDateTime startTime;
    private final LocalDateTime completeTime;
    private final LocalDateTime createTime;
}