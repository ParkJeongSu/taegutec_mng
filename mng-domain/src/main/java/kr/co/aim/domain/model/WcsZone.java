package kr.co.aim.domain.model;

import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.WcsZoneCreateCommand;
import kr.co.aim.domain.command.WcsZoneUpdateCommand;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsZone implements HasTransactionInfo {

    private String factoryName;
    private String zoneName;

    private Boolean deepFirstFlag;
    private Integer frontRowInterval;
    private String loadType;
    private BigDecimal maxCapacityPercent;
    private Integer zoneCapacity;
    private String zoneColor;
    private Integer zoneSize;
    private String zoneType;
    private String shelfSelectMode;
    private BigDecimal useCapacityPercent;
    private Boolean waitingAreaFlag;

    private String lastEventComment;
    private String lastEventName;
    private LocalDateTime lastEventTime;
    private String lastEventUser;

    public static WcsZone create(WcsZoneCreateCommand command) {
        LocalDateTime now = LocalDateTime.now();
        String eventName = "WcsZoneCreated";
        String eventUser = "SYSTEM";
        String eventComment = "WcsZone created";
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

        return WcsZone.builder()
                .factoryName(command.getFactoryName())
                .zoneName(command.getZoneName())
                .deepFirstFlag(command.getDeepFirstFlag() != null ? command.getDeepFirstFlag() : Boolean.FALSE)
                .frontRowInterval(command.getFrontRowInterval())
                .loadType(command.getLoadType())
                .maxCapacityPercent(command.getMaxCapacityPercent())
                .zoneCapacity(command.getZoneCapacity())
                .zoneColor(command.getZoneColor())
                .zoneSize(command.getZoneSize())
                .zoneType(command.getZoneType())
                .shelfSelectMode(command.getShelfSelectMode())
                .useCapacityPercent(command.getUseCapacityPercent())
                .waitingAreaFlag(command.getWaitingAreaFlag() != null ? command.getWaitingAreaFlag() : Boolean.FALSE)
                .lastEventName(eventName)
                .lastEventTime(eventTime)
                .lastEventUser(eventUser)
                .lastEventComment(eventComment)
                .build();
    }

    public WcsZone update(WcsZoneUpdateCommand command) {
        if (command.getDeepFirstFlag() != null) {
            this.deepFirstFlag = command.getDeepFirstFlag();
        }
        if (command.getFrontRowInterval() != null) {
            this.frontRowInterval = command.getFrontRowInterval();
        }
        if (command.getLoadType() != null) {
            this.loadType = command.getLoadType();
        }
        if (command.getMaxCapacityPercent() != null) {
            this.maxCapacityPercent = command.getMaxCapacityPercent();
        }
        if (command.getZoneCapacity() != null) {
            this.zoneCapacity = command.getZoneCapacity();
        }
        if (command.getZoneColor() != null) {
            this.zoneColor = command.getZoneColor();
        }
        if (command.getZoneSize() != null) {
            this.zoneSize = command.getZoneSize();
        }
        if (command.getZoneType() != null) {
            this.zoneType = command.getZoneType();
        }
        if (command.getShelfSelectMode() != null) {
            this.shelfSelectMode = command.getShelfSelectMode();
        }
        if (command.getUseCapacityPercent() != null) {
            this.useCapacityPercent = command.getUseCapacityPercent();
        }
        if (command.getWaitingAreaFlag() != null) {
            this.waitingAreaFlag = command.getWaitingAreaFlag();
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
