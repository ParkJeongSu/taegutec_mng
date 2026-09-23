package kr.co.aim.api.dto;

import kr.co.aim.domain.model.WcsPort;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsPortResponse {

    private String equipmentName;
    private String factoryName;
    private Integer localNo;
    private Integer portNumber;

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

    private String lastEventComment;
    private String lastEventName;
    private LocalDateTime lastEventTime;
    private String lastEventUser;

    public static WcsPortResponse fromDomain(WcsPort domain) {
        if (domain == null) {
            return null;
        }

        return WcsPortResponse.builder()
                .equipmentName(domain.getEquipmentName())
                .factoryName(domain.getFactoryName())
                .localNo(domain.getLocalNo())
                .portNumber(domain.getPortNumber())
                .carrierName(domain.getCarrierName())
                .errorHappen(domain.getErrorHappen())
                .portContainStatus(domain.getPortContainStatus())
                .portEnableMode(domain.getPortEnableMode())
                .portName(domain.getPortName())
                .portStatus(domain.getPortStatus())
                .portTransferStatus(domain.getPortTransferStatus())
                .portType(domain.getPortType())
                .touchPanelNumber(domain.getTouchPanelNumber())
                .zoneName(domain.getZoneName())
                .bin(domain.getBin())
                .row(domain.getRow())
                .stage(domain.getStage())
                .col(domain.getCol())
                .linkEquipmentName(domain.getLinkEquipmentName())
                .linkPortName(domain.getLinkPortName())
                .linkPortType(domain.getLinkPortType())
                .portTransferMode(domain.getPortTransferMode())
                .portReadingEnableMode(domain.getPortReadingEnableMode())
                .portDetailType(domain.getPortDetailType())
                .rejectEquipmentName(domain.getRejectEquipmentName())
                .rejectPortName(domain.getRejectPortName())
                .useWorkerFlag(domain.getUseWorkerFlag())
                .portUseType(domain.getPortUseType())
                .portMode(domain.getPortMode())
                .portOperationMode(domain.getPortOperationMode())
                .portRfidEnableMode(domain.getPortRfidEnableMode())
                .lastEventComment(domain.getLastEventComment())
                .lastEventName(domain.getLastEventName())
                .lastEventTime(domain.getLastEventTime())
                .lastEventUser(domain.getLastEventUser())
                .build();
    }
}
