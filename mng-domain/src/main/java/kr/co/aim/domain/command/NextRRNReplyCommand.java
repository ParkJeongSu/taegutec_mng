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
public class NextRRNReplyCommand {
    private final TransactionInfo transactionInfo;
    private final String orderId;
    private final String orderLineNumber;
    private final Long productionOrderId;
    private final Integer seq;
    private final String productionStatus;
    private final String processStatus;
    private final Long mngKey;
    private final String rrnRequestState;
    private final LocalDateTime rrnRequestTime;
    private final LocalDateTime rrnReplyTime;
    private final String nextEquipmentName;


}
