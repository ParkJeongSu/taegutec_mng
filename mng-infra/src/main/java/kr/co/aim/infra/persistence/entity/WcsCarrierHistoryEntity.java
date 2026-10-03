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
@Table(name = "W_CARRIER_HISTORY", catalog = "NEXBEWCSHT", schema = "dbo")
public class WcsCarrierHistoryEntity {

    @Id
    @Column(name = "eventTimeKey", length = 30, nullable = false)
    private String eventTimeKey;

    @Column(name = "carrierName", length = 64, nullable = false)
    private String carrierName;

    @Column(name = "factoryName", length = 32)
    private String factoryName;

    @Column(name = "afterProcess", length = 64)
    private String afterProcess;

    @Column(name = "beforeProcess", length = 64)
    private String beforeProcess;

    @Column(name = "carrierDetailType", length = 64)
    private String carrierDetailType;

    @Column(name = "carrierGroup", length = 64)
    private String carrierGroup;

    @Column(name = "carrierStatus", length = 20)
    private String carrierStatus;

    @Column(name = "carrierType", length = 32)
    private String carrierType;

    @Column(name = "createTime")
    private LocalDateTime createTime;

    @Column(name = "currentEquipmentName", length = 64)
    private String currentEquipmentName;

    @Column(name = "currentPositionName", length = 64)
    private String currentPositionName;

    @Column(name = "hotLot", length = 64)
    private String hotLot;

    @Column(name = "lotName", length = 64)
    private String lotName;

    @Column(name = "owner", length = 64)
    private String owner;

    @Column(name = "previousCarrierStatus", length = 20)
    private String previousCarrierStatus;

    @Column(name = "productQuantity", length = 64)
    private String productQuantity;

    @Column(name = "transferCommandName", length = 64)
    private String transferCommandName;

    @Column(name = "travelProfile", length = 20)
    private String travelProfile;

    @Column(name = "zoneName", length = 64)
    private String zoneName;

    @Column(name = "itemName", length = 64)
    private String itemName;

    @Column(name = "orderId", length = 64)
    private String orderId;

    @Column(name = "orderLineNumber")
    private Integer orderLineNumber;

    @Column(name = "productionType", length = 20)
    private String productionType;

    @Column(name = "inboundTime")
    private LocalDateTime inboundTime;

    @Column(name = "outboundTime")
    private LocalDateTime outboundTime;

    @Column(name = "weight", length = 20)
    private String weight;

    @Column(name = "carrierUseCount", nullable = false)
    @Builder.Default
    private Integer carrierUseCount = 0;

    @Column(name = "eventComment", length = 255)
    private String eventComment;

    @Column(name = "eventName", length = 40)
    private String eventName;

    @Column(name = "eventTime")
    private LocalDateTime eventTime;

    @Column(name = "eventUser", length = 40)
    private String eventUser;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (this.carrierUseCount == null) {
            this.carrierUseCount = 0;
        }
    }
}