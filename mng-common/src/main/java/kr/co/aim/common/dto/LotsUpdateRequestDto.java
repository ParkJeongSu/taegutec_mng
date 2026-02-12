package kr.co.aim.common.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
public class LotsUpdateRequestDto {

    private Long id;
    private String lotName;
    private String productionType;
    private String lotState;
    private String processState;
    private String productDefId;
    private String processSpecId;
    private String processSpecVersion;
    private String processFlowId;
    private String processOperationId;
    private String workOrderId;
    private String equipmentName;
    private String portName;
    private String recipeName;
    private Long carrierId;
    private Integer priority;
    private String lotGrade;
    private String productionDetailType;
    private LocalDateTime planStartDate;
    private LocalDateTime planDueDate;
    private LocalDateTime createTime;
    private LocalDateTime releaseTime;
    private LocalDateTime shipTime;
    private LocalDateTime trackInTime;
    private LocalDateTime trackOutTime;
    private LocalDateTime operationMoveTime;
    private Integer quantity;
    private Integer oldQuantity;
    private String holdState;
    private String reworkState;
    private Integer reworkCount;
    private String originalProcessSpecId;
    private String originalProcessSpecVersion;
    private String returnProcessFlowId;
    private String returnProcessOperationId;
    private String reasonCode;
    private String ownerCode;
    private String eventName;
    
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
}