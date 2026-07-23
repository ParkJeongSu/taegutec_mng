package kr.co.aim.common.condition;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EquipmentDefSearchCondition {
    private Long id;
    private String equipmentName;
    private String factoryName;
    private String description;
    private String equipmentType;
    private String equipmentGroupName;
    private String detailEquipmentType;
    private String vendorId;
    private String modelId;
    private Integer processCapacity;
    private String containerType;
    private String plcType;
    private String routeKey;
    private String serverName;
    private String checkOutState;
    private LocalDateTime checkOutTime;
    private String checkOutUser;
    private String dataState;
    private Integer localNo;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
}
