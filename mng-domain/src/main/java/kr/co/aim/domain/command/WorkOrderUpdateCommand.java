package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@RequiredArgsConstructor
@Builder
public class WorkOrderUpdateCommand {
    private final TransactionInfo transactionInfo;
    private final Long id;
    private final String workOrderName;
    private final String description;
    private final String vendorName;
    private final Long productDefId;
    private final String processFlowId;
    private final String processOperationId;
    private final String workOrderState;
    private final String holdState;
    private final String reasonCode;
    private final String equipmentName;
    private final Integer planQuantity;
    private final Integer createdQuantity;
    private final Integer releasedQuantity;
    private final Integer finishedQuantity;
    private final Integer scrappedQuantity;
    private final Integer workOrderCount;
    private final LocalDateTime createTime;
    private final LocalDateTime releaseTime;
    private final LocalDateTime completeTime;
    private final String createUser;
    private final String releaseUser;
    private final String completeUser;
    private final LocalDateTime dueDate;
}
