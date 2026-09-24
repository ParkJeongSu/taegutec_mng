package kr.co.aim.api.dto;

import kr.co.aim.domain.model.WcsCarrier;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsCarrierResponse {

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

    public static WcsCarrierResponse fromDomain(WcsCarrier domain) {
        if (domain == null) {
            return null;
        }

        return WcsCarrierResponse.builder()
                .carrierName(domain.getCarrierName())
                .factoryName(domain.getFactoryName())
                .afterProcess(domain.getAfterProcess())
                .beforeProcess(domain.getBeforeProcess())
                .carrierGroup(domain.getCarrierGroup())
                .carrierStatus(domain.getCarrierStatus())
                .createTime(domain.getCreateTime())
                .currentPositionName(domain.getCurrentPositionName())
                .hotLot(domain.getHotLot())
                .lotName(domain.getLotName())
                .owner(domain.getOwner())
                .previousCarrierStatus(domain.getPreviousCarrierStatus())
                .productQuantity(domain.getProductQuantity())
                .zoneName(domain.getZoneName())
                .currentEquipmentName(domain.getCurrentEquipmentName())
                .carrierDetailType(domain.getCarrierDetailType())
                .carrierType(domain.getCarrierType())
                .transferCommandName(domain.getTransferCommandName())
                .travelProfile(domain.getTravelProfile())
                .itemName(domain.getItemName())
                .orderId(domain.getOrderId())
                .orderLineNumber(domain.getOrderLineNumber())
                .productionType(domain.getProductionType())
                .inboundTime(domain.getInboundTime())
                .outboundTime(domain.getOutboundTime())
                .weight(domain.getWeight())
                .carrierUseCount(domain.getCarrierUseCount())
                .lastEventComment(domain.getLastEventComment())
                .lastEventName(domain.getLastEventName())
                .lastEventTime(domain.getLastEventTime())
                .lastEventUser(domain.getLastEventUser())
                .build();
    }
}
