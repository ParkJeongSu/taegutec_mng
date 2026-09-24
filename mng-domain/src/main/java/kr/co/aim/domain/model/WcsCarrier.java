package kr.co.aim.domain.model;

import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.WcsCarrierCreateCommand;
import kr.co.aim.domain.command.WcsCarrierUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsCarrier implements HasTransactionInfo {

    private String carrierName;
    private String factoryName;

    private String afterProcess;
    private String beforeProcess;
    private String carrierGroup;
    private String carrierStatus;
    private LocalDateTime createTime;
    private String currentPositionName;
    private String hotLot;
    private String lotName;
    private String owner;
    private String previousCarrierStatus;
    private String productQuantity;
    private String zoneName;
    private String currentEquipmentName;
    private String carrierDetailType;
    private String carrierType;
    private String transferCommandName;
    private String travelProfile;
    private String itemName;
    private String orderId;
    private Integer orderLineNumber;
    private String productionType;
    private LocalDateTime inboundTime;
    private LocalDateTime outboundTime;
    private String weight;
    private Integer carrierUseCount;

    private String lastEventComment;
    private String lastEventName;
    private LocalDateTime lastEventTime;
    private String lastEventUser;

    public static WcsCarrier create(WcsCarrierCreateCommand command) {
        LocalDateTime now = LocalDateTime.now();
        String eventName = "WcsCarrierCreated";
        String eventUser = "SYSTEM";
        String eventComment = "WcsCarrier created";
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

        return WcsCarrier.builder()
                .carrierName(command.getCarrierName())
                .factoryName(command.getFactoryName())
                .afterProcess(command.getAfterProcess())
                .beforeProcess(command.getBeforeProcess())
                .carrierGroup(command.getCarrierGroup())
                .carrierStatus(command.getCarrierStatus())
                .createTime((command.getCreateTime() != null) ? command.getCreateTime() : now)
                .currentPositionName(command.getCurrentPositionName())
                .hotLot(command.getHotLot())
                .lotName(command.getLotName())
                .owner(command.getOwner())
                .previousCarrierStatus(command.getPreviousCarrierStatus())
                .productQuantity(command.getProductQuantity())
                .zoneName(command.getZoneName())
                .currentEquipmentName(command.getCurrentEquipmentName())
                .carrierDetailType(command.getCarrierDetailType())
                .carrierType(command.getCarrierType())
                .transferCommandName(command.getTransferCommandName())
                .travelProfile(command.getTravelProfile())
                .itemName(command.getItemName())
                .orderId(command.getOrderId())
                .orderLineNumber(command.getOrderLineNumber())
                .productionType(command.getProductionType())
                .inboundTime(command.getInboundTime())
                .outboundTime(command.getOutboundTime())
                .weight(command.getWeight())
                .carrierUseCount((command.getCarrierUseCount() != null) ? command.getCarrierUseCount() : 0)
                .lastEventName(eventName)
                .lastEventTime(eventTime)
                .lastEventUser(eventUser)
                .lastEventComment(eventComment)
                .build();
    }

    public WcsCarrier update(WcsCarrierUpdateCommand command) {
        if (command.getAfterProcess() != null) {
            this.afterProcess = command.getAfterProcess();
        }
        if (command.getBeforeProcess() != null) {
            this.beforeProcess = command.getBeforeProcess();
        }
        if (command.getCarrierGroup() != null) {
            this.carrierGroup = command.getCarrierGroup();
        }
        if (command.getCarrierStatus() != null) {
            this.carrierStatus = command.getCarrierStatus();
        }
        if (command.getCreateTime() != null) {
            this.createTime = command.getCreateTime();
        }
        if (command.getCurrentPositionName() != null) {
            this.currentPositionName = command.getCurrentPositionName();
        }
        if (command.getHotLot() != null) {
            this.hotLot = command.getHotLot();
        }
        if (command.getLotName() != null) {
            this.lotName = command.getLotName();
        }
        if (command.getOwner() != null) {
            this.owner = command.getOwner();
        }
        if (command.getPreviousCarrierStatus() != null) {
            this.previousCarrierStatus = command.getPreviousCarrierStatus();
        }
        if (command.getProductQuantity() != null) {
            this.productQuantity = command.getProductQuantity();
        }
        if (command.getZoneName() != null) {
            this.zoneName = command.getZoneName();
        }
        if (command.getCurrentEquipmentName() != null) {
            this.currentEquipmentName = command.getCurrentEquipmentName();
        }
        if (command.getCarrierDetailType() != null) {
            this.carrierDetailType = command.getCarrierDetailType();
        }
        if (command.getCarrierType() != null) {
            this.carrierType = command.getCarrierType();
        }
        if (command.getTransferCommandName() != null) {
            this.transferCommandName = command.getTransferCommandName();
        }
        if (command.getTravelProfile() != null) {
            this.travelProfile = command.getTravelProfile();
        }
        if (command.getItemName() != null) {
            this.itemName = command.getItemName();
        }
        if (command.getOrderId() != null) {
            this.orderId = command.getOrderId();
        }
        if (command.getOrderLineNumber() != null) {
            this.orderLineNumber = command.getOrderLineNumber();
        }
        if (command.getProductionType() != null) {
            this.productionType = command.getProductionType();
        }
        if (command.getInboundTime() != null) {
            this.inboundTime = command.getInboundTime();
        }
        if (command.getOutboundTime() != null) {
            this.outboundTime = command.getOutboundTime();
        }
        if (command.getWeight() != null) {
            this.weight = command.getWeight();
        }
        if (command.getCarrierUseCount() != null) {
            this.carrierUseCount = command.getCarrierUseCount();
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
