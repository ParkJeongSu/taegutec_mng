package kr.co.aim.domain.model;

import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.WcsCraneCreateCommand;
import kr.co.aim.domain.command.WcsCraneUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsCrane implements HasTransactionInfo {

    private String craneName;
    private Integer craneNumber;
    private String factoryName;
    private Integer localNo;
    private String stockerName;

    private String autoRunStatus;
    private String carrierExist;
    private String carrierName;
    private String columnPosition;
    private String currentCmdData;
    private String errorHappen;
    private String forkPreStatus;
    private String forkStatus;
    private String jobCompleteState;
    private String mode;
    private String positionType;
    private String preStatus;
    private String rowPosition;
    private String stagePosition;
    private String status;
    private Integer touchPanelNumber;
    private String zoneName;
    private String craneReadingEnableMode;
    private String craneRfidEnableMode;
    private String portNoPosition;
    private Integer permissionRetryCount;
    private Integer permissionRetryTime;
    private Boolean opportunisticEnabled;

    private String lastEventComment;
    private String lastEventName;
    private LocalDateTime lastEventTime;
    private String lastEventUser;

    public static WcsCrane create(WcsCraneCreateCommand command) {
        LocalDateTime now = LocalDateTime.now();
        String eventName = "WcsCraneCreated";
        String eventUser = "SYSTEM";
        String eventComment = "WcsCrane created";
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

        return WcsCrane.builder()
                .craneName(command.getCraneName())
                .craneNumber(command.getCraneNumber())
                .factoryName(command.getFactoryName())
                .localNo(command.getLocalNo())
                .stockerName(command.getStockerName())
                .autoRunStatus(command.getAutoRunStatus())
                .carrierExist(command.getCarrierExist())
                .carrierName(command.getCarrierName())
                .columnPosition(command.getColumnPosition())
                .currentCmdData(command.getCurrentCmdData())
                .errorHappen(command.getErrorHappen())
                .forkPreStatus(command.getForkPreStatus())
                .forkStatus(command.getForkStatus())
                .jobCompleteState(command.getJobCompleteState())
                .mode(command.getMode())
                .positionType(command.getPositionType())
                .preStatus(command.getPreStatus())
                .rowPosition(command.getRowPosition())
                .stagePosition(command.getStagePosition())
                .status(command.getStatus())
                .touchPanelNumber(command.getTouchPanelNumber())
                .zoneName(command.getZoneName())
                .craneReadingEnableMode(command.getCraneReadingEnableMode())
                .craneRfidEnableMode(command.getCraneRfidEnableMode())
                .portNoPosition(command.getPortNoPosition())
                .permissionRetryCount(command.getPermissionRetryCount() != null ? command.getPermissionRetryCount() : 0)
                .permissionRetryTime(command.getPermissionRetryTime() != null ? command.getPermissionRetryTime() : 0)
                .opportunisticEnabled(command.getOpportunisticEnabled())
                .lastEventName(eventName)
                .lastEventTime(eventTime)
                .lastEventUser(eventUser)
                .lastEventComment(eventComment)
                .build();
    }

    public WcsCrane update(WcsCraneUpdateCommand command) {
        if (command.getAutoRunStatus() != null) {
            this.autoRunStatus = command.getAutoRunStatus();
        }
        if (command.getCarrierExist() != null) {
            this.carrierExist = command.getCarrierExist();
        }
        if (command.getCarrierName() != null) {
            this.carrierName = command.getCarrierName();
        }
        if (command.getColumnPosition() != null) {
            this.columnPosition = command.getColumnPosition();
        }
        if (command.getCurrentCmdData() != null) {
            this.currentCmdData = command.getCurrentCmdData();
        }
        if (command.getErrorHappen() != null) {
            this.errorHappen = command.getErrorHappen();
        }
        if (command.getForkPreStatus() != null) {
            this.forkPreStatus = command.getForkPreStatus();
        }
        if (command.getForkStatus() != null) {
            this.forkStatus = command.getForkStatus();
        }
        if (command.getJobCompleteState() != null) {
            this.jobCompleteState = command.getJobCompleteState();
        }
        if (command.getMode() != null) {
            this.mode = command.getMode();
        }
        if (command.getPositionType() != null) {
            this.positionType = command.getPositionType();
        }
        if (command.getPreStatus() != null) {
            this.preStatus = command.getPreStatus();
        }
        if (command.getRowPosition() != null) {
            this.rowPosition = command.getRowPosition();
        }
        if (command.getStagePosition() != null) {
            this.stagePosition = command.getStagePosition();
        }
        if (command.getStatus() != null) {
            this.status = command.getStatus();
        }
        if (command.getTouchPanelNumber() != null) {
            this.touchPanelNumber = command.getTouchPanelNumber();
        }
        if (command.getZoneName() != null) {
            this.zoneName = command.getZoneName();
        }
        if (command.getCraneReadingEnableMode() != null) {
            this.craneReadingEnableMode = command.getCraneReadingEnableMode();
        }
        if (command.getCraneRfidEnableMode() != null) {
            this.craneRfidEnableMode = command.getCraneRfidEnableMode();
        }
        if (command.getPortNoPosition() != null) {
            this.portNoPosition = command.getPortNoPosition();
        }
        if (command.getPermissionRetryCount() != null) {
            this.permissionRetryCount = command.getPermissionRetryCount();
        }
        if (command.getPermissionRetryTime() != null) {
            this.permissionRetryTime = command.getPermissionRetryTime();
        }
        if (command.getOpportunisticEnabled() != null) {
            this.opportunisticEnabled = command.getOpportunisticEnabled();
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
