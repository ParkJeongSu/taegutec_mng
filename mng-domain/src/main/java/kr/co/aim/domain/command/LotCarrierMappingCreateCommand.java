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
public class LotCarrierMappingCreateCommand {
    private final TransactionInfo transactionInfo;
    private final Long id;
    private final String lotName;
    private final String carrierName;
    private final String orderId;
    private final String orderLineNumber;
    private final Long productionOrderId;
    private final String productionStatus;
    private final String processStatus;
    private final BigDecimal quantity;
    private final BigDecimal galQuantity;
    private final Long mngKey;
    private final LocalDateTime jobStartTime;
    private final LocalDateTime jobEndTime;
    private final String mantiRequestState;
    private final LocalDateTime mantiRequestTime;
    private final LocalDateTime mantiReplyTime;
    private final String rrnRequestState;
    private final LocalDateTime rrnRequestTime;
    private final LocalDateTime rrnReplyTime;
    private final String holdState;
    private final String reasonCode;
}