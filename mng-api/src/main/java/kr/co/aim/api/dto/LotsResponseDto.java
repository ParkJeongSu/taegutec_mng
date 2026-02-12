package kr.co.aim.api.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
@Builder
public class LotsResponseDto {

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

    @QueryProjection
    public LotsResponseDto
            (
                    Long id,
                    String lotName,
                    String productionType,
                    String lotState,
                    String processState,
                    String productDefId,
                    String processSpecId,
                    String processSpecVersion,
                    String processFlowId,
                    String processOperationId,
                    String workOrderId,
                    String equipmentName,
                    String portName,
                    String recipeName,
                    Long carrierId,
                    Integer priority,
                    String lotGrade,
                    String productionDetailType,
                    LocalDateTime planStartDate,
                    LocalDateTime planDueDate,
                    LocalDateTime createTime,
                    LocalDateTime releaseTime,
                    LocalDateTime shipTime,
                    LocalDateTime trackInTime,
                    LocalDateTime trackOutTime,
                    LocalDateTime operationMoveTime,
                    Integer quantity,
                    Integer oldQuantity,
                    String holdState,
                    String reworkState,
                    Integer reworkCount,
                    String originalProcessSpecId,
                    String originalProcessSpecVersion,
                    String returnProcessFlowId,
                    String returnProcessOperationId,
                    String reasonCode,
                    String ownerCode,
                    String eventName,
                    
                    LocalDateTime eventTime,
                    String eventUser,
                    String eventComment
            ){
        this.id = id;
        this.lotName = lotName;
        this.productionType = productionType;
        this.lotState = lotState;
        this.processState = processState;
        this.productDefId = productDefId;
        this.processSpecId = processSpecId;
        this.processSpecVersion = processSpecVersion;
        this.processFlowId = processFlowId;
        this.processOperationId = processOperationId;
        this.workOrderId = workOrderId;
        this.equipmentName = equipmentName;
        this.portName = portName;
        this.recipeName = recipeName;
        this.carrierId = carrierId;
        this.priority = priority;
        this.lotGrade = lotGrade;
        this.productionDetailType = productionDetailType;
        this.planStartDate = planStartDate;
        this.planDueDate = planDueDate;
        this.createTime = createTime;
        this.releaseTime = releaseTime;
        this.shipTime = shipTime;
        this.trackInTime = trackInTime;
        this.trackOutTime = trackOutTime;
        this.operationMoveTime = operationMoveTime;
        this.quantity = quantity;
        this.oldQuantity = oldQuantity;
        this.holdState = holdState;
        this.reworkState = reworkState;
        this.reworkCount = reworkCount;
        this.originalProcessSpecId = originalProcessSpecId;
        this.originalProcessSpecVersion = originalProcessSpecVersion;
        this.returnProcessFlowId = returnProcessFlowId;
        this.returnProcessOperationId = returnProcessOperationId;
        this.reasonCode = reasonCode;
        this.ownerCode = ownerCode;
        this.eventName = eventName;
        
        this.eventTime = eventTime;
        this.eventUser = eventUser;
        this.eventComment = eventComment;
    }
}