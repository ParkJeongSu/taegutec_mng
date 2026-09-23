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
@Table(name = "CONVEYOR", catalog = "NEXBEWCS", schema = "dbo")
@IdClass(WcsConveyorId.class)
public class WcsConveyorEntity {

    @Id
    @Column(name = "conveyorGroup")
    private String conveyorGroup;

    @Id
    @Column(name = "conveyorName")
    private String conveyorName;

    @Id
    @Column(name = "conveyorNumber")
    private Integer conveyorNumber;

    @Id
    @Column(name = "factoryName")
    private String factoryName;

    @Id
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

    @Column(name = "eqRouteKey")
    private String eqRouteKey;

    @Column(name = "conveyorConnectionStatus")
    private String conveyorConnectionStatus;

    @Column(name = "areaName")
    private String areaName;

    @Column(name = "lastEventComment")
    private String lastEventComment;

    @Column(name = "lastEventName")
    private String lastEventName;

    @Column(name = "lastEventTime")
    private LocalDateTime lastEventTime;

    @Column(name = "lastEventUser")
    private String lastEventUser;
}
