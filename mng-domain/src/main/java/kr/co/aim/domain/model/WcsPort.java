package kr.co.aim.domain.model;

import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.WcsPortCreateCommand;
import kr.co.aim.domain.command.WcsPortUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsPort implements HasTransactionInfo {

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

    public static WcsPort create(WcsPortCreateCommand command) {
        LocalDateTime now = LocalDateTime.now();
        String eventName = "WcsPortCreated";
        String eventUser = "SYSTEM";
        String eventComment = "WcsPort created";
        LocalDateTime eventTime = now;

        if (command.getTransactionInfo() != null) {
            if (command.getTransactionInfo().eventName() != null && !command.getTransactionInfo().eventName().trim().isEmpty()) {
                eventName = command.getTransactionInfo().eventName().trim();
            }
            if (command.getTransactionInfo().eventUser() != null && !command.getTransactionInfo().eventUser().trim().isEmpty()) {
                eventUser = command.getTransactionInfo().eventUser().trim();
            }
            if (command.getTransactionInfo().eventComment() != null) {
                eventComment = command.getTransactionInfo().eventComment().trim();
            }
            if (command.getTransactionInfo().eventTime() != null) {
                eventTime = command.getTransactionInfo().eventTime();
            }
        }

        return WcsPort.builder()
                .equipmentName(command.getEquipmentName())
                .factoryName(command.getFactoryName())
                .localNo(command.getLocalNo())
                .portNumber(command.getPortNumber())
                .carrierName(command.getCarrierName())
                .errorHappen(command.getErrorHappen())
                .portContainStatus(command.getPortContainStatus())
                .portEnableMode(command.getPortEnableMode())
                .portName(command.getPortName())
                .portStatus(command.getPortStatus())
                .portTransferStatus(command.getPortTransferStatus())
                .portType(command.getPortType())
                .touchPanelNumber(command.getTouchPanelNumber())
                .zoneName(command.getZoneName())
                .bin(command.getBin() != null ? command.getBin() : 0)
                .row(command.getRow() != null ? command.getRow() : 0)
                .stage(command.getStage() != null ? command.getStage() : 0)
                .col(command.getCol() != null ? command.getCol() : 0)
                .linkEquipmentName(command.getLinkEquipmentName())
                .linkPortName(command.getLinkPortName())
                .linkPortType(command.getLinkPortType())
                .portTransferMode(command.getPortTransferMode())
                .portReadingEnableMode(command.getPortReadingEnableMode())
                .portDetailType(command.getPortDetailType())
                .rejectEquipmentName(command.getRejectEquipmentName())
                .rejectPortName(command.getRejectPortName())
                .useWorkerFlag(command.getUseWorkerFlag())
                .portUseType(command.getPortUseType())
                .portMode(command.getPortMode())
                .portOperationMode(command.getPortOperationMode())
                .portRfidEnableMode(command.getPortRfidEnableMode())
                .lastEventName(eventName)
                .lastEventTime(eventTime)
                .lastEventUser(eventUser)
                .lastEventComment(eventComment)
                .build();
    }

    public WcsPort update(WcsPortUpdateCommand command) {
        if (command.getCarrierName() != null) {
            this.carrierName = command.getCarrierName();
        }
        if (command.getErrorHappen() != null) {
            this.errorHappen = command.getErrorHappen();
        }
        if (command.getPortContainStatus() != null) {
            this.portContainStatus = command.getPortContainStatus();
        }
        if (command.getPortEnableMode() != null) {
            this.portEnableMode = command.getPortEnableMode();
        }
        if (command.getPortName() != null) {
            this.portName = command.getPortName();
        }
        if (command.getPortStatus() != null) {
            this.portStatus = command.getPortStatus();
        }
        if (command.getPortTransferStatus() != null) {
            this.portTransferStatus = command.getPortTransferStatus();
        }
        if (command.getPortType() != null) {
            this.portType = command.getPortType();
        }
        if (command.getTouchPanelNumber() != null) {
            this.touchPanelNumber = command.getTouchPanelNumber();
        }
        if (command.getZoneName() != null) {
            this.zoneName = command.getZoneName();
        }
        if (command.getBin() != null) {
            this.bin = command.getBin();
        }
        if (command.getRow() != null) {
            this.row = command.getRow();
        }
        if (command.getStage() != null) {
            this.stage = command.getStage();
        }
        if (command.getCol() != null) {
            this.col = command.getCol();
        }
        if (command.getLinkEquipmentName() != null) {
            this.linkEquipmentName = command.getLinkEquipmentName();
        }
        if (command.getLinkPortName() != null) {
            this.linkPortName = command.getLinkPortName();
        }
        if (command.getLinkPortType() != null) {
            this.linkPortType = command.getLinkPortType();
        }
        if (command.getPortTransferMode() != null) {
            this.portTransferMode = command.getPortTransferMode();
        }
        if (command.getPortReadingEnableMode() != null) {
            this.portReadingEnableMode = command.getPortReadingEnableMode();
        }
        if (command.getPortDetailType() != null) {
            this.portDetailType = command.getPortDetailType();
        }
        if (command.getRejectEquipmentName() != null) {
            this.rejectEquipmentName = command.getRejectEquipmentName();
        }
        if (command.getRejectPortName() != null) {
            this.rejectPortName = command.getRejectPortName();
        }
        if (command.getUseWorkerFlag() != null) {
            this.useWorkerFlag = command.getUseWorkerFlag();
        }
        if (command.getPortUseType() != null) {
            this.portUseType = command.getPortUseType();
        }
        if (command.getPortMode() != null) {
            this.portMode = command.getPortMode();
        }
        if (command.getPortOperationMode() != null) {
            this.portOperationMode = command.getPortOperationMode();
        }
        if (command.getPortRfidEnableMode() != null) {
            this.portRfidEnableMode = command.getPortRfidEnableMode();
        }

        if (command.getTransactionInfo() != null) {
            if (command.getTransactionInfo().eventName() != null && !command.getTransactionInfo().eventName().trim().isEmpty()) {
                this.lastEventName = command.getTransactionInfo().eventName().trim();
            }
            if (command.getTransactionInfo().eventUser() != null && !command.getTransactionInfo().eventUser().trim().isEmpty()) {
                this.lastEventUser = command.getTransactionInfo().eventUser().trim();
            }
            if (command.getTransactionInfo().eventComment() != null) {
                this.lastEventComment = command.getTransactionInfo().eventComment().trim();
            }
            this.lastEventTime = (command.getTransactionInfo().eventTime() != null) ? command.getTransactionInfo().eventTime() : LocalDateTime.now();
        }

        return this;
    }

    @Override
    public void setEventName(String v) {
        this.lastEventName = v;
    }

    @Override
    public void setEventTime(LocalDateTime v) {
        this.lastEventTime = v;
    }

    @Override
    public void setEventUser(String v) {
        this.lastEventUser = v;
    }

    @Override
    public void setEventComment(String v) {
        this.lastEventComment = v;
    }
}
