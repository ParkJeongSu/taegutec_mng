package kr.co.aim.domain.model;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.WorkOrderCreateCommand;
import kr.co.aim.domain.command.WorkOrderUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkOrder implements HasTransactionInfo {

    private Long id;
    private String workOrderName;
    private String lotName;
    private String description;
    private String vendorName;
    private String productDefName;
    private String processFlowName;
    private String processOperationName;
    private String recipeName;
    private String workOrderState;
    private String holdState;
    private String reasonCode;
    private String equipmentName;
    private Integer planQuantity;
    private Integer createdQuantity;
    private Integer releasedQuantity;
    private Integer finishedQuantity;
    private Integer scrappedQuantity;
    private Integer workOrderCount;
    private LocalDateTime createTime;
    private LocalDateTime releaseTime;
    private LocalDateTime completeTime;
    private String createUser;
    private String releaseUser;
    private String completeUser;
    private LocalDateTime dueDate;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static WorkOrder create(WorkOrderCreateCommand command){
        return WorkOrder.builder()
                .id(TsidUtils.nextId())
                .workOrderName(command.getWorkOrderName())
                .lotName(command.getLotName())
                .description(command.getDescription())
                .vendorName(command.getVendorName())
                .productDefName(command.getProductDefName())
                .processFlowName(command.getProcessFlowName())
                .processOperationName(command.getProcessOperationName())
                .recipeName(command.getRecipeName())
                .workOrderState(command.getWorkOrderState())
                .holdState(command.getHoldState())
                .reasonCode(command.getReasonCode())
                .equipmentName(command.getEquipmentName())
                .planQuantity(command.getPlanQuantity())
                .createdQuantity(command.getCreatedQuantity())
                .releasedQuantity(command.getReleasedQuantity())
                .finishedQuantity(command.getFinishedQuantity())
                .scrappedQuantity(command.getScrappedQuantity())
                .workOrderCount(command.getWorkOrderCount())
                .createTime(command.getCreateTime())
                .releaseTime(command.getReleaseTime())
                .completeUser(command.getCompleteUser())
                .dueDate(command.getDueDate())
                .eventName(command.getTransactionInfo().eventName())
                .eventTime(command.getTransactionInfo().eventTime())
                .eventUser(command.getTransactionInfo().eventUser())
                .eventComment(command.getTransactionInfo().eventComment()).
                build();
    }
    public void changeWorkOrder(WorkOrderUpdateCommand command){
        this.apply(command.getTransactionInfo());
    }

}
