package kr.co.aim.common.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
@Builder
public class WorkOrderResponseDto {

    private Long id;
    private String workOrderName;
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


    @QueryProjection
    public WorkOrderResponseDto(
            Long id,
            String workOrderName,
            String description,
            String vendorName,
            String productDefName,
            String processFlowName,
            String processOperationName,
            String recipeName,
            String workOrderState,
            String holdState,
            String reasonCode,
            String equipmentName,
            Integer planQuantity,
            Integer createdQuantity,
            Integer releasedQuantity,
            Integer finishedQuantity,
            Integer scrappedQuantity,
            Integer workOrderCount,
            LocalDateTime createTime,
            LocalDateTime releaseTime,
            LocalDateTime completeTime,
            String createUser,
            String releaseUser,
            String completeUser,
            LocalDateTime dueDate,
            String eventName,
            LocalDateTime eventTime,
            String eventUser,
            String eventComment
    )
    {
        this.id = id;
        this.workOrderName = workOrderName;
        this.description = description;
        this.vendorName = vendorName;
        this.productDefName = productDefName;
        this.processFlowName = processFlowName;
        this.processOperationName = processOperationName;
        this.recipeName = recipeName;
        this.workOrderState = workOrderState;
        this.holdState = holdState;
        this.reasonCode = reasonCode;
        this.equipmentName = equipmentName;
        this.planQuantity = planQuantity;
        this.createdQuantity = createdQuantity;
        this.releasedQuantity = releasedQuantity;
        this.finishedQuantity = finishedQuantity;
        this.scrappedQuantity = scrappedQuantity;
        this.workOrderCount = workOrderCount;
        this.createTime = createTime;
        this.releaseTime = releaseTime;
        this.completeTime = completeTime;
        this.createUser = createUser;
        this.releaseUser = releaseUser;
        this.completeUser = completeUser;
        this.dueDate = dueDate;
        this.eventName = eventName;
        this.eventTime = eventTime;
        this.eventUser = eventUser;
        this.eventComment = eventComment;
    }
}