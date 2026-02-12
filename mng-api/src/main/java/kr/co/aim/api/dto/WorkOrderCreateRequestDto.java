package kr.co.aim.api.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
@Builder
@AllArgsConstructor
public class WorkOrderCreateRequestDto {
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
}