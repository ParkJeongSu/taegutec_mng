package kr.co.aim.common.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
@Builder
public class EquipmentDefResponseDto {

    private Long id;
    private String equipmentDefName;
    private String description;
    private String equipmentType;
    private Long equipmentGroupId;
    private String detailEquipmentType;
    private String vendorId;
    private String modelId;
    private Integer processCapacity;
    private String checkOutState;
    private LocalDateTime checkOutTime;
    private String checkOutUser;
    private String dataState;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
    private String containerType;

    @QueryProjection
    public EquipmentDefResponseDto(
            Long id,
            String equipmentDefName,
            String description,
            String equipmentType,
            Long equipmentGroupId,
            String detailEquipmentType,
            String vendorId,
            String modelId,
            Integer processCapacity,
            String checkOutState,
            LocalDateTime checkOutTime,
            String checkOutUser,
            String dataState,
            String eventName,
            LocalDateTime eventTime,
            String eventUser,
            String eventComment,
            String containerType
    )
    {
        this.id = id;
        this.equipmentDefName = equipmentDefName;
        this.description = description;
        this.equipmentType = equipmentType;
        this.equipmentGroupId = equipmentGroupId;
        this.detailEquipmentType = detailEquipmentType;
        this.vendorId = vendorId;
        this.modelId = modelId;
        this.processCapacity = processCapacity;
        this.checkOutState = checkOutState;
        this.checkOutTime = checkOutTime;
        this.checkOutUser = checkOutUser;
        this.dataState = dataState;
        this.eventName = eventName;
        this.eventTime = eventTime;
        this.eventUser = eventUser;
        this.eventComment = eventComment;
        this.containerType = containerType;
    }
}