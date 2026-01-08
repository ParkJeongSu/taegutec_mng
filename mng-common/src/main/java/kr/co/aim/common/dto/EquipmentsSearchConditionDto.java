package kr.co.aim.common.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
public class EquipmentsSearchConditionDto {

    private Long id;
    private String equipmentName;
    private Long equipmentDefId;
    private Long parentEquipmentId;
    private String equipmentLevel;
    private String equipmentState;
    private String communicationState;
    private Integer processCount;
    private String recipeName;
    private String defaultStockerId;
    private String defaultZoneId;
    private String holdState;
    private String reasonCode;
    private String resourceState;
    private String operationMode;
    private String messageServiceAddress;
}