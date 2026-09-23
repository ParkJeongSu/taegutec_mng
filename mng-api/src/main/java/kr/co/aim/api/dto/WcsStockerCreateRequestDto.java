package kr.co.aim.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsStockerCreateRequestDto {

    @NotBlank(message = "공장 구분은 필수 입력 항목입니다.")
    private String factoryName;

    @NotBlank(message = "스토커 명은 필수 입력 항목입니다.")
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

    private String eventName;
    private String eventUser;
    private String eventComment;
}
