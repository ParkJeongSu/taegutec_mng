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
@Table(name = "W_CONVEYOR_HISTORY", catalog = "NEXBEWCSHT", schema = "dbo")
public class WcsConveyorHistoryEntity {

    @Id
    @Column(name = "eventTimeKey", length = 30, nullable = false)
    private String eventTimeKey;

    @Column(name = "autoRunStatus", length = 30)
    private String autoRunStatus;

    @Column(name = "carrierExist", length = 30)
    private String carrierExist;

    @Column(name = "carrierName", length = 64)
    private String carrierName;

    @Column(name = "conveyorGroupNumber", nullable = false)
    @Builder.Default
    private Integer conveyorGroupNumber = 0;

    @Column(name = "conveyorType", length = 30)
    private String conveyorType;

    @Column(name = "currentCmdData", length = 30)
    private String currentCmdData;

    @Column(name = "direction", length = 30)
    private String direction;

    @Column(name = "dispatchingPriority", length = 20)
    private String dispatchingPriority;

    @Column(name = "downConveyorCount", nullable = false)
    @Builder.Default
    private Integer downConveyorCount = 0;

    @Column(name = "errorHappen", length = 64)
    private String errorHappen;

    @Column(name = "jobCompleteState", length = 30)
    private String jobCompleteState;

    @Column(name = "machineTypeName", length = 100)
    private String machineTypeName;

    @Column(name = "mode", length = 30)
    private String mode;

    @Column(name = "onCarrierCount", nullable = false)
    @Builder.Default
    private Integer onCarrierCount = 0;

    @Column(name = "onlineControlStatus", length = 10)
    private String onlineControlStatus;

    @Column(name = "operationMode", length = 10)
    private String operationMode;

    @Column(name = "preStatus", length = 30)
    private String preStatus;

    @Column(name = "rfidEnableMode", length = 30)
    private String rfidEnableMode;

    @Column(name = "rtvNumber", nullable = false)
    @Builder.Default
    private Integer rtvNumber = 0;

    @Column(name = "runConveyorCount", nullable = false)
    @Builder.Default
    private Integer runConveyorCount = 0;

    @Column(name = "serverName", length = 64)
    private String serverName;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "totalConveyorCount", nullable = false)
    @Builder.Default
    private Integer totalConveyorCount = 0;

    @Column(name = "touchPanelNumber", nullable = false)
    @Builder.Default
    private Integer touchPanelNumber = 0;

    @Column(name = "conveyorGroup", length = 64, nullable = false)
    private String conveyorGroup;

    @Column(name = "conveyorName", length = 64, nullable = false)
    private String conveyorName;

    @Column(name = "conveyorNumber", nullable = false)
    @Builder.Default
    private Integer conveyorNumber = 0;

    @Column(name = "eventComment", length = 255)
    private String eventComment;

    @Column(name = "eventName", length = 64)
    private String eventName;

    @Column(name = "eventTime")
    private LocalDateTime eventTime;

    @Column(name = "eventUser", length = 64)
    private String eventUser;

    @Column(name = "factoryName", length = 32)
    private String factoryName;

    @Column(name = "localNo", nullable = false)
    @Builder.Default
    private Integer localNo = 0;

    @Column(name = "readingEnableMode", length = 30)
    private String readingEnableMode;

    @Column(name = "eqRouteKey", length = 64)
    private String eqRouteKey;

    @Column(name = "conveyorConnectionStatus", length = 20)
    private String conveyorConnectionStatus;

    @Column(name = "areaName", length = 64)
    private String areaName;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (this.conveyorGroupNumber == null) this.conveyorGroupNumber = 0;
        if (this.downConveyorCount == null) this.downConveyorCount = 0;
        if (this.onCarrierCount == null) this.onCarrierCount = 0;
        if (this.rtvNumber == null) this.rtvNumber = 0;
        if (this.runConveyorCount == null) this.runConveyorCount = 0;
        if (this.totalConveyorCount == null) this.totalConveyorCount = 0;
        if (this.touchPanelNumber == null) this.touchPanelNumber = 0;
        if (this.conveyorNumber == null) this.conveyorNumber = 0;
        if (this.localNo == null) this.localNo = 0;
    }
}