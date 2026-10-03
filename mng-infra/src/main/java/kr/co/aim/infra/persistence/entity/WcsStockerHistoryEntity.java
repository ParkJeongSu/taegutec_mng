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
@Table(name = "W_STOCKER_HISTORY", catalog = "NEXBEWCSHT", schema = "dbo")
public class WcsStockerHistoryEntity {

    @Id
    @Column(name = "eventTimeKey", length = 30, nullable = false)
    private String eventTimeKey;

    @Column(name = "factoryName", length = 32)
    private String factoryName;

    @Column(name = "stockerName", length = 64, nullable = false)
    private String stockerName;

    @Column(name = "dispatchingPriority", length = 20)
    private String dispatchingPriority;

    @Column(name = "onlineControlStatus", length = 10)
    private String onlineControlStatus;

    @Column(name = "operationMode", length = 10)
    private String operationMode;

    @Column(name = "serverName", length = 64)
    private String serverName;

    @Column(name = "stockerConnectionStatus", length = 20)
    private String stockerConnectionStatus;

    @Column(name = "stockerMode", length = 16)
    private String stockerMode;

    @Column(name = "stockerNumber")
    private Integer stockerNumber;

    @Column(name = "stockerStatus", length = 20)
    private String stockerStatus;

    @Column(name = "stockerType", length = 40)
    private String stockerType;

    @Column(name = "machineTypeName", length = 100)
    private String machineTypeName;

    @Column(name = "eqRouteKey", length = 64)
    private String eqRouteKey;

    @Column(name = "areaName", length = 64)
    private String areaName;

    @Column(name = "abnormalShelfCount", nullable = false)
    @Builder.Default
    private Integer abnormalShelfCount = 0;

    @Column(name = "emptyShelfCount", nullable = false)
    @Builder.Default
    private Integer emptyShelfCount = 0;

    @Column(name = "normalShelfCount", nullable = false)
    @Builder.Default
    private Integer normalShelfCount = 0;

    @Column(name = "reservedShelfCount", nullable = false)
    @Builder.Default
    private Integer reservedShelfCount = 0;

    @Column(name = "totalShelfCount", nullable = false)
    @Builder.Default
    private Integer totalShelfCount = 0;

    @Column(name = "useShelfCount", nullable = false)
    @Builder.Default
    private Integer useShelfCount = 0;

    @Column(name = "lastStockerArrangeExecuteTime")
    private LocalDateTime lastStockerArrangeExecuteTime;

    @Column(name = "stockerArrangeDailyTime", length = 5)
    private String stockerArrangeDailyTime;

    @Column(name = "stockerArrangeEnabled", nullable = false)
    @Builder.Default
    private Boolean stockerArrangeEnabled = false;

    @Column(name = "stockerArrangeExecuteTime")
    private LocalDateTime stockerArrangeExecuteTime;

    @Column(name = "stockerArrangeMaxCommandCount", nullable = false)
    @Builder.Default
    private Integer stockerArrangeMaxCommandCount = 1;

    @Column(name = "stockerArrangeMode", length = 30, nullable = false)
    @Builder.Default
    private String stockerArrangeMode = "DEFAULT";

    @Column(name = "stockerArrangeScheduleType", length = 20, nullable = false)
    @Builder.Default
    private String stockerArrangeScheduleType = "ONCE";

    @Column(name = "stockerArrangeState", length = 30, nullable = false)
    @Builder.Default
    private String stockerArrangeState = "READY";

    @Column(name = "carrierUseCountThreshold", nullable = false)
    @Builder.Default
    private Integer carrierUseCountThreshold = 0;

    @Column(name = "eventComment", length = 255)
    private String eventComment;

    @Column(name = "eventName", length = 64)
    private String eventName;

    @Column(name = "eventTime")
    private LocalDateTime eventTime;

    @Column(name = "eventUser", length = 64)
    private String eventUser;
}