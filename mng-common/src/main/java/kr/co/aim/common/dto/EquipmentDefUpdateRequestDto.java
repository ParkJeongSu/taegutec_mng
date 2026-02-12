package kr.co.aim.common.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
public class EquipmentDefUpdateRequestDto {

    private Long id;
    private String equipmentDefName;
    private String description;
    private String equipmentType;
    private Long equipmentGroupId;
    private String detailEquipmentType;
    private String stateModel;
    private String vendorId;
    private String modelId;
    private Integer processCapacity;
    private Integer loadingCapacity;
    private String checkOutState;
    private LocalDateTime checkOutTime;
    private String checkOutUser;
    private String dataState;
    private String eventName;
    
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
    private String containerType;
}