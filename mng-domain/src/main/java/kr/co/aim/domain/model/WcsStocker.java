package kr.co.aim.domain.model;

import kr.co.aim.domain.command.WcsStockerCreateCommand;
import kr.co.aim.domain.command.WcsStockerUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsStocker {

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

    public static WcsStocker create(WcsStockerCreateCommand command) {
        LocalDateTime now = LocalDateTime.now();
        String eventName = "WcsStockerCreated";
        String eventUser = "SYSTEM";
        String eventComment = "WcsStocker created";
        LocalDateTime eventTime = now;

        if (command.getTransactionInfo() != null) {
            if (command.getTransactionInfo().eventName() != null && !command.getTransactionInfo().eventName().trim().isEmpty()) {
                eventName = command.getTransactionInfo().eventName().trim();
            }
            if (command.getTransactionInfo().eventUser() != null && !command.getTransactionInfo().eventUser().trim().isEmpty()) {
                eventUser = command.getTransactionInfo().eventUser().trim();
            }
            if (command.getTransactionInfo().eventComment() != null) {
                eventComment = command.getTransactionInfo().eventComment().trim();
            }
            if (command.getTransactionInfo().eventTime() != null) {
                eventTime = command.getTransactionInfo().eventTime();
            }
        }

        return WcsStocker.builder()
                .factoryName(command.getFactoryName())
                .stockerName(command.getStockerName())
                .dispatchingPriority(command.getDispatchingPriority())
                .onlineControlStatus(command.getOnlineControlStatus())
                .operationMode(command.getOperationMode())
                .serverName(command.getServerName())
                .stockerConnectionStatus(command.getStockerConnectionStatus())
                .stockerMode(command.getStockerMode())
                .stockerNumber(command.getStockerNumber())
                .stockerStatus(command.getStockerStatus())
                .stockerType(command.getStockerType())
                .machineTypeName(command.getMachineTypeName())
                .eqRouteKey(command.getEqRouteKey())
                .areaName(command.getAreaName())
                .abnormalShelfCount(command.getAbnormalShelfCount())
                .emptyShelfCount(command.getEmptyShelfCount())
                .normalShelfCount(command.getNormalShelfCount())
                .reservedShelfCount(command.getReservedShelfCount())
                .totalShelfCount(command.getTotalShelfCount())
                .useShelfCount(command.getUseShelfCount())
                .lastStockerArrangeExecuteTime(command.getLastStockerArrangeExecuteTime())
                .stockerArrangeDailyTime(command.getStockerArrangeDailyTime())
                .stockerArrangeEnabled(command.getStockerArrangeEnabled() != null ? command.getStockerArrangeEnabled() : Boolean.FALSE)
                .stockerArrangeExecuteTime(command.getStockerArrangeExecuteTime())
                .stockerArrangeMaxCommandCount(command.getStockerArrangeMaxCommandCount())
                .stockerArrangeMode(command.getStockerArrangeMode())
                .stockerArrangeScheduleType(command.getStockerArrangeScheduleType())
                .stockerArrangeState(command.getStockerArrangeState())
                .carrierUseCountThreshold(command.getCarrierUseCountThreshold())
                .lastEventName(eventName)
                .lastEventTime(eventTime)
                .lastEventUser(eventUser)
                .lastEventComment(eventComment)
                .build();
    }

    public WcsStocker update(WcsStockerUpdateCommand command) {
        if (command.getDispatchingPriority() != null) {
            this.dispatchingPriority = command.getDispatchingPriority();
        }
        if (command.getOnlineControlStatus() != null) {
            this.onlineControlStatus = command.getOnlineControlStatus();
        }
        if (command.getOperationMode() != null) {
            this.operationMode = command.getOperationMode();
        }
        if (command.getServerName() != null) {
            this.serverName = command.getServerName();
        }
        if (command.getStockerConnectionStatus() != null) {
            this.stockerConnectionStatus = command.getStockerConnectionStatus();
        }
        if (command.getStockerMode() != null) {
            this.stockerMode = command.getStockerMode();
        }
        if (command.getStockerNumber() != null) {
            this.stockerNumber = command.getStockerNumber();
        }
        if (command.getStockerStatus() != null) {
            this.stockerStatus = command.getStockerStatus();
        }
        if (command.getStockerType() != null) {
            this.stockerType = command.getStockerType();
        }
        if (command.getMachineTypeName() != null) {
            this.machineTypeName = command.getMachineTypeName();
        }
        if (command.getEqRouteKey() != null) {
            this.eqRouteKey = command.getEqRouteKey();
        }
        if (command.getAreaName() != null) {
            this.areaName = command.getAreaName();
        }
        if (command.getAbnormalShelfCount() != null) {
            this.abnormalShelfCount = command.getAbnormalShelfCount();
        }
        if (command.getEmptyShelfCount() != null) {
            this.emptyShelfCount = command.getEmptyShelfCount();
        }
        if (command.getNormalShelfCount() != null) {
            this.normalShelfCount = command.getNormalShelfCount();
        }
        if (command.getReservedShelfCount() != null) {
            this.reservedShelfCount = command.getReservedShelfCount();
        }
        if (command.getTotalShelfCount() != null) {
            this.totalShelfCount = command.getTotalShelfCount();
        }
        if (command.getUseShelfCount() != null) {
            this.useShelfCount = command.getUseShelfCount();
        }
        if (command.getLastStockerArrangeExecuteTime() != null) {
            this.lastStockerArrangeExecuteTime = command.getLastStockerArrangeExecuteTime();
        }
        if (command.getStockerArrangeDailyTime() != null) {
            this.stockerArrangeDailyTime = command.getStockerArrangeDailyTime();
        }
        if (command.getStockerArrangeEnabled() != null) {
            this.stockerArrangeEnabled = command.getStockerArrangeEnabled();
        }
        if (command.getStockerArrangeExecuteTime() != null) {
            this.stockerArrangeExecuteTime = command.getStockerArrangeExecuteTime();
        }
        if (command.getStockerArrangeMaxCommandCount() != null) {
            this.stockerArrangeMaxCommandCount = command.getStockerArrangeMaxCommandCount();
        }
        if (command.getStockerArrangeMode() != null) {
            this.stockerArrangeMode = command.getStockerArrangeMode();
        }
        if (command.getStockerArrangeScheduleType() != null) {
            this.stockerArrangeScheduleType = command.getStockerArrangeScheduleType();
        }
        if (command.getStockerArrangeState() != null) {
            this.stockerArrangeState = command.getStockerArrangeState();
        }
        if (command.getCarrierUseCountThreshold() != null) {
            this.carrierUseCountThreshold = command.getCarrierUseCountThreshold();
        }

        if (command.getTransactionInfo() != null) {
            if (command.getTransactionInfo().eventName() != null && !command.getTransactionInfo().eventName().trim().isEmpty()) {
                this.lastEventName = command.getTransactionInfo().eventName().trim();
            }
            if (command.getTransactionInfo().eventUser() != null && !command.getTransactionInfo().eventUser().trim().isEmpty()) {
                this.lastEventUser = command.getTransactionInfo().eventUser().trim();
            }
            if (command.getTransactionInfo().eventComment() != null) {
                this.lastEventComment = command.getTransactionInfo().eventComment().trim();
            }
            this.lastEventTime = command.getTransactionInfo().eventTime() != null ? command.getTransactionInfo().eventTime() : LocalDateTime.now();
        }

        return this;
    }
}
