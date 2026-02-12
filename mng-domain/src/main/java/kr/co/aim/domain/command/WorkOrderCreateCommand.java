package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Builder
public class WorkOrderCreateCommand {
    private final TransactionInfo transactionInfo;
    private final Long id;
    private final String workOrderName;
    private final String lotName;
    private final String description;
    private final String vendorName;
    private final String productDefName;
    private final String processFlowName;
    private final String processOperationName;
    private final String recipeName;
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
    private final String eventName;
    private final LocalDateTime eventTime;
    private final String eventUser;
    private final String eventComment;
}
