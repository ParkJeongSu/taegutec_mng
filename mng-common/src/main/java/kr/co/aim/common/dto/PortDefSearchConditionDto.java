package kr.co.aim.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PortDefSearchConditionDto {
    private Long id;
    private String equipmentName;
    private String portName;
    private String factoryName;
    private Integer portNumber;
    private String description;
    private String transportMode;
    private String portType;
    private String detailPortType;
    private String portUseType;
    private String portRoleType;
    private String workCenterName;
    private String locationId;
    private String connectedEquipmentName;
    private String connectedPortName;
    private String checkOutState;
    private LocalDateTime checkOutTime;
    private String checkOutUser;
    private String dataState;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
}
