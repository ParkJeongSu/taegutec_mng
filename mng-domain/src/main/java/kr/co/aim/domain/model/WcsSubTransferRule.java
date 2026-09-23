package kr.co.aim.domain.model;

import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.WcsSubTransferRuleCreateCommand;
import kr.co.aim.domain.command.WcsSubTransferRuleUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsSubTransferRule implements HasTransactionInfo {

    private String equipmentName;
    private String factoryName;
    private String moduleName;
    private Long routeLinkId;
    private Integer carrierCount;
    private String description;
    private String moduleType;
    private String ngStatus;

    private String lastEventComment;
    private String lastEventName;
    private LocalDateTime lastEventTime;
    private String lastEventUser;

    public static WcsSubTransferRule create(WcsSubTransferRuleCreateCommand command) {
        LocalDateTime now = LocalDateTime.now();
        String eventName = "WcsSubTransferRuleCreated";
        String eventUser = "SYSTEM";
        String eventComment = "WcsSubTransferRule created";
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

        return WcsSubTransferRule.builder()
                .equipmentName(command.getEquipmentName())
                .factoryName(command.getFactoryName())
                .moduleName(command.getModuleName())
                .routeLinkId(command.getRouteLinkId())
                .carrierCount(command.getCarrierCount() != null ? command.getCarrierCount() : 0)
                .description(command.getDescription())
                .moduleType(command.getModuleType())
                .ngStatus(command.getNgStatus())
                .lastEventName(eventName)
                .lastEventTime(eventTime)
                .lastEventUser(eventUser)
                .lastEventComment(eventComment)
                .build();
    }

    public WcsSubTransferRule update(WcsSubTransferRuleUpdateCommand command) {
        if (command.getCarrierCount() != null) {
            this.carrierCount = command.getCarrierCount();
        }
        if (command.getDescription() != null) {
            this.description = command.getDescription();
        }
        if (command.getModuleType() != null) {
            this.moduleType = command.getModuleType();
        }
        if (command.getNgStatus() != null) {
            this.ngStatus = command.getNgStatus();
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
