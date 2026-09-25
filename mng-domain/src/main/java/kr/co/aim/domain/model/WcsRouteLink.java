package kr.co.aim.domain.model;

import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.WcsRouteLinkCreateCommand;
import kr.co.aim.domain.command.WcsRouteLinkUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsRouteLink implements HasTransactionInfo {

    private String factoryName;
    private Long routeLinkId;
    private String description;
    private String fromNodeId;
    private Integer length;
    private String passYn;
    private Integer priority;
    private String processType;
    private String routeLinkType;
    private String toNodeId;
    private String usableYn;
    private String useYn;

    private String lastEventComment;
    private String lastEventName;
    private LocalDateTime lastEventTime;
    private String lastEventUser;

    public static WcsRouteLink create(WcsRouteLinkCreateCommand command) {
        LocalDateTime now = LocalDateTime.now();
        String eventName = "WcsRouteLinkCreated";
        String eventUser = "SYSTEM";
        String eventComment = "WcsRouteLink created";
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

        return WcsRouteLink.builder()
                .factoryName(command.getFactoryName())
                .routeLinkId(command.getRouteLinkId())
                .description(command.getDescription())
                .fromNodeId(command.getFromNodeId())
                .length(command.getLength())
                .passYn(command.getPassYn() != null ? command.getPassYn() : "N")
                .priority(command.getPriority() != null ? command.getPriority() : 0)
                .processType(command.getProcessType())
                .routeLinkType(command.getRouteLinkType())
                .toNodeId(command.getToNodeId())
                .usableYn(command.getUsableYn() != null ? command.getUsableYn() : "Y")
                .useYn(command.getUseYn() != null ? command.getUseYn() : "Y")
                .lastEventName(eventName)
                .lastEventTime(eventTime)
                .lastEventUser(eventUser)
                .lastEventComment(eventComment)
                .build();
    }

    public WcsRouteLink update(WcsRouteLinkUpdateCommand command) {
        if (command.getDescription() != null) {
            this.description = command.getDescription();
        }
        if (command.getFromNodeId() != null) {
            this.fromNodeId = command.getFromNodeId();
        }
        if (command.getLength() != null) {
            this.length = command.getLength();
        }
        if (command.getPassYn() != null) {
            this.passYn = command.getPassYn();
        }
        if (command.getPriority() != null) {
            this.priority = command.getPriority();
        }
        if (command.getProcessType() != null) {
            this.processType = command.getProcessType();
        }
        if (command.getRouteLinkType() != null) {
            this.routeLinkType = command.getRouteLinkType();
        }
        if (command.getToNodeId() != null) {
            this.toNodeId = command.getToNodeId();
        }
        if (command.getUsableYn() != null) {
            this.usableYn = command.getUsableYn();
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
