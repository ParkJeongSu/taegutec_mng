package kr.co.aim.api.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsPortUpdateRequestDto {

    private String carrierName;
    private String errorHappen;
    private String portContainStatus;
    private String portEnableMode;
    private String portName;
    private String portStatus;
    private String portTransferStatus;
    private String portType;
    private Integer touchPanelNumber;
    private String zoneName;
    private Integer bin;
    private Integer row;
    private Integer stage;
    private Integer col;
    private String linkEquipmentName;
    private String linkPortName;
    private String linkPortType;
    private String portTransferMode;
    private String portReadingEnableMode;
    private String portDetailType;
    private String rejectEquipmentName;
    private String rejectPortName;
    private Boolean useWorkerFlag;
    private String portUseType;
    private String portMode;
    private String portOperationMode;
    private String portRfidEnableMode;

    private String eventName;
    private String eventUser;
    private String eventComment;
}
