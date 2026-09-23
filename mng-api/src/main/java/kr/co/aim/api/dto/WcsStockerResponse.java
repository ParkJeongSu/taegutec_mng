package kr.co.aim.api.dto;

import kr.co.aim.domain.model.WcsStocker;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsStockerResponse {

    private String factoryName;
    private String stockerName;
    private String dispatchingPriority;
    private String onlineControlStatus;
    private String operationMode;
    private String serverName;
    private String stockerConnectionStatus;
    private String stockerMode;
    private String stockerNumber;
    private String stockerStatus;
    private String stockerType;
    private String machineTypeName;
    private String eqRouteKey;
    private String areaName;

    private Integer abnormalShelfCount;
    private Integer emptyShelfCount;
    private Integer normalShelfCount;
    private Integer reservedShelfCount;
    private Integer totalShelfCount;
    private Integer useShelfCount;

    private LocalDateTime lastStockerArrangeExecuteTime;
    private String stockerArrangeDailyTime;
    private Boolean stockerArrangeEnabled;
    private LocalDateTime stockerArrangeExecuteTime;
    private Integer stockerArrangeMaxCommandCount;
    private String stockerArrangeMode;
    private String stockerArrangeScheduleType;
    private String stockerArrangeState;
    private Integer carrierUseCountThreshold;

    private String lastEventComment;
    private String lastEventName;
    private LocalDateTime lastEventTime;
    private String lastEventUser;

    public static WcsStockerResponse fromDomain(WcsStocker domain) {
        if (domain == null) {
            return null;
        }

        return WcsStockerResponse.builder()
                .factoryName(domain.getFactoryName())
                .stockerName(domain.getStockerName())
                .dispatchingPriority(domain.getDispatchingPriority())
                .onlineControlStatus(domain.getOnlineControlStatus())
                .operationMode(domain.getOperationMode())
                .serverName(domain.getServerName())
                .stockerConnectionStatus(domain.getStockerConnectionStatus())
                .stockerMode(domain.getStockerMode())
                .stockerNumber(domain.getStockerNumber())
                .stockerStatus(domain.getStockerStatus())
                .stockerType(domain.getStockerType())
                .machineTypeName(domain.getMachineTypeName())
                .eqRouteKey(domain.getEqRouteKey())
                .areaName(domain.getAreaName())
                .abnormalShelfCount(domain.getAbnormalShelfCount())
                .emptyShelfCount(domain.getEmptyShelfCount())
                .normalShelfCount(domain.getNormalShelfCount())
                .reservedShelfCount(domain.getReservedShelfCount())
                .totalShelfCount(domain.getTotalShelfCount())
                .useShelfCount(domain.getUseShelfCount())
                .lastStockerArrangeExecuteTime(domain.getLastStockerArrangeExecuteTime())
                .stockerArrangeDailyTime(domain.getStockerArrangeDailyTime())
                .stockerArrangeEnabled(domain.getStockerArrangeEnabled())
                .stockerArrangeExecuteTime(domain.getStockerArrangeExecuteTime())
                .stockerArrangeMaxCommandCount(domain.getStockerArrangeMaxCommandCount())
                .stockerArrangeMode(domain.getStockerArrangeMode())
                .stockerArrangeScheduleType(domain.getStockerArrangeScheduleType())
                .stockerArrangeState(domain.getStockerArrangeState())
                .carrierUseCountThreshold(domain.getCarrierUseCountThreshold())
                .lastEventComment(domain.getLastEventComment())
                .lastEventName(domain.getLastEventName())
                .lastEventTime(domain.getLastEventTime())
                .lastEventUser(domain.getLastEventUser())
                .build();
    }
}
