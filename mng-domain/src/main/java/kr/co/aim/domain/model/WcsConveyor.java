package kr.co.aim.domain.model;

import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.WcsConveyorCreateCommand;
import kr.co.aim.domain.command.WcsConveyorUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsConveyor implements HasTransactionInfo {

    private String conveyorGroup;
    private String conveyorName;
    private Integer conveyorNumber;
    private String factoryName;
    private Integer localNo;

    private String autoRunStatus;
    private String carrierExist;
    private String carrierName;
    private Integer conveyorGroupNumber;
    private String conveyorType;
    private String currentCmdData;
    private String direction;
    private String dispatchingPriority;
    private String errorHappen;
    private String jobCompleteState;
    private String onlineControlStatus;
    private String operationMode;
    private String preStatus;
    private Integer rtvNumber;
    private String status;
    private Integer touchPanelNumber;
    private String serverName;
    private String mode;
    private Integer downConveyorCount;
    private Integer onCarrierCount;
    private Integer totalConveyorCount;
    private Integer runConveyorCount;
    private String machineTypeName;
    private String readingEnableMode;
    private String rfidEnableMode;
    private String eqRouteKey;
    private String conveyorConnectionStatus;
    private String areaName;

    private String lastEventComment;
    private String lastEventName;
    private LocalDateTime lastEventTime;
    private String lastEventUser;

    public static WcsConveyor create(WcsConveyorCreateCommand command) {
        LocalDateTime now = LocalDateTime.now();
        String eventName = "WcsConveyorCreated";
        String eventUser = "SYSTEM";
        String eventComment = "WcsConveyor created";
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

        return WcsConveyor.builder()
                .conveyorGroup(command.getConveyorGroup())
                .conveyorName(command.getConveyorName())
                .conveyorNumber(command.getConveyorNumber())
                .factoryName(command.getFactoryName())
                .localNo(command.getLocalNo())
                .autoRunStatus(command.getAutoRunStatus())
                .carrierExist(command.getCarrierExist())
                .carrierName(command.getCarrierName())
                .conveyorGroupNumber(command.getConveyorGroupNumber())
                .conveyorType(command.getConveyorType())
                .currentCmdData(command.getCurrentCmdData())
                .direction(command.getDirection())
                .dispatchingPriority(command.getDispatchingPriority())
                .errorHappen(command.getErrorHappen())
                .jobCompleteState(command.getJobCompleteState())
                .onlineControlStatus(command.getOnlineControlStatus())
                .operationMode(command.getOperationMode())
                .preStatus(command.getPreStatus())
                .rtvNumber(command.getRtvNumber())
                .status(command.getStatus())
                .touchPanelNumber(command.getTouchPanelNumber())
                .serverName(command.getServerName())
                .mode(command.getMode())
                .downConveyorCount(command.getDownConveyorCount() != null ? command.getDownConveyorCount() : 0)
                .onCarrierCount(command.getOnCarrierCount() != null ? command.getOnCarrierCount() : 0)
                .totalConveyorCount(command.getTotalConveyorCount() != null ? command.getTotalConveyorCount() : 0)
                .runConveyorCount(command.getRunConveyorCount() != null ? command.getRunConveyorCount() : 0)
                .machineTypeName(command.getMachineTypeName())
                .readingEnableMode(command.getReadingEnableMode())
                .rfidEnableMode(command.getRfidEnableMode())
                .eqRouteKey(command.getEqRouteKey())
                .conveyorConnectionStatus(command.getConveyorConnectionStatus())
                .areaName(command.getAreaName())
                .lastEventName(eventName)
                .lastEventTime(eventTime)
                .lastEventUser(eventUser)
                .lastEventComment(eventComment)
                .build();
    }

    public WcsConveyor update(WcsConveyorUpdateCommand command) {
        if (command.getAutoRunStatus() != null) {
            this.autoRunStatus = command.getAutoRunStatus();
        }
        if (command.getCarrierExist() != null) {
            this.carrierExist = command.getCarrierExist();
        }
        if (command.getCarrierName() != null) {
            this.carrierName = command.getCarrierName();
        }
        if (command.getConveyorGroupNumber() != null) {
            this.conveyorGroupNumber = command.getConveyorGroupNumber();
        }
        if (command.getConveyorType() != null) {
            this.conveyorType = command.getConveyorType();
        }
        if (command.getCurrentCmdData() != null) {
            this.currentCmdData = command.getCurrentCmdData();
        }
        if (command.getDirection() != null) {
            this.direction = command.getDirection();
        }
        if (command.getDispatchingPriority() != null) {
            this.dispatchingPriority = command.getDispatchingPriority();
        }
        if (command.getErrorHappen() != null) {
            this.errorHappen = command.getErrorHappen();
        }
        if (command.getJobCompleteState() != null) {
            this.jobCompleteState = command.getJobCompleteState();
        }
        if (command.getOnlineControlStatus() != null) {
            this.onlineControlStatus = command.getOnlineControlStatus();
        }
        if (command.getOperationMode() != null) {
            this.operationMode = command.getOperationMode();
        }
        if (command.getPreStatus() != null) {
            this.preStatus = command.getPreStatus();
        }
        if (command.getRtvNumber() != null) {
            this.rtvNumber = command.getRtvNumber();
        }
        if (command.getStatus() != null) {
            this.status = command.getStatus();
        }
        if (command.getTouchPanelNumber() != null) {
            this.touchPanelNumber = command.getTouchPanelNumber();
        }
        if (command.getServerName() != null) {
            this.serverName = command.getServerName();
        }
        if (command.getMode() != null) {
            this.mode = command.getMode();
        }
        if (command.getDownConveyorCount() != null) {
            this.downConveyorCount = command.getDownConveyorCount();
        }
        if (command.getOnCarrierCount() != null) {
            this.onCarrierCount = command.getOnCarrierCount();
        }
        if (command.getTotalConveyorCount() != null) {
            this.totalConveyorCount = command.getTotalConveyorCount();
        }
        if (command.getRunConveyorCount() != null) {
            this.runConveyorCount = command.getRunConveyorCount();
        }
        if (command.getMachineTypeName() != null) {
            this.machineTypeName = command.getMachineTypeName();
        }
        if (command.getReadingEnableMode() != null) {
            this.readingEnableMode = command.getReadingEnableMode();
        }
        if (command.getRfidEnableMode() != null) {
            this.rfidEnableMode = command.getRfidEnableMode();
        }
        if (command.getEqRouteKey() != null) {
            this.eqRouteKey = command.getEqRouteKey();
        }
        if (command.getConveyorConnectionStatus() != null) {
            this.conveyorConnectionStatus = command.getConveyorConnectionStatus();
        }
        if (command.getAreaName() != null) {
            this.areaName = command.getAreaName();
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
