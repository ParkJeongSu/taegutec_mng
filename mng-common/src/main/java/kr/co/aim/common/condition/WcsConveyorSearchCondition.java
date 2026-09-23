package kr.co.aim.common.condition;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsConveyorSearchCondition {

    private String factoryName;
    private String conveyorGroup;
    private String conveyorName;
    private Integer conveyorNumber;
    private Integer localNo;
    private String conveyorType;
    private String status;
    private String autoRunStatus;
    private String carrierName;
    private String carrierExist;
    private String areaName;
    private String direction;
    private String operationMode;
    private String machineTypeName;
    private String serverName;
    private String mode;
    private String onlineControlStatus;
    private String conveyorConnectionStatus;
}
