package kr.co.aim.domain.model;

import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.WcsAlternativeStorageZoneCreateCommand;
import kr.co.aim.domain.command.WcsAlternativeStorageZoneUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsAlternativeStorageZone implements HasTransactionInfo {

    private String alternativeZoneName;
    private String factoryName;
    private Integer priority;
    private String sourceZoneName;
    private String description;
    private String useYn;

    private String lastEventComment;
    private String lastEventName;
    private LocalDateTime lastEventTime;
    private String lastEventUser;

    public static WcsAlternativeStorageZone create(WcsAlternativeStorageZoneCreateCommand command) {
        LocalDateTime now = LocalDateTime.now();
        String eventName = "WcsAlternativeStorageZoneCreated";
        String eventUser = "SYSTEM";
        String eventComment = "WcsAlternativeStorageZone created";
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

        return WcsAlternativeStorageZone.builder()
                .alternativeZoneName(command.getAlternativeZoneName())
                .factoryName(command.getFactoryName())
                .priority(command.getPriority() != null ? command.getPriority() : 0)
                .sourceZoneName(command.getSourceZoneName())
                .description(command.getDescription())
                .useYn(command.getUseYn() != null ? command.getUseYn() : "Y")
                .lastEventName(eventName)
                .lastEventTime(eventTime)
                .lastEventUser(eventUser)
                .lastEventComment(eventComment)
                .build();
    }

    public WcsAlternativeStorageZone update(WcsAlternativeStorageZoneUpdateCommand command) {
        if (command.getDescription() != null) {
            this.description = command.getDescription();
        }
        if (command.getUseYn() != null) {
            this.useYn = command.getUseYn();
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
