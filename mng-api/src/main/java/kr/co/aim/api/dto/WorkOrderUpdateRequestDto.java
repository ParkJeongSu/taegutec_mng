package kr.co.aim.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
@AllArgsConstructor
public class WorkOrderUpdateRequestDto {

    private Long id;
    private String workOrderName;
    private String description;
    private String vendorName;
    private Long productDefId;
    private String processFlowId;
    private String processOperationId;
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
}