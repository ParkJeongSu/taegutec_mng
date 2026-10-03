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
@Table(name = "STOCKER", catalog = "NEXBEWCS", schema = "dbo")
@IdClass(WcsStockerId.class)
public class WcsStockerEntity {

    @Id
    @Column(name = "factoryName", length = 32, nullable = false)
    private String factoryName;

    @Id
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

    @Column(name = "lastEventComment", length = 255)
    private String lastEventComment;

    @Column(name = "lastEventName", length = 64)
    private String lastEventName;

    @Column(name = "lastEventTime")
    private LocalDateTime lastEventTime;

    @Column(name = "lastEventUser", length = 64)
    private String lastEventUser;

    /**
     * INSERT / UPDATE 쿼리 발생 직전 NULL 방어 로직 (JPA 라이프사이클 훅)
     */
    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (this.abnormalShelfCount == null) this.abnormalShelfCount = 0;
        if (this.emptyShelfCount == null) this.emptyShelfCount = 0;
        if (this.normalShelfCount == null) this.normalShelfCount = 0;
        if (this.reservedShelfCount == null) this.reservedShelfCount = 0;
        if (this.totalShelfCount == null) this.totalShelfCount = 0;
        if (this.useShelfCount == null) this.useShelfCount = 0;
        if (this.stockerArrangeEnabled == null) this.stockerArrangeEnabled = false;
        if (this.stockerArrangeMaxCommandCount == null) this.stockerArrangeMaxCommandCount = 1;
        if (this.stockerArrangeMode == null || this.stockerArrangeMode.trim().isEmpty()) this.stockerArrangeMode = "DEFAULT";
        if (this.stockerArrangeScheduleType == null || this.stockerArrangeScheduleType.trim().isEmpty()) this.stockerArrangeScheduleType = "ONCE";
        if (this.stockerArrangeState == null || this.stockerArrangeState.trim().isEmpty()) this.stockerArrangeState = "READY";
        if (this.carrierUseCountThreshold == null) this.carrierUseCountThreshold = 0;
    }
}