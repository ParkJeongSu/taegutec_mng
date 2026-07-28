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
public class ProcessJobEndedCommand {
    private final TransactionInfo transactionInfo;
    private final String equipmentName;
    private final String recipeName;
    private final String lotName;
    private final String itemName;
    private final String carrierName;
    private final String orderId;
    private final String orderLineNumber;
    private final String productionTaskEnd;
    private final Long productionOrderId;
    private final String productionStatus;
    private final String processStatus;
    private final BigDecimal quantity;
    private final Long mngKey;
    private final LocalDateTime jobEndTime;
}
