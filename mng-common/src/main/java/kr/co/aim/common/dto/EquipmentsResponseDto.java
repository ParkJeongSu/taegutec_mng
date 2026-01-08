package kr.co.aim.common.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
@Builder
public class EquipmentsResponseDto {

    private Long id;
    private String equipmentName;
    private Long equipmentDefId;
    private Long parentEquipmentId;
    private String equipmentLevel;
    private String equipmentState;
    private String communicationState;
    private Integer loadingCount;
    private Integer processCount;
    private String recipeName;
    private String holdState;
    private String reasonCode;
    private String resourceState;
    private String operationMode;
    private String messageServiceAddress;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
    private Long workOrderId;

    @QueryProjection
    public EquipmentsResponseDto(
                    Long id,
                    String equipmentName,
                    Long equipmentDefId,
                    Long parentEquipmentId,
                    String equipmentLevel,
                    String equipmentState,
                    String communicationState,
                    Integer loadingCount,
                    Integer processCount,
                    String recipeName,
                    String holdState,
                    String reasonCode,
                    String resourceState,
                    String operationMode,
                    String messageServiceAddress,
                    String eventName,
                    LocalDateTime eventTime,
                    String eventUser,
                    String eventComment,
                    Long workOrderId
    )
    {
        this.id = id;
        this.equipmentName = equipmentName;
        this.equipmentDefId = equipmentDefId;
        this.parentEquipmentId = parentEquipmentId;
        this.equipmentLevel = equipmentLevel;
        this.equipmentState = equipmentState;
        this.communicationState = communicationState;
        this.loadingCount = loadingCount;
        this.processCount = processCount;
        this.recipeName = recipeName;
        this.holdState = holdState;
        this.reasonCode = reasonCode;
        this.resourceState = resourceState;
        this.operationMode = operationMode;
        this.messageServiceAddress = messageServiceAddress;
        this.eventName = eventName;
        this.eventTime = eventTime;
        this.eventUser = eventUser;
        this.eventComment = eventComment;
        this.workOrderId = workOrderId;
    }
}