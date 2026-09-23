package kr.co.aim.domain.model;

import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.WcsShelfCreateCommand;
import kr.co.aim.domain.command.WcsShelfUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsShelf implements HasTransactionInfo {

    private String factoryName;
    private String shelfName;
    private String stockerName;

    private Integer abnormalStageNumber;
    private Integer bin;
    private String carrierName;
    private Integer col;
    private String lastTransferCmdName;
    private Integer numberOfUses;
    private Integer row;
    private String shelfEnableMode;
    private String shelfStatus;
    private String shelfTransferStatus;
    private String shelfType;
    private Integer stage;
    private String zoneName;

    private String lastEventComment;
    private String lastEventName;
    private LocalDateTime lastEventTime;
    private String lastEventUser;

    public static WcsShelf create(WcsShelfCreateCommand command) {
        LocalDateTime now = LocalDateTime.now();
        String eventName = "WcsShelfCreated";
        String eventUser = "SYSTEM";
        String eventComment = "WcsShelf created";
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

        return WcsShelf.builder()
                .factoryName(command.getFactoryName())
                .shelfName(command.getShelfName())
                .stockerName(command.getStockerName())
                .abnormalStageNumber(command.getAbnormalStageNumber())
                .bin(command.getBin())
                .carrierName(command.getCarrierName())
                .col(command.getCol())
                .lastTransferCmdName(command.getLastTransferCmdName())
                .numberOfUses(command.getNumberOfUses())
                .row(command.getRow())
                .shelfEnableMode(command.getShelfEnableMode())
                .shelfStatus(command.getShelfStatus())
                .shelfTransferStatus(command.getShelfTransferStatus())
                .shelfType(command.getShelfType())
                .stage(command.getStage())
                .zoneName(command.getZoneName())
                .lastEventName(eventName)
                .lastEventTime(eventTime)
                .lastEventUser(eventUser)
                .lastEventComment(eventComment)
                .build();
    }

    public WcsShelf update(WcsShelfUpdateCommand command) {
        if (command.getAbnormalStageNumber() != null) {
            this.abnormalStageNumber = command.getAbnormalStageNumber();
        }
        if (command.getBin() != null) {
            this.bin = command.getBin();
        }
        if (command.getCarrierName() != null) {
            this.carrierName = command.getCarrierName();
        }
        if (command.getCol() != null) {
            this.col = command.getCol();
        }
        if (command.getLastTransferCmdName() != null) {
            this.lastTransferCmdName = command.getLastTransferCmdName();
        }
        if (command.getNumberOfUses() != null) {
            this.numberOfUses = command.getNumberOfUses();
        }
        if (command.getRow() != null) {
            this.row = command.getRow();
        }
        if (command.getShelfEnableMode() != null) {
            this.shelfEnableMode = command.getShelfEnableMode();
        }
        if (command.getShelfStatus() != null) {
            this.shelfStatus = command.getShelfStatus();
        }
        if (command.getShelfTransferStatus() != null) {
            this.shelfTransferStatus = command.getShelfTransferStatus();
        }
        if (command.getShelfType() != null) {
            this.shelfType = command.getShelfType();
        }
        if (command.getStage() != null) {
            this.stage = command.getStage();
        }
        if (command.getZoneName() != null) {
            this.zoneName = command.getZoneName();
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
