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
@Table(name = "CRANE", catalog = "NEXBEWCS", schema = "dbo")
@IdClass(WcsCraneId.class)
public class WcsCraneEntity {

    @Id
    @Column(name = "craneName")
    private String craneName;

    @Id
    @Column(name = "craneNumber")
    private Integer craneNumber;

    @Id
    @Column(name = "factoryName")
    private String factoryName;

    @Id
    @Column(name = "localNo")
    private Integer localNo;

    @Id
    @Column(name = "stockerName")
    private String stockerName;

    @Column(name = "autoRunStatus")
    private String autoRunStatus;

    @Column(name = "carrierExist")
    private String carrierExist;

    @Column(name = "carrierName")
    private String carrierName;

    @Column(name = "columnPosition")
    private String columnPosition;

    @Column(name = "currentCmdData")
    private String currentCmdData;

    @Column(name = "errorHappen")
    private String errorHappen;

    @Column(name = "forkPreStatus")
    private String forkPreStatus;

    @Column(name = "forkStatus")
    private String forkStatus;

    @Column(name = "jobCompleteState")
    private String jobCompleteState;

    @Column(name = "mode")
    private String mode;

    @Column(name = "positionType")
    private String positionType;

    @Column(name = "preStatus")
    private String preStatus;

    @Column(name = "rowPosition")
    private String rowPosition;

    @Column(name = "stagePosition")
    private String stagePosition;

    @Column(name = "status")
    private String status;

    @Column(name = "touchPanelNumber")
    private Integer touchPanelNumber;

    @Column(name = "zoneName")
    private String zoneName;

    @Column(name = "craneReadingEnableMode")
    private String craneReadingEnableMode;

    @Column(name = "portNoPosition")
    private String portNoPosition;

    @Column(name = "permissionRetryCount")
    private Integer permissionRetryCount;

    @Column(name = "permissionRetryTime")
    private Integer permissionRetryTime;

    @Column(name = "opportunisticEnabled")
    private Boolean opportunisticEnabled;

    @Column(name = "lastEventComment")
    private String lastEventComment;

    @Column(name = "lastEventName")
    private String lastEventName;

    @Column(name = "lastEventTime")
    private LocalDateTime lastEventTime;

    @Column(name = "lastEventUser")
    private String lastEventUser;
}
