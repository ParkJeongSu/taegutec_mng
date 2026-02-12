package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Builder
public class LotsCreateCommand {
    private final TransactionInfo transactionInfo;
    private final Long id;
    private final String lotName;
    private final String productionType;
    private final String lotState;
    private final String processState;
    // TODO: 현재 MNG 에서 spec, flow 등을 관리 안함 그래서 아래의 컬럼을 단순히 Name으로 변경
    private final String productDefId;
    private final String processSpecId;
    private final String processSpecVersion;
    private final String processFlowId;
    private final String processOperationId;
    private final String workOrderId; // TODO: Long type 으로 변경
    private final String equipmentName;
    private final String portName;
    private final String recipeName;
    private final Long carrierId;
    private final Integer priority;
    private final String lotGrade;
    private final String productionDetailType;
    private final LocalDateTime planStartDate;
    private final LocalDateTime planDueDate;
    private final LocalDateTime createTime;
    private final LocalDateTime releaseTime;
    private final LocalDateTime shipTime;
    private final LocalDateTime trackInTime;
    private final LocalDateTime trackOutTime;
    private final LocalDateTime operationMoveTime;
    private final Integer quantity;
    private final Integer oldQuantity;
    private final String holdState;
    private final String reworkState;
    private final Integer reworkCount;
    private final String originalProcessSpecId;
    private final String originalProcessSpecVersion;
    private final String returnProcessFlowId;
    private final String returnProcessOperationId;
    private final String reasonCode;
    private final String ownerCode;
}
