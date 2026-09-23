package kr.co.aim.domain.model;

import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.WcsTransferCommandCreateCommand;
import kr.co.aim.domain.command.WcsTransferCommandUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsTransferCommand implements HasTransactionInfo {

    private String transferCommandName;

    private String carrierName;
    private String commandStatus;
    private LocalDateTime createTime;
    private String currentEquipmentName;
    private String hotLot;
    private LocalDateTime jobCompletedTime;
    private LocalDateTime jobReceiveTime;
    private LocalDateTime jobStartTime;
    private String lotName;
    private String owner;
    private Integer priority;
    private Integer productQuantity;
    private String source;
    private String target;
    private String targetEquipmentName;
    private String factoryName;
    private String currentSource;
    private String orderType;
    private String sourceEquipmentName;
    private String sourceTransferType;
    private String targetTransferType;
    private Integer subCommandJobNo;
    private String subCommandStatus;
    private Integer transferSpeed;
    private String processType;
    private Boolean startReportFlag;

    private String lastEventComment;
    private String lastEventName;
    private LocalDateTime lastEventTime;
    private String lastEventUser;

    public static WcsTransferCommand create(WcsTransferCommandCreateCommand command) {
        LocalDateTime now = LocalDateTime.now();
        String eventName = "WcsTransferCommandCreated";
        String eventUser = "SYSTEM";
        String eventComment = "WcsTransferCommand created";
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

        return WcsTransferCommand.builder()
                .transferCommandName(command.getTransferCommandName())
                .carrierName(command.getCarrierName())
                .commandStatus(command.getCommandStatus())
                .createTime(command.getCreateTime() != null ? command.getCreateTime() : now)
                .currentEquipmentName(command.getCurrentEquipmentName())
                .hotLot(command.getHotLot())
                .jobCompletedTime(command.getJobCompletedTime())
                .jobReceiveTime(command.getJobReceiveTime())
                .jobStartTime(command.getJobStartTime())
                .lotName(command.getLotName())
                .owner(command.getOwner())
                .priority(command.getPriority())
                .productQuantity(command.getProductQuantity())
                .source(command.getSource())
                .target(command.getTarget())
                .targetEquipmentName(command.getTargetEquipmentName())
                .factoryName(command.getFactoryName())
                .currentSource(command.getCurrentSource())
                .orderType(command.getOrderType())
                .sourceEquipmentName(command.getSourceEquipmentName())
                .sourceTransferType(command.getSourceTransferType())
                .targetTransferType(command.getTargetTransferType())
                .subCommandJobNo(command.getSubCommandJobNo())
                .subCommandStatus(command.getSubCommandStatus())
                .transferSpeed(command.getTransferSpeed())
                .processType(command.getProcessType())
                .startReportFlag(command.getStartReportFlag())
                .lastEventName(eventName)
                .lastEventTime(eventTime)
                .lastEventUser(eventUser)
                .lastEventComment(eventComment)
                .build();
    }

    public WcsTransferCommand update(WcsTransferCommandUpdateCommand command) {
        if (command.getCarrierName() != null) {
            this.carrierName = command.getCarrierName();
        }
        if (command.getCommandStatus() != null) {
            this.commandStatus = command.getCommandStatus();
        }
        if (command.getCreateTime() != null) {
            this.createTime = command.getCreateTime();
        }
        if (command.getCurrentEquipmentName() != null) {
            this.currentEquipmentName = command.getCurrentEquipmentName();
        }
        if (command.getHotLot() != null) {
            this.hotLot = command.getHotLot();
        }
        if (command.getJobCompletedTime() != null) {
            this.jobCompletedTime = command.getJobCompletedTime();
        }
        if (command.getJobReceiveTime() != null) {
            this.jobReceiveTime = command.getJobReceiveTime();
        }
        if (command.getJobStartTime() != null) {
            this.jobStartTime = command.getJobStartTime();
        }
        if (command.getLotName() != null) {
            this.lotName = command.getLotName();
        }
        if (command.getOwner() != null) {
            this.owner = command.getOwner();
        }
        if (command.getPriority() != null) {
            this.priority = command.getPriority();
        }
        if (command.getProductQuantity() != null) {
            this.productQuantity = command.getProductQuantity();
        }
        if (command.getSource() != null) {
            this.source = command.getSource();
        }
        if (command.getTarget() != null) {
            this.target = command.getTarget();
        }
        if (command.getTargetEquipmentName() != null) {
            this.targetEquipmentName = command.getTargetEquipmentName();
        }
        if (command.getFactoryName() != null) {
            this.factoryName = command.getFactoryName();
        }
        if (command.getCurrentSource() != null) {
            this.currentSource = command.getCurrentSource();
        }
        if (command.getOrderType() != null) {
            this.orderType = command.getOrderType();
        }
        if (command.getSourceEquipmentName() != null) {
            this.sourceEquipmentName = command.getSourceEquipmentName();
        }
        if (command.getSourceTransferType() != null) {
            this.sourceTransferType = command.getSourceTransferType();
        }
        if (command.getTargetTransferType() != null) {
            this.targetTransferType = command.getTargetTransferType();
        }
        if (command.getSubCommandJobNo() != null) {
            this.subCommandJobNo = command.getSubCommandJobNo();
        }
        if (command.getSubCommandStatus() != null) {
            this.subCommandStatus = command.getSubCommandStatus();
        }
        if (command.getTransferSpeed() != null) {
            this.transferSpeed = command.getTransferSpeed();
        }
        if (command.getProcessType() != null) {
            this.processType = command.getProcessType();
        }
        if (command.getStartReportFlag() != null) {
            this.startReportFlag = command.getStartReportFlag();
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
