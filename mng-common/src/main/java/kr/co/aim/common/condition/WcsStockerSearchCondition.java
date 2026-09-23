package kr.co.aim.common.condition;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsStockerSearchCondition {

    private String factoryName;
    private String stockerName;
    private String stockerType;
    private String stockerStatus;
    private String stockerMode;
    private String onlineControlStatus;
    private String operationMode;
    private String serverName;
    private String stockerConnectionStatus;
    private String stockerNumber;
    private String machineTypeName;
    private String eqRouteKey;
    private String areaName;
    private Boolean stockerArrangeEnabled;
    private String stockerArrangeMode;
    private String stockerArrangeScheduleType;
    private String stockerArrangeState;
}
