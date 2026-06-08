package kr.co.aim.domain.model;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class PortDef {

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
