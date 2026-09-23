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
@Table(name = "CARRIER", catalog = "NEXBEWCS", schema = "dbo")
@IdClass(WcsCarrierId.class)
public class WcsCarrierEntity {

    @Id
    @Column(name = "carrierName")
    private String carrierName;

    @Id
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
    private String travelProfile;

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

    @Column(name = "lastEventComment")
    private String lastEventComment;

    @Column(name = "lastEventName")
    private String lastEventName;

    @Column(name = "lastEventTime")
    private LocalDateTime lastEventTime;

    @Column(name = "lastEventUser")
    private String lastEventUser;
}
