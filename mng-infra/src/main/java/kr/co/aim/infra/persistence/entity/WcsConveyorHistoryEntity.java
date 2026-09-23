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
    @Column(name = "eventTimeKey", length = 30)
    private String eventTimeKey;

    @Column(name = "conveyorGroup")
    private String conveyorGroup;

    @Column(name = "conveyorName")
    private String conveyorName;

    @Column(name = "conveyorNumber")
    private Integer conveyorNumber;

    @Column(name = "factoryName")
    private String factoryName;

    @Column(name = "localNo")
    private Integer localNo;

    @Column(name = "autoRunStatus")
    private String autoRunStatus;

    @Column(name = "carrierExist")
    private String carrierExist;

    @Column(name = "carrierName")
    private String carrierName;

    @Column(name = "conveyorGroupNumber")
    private Integer conveyorGroupNumber;

    @Column(name = "conveyorType")
    private String conveyorType;

    @Column(name = "currentCmdData")
    private String currentCmdData;

    @Column(name = "direction")
    private String direction;

    @Column(name = "dispatchingPriority")
    private String dispatchingPriority;

    @Column(name = "errorHappen")
    private String errorHappen;

    @Column(name = "jobCompleteState")
    private String jobCompleteState;

    @Column(name = "onlineControlStatus")
    private String onlineControlStatus;

    @Column(name = "operationMode")
    private String operationMode;

    @Column(name = "preStatus")
    private String preStatus;

    @Column(name = "rtvNumber")
    private Integer rtvNumber;

    @Column(name = "status")
    private String status;

    @Column(name = "touchPanelNumber")
    private Integer touchPanelNumber;

    @Column(name = "serverName")
    private String serverName;

    @Column(name = "mode")
    private String mode;

    @Column(name = "downConveyorCount")
    private Integer downConveyorCount;

    @Column(name = "onCarrierCount")
    private Integer onCarrierCount;

    @Column(name = "totalConveyorCount")
    private Integer totalConveyorCount;

    @Column(name = "runConveyorCount")
    private Integer runConveyorCount;

    @Column(name = "machineTypeName")
    private String machineTypeName;

    @Column(name = "readingEnableMode")
    private String readingEnableMode;

    @Column(name = "rfidEnableMode")
    private String rfidEnableMode;

    @Column(name = "eqRouteKey")
    private String eqRouteKey;

    @Column(name = "conveyorConnectionStatus")
    private String conveyorConnectionStatus;

    @Column(name = "areaName")
    private String areaName;

    @Column(name = "eventComment")
    private String eventComment;

    @Column(name = "eventName")
    private String eventName;

    @Column(name = "eventTime")
    private LocalDateTime eventTime;

    @Column(name = "eventUser")
    private String eventUser;
}
