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
@Table(name = "STOCKER_HISTORY", catalog = "NEXBEWCSHT", schema = "dbo")
public class WcsStockerHistoryEntity {

    @Id
    @Column(name = "eventTimeKey", length = 30)
    private String eventTimeKey;

    @Column(name = "factoryName")
    private String factoryName;

    @Column(name = "stockerName")
    private String stockerName;

    @Column(name = "dispatchingPriority")
    private String dispatchingPriority;

    @Column(name = "onlineControlStatus")
    private String onlineControlStatus;

    @Column(name = "operationMode")
    private String operationMode;

    @Column(name = "serverName")
    private String serverName;

    @Column(name = "stockerConnectionStatus")
    private String stockerConnectionStatus;

    @Column(name = "stockerMode")
    private String stockerMode;

    @Column(name = "stockerNumber")
    private String stockerNumber;

    @Column(name = "stockerStatus")
    private String stockerStatus;

    @Column(name = "stockerType")
    private String stockerType;

    @Column(name = "machineTypeName")
    private String machineTypeName;

    @Column(name = "eqRouteKey")
    private String eqRouteKey;

    @Column(name = "areaName")
    private String areaName;

    @Column(name = "abnormalShelfCount")
    private Integer abnormalShelfCount;

    @Column(name = "emptyShelfCount")
    private Integer emptyShelfCount;

    @Column(name = "normalShelfCount")
    private Integer normalShelfCount;

    @Column(name = "reservedShelfCount")
    private Integer reservedShelfCount;

    @Column(name = "totalShelfCount")
    private Integer totalShelfCount;

    @Column(name = "useShelfCount")
    private Integer useShelfCount;

    @Column(name = "lastStockerArrangeExecuteTime")
    private LocalDateTime lastStockerArrangeExecuteTime;

    @Column(name = "stockerArrangeDailyTime")
    private String stockerArrangeDailyTime;

    @Column(name = "stockerArrangeEnabled")
    private Boolean stockerArrangeEnabled;

    @Column(name = "stockerArrangeExecuteTime")
    private LocalDateTime stockerArrangeExecuteTime;

    @Column(name = "stockerArrangeMaxCommandCount")
    private Integer stockerArrangeMaxCommandCount;

    @Column(name = "stockerArrangeMode")
    private String stockerArrangeMode;

    @Column(name = "stockerArrangeScheduleType")
    private String stockerArrangeScheduleType;

    @Column(name = "stockerArrangeState")
    private String stockerArrangeState;

    @Column(name = "carrierUseCountThreshold")
    private Integer carrierUseCountThreshold;

    @Column(name = "eventComment")
    private String eventComment;

    @Column(name = "eventName")
    private String eventName;

    @Column(name = "eventTime")
    private LocalDateTime eventTime;

    @Column(name = "eventUser")
    private String eventUser;
}
