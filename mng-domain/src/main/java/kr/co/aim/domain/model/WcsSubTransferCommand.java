package kr.co.aim.domain.model;

import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.WcsSubTransferCommandCreateCommand;
import kr.co.aim.domain.command.WcsSubTransferCommandUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsSubTransferCommand implements HasTransactionInfo {

    private Integer jobNo;
    private String transferCommandName;

    private LocalDateTime createTime;
    private String currentCommandData;
    private String jobCompleteState;
    private Integer localNo;
    private String sourcePositionColumn;
    private String sourcePositionName;
    private String sourcePositionPortNo;
    private String sourcePositionRow;
    private String sourcePositionStage;
    private String sourcePositionBin;
    private String subCommandStatus;
    private String targetPositionColumn;
    private String targetPositionName;
    private String targetPositionPortNo;
    private String targetPositionRow;
    private String targetPositionStage;
    private String targetPositionBin;
    private String transferEquipmentName;
    private String transferType;
    private String transferUnitName;
    private Integer transferUnitNumber;
    private String factoryName;
    private LocalDateTime endTime;
    private LocalDateTime startTime;
    private Integer transferSpeed;
    private String loadType;
    private String carrierName;

    private String lastEventComment;
    private String lastEventName;
    private LocalDateTime lastEventTime;
    private String lastEventUser;

    public static WcsSubTransferCommand create(WcsSubTransferCommandCreateCommand command) {
        LocalDateTime now = LocalDateTime.now();
        String eventName = "WcsSubTransferCommandCreated";
        String eventUser = "SYSTEM";
        String eventComment = "WcsSubTransferCommand created";
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

        return WcsSubTransferCommand.builder()
                .jobNo(command.getJobNo())
                .transferCommandName(command.getTransferCommandName())
                .createTime(command.getCreateTime() != null ? command.getCreateTime() : now)
                .currentCommandData(command.getCurrentCommandData())
                .jobCompleteState(command.getJobCompleteState())
                .localNo(command.getLocalNo() != null ? command.getLocalNo() : 0)
                .sourcePositionColumn(command.getSourcePositionColumn())
                .sourcePositionName(command.getSourcePositionName())
                .sourcePositionPortNo(command.getSourcePositionPortNo())
                .sourcePositionRow(command.getSourcePositionRow())
                .sourcePositionStage(command.getSourcePositionStage())
                .sourcePositionBin(command.getSourcePositionBin())
                .subCommandStatus(command.getSubCommandStatus())
                .targetPositionColumn(command.getTargetPositionColumn())
                .targetPositionName(command.getTargetPositionName())
                .targetPositionPortNo(command.getTargetPositionPortNo())
                .targetPositionRow(command.getTargetPositionRow())
                .targetPositionStage(command.getTargetPositionStage())
                .targetPositionBin(command.getTargetPositionBin())
                .transferEquipmentName(command.getTransferEquipmentName())
                .transferType(command.getTransferType())
                .transferUnitName(command.getTransferUnitName())
                .transferUnitNumber(command.getTransferUnitNumber() != null ? command.getTransferUnitNumber() : 0)
                .factoryName(command.getFactoryName())
                .endTime(command.getEndTime())
                .startTime(command.getStartTime())
                .transferSpeed(command.getTransferSpeed())
                .loadType(command.getLoadType())
                .carrierName(command.getCarrierName())
                .lastEventName(eventName)
                .lastEventTime(eventTime)
                .lastEventUser(eventUser)
                .lastEventComment(eventComment)
                .build();
    }

    public WcsSubTransferCommand update(WcsSubTransferCommandUpdateCommand command) {
        if (command.getCreateTime() != null) {
            this.createTime = command.getCreateTime();
        }
        if (command.getCurrentCommandData() != null) {
            this.currentCommandData = command.getCurrentCommandData();
        }
        if (command.getJobCompleteState() != null) {
            this.jobCompleteState = command.getJobCompleteState();
        }
        if (command.getLocalNo() != null) {
            this.localNo = command.getLocalNo();
        }
        if (command.getSourcePositionColumn() != null) {
            this.sourcePositionColumn = command.getSourcePositionColumn();
        }
        if (command.getSourcePositionName() != null) {
            this.sourcePositionName = command.getSourcePositionName();
        }
        if (command.getSourcePositionPortNo() != null) {
            this.sourcePositionPortNo = command.getSourcePositionPortNo();
        }
        if (command.getSourcePositionRow() != null) {
            this.sourcePositionRow = command.getSourcePositionRow();
        }
        if (command.getSourcePositionStage() != null) {
            this.sourcePositionStage = command.getSourcePositionStage();
        }
        if (command.getSourcePositionBin() != null) {
            this.sourcePositionBin = command.getSourcePositionBin();
        }
        if (command.getSubCommandStatus() != null) {
            this.subCommandStatus = command.getSubCommandStatus();
        }
        if (command.getTargetPositionColumn() != null) {
            this.targetPositionColumn = command.getTargetPositionColumn();
        }
        if (command.getTargetPositionName() != null) {
            this.targetPositionName = command.getTargetPositionName();
        }
        if (command.getTargetPositionPortNo() != null) {
            this.targetPositionPortNo = command.getTargetPositionPortNo();
        }
        if (command.getTargetPositionRow() != null) {
            this.targetPositionRow = command.getTargetPositionRow();
        }
        if (command.getTargetPositionStage() != null) {
            this.targetPositionStage = command.getTargetPositionStage();
        }
        if (command.getTargetPositionBin() != null) {
            this.targetPositionBin = command.getTargetPositionBin();
        }
        if (command.getTransferEquipmentName() != null) {
            this.transferEquipmentName = command.getTransferEquipmentName();
        }
        if (command.getTransferType() != null) {
            this.transferType = command.getTransferType();
        }
        if (command.getTransferUnitName() != null) {
            this.transferUnitName = command.getTransferUnitName();
        }
        if (command.getTransferUnitNumber() != null) {
            this.transferUnitNumber = command.getTransferUnitNumber();
        }
        if (command.getFactoryName() != null) {
            this.factoryName = command.getFactoryName();
        }
        if (command.getEndTime() != null) {
            this.endTime = command.getEndTime();
        }
        if (command.getStartTime() != null) {
            this.startTime = command.getStartTime();
        }
        if (command.getTransferSpeed() != null) {
            this.transferSpeed = command.getTransferSpeed();
        }
        if (command.getLoadType() != null) {
            this.loadType = command.getLoadType();
        }
        if (command.getCarrierName() != null) {
            this.carrierName = command.getCarrierName();
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
