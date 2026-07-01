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
public class ProductionOrderCreateCommand {
    private final TransactionInfo transactionInfo;
    private final Long id;
    private final String orderId;
    private final String orderLineNumber;
    private final String lotName;
    private final String description;
    private final String itemName;
    private final String recipeName;
    private final String carrierName;
    private final Long idocId;
    private final Long h2OrderDpLineId;
    private final String galKey;
    private final Long mngKey;
    private final String productionOrderType;
    private final String productionOrderState;
    private final String reportState;
    private final String holdState;
    private final String reasonCode;
    private final String equipmentName;
    private final BigDecimal planQuantity;
    private final BigDecimal releasedQuantity;
    private final BigDecimal startedQuantity;
    private final BigDecimal endedQuantity;
    private final BigDecimal scrappedQuantity;
    private final LocalDateTime createTime;
    private final LocalDateTime releaseTime;
    private final LocalDateTime completeTime;
    private final LocalDateTime validationTime;
    private final String createUser;
    private final String releaseUser;
    private final String completeUser;
    private final LocalDateTime dueDate;
    private final String eventName;
    private final LocalDateTime eventTime;
    private final String eventUser;
    private final String eventComment;
}
