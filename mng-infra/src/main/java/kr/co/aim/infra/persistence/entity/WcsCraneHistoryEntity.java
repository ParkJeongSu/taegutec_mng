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
@Table(name = "W_CRANE_HISTORY", catalog = "NEXBEWCSHT", schema = "dbo")
public class WcsCraneHistoryEntity {

    @Id
    @Column(name = "eventTimeKey", length = 30)
    private String eventTimeKey;

    @Column(name = "craneName")
    private String craneName;

    @Column(name = "craneNumber")
    private Integer craneNumber;

    @Column(name = "factoryName")
    private String factoryName;

    @Column(name = "localNo")
    private Integer localNo;

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

    @Column(name = "craneRfidEnableMode")
    private String craneRfidEnableMode;

    @Column(name = "portNoPosition")
    private String portNoPosition;

    @Column(name = "permissionRetryCount")
    private Integer permissionRetryCount;

    @Column(name = "permissionRetryTime")
    private Integer permissionRetryTime;

    @Column(name = "opportunisticEnabled")
    private Boolean opportunisticEnabled;

    @Column(name = "eventComment")
    private String eventComment;

    @Column(name = "eventName")
    private String eventName;

    @Column(name = "eventTime")
    private LocalDateTime eventTime;

    @Column(name = "eventUser")
    private String eventUser;
}
