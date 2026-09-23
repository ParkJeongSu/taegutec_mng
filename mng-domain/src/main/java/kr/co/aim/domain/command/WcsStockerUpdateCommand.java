package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsStockerUpdateCommand {

    private TransactionInfo transactionInfo;

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
}
