package kr.co.aim.common.condition;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsPortSearchCondition {

    private String factoryName;
    private String equipmentName;
    private Integer localNo;
    private Integer portNumber;
    private String portName;
    private String portStatus;
    private String carrierName;
    private String zoneName;
    private String portType;
    private String portContainStatus;
    private String portEnableMode;
    private String portTransferStatus;
    private String portTransferMode;
    private String portDetailType;
    private Boolean useWorkerFlag;
    private String portUseType;
    private String portReadingEnableMode;
    private String linkEquipmentName;
    private String linkPortName;
    private String linkPortType;
    private String errorHappen;
}
