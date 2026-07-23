package kr.co.aim.common.condition;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PortDefSearchCondition {
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
}
