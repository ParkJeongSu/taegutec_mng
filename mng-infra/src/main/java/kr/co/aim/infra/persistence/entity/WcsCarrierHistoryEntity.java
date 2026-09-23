package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "CARRIER_HISTORY", catalog = "NEXBEWCSHT", schema = "dbo")
public class WcsCarrierHistoryEntity {

    @Id
    @Column(name = "eventTimeKey", length = 30)
    private String eventTimeKey;

    @Column(name = "carrierName")
    private String carrierName;

    @Column(name = "factoryName")
    private String factoryName;

    @Column(name = "afterProcess")
    private String afterProcess;

    @Column(name = "beforeProcess")
    private String beforeProcess;

    @Column(name = "carrierGroup")
    private String carrierGroup;

    @Column(name = "carrierStatus")
    private String carrierStatus;

    @Column(name = "createTime")
    private LocalDateTime createTime;

    @Column(name = "currentPositionName")
    private String currentPositionName;

    @Column(name = "hotLot")
    private String hotLot;

    @Column(name = "lotName")
    private String lotName;

    @Column(name = "owner")
    private String owner;

    @Column(name = "previousCarrierStatus")
    private String previousCarrierStatus;

    @Column(name = "productQuantity")
    private Integer productQuantity;

    @Column(name = "zoneName")
    private String zoneName;

    @Column(name = "currentEquipmentName")
    private String currentEquipmentName;

    @Column(name = "carrierDetailType")
    private String carrierDetailType;

    @Column(name = "carrierType")
    private String carrierType;

    @Column(name = "transferCommandName")
    private String transferCommandName;

    @Column(name = "travelProfile")
    private Integer travelProfile;

    @Column(name = "itemName")
    private String itemName;

    @Column(name = "orderId")
    private String orderId;

    @Column(name = "orderLineNumber")
    private String orderLineNumber;

    @Column(name = "productionType")
    private String productionType;

    @Column(name = "inboundTime")
    private LocalDateTime inboundTime;

    @Column(name = "outboundTime")
    private LocalDateTime outboundTime;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "carrierUseCount")
    private Integer carrierUseCount;

    @Column(name = "carrierNo")
    private String carrierNo;

    @Column(name = "carrierQTime")
    private String carrierQTime;

    @Column(name = "carrierStatusTime")
    private LocalDateTime carrierStatusTime;

    @Column(name = "machineRecipeName")
    private String machineRecipeName;

    @Column(name = "portName")
    private String portName;

    @Column(name = "processPriority")
    private String processPriority;

    @Column(name = "subUnitName")
    private String subUnitName;

    @Column(name = "substrateQuantity")
    private Integer substrateQuantity;

    @Column(name = "substrateSlotMap")
    private String substrateSlotMap;

    @Column(name = "unitName")
    private String unitName;

    @Column(name = "eventComment")
    private String eventComment;

    @Column(name = "eventName")
    private String eventName;

    @Column(name = "eventTime")
    private LocalDateTime eventTime;

    @Column(name = "eventUser")
    private String eventUser;
}
